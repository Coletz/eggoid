package co.eggon.eggoid.extension

import co.eggon.eggoid.RealmPromise
import co.eggon.eggoid.extension.create
import co.eggon.eggoid.extension.update
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.*
import io.realm.exceptions.RealmException
import kotlin.reflect.KClass

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