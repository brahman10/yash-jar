package com.jar.app.feature_homepage.shared.domain.model.hamburger

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HamburgerData(
    @SerialName("hamburgerItems")
    val hamburgerItems: HamburgerItems? = null,
)