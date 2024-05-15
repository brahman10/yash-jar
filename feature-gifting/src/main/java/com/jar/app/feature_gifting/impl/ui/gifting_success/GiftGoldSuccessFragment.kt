package com.jar.app.feature_gifting.impl.ui.gifting_success

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.EpoxyController
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentGiftGoldSuccessBinding
import com.jar.app.feature_gifting.shared.util.EventKey
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GiftGoldSuccessFragment :
    BaseFragment<FeatureGiftingFragmentGiftGoldSuccessBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var giftingApi: GiftingApi

    private val args by navArgs<GiftGoldSuccessFragmentArgs>()

    private val goldGiftResponse by lazy {
        serializer.decodeFromString<FetchManualPaymentStatusResponse>(
            decodeUrl(args.fetchManualPaymentStatusResponse)
        ).sendGiftResponse!!
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentGiftGoldSuccessBinding
        get() = FeatureGiftingFragmentGiftGoldSuccessBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi


    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            EventKey.Shown_SuccessScreen_GiftGoldScreen,
            mapOf(
                EventKey.status to EventKey.success,
                EventKey.giftedTo to if (goldGiftResponse.receiverDetails?.receiverJarUser.orFalse()) EventKey.existingUser else EventKey.newUser,
                EventKey.amount to goldGiftResponse.receiverDetails?.amount.orZero().toString(),
                EventKey.quantity to goldGiftResponse.receiverDetails?.volume.orZero().toString(),
            )
        )

        binding.txtGiftSentTo.text = goldGiftResponse.title

        val controller = object : EpoxyController() {
            override fun buildModels() {
                goldGiftResponse.description?.forEach {
                    SuccessDescriptionEpoxyModel(it).id(it).addTo(this)
                }
            }
        }

        binding.recyclerView.setControllerAndBuildModels(controller)

        binding.topLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Gifting/gift_success.json"
        )

        binding.animConfetti.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                binding.animConfetti.isVisible = false
            }
        })

        binding.btnClose.setDebounceClickListener {
            popBackStack()
        }
        binding.btnShare.setDebounceClickListener {
            giftingApi.shareGift(
                uiScope,
                WeakReference(requireContext()),
                goldGiftResponse.receiverDetails?.receiverJarUser.orFalse(),
                shouldShareOnlyOnWA = true
            )
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_GiftGoldFlow,
                mapOf(
                    EventKey.status to EventKey.success,
                    EventKey.giftedTo to if (goldGiftResponse.receiverDetails?.receiverJarUser.orFalse()) EventKey.existingUser else EventKey.newUser,
                    EventKey.buttonType to EventKey.letThemKnow,
                )
            )
        }

        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
    }
}