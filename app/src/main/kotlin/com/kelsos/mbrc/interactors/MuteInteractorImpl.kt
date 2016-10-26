package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.Mute
import com.kelsos.mbrc.cache.PlayerStateCache
import com.kelsos.mbrc.dto.requests.ChangeStateRequest
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class MuteInteractorImpl
@Inject constructor(private val cache: PlayerStateCache,
                    private val service: PlayerService): MuteInteractor {

  override fun getMuteState(): Observable<Boolean> {
    val networkRequest = service.getMuteState()
        .flatMap { (if (it.enabled) Mute.ON else Mute.OFF).toSingletonObservable() }
        .doOnNext { cache.muteState = it }

    val cached = Observable.just(cache.muteState)

    return Observable.concat(networkRequest, cached)
        .filter { it !== Mute.UNDEF }
        .map { it === Mute.ON }
        .doOnError { Observable.just(false) }
        .first()
  }

  override fun toggle(): Observable<Boolean> {
    return cache.muteState.toSingletonObservable()
        .map { it === Mute.ON }
        .flatMap {
          val stateRequest = ChangeStateRequest()
          stateRequest.enabled = !it
          service.updateMuteState(stateRequest)
              .io()
              .flatMap {
                cache.muteState = if (it.enabled) Mute.ON else Mute.OFF
                it.enabled.toSingletonObservable()
              }
        }.io()
  }
}