package co.eggon.eggoid

import io.realm.RealmList
import io.realm.RealmModel

/**
 * In any class that implements this interface, the overridden data property must have
 * the following annotation:
 * @JsonDeserialize(`as` = MyWrappedRealmObject::class)
 *
 * Example:
 * class SomeResponse : DataWrapper {
 *     @JsonDeserialize(`as` = MyWrappedRealmObject::class)
 *     override var data: RealmModel? = null
 * }
 */
interface DataWrapper {
    var data: RealmModel?
}

/**
 * In any class that implements this interface, the overridden data property must have
 * the following annotation:
 * @JsonDeserialize(contentAs = MyWrappedRealmObject::class)
 *
 * Example:
 * class SomeResponse : DataListWrapper {
 *     @JsonDeserialize(contentAs = MyWrappedRealmObject::class)
 *     override var data: RealmList<RealmModel>? = null
 * }
 */
interface DataListWrapper {
    var data: RealmList<RealmModel>?
}