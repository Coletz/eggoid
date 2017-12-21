package co.eggon.eggoid.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import kotlin.reflect.KClass

fun Context.isConnectionAvailable(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}

fun <T : Activity> Context.clearTask(activity: KClass<T>, vararg extras: Pair<String, Any>) {
    Intent(this, activity.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        extras.forEach {
            when (it.second) {
                is String -> putExtra(it.first, it.second as String)
                is Int -> putExtra(it.first, it.second as Int)
                is Boolean -> putExtra(it.first, it.second as Boolean)
                is Parcelable -> putExtra(it.first, it.second as Parcelable)
                is Bundle -> putExtra(it.first, it.second as Bundle)
            }
        }
        this@clearTask.startActivity(this@apply)
    }
}