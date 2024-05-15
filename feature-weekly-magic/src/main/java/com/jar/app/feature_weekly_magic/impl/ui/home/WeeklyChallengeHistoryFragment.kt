package com.jar.app.feature_weekly_magic.impl.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.feature_weekly_magic.databinding.FragmentWeeklyChallengeHistoryBinding
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.countDownTimer
import com.jar.app.base.util.milliSecondsToCountDown
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet12Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet5Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet8Binding
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class WeeklyChallengeHistoryFragment : BaseFragment<FragmentWeeklyChallengeHistoryBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<WeeklyChallengeHomeViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val challengeId by lazy {
        arguments?.getString(CHALLENGE_ID) ?: ""
    }

    private val fromScreen by lazy {
        arguments?.getString(FROM_SCREEN) ?: ""
    }

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }

    companion object {
        private const val CHALLENGE_ID = "CHALLENGE_ID"
        private const val FROM_SCREEN = "FROM_SCREEN"
        fun newInstance(challengeId: String, fromScreen: String) =
            WeeklyChallengeHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(CHALLENGE_ID, challengeId)
                    putString(FROM_SCREEN, fromScreen)
                }
            }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWeeklyChallengeHistoryBinding
        get() = FragmentWeeklyChallengeHistoryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setUpUI()
        setListeners()
        setObservers()
        getData()
    }

    private fun getData() {
        challengeId.takeIf { it.isNotBlank() }?.let {
            viewModel.fetchWeeklyChallengeDetails(it)
        }
    }

    private fun setUpUI() {
        binding.containerCards.removeAllViews()
    }

    private fun setListeners() {
        binding.btnRightChevron.setDebounceClickListener {
            viewModel.weeklyChallengeDetailByIdFlow.value
                .data?.data?.nextChallengeId?.takeIf { it.isNotBlank() }
                ?.let {
                    registerClickEvent("Right_Chevron")
                    setFragmentResult(
                        WeeklyChallengeHomeFragment.MOVE_TO_RIGHT, bundleOf(
                            Pair(WeeklyChallengeHomeFragment.CHALLENGE_ID, it)
                        )
                    )
                }
        }
        binding.btnLeftChevron.setDebounceClickListener {
            viewModel.weeklyChallengeDetailByIdFlow.value
                .data?.data?.prevChallengeId?.takeIf { it.isNotBlank() }
                ?.let {
                    registerClickEvent("Left_Chevron")
                    setFragmentResult(
                        WeeklyChallengeHomeFragment.MOVE_TO_LEFT, bundleOf(
                            Pair(WeeklyChallengeHomeFragment.CHALLENGE_ID, it)
                        )
                    )
                }
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeDetailByIdFlow.collect(
                    onLoading = {
                        dismissProgressBar()
                    },
                    onSuccess = { weeklyChallengeDetail ->
                        dismissProgressBar()
                        weeklyChallengeDetail?.let {
                            setTextHeadings(it)
                            setActionsVisibility(it)
                            setResult(it)
                            analyticsHandler.postEvent(
                                WeeklyMagicConstants.AnalyticsKeys.Shown_WeeklyMagicScreen,
                                mapOf(
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.fromScreen to fromScreen,
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.shownCards to it.totalNumberofcards.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to it.numCardsCollected.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.minimumOrderValue to it.minEligibleTxnAmount.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.resultStatus to getResultValue(it),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.screenNumber to if (!it.prevChallengeId.isNullOrBlank()) 1 else 2,
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.resultStatus to getResultValue(it),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.challengelostmessage to it.highlightedText?.text.orEmpty().getHtmlTextValue().toString()
                                )
                            )
                        }
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun getResultValue(data: WeeklyChallengeDetail): String {
        return if (data.numCardsCollected.orZero() >= data.totalNumberofcards.orZero()) {
            "WEEKLY_CHALLENGE_WON"
        } else {
            "WEEKLY_CHALLENGE_LOST"
        }
    }

    private fun setActionsVisibility(data: WeeklyChallengeDetail) {
        binding.btnLeftChevron.isVisible = !data.prevChallengeId.isNullOrBlank()
        binding.btnRightChevron.isVisible = !data.nextChallengeId.isNullOrBlank()
    }

    private fun setTextHeadings(data: WeeklyChallengeDetail) {
        binding.tvCurrentWeekTitle.setHtmlText(getTitle(data).orEmpty())
        binding.llParticipatedUsers.isVisible = data.usersParticipatedText.isNullOrEmpty().not()
        binding.tvUsersParticipated.setHtmlText(data.usersParticipatedText.orEmpty())
        binding.llGoldWon.isVisible = data.amountWonText.isNullOrEmpty().not()
        binding.tvGoldWon.setHtmlText(data.amountWonText.orEmpty())
        data.highlightedText?.icon?.takeIf { it.isEmpty().not() }?.let {
            Glide.with(requireContext()).load(it).into(binding.ivHighlightedComponentIcon)
        }
        binding.tvHighlightedComponentText.setHtmlText(data.highlightedText?.text.orEmpty())
        data.highlightedText?.backgroundColour.takeIf { it.isNullOrEmpty().not() }?.let {
            binding.cvHighlightedComponent.setCardBackgroundColor(Color.parseColor(it))
        }
        binding.cvHighlightedComponent.isVisible = data.highlightedText != null
        setUserImages(data)
    }

    private fun getTitle(data: WeeklyChallengeDetail) =
        if (viewModel.weeklyChallengeMetaDataFlow.value.data?.data?.challengeId == data.challengeId) {
            data.title
        } else {
            data.storyTitle
        }

    private fun setUserImages(data: WeeklyChallengeDetail) {
        uiScope.launch {
            if (data.participatedUrls == null) {
                binding.ivUserOne.isVisible = false
                binding.ivUserTwo.isVisible = false
                binding.ivUserThree.isVisible = false
                return@launch
            }
            if (data.participatedUrls!!.isNotEmpty()) {
                binding.ivUserOne.isVisible = true
                Glide.with(requireContext())
                    .load(data.participatedUrls!![0])
                    .circleCrop()
                    .error(com.jar.app.weekly_magic_common.R.drawable.default_participant_icon_one)
                    .circleCrop()
                    .into(binding.ivUserOne)
            } else {
                binding.ivUserOne.isVisible = false
            }
            if (data.participatedUrls!!.size > 1) {
                binding.ivUserTwo.isVisible = true
                Glide.with(requireContext())
                    .load(data.participatedUrls!![1])
                    .circleCrop()
                    .error(com.jar.app.weekly_magic_common.R.drawable.default_participant_icon_two)
                    .circleCrop()
                    .into(binding.ivUserTwo)
            } else {
                binding.ivUserTwo.isVisible = false
            }
            if (data.participatedUrls!!.size > 2) {
                binding.ivUserThree.isVisible = true
                Glide.with(requireContext())
                    .load(data.participatedUrls!![2])
                    .circleCrop()
                    .error(com.jar.app.weekly_magic_common.R.drawable.default_participant_icon_three)
                    .circleCrop()
                    .into(binding.ivUserThree)
            } else {
                binding.ivUserThree.isVisible = false
            }
        }
    }

    private fun setResult(data: WeeklyChallengeDetail) {
        if (data.totalNumberofcards.orZero() == 0) {
            return
        }
        if (data.numCardsCollected.orZero() >= data.totalNumberofcards.orZero()) {
            binding.ivBackground.setImageResource(com.jar.app.weekly_magic_common.R.drawable.bg_weekly_challenge_home)
            setWonCardDetails(data)
        } else {
            binding.ivBackground.setImageResource(com.jar.app.weekly_magic_common.R.drawable.bg_weekly_challenge_previous)
            setCards(data)
        }
        setResultTimer(data)
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
    }

    private fun setCards(data: WeeklyChallengeDetail) {
        when (data.totalNumberofcards) {
            5 -> addCardSet5ToView(data.numCardsCollected.orZero())
            8 -> addCardSet8ToView(data.numCardsCollected.orZero())
            12 -> addCardSet12ToView(data.numCardsCollected.orZero())
            else -> binding.containerCards.isVisible = false
        }
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
            val marginTop = 3f / 100f * binding.containerDetails.width.toFloat()
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
        binding.containerCards.isVisible = true
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
            val marginTop = 3f / 100f * binding.containerDetails.width.toFloat()
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
        binding.containerCards.isVisible = true
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
            val marginTop = 3f / 100f * binding.containerDetails.width.toFloat()
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
        binding.containerCards.isVisible = true
    }

    private fun registerClickEvent(optionChosen: String) {
        viewModel.weeklyChallengeDetailByIdFlow.value.data?.data?.let {
            analyticsHandler.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.Clicked_Button_WeeklyMagicScreen,
                mapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.optionChosen to optionChosen,
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.minimumOrderValue to it.minEligibleTxnAmount.toString(),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.shownCards to (it.totalNumberofcards?.toString() ?: ""),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to (it.numCardsCollected?.toString() ?: "")
                )
            )

        }
    }


    private fun setResultTimer(result: WeeklyChallengeDetail?) {
        result?.let { data ->
            binding.containerCalender.isVisible = false
            binding.containerMessageTimerValue.isVisible = false
            val today = Instant.now().atZone(ZoneId.systemDefault()).toEpochSecond()
            val zoneOffSet = OffsetDateTime.now().offset
            val endDay = ZonedDateTime.parse(data.nextChallengeStartDate).toEpochSecond()
            val daysBetween = (endDay - today) / (60 * 60 * 24)
            if (daysBetween > 0) {
                binding.tvMessageTimer.isVisible = true
                binding.tvMessageTimer.text =
                    getCustomString(MR.strings.feature_weekly_magic_common_next_challenge_starts_in)
                binding.containerCalender.isVisible = true
                binding.tvValueDay.text = daysBetween.toString()
                binding.tvLabelDay.text =
                    getCustomString(if (daysBetween == 1L) MR.strings.feature_weekly_magic_common_day else MR.strings.feature_weekly_magic_common_days)
            } else {
                val milliSecondsBetween = (endDay - today) * 1000
                setValidForCounter(milliSecondsBetween)
            }
        }
    }

    private fun setValidForCounter(validityTimeStamp: Long?) {
        validityTimeStamp?.takeIf { it > 0L }?.let { validity ->
            binding.containerMessageTimerValue.isVisible = true
            binding.tvMessageTimer.isVisible = true
            binding.tvMessageTimer.text =
                getCustomString(MR.strings.feature_weekly_magic_common_next_challenge_begins_in)
            uiScope.countDownTimer(
                validity,
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
                    setFragmentResult(WeeklyChallengeHomeFragment.REFRESH_DATA, bundleOf())
                }
            )
        } ?: kotlin.run {
            binding.containerMessageTimerValue.isVisible = false
            binding.tvMessageTimer.isVisible = false
        }
    }


}