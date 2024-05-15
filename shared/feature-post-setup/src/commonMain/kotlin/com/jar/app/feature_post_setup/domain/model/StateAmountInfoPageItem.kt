package com.jar.app.feature_post_setup.domain.model

import com.jar.app.feature_post_setup.domain.model.calendar.StateInfoDetails

data class StateAmountInfoPageItem(
    val order: Int,
    val stateInfoDetails: StateInfoDetails
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}