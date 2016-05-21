package com.kelsos.mbrc.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.LyricsAdapter;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import roboguice.RoboGuice;

public class LyricsFragment extends Fragment {
  public static final String NEWLINE = "\r\n|\n";
  @Inject Bus bus;
  @BindView(R.id.lyrics_recycler_view) RecyclerView lyricsRecycler;
  @BindView(R.id.empty_view) LinearLayout emptyView;
  @BindView(R.id.empty_view_text) TextView emptyText;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RoboGuice.getInjector(getContext()).injectMembers(this);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);
    lyricsRecycler.setHasFixedSize(true);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
    lyricsRecycler.setLayoutManager(layoutManager);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.ui_fragment_lyrics, container, false);
  }

  @Override public void onStart() {
    super.onStart();
    bus.register(this);
  }

  @Override public void onStop() {
    super.onStop();
    bus.unregister(this);
  }

  @Subscribe public void updateLyricsData(LyricsUpdated update) {
    final String text = update.getLyrics();

    final List<String> lyrics = new ArrayList<>(Arrays.asList(text.split(NEWLINE)));

    if (lyrics.size() == 1) {
      lyricsRecycler.setVisibility(View.GONE);
      emptyText.setText(lyrics.get(0));
      emptyView.setVisibility(View.VISIBLE);
    } else {
      emptyView.setVisibility(View.GONE);
      lyricsRecycler.setVisibility(View.VISIBLE);
      LyricsAdapter adapter = new LyricsAdapter(getActivity(), lyrics);
      lyricsRecycler.setAdapter(adapter);
    }

  }
}