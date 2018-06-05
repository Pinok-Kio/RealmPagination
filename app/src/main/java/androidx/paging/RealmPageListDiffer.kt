package androidx.paging

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedCollectionChangeSet
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 02.06.18.
 */
class RealmPageListDiffer<T : RealmModel>(adapter: RecyclerView.Adapter<*>, private val mPagedList: RealmPagedList<T>) {
    private val mUpdateCallback = AdapterListUpdateCallback(adapter)

    val itemCount: Int
        get() = mPagedList.size

    init {
        val innerRealmData = mPagedList.mStorage.realmData
        if (innerRealmData is RealmList) {
            innerRealmData.addChangeListener { _, changeSet -> onInternalDataChanged(changeSet) }
        } else if (innerRealmData is RealmResults) {
            innerRealmData.addChangeListener { _, changeSet -> onInternalDataChanged(changeSet) }
        }
    }

    private fun onInternalDataChanged(changeSet: OrderedCollectionChangeSet?) {
        if (changeSet != null) {
            if (changeSet.state == OrderedCollectionChangeSet.State.INITIAL) {
                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    mUpdateCallback.onInserted(range.startIndex, range.length)
                }
                return
            }
            // For deletions, the adapter has to be notified in reverse order.
            val deletions = changeSet.deletionRanges
            for (i in deletions.indices.reversed()) {
                val range = deletions[i]
                mUpdateCallback.onRemoved(range.startIndex, range.length)
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {
                mUpdateCallback.onInserted(range.startIndex, range.length)
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {
                mUpdateCallback.onChanged(range.startIndex, range.length, null)
            }
        }
    }

    fun getItem(index: Int): T? {
        mPagedList.loadAround(index)
        return mPagedList[index]
    }
}
