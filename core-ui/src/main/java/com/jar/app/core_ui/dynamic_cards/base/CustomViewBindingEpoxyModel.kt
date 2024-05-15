package com.jar.app.core_ui.dynamic_cards.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.airbnb.epoxy.EpoxyModel

abstract class CustomViewBindingEpoxyModel<T : ViewBinding>(
    @LayoutRes private val layoutRes: Int
) : EpoxyModel<View>() {

    abstract fun getBinding(view: View): T

    abstract fun bindItem(binding: T)

    override fun bind(view: View) {
        super.bind(view)
        bindItem(getBinding(view))
    }

    override fun getDefaultLayout() = layoutRes
}