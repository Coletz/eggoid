package co.eggon.eggoid

import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewPropertyAnimator

class Groupie<T : View>(vararg views: T) {

    val mViews = ArrayList(views.asList())
    var mAnimator = Animator()

    var visibility: Int = View.VISIBLE
        set(value) {
            field = value
            if (value == View.VISIBLE || value == View.INVISIBLE || value == View.GONE) {
                mViews.forEach { it.visibility = value }
            }
        }

    var isFocusable: Boolean = true
        set(value) {
            field = value
            mViews.forEach { it.isFocusable = value; it.isFocusableInTouchMode = value }
        }

    var isClickable: Boolean = true
        set(value) {
            field = value
            mViews.forEach { it.isClickable = value }
        }

    fun setOnClickListener(listener: ((View) -> Unit)?) {
        mViews.forEach { it.setOnClickListener { listener?.invoke(it) } }
    }

    fun animate(): Animator {
        mAnimator.init()
        return mAnimator
    }

    inner class Animator {
        private val mPropertyAnimators: ArrayList<ViewPropertyAnimator> = ArrayList()

        fun init(): Animator {
            mPropertyAnimators.clear()
            mViews.forEach { mPropertyAnimators.add(it.animate()) }
            return this
        }

        fun setDuration(duration: Long): Animator {
            mPropertyAnimators.forEach {
                it.duration = duration
            }
            return this
        }

        fun setListener(listener: AnimatorListenerAdapter): Animator {
            mPropertyAnimators.forEach {
                it.setListener(listener)
            }
            return this
        }

        fun alpha(alpha: Float): Animator {
            mPropertyAnimators.forEach {
                it.alpha(alpha)
            }
            return this
        }
    }
}