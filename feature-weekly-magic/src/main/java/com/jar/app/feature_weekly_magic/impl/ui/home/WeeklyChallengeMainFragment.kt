package com.jar.app.feature_weekly_magic.impl.ui.home

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView
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
import com.example.feature_weekly_magic.databinding.FragmentWeeklyChallengeMainBinding
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.getHtmlTextValue
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_buy_gold_v2.api.BuyGoldV2Api
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeCtaRedirectionType
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet12Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet5Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet8Binding
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.DateTimeException
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class WeeklyChallengeMainFragment : BaseFragment<FragmentWeeklyChallengeMainBinding>() {

    @Inject
    lateinit var buyGoldApi: BuyGoldV2Api

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val timeInit = System.currentTimeMillis()

    private val viewModelProvider by viewModels<WeeklyChallengeHomeViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val weakReference: WeakReference<View> by lazy {
        WeakReference(binding.root)
    }

    private var minimumAmount = 0
    private var daysBetween = 0L
    private val today = Instant.now()
    private var todayContainer: View? = null

    private val fromScreen by lazy {
        arguments?.getString(FROM_SCREEN) ?: ""
    }

    private var isWeeklyChallengeInfoShowedOnce = false
    private var isAutoScrolled = false

    companion object {
        private const val BUBBLE_MESSAGE_TRANSLATION_Y = 15F
        private const val BUBBLE_MESSAGE_TRANSLATION_DURATION = 1000L
        private const val FROM_SCREEN = "FROM_SCREEN"
        fun newInstance(fromScreen: String) = WeeklyChallengeMainFragment().apply {
            arguments = Bundle().apply {
                putString(FROM_SCREEN, fromScreen)
            }
        }
    }

    private var bubbleJob: Job? = null
    private var bubbleAnimation: ObjectAnimator? = null

    private var objectAnimatorDown: ObjectAnimator? = null
    private var objectAnimatorUp:ObjectAnimator? = null
    private var animatorSetBounce: AnimatorSet? = null

    private val bounceAnimationListener = object : Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            animatorSetBounce?.start()
        }

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}
    }

    private var jobScrollViewAnimation: Job? = null


    private var scrollDownObjectAnimator: ObjectAnimator? = null
    private var scrollDownAnimationListener = object: Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            scrollUpObjectAnimator = ObjectAnimator.ofInt(
                binding.scrollContent,
                "scrollY", 0
            )
            scrollUpObjectAnimator?.duration = 1000
            scrollUpObjectAnimator?.interpolator = AccelerateDecelerateInterpolator()
            scrollUpObjectAnimator?.addListener(scrollUpAnimationListener)
            scrollUpObjectAnimator?.start()
        }
        override fun onAnimationCancel(animation: Animator) {
            binding.scrollContent.setScrollingEnabled(true)
        }
        override fun onAnimationRepeat(animation: Animator) {}
    }

    private var scrollUpObjectAnimator: ObjectAnimator? = null
    private var scrollUpAnimationListener = object : Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            binding.scrollContent.setScrollingEnabled(true)
        }
        override fun onAnimationCancel(animation: Animator) {
            binding.scrollContent.setScrollingEnabled(true)
        }
        override fun onAnimationRepeat(animation: Animator) {}
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWeeklyChallengeMainBinding
        get() = FragmentWeeklyChallengeMainBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setUpUI()
        setListeners()
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchWeeklyChallengeMetaData()
        viewModel.fetchWeeklyChallengeDetails()
    }

    private fun setUpUI() {
        binding.containerCards.removeAllViews()
        binding.magicHatFabLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.WEEKLY_CHALLENGE_HAT_WITHOUT_HIGHLIGHT
        )
    }

    private fun startScrollViewAnimation() {
        jobScrollViewAnimation?.cancel()
        jobScrollViewAnimation = uiScope.launch {
            binding.scrollContent.setScrollingEnabled(false)
            scrollDownObjectAnimator = ObjectAnimator.ofInt(
                binding.scrollContent,
                "scrollY", 80.dp
            )
            scrollDownObjectAnimator?.duration = 1500
            scrollDownObjectAnimator?.interpolator = AccelerateDecelerateInterpolator()
            scrollDownObjectAnimator?.addListener(scrollDownAnimationListener)

            scrollDownObjectAnimator?.start()
        }
    }

    private fun setListeners() {
        binding.btnAction.setDebounceClickListener {
            registerClickEvent(binding.btnAction.getText(), mapOf(WeeklyMagicConstants.AnalyticsKeys.Parameters.timespentbeforeclick to ((System.currentTimeMillis() - timeInit)/1000f).toString()))
            if (viewModel.weeklyChallengeDetailFlow.value.data?.data?.getCtaRedirectionType() == WeeklyChallengeCtaRedirectionType.INFO_CARD && isWeeklyChallengeInfoShowedOnce.not()) {
                isWeeklyChallengeInfoShowedOnce = true
                setFragmentResult(WeeklyChallengeHomeFragment.OPEN_INFO_DIALOG, bundleOf())
            } else {
                viewModel.weeklyChallengeDetailFlow.value.data?.data?.ctaDeeplink.takeIf { it.isNullOrEmpty().not() }?.let {
                    EventBus.getDefault().post(HandleDeepLinkEvent(it))
                } ?: kotlin.run {
                    minimumAmount.takeIf { it != 0 }?.let {
                        buyGoldApi.openBuyGoldFlowWithWeeklyChallengeAmount(
                            viewModel.weeklyChallengeDetailFlow.value.data?.data?.minEligibleTxnAmount.orZero(),
                            BaseConstants.BuyGoldFlowContext.WEEKLY_CHALLENGE
                        )
                    }
                }
            }
        }
        binding.btnLeftChevron.setDebounceClickListener {
            viewModel.weeklyChallengeDetailFlow.value.data?.data?.prevChallengeId?.let {
                if (previousChallengeIsViewed()) {
                    proceedToPreviousWeekChallenge()
                } else {
                    viewModel.markPreviousChallengeViewed(it)
                }
            } ?: kotlin.run {
                getCustomString(MR.strings.feature_weekly_magic_common_challenge_not_found).snackBar(
                    binding.root
                )
            }
        }

        binding.containerCards.setDebounceClickListener {
            registerClickEvent("Mystery_Card")
        }

        binding.tvBubbleText.setDebounceClickListener {
            registerClickEvent(WeeklyMagicConstants.AnalyticsKeys.Values.MessageClicked)
        }

        binding.magicHatFabLottie.setDebounceClickListener {
            registerClickEvent(WeeklyMagicConstants.AnalyticsKeys.Values.HatClicked)
        }

        binding.containerOne.setDebounceClickListener {
            sendDateClickedEvent()
        }

        binding.containerTwo.setDebounceClickListener {
            sendDateClickedEvent()
        }

        binding.containerThree.setDebounceClickListener {
            sendDateClickedEvent()
        }

        binding.containerFour.setDebounceClickListener {
            sendDateClickedEvent()
        }

        binding.containerFive.setDebounceClickListener {
            sendDateClickedEvent()
        }

        binding.containerSix.setDebounceClickListener {
            sendDateClickedEvent()
        }

        binding.containerSeven.setDebounceClickListener {
            sendDateClickedEvent()
        }
    }

    private fun sendDateClickedEvent() {
        registerClickEvent(WeeklyMagicConstants.AnalyticsKeys.Values.DateClicked)
    }

    private fun previousChallengeIsViewed(): Boolean {
        return viewModel.weeklyChallengeMetaDataFlow.value.data?.data?.prevWeekStoryViewedStatus
            ?: true
    }

    private fun proceedToPreviousWeekChallenge() {
        viewModel.weeklyChallengeDetailFlow.value.data?.data?.prevChallengeId?.let {
            registerClickEvent("Left_Chevron")
            setFragmentResult(
                WeeklyChallengeHomeFragment.MOVE_TO_LEFT, bundleOf(
                    Pair(WeeklyChallengeHomeFragment.CHALLENGE_ID, it)
                )
            )
        } ?: kotlin.run {
            getCustomString(MR.strings.feature_weekly_magic_common_challenge_not_found).snackBar(
                binding.root
            )
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyChallengeDetailFlow.collect(
                    onSuccess = { weeklyChallengeDetails ->
                        weeklyChallengeDetails?.let {
                            setTextHeadings(it)
                            setVisibility(it)
                            setCalenderData(it)
                            setCards(it)
                            if (it.getCtaRedirectionType() == WeeklyChallengeCtaRedirectionType.INFO_CARD && isAutoScrolled.not()) {
                                isAutoScrolled = true
                                startScrollViewAnimation()
                            }
                            if (it.chatBubbleList?.size.orZero() > 0) {
                                uiScope.launch {
                                    //Intentional delay, Product wanted the chat bubbles to load 1 seconds
                                    //after other details is populated
                                    delay(1000L)
                                    startBubbleAnimation(it)
                                }
                            }
                            analyticsHandler.postEvent(
                                WeeklyMagicConstants.AnalyticsKeys.Shown_WeeklyMagicScreen,
                                mapOf(
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.fromScreen to fromScreen,
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.shownCards to it.totalNumberofcards.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to it.numCardsCollected.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.daysLeft to daysBetween.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.minimumOrderValue to it.minEligibleTxnAmount.toString(),
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.screenNumber to 0,
                                    WeeklyMagicConstants.AnalyticsKeys.Parameters.weeklymagictips to it.chatBubbleList?.joinToString(",") { bubbleItem -> bubbleItem.text.getHtmlTextValue().toString() }.orEmpty(),
                                )
                            )
                        }
                    }, onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.markPreviousChallengeViewedFlow.collect(
                    onSuccess = {
                        viewModel.fetchWeeklyChallengeMetaData()
                        proceedToPreviousWeekChallenge()
                    }, onSuccessWithNullData = {
                        viewModel.fetchWeeklyChallengeMetaData()
                        proceedToPreviousWeekChallenge()
                    }, onError = { _, _ ->
                        viewModel.fetchWeeklyChallengeMetaData()
                        proceedToPreviousWeekChallenge()
                    }
                )
            }
        }
    }

    private fun startBubbleAnimation(data: WeeklyChallengeDetail) {
        data.chatBubbleList?.let {
            bubbleJob?.cancel()
            bubbleJob = uiScope.launch {
                startBounceAnimationForBubble()
                for (chatBubble in it) {
                    binding.tvBubbleText.setHtmlText(chatBubble.text)
                    binding.tvBubbleText.isVisible = true
                    bubbleAnimation?.end()
                    bubbleAnimation =
                        ObjectAnimator.ofFloat(
                            binding.tvBubbleText,
                            "alpha",
                            0F,
                            1F
                        )
                    bubbleAnimation?.duration = 500
                    bubbleAnimation?.interpolator = LinearInterpolator()
                    bubbleAnimation?.start()
                    delay(chatBubble.duration ?: 2000)
                }
                animatorSetBounce?.cancel()
                binding.tvBubbleText.isVisible = false
            }
        }
    }

    private fun startBounceAnimationForBubble() {
        objectAnimatorUp?.cancel()
        objectAnimatorDown?.cancel()
        animatorSetBounce?.cancel()

        objectAnimatorUp = ObjectAnimator.ofFloat(binding.tvBubbleText,"translationY",binding.tvBubbleText.y - BUBBLE_MESSAGE_TRANSLATION_Y, binding.tvBubbleText.y + BUBBLE_MESSAGE_TRANSLATION_Y)
            .setDuration(BUBBLE_MESSAGE_TRANSLATION_DURATION)
        objectAnimatorDown = ObjectAnimator.ofFloat(binding.tvBubbleText,"translationY",binding.tvBubbleText.y + BUBBLE_MESSAGE_TRANSLATION_Y, binding.tvBubbleText.y - BUBBLE_MESSAGE_TRANSLATION_Y)
            .setDuration(BUBBLE_MESSAGE_TRANSLATION_DURATION)
        animatorSetBounce = AnimatorSet()
        animatorSetBounce?.play(objectAnimatorUp)?.before(objectAnimatorDown)
        animatorSetBounce?.addListener(bounceAnimationListener)
        animatorSetBounce?.start()
    }

    private fun setTextHeadings(data: WeeklyChallengeDetail) {
        binding.tvCurrentWeekTitle.setHtmlText(data.title.orEmpty())
        binding.tvBottomMessage.setHtmlText(data.secondaryDescription.orEmpty())
        binding.tvPrimaryDescription.setHtmlText(data.primaryDescription.orEmpty())
        binding.btnAction.setText(data.ctaText ?: getCustomStringFormatted(MR.strings.feature_weekly_magic_common_get_mystery_card))
        minimumAmount = data.minEligibleTxnAmount.orZero().toInt()
    }

    private fun setVisibility(data: WeeklyChallengeDetail) {
        binding.btnAction.isVisible = data.minEligibleTxnAmount != 0f
        binding.btnLeftChevron.isVisible = !data.prevChallengeId.isNullOrBlank()
    }

    private fun setCalenderData(data: WeeklyChallengeDetail) {
        try {
            Instant.parse(data.nextChallengeStartDate)?.let { nextChallengeStartDate ->
                val endDay = nextChallengeStartDate.atZone(ZoneId.systemDefault()).minusSeconds(1L)
                var dateTimeOfStart = endDay.minusDays(6L)
                setDateData(
                    binding.tvDayOne, binding.tvDateOne, binding.containerOne, dateTimeOfStart
                )
                dateTimeOfStart = dateTimeOfStart.plusDays(1L)
                setDateData(
                    binding.tvDayTwo, binding.tvDateTwo, binding.containerTwo, dateTimeOfStart
                )
                dateTimeOfStart = dateTimeOfStart.plusDays(1L)
                setDateData(
                    binding.tvDayThree, binding.tvDateThree, binding.containerThree, dateTimeOfStart
                )
                dateTimeOfStart = dateTimeOfStart.plusDays(1L)
                setDateData(
                    binding.tvDayFour, binding.tvDateFour, binding.containerFour, dateTimeOfStart
                )
                dateTimeOfStart = dateTimeOfStart.plusDays(1L)
                setDateData(
                    binding.tvDayFive, binding.tvDateFive, binding.containerFive, dateTimeOfStart
                )
                dateTimeOfStart = dateTimeOfStart.plusDays(1L)
                setDateData(
                    binding.tvDaySix, binding.tvDateSix, binding.containerSix, dateTimeOfStart
                )
                dateTimeOfStart = dateTimeOfStart.plusDays(1L)
                setDateData(
                    binding.tvDaySeven, binding.tvDateSeven, binding.containerSeven, dateTimeOfStart
                )
                daysBetween = ChronoUnit.DAYS.between(today, endDay.plusDays(1L))
                binding.tvRemainingDays.setHtmlText(data.daysLeftDescription.orEmpty())
                binding.tvCurrentMonth.text =
                    today.atZone(ZoneId.systemDefault()).month.name.substring(0, 3).lowercase()
                        .replaceFirstChar { it.uppercase() }
                uiScope.launch {
                    delay(500)
                    todayContainer?.let {
                        val dimensions = IntArray(2)
                        it.getLocationInWindow(dimensions)
                        val distanceOfContainer = dimensions[0]
                        val widthOfContainer = it.width / 2
                        binding.progressBar.setProgressCompat(
                            (((distanceOfContainer + widthOfContainer) * 100) / binding.containerCalender.width),
                            true
                        )
                    }
                }
            }
        } catch (e: DateTimeException) {

        }
    }

    private fun setDateData(
        tvDay: AppCompatTextView,
        tvDate: AppCompatTextView,
        containerLayout: View,
        dateTimeOfStart: ZonedDateTime
    ) {
        tvDay.text = dateTimeOfStart.dayOfWeek.name.substring(0, 3).lowercase()
            .replaceFirstChar { it.uppercase() }
        tvDate.text =
            dateTimeOfStart.dayOfMonth.toString().lowercase().replaceFirstChar { it.uppercase() }
        if (dateTimeOfStart.toInstant().isBefore(today)) {
            tvDay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white_30
                )
            )
            tvDate.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white_30
                )
            )
        }
        val todayOfYear = today.atZone(ZoneId.systemDefault()).dayOfYear
        val todayYear = today.atZone(ZoneId.systemDefault()).year
        var dayOfYear = dateTimeOfStart.dayOfYear
        if (dateTimeOfStart.year > todayYear) {
            dayOfYear += if (todayYear % 400 == 0 || (todayYear % 4 == 0 && todayYear % 100 != 0)) {
                366
            } else {
                365
            }
        }
        if (dayOfYear - todayOfYear == 0) {
            containerLayout.setBackgroundResource(com.jar.app.core_ui.R.drawable.bg_rounded_corner_5636af_20_radius_8dp)
            tvDay.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white
                )
            )
            tvDate.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.white
                )
            )
            tvDate.setTypeface(tvDate.typeface, Typeface.BOLD)
            tvDay.setTypeface(tvDay.typeface, Typeface.BOLD)
            todayContainer = containerLayout
        } else {
            containerLayout.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun setCards(data: WeeklyChallengeDetail) {
        binding.containerCards.removeAllViews()
        when (data.totalNumberofcards) {
            5 -> addCardSet5ToView(data.numCardsCollected.orZero())
            8 -> addCardSet8ToView(data.numCardsCollected.orZero())
            12 -> addCardSet12ToView(data.numCardsCollected.orZero())
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
            val marginTop = 3f / 100f * binding.containerHome.width.toFloat()
            (cardSetBinding.cardBackgroundFour.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFour.requestLayout()
            }
            (cardSetBinding.cardBackgroundFive.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFive.requestLayout()
            }
        }
        setupAnalyticsOnCard(
            numCardsCollected,
            cardSetBinding.ivCardOne,
            cardSetBinding.ivCardTwo,
            cardSetBinding.ivCardThree,
            cardSetBinding.ivCardFour,
            cardSetBinding.ivCardFive
        )
        binding.containerCards.addView(cardSetBinding.root)
    }

    private fun setupAnalyticsOnCard(count: Int, vararg args: View) {
        args.forEachIndexed { index, view ->
            if (index < count)
                view.setDebounceClickListener {
                    registerClickEvent("Mystery_Card", mapOf("Placeholder" to ("false")))
                }
        }
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
            val marginTop = 3f / 100f * binding.containerHome.width.toFloat()
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
        setupAnalyticsOnCard(
            numCardsCollected,
            cardSetBinding.ivCardOne,
            cardSetBinding.ivCardTwo,
            cardSetBinding.ivCardThree,
            cardSetBinding.ivCardFour,
            cardSetBinding.ivCardFive,
            cardSetBinding.ivCardSix,
            cardSetBinding.ivCardSeven,
            cardSetBinding.ivCardEight,
        )
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
            val marginTop = 3f / 100f * binding.containerHome.width.toFloat()
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

        setupAnalyticsOnCard(
            numCardsCollected,
            cardSetBinding.ivCardOne,
            cardSetBinding.ivCardTwo,
            cardSetBinding.ivCardThree,
            cardSetBinding.ivCardFour,
            cardSetBinding.ivCardFive,
            cardSetBinding.ivCardSix,
            cardSetBinding.ivCardSeven,
            cardSetBinding.ivCardEight,
            cardSetBinding.ivCardNine,
            cardSetBinding.ivCardTen,
            cardSetBinding.ivCardEleven,
            cardSetBinding.ivCardTwelve,
        )
        binding.containerCards.addView(
            cardSetBinding.root, ViewGroup.LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
        )
    }

    private fun registerClickEvent(optionChosen: String, map: Map<String, String>? = null) {
        viewModel.weeklyChallengeDetailFlow.value.data?.data?.let {
            analyticsHandler.postEvent(
                WeeklyMagicConstants.AnalyticsKeys.Clicked_Button_WeeklyMagicScreen,
                mutableMapOf(
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.optionChosen to optionChosen,
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.daysLeft to daysBetween.toString(),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.minimumOrderValue to it.minEligibleTxnAmount.toString(),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.shownCards to (it.totalNumberofcards?.toString() ?: ""),
                    WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to (it.numCardsCollected?.toString() ?: "")
                ).apply {
                    map?.forEach {
                        this[it.key] = it.value
                    }
                }
            )

        }
    }

    override fun onDestroyView() {
        bubbleJob?.cancel()
        bubbleAnimation?.end()
        animatorSetBounce?.removeListener(bounceAnimationListener)
        animatorSetBounce?.end()
        jobScrollViewAnimation?.cancel()
        scrollDownObjectAnimator?.removeListener(scrollDownAnimationListener)
        scrollUpObjectAnimator?.removeListener(scrollUpAnimationListener)
        super.onDestroyView()
    }
}