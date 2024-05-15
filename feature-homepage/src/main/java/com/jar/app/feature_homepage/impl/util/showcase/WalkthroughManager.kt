package com.jar.app.feature_homepage.impl.util.showcase

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.animateViewWithFadeInAnimation
import com.jar.app.core_ui.extension.animateViewWithFadeOutAnimation
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageLayoutWalkthroughOverlayBinding

class WalkthroughManager(
    private val context: Context,
    private val onNextClicked: () -> Unit,
    private val onSkipClicked: (Boolean) -> Unit,
    private val onCustomWalkThroughAnimationStart: (Boolean, Long) -> Unit,
) {

    private val activity = (context as Activity)
    private var isFinalSuccessScreen = false
    private val binding: FeatureHomepageLayoutWalkthroughOverlayBinding
    private val parent: ViewGroup = activity.window.decorView.findViewById(android.R.id.content)
    private val addAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloatOrZero() * 100).toInt()
        if (progress == 100) {
            onSkipClicked.invoke(false)
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 700L
        private const val HIDE_ANIMATION_DURATION = 400L
    }

    init {
        binding =
            FeatureHomepageLayoutWalkthroughOverlayBinding.inflate(
                LayoutInflater.from(context),
                parent,
                true
            )
        setupListener()
    }

    //To show walkthrough initiation welcome screen
    fun showWalkthroughWelcomeScreen(header: String, title: String, footer: String) {
        onCustomWalkThroughAnimationStart.invoke(true, ANIMATION_DURATION)
        binding.clStatesContainer.animateViewWithFadeInAnimation(ANIMATION_DURATION) {
            binding.tvIntroHeader.text = header
            binding.tvIntroTitle.text = title
            binding.tvTapToProceed.text = footer
            binding.pulsatingLottie.isVisible = true
            binding.pulsatingLottie.playLottieWithUrlAndExceptionHandling(
                context, BaseConstants.LottieUrls.PULSATING_LOTTIE
            )
        }
    }

    //To initiate walkthrough flow which will custom draw the views
    fun showWalkthrough(
        shouldHideWelcomeScreen: Boolean = false,
        shouldShowSkipButton: Boolean = false,
        header: String?,
        title: String,
        targetView: List<View>
    ) {
        onCustomWalkThroughAnimationStart.invoke(true, ANIMATION_DURATION)
        binding.skip.isInvisible = shouldShowSkipButton.not()
        if (shouldHideWelcomeScreen) {
            binding.clStatesContainer.animateViewWithFadeOutAnimation(ANIMATION_DURATION) {
                // Show the ShowCaseView with animation
                binding.showCaseView.animateViewWithFadeInAnimation(ANIMATION_DURATION) {
                    // Set the label text for the TapTargetView
                    binding.showCaseView.setOverlayContent(header, title, targetView)
                }
                if (binding.skip.isInvisible.not())
                    binding.skip.animateViewWithFadeInAnimation(ANIMATION_DURATION) {}
            }
        } else {
            binding.showCaseView.animateViewWithFadeInAnimation(ANIMATION_DURATION) {
                // Show the ShowCaseView with animation
                binding.showCaseView.setOverlayContent(header, title, targetView)
            }
            if (binding.skip.isInvisible.not())
                binding.skip.animateViewWithFadeInAnimation(ANIMATION_DURATION) {}
        }
    }

    //To show walkthrough flow for bottom tab
    fun showWalkthroughForBottomTab(title: String, targetView: List<View>) {
        // Show the ShowCaseView with animation
        onCustomWalkThroughAnimationStart.invoke(true, ANIMATION_DURATION)
        binding.showCaseView.animateViewWithFadeInAnimation(ANIMATION_DURATION) {
            binding.showCaseView.setOverlayContent(
                null,
                title,
                targetView,
                focusShape = FocusShape.RECTANGLE
            )
        }
        if (binding.skip.isInvisible.not())
            binding.skip.animateViewWithFadeInAnimation(ANIMATION_DURATION) {}
    }


    //To show walkthrough completion screen
    fun showWalkthroughCompletedScreen(header: String, title: String) {
        isFinalSuccessScreen = true
        onCustomWalkThroughAnimationStart.invoke(true, ANIMATION_DURATION)
        binding.clStatesContainer.animateViewWithFadeInAnimation(ANIMATION_DURATION) {
            binding.skip.isInvisible = true
            binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
                context, BaseConstants.LottieUrls.CONFETTI_FROM_TOP
            )
            binding.pulsatingLottie.isVisible = false
            binding.tvTapToProceed.isVisible = false
            binding.lottieCelebration.addAnimatorUpdateListener(addAnimatorUpdateListener)
            binding.tvIntroHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            binding.tvIntroHeader.text = header
            binding.tvIntroTitle.setTextColor(
                ContextCompat.getColorStateList(context, com.jar.app.core_ui.R.color.white)
            )
            binding.tvIntroTitle.text = title
        }
    }

    // Hide the ShowCaseView with animation
    private fun hideWalkthrough(isNextClicked: Boolean) {
        binding.showCaseView.clearTargetList()
        onCustomWalkThroughAnimationStart.invoke(false, HIDE_ANIMATION_DURATION)
        binding.showCaseView.animateViewWithFadeOutAnimation(HIDE_ANIMATION_DURATION) {
            if (isNextClicked) onNextClicked.invoke() else onSkipClicked.invoke(true)
            parent.removeView(binding.showCaseView)
            parent.removeView(binding.skip)
        }
        binding.skip.animateViewWithFadeOutAnimation(ANIMATION_DURATION) {}
    }

    private fun setupListener() {
        binding.showCaseView.setDebounceClickListener(2000L) {
            hideWalkthrough(isNextClicked = true)
        }

        binding.tvTapToProceed.setDebounceClickListener (2000L){
            hideWalkthrough(isNextClicked = true)
        }

        binding.skip.setDebounceClickListener(2000L) {
            hideWalkthrough(isNextClicked = false)
        }

        binding.clStatesContainer.setDebounceClickListener(2000L) {
            if (isFinalSuccessScreen.not()) {
                hideWalkthrough(isNextClicked = true)
            }
        }
    }

    fun dismissWalkthrough() {
        onCustomWalkThroughAnimationStart.invoke(false, ANIMATION_DURATION)
        binding.root.animateViewWithFadeOutAnimation(200) {
            parent.removeView(binding.root)
        }
    }

}
