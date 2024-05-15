package com.jar.app.weekly_magic_common.impl.ui.onboarding

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.app.weekly_magic_common.databinding.FragmentOnboardingWeeklyChallengeBinding
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.collections.set


@AndroidEntryPoint
internal class OnBoardingWeeklyChallengeFragment :
    BaseFragment<FragmentOnboardingWeeklyChallengeBinding>() {

    private var isPaused = false

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<OnBoardingWeeklyChallengeViewModelAndroid> {
        defaultViewModelProviderFactory
    }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<OnBoardingWeeklyChallengeFragmentArgs>()

    private var currentPosition = -1

    private val spannableColor by lazy {
        ForegroundColorSpan(whiteColor)
    }

    private val whiteColor by lazy {
        ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)
    }

    private lateinit var animator: Animator
    private var curtainsRaisedOnce = false
    private var colorAnimation: ValueAnimator? = null
    private var counterJob: Job? = null

    private val curtainAnimationListener: Animator.AnimatorListener =
        object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                if (currentPosition == 0) {
                    binding.animViewCurtain.isVisible = false
                    animateScreenOneTexts()
                } else if (currentPosition >= 2
                    && !binding.animViewCurtain.isAnimating
                    && counterJob?.isCompleted == true
                ) {
                    finishOnBoarding()
                }
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        }

    private var startTimeStamp = 0L

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = OnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startTimeStamp = System.currentTimeMillis()
                binding.animViewCurtain.pauseAnimation()
                binding.animViewMain.pauseAnimation()
                isPaused = true
                if (::animator.isInitialized)
                    animator.pause()
                colorAnimation?.pause()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                binding.animViewCurtain.resumeAnimation()
                binding.animViewMain.resumeAnimation()
                isPaused = false
                if (::animator.isInitialized)
                    animator.resume()
                colorAnimation?.resume()
                return@OnTouchListener (System.currentTimeMillis() - startTimeStamp > 1000L)
            }
        }
        return@OnTouchListener false
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOnboardingWeeklyChallengeBinding
        get() = FragmentOnboardingWeeklyChallengeBinding::inflate

    override fun setupAppBar() =
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        registerEvent()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
    }

    private fun registerEvent() {
        analyticsHandler.postEvent(
            WeeklyMagicConstants.AnalyticsKeys.Shown_OnboardingStory,
            mapOf(
                EventKey.FromScreen to args.fromScreen
            )
        )
    }

    private fun setupUI() {
        binding.animViewCurtain.addAnimatorListener(curtainAnimationListener)
        viewModel.markWeeklyChallengeOnBoardingCompleted()
        moveToNextScreen()
    }

    private fun moveToNextScreen() {
        currentPosition += 1
        uiScope.launch {
            binding.tvMessage.text = ""
            binding.tvMessage.isVisible = false
            when (currentPosition) {
                0 -> {
                    curtainsRaisedOnce = false
                    binding.btnSkip.text = getCustomString(MR.strings.feature_weekly_magic_common_next)
                    binding.progressBarTwo.progress = 0
                    binding.progressBarThree.progress = 0
                    loadProgressBar(binding.progressBarOne, 8)
                    runScreenOne()
                }
                1 -> {
                    curtainsRaisedOnce = true
                    binding.animViewCurtain.isVisible = false
                    binding.btnSkip.text = getCustomString(MR.strings.feature_weekly_magic_common_next)
                    binding.progressBarOne.progress = 100
                    binding.progressBarThree.progress = 0
                    loadProgressBar(binding.progressBarTwo, 7)
                    runScreenTwo()
                }
                2 -> {
                    curtainsRaisedOnce = true
                    binding.btnSkip.text = getCustomString(MR.strings.feature_weekly_magic_common_close)
                    binding.progressBarOne.progress = 100
                    binding.progressBarTwo.progress = 100
                    loadProgressBar(binding.progressBarThree, 7)
                    runScreenThree()
                }
                else -> {
                    currentPosition -= 1
                }
            }
        }
    }

    private fun moveToPreviousScreen() {
        binding.animViewMain.cancelAnimation()
        binding.animViewCurtain.cancelAnimation()
        checkAndCancelAnimator()
        checkAndCancelJob()
        if (currentPosition > 0)
            currentPosition -= 2
        else {
            currentPosition = -1
        }
        moveToNextScreen()
    }

    private fun loadProgressBar(progressBar: LinearProgressIndicator, duration: Long) {
        checkAndCancelAnimator()
        animator = ValueAnimator.ofInt(0, progressBar.max)
        animator.duration = duration * 1000L
        (animator as ValueAnimator?)?.addUpdateListener {
            progressBar.setProgressCompat(
                (it.animatedValue as Int?) ?: 0,
                true
            )
        }
        animator.start()
    }

    private fun runScreenOne() {
        binding.animViewMain.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.WEEKLY_CHALLENGE_ONBOARDING1
        )
        raiseTheCurtains()
    }

    private fun raiseTheCurtains() {
        if (curtainsRaisedOnce) {
            return
        }
        curtainsRaisedOnce = true
        binding.animViewCurtain.isVisible = true
        binding.animViewCurtain.setAnimation("on_boarding_curtain_opening.lottie")
        binding.animViewCurtain.playAnimation()
    }

    private fun animateScreenOneTexts() {
        animateTextLine(
            getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_one_line_one)
        ) {
            animateTextLine(
                getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_one_line_two)
            ) {
                animateTextLine(getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_one_line_three)) {
                    moveToNextScreen()
                }
            }
        }
    }

    private fun runScreenTwo() {
        binding.animViewMain.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.WEEKLY_CHALLENGE_ONBOARDING2
        )
        animateScreenTwoTexts()
    }

    private fun animateScreenTwoTexts() {
        animateTextLine(
            getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_two_line_one)
        ) {
            animateTextLine(
                getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_two_line_two)
            ) {
                moveToNextScreen()
            }
        }
    }

    private fun runScreenThree() {
        binding.animViewMain.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.WEEKLY_CHALLENGE_ONBOARDING3
        )
        animateScreenThreeTexts()
    }

    private fun animateScreenThreeTexts() {
        animateTextLine(
            getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_three_line_one)
        ) {
            animateTextLine(
                getCustomString(MR.strings.feature_weekly_magic_common_on_boarding_screen_three_line_two)
            ) {
                dropTheCurtains()
            }
        }
    }

    private fun dropTheCurtains() {
        binding.animViewCurtain.isVisible = true
        binding.animViewCurtain.setAnimation("on_boarding_curtain_closing.lottie")
        binding.animViewCurtain.playAnimation()
    }

    private fun finishOnBoarding(delay: Boolean = true) {
        uiScope.launch {
            if (delay) {
                delay(500)
            }
            if (args.triggerReturnResult) {
                findNavController().currentBackStackEntry?.savedStateHandle
                    ?.set(
                        WeeklyMagicConstants.ON_BOARDING_ANIMATION_FINISHED,
                        !((findNavController().currentBackStackEntry?.savedStateHandle?.get(
                            WeeklyMagicConstants.ON_BOARDING_ANIMATION_FINISHED
                        ) as Boolean?)
                            ?: false)
                    )
            }
            popBackStack()
        }
    }

    private fun animateTextLine(text: String, onFinished: () -> Unit) {
        uiScope.launch {
            delay(200L)
            binding.tvMessage.text = ""
            binding.tvMessage.isVisible = true
            val duration = 200L
            val words = text.split(" ")
            var actualEndPosition = 0
            val hashMap = HashMap<Int, Int>()
            words.forEachIndexed { index, _ ->
                actualEndPosition += words[index].length + if (index != 0) 1 else 0
                hashMap[index] = actualEndPosition
            }

            var spannableText = SpannableString(text)
            var position = 0
            checkAndCancelJob()
            binding.tvMessage.text = ""
            counterJob = uiScope.countDownTimer(words.size * duration, duration, {
                colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), 135, 165, 195, 225, 255)
                colorAnimation?.duration = duration
                colorAnimation?.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (counterJob?.isCancelled == true) {
                            uiScope.launch {
                                binding.tvMessage.text = ""
                            }
                        } else {
                            uiScope.launch {
                                binding.tvMessage.text = spannableText
                                spannableText = SpannableString(binding.tvMessage.text.toString())
                            }
                        }
                    }
                })
                colorAnimation?.start()
                position += 1
            }, {
                onFinished.invoke()
            }, { isPaused })
            counterJob?.start()
        }
    }

    private fun setupListener() {
        binding.btnSkip.setDebounceClickListener {
            when (currentPosition) {
                0 -> {
                    moveToNextScreen()
                }
                1 -> {
                    moveToNextScreen()
                }
                2 -> {
                    finishOnBoarding(false)
                }
            }
        }
        binding.containerLeft.setDebounceClickListener {
            moveToPreviousScreen()
        }
        binding.containerRight.setDebounceClickListener {
            moveToNextScreen()
        }
        binding.containerCenter.setOnTouchListener(touchListener)
        binding.containerLeft.setOnTouchListener(touchListener)
        binding.containerRight.setOnTouchListener(touchListener)
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeOnBoardingCompletedFlow.collect(
                    onSuccess = {
                        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
                    },
                    onSuccessWithNullData = {
                        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun checkAndCancelAnimator() {
        if (::animator.isInitialized && animator.isRunning) {
            animator.cancel()
        }
    }

    private fun checkAndCancelJob() {
        if (counterJob?.isActive == true || isPaused) {
            counterJob?.cancel()
        }
        counterJob = null
        if (colorAnimation?.isRunning == true || isPaused) {
            colorAnimation?.cancel()
        }
        colorAnimation = null
    }

    override fun onDestroyView() {
        checkAndCancelAnimator()
        checkAndCancelJob()
        binding.animViewMain.addAnimatorListener(null)
        binding.animViewCurtain.addAnimatorListener(null)
        analyticsHandler.postEvent(WeeklyMagicConstants.AnalyticsKeys.Completed_WeeklyChallengeOnBoarding)
        super.onDestroyView()
    }

}