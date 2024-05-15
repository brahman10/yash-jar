package com.jar.app.feature_lending.impl.ui.mandate.custom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FeatureLendingMandatePaymentModeButtonLayoutBinding

internal class MandateModeSetupButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: FeatureLendingMandatePaymentModeButtonLayoutBinding

    // 0-next type (next arrow at end),  1- selection (radio button at end)
    private var buttonModeType = 0
    private var isDisabled = false
    init {
        removeAllViews()
        binding = FeatureLendingMandatePaymentModeButtonLayoutBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        context.theme.obtainStyledAttributes(
            attrs,
            com.jar.app.feature_lending.R.styleable.MandateModeSetupButtonView,
            defStyleAttr,
            0
        ).use {
            extractValues(it)
        }
    }

    private fun extractValues(typedArray: TypedArray) {
        typedArray.getString(
            com.jar.app.feature_lending.R.styleable.MandateModeSetupButtonView_modeButton_title)
            ?.let {
                setTitle(it)
            }
        typedArray.getDrawable(com.jar.app.feature_lending.R.styleable.MandateModeSetupButtonView_modeButton_startIcon)?.let {
            setStartIconDrawable(it)
        }
        buttonModeType = typedArray.getInteger(com.jar.app.feature_lending.R.styleable.MandateModeSetupButtonView_modeButton_type,buttonModeType)
        val isSelected = typedArray.getBoolean(com.jar.app.feature_lending.R.styleable.MandateModeSetupButtonView_modeButton_isSelected,false)
        val isQuickerApproval = typedArray.getBoolean(com.jar.app.feature_lending.R.styleable.MandateModeSetupButtonView_modeButton_isQuickerApproval,false)
        setButtonType(buttonModeType)
        setButtonSelected(isSelected)
        shouldShowQuickApprovalTag(isQuickerApproval)

    }

    fun setOnButtonClickListener(onClickListener: (view: View) -> Unit) {
        binding.root.setDebounceClickListener {
            if (!isDisabled) onClickListener(it)
        }
    }

    /**
     * set the button type to either Next or Selection
     * buttonType - 0 for next,
     * 1- for selection
     */
    fun setButtonType(buttonType:Int){
        val icon = if (buttonType == 0)com.jar.app.feature_lending.R.drawable.feature_lending_ic_right_chevron_padded
            else com.jar.app.feature_lending.R.drawable.feature_lending_ic_radio_unselected
        Glide.with(context).load(icon).into(binding.ivEndIcon)
        postInvalidate()
    }

    fun setButtonSelected(isSelected:Boolean){
        if (buttonModeType == 0) return // if button is nextType ignore selection
        val icon = if (isSelected) com.jar.app.feature_lending.R.drawable.feature_lending_ic_radio_selected_v2
        else com.jar.app.feature_lending.R.drawable.feature_lending_ic_radio_unselected
        Glide.with(context).load(icon).into(binding.ivEndIcon)
        binding.clRow.isSelected = isSelected
        postInvalidate()
    }


    fun setStartIcon(url: String) {
        Glide.with(context).load(url).into(binding.ivStartIcon)
    }
    fun setStartIconDrawable(drawable: Drawable) {
        Glide.with(context).load(drawable).into(binding.ivStartIcon)
    }

    fun setStartIconResource(@DrawableRes icon: Int) {
        Glide.with(context).load(icon).into(binding.ivStartIcon)
    }

    fun setTitle(title: String) {
        binding.tvPaymentModeTitle.text = title
    }

    fun shouldShowQuickApprovalTag(shouldShow: Boolean) {
        binding.tvQuickerApproval.isVisible = shouldShow
    }

    fun setDisabled(isDisabled:Boolean){
        this.isDisabled = isDisabled
        binding.root.alpha = if (isDisabled) 0.3f else 1.0f
    }
}