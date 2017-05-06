package co.eggon.eggoid.extension

import android.util.Log.*
import java.text.SimpleDateFormat
import java.util.*

fun Date.ageNow(): Int {
    val now = Calendar.getInstance()
    val birthday = Calendar.getInstance()
    birthday.time = this
    var diff = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)
    if (birthday.get(Calendar.MONTH) > now.get(Calendar.MONTH) ||
            birthday.get(Calendar.MONTH) == now.get(Calendar.MONTH) && birthday.get(Calendar.DATE) > now.get(Calendar.DATE)) {
        diff--
    }
    return diff
}

fun Date?.asString(toFormat: String = "dd/MM/yyyy", locale: Locale = Locale.getDefault()): String {
    if (this == null) {
        return ""
    } else {
        try {
            val formatter = SimpleDateFormat(toFormat, locale)
            return formatter.format(this)
        } catch(e: Exception) {
            e.wtf("Exception")
            return ""
        }
    }
}

fun String?.asDate(fromFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ", locale: Locale = Locale.getDefault()): Date? {
    if (this == null) {
        return null
    } else {
        try {
            val formatter = SimpleDateFormat(fromFormat, locale)
            return formatter.parse(this)
        } catch(e: Exception) {
            //e.wtf("Exception")
            e("err", "ex $e")
            return null
        }
    }
}

fun String?.asFormattedDate(inputFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ", outputFormat: String = "dd/MM/yyyy", locale: Locale = Locale.getDefault()): String? {
    return this.asDate(inputFormat, locale).asString(outputFormat, locale)
}