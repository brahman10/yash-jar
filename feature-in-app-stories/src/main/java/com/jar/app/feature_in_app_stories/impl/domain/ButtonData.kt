package com.jar.app.feature_in_app_stories.impl.domain

data class ImageButtonData(
    val id: Int,
    val iconUrl: String?,
    val text:String? = null,
    val actionType: ActionType,
    val btnColor: String? = null,
    val count: Int? = null
)
enum class ActionType(val value:String){
    LIKE("Like"),
    SHARE("Share"),
    DOWNLOAD("Download"),
    CTA("CTA")
}

enum class DeepLinkType(val value: String){
    INTERNAL("Internal"),
    EXTERNAL("External")
}