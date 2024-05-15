package com.jar.app.core_ui.item_decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class CustomDividerItemDecorator(private val divider: Drawable?) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + divider!!.intrinsicHeight
            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (parent.adapter != null && position == parent.adapter!!.itemCount - 1) {
            outRect.set(0, 0, 0, divider!!.intrinsicHeight)
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}