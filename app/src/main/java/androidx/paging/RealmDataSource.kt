package androidx.paging

import io.realm.RealmModel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 02.06.18.
 */
// Since we currently rely on implementation details of two implementations,
// prevent external subclassing, except through exposed subclasses
abstract class RealmDataSource<Key, Value : RealmModel> internal constructor() {
    private val mInvalid = AtomicBoolean(false)
    val isInvalid: Boolean get() = mInvalid.get()

    abstract class Factory<Key, Value : RealmModel> {
        abstract fun create(): RealmDataSource<Key, Value>
    }

    internal class LoadCallbackHelper(
            private val mDataSource: RealmDataSource<*, *>,
            @RealmPageResult.ResultType val mResultType: Int,
            private val mReceiver: RealmPageResult.Receiver) {
        private val mSignalLock = Any()
        private var mHasSignalled = false

        /**
         * Call before verifying args, or dispatching actul results
         *
         * @return true if DataSource was invalid, and invalid result dispatched
         */
        fun dispatchInvalidResultIfInvalid(): Boolean {
            return if (mDataSource.isInvalid) {
                dispatchResultToReceiver(RealmPageResult.invalidResult)
                true
            } else {
                false
            }
        }

        fun dispatchResultToReceiver(result: RealmPageResult) {
            synchronized(mSignalLock) {
                if (mHasSignalled) {
                    throw IllegalStateException("callback.onResult already called, cannot call again.")
                }
                mHasSignalled = true
            }
            mReceiver.onPageResult(mResultType, result)
        }
    }
}
