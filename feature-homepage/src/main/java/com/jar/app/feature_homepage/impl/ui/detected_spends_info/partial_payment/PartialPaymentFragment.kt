package com.jar.app.feature_homepage.impl.ui.detected_spends_info.partial_payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_homepage.shared.domain.event.detected_spends.InitiateDetectedRoundOffsPaymentEvent
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.databinding.FeatureHomepageFragmentPartialPaymentBinding
import com.jar.app.feature_one_time_payments.shared.data.model.ManualPaymentCompletedEvent
import com.jar.app.feature_user_api.domain.model.DetectedSpendsData
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PartialPaymentFragment :
    BaseFragment<FeatureHomepageFragmentPartialPaymentBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<PartialPaymentFragmentArgs>()

    private val viewModel by viewModels<PartialPaymentFragmentViewModel> { defaultViewModelProviderFactory }

    private val detectedSpendData by lazy {
        serializer.decodeFromString<DetectedSpendsData>(
            decodeUrl(args.detectedSpendsData)
        )
    }

    private val adapter by lazy {
        PartialPaymentAdapter(detectedSpendData.fullPaymentInfo?.txnAmt.orZero()) {
            EventBus.getDefault().post(
                com.jar.app.feature_homepage.shared.domain.event.detected_spends.InitiateDetectedRoundOffsPaymentEvent(
                    com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest(
                        txnAmt = it.amount!!,
                        orderId = detectedSpendData.fullPaymentInfo?.orderId!!,
                        percent = it.percentage,
                        isPartial = true
                    )
                )
            )
            analyticsHandler.postEvent(
                EventKey.CLICKED_OPTION_PARTIAL_PAYMENT_SCREEN, hashMapOf(
                    EventKey.OPTION_SELECTED to it.percentage?.toString().orEmpty(),
                    EventKey.AMOUNT to detectedSpendData.fullPaymentInfo?.txnAmt!!
                )
            )
        }
    }

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(20.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFragmentPartialPaymentBinding
        get() = FeatureHomepageFragmentPartialPaymentBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(
                UpdateAppBarEvent(
                    AppBarData(ToolbarDefault(title = getString(R.string.feature_homepage_invest_partial_amount)))
                )
            )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        OverScrollDecoratorHelper.setUpOverScroll(binding.root)
        binding.tvAmount.text =
            getString(
                R.string.feature_homepage_rupee_x_in_double,
                detectedSpendData.fullPaymentInfo?.txnAmt
            )

        binding.tvTitle.text = detectedSpendData.partPaymentInfo?.title
        binding.tvTitle.isVisible = !detectedSpendData.partPaymentInfo?.title.isNullOrBlank()

        binding.tvDescription.text = detectedSpendData.partPaymentInfo?.description
        binding.tvDescription.isVisible =
            !detectedSpendData.partPaymentInfo?.description.isNullOrBlank()

        binding.btnSkipInvestment.isVisible =
            detectedSpendData.partPaymentInfo?.skipAvailable.orFalse()

        binding.tvSkipInfo.text = detectedSpendData.partPaymentInfo?.skipInfo
        binding.tvSkipInfo.isVisible =
            !detectedSpendData.partPaymentInfo?.skipInfo.isNullOrBlank()

        binding.rvPartPaymentOptions.layoutManager = LinearLayoutManager(context)
        binding.rvPartPaymentOptions.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvPartPaymentOptions.adapter = adapter
    }

    private fun setupListeners() {

        binding.tvAmount.setDebounceClickListener {
//            navigateTo(HomeNavigationDirections.actionToUserGoldBreakdownFragment())
        }

        binding.btnFullPayment.setDebounceClickListener {
            EventBus.getDefault().post(
                com.jar.app.feature_homepage.shared.domain.event.detected_spends.InitiateDetectedRoundOffsPaymentEvent(
                    com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest(
                        txnAmt = detectedSpendData.fullPaymentInfo?.txnAmt!!,
                        orderId = detectedSpendData.fullPaymentInfo?.orderId!!
                    )
                )
            )
            analyticsHandler.postEvent(
                EventKey.CLICKED_OPTION_PARTIAL_PAYMENT_SCREEN, hashMapOf(
                    EventKey.OPTION_SELECTED to "Invest Now (100%)",
                    EventKey.AMOUNT to detectedSpendData.fullPaymentInfo?.txnAmt!!
                )
            )
        }

        binding.btnSkipInvestment.setDebounceClickListener {
            viewModel.skipPayment(
                detectedSpendData.fullPaymentInfo?.txnAmt!!,
                detectedSpendData.fullPaymentInfo?.orderId!!
            )
            analyticsHandler.postEvent(
                EventKey.CLICKED_OPTION_PARTIAL_PAYMENT_SCREEN, hashMapOf(
                    EventKey.OPTION_SELECTED to "Skip this investment",
                    EventKey.AMOUNT to 0
                )
            )
        }

    }

    private fun observeLiveData() {
        adapter.submitList(detectedSpendData.partPaymentInfo?.paymentOptions)

        viewModel.skipPaymentLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                EventBus.getDefault()
                    .postSticky(ManualPaymentCompletedEvent(ManualPaymentStatus.SUCCESS))
                popBackStack()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                EventBus.getDefault()
                    .postSticky(ManualPaymentCompletedEvent(ManualPaymentStatus.SUCCESS))
                popBackStack()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }
}