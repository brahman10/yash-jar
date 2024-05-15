package com.jar.app.feature_post_setup.domain.model

data class BottomSectionPageItem(
    val order: Int,
    val imageUrl: String
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}
