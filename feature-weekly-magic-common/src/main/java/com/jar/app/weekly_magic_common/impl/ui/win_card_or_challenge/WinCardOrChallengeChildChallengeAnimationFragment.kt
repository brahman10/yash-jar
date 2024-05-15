package com.jar.app.weekly_magic_common.impl.ui.win_card_or_challenge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.app.weekly_magic_common.databinding.FragmentWinCardOrChallengeChildChallengeAnimationBinding
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject


@AndroidEntryPoint
class WinCardOrChallengeChildChallengeAnimationFragment :
    BaseFragment<FragmentWinCardOrChallengeChildChallengeAnimationBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        private const val EXTRA_DATA = "EXTRA_DATA"

        fun newInstance(weeklyChallengeDetail: WeeklyChallengeDetail) =
            WinCardOrChallengeChildChallengeAnimationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_DATA, weeklyChallengeDetail)
                }
            }
    }

    private val weeklyChallengeData by lazy {
        arguments?.getParcelable<WeeklyChallengeDetail>(EXTRA_DATA)
    }

    private val viewModelProvider by viewModels<WinCardOrChallengeViewModelAndroid> {
        defaultViewModelProviderFactory
    }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWinCardOrChallengeChildChallengeAnimationBinding
        get() = FragmentWinCardOrChallengeChildChallengeAnimationBinding::inflate

    override fun setupAppBar() =
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))

    override fun setup(savedInstanceState: Bundle?) {
        weeklyChallengeData?.let {
            it.challengeId?.let {
                viewModel.markWeeklyChallengeWinViewed(it)
            }
            setObservers()
            setData(it)
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.markWeeklyChallengeWinFlow.collect(
                    onSuccess = {
                        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
                    },
                    onSuccessWithNullData = {
                        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
                    }
                )
            }
        }
    }

    private fun setData(data: WeeklyChallengeDetail) {
        analyticsHandler.postEvent(WeeklyMagicConstants.AnalyticsKeys.Shown_WeeklyMagicCompletionScreen)
        binding.animViewMain.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                uiScope.launch {
                    setAmountData()
                }
            }
        })
        when (data.totalNumberofcards.orZero()) {
            5 -> binding.animViewMain.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.LottieUrls.WEEKLY_CHALLENGE_5_CARD
            )
            8 -> binding.animViewMain.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.LottieUrls.WEEKLY_CHALLENGE_8_CARD
            )
            12 -> binding.animViewMain.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.LottieUrls.WEEKLY_CHALLENGE_12_CARD
            )
        }
        binding.animViewMain.playAnimation()

    }

    private fun setAmountData() {
        binding.animViewPoof.setAnimation("magic_card_poof.lottie")
        binding.animViewPoof.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                uiScope.launch {
                    binding.animViewPoof.isVisible = false
                    weeklyChallengeData?.let {
                        setResultTimer(it)
                    } ?: kotlin.run {
                        goToNextScreen()
                    }
                }
            }

            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                uiScope.launch {
                    weeklyChallengeData?.let {
                        binding.tvWonAmount.text =
                            getCustomStringFormatted(
                                MR.strings.feature_weekly_magic_common_weekly_magic_currency_in_s,
                                it.rewardAmount.orZero().toInt().toString()
                            )
                    }
                    binding.ivStars.isVisible = true
                    binding.tvCongrats.isVisible = true
                }
            }
        })
        binding.animViewPoof.playAnimation()
    }

    private fun setResultTimer(data: WeeklyChallengeDetail) {
        binding.containerCalender.isVisible = false
        binding.containerMessageTimerValue.isVisible = false
        binding.tvMessageTimer.isVisible = !data.nextChallengeStartDate.isNullOrBlank()
        binding.tvMessageTimer.text =
            getCustomString(MR.strings.feature_weekly_magic_common_next_challenge_starts_in)
        val today = Instant.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        //val zoneOffSet = OffsetDateTime.now().offset
        val endDay = ZonedDateTime.parse(data.nextChallengeStartDate).toEpochSecond()
        val daysBetween = (endDay - today) / (60 * 60 * 24)
        if (daysBetween > 0) {
            binding.containerCalender.isVisible = true
            binding.containerMessageTimerValue.isVisible = false
            binding.tvValueDay.text = daysBetween.toString()
            binding.tvLabelDay.text =
                getCustomString(if (daysBetween == 1L) MR.strings.feature_weekly_magic_common_day else MR.strings.feature_weekly_magic_common_days)
        } else {
            binding.containerCalender.isVisible = false
            binding.containerMessageTimerValue.isVisible = true
            val milliSecondsBetween = (endDay - today) * 1000
            setValidForCounter(milliSecondsBetween)
        }
        goToNextScreen()
    }

    private fun setValidForCounter(validityTimeStamp: Long) {
        validityTimeStamp.takeIf { it > 0L }?.let { timeStamp ->
            uiScope.countDownTimer(
                timeStamp,
                onInterval = {
                    val values = it.milliSecondsToCountDown(true)
                    val arr = values.split(":")
                    val hour = arr.getOrNull(0)
                    val minute = arr.getOrNull(1)
                    val second = arr.getOrNull(2)

                    binding.tvTxt1.text = hour?.getOrNull(0)?.toString()
                    binding.tvTxt2.text = hour?.getOrNull(1)?.toString()
                    binding.tvTxt3.text = minute?.getOrNull(0)?.toString()
                    binding.tvTxt4.text = minute?.getOrNull(1)?.toString()
                    binding.tvTxt5.text = second?.getOrNull(0)?.toString()
                    binding.tvTxt6.text = second?.getOrNull(1)?.toString()
                },
                onFinished = {
                    goToNextScreen()
                }
            ).start()
        } ?: kotlin.run {
            binding.containerMessageTimerValue.isVisible = false
        }

    }

    private fun goToNextScreen() {
        uiScope.launch {
            delay(3000)
            setFragmentResult(WinCardOrChallengeParentFragment.MOVE_TO_NEXT, bundleOf())
        }
    }
}