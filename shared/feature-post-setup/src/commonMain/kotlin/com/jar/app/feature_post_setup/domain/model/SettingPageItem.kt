package com.jar.app.feature_post_setup.domain.model

import com.jar.app.feature_post_setup.domain.model.setting.PostSetupQuickActionList
import dev.icerock.moko.resources.StringResource

data class SettingPageItem(
    val order: Int,
    val titleRes: StringResource,
    val postSetupQuickActionList: PostSetupQuickActionList
) : PostSetupPageItem {
    override fun getSortKey(): Int {
        return order
    }
}