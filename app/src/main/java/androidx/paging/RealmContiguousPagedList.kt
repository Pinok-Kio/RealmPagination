package androidx.paging

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import io.realm.OrderedRealmCollection
import io.realm.RealmModel
import kotlin.math.max

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 02.06.18.
 */
class RealmContiguousPagedList<K, V : RealmModel> internal constructor(
        realmData: OrderedRealmCollection<V>,
        private val mDataSource: RealmContiguousDataSource<K, V>,
        boundaryCallback: RealmPagedList.BoundaryCallback<V>?,
        config: RealmPagedList.Config,
        key: K?,
        lastLoad: Int
) : RealmPagedList<V>(RealmPagedStorage<V>(realmData), boundaryCallback, config), RealmPagedStorage.Callback {
    private var mPrependWorkerRunning = false
    private var mAppendWorkerRunning = false
    private var mPrependItemsRequested = 0
    private var mAppendItemsRequested = 0

    override val dataSource: RealmDataSource<*, V> get() = mDataSource
    override val lastKey: Any? get() = mDataSource.getKey(mLastLoad, mLastItem)

    private val mReceiver = object : RealmPageResult.Receiver {
        @AnyThread
        override fun onPageResult(@RealmPageResult.ResultType resultType: Int, pageResult: RealmPageResult) {
            if (pageResult.isInvalid) {
                detach()
                return
            }

            if (isDetached) {
                // No op, have detached
                return
            }
            if (resultType == RealmPageResult.APPEND) {
                mStorage.appendPage(pageResult.loadedCount, this@RealmContiguousPagedList)
            } else if (resultType == RealmPageResult.PREPEND) {
                mStorage.prependPage(pageResult.loadedCount, this@RealmContiguousPagedList)
            }

            if (mBoundaryCallback != null) {
                val deferEmpty = mStorage.size == 0
                val deferBegin = !deferEmpty && resultType == RealmPageResult.PREPEND && pageResult.loadedCount == 0
                val deferEnd = !deferEmpty && resultType == RealmPageResult.APPEND && pageResult.loadedCount == 0
                deferBoundaryCallbacks(deferEmpty, deferBegin, deferEnd)
            }
        }
    }

    init {
        mLastLoad = lastLoad
        if (mDataSource.isInvalid) {
            detach()
        } else {
            mDataSource.dispatchLoadInitial(key, config.initialLoadSizeHint, config.pageSize, mReceiver)
        }
    }

    @MainThread
    override fun loadAroundInternal(index: Int) {
        val prependItems = config.prefetchDistance - index
        val appendItems = index + config.prefetchDistance - mStorage.size

        mPrependItemsRequested = max(prependItems, mPrependItemsRequested)
        if (mPrependItemsRequested > 0) {
            schedulePrepend()
        }

        mAppendItemsRequested = max(appendItems, mAppendItemsRequested)
        if (mAppendItemsRequested > 0) {
            scheduleAppend()
        }
    }

    private fun schedulePrepend() {
        if (mPrependWorkerRunning) {
            return
        }
        mPrependWorkerRunning = true

        val position = mStorage.positionOffset

        // safe to access first item here - mStorage can't be empty if we're prepending
        val item = mStorage.firstLoadedItem
        if (isDetached) {
            return
        }
        if (mDataSource.isInvalid) {
            detach()
        } else {
            mDataSource.dispatchLoadBefore(position, mStorage.size, item!!, config.pageSize, mReceiver)
        }
    }

    private fun scheduleAppend() {
        if (mAppendWorkerRunning) {
            return
        }
        mAppendWorkerRunning = true

        val position = mStorage.size - 1 + mStorage.positionOffset

        // safe to access first item here - mStorage can't be empty if we're appending
        val item = mStorage.lastLoadedItem
        if (isDetached) {
            return
        }
        if (mDataSource.isInvalid) {
            detach()
        } else {
            mDataSource.dispatchLoadAfter(position, mStorage.size, item!!, config.pageSize, mReceiver)
        }
    }

    @MainThread
    override fun onPagePrepended(added: Int) {
        // consider whether to post more work, now that a page is fully prepended
        mPrependItemsRequested -= added
        mPrependWorkerRunning = false
        if (mPrependItemsRequested > 0) {
            // not done prepending, keep going
            schedulePrepend()
        }

        offsetBoundaryAccessIndices(added)
    }

    @MainThread
    override fun onPageAppended(added: Int) {
        // consider whether to post more work, now that a page is fully appended

        mAppendItemsRequested -= added
        mAppendWorkerRunning = false
        if (mAppendItemsRequested > 0) {
            // not done appending, keep going
            scheduleAppend()
        }
    }

    companion object {
        internal const val LAST_LOAD_UNSPECIFIED = -1
    }
}
