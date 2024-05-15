package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail

import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.jar.app.core_ui.extension.setDebounceClickListener
import net.cachapa.expandablelayout.ExpandableLayout

object ExpandableHelper {

    fun setupCollapsible(
        expandableLayouts: List<ExpandableLayout>,
        expandableButtons: List<View>,
        expandableImageViews: List<AppCompatImageView>,
        clickListener: ((View) -> Unit)? = null
    ) {
        expandableButtons.forEachIndexed { index, view ->
            view.setDebounceClickListener {
                clickListener?.invoke(it)
                if (expandableLayouts[index].isExpanded) {
                    expandableLayouts[index].collapse()
                    expandableLayouts.forEachIndexed { index, expandableLayout ->
                        Glide.with(view.context)
                            .load(getExpandedDrawable(false)).into(expandableImageViews[index])
                    }
                } else {
                    expandableLayouts.forEachIndexed { nestedIndex, expandableLayout ->
                        if (nestedIndex == index) {
                            expandableLayout.expand()
                            Glide.with(view.context)
                                .load(getExpandedDrawable(true)).into(expandableImageViews[nestedIndex])
                        } else {
                            expandableLayout.collapse()
                            Glide.with(view.context)
                                .load(getExpandedDrawable(false)).into(expandableImageViews[nestedIndex])
                        }
                    }
                }
            }
        }
    }
}

@DrawableRes
fun getExpandedDrawable(expanded: Boolean): Int {
    return if (!expanded) com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_down else com.jar.app.core_ui.R.drawable.core_ui_ic_arrow_up
}