package co.eggon.eggoid

import io.realm.RealmList
import io.realm.RealmModel

/**
 * The class implementing this interface must specify what type of RealmObject it is wrapping.
 *
 * Example:
 * class SomeResponse : DataWrapper<WrappedRealmObject> {
 *     override var data: WrappedRealmObject? = null
 * }
 */
interface DataWrapper<T : RealmModel> {
    var data: T?
}

/**
 * The class implementing this interface must specify what type of RealmObject the list
 * is wrapping.
 *
 * Example:
 * class SomeResponse : DataListWrapper<WrappedRealmObject> {
 *     override var data: RealmList<WrappedRealmObject>? = null
 * }
 */
interface DataListWrapper<T: RealmModel> {
    var data: RealmList<T>?
}