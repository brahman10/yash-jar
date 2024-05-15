package com.jar.app.weekly_magic_common.impl.ui.win_card_or_challenge

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import com.airbnb.lottie.LottieAnimationView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.orZero
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_weekly_magic_common.shared.MR
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeDetail
import com.jar.app.feature_weekly_magic_common.shared.utils.WeeklyMagicConstants
import com.jar.app.weekly_magic_common.databinding.FragmentWinCardOrChallengeChildCardResultBinding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet12Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet5Binding
import com.jar.app.weekly_magic_common.databinding.LayoutMagicCardSet8Binding
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class WinCardOrChallengeChildCardResultFragment :
    BaseFragment<FragmentWinCardOrChallengeChildCardResultBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var imageCardWonFromDeck: ImageView? = null
    private var imageCardWonFromDeckContainer: ConstraintLayout? = null
    private var imageCardWonFromDeckParent: ConstraintLayout? = null


    companion object {
        private const val EXTRA_DATA = "EXTRA_DATA"
        private const val EXTRA_FROM_SCREEN = "EXTRA_FROM_SCREEN"

        fun newInstance(
            weeklyChallengeDetail: WeeklyChallengeDetail,
            fromScreen: String
        ) =
            WinCardOrChallengeChildCardResultFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FROM_SCREEN, fromScreen)
                    putParcelable(EXTRA_DATA, weeklyChallengeDetail)
                }
            }
    }

    private val fromScreen by lazy {
        arguments?.getString(EXTRA_FROM_SCREEN)
    }

    private val weeklyChallengeData by lazy {
        arguments?.getParcelable<WeeklyChallengeDetail>(EXTRA_DATA)
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWinCardOrChallengeChildCardResultBinding
        get() = FragmentWinCardOrChallengeChildCardResultBinding::inflate

    override fun setupAppBar() =
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))

    override fun setup(savedInstanceState: Bundle?) {
        setUpUI()
        registerEvent()
    }

    private fun registerEvent() {
        analyticsHandler.postEvent(
            WeeklyMagicConstants.AnalyticsKeys.Shown_MysteryCardsCollectionScreen,
            mapOf(EventKey.FromScreen to fromScreen!!,
                WeeklyMagicConstants.AnalyticsKeys.Parameters.cardsCollected to weeklyChallengeData?.numCardsCollected.orZero())
        )
    }

    private fun setUpUI() {
        binding.containerCards.removeAllViews()
        binding.magicHatFabLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.WEEKLY_CHALLENGE_IDLE_STATE_VERTICAL
        )
        weeklyChallengeData?.let {
            binding.tvCurrentWeekTitle.setHtmlText(
                it.postSuccessCardMessage?.takeIf { it.isNotBlank() }
                    ?: getCustomString(MR.strings.feature_weekly_magic_common_current_challenge))
            binding.tvMessageOne.text = setHeaders(it)
            setPrefData(it)
            setCards(it)
            uiScope.launch {
                delay(1000)
                playLottieAnimationOnCard()
            }
        }
    }

    private fun setHeaders(data: WeeklyChallengeDetail) =
        if (data.totalNumberofcards.orZero() != 0 &&
            data.numCardsCollected.orZero() == data.totalNumberofcards.orZero()
        ) {
            getCustomString(MR.strings.feature_weekly_magic_common_your_collection_is_complete)
        } else {
            getCustomStringFormatted(MR.strings.feature_weekly_magic_common_collect_all_cards_and_win_up_in_gold,data.potentialWinAmount.orZero().toInt().toString())
        }

    private fun setPrefData(data: WeeklyChallengeDetail) {
        prefs.setWonMysteryCardCount(data.numCardsCollected.orZero())
        prefs.setWonMysteryCardChallengeId(data.challengeId ?: "")
    }

    private fun setCards(data: WeeklyChallengeDetail) {
        binding.containerCards.removeAllViews()
        binding.containerCards.isVisible = false
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
        binding.containerCards.addView(cardSetBinding.root)
        uiScope.launch {
            val marginTop = 3f/100f * binding.containerParent.width.toFloat()

            (cardSetBinding.cardBackgroundFour.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFour.requestLayout()
            }
            (cardSetBinding.cardBackgroundFive.layoutParams as ConstraintLayout.LayoutParams).let {
                it.topMargin = marginTop.toInt()
                cardSetBinding.cardBackgroundFive.requestLayout()
            }
        }
        imageCardWonFromDeck =
            when (numCardsCollected.orZero()) {
                1 -> cardSetBinding.ivCardOne.apply { isInvisible = true }
                2 -> cardSetBinding.ivCardTwo.apply { isInvisible = true }
                3 -> cardSetBinding.ivCardThree.apply { isInvisible = true }
                4 -> cardSetBinding.ivCardFour.apply { isInvisible = true }
                5 -> cardSetBinding.ivCardFive.apply { isInvisible = true }
                else -> cardSetBinding.ivCardFive.apply { isInvisible = true }
            }
        imageCardWonFromDeckContainer =
            when (numCardsCollected.orZero()) {
                1 -> cardSetBinding.cardBackgroundOne
                2 -> cardSetBinding.cardBackgroundTwo
                3 -> cardSetBinding.cardBackgroundThree
                4 -> cardSetBinding.cardBackgroundFour
                5 -> cardSetBinding.cardBackgroundFive
                else -> cardSetBinding.cardBackgroundOne
            }
        imageCardWonFromDeckParent = cardSetBinding.containerCardParent
        binding.containerCards.isVisible = true
    }

    private fun addCardSet8ToView(numCardsCollected: Int) {
        binding.containerCards.isVisible = false
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
        binding.containerCards.addView(cardSetBinding.root)
        imageCardWonFromDeck =
            when (numCardsCollected.orZero()) {
                1 -> cardSetBinding.ivCardOne.apply { isInvisible = true }
                2 -> cardSetBinding.ivCardTwo.apply { isInvisible = true }
                3 -> cardSetBinding.ivCardThree.apply { isInvisible = true }
                4 -> cardSetBinding.ivCardFour.apply { isInvisible = true }
                5 -> cardSetBinding.ivCardFive.apply { isInvisible = true }
                6 -> cardSetBinding.ivCardSix.apply { isInvisible = true }
                7 -> cardSetBinding.ivCardSeven.apply { isInvisible = true }
                8 -> cardSetBinding.ivCardEight.apply { isInvisible = true }
                else -> cardSetBinding.ivCardFive.apply { isInvisible = true }
            }
        imageCardWonFromDeckContainer =
            when (numCardsCollected.orZero()) {
                1 -> cardSetBinding.cardBackgroundOne
                2 -> cardSetBinding.cardBackgroundTwo
                3 -> cardSetBinding.cardBackgroundThree
                4 -> cardSetBinding.cardBackgroundFour
                5 -> cardSetBinding.cardBackgroundFive
                6 -> cardSetBinding.cardBackgroundSix
                7 -> cardSetBinding.cardBackgroundSeven
                8 -> cardSetBinding.cardBackgroundEight
                else -> cardSetBinding.cardBackgroundOne
            }
        imageCardWonFromDeckParent = cardSetBinding.containerCardParent
        uiScope.launch {
            val marginTop = 3f/100f * binding.containerParent.width.toFloat()
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
            delay(1500)
            binding.containerCards.isVisible = true
        }
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
        binding.containerCards.addView(cardSetBinding.root)
        uiScope.launch {
            val marginTop = 3f/100f * binding.containerParent.width.toFloat()
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
        imageCardWonFromDeck = when (numCardsCollected.orZero()) {
            1 -> cardSetBinding.ivCardOne.apply { isInvisible = true }
            2 -> cardSetBinding.ivCardTwo.apply { isInvisible = true }
            3 -> cardSetBinding.ivCardThree.apply { isInvisible = true }
            4 -> cardSetBinding.ivCardFour.apply { isInvisible = true }
            5 -> cardSetBinding.ivCardFive.apply { isInvisible = true }
            6 -> cardSetBinding.ivCardSix.apply { isInvisible = true }
            7 -> cardSetBinding.ivCardSeven.apply { isInvisible = true }
            8 -> cardSetBinding.ivCardEight.apply { isInvisible = true }
            9 -> cardSetBinding.ivCardNine.apply { isInvisible = true }
            10 -> cardSetBinding.ivCardTen.apply { isInvisible = true }
            11 -> cardSetBinding.ivCardEleven.apply { isInvisible = true }
            12 -> cardSetBinding.ivCardTwelve.apply { isInvisible = true }
            else -> cardSetBinding.ivCardTwelve.apply { isInvisible = true }
        }
        imageCardWonFromDeckContainer =
            when (numCardsCollected.orZero()) {
                1 -> cardSetBinding.cardBackgroundOne
                2 -> cardSetBinding.cardBackgroundTwo
                3 -> cardSetBinding.cardBackgroundThree
                4 -> cardSetBinding.cardBackgroundFour
                5 -> cardSetBinding.cardBackgroundFive
                6 -> cardSetBinding.cardBackgroundSix
                7 -> cardSetBinding.cardBackgroundSeven
                8 -> cardSetBinding.cardBackgroundEight
                9 -> cardSetBinding.cardBackgroundNine
                10 -> cardSetBinding.cardBackgroundTen
                11 -> cardSetBinding.cardBackgroundEleven
                12 -> cardSetBinding.cardBackgroundTwelve
                else -> cardSetBinding.cardBackgroundOne
            }
        imageCardWonFromDeckParent = cardSetBinding.containerCardParent
        binding.containerCards.isVisible = true
    }

    private fun playLottieAnimationOnCard() {
        goToNextScreen()
       /* imageCardWonFromDeck?.let { cardToPlayAnimationOn ->
            val lottie = getPoofLottie()
            lottie.setAnimation("magic_card_poof.lottie")
            lottie.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    uiScope.launch {
                        goToNextScreen()
                    }
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    uiScope.launch {
                        cardToPlayAnimationOn.alpha = 0f
                        cardToPlayAnimationOn.isVisible = true
                        cardToPlayAnimationOn.animate().alpha(1f).duration = 1000
                    }
                }
            })
            lottie.playAnimation()
        }*/
    }

    private fun getPoofLottie(): LottieAnimationView {
        val set = ConstraintSet()
        set.clone(imageCardWonFromDeckParent)
        val poofLottie = LottieAnimationView(requireContext())
        poofLottie.id = View.generateViewId()
        poofLottie.scaleType = ImageView.ScaleType.CENTER_INSIDE
      //  poofLottie.setAnimation("magic_card_poof.lottie")
        imageCardWonFromDeckParent?.addView(poofLottie)
        set.constrainHeight(poofLottie.id, WRAP_CONTENT)
        set.constrainWidth(poofLottie.id, WRAP_CONTENT)
        set.connect(
            poofLottie.id,
            ConstraintSet.TOP,
            imageCardWonFromDeckContainer!!.id,
            ConstraintSet.TOP
        )
        set.connect(
            poofLottie.id,
            ConstraintSet.START,
            imageCardWonFromDeckContainer!!.id,
            ConstraintSet.START
        )
        set.connect(
            poofLottie.id,
            ConstraintSet.BOTTOM,
            imageCardWonFromDeckContainer!!.id,
            ConstraintSet.BOTTOM
        )
        set.connect(
            poofLottie.id,
            ConstraintSet.END,
            imageCardWonFromDeckContainer!!.id,
            ConstraintSet.END
        )
        set.applyTo(imageCardWonFromDeckParent)
        return poofLottie
    }

    private fun goToNextScreen() {
        uiScope.launch {
            delay(1000)
            setFragmentResult(WinCardOrChallengeParentFragment.MOVE_TO_NEXT, bundleOf())
        }
    }
}