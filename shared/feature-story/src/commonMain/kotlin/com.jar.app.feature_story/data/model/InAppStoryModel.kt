package com.jar.app.feature_story.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class InAppStoryModel(
    @SerialName("icon")
    val icon: String?,
    @SerialName("title_icon")
    val titleIcon: String? = null,
    val header: String?,
    @SerialName("water_mark")
    val waterMark: String? = "",
    @SerialName("story_id")
    val storyId: String,
    @SerialName("story_name")
    val storyName: String? = "",
    @SerialName("page_type")
    val pageType: String? = "",
    @SerialName("user_segment_ids")
    val userSegmentIds: String? = "",
    @SerialName("thumbnail_url")
    val thumbnailUrl: String?,
    @SerialName("pages")
    val pages: List<Page>?,
    @SerialName("seen_pages")
    val seenPages: Int?,
    @SerialName("total_pages")
    val totalPages: Int?,
    @SerialName("empty_content")
    val emptyContent: EmptyContent?,
    @SerialName("error_response")
    val errorResponse: ErrorResponse?,
    @SerialName("page_change_left_ratio")
    val pageChangeLeftRatio: Int?,
    @SerialName("page_change_right_ratio")
    val pageChangeRightRatio: Int?,
    @SerialName("story_cancel_top_ratio")
    val storyCancelTopRatio: Int?,
    @SerialName("is_pulsating")
    val isPulsating: Boolean?,

    )

@Serializable
data class EmptyContent(
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("text")
    val text: String?
)

@Serializable
data class ErrorResponse(
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("text")
    val text: String?
)

@Serializable
data class Page(
    @SerialName("page_id")
    val pageId: String,
    @SerialName("upload_time")
    val uploadTime: String? = null,
    @SerialName("media_url")
    val mediaUrl: String? = null,
    @SerialName("audio_url")
    val audioUrl: String? = null,
    @SerialName("download_video_url")
    val downloadVideoUrl: String? = null,
    @SerialName("video_thumbnail_url")
    val videoThumbnailUrl: String? = null,
    @SerialName("media_type")
    val mediaType: String,
    @SerialName("duration")
    val duration: Int? = null,
    @SerialName("download_icon")
    val downloadIcon: String? = null,
    @SerialName("share_cta")
    val shareCta: CTA? = null,
    @SerialName("download_cta")
    val downloadCta: CTA? = null,
    @SerialName("cta")
    val cta: CTA? = null,
    @SerialName("is_viewed")
    val isViewed: Boolean,
    @SerialName("viewed_at")
    val viewedAt: String? = null,
    @SerialName("like_cta")
    val likeCta: LikeCTA? = null,
    @SerialName("action_orders")
    val actionOrders: List<ActionOrder>? = null,
    @SerialName("content_id")
    val contentId: String? = null,
    @SerialName("categories")
    val categories: List<String>? = null,
    @Transient var timeToLoadMedia: Long? = null
)

@Serializable
data class CTA(
    @SerialName("icon")
    val icon: String? = null,
    @SerialName("link")
    val link: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("background_color")
    val backgroundColor: String? = null,
    @SerialName("count")
    val count: Int?= null,
    @SerialName("is_downloaded")
    var is_downloaded: Boolean?= null,
    @SerialName("is_shared")
    var is_shared: Boolean?= null
)

@Serializable
data class LikeCTA(
    @SerialName("liked_icon")
    val likedIcon: String,
    @SerialName("unliked_icon")
    val unlikedIcon: String,
    @SerialName("is_liked")
    val isLiked: Boolean,
    @SerialName("count")
    var count: Int? = null,

)

@Serializable
data class ActionOrder(
    @SerialName("action_type")
    val actionType: String,
    @SerialName("order")
    val order: Int
)

enum class MediaType(val value:String){
    IMAGE("image"),
    VIDEO("video")

}
