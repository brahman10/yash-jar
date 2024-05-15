package com.jar.app.core_ui.item_decoration

import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class BaseItemDecoration : RecyclerView.ItemDecoration() {

    protected val headers: SparseArray<View> = SparseArray()

    fun measureHeaderView(view: View, parent: ViewGroup) {
        if (view.layoutParams == null) {
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val displayMetrics: DisplayMetrics = parent.context.resources.displayMetrics
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY)
        val childWidth: Int = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight, view.layoutParams.width
        )
        val childHeight: Int = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom, view.layoutParams.height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {
        fun isItemDecorationSection(position: Int): Boolean
        @LayoutRes
        fun getItemDecorationLayoutRes(position: Int): Int
        fun bindItemDecorationData(view: View, position: Int)
    }
}