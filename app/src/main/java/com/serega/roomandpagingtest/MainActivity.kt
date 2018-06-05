package com.serega.roomandpagingtest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.UUID
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var adapterWrapper: AdapterWrapper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonInsert.setOnClickListener {
            Realm.getDefaultInstance().executeTransaction {
                it.insertOrUpdate(createProfile())
            }
        }
        buttonClear.setOnClickListener {
            Realm.getDefaultInstance().executeTransaction {
                it.where(ProfileModel::class.java).findAll().deleteAllFromRealm()
            }
        }
        val dsFactory = RealmDsFactory()
        val realmPagedList = RealmPagedListBuilder(dsFactory, RealmPagedList.Config.Builder().setInitialLoadSizeHint(40).setPageSize(20).setPrefetchDistance(10).build())
                .setRealmData(Realm.getDefaultInstance().where(ProfileModel::class.java).findAll())
                .setBoundaryCallback(object : RealmPagedList.BoundaryCallback<ProfileModel>() {
                    override fun onZeroItemsLoaded() {
                        Log.i("M_DATA", "onZeroItemsLoaded")
                    }

                    override fun onItemAtFrontLoaded(itemAtFront: ProfileModel) {
                        Log.i("M_DATA", "onItemAtFrontLoaded")
                    }

                    override fun onItemAtEndLoaded(itemAtEnd: ProfileModel) {
                        Log.i("M_DATA", "onItemAtEndLoaded")
                        Toast.makeText(this@MainActivity, "All items loaded", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()

        val adapter = Adapter(realmPagedList)
        adapterWrapper = AdapterWrapper(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapterWrapper

        dsFactory.liveData.observe(this, Observer {
            recyclerView?.post {
                when (it) {
                    1 -> adapterWrapper?.showBottomProgressBar()
                    2 -> adapterWrapper?.hideBottomProgressBar()
                }
            }
        })
    }

    class RealmDsFactory : RealmDataSource.Factory<String, ProfileModel>() {
        val liveData = MutableLiveData<Int>()

        override fun create(): RealmDataSource<String, ProfileModel> {
            return object : RealmPageKeyedDataSource<String, ProfileModel>() {
                override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<String, ProfileModel>) {
                    Log.i("M_DATA", "loadInitial")
                    val data = Realm.getDefaultInstance().where(ProfileModel::class.java).findAll()
                    if (data.size != 0) {
                        callback.onResult(data.size, null, data.last()!!.uuid)
                        return
                    }
                    val list = ArrayList<ProfileModel>(params.requestedLoadSize)
                    for (i in 0 until params.requestedLoadSize) {
                        list.add(ProfileModel().apply {
                            uuid = UUID.randomUUID().toString()
                            name = NameProvider.getName()
                            age = NameProvider.getAge()
                        })
                    }
                    Realm.getDefaultInstance().executeTransaction {
                        it.insertOrUpdate(list)
                    }
                    callback.onResult(list.size, null, list.last().uuid)
                }

                override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, ProfileModel>) {
                    Log.i("M_DATA", "loadBefore")
                    callback.onResult(0, null)
                }

                override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, ProfileModel>) {
                    Log.i("M_DATA", "loadAfter ${params.key}")
                    val data = Realm.getDefaultInstance().where(ProfileModel::class.java).findAll()
                    if (data.size > 100) {
                        callback.onResult(0, null)
                        return
                    }
                    liveData.value = 1
                    val list = ArrayList<ProfileModel>(params.requestedLoadSize)
                    for (i in 0 until params.requestedLoadSize) {
                        list.add(ProfileModel().apply {
                            uuid = UUID.randomUUID().toString()
                            name = NameProvider.getName()
                            age = NameProvider.getAge()
                        })
                    }
                    launch(CommonPool) {
                        delay(5000)
                        launch(UI) {
                            Realm.getDefaultInstance().executeTransaction {
                                it.copyToRealmOrUpdate(list)
                                callback.onResult(list.size, list.last().uuid)
                                liveData.value = 2
                            }
                        }
                    }
                }
            }
        }
    }

    class Adapter(list: RealmPagedList<ProfileModel>) : RealmPagedListAdapter<ProfileModel, RecyclerView.ViewHolder>(list) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)) {

            }
        }

        override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
            val profile = getItem(position)
            if (profile != null) {
                holder.itemView.textName.text = profile.name
                holder.itemView.textInfo.text = profile.uuid
            }
        }
    }

    private fun createProfile() = ProfileModel().apply {
        uuid = UUID.randomUUID().toString()
        name = NameProvider.getName()
        age = NameProvider.getAge()
    }

    class AdapterWrapper(private val innerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var topProgressBar = 0
        private var bottomProgressBar = 0

        init {
            innerAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    notifyItemRangeRemoved(positionStart, itemCount)
                }

                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    notifyItemRangeInserted(positionStart, itemCount)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    notifyItemRangeChanged(positionStart, itemCount)
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                    notifyItemRangeChanged(positionStart, itemCount, payload)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                R.layout.recycler_view_progress -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_progress, parent, false)) {}
                else -> innerAdapter.onCreateViewHolder(parent, viewType)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (!isProgress(position)) {
                innerAdapter.onBindViewHolder(holder, position)
            }
        }

        private fun isProgress(position: Int): Boolean {
            if (topProgressBar != 0 && position == 0) {
                return true
            } else if (bottomProgressBar != 0 && position == innerAdapter.itemCount) {
                return true
            }
            return false
        }

        fun showTopProgress() {
            if (topProgressBar == 0) {
                topProgressBar = 1
                notifyItemInserted(0)
            }
        }

        fun hideTopProgress() {
            if (topProgressBar == 1) {
                topProgressBar = 0
                notifyItemRemoved(0)
            }
        }

        fun showBottomProgressBar() {
            if (bottomProgressBar == 0) {
                bottomProgressBar = 1
                notifyItemInserted(innerAdapter.itemCount)
            }
        }

        fun hideBottomProgressBar() {
            if (bottomProgressBar == 1) {
                bottomProgressBar = 0
                notifyItemRemoved(innerAdapter.itemCount)
            }
        }

        override fun getItemCount() = innerAdapter.itemCount + topProgressBar + bottomProgressBar

        override fun getItemViewType(position: Int) = if (isProgress(position)) R.layout.recycler_view_progress else innerAdapter.getItemViewType(position)
    }
}


