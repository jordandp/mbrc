package com.kelsos.mbrc.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.Album;
import com.kelsos.mbrc.data.Artist;
import com.kelsos.mbrc.data.Genre;
import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.PageRange;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.data.Track;
import com.kelsos.mbrc.utilities.SettingsManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AuxiliarySocket {
  @Inject private SettingsManager settings;
  @Inject private ObjectMapper mapper;

  private Socket socket;
  private Subscription subscription;

  @NonNull private byte[] getMessage(SocketMessage message) throws JsonProcessingException {
    return (mapper.writeValueAsString(message) + "\r\n").getBytes();
  }

  public Observable<Page<Genre>> getGenres(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseGenres, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Genre>> typeReference = new TypeReference<Page<Genre>>() {
        };
        Page<Genre> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  public Observable<Page<Genre>> getArtists(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseArtists, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Artist>> typeReference = new TypeReference<Page<Artist>>() {
        };
        Page<Genre> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  public Observable<Page<Album>> getAlbums(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseAlbums, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Album>> typeReference = new TypeReference<Page<Album>>() {
        };
        Page<Album> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  public Observable<Page<Track>> getTracks(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);

    return request(Protocol.LibraryBrowseTracks, range == null ? "" : range).flatMap(socketMessage -> Observable.create(subscriber -> {
      try {
        TypeReference<Page<Track>> typeReference = new TypeReference<Page<Track>>() {
        };
        Page<Track> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    }));
  }

  @Nullable private PageRange getPageRange(int offset, int limit) {
    PageRange range = null;

    if (limit > 0) {
      range = new PageRange();
      range.setOffset(offset);
      range.setLimit(limit);
    }
    return range;
  }

  @NonNull private Observable<SocketMessage> request(String request, Object data) {
    return Observable.using(this::getSocket, this::getObservable, this::cleanup)
        .subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .flatMap(s -> getSocketMessageObservable(request, data, s))
        .skipWhile(this::shouldSkip);
  }

  @NonNull private Observable<SocketMessage> getSocketMessageObservable(String request, Object data, String s) {
    return Observable.create((Subscriber<? super SocketMessage> subscriber) -> {
      try {
        SocketMessage message = mapper.readValue(s, SocketMessage.class);
        String context = message.getContext();

        if (Protocol.Player.equals(context)) {
          sendMessage(SocketMessage.create(Protocol.ProtocolTag, Protocol.NoBroadcast));
        } else if (Protocol.ProtocolTag.equals(context)) {
          sendMessage(SocketMessage.create(request, data));
        }

        message.setData(mapper.writeValueAsString(message.getData()));

        subscriber.onNext(message);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    });
  }

  private boolean shouldSkip(SocketMessage ms) {
    return Protocol.Player.equals(ms.getContext()) || Protocol.ProtocolTag.equals(ms.getContext());
  }

  private void sendMessage(SocketMessage socketMessage) throws IOException {
    socket.getOutputStream().write(getMessage(socketMessage));
  }

  private void cleanup(Socket socket) {
    Timber.v("Cleaning auxiliary socket");
    if (!socket.isClosed()) {
      try {
        socket.close();
      } catch (IOException ex) {
        Timber.v(ex, "Failed to clause the auxiliary socket");
      }
    }
  }

  @NonNull private Observable<? extends String> getObservable(Socket socket) {
    try {
      final InputStreamReader in = new InputStreamReader(socket.getInputStream(), Const.UTF_8);
      final BufferedReader bufferedReader = new BufferedReader(in);
      return Observable.create(new OnSubscribeReader(bufferedReader));
    } catch (IOException ex) {
      return Observable.error(ex);
    }
  }

  private Socket getSocket() {
    try {
      Timber.v("Creating new socket");
      socket = new Socket();
      socket.connect(settings.getSocketAddress());
      sendMessage(SocketMessage.create(Protocol.Player, "Android"));
      return socket;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}