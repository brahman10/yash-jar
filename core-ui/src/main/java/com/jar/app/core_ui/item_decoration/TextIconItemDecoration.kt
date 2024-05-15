package com.jar.app.core_ui.item_decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TextIconItemDecoration(private val sectionCallback: SectionCallback) : BaseItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildLayoutPosition(view)

        if (position != RecyclerView.NO_POSITION && sectionCallback.isItemDecorationSection(position)) {
            val headerLayout = LayoutInflater.from(parent.context)
                .inflate(sectionCallback.getItemDecorationLayoutRes(position), parent, false)
            sectionCallback.bindItemDecorationData(headerLayout, position)
            headers.put(position, headerLayout)
            measureHeaderView(headerLayout, parent)
            outRect.top = headerLayout.height
        } else {
            headers.remove(position)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(child)
            if (position != RecyclerView.NO_POSITION && sectionCallback.isItemDecorationSection(
                    position
                )
            ) {
                canvas.save()
                headers.get(position)?.let { headerView ->
                    canvas.translate(
                        (child.width - headerView.width).toFloat() / 2f,
                        child.y - headerView.height
                    )
                    headerView.draw(canvas)
                    canvas.restore()
                }
            }
        }
    }
}