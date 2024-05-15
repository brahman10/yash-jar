package com.jar.app.core_ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView


/**
 * NestedScrollView in which scrolling can be enabled or disabled.
 * setScrollingEnabled(enabled: Boolean) is used to set whether
 * the scroll needs to be enabled or disabled
 * **/
class LockableNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    // true if we can scroll (not locked)
    // false if we cannot scroll (locked)
    private var mScrollable = true

    fun setScrollingEnabled(enabled: Boolean) {
        mScrollable = enabled
    }

    fun isScrollable(): Boolean {
        return mScrollable
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return when (ev.action) {
            MotionEvent.ACTION_DOWN ->  // if we can scroll pass the event to the superclass
                mScrollable && super.onTouchEvent(ev)
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        return mScrollable && super.onInterceptTouchEvent(ev)
    }

}