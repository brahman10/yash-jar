package com.jar.app.core_ui.explanatory_video

import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.domain.model.card_library.InfographicType
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CoreUiFragmentExplanatoryVideoBinding
import com.jar.app.core_ui.explanatory_video.model.ExplanatoryVideoData
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ExplanatoryVideoFragment : BaseFragment<CoreUiFragmentExplanatoryVideoBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiFragmentExplanatoryVideoBinding
        get() = CoreUiFragmentExplanatoryVideoBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var prefs: PrefsApi

    private val args by navArgs<ExplanatoryVideoFragmentArgs>()

    private val explanatoryVideoData by lazy {
        serializer.decodeFromString<ExplanatoryVideoData>(decodeUrl(args.explanatoryVideoData))
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onVideoEnded(true)
        }
    }

    private val animUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloat().orZero() * 100).toInt()
        uiScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.lpProgressBar.setProgress(progress, true)
            } else {
                binding.lpProgressBar.progress = progress
            }
            if (progress == 0) {
                dismissProgressBar()
                onInfographicAnimationStarted()
            }
            if (progress == 100) {
                onVideoEnded()
            }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        when (explanatoryVideoData.infographicType) {
            InfographicType.LOTTIE -> {
                binding.videoPlayer.isVisible = false
                binding.clLottie.isVisible = true
                uiScope.launch {
                    binding.clLottie.isVisible = true
                    binding.lottiePlayer.isVisible = true
                    binding.lottiePlayer.removeAllAnimatorListeners()
                    binding.tvSkip.isVisible = explanatoryVideoData.shouldShowSkipButton
                    binding.lottiePlayer.addAnimatorUpdateListener(animUpdateListener)
                    binding.lottiePlayer.setAnimation(R.raw.daily_savings_onboarding)
//                    binding.lottiePlayer.playLottieWithUrlAndExceptionHandling(
//                        requireContext(), explanatoryVideoData.infographicUrl
//                    )
                }
            }

            InfographicType.VIDEO -> {
                binding.videoPlayer.isVisible = true
                binding.clLottie.isVisible = false
                explanatoryVideoData.let {
                    binding.videoPlayer.setBackButtonVisibility(it.shouldShowBackButton)
                    binding.videoPlayer.setSkipButtonVisibility(it.shouldShowSkipButton)
                    binding.videoPlayer.setIsLoopingEnabled(it.shouldPlayVideoInLoop)
                }
            }

            else -> {}
        }
    }

    private fun setupListener() {
        binding.videoPlayer.setOnVideoEndedListener {
            binding.videoPlayer.setReplayButtonVisibility(explanatoryVideoData.shouldShowReplayButton)
            onVideoEnded()
        }

        binding.videoPlayer.setOnSkipButtonClickedListener {
            if (explanatoryVideoData.shouldShowSkipButton.orFalse())
                onVideoEnded()
        }

        binding.tvSkip.setDebounceClickListener {
            onVideoEnded(isSkipped = true)
        }

        binding.videoPlayer.setOnBackButtonClickedListener {
            popBackStack()
        }

        binding.videoPlayer.setOnVideoStartedListener {
            onInfographicAnimationStarted()
        }
    }

    private fun onVideoEnded(isSkipped: Boolean = false) {
        if (explanatoryVideoData.shouldNavigateToDeeplink) {
            explanatoryVideoData.deeplink?.let {
                EventBus.getDefault().postSticky(HandleDeepLinkEvent(it))
            } ?: kotlin.run {
                if (!explanatoryVideoData.justPopBackStack)
                    findNavController().currentBackStackEntry?.savedStateHandle?.set(
                        BaseConstants.ON_VIDEO_ENDED,
                        isSkipped
                    )
            }
        } else {
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                BaseConstants.ON_VIDEO_ENDED,
                isSkipped
            )
        }
    }

    private fun onInfographicAnimationStarted() {
        findNavController().currentBackStackEntry?.savedStateHandle?.set(
            BaseConstants.ON_VIDEO_STARTED,
            true
        )
    }

    private fun registerBackPressDispatcher() {
        if (explanatoryVideoData.infographicType == InfographicType.LOTTIE) {
            binding.lottiePlayer.cancelAnimation()
        }
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onResume() {
        prefs.setOnboardingComplete()
        when (explanatoryVideoData.infographicType) {
            InfographicType.LOTTIE -> {
                binding.lottiePlayer.playLottieWithUrlAndExceptionHandling(
                    requireContext(), explanatoryVideoData.infographicUrl
                )
            }

            InfographicType.VIDEO -> {
                binding.videoPlayer.startVideo(explanatoryVideoData.infographicUrl)
            }

            else -> {}
        }
        super.onResume()
    }

    override fun onDestroyView() {
        when (explanatoryVideoData.infographicType) {
            InfographicType.LOTTIE -> {
                binding.lottiePlayer.removeUpdateListener(animUpdateListener)
            }

            InfographicType.VIDEO -> {
                binding.videoPlayer.teardown()
            }

            else -> {}
        }
        super.onDestroyView()
    }
}