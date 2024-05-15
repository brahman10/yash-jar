package com.jar.app.core_ui.generic_post_action.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.jar.app.core_ui.R

@kotlinx.serialization.Serializable
data class GenericPostActionStatusData(
    val postActionStatus: String,
    val header: String?,
    val title: String? = null,
    val description: String? = null,
    val screenTime: Long = 3,
    val lottieUrl: String? = null,
    val imageUrl: String? = null,
    val headerTextSize: Float = 28f,
    val titleTextSize: Float = 16f,
    val descTextSize: Float = 16f,
    val shouldShowConfettiFromTop: Boolean = true,
    val shouldShowTopProgress: Boolean = true,
    @DrawableRes
    val imageRes: Int? = null,

    @ColorRes
    val headerColorRes: Int = R.color.white,

    @ColorRes
    val titleColorRes: Int = R.color.color_EEEAFF,

    @ColorRes
    val descriptionColorRes: Int = R.color.color_ACA1D3,
)