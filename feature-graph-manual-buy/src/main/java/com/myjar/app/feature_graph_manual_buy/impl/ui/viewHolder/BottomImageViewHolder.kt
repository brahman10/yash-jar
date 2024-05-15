package com.myjar.app.feature_graph_manual_buy.impl.ui.viewHolder

import com.jar.app.base.ui.BaseResources
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.myjar.app.feature_graph_manual_buy.databinding.BottomImageLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.model.BottomImageItem

class BottomImageViewHolder (
    private val binding: BottomImageLayoutBinding
        ): BaseViewHolder(binding.root), BaseResources {
            fun bind(item: BottomImageItem) {}
}