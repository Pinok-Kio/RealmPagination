package androidx.paging

import androidx.annotation.IntDef

/**
 * @author S.A.Bobrischev
 * Developed by Magora Team (magora-systems.com). 03.06.18.
 */
class RealmPageResult internal constructor(val loadedCount: Int) {
    val isInvalid: Boolean get() = this === invalidResult

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(INIT, APPEND, PREPEND)
    internal annotation class ResultType

    internal interface Receiver {

        fun onPageResult(@ResultType resultType: Int, pageResult: RealmPageResult)
    }

    companion object {
        internal val invalidResult = RealmPageResult(-1)

        internal const val INIT = 0
        internal const val APPEND = 1
        internal const val PREPEND = 2
    }
}
