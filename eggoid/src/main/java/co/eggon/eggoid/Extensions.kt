package co.eggon.eggoid

import android.util.Log.*
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
        VERBOSE -> android.util.Log.v(logger, message)
        DEBUG -> android.util.Log.d(logger, message)
        INFO -> android.util.Log.i(logger, message)
        WARN -> android.util.Log.w(logger, message)
        ERROR -> android.util.Log.e(logger, message)
        else -> android.util.Log.wtf(logger, message)
    }

    if (this is Throwable) {
        android.util.Log.e(logger, "******** STACK TRACE ********")
        this.printStackTrace()
    } else {
        android.util.Log.e(logger, "Not a throwable")
    }
}

fun Date.ageNow(): Int {
    val now = Calendar.getInstance()
    val birthday = Calendar.getInstance()
    birthday.time = this
    var diff = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)
    if (birthday.get(Calendar.MONTH) > now.get(Calendar.MONTH) || birthday.get(Calendar.MONTH) == now.get(Calendar.MONTH) && birthday.get(Calendar.DATE) > now.get(Calendar.DATE)) {
        diff--
    }
    return diff
}

fun String?.capitalizeFully(): String? {
    this?.toLowerCase()?.toCharArray()?.let {
        if (it.isNotEmpty()) {
            var capitalizeNext = true
            for (i in 0..it.size - 1) {
                val ch = it[i]
                if (ch == ' ') {
                    capitalizeNext = true
                } else if (capitalizeNext) {
                    it[i] = Character.toTitleCase(ch)
                    capitalizeNext = false
                }
            }
        }
        return String(it)
    }
    return null
}

fun Date?.asString(toFormat: String = "dd/MM/yyyy"): String {
    if (this == null) {
        return ""
    } else {
        try {
            val formatter = SimpleDateFormat(toFormat, Locale.ROOT)
            return formatter.format(this)
        } catch(e: Exception) {
            e.wtf("Exception")
            return ""
        }
    }
}

fun String?.asDate(fromFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ"): Date? {
    if (this == null) {
        return null
    } else {
        try {
            val formatter = SimpleDateFormat(fromFormat, Locale.ROOT)
            return formatter.parse(this)
        } catch(e: Exception) {
            //e.wtf("Exception")
            android.util.Log.e("err", "ex $e")
            return null
        }
    }
}

fun String?.asFormattedDate(inputFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ", outputFormat: String = "dd/MM/yyyy"): String? {
    return this.asDate().asString()
}

/****************
 * RealmPromise *
 ****************/

/**
 * Insert a RealmObject / RealmList<E> on realm
 **/

fun <E : RealmModel> Realm?.create(obj: E): RealmPromise<E> {

    val promise = RealmPromise<E>()
    this?.let { realm ->
        if (!realm.isClosed) {
            realm.executeTransactionAsync(
                    { it.insert(obj) },
                    { promise.action?.invoke(obj) },
                    { promise.error?.invoke(it) }
            )
        }
    }
    return promise
}

fun <E : RealmModel> Realm?.create(list: RealmList<E>): RealmPromise<RealmList<E>> {
    val promise = RealmPromise<RealmList<E>>()
    this?.let { realm ->
        if (!realm.isClosed) {
            realm.executeTransactionAsync(
                    { it.insert(list) },
                    { promise.action?.invoke(list) },
                    { promise.error?.invoke(it) }
            )
        }
    }
    return promise
}

/**
 * Update a RealmObject (MUST have a PrimaryKey) / RealmList<E> (E MUST have a PrimaryKey) on realm. Object will be created if it doesn't exist
 **/
fun <E : RealmModel> Realm?.update(obj: E): RealmPromise<E> {
    val promise = RealmPromise<E>()
    this?.let { realm ->
        if (!realm.isClosed) {
            realm.executeTransactionAsync(
                    { it.insertOrUpdate(obj) },
                    { promise.action?.invoke(obj) },
                    { promise.error?.invoke(it) }
            )
        }
    }
    return promise
}

fun <E : RealmModel> Realm?.update(list: RealmList<E>): RealmPromise<RealmList<E>> {
    val promise = RealmPromise<RealmList<E>>()
    this?.let { realm ->
        if (!realm.isClosed) {
            realm.executeTransactionAsync(
                    { it.insertOrUpdate(list) },
                    { promise.action?.invoke(list) },
                    { promise.error?.invoke(it) }
            )
        }
    }
    return promise
}

/**
 * Remove all RealmResults from realm using a query
 **/
fun <E : RealmModel> Realm?.remove(kclass: KClass<E>, criteria: List<Pair<String, String>>, case: Case): RealmPromise<Boolean> {
    val promise = RealmPromise<Boolean>()
    var deleted = false
    this?.let { realm ->
        if (!realm.isClosed) {
            realm.executeTransactionAsync(
                    { realm ->
                        val query = realm.where(kclass.java)
                        criteria.forEach { query.equalTo(it.first, it.second, case) }
                        deleted = query.findAll().deleteAllFromRealm()
                    },
                    { promise.action?.invoke(deleted) },
                    { promise.error?.invoke(it) }
            )
        }
    }
    return promise
}

/**
 * Query an object on realm
 **/
fun <E : RealmModel> RealmQuery<E>?.query(): RealmPromise<RealmResults<E>> {
    val promise = RealmPromise<RealmResults<E>>()
    this?.let { query ->
        val result = query.findAllAsync()
        result?.addChangeListener { elements ->
            promise.action?.invoke(elements)
        }
    }
    return promise
}

/*************************
 * RetroRealm extensions *
 *************************/

@Throws(RealmException::class)
fun <E : RealmModel> Observable<E>.objectToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((E) -> Unit)? = null): RealmPromise<E> {
    val promise = RealmPromise<E>()
    this.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (update) {
                    beforeSave?.invoke(it)
                    realm.update(it) then { promise.action?.invoke(it) } onError { promise.error?.invoke(it) ?: throw it }
                } else {
                    realm.create(it) then { promise.action?.invoke(it) } onError { promise.error?.invoke(it) ?: throw it }
                }
            }, {
                promise.error?.invoke(it) ?: throw it
            })
    return promise
}

@Throws(RealmException::class)
fun <E : RealmList<out RealmModel>> Observable<E>.listToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((E) -> Unit)? = null): RealmPromise<RealmList<out RealmModel>> {
    val promise = RealmPromise<RealmList<out RealmModel>>()
    this.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (update) {
                    beforeSave?.invoke(it)
                    realm.update(it) then { promise.action?.invoke(it) } onError { promise.error?.invoke(it) ?: throw it }
                } else {
                    realm.create(it) then { promise.action?.invoke(it) } onError { promise.error?.invoke(it) ?: throw it }
                }
            }, {
                promise.error?.invoke(it) ?: throw it
            })
    return promise
}