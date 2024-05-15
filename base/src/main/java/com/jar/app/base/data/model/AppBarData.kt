package com.jar.app.base.data.model

data class AppBarData(
    val toolbar: CustomToolbar = ToolbarNone
)

sealed class CustomToolbar

data class ToolbarDefault(
    val title: String? = null,
    val showBackButton: Boolean = true,
    val showSeparator: Boolean = false,
    val backFactorScale: Float? = null
) : CustomToolbar()

data class ToolbarHome(
    val showSettings: Boolean = false,
    val showStoryIcon: Boolean = false,
    val showNotification: Boolean = false,
    val showGoldPrice: Boolean = false,
    val showSeparator: Boolean = true
) : CustomToolbar()

data class CustomisableToolbarHome(
    val showSettings: Boolean = false,
    val showNotification: Boolean = false,
    val showStoryIcon: Boolean = false,
    val showGoldPrice: Boolean = false,
    val showSeparator: Boolean = true,
    val startColor:Int = com.jar.app.core_base.shared.R.color.color_272239,
    val endColor:Int = com.jar.app.core_base.shared.R.color.color_272239
) : CustomToolbar()

object ToolbarNone : CustomToolbar()