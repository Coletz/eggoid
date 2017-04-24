package co.eggon.eggoid.extension

import android.support.annotation.IdRes
import android.view.View

fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T> {
    // FROM: https://medium.com/@quiro91/improving-findviewbyid-with-kotlin-4cf2f8f779bb
    @Suppress("UNCHECKED_CAST")
    return lazy(LazyThreadSafetyMode.NONE, findViewById(idRes) as () -> T )
}