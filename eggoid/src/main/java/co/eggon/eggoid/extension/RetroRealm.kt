package co.eggon.eggoid.extension

import co.eggon.eggoid.RealmPromise
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.*
import io.realm.exceptions.RealmException

@Throws(RealmException::class)
fun <E : RealmModel> Observable<E>.objectToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((E) -> Unit)? = null): RealmPromise<E> {
    val promise = RealmPromise<E>()
    this.subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
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
    this.subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
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