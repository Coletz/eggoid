package co.eggon.eggoid

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import kotlin.reflect.KClass

abstract class BaseFragment(val layout: Int? = null) : Fragment() {

    companion object {
        fun <T : Fragment> newInstance(kClass: KClass<T>, bundle: Bundle? = null): Fragment =
                kClass.java.newInstance().apply { bundle?.let { arguments = it } }
    }

    /*** Rx ***/
    var disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layout?.let {
            inflater.inflate(layout, container, false)
        } ?: run {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }
}