package co.eggon.eggoid.extension

import android.content.Context
import android.util.DisplayMetrics

fun Float.dpToPx(context: Context): Float =
        this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun Float.pxToDp(context: Context): Float =
        this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)