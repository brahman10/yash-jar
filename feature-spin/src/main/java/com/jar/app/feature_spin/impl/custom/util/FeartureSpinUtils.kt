package com.jar.app.feature_spin.impl.custom.util

import android.app.Activity
import android.content.Context
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.updateMargins
import kotlin.math.roundToInt

fun getWidthAndHeight(activity: Activity): Pair<Int, Int> {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    return Pair(screenWidth, screenHeight)
}

fun fromHtml(html: String): Spanned {
    return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun setMarginAsPercentage(context: Context, view: View, leftPercentage: Int, topPercentage: Int, rightPercentage: Int, bottomPercentage: Int) {
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    val leftMargin = (screenWidth * (leftPercentage / 100.0)).roundToInt()
    val topMargin = (screenHeight * (topPercentage / 100.0)).roundToInt()
    val rightMargin = (screenWidth * (rightPercentage / 100.0)).roundToInt()
    val bottomMargin = (screenHeight * (bottomPercentage / 100.0)).roundToInt()

    val layoutParams = view.layoutParams as? ConstraintLayout.LayoutParams
        ?: throw IllegalArgumentException("View's layout params must be of type ConstraintLayout.LayoutParams")

    layoutParams.updateMargins(
        left = leftMargin,
        top = topMargin,
        right = rightMargin,
        bottom = bottomMargin
    )

    view.layoutParams = layoutParams
}

