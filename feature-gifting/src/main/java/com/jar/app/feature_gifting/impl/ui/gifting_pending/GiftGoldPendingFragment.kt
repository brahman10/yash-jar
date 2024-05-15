package com.jar.app.feature_gifting.impl.ui.gifting_pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.scrollToBottom
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentGiftPendingBinding
import com.jar.app.feature_gifting.impl.ui.gifting_success.GiftGoldSuccessFragmentArgs
import com.jar.app.feature_gifting.impl.ui.gifting_success.GoldGiftStatusViewModel
import com.jar.app.feature_gifting.shared.util.EventKey
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.GiftingStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GiftGoldPendingFragment : BaseFragment<FeatureGiftingFragmentGiftPendingBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var giftingApi: GiftingApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<GiftGoldSuccessFragmentArgs>()

    private val fetchManualPaymentStatusResponse by lazy {
        serializer.decodeFromString<FetchManualPaymentStatusResponse>(
            decodeUrl(args.fetchManualPaymentStatusResponse)
        )
    }

    private val viewModel by viewModels<GoldGiftStatusViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentGiftPendingBinding
        get() = FeatureGiftingFragmentGiftPendingBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            EventKey.Shown_SuccessScreen_GiftGoldScreen,
            mapOf(
                EventKey.status to EventKey.pending,
                EventKey.giftedTo to if (fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.receiverJarUser.orFalse()) EventKey.existingUser else EventKey.newUser,
                EventKey.amount to fetchManualPaymentStatusResponse.sendGiftResponse?.amountToBePaid.toString(),
                EventKey.quantity to fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.volume.toString(),
            )
        )
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.root.scrollToBottom()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Gifting/gift_pending.json"
        )

        binding.tvAmountPaid.text = getString(
            R.string.feature_gifting_amount_paid_x,
            fetchManualPaymentStatusResponse.amount.orZero()
        )
        binding.tvGoldVolume.text = getString(
            R.string.feature_gifting_gold_gift_quantity_x,
            fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.volume.orZero()
                .volumeToString()
        )
        binding.tvId.text = fetchManualPaymentStatusResponse.transactionId

        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
    }

    private fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnRefresh.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_GiftGoldFlow,
                mapOf(
                    EventKey.status to EventKey.pending,
                    EventKey.giftedTo to if (fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.receiverJarUser.orFalse()) EventKey.existingUser else EventKey.newUser,
                    EventKey.buttonType to EventKey.refresh,
                )
            )
            viewModel.fetchManualPaymentStatus(fetchManualPaymentStatusResponse)
        }

        binding.tvId.setDebounceClickListener {
            requireContext().copyToClipboard(
                fetchManualPaymentStatusResponse.transactionId.orEmpty(),
                getString(R.string.feature_gifting_copied)
            )
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.fetchManualPaymentResponseLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                when (it.sendGiftResponse?.getGiftingStatus()) {
                    GiftingStatus.FAILURE -> {
                        navigateTo(
                            GiftGoldPendingFragmentDirections
                                .actionGiftGoldPendingFragmentToGiftGoldFailureFragment(
                                    encodeUrl(serializer.encodeToString(it))
                                ),
                            popUpTo = R.id.giftGoldPendingFragment,
                            inclusive = true
                        )
                    }
                    GiftingStatus.SENT -> {
                        navigateTo(
                            GiftGoldPendingFragmentDirections
                                .actionGiftGoldPendingFragmentToGiftGoldSuccessFragment(
                                    encodeUrl(serializer.encodeToString(it))
                                ),
                            popUpTo = R.id.giftGoldPendingFragment,
                            inclusive = true
                        )
                    }
                    else -> {
                        //Do Nothing..
                    }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )
    }
}