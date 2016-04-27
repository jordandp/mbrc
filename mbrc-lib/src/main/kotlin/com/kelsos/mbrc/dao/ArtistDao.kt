package com.kelsos.mbrc.dao

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = RemoteDatabase::class,
    name = "artists",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "artist_name_index")))
class ArtistDao : BaseModel() {

  @Column
  @PrimaryKey(autoincrement = true)
  var id: Long = 0

  @Column
  @Index(indexGroups = intArrayOf(1))
  var name: String = ""

  @Column(name = "date_added")
  var dateAdded: Long = 0

  @Column(name = "date_updated")
  var dateUpdated: Long = 0

  @Column(name = "date_deleted")
  var dateDeleted: Long = 0
}