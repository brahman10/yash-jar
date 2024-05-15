package com.jar.app.feature_gifting.impl.ui.gift_summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.core_base.util.roundUp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByAmountRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.impl.BuyGoldRequestType
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingFragmentGiftSummaryBinding
import com.jar.app.feature_gifting.shared.MR
import com.jar.app.feature_gifting.shared.domain.model.GiftSummary
import com.jar.app.feature_gifting.shared.util.EventKey
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GiftSummaryFragment : BaseFragment<FeatureGiftingFragmentGiftSummaryBinding>() {

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<GiftSummaryFragmentArgs>()

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

    private val viewModel by viewModels<GiftSummaryFragmentViewModel> { defaultViewModelProviderFactory }

    private var controller: GiftSummaryController? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGiftingFragmentGiftSummaryBinding
        get() = FeatureGiftingFragmentGiftSummaryBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            EventKey.Shown_Screen_GoldGiftingFlow,
            mapOf(EventKey.screenType to EventKey.giftSummary)
        )
        viewModel.fetchBuyPrice()
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun setupUI() {
        binding.rvGiftingBreakdown.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGiftingBreakdown.addItemDecorationIfNoneAdded(spaceItemDecoration)
        controller = GiftSummaryController()
        binding.rvGiftingBreakdown.setControllerAndBuildModels(controller!!)
    }

    private fun setData(fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse) {
        val amountWithoutTax =
            (args.sendGiftRequest.amount.orZero() * 100f) / (100f + fetchCurrentGoldPriceResponse.applicableTax.orZero())
        val tax = args.sendGiftRequest.amount.orZero() - amountWithoutTax

        val goldGiftValue = GiftSummary(
            "Gold Gift value",
            getCustomStringFormatted(
                MR.strings.feature_gifting_currency_sign_x_float,
                amountWithoutTax.roundUp(2)
            )
        )
        val gst = GiftSummary(
            "GST(${fetchCurrentGoldPriceResponse.applicableTax}%)",
            getCustomStringFormatted(
                MR.strings.feature_gifting_currency_sign_x_float,
                tax.roundDown(2)
            )
        )
        val goldGiftVolume =
            GiftSummary(
                "Gold Gift Quantity",
                "${args.sendGiftRequest.volume} ${getString(R.string.feature_gifting_gm_label)}"
            )

        val list = mutableListOf(goldGiftValue, gst, goldGiftVolume)
        controller?.cards = list

        binding.tvTotalAmount.text =
            getCustomStringFormatted(
                MR.strings.feature_gifting_currency_sign_x_float,
                args.sendGiftRequest.amount.orZero()
            )

        binding.tvName.text = args.sendGiftRequest.receiverName
        binding.tvNumber.text = args.sendGiftRequest.receiverPhoneNo

        binding.goldPriceProgressLayout.start(
            livePriceMessage = getString(
                R.string.feature_gifting_live_buy_price,
                fetchCurrentGoldPriceResponse.price
            ),
            validityInMillis = fetchCurrentGoldPriceResponse.getValidityInMillis(),
            uiScope = uiScope,
            onFinish = {
                viewModel.fetchBuyPrice()
            },
        )
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.buyPriceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                setData(it)
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.buyGoldLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                it?.let {
                    dismissProgressBar()
                    EventBus.getDefault()
                        .post(InitiatePaymentEvent(it, BaseConstants.ManualPaymentFlowType.Gifting))
                }
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.sendGoldGiftLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                it?.let {
                    val buyPriceResponse = viewModel.buyPriceLiveData.value?.data?.data
                    if (buyPriceResponse != null) {
                        when (BuyGoldRequestType.valueOf(args.sendGiftRequest.buyGoldRequestType!!)) {
                            BuyGoldRequestType.AMOUNT -> {
                                val buyGoldByAmountRequest = BuyGoldByAmountRequest(
                                    amount = args.sendGiftRequest.amount.orZero(),
                                    fetchCurrentGoldPriceResponse = buyPriceResponse,
                                    giftingId = it.giftingId,
                                    paymentGateway = paymentManager.getCurrentPaymentGateway()
                                )
                                viewModel.buyGoldByAmount(buyGoldByAmountRequest)
                            }

                            BuyGoldRequestType.VOLUME -> {
                                val buyGoldByVolumeRequest = BuyGoldByVolumeRequest(
                                    volume = args.sendGiftRequest.volume.orZero(),
                                    fetchCurrentGoldPriceResponse = buyPriceResponse,
                                    giftingId = it.giftingId,
                                    paymentGateway = paymentManager.getCurrentPaymentGateway()
                                )
                                viewModel.buyGoldByVolume(buyGoldByVolumeRequest)
                            }

                            else -> {
                                //Do Nothing..
                            }
                        }
                    }
                }
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnPayNow.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Clicked_Button_GiftGoldFlow,
                mapOf(
                    EventKey.amount to args.sendGiftRequest.amount.toString(),
                    EventKey.quantity to args.sendGiftRequest.volume.toString(),
                    EventKey.receiverDetails to args.sendGiftRequest.receiverName.toString(),
                    EventKey.buttonType to EventKey.payNow
                )
            )
            viewModel.sendGift(args.sendGiftRequest)
        }
    }

}