package com.jar.app.feature_homepage.shared.domain.model.partner_banner

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class BannerList(
    @SerialName("banners")
    val banners: MutableList<Banner>
)