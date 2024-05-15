package com.jar.app.core_ui.item_decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.util.dp

class TimelineItemDecoration(private val sectionCallback: SectionCallback) :
    RecyclerView.ItemDecoration() {

    private val headers: SparseArray<View> = SparseArray()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildLayoutPosition(view)

        if (position != RecyclerView.NO_POSITION && sectionCallback.isHeaderSection(position)) {
            val headerLayout = LayoutInflater.from(parent.context)
                .inflate(sectionCallback.getHeaderLayoutRes(position), parent, false)
            sectionCallback.bindHeaderData(headerLayout, position)
            headers.put(position, headerLayout)
            measureHeaderView(headerLayout, parent)
            outRect.left = headerLayout.width
            outRect.top = 2.dp
        } else {
            headers.remove(position)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(child)
            if (position != RecyclerView.NO_POSITION && sectionCallback.isHeaderSection(position)) {
                canvas.save()
                headers.get(position)?.let { headerView ->
                    canvas.translate(
                        child.x - headerView.width.toFloat(),
                        child.y + 2.dp
                    )
                    headerView.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }

    private fun measureHeaderView(view: View, parent: ViewGroup) {
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
            parent.paddingStart + parent.paddingEnd, view.layoutParams.width
        )
        val childHeight: Int = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom, view.layoutParams.height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    interface SectionCallback {
        fun isHeaderSection(position: Int): Boolean
        @LayoutRes
        fun getHeaderLayoutRes(position: Int): Int
        fun bindHeaderData(view: View, position: Int)
    }
}