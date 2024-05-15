package com.jar.app.core_ui.label_and_value

import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import com.jar.app.core_ui.R

data class LabelAndValue(
    val label: String,
    val value: String,
    @ColorRes val labelColorRes: Int = R.color.white,
    @ColorRes val valueColorRes: Int = R.color.white,
    @ColorRes val backgroundColorRes: Int = R.color.transparent,
    val isTextualValue: Boolean = true,
    @StyleRes val labelTextStyle: Int = R.style.CommonBoldTextViewStyle,
    val showCopyToClipBoardIcon: Boolean = false,
    @StyleRes val valueTextStyle: Int = R.style.CommonBoldTextViewStyle,
    val valueColorString: String? = null,
)