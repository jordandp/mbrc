package com.kelsos.mbrc.connection_manager

import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.presenters.BasePresenter
import com.kelsos.mbrc.repository.ConnectionRepository
import rx.Observable
import timber.log.Timber
import javax.inject.Inject


class ConnectionManagerPresenterImpl
@Inject
constructor(private val repository: ConnectionRepository) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

  override fun load() {
    checkIfAttached()
    val all = Observable.defer { Observable.just(repository.all) }
    val defaultId = Observable.defer { Observable.just(repository.defaultId) }

    addSubcription(Observable.zip<Long, List<ConnectionSettings>, ConnectionModel>(defaultId, all, { defaultId, settings ->
      ConnectionModel(defaultId, settings)
    }).subscribe({
      view?.updateModel(it)
    }, {
      this.onLoadError(it)
    }))
  }

  override fun setDefault(settings: ConnectionSettings) {
    checkIfAttached()
    repository.default = settings
    view?.defaultChanged()
    view?.dataUpdated()
  }

  override fun save(settings: ConnectionSettings) {
    checkIfAttached()

    if (settings.id > 0) {
      repository.update(settings)
    } else {
      repository.save(settings)
    }

    if (settings.id == repository.defaultId) {
      view?.defaultChanged()
    }

    view?.dataUpdated()
  }

  override fun delete(settings: ConnectionSettings) {
    checkIfAttached()
    repository.delete(settings)
    if (settings.id == repository.defaultId) {
      view?.defaultChanged()
    }

    view?.dataUpdated()
  }

  private fun onLoadError(throwable: Throwable) {
    checkIfAttached()
    Timber.v(throwable, "Failure")
  }
}