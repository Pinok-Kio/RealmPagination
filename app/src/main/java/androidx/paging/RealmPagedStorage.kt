package androidx.paging

import io.realm.OrderedRealmCollection
import java.util.*

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 02.06.18.
 */
class RealmPagedStorage<T> internal constructor(internal val realmData: OrderedRealmCollection<T>) : AbstractList<T>() {
    internal var positionOffset = 0
        private set

    internal val firstLoadedItem: T? get() = if (realmData.isValid && !realmData.isEmpty()) realmData.first() else null

    internal val lastLoadedItem: T? get() = if (realmData.isValid && !realmData.isEmpty()) realmData.last() else null

    override val size: Int get() = if (realmData.isValid) realmData.size else 0

    internal interface Callback {
        fun onPagePrepended(added: Int)
        fun onPageAppended(added: Int)
    }

    override fun get(index: Int): T? = if (realmData.isValid) realmData[index] else null

    internal fun prependPage(count: Int, callback: Callback) {
        positionOffset -= count
        callback.onPagePrepended(count)
    }

    internal fun appendPage(count: Int, callback: Callback) {
        callback.onPageAppended(count)
    }
}
