package com.jar.app.feature_post_setup.domain.model

import com.jar.app.core_base.domain.model.GenericFaqItem
import dev.icerock.moko.resources.StringResource

data class PostSetupFaqPageItem(
    val order: Int,
    val titleRes: StringResource,
    val faq: List<GenericFaqItem>
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}