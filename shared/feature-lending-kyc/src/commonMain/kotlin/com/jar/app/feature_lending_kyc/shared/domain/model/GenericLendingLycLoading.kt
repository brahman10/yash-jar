package com.jar.app.feature_lending_kyc.shared.domain.model

data class AssetUrl(val assetUrl: String, val isIllustrationUrl: Boolean)
data class ProgressSuccess(val title: String, val lottieUrl: String)
data class ProgressDismiss(
    val dismissTime: Long,
    val isDismissingAfterSuccess: Boolean = false,
    val from: String
)

data class ProgressDismissResult(
    val isDismissingAfterSuccess: Boolean = false,
    val fromScreen: String
)