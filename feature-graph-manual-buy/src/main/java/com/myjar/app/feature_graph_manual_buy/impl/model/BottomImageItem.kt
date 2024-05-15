package com.myjar.app.feature_graph_manual_buy.impl.model

import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem

data class BottomImageItem(
    val order: Int,
val title: String,
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}