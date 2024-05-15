package com.jar.app.feature_homepage.impl.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyViewHolder

class HomeSpaceItemDecoration(
    private val horizontalSpace: Int,
    private val verticalSpace: Int,
    private var orientation: Int = RecyclerView.VERTICAL,
    private val escapeEdges: Boolean = false,
    private val ignorePaddingViewHolders: List<Class<*>> = emptyList()
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val viewHolder = parent.getChildViewHolder(view)
        val model = (viewHolder as EpoxyViewHolder).model
        if (ignorePaddingViewHolders.contains(model.javaClass)) {
            return
        }

        if (escapeEdges) {
            val pos = parent.getChildAdapterPosition(view)
            if (orientation == RecyclerView.VERTICAL) {
                outRect.left = horizontalSpace
                outRect.right = horizontalSpace
                outRect.top =  verticalSpace
                outRect.bottom = if (pos == (parent.adapter?.itemCount ?: (0 - 1))) 0 else verticalSpace
            } else if (orientation == RecyclerView.HORIZONTAL) {
                outRect.left = if (pos == 0) 0 else horizontalSpace
                outRect.right =
                    if (pos == (parent.adapter?.itemCount ?: (0 - 1))) 0 else horizontalSpace
                outRect.top = verticalSpace
                outRect.bottom = verticalSpace
            }
        } else {
            outRect.left = horizontalSpace
            outRect.right = horizontalSpace
            outRect.top = verticalSpace
            outRect.bottom = verticalSpace
        }
    }
}