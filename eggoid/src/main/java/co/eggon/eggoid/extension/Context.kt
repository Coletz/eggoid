package co.eggon.eggoid.extension

import android.content.Context
import android.net.ConnectivityManager

fun Context.isConnectionAvailable(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}