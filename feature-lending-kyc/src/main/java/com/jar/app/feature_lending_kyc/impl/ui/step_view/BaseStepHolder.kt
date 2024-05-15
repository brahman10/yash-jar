package com.jar.app.feature_lending_kyc.impl.ui.step_view

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.impl.data.KycStep
import com.jar.app.feature_lending_kyc.impl.data.KycStepStatus

internal abstract class BaseStepHolder(binding: ViewBinding) : BaseViewHolder(binding.root) {

    abstract fun bindStep(step: KycStep)

    protected fun getMarkerContainerBackground(step: KycStep): Drawable? {
        return ContextCompat.getDrawable(
            context,
            if (step.status == KycStepStatus.NOT_YET_VISITED)
                R.drawable.feature_lending_kyc_bg_solid_mark
            else
                R.drawable.feature_lending_kyc_bg_white_ring
        )
    }

    protected fun shouldHideBeforeLine() = bindingAdapterPosition == 0

    protected fun shouldHideAfterLine(stepSize: Int) = bindingAdapterPosition == stepSize - 1

    protected fun getStepNumberTextColor(step: KycStep): Int {
        return ContextCompat.getColor(
            context,
            if (step.status == KycStepStatus.NOT_YET_VISITED) com.jar.app.core_ui.R.color.color_776E94 else com.jar.app.core_ui.R.color.white
        )
    }

    protected fun shouldShowTickIcon(step: KycStep) = step.status == KycStepStatus.COMPLETED

    protected fun shouldShowStepNumberText(step: KycStep) = step.status != KycStepStatus.COMPLETED

    protected fun getBeforeLineBackgroundColor(step: KycStep): Int {
        return ContextCompat.getColor(
            context,
            if (step.status == KycStepStatus.NOT_YET_VISITED) com.jar.app.core_ui.R.color.color_3C3357 else com.jar.app.core_ui.R.color.white
        )
    }

    protected fun getAfterLineBackgroundColor(step: KycStep): Int {
        return ContextCompat.getColor(
            context,
            if (step.status == KycStepStatus.COMPLETED || step.status == KycStepStatus.FAILURE) com.jar.app.core_ui.R.color.white else com.jar.app.core_ui.R.color.color_3C3357
        )
    }
}