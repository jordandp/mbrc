package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.model.ConnectionModel
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.views.MainView
import javax.inject.Inject

class MainViewPresenter : BasePresenter<MainView>() {

  @Inject lateinit var bus: RxBus
  @Inject lateinit var model: MainDataModel
  @Inject lateinit var connectionModel: ConnectionModel

  fun load() {
    checkIfAttached()
    view?.updateLfmStatus(model.lfmStatus)
    view?.updateScrobbleStatus(model.isScrobblingEnabled)
    view?.updateRepeat(model.repeat)
    view?.updateShuffleState(model.shuffle)
    view?.updateVolume(model.getVolume(), model.isMute)
    view?.updatePlayState(model.getPlayState())
    view?.updateTrackInfo(model.trackInfo)
    view?.updateConnection(connectionModel.connection)
  }

  fun requestNowPlayingPosition() {
    val action = UserAction.create(Protocol.NowPlayingPosition)
    bus.post(MessageEvent.action(action))
  }

  fun toggleScrobbling() {
    bus.post(MessageEvent.action(UserAction(Protocol.PlayerScrobble, Const.TOGGLE)))
  }
}