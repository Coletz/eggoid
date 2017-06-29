package co.eggon.eggoid.extension

import co.eggon.eggoid.RealmPromise
import io.realm.*
import io.realm.exceptions.RealmException
import java.util.*
import kotlin.reflect.KClass

/****************
 * RealmPromise *
 ****************/

/**
 * Check if realm is not null and not closed
 * If true return the realm, otherwise return null and report the exception to the onError handler of the promise
 **/
fun <T> RealmPromise<T>?.checkRealmStatus(realm: Realm?): Realm? {
    realm?.let {
        if(it.isClosed){
            this?.error?.invoke(RealmException("Realm is closed"))
            return null
        } else {
            return it
        }
    } ?: run {
        this?.error?.invoke(RealmException("Realm is null"))
        return null
    }
}

/**
 * Execute an async transaction in a safe realm
 **/

fun Realm?.safeExec(async: (Realm) -> Unit): RealmPromise<Boolean> {
    val promise = RealmPromise<Boolean>()
    promise.checkRealmStatus(this)?.let { realm ->
        realm.executeTransactionAsync(
                { async(it) },
                { promise.action?.invoke(true) },
                { promise.error?.invoke(it) }
        )
    }
    return promise
}

/**
 * Insert a RealmObject / RealmList<E> on realm
 **/
fun <E : RealmModel> Realm?.create(obj: E): RealmPromise<E> {
    RealmPromise<E>().let { promise ->
        this.safeExec { it.insert(obj) } then { promise.action?.invoke(obj) } onError { promise.error?.invoke(it) }
        return promise
    }
}

fun <E : RealmModel> Realm?.create(list: RealmList<E>): RealmPromise<RealmList<E>> {
    RealmPromise<RealmList<E>>().let { promise ->
        this.safeExec { it.insert(list) } then { promise.action?.invoke(list) } onError { promise.error?.invoke(it) }
        return promise
    }
}

/**
 * Update a RealmObject (MUST have a PrimaryKey) / RealmList<E> (E MUST have a PrimaryKey) on realm. Object will be created if it doesn't exist
 **/
fun <E : RealmModel> Realm?.update(obj: E): RealmPromise<E> {
    RealmPromise<E>().let { promise ->
        this.safeExec { it.insertOrUpdate(obj) } then { promise.action?.invoke(obj) } onError { promise.error?.invoke(it) }
        return promise
    }
}

fun <E : RealmModel> Realm?.update(list: RealmList<E>): RealmPromise<RealmList<E>> {
    RealmPromise<RealmList<E>>().let { promise ->
        this.safeExec { it.insertOrUpdate(list) } then { promise.action?.invoke(list) } onError { promise.error?.invoke(it) }
        return promise
    }
}

/**
 * Remove all RealmResults from realm using a query
 **/
fun <E : RealmModel> Realm?.remove(kclass: KClass<E>, criteria: Pair<String, Any>, case: Case = Case.SENSITIVE): RealmPromise<Boolean> {
    return this.remove(kclass, listOf(criteria), case)
}

fun <E : RealmModel> Realm?.remove(kclass: KClass<E>, criteria: List<Pair<String, Any>>, case: Case = Case.SENSITIVE): RealmPromise<Boolean> {
    var deleted = false
    RealmPromise<Boolean>().let { promise ->
        this.safeExec { asyncRealm ->
                    val query = asyncRealm.where(kclass.java)
                    criteria.forEach {
                        val secondParam = it.second
                        when(secondParam){
                            is Date -> query.equalTo(it.first, secondParam)
                            is Boolean -> query.equalTo(it.first, secondParam)
                            is Byte -> query.equalTo(it.first, secondParam)
                            is ByteArray -> query.equalTo(it.first, secondParam)
                            is Double -> query.equalTo(it.first, secondParam)
                            is Float -> query.equalTo(it.first, secondParam)
                            is Int -> query.equalTo(it.first, secondParam)
                            is Long -> query.equalTo(it.first, secondParam)
                            is Short -> query.equalTo(it.first, secondParam)
                            is String -> query.equalTo(it.first, secondParam, case)
                            else -> throw TypeCastException("Criteria support only primitives")
                        }
                    }
                    deleted = query.findAll().deleteAllFromRealm()
        } then {
            promise.action?.invoke(deleted)
        } onError {
            promise.error?.invoke(it)
        }
        return promise
    }
}

/**
 * Query an object on realm and immediatly return a result
 **/
fun <E : RealmModel> RealmQuery<E>?.search(): RealmPromise<RealmResults<E>> {
    val promise = RealmPromise<RealmResults<E>>()
    this?.let { query ->
        val result = query.findAllAsync()
        result?.addChangeListener { elements ->
            if(result.isLoaded && result.isManaged && result.isValid){
                result.removeAllChangeListeners()
                promise.action?.invoke(elements)
            }
        }
    }
    return promise
}

fun <E : RealmModel> RealmQuery<E>?.queryAsync(): RealmPromise<RealmResults<E>> {
    val promise = RealmPromise<RealmResults<E>>()
    this?.let { query ->
        val result = query.findAllAsync()
        result?.addChangeListener { elements ->
            promise.action?.invoke(elements)
        }
    }
    return promise
}