package com.jar.app.weekly_magic_common.impl.ui.win_card_or_challenge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.weekly_magic_common.R
import com.jar.app.weekly_magic_common.databinding.FragmentWinCardOrChallengeChildCardAnimationBinding
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
class WinCardOrChallengeChildCardAnimationFragment :
    BaseFragment<FragmentWinCardOrChallengeChildCardAnimationBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val listOf5Cards = listOf(
        R.drawable.ic_magic_card_5_design_1,
        R.drawable.ic_magic_card_5_design_2,
        R.drawable.ic_magic_card_5_design_3,
        R.drawable.ic_magic_card_5_design_4,
        R.drawable.ic_magic_card_5_design_5
    )

    private val listOf8Cards = listOf(
        R.drawable.ic_magic_card_8_design_1,
        R.drawable.ic_magic_card_8_design_2,
        R.drawable.ic_magic_card_8_design_3,
        R.drawable.ic_magic_card_8_design_4,
        R.drawable.ic_magic_card_8_design_5,
        R.drawable.ic_magic_card_8_design_6,
        R.drawable.ic_magic_card_8_design_7,
        R.drawable.ic_magic_card_8_design_8
    )

    private val listOf12Cards = listOf(
        R.drawable.ic_magic_card_12_design_1,
        R.drawable.ic_magic_card_12_design_2,
        R.drawable.ic_magic_card_12_design_3,
        R.drawable.ic_magic_card_12_design_4,
        R.drawable.ic_magic_card_12_design_5,
        R.drawable.ic_magic_card_12_design_6,
        R.drawable.ic_magic_card_12_design_7,
        R.drawable.ic_magic_card_12_design_8,
        R.drawable.ic_magic_card_12_design_9,
        R.drawable.ic_magic_card_12_design_10,
        R.drawable.ic_magic_card_12_design_11,
        R.drawable.ic_magic_card_12_design_12
    )

    private var isAnimationCompleted = false

    companion object {
        private const val SHOW_TEXT_ANIMATION = "SHOW_TEXT_ANIMATION"
        private const val EXTRA_DATA = "EXTRA_DATA"
        private const val EXTRA_FROM_SCREEN = "EXTRA_FROM_SCREEN"

        fun newInstance(
            showCongratsTextAnimation: Boolean,
            fromScreen:String,
            weeklyChallengeDetail: WeeklyChallengeDetail
        ) =
            WinCardOrChallengeChildCardAnimationFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(SHOW_TEXT_ANIMATION, showCongratsTextAnimation)
                    putString(EXTRA_FROM_SCREEN, fromScreen)
                    putParcelable(EXTRA_DATA, weeklyChallengeDetail)
                }
            }
    }

    private val weeklyChallengeData by lazy {
        arguments?.getParcelable<WeeklyChallengeDetail>(EXTRA_DATA)
    }

    private val fromScreen by lazy {
        arguments?.getString(EXTRA_FROM_SCREEN)
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWinCardOrChallengeChildCardAnimationBinding
        get() = FragmentWinCardOrChallengeChildCardAnimationBinding::inflate

    override fun setupAppBar() =
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))

    override fun setup(savedInstanceState: Bundle?) {
        setUpUI()
        registerEvent()
    }

    private fun registerEvent() {
        analyticsHandler.postEvent(
            WeeklyMagicConstants.AnalyticsKeys.Shown_MysteryCardAnimationScreen,
            mapOf(EventKey.FromScreen to fromScreen!!))
    }


    private fun setUpUI() {
        isAnimationCompleted = false
        if (arguments?.getBoolean(SHOW_TEXT_ANIMATION, false) == true) {
            binding.animTick.isVisible = true
            binding.animTick.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    uiScope.launch {
                        binding.animTick.isVisible = false
                        binding.tvCongratsMessage.isVisible = false
                        startNextAnimation()
                    }
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    uiScope.launch {
                        binding.tvCongratsMessage.alpha = 0.0f
                        binding.tvCongratsMessage.isVisible = true
                        binding.tvCongratsMessage
                            .animate()
                            .translationY(binding.tvCongratsMessage.height.toFloat())
                            .alpha(1.0f)
                            .setDuration(1000)
                            .start()
                    }
                }
            })
            binding.animTick.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                "${BaseConstants.CDN_BASE_URL}/LottieFiles/Lending_Kyc/tick_with _celebration.json"
            )
        } else {
            startNextAnimation()
        }
    }

    private fun startNextAnimation() {
        uiScope.launch {
            binding.animViewMain.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    uiScope.launch {
                        binding.tvHoorayMessage.isVisible = true
                    }
                }
            })
            revealCard()
        }
    }


    private fun revealCard() {
        setDetailsOfCard()
        binding.ivMysteryCard.isVisible = false
        binding.ivMysteryCard.animate()
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setStartDelay(2500)
            .translationY(-200f)
            .rotationBy(-20f)
            .rotationYBy(360f * 3f)
            .alpha(1.0f)
            .setDuration(3000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    uiScope.launch {
                        binding.ivMysteryCard.isVisible = true
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    uiScope.launch {
                        moveCardToBottom()
                    }
                }
            }).start()
    }


    private fun setDetailsOfCard() {
        val cardNumber = weeklyChallengeData?.numCardsCollected?.takeIf {
            it > 1 && it <= weeklyChallengeData?.totalNumberofcards.orZero()
        }?.let {
            it - 1
        } ?: kotlin.run {
            0
        }

        when (weeklyChallengeData?.totalNumberofcards) {
            5 -> {
                binding.ivMysteryCard.setImageResource(listOf5Cards[cardNumber])
                binding.ivMysteryCard.setBackgroundResource(R.drawable.ic_magic_card_5_base)
            }
            8 -> {
                binding.ivMysteryCard.setImageResource(listOf8Cards[cardNumber])
                binding.ivMysteryCard.setBackgroundResource(R.drawable.ic_magic_card_8_base)
            }
            12 -> {
                binding.ivMysteryCard.setImageResource(listOf12Cards[cardNumber])
                binding.ivMysteryCard.setBackgroundResource(R.drawable.ic_magic_card_12_base)
            }
            else -> {
                binding.ivMysteryCard.setBackgroundResource(R.drawable.ic_magic_card_empty)
            }
        }
    }

    private fun moveCardToBottom() {
        uiScope.launch {
            delay(1000)
            setFragmentResult(WinCardOrChallengeParentFragment.MOVE_TO_NEXT, bundleOf())
        }
    }

}