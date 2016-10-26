package com.kelsos.mbrc.controller

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kelsos.mbrc.events.ChangeWebSocketStatusEvent
import com.kelsos.mbrc.extensions.initDBFlow
import com.kelsos.mbrc.interactors.LibrarySyncInteractor
import com.kelsos.mbrc.messaging.NotificationService
import com.kelsos.mbrc.messaging.SocketMessageHandler
import com.kelsos.mbrc.net.SocketService
import com.kelsos.mbrc.receivers.PlayerActionReceiver
import com.kelsos.mbrc.receivers.StateBroadcastReceiver
import com.kelsos.mbrc.services.ServiceDiscovery
import com.kelsos.mbrc.utilities.RxBus
import com.kelsos.mbrc.utilities.SettingsManager
import com.raizlabs.android.dbflow.config.FlowManager
import rx.Observable
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class Controller : Service() {

  @Inject lateinit var socket: SocketService
  @Inject lateinit var handler: SocketMessageHandler
  @Inject lateinit var receiver: StateBroadcastReceiver
  @Inject lateinit var actionReceiver: PlayerActionReceiver
  @Inject lateinit var notificationService: NotificationService
  @Inject lateinit var discovery: ServiceDiscovery
  @Inject lateinit var settingsManager: SettingsManager
  @Inject lateinit var bus: RxBus
  @Inject lateinit var sync: LibrarySyncInteractor
  private lateinit var scope: Scope

  init {
    Timber.d("Application Controller Initialized")
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    this.initDBFlow()
    scope = Toothpick.openScopes(application, this)
    Toothpick.inject(this, scope)

    this.registerReceiver(actionReceiver, actionReceiver.intentFilter)
    this.registerReceiver(receiver, receiver.intentFilter)
    bus.register(this,
        ChangeWebSocketStatusEvent::class.java,
        { this.onWebSocketActionRequest(it) })
  }


  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.v("[Service] start command received")
    Observable.merge(discovery.startDiscovery(), settingsManager.observableDefault)
        .first()
        .subscribe({
          if (it != null) {
            socket.startWebSocket()
            sync.sync()
          }

        }) { Timber.v(it, "Discovery failed") }

    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.v("[Service] destroying service")
    bus.unregister(this)
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER)
    socket.disconnect()
    FlowManager.destroy()
    this.unregisterReceiver(receiver)
    this.unregisterReceiver(actionReceiver)
    Toothpick.closeScope(this)
  }

  private fun onWebSocketActionRequest(event: ChangeWebSocketStatusEvent) {
    when (event.action) {
      ChangeWebSocketStatusEvent.CONNECT -> {
        Timber.v("Attempting to start the websocket")
        socket.startWebSocket()
      }
      ChangeWebSocketStatusEvent.DISCONNECT -> {
        Timber.v("Attempting to stop the websocket")
        socket.disconnect()
      }
    }
  }

}