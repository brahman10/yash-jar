package com.jar.app.feature_post_setup.domain.model

data class SetupDetailsPageItem(
    val order: Int,
    val userPostSetupData: UserPostSetupData
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}