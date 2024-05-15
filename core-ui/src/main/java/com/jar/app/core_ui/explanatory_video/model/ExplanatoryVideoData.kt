package com.jar.app.core_ui.explanatory_video.model

import com.jar.app.core_base.domain.model.card_library.InfographicType

@kotlinx.serialization.Serializable
data class ExplanatoryVideoData(
    val infographicUrl: String,
    val shouldShowReplayButton: Boolean = true,
    val shouldShowBackButton: Boolean = true,
    val shouldShowSkipButton: Boolean = true,
    val shouldPlayVideoInLoop: Boolean = false,
    val justPopBackStack: Boolean = true,
    val infographicType: InfographicType = InfographicType.VIDEO,
    val deeplink: String? = null,
    val shouldNavigateToDeeplink: Boolean = true,
    val flow: String? = null
)