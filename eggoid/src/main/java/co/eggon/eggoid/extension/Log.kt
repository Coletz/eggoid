package co.eggon.eggoid.extension

import android.util.Log.*
import co.eggon.eggoid.RealmPromise
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.*
import io.realm.exceptions.RealmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

fun Any?.wtf(obj: Any = "[ASSERT]") {
    this.log(obj, ASSERT)
}

fun Any?.error(obj: Any = "[ERROR]") {
    this.log(obj, ERROR)
}

fun Any?.warn(obj: Any = "[WARN]") {
    this.log(obj, WARN)
}

fun Any?.info(obj: Any = "[INFO]") {
    this.log(obj, INFO)
}

fun Any?.debug(obj: Any = "[DEBUG]") {
    this.log(obj, DEBUG)
}

fun Any?.verbose(obj: Any = "[VERBOSE]") {
    this.log(obj, VERBOSE)
}

private fun Any?.log(obj: Any? = null, level: Int = ERROR) {
    val logger = when (obj) {
        is String -> obj
        is Class<*> -> obj::class.java.simpleName
        is KClass<*> -> obj.java.simpleName
        null -> if (this is Enum<*>) "Enum" else "Default"
        else -> obj.javaClass.simpleName
    }

    val message = when (this) {
        is Enum<*>? -> if (this == null) "NullEnum" else "${this::class.java.simpleName}.${this.name}"
        is String? -> if (this == null) "NullString" else "String: $this"
        is Int? -> if (this == null) "NullInt" else "Int: $this"
        is Float? -> if (this == null) "NullFloat" else "Float: $this"
        is Double? -> if (this == null) "NullDouble" else "Double: $this"
        else -> if (this == null) "NullValue" else "Value: $this"
    }

    when (level) {
        VERBOSE -> v(logger, message)
        DEBUG -> d(logger, message)
        INFO -> i(logger, message)
        WARN -> w(logger, message)
        ERROR -> e(logger, message)
        else -> wtf(logger, message)
    }

    if (this is Throwable) {
        e(logger, "******** STACK TRACE ********")
        this.printStackTrace()
    } else {
        e(logger, "Not a throwable")
    }
}