package co.eggon.eggoid

import android.os.Bundle
import co.eggon.eggoid.extension.create
import co.eggon.eggoid.extension.debug
import co.eggon.eggoid.extension.remove
import co.eggon.eggoid.extension.update
import io.realm.*
import io.realm.exceptions.RealmException
import kotlin.reflect.KClass


abstract class RealmActivity : BaseActivity() {
    var realm: Realm? = null
    private var realmConfig: RealmConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realmConfig = onRealmSetup()
        open()
    }

    open fun onRealmSetup(): RealmConfiguration? = null

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
        realm = realmConfig?.let {
            "Loading custom realm file: ${it.realmFileName}".debug()
            Realm.getInstance(it)
        } ?: run {
            val r = Realm.getDefaultInstance()
            "Loading default realm file: ${r.configuration.realmFileName}.realm".debug()
            r
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
        return realm?.let {
            if (update) {
                it.update(obj)
            } else {
                it.create(obj)
            }
        } ?: throw RealmException("Can't insert data into a closed realm")
    }

    fun <T : RealmModel> insert(obj: RealmList<T>, update: Boolean = true): RealmPromise<RealmList<T>> {
        return realm?.let {
            if (update) {
                it.update(obj)
            } else {
                it.create(obj)
            }
        } ?: throw RealmException("Can't insert data into a closed realm")
    }

    fun <T : RealmObject> remove(obj: T) {
        realm?.let {
            it.executeTransaction {
                obj.deleteFromRealm()
            }
        } ?: throw RealmException("Can't remove data from a closed realm")
    }

    fun <T : RealmModel> remove(list: RealmList<T>) {
        realm?.let {
            it.executeTransaction {
                list.deleteAllFromRealm()
            }
        } ?: throw RealmException("Can't remove data from a closed realm")
    }

    fun <T : RealmModel> remove(kclass: KClass<T>, criteria: List<Pair<String, String>>, case: Case = Case.INSENSITIVE): RealmPromise<Boolean> =
            realm?.remove(kclass, criteria, case) ?: throw RealmException("Can't remove data from a closed realm")

    fun <T : RealmModel> select(kclass: KClass<T>): RealmQuery<T> =
            realm?.where(kclass.java) ?: throw RealmException("Can't query a closed realm")
}
