package com.jar.app.feature_jar_duo.shared.domain.model.v2.duo_main_page

@kotlinx.serialization.Serializable
data class DuoClickHandle(
    val deepLink: String? = null,
    val message: String? = null,
    val optionName: String? = null,
    val initialState:Boolean = false
)
