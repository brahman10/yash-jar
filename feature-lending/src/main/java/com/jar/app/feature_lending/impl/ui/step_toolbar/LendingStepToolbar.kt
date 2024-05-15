package com.jar.app.feature_lending.impl.ui.step_toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingProgressiveStepBinding
import com.jar.app.feature_lending.databinding.FeatureLendingStepToolbarBinding
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepStatus
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep

internal class LendingStepToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: FeatureLendingStepToolbarBinding
    private val mSteps = LinkedHashMap<LendingProgressStep, FeatureLendingProgressiveStepBinding>()

    private var currentPosition = 1

    private var onNeedHelpButtonClickListener: ((View) -> Unit)? = null
    private var onBackButtonClickListener: ((View) -> Unit)? = null

    init {
        binding = FeatureLendingStepToolbarBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        binding.toolbarLayout.separator.isVisible = false
        binding.toolbarLayout.separator.setBackgroundColor(
            ContextCompat.getColor(context, com.jar.app.core_ui.R.color.purple400)
        )
        binding.root.setBackgroundColor(
            ContextCompat.getColor(context, com.jar.app.core_ui.R.color.bgColor)
        )
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.toolbarLayout.btnNeedHelp.setDebounceClickListener {
            onNeedHelpButtonClickListener?.invoke(it)
        }

        binding.toolbarLayout.btnBack.setDebounceClickListener {
            onBackButtonClickListener?.invoke(it)
        }
    }

    private fun getStepViewBinding() =
        FeatureLendingProgressiveStepBinding.inflate(LayoutInflater.from(context))

    private fun inflateSteps() {
        clearAllSteps(true)
        mSteps.forEach {
            renderStep(it.key, it.value)
        }
        invalidate()
    }

    private fun renderStep(
        lendingProgressStep: LendingProgressStep,
        stepBinding: FeatureLendingProgressiveStepBinding
    ) {
        stepBinding.ivTickIcon.isVisible = false
        val isInProgress = lendingProgressStep.status == LendingStepStatus.IN_PROGRESS
        val isCompleted = lendingProgressStep.status == LendingStepStatus.COMPLETED
        stepBinding.tvStepNumber.setTextColor(
            ContextCompat.getColor(
                context,
                if (isCompleted)
                    com.jar.app.core_ui.R.color.purple400
                else if (isInProgress)
                    com.jar.app.core_ui.R.color.color_D5CDF2
                else com.jar.app.core_ui.R.color.white_30
            )
        )
        stepBinding.markContainer.background = ContextCompat.getDrawable(
            context,
            if (isCompleted)
                R.drawable.feature_lending_bg_solid_d5cdf2_mark
            else if (isInProgress)
                R.drawable.feature_lending_bg_d5cdf2_ring
            else
                R.drawable.feature_lending_bg_solid_mark
        )
        stepBinding.rightLine.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (isCompleted) com.jar.app.core_ui.R.color.color_D5CDF2
                else if (isInProgress) com.jar.app.core_ui.R.color.purple400
                else com.jar.app.core_ui.R.color.purple400
            )
        )
        stepBinding.leftLine.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (isCompleted) com.jar.app.core_ui.R.color.color_D5CDF2
                else if (isInProgress) com.jar.app.core_ui.R.color.color_D5CDF2
                else com.jar.app.core_ui.R.color.purple400
            )
        )
        stepBinding.tvStepNumber.text = "${lendingProgressStep.position}"
        stepBinding.tvStepNumber.isVisible = true
        stepBinding.leftLine.isInvisible = lendingProgressStep.position == 1
        stepBinding.rightLine.isInvisible = lendingProgressStep.position == mSteps.size
        stepBinding.tvStepTitle.text = context.getString(lendingProgressStep.titleResId.resourceId)

        val params = LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1f)
        stepBinding.root.layoutParams = params
        binding.stepsHolder.addView(stepBinding.root)
    }

    fun setNeedHelpButtonClickListener(onNeedHelpButtonClickListener: (View) -> Unit) {
        this.onNeedHelpButtonClickListener = onNeedHelpButtonClickListener
    }

    fun setBackButtonClickListener(onBackButtonClickListener: (View) -> Unit) {
        this.onBackButtonClickListener = onBackButtonClickListener
    }

    fun setTitle(title: String) {
        binding.toolbarLayout.tvTitle.text = title
    }

    fun shouldShowToolbar(shouldShowToolbar:Boolean){
        binding.toolbarLayout.root.isVisible = shouldShowToolbar

    }
    fun addStep(lendingProgressStep: LendingProgressStep) {
        mSteps[lendingProgressStep] = getStepViewBinding()
        inflateSteps()
    }

    fun addSteps(steps: List<LendingProgressStep>) {
        clearAllSteps()
        steps.forEach {
            mSteps[it] = getStepViewBinding()
        }
        inflateSteps()
    }

    fun clearAllSteps(softClearOnly: Boolean = false) {
        binding.stepsHolder.removeAllViews()
        if (!softClearOnly) mSteps.clear()
        invalidate()
    }

    fun setStepCurrentPosition(position: Int) {
        this.currentPosition = position
        inflateSteps()
    }
}