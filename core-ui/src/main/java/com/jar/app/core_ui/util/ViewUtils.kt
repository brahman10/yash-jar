package com.jar.app.core_ui.util

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import com.jar.app.base.util.isNullOrZero

fun applyRoundedRectangleDrawableWithBorder(
    targetView: View,
    @ColorInt bgColor: Int,
    radius: Float,
    @ColorInt borderColor: Int,
    borderThickness: Int,
    dashSpace: Float? = null,
    dashLength: Float? = null
) {
    val gradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(bgColor)
        if (radius > 0) cornerRadius = radius
        if (dashSpace != null && dashLength != null) {
            setStroke(borderThickness, borderColor, dashLength, dashSpace)
        } else {
            setStroke(borderThickness, borderColor)
        }
    }
    targetView.background = gradientDrawable
}
fun applyRoundedRectBackground(
    targetView: View,
    @ColorInt bgColor: Int,
    radius: Float = 0.0f,
    topLeftRadius: Float = 0.0f,
    topRightRadius: Float = 0.0f,
    bottomLeftRadius: Float = 0.0f,
    bottomRightRadius: Float = 0.0f,
    strokeWidth: Int = 0,
    @ColorInt strokeColor: Int? = null
) {
    targetView.background = createRoundedRectBackground(
        targetView = targetView,
        bgColor = bgColor,
        radius = radius,
        topLeftRadius = topLeftRadius,
        topRightRadius = topRightRadius,
        bottomLeftRadius = bottomLeftRadius,
        bottomRightRadius = bottomRightRadius,
        strokeWidth = strokeWidth,
        strokeColor = strokeColor
    )
}

fun createRoundedRectBackground(
    targetView: View,
    @ColorInt bgColor: Int,
    radius: Float = 0.0f,
    topLeftRadius: Float = 0.0f,
    topRightRadius: Float = 0.0f,
    bottomLeftRadius: Float = 0.0f,
    bottomRightRadius: Float = 0.0f,
    strokeWidth: Int = 0,
    @ColorInt strokeColor: Int? = null
): GradientDrawable {
    var cornerRadiiList: FloatArray? = null
    if (topLeftRadius.isNullOrZero().not() || topRightRadius.isNullOrZero().not()
        || bottomRightRadius.isNullOrZero().not() || bottomLeftRadius.isNullOrZero().not()) {
        cornerRadiiList = floatArrayOf(topLeftRadius,topLeftRadius,topRightRadius,topRightRadius,bottomRightRadius,bottomRightRadius,bottomLeftRadius,bottomLeftRadius)
    }
    return GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(bgColor)
        if (radius.isNullOrZero().not() && cornerRadiiList == null) cornerRadius = radius
        if (cornerRadiiList != null) cornerRadii = cornerRadiiList
        if (strokeColor != null && strokeWidth > 0) setStroke(strokeWidth, strokeColor)
    }.also { targetView.background = it }
}
