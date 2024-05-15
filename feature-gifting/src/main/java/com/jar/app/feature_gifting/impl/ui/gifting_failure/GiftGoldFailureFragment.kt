package com.jar.app.feature_gifting.impl.ui.gifting_failure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.scrollToBottom
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.api.GiftingApi
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentGiftFailureBinding
import com.jar.app.feature_gifting.shared.domain.model.SendGiftGoldRequest
import com.jar.app.feature_gifting.impl.ui.gifting_pending.GiftGoldPendingFragmentDirections
import com.jar.app.feature_gifting.impl.ui.gifting_success.GiftGoldSuccessFragmentArgs
import com.jar.app.feature_gifting.impl.ui.gifting_success.GoldGiftStatusViewModel
import com.jar.app.feature_gifting.shared.util.EventKey
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_one_time_payments_common.shared.GiftingStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_gifting.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GiftGoldFailureFragment : BaseFragment<FeatureGiftingFragmentGiftFailureBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var giftingApi: GiftingApi

    private val args by navArgs<GiftGoldSuccessFragmentArgs>()

    private val fetchManualPaymentStatusResponse by lazy {
        serializer.decodeFromString<FetchManualPaymentStatusResponse>(
            decodeUrl(args.fetchManualPaymentStatusResponse)
        )
    }

    private val viewModel by viewModels<GoldGiftStatusViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentGiftFailureBinding
        get() = FeatureGiftingFragmentGiftFailureBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            EventKey.Shown_SuccessScreen_GiftGoldScreen,
            mapOf(
                EventKey.status to EventKey.failure,
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
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Gifting/gift_failed.json"
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
    }

    private fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnTryAgain.setDebounceClickListener {
            val sendGiftGoldRequest = SendGiftGoldRequest(
                receiverName = fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.receiverName,
                receiverPhoneNo = fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.receiverNumber
            )
            giftingApi.openSendGiftScreen(EventKey.retryFlow, sendGiftGoldRequest)
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_GiftGoldFlow,
                mapOf(
                    EventKey.status to EventKey.pending,
                    EventKey.giftedTo to if (fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.receiverJarUser.orFalse()) EventKey.existingUser else EventKey.newUser,
                    EventKey.buttonType to EventKey.tryAgain,
                )
            )
        }

        binding.btnContactSupport.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_GiftGoldFlow,
                mapOf(
                    EventKey.status to EventKey.pending,
                    EventKey.giftedTo to if (fetchManualPaymentStatusResponse.sendGiftResponse?.receiverDetails?.receiverJarUser.orFalse()) EventKey.existingUser else EventKey.newUser,
                    EventKey.buttonType to EventKey.contact,
                )
            )
            val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy | hh:mm a")
            val transactionDate =
                if (fetchManualPaymentStatusResponse.transactionDate != null)
                    Instant.ofEpochMilli(fetchManualPaymentStatusResponse.transactionDate!!)
                        .atZone(ZoneId.systemDefault())
                else
                    Instant.now().atZone(ZoneId.systemDefault())

            val message = getCustomStringFormatted(
                MR.strings.feature_gifting_failure_contact_us_message,
                fetchManualPaymentStatusResponse.transactionId!!,
                fetchManualPaymentStatusResponse.amount?.roundUp(2).orZero(),
                transactionDate.format(formatter)
            )
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
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