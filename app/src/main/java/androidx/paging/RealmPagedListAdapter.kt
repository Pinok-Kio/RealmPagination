package androidx.paging

import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmModel

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 02.06.18.
 */
abstract class RealmPagedListAdapter<T : RealmModel, VH : RecyclerView.ViewHolder>(pagedList: RealmPagedList<T>) : RecyclerView.Adapter<VH>() {
    private val mDiffer: RealmPageListDiffer<T> = RealmPageListDiffer(this, pagedList)

    protected fun getItem(position: Int): T? = mDiffer.getItem(position)

    override fun getItemCount(): Int = mDiffer.itemCount
}
