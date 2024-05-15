package com.jar.app.feature_settings.domain.model

import dev.icerock.moko.resources.StringResource

data class NotificationSettingsSwitch (
    val title: StringResource,
    val desc: StringResource? = null,
    val isEnabled: Boolean,
    val position: Int
)