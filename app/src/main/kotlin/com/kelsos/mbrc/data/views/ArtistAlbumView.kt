package com.kelsos.mbrc.data.views

import com.kelsos.mbrc.data.RemoteDatabase
import com.kelsos.mbrc.data.dao.AlbumDao
import com.kelsos.mbrc.data.dao.AlbumDao_Table
import com.kelsos.mbrc.data.dao.ArtistDao
import com.kelsos.mbrc.data.dao.ArtistDao_Table
import com.kelsos.mbrc.data.dao.CoverDao
import com.kelsos.mbrc.data.dao.CoverDao_Table
import com.kelsos.mbrc.data.dao.TrackDao
import com.kelsos.mbrc.data.dao.TrackDao_Table
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ModelView
import com.raizlabs.android.dbflow.annotation.ModelViewQuery
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.language.NameAlias
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.BaseModelView

@ModelView(database = RemoteDatabase::class,
    name = "artist_album_view") class ArtistAlbumView : BaseModelView<TrackDao>() {

  @Column var id: Long = 0
  @Column var name: String? = null
  @Column var artist: String? = null
  @Column(name = "artist_id") var artistId: Long = 0
  @Column var cover: String? = null

  companion object {
    @JvmField
    @ModelViewQuery val QUERY: Query = SQLite.select(AlbumDao_Table.id.`as`("id").withTable(),
        AlbumDao_Table.name.`as`("name").withTable(),
        ArtistDao_Table.name.`as`("artist").withTable(),
        ArtistDao_Table.id.`as`("artist_id").withTable(),
        CoverDao_Table.hash.`as`("cover").withTable())
        .distinct()
        .from(TrackDao::class.java)
        .innerJoin(AlbumDao::class.java)
        .on(TrackDao_Table.album_id.withTable().`is`(AlbumDao_Table.id.withTable()))
        .innerJoin(ArtistDao::class.java)
        .on(TrackDao_Table.artist_id.withTable().`is`(ArtistDao_Table.id.withTable()))
        .leftOuterJoin(CoverDao::class.java)
        .on(AlbumDao_Table.cover_id.withTable().`is`(CoverDao_Table.id.withTable()))
        .orderBy(OrderBy.fromNameAlias(NameAlias.builder("name").build()).ascending())
  }
}