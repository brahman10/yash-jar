package com.jar.app.feature.inform_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.airbnb.lottie.LottieDrawable
import com.google.android.flexbox.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_analytics.EventKey.AutopayBottomSheet.CloseType
import com.jar.app.core_analytics.EventKey.AutopayBottomSheet.MysteryCardSpin
import com.jar.app.core_analytics.EventKey.AutopayBottomSheet.SpinMysteryCard
import com.jar.app.core_analytics.EventKey.AutopayBottomSheet.SpinWeeklyMagic
import com.jar.app.core_analytics.EventKey.AutopayBottomSheet.WeeklyMagicSpin
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.winnings.WinningsRVAdapter
import com.jar.app.core_base.domain.model.WinningsType
import com.jar.app.databinding.AutoSavedNotifyBinding
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentReward
import com.jar.app.feature_one_time_payments_common.shared.PostPaymentRewardCard
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class AutoSaveNotificationFragment :
    BaseBottomSheetDialogFragment<AutoSavedNotifyBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<AutoSaveNotificationFragmentArgs>()

    private val postPaymentReward by lazy {
        serializer.decodeFromString<PostPaymentReward?>(
            decodeUrl(args.autoGoldInvestedData.orEmpty())
        )
    }

    private val rvAdapter: WinningsRVAdapter by lazy {
        WinningsRVAdapter(false) { deeplink, featureType ->
            val buttonClickedString = when (WinningsType.getWinningsType(featureType)) {
                WinningsType.SPINS -> EventKey.AutopayBottomSheet.Spin_Count
                WinningsType.MYSTERY_CARDS -> EventKey.AutopayBottomSheet.MysteryCard
                WinningsType.WEEKLY_MAGIC -> EventKey.AutopayBottomSheet.WeeklyMagic
                WinningsType.WEEKLY_MAGIC_NEW -> EventKey.AutopayBottomSheet.WeeklyMagicNew
                WinningsType.MYSTERY_CARD_HERO -> EventKey.AutopayBottomSheet.MysteryCardHero
            }
            analyticsHandler.postEvent(
                EventKey.AutopayBottomSheet.Clicked_AutopayBottomSheet,
                mapOf(
                    EventKey.AutopayBottomSheet.Button to (buttonClickedString ?: featureType)
                )
            )
            EventBus.getDefault().post(HandleDeepLinkEvent(deeplink))
        }
    }

    private val flexboxLayoutManager: FlexboxLayoutManager? by lazy {
        if (context != null) {
            FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.SPACE_AROUND
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        } else {
            null
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> AutoSavedNotifyBinding
        get() = AutoSavedNotifyBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            shouldShowFullHeight = true,
        )

    override fun onStart() {
        super.onStart()
        try {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = bottomSheet?.let { BottomSheetBehavior.from(it) }

            behavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset > 0 && slideOffset < 1) {
                        analyticsHandler.postEvent(
                            EventKey.AutopayBottomSheet.Clicked_AutopayBottomSheetClose,
                            mapOf(
                                CloseType to EventKey.AutopayBottomSheet.CloseTypeDrag
                            )
                        )
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as? BottomSheetDialog
            val parentLayout =
                bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                it.setBackgroundResource(com.jar.app.R.drawable.bottom_sheet_background_rounded_stroked)
                val behaviour = BottomSheetBehavior.from(it)
                if (isBindingInitialized())
                    behaviour.peekHeight = binding.root.measuredHeight
            }
        }
        return dialog
    }


    override fun setup() {
        goldIconAnimation()
        setClickListeners()
        populateViewsWithData()
        sendAnalyticEvent()
    }

    private fun sendAnalyticEvent() {
        val value = when (postPaymentReward?.postPaymentRewardCardList?.size) {
            1 -> {
                val rewardAtSecPos =
                    postPaymentReward?.postPaymentRewardCardList?.get(0)?.animationType
                when (rewardAtSecPos?.let { WinningsType.getWinningsType(it) }) {
                    WinningsType.MYSTERY_CARDS -> EventKey.AutopayBottomSheet.OnlMysteryCard
                    WinningsType.MYSTERY_CARD_HERO -> EventKey.AutopayBottomSheet.OnlMysteryCardHero
                    WinningsType.WEEKLY_MAGIC -> EventKey.AutopayBottomSheet.OnlyWeeklyMagic
                    else -> EventKey.AutopayBottomSheet.NoMysteryCard
                }
            }

            2 -> {
                val rewardAtFirstPos =
                    postPaymentReward?.postPaymentRewardCardList?.get(0)?.animationType
                val rewardAtSecPos =
                    postPaymentReward?.postPaymentRewardCardList?.get(1)?.animationType
                val finalEventString: String
                val firstPosType = WinningsType.getWinningsType(rewardAtFirstPos!!)
                val secPosType = WinningsType.getWinningsType(rewardAtSecPos!!)

                val eventMap = mapOf(
                    Pair(WinningsType.SPINS, WinningsType.MYSTERY_CARDS) to SpinMysteryCard,
                    Pair(WinningsType.MYSTERY_CARDS, WinningsType.SPINS) to MysteryCardSpin,
                    Pair(WinningsType.WEEKLY_MAGIC, WinningsType.SPINS) to WeeklyMagicSpin,
                    Pair(WinningsType.SPINS, WinningsType.WEEKLY_MAGIC) to SpinWeeklyMagic
                )

                finalEventString = eventMap[Pair(firstPosType, secPosType)] ?: "no expected"
                finalEventString
            }
            else -> "NULL"
        }

        analyticsHandler.postEvent(
            EventKey.AutopayBottomSheet.Shown_AutopayBottomSheet,
            mapOf(
                EventKey.AutopayBottomSheet.State to value
            )
        )
    }

    private fun populateViewsWithData() {
        postPaymentReward?.let {
            with(binding) {
                welcomeTitle.text = it.header
                welcomeSubTitle.text = it.bottomHeader
                youSaved.text = it.savedText
                totalSpare.text = it.goldValue
                kGold.text = it.subText
                yourReward.text = it.subTitle
                setupRecyclerView(it.postPaymentRewardCardList)
            }
        }
    }

    private fun setClickListeners() {
        with(binding) {
            ivClose.setDebounceClickListener {
                analyticsHandler.postEvent(
                    EventKey.AutopayBottomSheet.Clicked_AutopayBottomSheetClose,
                    mapOf(
                        CloseType to EventKey.AutopayBottomSheet.CloseTypeIcon
                    )
                )
                dismissAllowingStateLoss()
            }
        }
    }

    private fun setupRecyclerView(postPaymentRewardCardList: List<PostPaymentRewardCard>?) {
        with(binding) {
            if (postPaymentRewardCardList == null) {
                yourReward.visibility = View.GONE
            }
            with(rvReq) {
                adapter = rvAdapter
                layoutManager = flexboxLayoutManager
            }
            rvAdapter.setData(postPaymentRewardCardList)
        }
    }

    private fun goldIconAnimation() {
        binding.oldAnimation.apply {
            repeatCount = LottieDrawable.INFINITE
            playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.LottieUrls.GOLD_BAR
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

