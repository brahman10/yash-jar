package com.jar.app.feature_lending_kyc.impl.ui.custom_step_toolbar

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycProgressiveStepViewBinding
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycStepToolbarViewBinding
import com.jar.app.feature_lending_kyc.impl.data.KycStep
import com.jar.app.feature_lending_kyc.impl.data.KycStepStatus

class KycStepToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: FeatureLendingKycStepToolbarViewBinding
    private val mSteps = LinkedHashMap<KycStep, FeatureLendingKycProgressiveStepViewBinding>()

    var currentStep: KycStep? = null
    private var currentPosition = 0
    private var renderedSteps = ArrayList<KycStep>()

    private var onCloseButtonClickListener: ((View) -> Unit)? = null
    private var onBackButtonClickListener: ((View) -> Unit)? = null

    init {
        binding = FeatureLendingKycStepToolbarViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        binding.toolbarLayout.separator.isVisible = true
        binding.toolbarLayout.separator.setBackgroundColor(
            ContextCompat.getColor(context, com.jar.app.core_ui.R.color.purple400)
        )
        binding.toolbarLayout.ivEndImage.isVisible = true
        binding.toolbarLayout.ivEndImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        binding.toolbarLayout.ivEndImage.setImageResource(com.jar.app.core_ui.R.drawable.ic_close)
        binding.root.setBackgroundColor(
            ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_322C48)
        )
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.toolbarLayout.ivEndImage.setDebounceClickListener {
            onCloseButtonClickListener?.invoke(it)
        }

        binding.toolbarLayout.btnBack.setDebounceClickListener {
            onBackButtonClickListener?.invoke(it)
        }
    }

    private fun getStepViewBinding() =
        FeatureLendingKycProgressiveStepViewBinding.inflate(LayoutInflater.from(context))

    private fun inflateSteps() {
        clearAllSteps(true)
        renderedSteps.clear()
        mSteps.forEach {
            if (it.key.position >= currentPosition) {
                renderedSteps.add(it.key)
                renderStep(it.key, it.value)
            }
        }
        currentStep?.let {
            setTitle(it.text)
        }
        binding.startView.isInvisible = !shouldShowStartLine()
        invalidate()
    }

    private fun renderStep(
        kycStep: KycStep,
        stepBinding: FeatureLendingKycProgressiveStepViewBinding
    ) {
        stepBinding.tvStepNumber.text = "${kycStep.position}"
        stepBinding.tvStepNumber.setTextColor(
            ContextCompat.getColor(
                context,
                if (kycStep.isPending()) com.jar.app.core_ui.R.color.color_776E94 else com.jar.app.core_ui.R.color.white
            )
        )
        stepBinding.markContainer.background = ContextCompat.getDrawable(
            context,
            if (kycStep.isPending())
                R.drawable.feature_lending_kyc_bg_solid_mark
            else
                R.drawable.feature_lending_kyc_bg_white_ring
        )
        stepBinding.ivTickIcon.isVisible = kycStep.status == KycStepStatus.COMPLETED
        stepBinding.tvStepNumber.isVisible = kycStep.status != KycStepStatus.COMPLETED
        stepBinding.progressBar.isGone = mSteps.size == kycStep.position
        stepBinding.progressBar.progress = if (kycStep.status == KycStepStatus.COMPLETED)
            kycStep.stepProgress else 0
        binding.stepsHolder.addView(stepBinding.root)
    }

    fun setCloseButtonClickListener(onCloseButtonClickListener: (View) -> Unit) {
        this.onCloseButtonClickListener = onCloseButtonClickListener
    }

    fun setBackButtonClickListener(onBackButtonClickListener: (View) -> Unit) {
        this.onBackButtonClickListener = onBackButtonClickListener
    }

    fun setTitle(title: String) {
        binding.toolbarLayout.tvTitle.text = title
    }

    fun addStep(kycStep: KycStep) {
        mSteps[kycStep] = getStepViewBinding()
        inflateSteps()
    }

    fun addSteps(steps: List<KycStep>) {
        clearAllSteps()
        steps.forEach {
            mSteps[it] = getStepViewBinding()
        }
        inflateSteps()
    }

    fun clearAllSteps(softClearOnly: Boolean = false) {
        binding.stepsHolder.removeAllViews()
        if (!softClearOnly)
            mSteps.clear()
        postInvalidate()
    }

    fun showToolbarSeparator(shouldShow: Boolean) {
        binding.toolbarLayout.separator.isVisible = shouldShow
    }

    private fun shouldShowStartLine(): Boolean {
        return renderedSteps.all { it.position > 1 }
    }

    fun setStepCurrentPosition(position: Int) {
        this.currentPosition = position
        inflateSteps()
    }
}