package com.jar.app.feature_post_setup.domain.model.setting

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostSetupQuickActionList(
    @SerialName("quickActionList")
    val postSetupQuickActionList: List<PostSetupQuickActionItem>
)