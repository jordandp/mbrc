package com.kelsos.mbrc.ui.navigation.nowplaying

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.kelsos.mbrc.R
import com.kelsos.mbrc.data.dao.NowPlaying
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.ui.drag.SimpleItemTouchHelper
import com.kelsos.mbrc.ui.navigation.nowplaying.NowPlayingAdapter.NowPlayingListener
import com.kelsos.mbrc.ui.widgets.EmptyRecyclerView
import com.kelsos.mbrc.ui.widgets.MultiSwipeRefreshLayout
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.Scope
import toothpick.Toothpick
import toothpick.smoothie.module.SmoothieActivityModule
import javax.inject.Inject

class NowPlayingActivity : BaseActivity(),
                           NowPlayingView,
                           OnQueryTextListener,
                           NowPlayingListener {

  @BindView(R.id.now_playing_list) lateinit var nowPlayingList: EmptyRecyclerView
  @BindView(R.id.swipe_layout) lateinit var swipeRefreshLayout: MultiSwipeRefreshLayout
  @BindView(R.id.empty_view) lateinit var emptyView: View
  @Inject lateinit var adapter: NowPlayingAdapter

  @Inject lateinit var presenter: NowPlayingPresenter
  private var searchView: SearchView? = null
  private var searchItem: MenuItem? = null
  private lateinit var scope: Scope
  private lateinit var touchListener: NowPlayingTouchListener

  override fun onQueryTextSubmit(query: String): Boolean {
    presenter.search(query)
    searchView!!.setQuery("", false)
    searchView!!.isIconified = true
    searchView!!.clearFocus()
    MenuItemCompat.collapseActionView(searchItem)
    return true
  }

  override fun onQueryTextChange(newText: String): Boolean {
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.nowplaying_search, menu)
    searchItem = menu.findItem(R.id.now_playing_search)
    searchView = MenuItemCompat.getActionView(searchItem) as SearchView
    searchView?.queryHint = getString(R.string.now_playing_search_hint)
    searchView?.setIconifiedByDefault(true)
    searchView?.setOnQueryTextListener(this)
    return super.onCreateOptionsMenu(menu)
  }

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_nowplaying)
    ButterKnife.bind(this)
    scope = Toothpick.openScopes(application, this)
    scope.installModules(SmoothieActivityModule(this), NowPlayingModule.create())
    Toothpick.inject(this, scope)
    super.setup()
    swipeRefreshLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
    nowPlayingList.emptyView = emptyView
    val manager = LinearLayoutManager(this)
    nowPlayingList.layoutManager = manager
    nowPlayingList.adapter = adapter
    nowPlayingList.itemAnimator.changeDuration = 0
    touchListener = NowPlayingTouchListener(this, {
      if (it) {
        swipeRefreshLayout.clearSwipeableChildren()
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.cancelPendingInputEvents()
      } else {
        swipeRefreshLayout.setSwipeableChildren(R.id.now_playing_list, R.id.empty_view)
        swipeRefreshLayout.isEnabled = true
      }
    })
    nowPlayingList.addOnItemTouchListener(touchListener)
    val callback = SimpleItemTouchHelper(adapter)
    val helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(nowPlayingList)
    adapter.setListener(this)
    swipeRefreshLayout.setOnRefreshListener { this.refresh() }
    presenter.attach(this)
    refresh()
  }

  private fun refresh() {
    if (!swipeRefreshLayout.isRefreshing) {
      swipeRefreshLayout.isRefreshing = true
    }
    presenter.reload()
  }

  override fun onStart() {
    super.onStart()
    presenter.attach(this)
  }

  override fun onStop() {
    super.onStop()
    presenter.detach()
  }

  override fun onPress(position: Int) {
    presenter.play(position + 1)
  }

  override fun onMove(from: Int, to: Int) {
    presenter.moveTrack(from, to)
  }

  override fun onDismiss(position: Int) {
    presenter.removeTrack(position)
  }

  override fun active(): Int {
    return R.id.nav_now_playing
  }

  override fun onDestroy() {
    Toothpick.closeScope(this)
    super.onDestroy()
  }

  override fun update(cursor: FlowCursorList<NowPlaying>) {
    adapter.update(cursor)
    swipeRefreshLayout.isRefreshing = false
  }

  override fun reload() {
    adapter.refresh()
  }

  override fun trackChanged(trackInfo: TrackInfo) {
    adapter.setPlayingTrack(trackInfo.path)
  }

  override fun failure(throwable: Throwable) {
    swipeRefreshLayout.isRefreshing = false
    Snackbar.make(nowPlayingList, R.string.refresh_failed, Snackbar.LENGTH_SHORT).show()
  }
}
