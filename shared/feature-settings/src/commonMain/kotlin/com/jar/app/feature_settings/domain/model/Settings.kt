package com.jar.app.feature_settings.domain.model

import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource

sealed class Settings {
    abstract val position: Int
    abstract val uniqueId: String
}

data class Setting(
    val title: String? = null,
    val desc: String? = null,
    val descColor: ColorResource? = null,
    val descIcon: ImageResource? = null,
    val startIconRes: ImageResource,
    val viewTag: String,
    override val position: Int,
    override val uniqueId: String = title?.plus(desc)?.plus(position).orEmpty()
) : Settings()

data class SettingGroup(
    val title: String,
    override val position: Int,
    override val uniqueId: String = title.plus(position)
) : Settings()

data class SettingSeparator(
    override val position: Int,
    override val uniqueId: String
) : Settings()