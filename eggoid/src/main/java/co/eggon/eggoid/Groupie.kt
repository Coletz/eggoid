package co.eggon.eggoid

import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator

class Groupie<T : View>(vararg views: T) {

    private val mViews = ArrayList(views.asList())
    var mAnimator = Animator()

    var visibility: Int = View.VISIBLE
        set(value) {
            field = value
            if (value == View.VISIBLE || value == View.INVISIBLE || value == View.GONE) {
                mViews.forEach { it.visibility = value }
            }
        }

    var backgroundColor: Int = Color.WHITE
        set(value) {
            field = value
            mViews.forEach { it.setBackgroundColor(value) }
        }

    var backgroundRes: Int = 0
        set(value) {
            field = value
            mViews.forEach { it.setBackgroundResource(field) }
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

    var layoutParams: ViewGroup.LayoutParams = ViewGroup.LayoutParams(0,0)
        set(value) {
            field = value
            mViews.forEach { it.layoutParams = value }
        }

    var lpWidth: Int = 0
        set(value) {
            field = value
            mViews.forEach { it.layoutParams.width = value }
        }

    var lpHeight: Int = 0
        set(value) {
            field = value
            mViews.forEach { it.layoutParams.height= value }
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