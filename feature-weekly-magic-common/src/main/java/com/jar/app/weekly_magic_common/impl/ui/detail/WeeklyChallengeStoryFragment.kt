package com.jar.app.weekly_magic_common.impl.ui.detail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.RefreshWeeklyChallengeMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.weekly_magic_common.R
import com.jar.app.weekly_magic_common.api.WeeklyChallengeCommonApi
import com.jar.app.weekly_magic_common.databinding.FragmentWeeklyChallengeDetailBinding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet12Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet5Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet8Binding
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class WeeklyChallengeStoryFragment : BaseFragment<FragmentWeeklyChallengeDetailBinding>() {

    @Inject
    lateinit var weeklyChallengeCommonApi: WeeklyChallengeCommonApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<WeeklyChallengeStoryFragmentArgs>()

    private lateinit var animator: Animator

    private val viewModelProvider by viewModels<WeeklyChallengeStoryViewModelAndroid> {
        defaultViewModelProviderFactory
    }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var daysBetween = 0L

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWeeklyChallengeDetailBinding
        get() = FragmentWeeklyChallengeDetailBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setUpUI()
        setListeners()
        setObservers()
        getData()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().postSticky(RefreshWeeklyChallengeMetaEvent())
    }

    private fun getData() {
        viewModel.fetchWeeklyChallengeDetails(args.challengeId)
    }

    private fun setUpUI() {
        binding.containerCards.removeAllViews()
    }

    private fun setListeners() {
        binding.btnBack.setDebounceClickListener {
            registerClickEvent("Back_Arrow")
            popBackStack()
        }
    }

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeDetailFlow.collect(
                    onLoading = {
                        dismissProgressBar()
                    },
                    onSuccess = { weeklyChallengeDetail ->
                        dismissProgressBar()
                        weeklyChallengeDetail?.let {
                            setResult(it)
                            binding.tvBottomStoryText.setHtmlText(it.bottomStoryText.orEmpty())
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        startStoryTimer()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                        startStoryTimer()
                    }
                )
            }
        }
    }

    private fun setResult(data: WeeklyChallengeDetail) {
        setTextHeadings(data)
        if (data.totalNumberofcards.orZero() == 0) {
            return
        }
        if (data.numCardsCollected.orZero() >= data.totalNumberofcards.orZero()) {
            binding.ivBackground.setImageResource(R.drawable.bg_weekly_challenge_home)
            setWonCardDetails(data)
        } else {
            binding.ivBackground.setImageResource(R.drawable.bg_weekly_challenge_previous)
            setCards(data)
        }
    }

    private fun setTextHeadings(data: WeeklyChallengeDetail) {
        binding.tvCurrentWeekTitle.setHtmlText(data.storyTitle.orEmpty())
        data.highlightedText?.icon?.takeIf { it.isEmpty().not() }?.let {
            Glide.with(requireContext()).load(it).into(binding.ivHighlightedComponentIcon)
        }
        binding.tvHighlightedComponentText.setHtmlText(data.highlightedText?.text.orEmpty())
        data.highlightedText?.backgroundColour.takeIf { it.isNullOrEmpty().not() }?.let {
            binding.cvHighlightedComponent.setCardBackgroundColor(Color.parseColor(it))
        }
        binding.cvHighlightedComponent.isVisible = data.highlightedText != null
    }

    private fun setWonCardDetails(data: WeeklyChallengeDetail) {
        binding.containerWonCard.isVisible = true
        binding.tvWonAmount.text =
            getCustomStringFormatted(
                MR.strings.feature_weekly_magic_common_weekly_magic_currency_in_s,
                data.rewardAmount.orZero().toInt().toString()
            )
        binding.containerBottom.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                when (data.totalNumberofcards) {
                    5 -> com.jar.app.core_ui.R.color.color_C027D5
                    8 -> com.jar.app.core_ui.R.color.color_CC348D
                    12 -> com.jar.app.core_ui.R.color.color_FE5A70
                    else -> com.jar.app.core_ui.R.color.color_C027D5
                }
            )
        )
        startStoryTimer()
    }

    private fun setCards(data: WeeklyChallengeDetail) {
        if (data.numCardsCollected.orZero() < data.totalNumberofcards.orZero()) {
            uiScope.launch {
                binding.containerCards.isVisible = false
                binding.animSadHat.setAnimation("sad_hat.lottie")
                binding.animSadHat.isVisible = true
                binding.animSadHat.addAnimatorListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        uiScope.launch {
                            binding.animSadHat.isVisible = false
                            displayCards(data)
                        }
                    }
                })
                binding.animSadHat.playAnimation()
            }
        } else {
            displayCards(data)
        }
    }

    private fun displayCards(data: WeeklyChallengeDetail) {
        binding.containerCards.isVisible = true
        when (data.totalNumberofcards) {
            5 -> addCardSet5ToView(data.numCardsCollected.orZero())
            8 -> addCardSet8ToView(data.numCardsCollected.orZero())
            12 -> addCardSet12ToView(data.numCardsCollected.orZero())
            else -> binding.containerCards.isVisible = false
        }
        startStoryTimer()
    }

    private fun addCardSet5ToView(numCardsCollected: Int) {
        val cardSetBinding =
            LayoutMagicCardSet5Binding.inflate(LayoutInflater.from(requireContext()))
        cardSetBinding.ivCardOne.isInvisible = numCardsCollected < 1
        cardSetBinding.ivCardTwo.isInvisible = numCardsCollected < 2
        cardSetBinding.ivCardThree.isInvisible = numCardsCollected < 3
        cardSetBinding.ivCardFour.isInvisible = numCardsCollected < 4
        cardSetBinding.ivCardFive.isInvisible = numCardsCollected < 5
        uiScope.launch {
            val marginTop = 3f / 100f * binding.containerStory.width.toFloat()

            (cardSetBinding.cardBackgroundFour.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFour.requestLayout()
            }
            (cardSetBinding.cardBackgroundFive.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFive.requestLayout()
            }
        }
        binding.containerCards.addView(cardSetBinding.root)
    }

    private fun addCardSet8ToView(numCardsCollected: Int) {
        val cardSetBinding =
            LayoutMagicCardSet8Binding.inflate(LayoutInflater.from(requireContext()))
        cardSetBinding.ivCardOne.isInvisible = numCardsCollected < 1
        cardSetBinding.ivCardTwo.isInvisible = numCardsCollected < 2
        cardSetBinding.ivCardThree.isInvisible = numCardsCollected < 3
        cardSetBinding.ivCardFour.isInvisible = numCardsCollected < 4
        cardSetBinding.ivCardFive.isInvisible = numCardsCollected < 5
        cardSetBinding.ivCardSix.isInvisible = numCardsCollected < 6
        cardSetBinding.ivCardSeven.isInvisible = numCardsCollected < 7
        cardSetBinding.ivCardEight.isInvisible = numCardsCollected < 8
        uiScope.launch {
            val marginTop = 3f / 100f * binding.containerStory.width.toFloat()
            (cardSetBinding.cardBackgroundFive.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFive.requestLayout()
            }
            (cardSetBinding.cardBackgroundSix.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundSix.requestLayout()
            }
            (cardSetBinding.cardBackgroundSeven.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundSeven.requestLayout()
            }
            (cardSetBinding.cardBackgroundEight.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundEight.requestLayout()
            }
        }
        binding.containerCards.addView(cardSetBinding.root)
    }

    private fun addCardSet12ToView(numCardsCollected: Int) {
        val cardSetBinding =
            LayoutMagicCardSet12Binding.inflate(LayoutInflater.from(requireContext()))
        cardSetBinding.ivCardOne.isInvisible = numCardsCollected < 1
        cardSetBinding.ivCardTwo.isInvisible = numCardsCollected < 2
        cardSetBinding.ivCardThree.isInvisible = numCardsCollected < 3
        cardSetBinding.ivCardFour.isInvisible = numCardsCollected < 4
        cardSetBinding.ivCardFive.isInvisible = numCardsCollected < 5
        cardSetBinding.ivCardSix.isInvisible = numCardsCollected < 6
        cardSetBinding.ivCardSeven.isInvisible = numCardsCollected < 7
        cardSetBinding.ivCardEight.isInvisible = numCardsCollected < 8
        cardSetBinding.ivCardNine.isInvisible = numCardsCollected < 9
        cardSetBinding.ivCardTen.isInvisible = numCardsCollected < 10
        cardSetBinding.ivCardEleven.isInvisible = numCardsCollected < 11
        cardSetBinding.ivCardTwelve.isInvisible = numCardsCollected < 12
        uiScope.launch {
            val marginTop = 3f / 100f * binding.containerStory.width.toFloat()
            (cardSetBinding.cardBackgroundFive.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFive.requestLayout()
            }
            (cardSetBinding.cardBackgroundSix.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundSix.requestLayout()
            }
            (cardSetBinding.cardBackgroundSeven.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundSeven.requestLayout()
            }
            (cardSetBinding.cardBackgroundEight.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundEight.requestLayout()
            }
            (cardSetBinding.cardBackgroundNine.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundNine.requestLayout()
            }
            (cardSetBinding.cardBackgroundTen.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundTen.requestLayout()
            }
            (cardSetBinding.cardBackgroundEleven.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundEleven.requestLayout()
            }
            (cardSetBinding.cardBackgroundTwelve.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundTwelve.requestLayout()
            }
        }
        binding.containerCards.addView(cardSetBinding.root)
    }

    private fun startStoryTimer() {
        viewModel.markPreviousWeeklyChallengeStoryViewed(args.challengeId)
        checkAndCancelAnimator()
        binding.progressBar.isVisible = true
        binding.tvBottomStoryText.isVisible = true
        binding.containerCalender.isVisible = false
        animator = ValueAnimator.ofInt(0, binding.progressBar.max)
        animator.duration = 5000
        (animator as ValueAnimator?)?.addUpdateListener { it ->
            val progress = (it.animatedValue as Int?) ?: 0
            binding.progressBar.setProgressCompat(progress, true)
            if (progress >= 100) {
                closeTheStoryMode()
            }
            binding
        }
        animator.start()
    }

    private fun closeTheStoryMode() {
        uiScope.launch {
            findNavController().currentBackStackEntry?.savedStateHandle
                ?.set(WeeklyMagicConstants.ON_STORY_MODE_FINISHED, true)
            popBackStack()
        }
    }

    private fun checkAndCancelAnimator() {
        if (::animator.isInitialized && animator.isRunning) {
            animator.cancel()
        }
    }

    override fun onDestroyView() {
        checkAndCancelAnimator()
        //analyticsHandler.postEvent(WeeklyMagicConstants.AnalyticsKeys.Completed_WeeklyChallengeOnBoarding)
        super.onDestroyView()
    }

    private fun registerClickEvent(optionChosen: String) {
        viewModel.weeklyChallengeDetailFlow.value.data?.data?.let {
            analyticsHandler.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.Clicked_Button_WeeklyMagicScreen,
                mapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.optionChosen to optionChosen,
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.daysLeft to daysBetween.toString(),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.minimumOrderValue to it.minEligibleTxnAmount.toString(),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.shownCards to (it.totalNumberofcards?.toString() ?: ""),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to (it.numCardsCollected?.toString() ?: "")
                )
            )

        }
    }

}