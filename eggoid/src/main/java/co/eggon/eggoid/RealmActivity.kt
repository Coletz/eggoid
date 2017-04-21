package co.eggon.eggoid

import android.app.Activity
import android.os.Bundle
import io.realm.*
import io.realm.exceptions.RealmException
import kotlin.reflect.KClass

open class RealmActivity : Activity() {
    internal var realm: Realm? = null
    private var realmConfig: RealmConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realmConfig = onRealmSetup()
        open()
    }

    open fun onRealmSetup(): RealmConfiguration? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        close()
    }

    fun changeConfig(newConfig: RealmConfiguration? = null) {
        close()
        realmConfig = newConfig
        open()
    }

    private fun open() {
        if (realmConfig != null) {
            "Realm file loaded: ${realmConfig!!.realmFileName}".debug()
            realm = Realm.getInstance(realmConfig)
        } else {
            "Default realm loaded: ${realmConfig?.realmFileName}".debug()
            realm = Realm.getDefaultInstance()
        }
    }

    private fun close() {
        realm?.let {
            if (!it.isClosed) {
                it.close()
            }
        }
    }

    fun <T : RealmModel> insert(obj: T, update: Boolean = true): RealmPromise<T> {
        if (realm == null) {
            throw RealmException("Can't insert data into a closed realm")
        } else {
            return if (update) {
                realm!!.update(obj)
            } else {
                realm!!.create(obj)
            }
        }
    }

    fun <T : RealmModel> insert(obj: RealmList<T>, update: Boolean = true): RealmPromise<RealmList<T>> {
        if (realm == null) {
            throw RealmException("Can't insert data into a closed realm")
        } else {
            return if (update) {
                realm.update(obj)
            } else {
                realm.create(obj)
            }
        }
    }

    fun <T : RealmObject> remove(obj: T) {
        if (realm == null) {
            throw RealmException("Can't insert data into a closed realm")
        } else {
            realm!!.executeTransaction {
                obj.deleteFromRealm()
            }
        }
    }

    fun <T : RealmModel> remove(list: RealmList<T>) {
        if (realm == null) {
            throw RealmException("Can't insert data into a closed realm")
        } else {
            realm!!.executeTransaction {
                list.deleteAllFromRealm()
            }
        }
    }

    fun <T : RealmModel> remove(kclass: KClass<T>, criteria: List<Pair<String, String>>, case: Case = Case.INSENSITIVE): RealmPromise<Boolean> {
        if (realm == null) {
            throw RealmException("Can't insert data into a closed realm")
        } else {
            return realm.remove(kclass, criteria, case)
        }
    }

    fun <T : RealmModel> select(kclass: KClass<T>): RealmQuery<T> {
        if (realm == null) {
            throw RealmException("Can't query a closed realm")
        } else {
            return realm!!.where(kclass.java)
        }
    }
}