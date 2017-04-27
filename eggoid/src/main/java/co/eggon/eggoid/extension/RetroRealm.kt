package co.eggon.eggoid.extension

import co.eggon.eggoid.DataListWrapper
import co.eggon.eggoid.DataWrapper
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
                    realm.update(it)
                            .then { promise.action?.invoke(it) }
                            .onError { promise.error?.invoke(it) ?: throw it }
                } else {
                    realm.create(it) then { promise.action?.invoke(it) } onError { promise.error?.invoke(it) ?: throw it }
                }
            }, {
                promise.error?.invoke(it) ?: throw it
            })
    return promise
}

@Throws(RealmException::class)
fun <E : RealmList<out RealmModel>> Observable<E>.listToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((E) -> Unit)? = null): RealmPromise<E> {
    val promise = RealmPromise<E>()
    this.subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                if (update) {
                    beforeSave?.invoke(list)
                    realm.update(list)
                            .then { promise.action?.invoke(list) }
                            .onError { promise.error?.invoke(it) ?: throw it }
                } else {
                    realm.create(list) then { promise.action?.invoke(list) } onError { promise.error?.invoke(it) ?: throw it }
                }
            }, {
                promise.error?.invoke(it) ?: throw it
            })
    return promise
}

/**
 * When using this function to unwrap an object that is returned as a field in the JSON,
 * the class representing the response must implement the DataWrapper interface and
 * the overridden data property must have the following annotation:
 * @JsonDeserialize(`as` = MyWrappedRealmObject::class)
 *
 * Example:
 * class SomeResponse : DataWrapper {
 *     @JsonDeserialize(`as` = MyWrappedRealmObject::class)
 *     override var data: RealmModel? = null
 * }
 */
@Throws(RealmException::class)
fun <E : DataWrapper> Observable<E>.wrappedToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((E) -> Unit)? = null): RealmPromise<E> {
    val promise = RealmPromise<E>()
    this.subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ wrapper ->
                if (update) {
                    beforeSave?.invoke(wrapper)
                    wrapper.data?.let { data ->
                        realm.update(data)
                                .then { promise.action?.invoke(wrapper) }
                                .onError { promise.error?.invoke(it) ?: throw it }
                    }
                } else {
                    wrapper.data?.let { data ->
                        realm.create(data)
                                .then { promise.action?.invoke(wrapper) }
                                .onError { promise.error?.invoke(it) ?: throw it }
                    }
                }
            }, {
                promise.error?.invoke(it) ?: throw it
            })
    return promise
}

/**
 * When using this function to unwrap a list of objects that is returned as a field in the JSON,
 * the class representing the response must implement the DataListWrapper interface and
 * the overridden data property must have the following annotation:
 * @JsonDeserialize(contentAs = MyWrappedRealmObject::class)
 *
 * Example:
 * class SomeResponse : DataListWrapper {
 *     @JsonDeserialize(contentAs = MyWrappedRealmObject::class)
 *     override var data: RealmList<RealmModel>? = null
 * }
 */
@Throws(RealmException::class)
fun <E : DataListWrapper> Observable<E>.wrappedListToRealm(realm: Realm?, update: Boolean = true, beforeSave: ((E) -> Unit)? = null): RealmPromise<E> {
    val promise = RealmPromise<E>()
    this.subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ wrapper ->
                if (update) {
                    beforeSave?.invoke(wrapper)
                    wrapper?.data?.let {
                        realm.update(it)
                                .then { promise.action?.invoke(wrapper) }
                                .onError { promise.error?.invoke(it) ?: throw it }
                    }
                } else {
                    wrapper?.data?.let {
                        realm.create(it)
                                .then { promise.action?.invoke(wrapper) }
                                .onError { promise.error?.invoke(it) ?: throw it }
                    }
                }
            }, {
                promise.error?.invoke(it) ?: throw it
            })
    return promise
}
