package com.kelsos.mbrc.ui.navigation.library.genre_artists

import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface GenreArtistsView : BaseView {
  fun update(data: FlowCursorList<Artist>)
}

interface GenreArtistsPresenter : Presenter<GenreArtistsView> {
  fun load(genre: String)
}


