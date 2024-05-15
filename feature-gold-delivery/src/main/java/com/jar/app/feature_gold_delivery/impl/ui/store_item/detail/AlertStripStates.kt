package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail

import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jar.app.feature_gold_delivery.R

enum class AlertStripStates(
    val title: String,
    @DrawableRes val backgroundColor: Int,
    @DrawableRes val drawableRes: Int,
    @ColorRes val shadowColor: Int,
) {
    BEST_SELLER(
        "BEST SELLER",
        com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_7745ff_8dp,
        R.drawable.feature_gold_delivery_ic_fire,
        com.jar.app.core_ui.R.color.color_4d7745ff
    ),
    SELLING_FAST(
        "SELLING FAST",
        com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_1ea787_8dp,
        R.drawable.feature_gold_delivery_ic_lightning,
        com.jar.app.core_ui.R.color.color_4d1ea787
    ),
    NEW("NEW", com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_547be1_8dp, R.drawable.feature_gold_delivery_ic_star,
    com.jar.app.core_ui.R.color.color_4d547be1),
}

public fun renderAlertStrip(
    whichState: String? = null,
    productLabelBg: View,
    productLabelIv: ImageView,
    productLabelTv: AppCompatTextView,
    productLabelBgShadow: View
) {
    AlertStripStates.values().find { it.title.equals(whichState, ignoreCase = true) }?.let {
        productLabelBg.setBackgroundResource(it.backgroundColor)
        productLabelIv.setImageResource(it.drawableRes)
        productLabelTv.text = whichState
        productLabelBgShadow.backgroundTintList = ContextCompat.getColorStateList(productLabelBgShadow.context, it.shadowColor)

        productLabelBg.isVisible = true
        productLabelIv.isVisible = true
        productLabelTv.isVisible = true
        productLabelBgShadow.isVisible = true
    } ?: run {
        productLabelBg.isVisible = false
        productLabelIv.isVisible = false
        productLabelTv.isVisible = false
        productLabelBgShadow.isVisible = false
    }
}