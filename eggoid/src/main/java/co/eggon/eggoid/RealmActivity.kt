package co.eggon.eggoid

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import co.eggon.eggoid.extension.create
import co.eggon.eggoid.extension.debug
import co.eggon.eggoid.extension.remove
import co.eggon.eggoid.extension.update
import io.reactivex.disposables.CompositeDisposable
import io.realm.*
import io.realm.exceptions.RealmException
import kotlin.reflect.KClass
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView


open class RealmActivity : AppCompatActivity() {
    var realm: Realm? = null
    private var realmConfig: RealmConfiguration? = null

    /*** Dialogs ***/
    private var loadingDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    /*** Rx ***/
    var disposables = CompositeDisposable()

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

    override fun onPause() {
        super.onPause()
        dismissErrorDialog()
        dismissLoadingDialog()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    fun changeConfig(newConfig: RealmConfiguration? = null) {
        close()
        realmConfig = newConfig
        open()
    }

    private fun open() {
        realm = realmConfig?.let {
            "Loading custom realm file: ${it.realmFileName}".debug()
            Realm.getInstance(realmConfig)
        } ?: run {
            "Loading default.realm".debug()
            Realm.getDefaultInstance()
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

    /**
     * Error dialog and loading view
     **/
    fun showErrorDialog(@StringRes msg: Int, onClick: (() -> Unit)? = null): AlertDialog? =
        showErrorDialog(getString(msg), onClick)

    fun showErrorDialog(msg: String? = null, onClick: (() -> Unit)? = null): AlertDialog? {
        if (!isFinishing) {
            val message = msg ?: getString(R.string.realm_activity_error)
            errorDialog = AlertDialog.Builder(this)
                    .setTitle(R.string.realm_activity_error)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.realm_activity_ok, { _, _ -> dismissErrorDialog(); onClick?.invoke() })
                    .show()
            return errorDialog
        }
        return null
    }

    fun showLoadingDialog(@StringRes msg: Int): AlertDialog? =
        showLoadingDialog(getString(msg))

    fun showLoadingDialog(msg: String? = null): AlertDialog? {
        if (!isFinishing) {
            val dialoglayout = layoutInflater.inflate(R.layout.realm_activity_loading_dialog, null)
            dialoglayout.findViewById<Button>(R.id.realm_activity_loading_message).text = msg ?: getString(R.string.realm_activity_error)

            loadingDialog = AlertDialog.Builder(this)
                    .setTitle(R.string.realm_activity_error)
                    .setView(dialoglayout)
                    .setCancelable(false)
                    .show()
            return loadingDialog
        }
        return null
    }

    fun dismissLoadingDialog(){
        if(loadingDialog?.isShowing == true){
            loadingDialog?.dismiss()
        }
    }

    fun dismissErrorDialog(){
        if(errorDialog?.isShowing == true){
            errorDialog?.dismiss()
        }
    }
}