package com.jar.app.feature_lending.impl.ui.common.custom_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.CellAnimatedProgressCheckpointsBinding
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashProgressBar
import kotlinx.parcelize.Parcelize

internal class AnimatedCheckpointProgressView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attr, defStyleAttr) {
    private val binding: CellAnimatedProgressCheckpointsBinding

    private var currentStep = 0

    init {
        binding = CellAnimatedProgressCheckpointsBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )
        isSaveEnabled = true
        addView(binding.root)
    }

    override fun onSaveInstanceState(): Parcelable {
        return SaveState(currentStep, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SaveState) {
            currentStep = state.currentStep
            makeAsAnimated()
        }
    }

    fun startAnimation() {
        animateCheckpoint(currentStep)
    }

    fun changeSteps(steps : List<ReadyCashProgressBar>){
        steps.forEachIndexed { index, readyProgressBar ->
            when(index){
                0 ->{
                    binding.tvStepOne.visibility = VISIBLE
                    binding.tvStepOne.text = readyProgressBar.stepName.replace(" ","\n")
                    binding.stepOneContainer.visibility = VISIBLE

                }
                1 ->{
                    binding.tvStepTwo.visibility = VISIBLE
                    binding.tvStepTwo.text = readyProgressBar.stepName.replace(" ","\n")
                    binding.stepTwoContainer.visibility = VISIBLE
                    binding.progressBarTwo.visibility = VISIBLE
                    binding.progressBarOne.visibility = VISIBLE


                }
                2 ->{
                    binding.tvStepThree.visibility = VISIBLE
                    binding.tvStepThree.text = readyProgressBar.stepName.replace(" ","\n")
                    binding.stepThreeContainer.visibility = VISIBLE
                    binding.progressBarTwo.visibility = VISIBLE

                }
                3 ->{
                    binding.tvStepFour.visibility = VISIBLE
                    binding.tvStepFour.text = readyProgressBar.stepName.replace(" ","\n")
                    binding.stepFourContainer.visibility = VISIBLE
                    binding.progressBarThree.visibility = VISIBLE


                }

            }
        }

    }

    private fun animateCheckpoint(step: Int) {
        when (step) {
            0 -> {
                animateStep(
                    binding.stepOneContainer,
                    binding.tvStepNumberOne,
                    binding.tvStepOne,
                    binding.progressBarOne
                )
            }
            1 -> {
                animateStep(
                    binding.stepTwoContainer,
                    binding.tvStepNumberTwo,
                    binding.tvStepTwo,
                    binding.progressBarTwo
                )
            }
            2 -> {
                animateStep(
                    binding.stepThreeContainer,
                    binding.tvStepNumberThree,
                    binding.tvStepThree,
                    binding.progressBarThree
                )
            }
            3 -> {
                animateStep(
                    binding.stepFourContainer,
                    binding.tvStepNumberFour,
                    binding.tvStepFour,
                    null
                )
            }
            else -> {
                //just ignore
            }

        }
    }

    private fun animateStep(
        stepView: FrameLayout,
        numberTextView: AppCompatTextView,
        stepTitleTextView: AppCompatTextView,
        progressBar: ProgressBar?
    ) {
        stepView.background = ContextCompat.getDrawable(
            context,
            R.drawable.feature_lending_bg_animated_step_container_selected
        )
        stepTitleTextView.alpha = 1f
        numberTextView.setTextColor(Color.WHITE)
        progressBar?.let {
            val valueAnimator = ValueAnimator.ofInt(0, 100)
            valueAnimator.duration = 1000L
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                if (value == 100) {
                    animateCheckpoint(++currentStep)
                    it.progress = value
                } else {
                    it.progress = value
                }
            }
            valueAnimator.start()
        }

    }

    private fun makeAsAnimated() {
        if (currentStep > 0)
            makeCompletedState(
                binding.stepOneContainer,
                binding.tvStepNumberOne,
                binding.tvStepOne,
                binding.progressBarOne
            )
        if (currentStep > 1)
            makeCompletedState(
                binding.stepTwoContainer,
                binding.tvStepNumberTwo,
                binding.tvStepTwo,
                binding.progressBarTwo
            )
        if (currentStep > 2)
            makeCompletedState(
                binding.stepThreeContainer,
                binding.tvStepNumberThree,
                binding.tvStepThree,
                binding.progressBarThree
            )
        if (currentStep > 3)
            makeCompletedState(
                binding.stepFourContainer,
                binding.tvStepNumberFour,
                binding.tvStepFour,
                null
            )
        animateCheckpoint(currentStep)
    }

    private fun makeCompletedState(
        stepView: FrameLayout,
        numberTextView: AppCompatTextView,
        stepTitleTextView: AppCompatTextView,
        progressBar: ProgressBar?
    ) {
        stepView.background = ContextCompat.getDrawable(
            context,
            R.drawable.feature_lending_bg_animated_step_container_selected
        )
        stepTitleTextView.alpha = 1f
        numberTextView.setTextColor(Color.WHITE)
        progressBar?.progress = 100
    }

    //data class to hold state of this view
    @Parcelize
    data class SaveState(
        val currentStep: Int,
        val parentState: Parcelable?
    ) : BaseSavedState(parentState), Parcelable
}