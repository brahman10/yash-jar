package com.jar.app.feature_weekly_magic_common.shared.domain.model

data class TextMessageModel(
    val text:String,
    val highLight:Boolean,
    var animationDone:Boolean = false
)
