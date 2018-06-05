package androidx.paging

import io.realm.RealmModel

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 02.06.18.
 */
abstract class RealmContiguousDataSource<Key, Value : RealmModel> : RealmDataSource<Key, Value>() {

    internal abstract fun dispatchLoadInitial(key: Key?,
                                              initialLoadSize: Int,
                                              pageSize: Int,
                                              receiver: RealmPageResult.Receiver)

    internal abstract fun dispatchLoadAfter(currentEndIndex: Int,
                                            currentItemsCount: Int,
                                            currentEndItem: Value,
                                            pageSize: Int,
                                            receiver: RealmPageResult.Receiver)

    internal abstract fun dispatchLoadBefore(currentBeginIndex: Int,
                                             currentItemsCount: Int,
                                             currentBeginItem: Value,
                                             pageSize: Int,
                                             receiver: RealmPageResult.Receiver)

    /**
     * Get the key from either the position, or item, or null if position/item invalid.
     *
     * Position may not match passed item's position - if trying to query the key from a position
     * that isn't yet loaded, a fallback item (last loaded item accessed) will be passed.
     */
    internal abstract fun getKey(position: Int, item: Value?): Key?
}
