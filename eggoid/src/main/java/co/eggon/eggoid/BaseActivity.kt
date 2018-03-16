package co.eggon.eggoid

import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity() {

    /*** Dialogs ***/
    private var errorDialog: AlertDialog? = null
    private var progressDialog: AlertDialog? = null

    /*** Rx ***/
    var disposables = CompositeDisposable()

    override fun onPause() {
        super.onPause()
        dismissErrorDialog()
        dismissProgressDialog()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

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

    fun showProgressDialog(@StringRes msg: Int, @StringRes title: Int? = null, isCancelable: Boolean = false): AlertDialog? =
            title?.let {
                getString(it)
            }.let {
                showProgressDialog(getString(msg), it, isCancelable)
            }

    fun showProgressDialog(msg: String? = null, title: String? = null, isCancelable: Boolean): AlertDialog? {
        if (!isFinishing) {
            val dialoglayout = layoutInflater.inflate(R.layout.progress_dialog, null)
            dialoglayout.findViewById<TextView>(R.id.message).text = msg ?: getString(R.string.realm_activity_loading)

            progressDialog = AlertDialog.Builder(this)
                    .setTitle(title ?: getString(R.string.realm_activity_loading))
                    .setView(dialoglayout)
                    .setCancelable(isCancelable)
                    .show()
            return progressDialog
        }
        return null
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    fun dismissErrorDialog() {
        errorDialog?.dismiss()
    }
}