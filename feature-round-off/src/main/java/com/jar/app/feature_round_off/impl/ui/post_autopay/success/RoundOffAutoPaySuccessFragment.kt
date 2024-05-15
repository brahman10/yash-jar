package com.jar.app.feature_round_off.impl.ui.post_autopay.success

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.dynamic_cards.DynamicEpoxyController
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentAutopaySuccessBinding
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class RoundOffAutoPaySuccessFragment :
    BaseFragment<FeatureRoundOffFragmentAutopaySuccessBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentAutopaySuccessBinding
        get() = FeatureRoundOffFragmentAutopaySuccessBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    private var roundOffAmount = 0
    private var controller: DynamicEpoxyController? = null

    private val viewModel: RoundOffAutoPaySuccessViewModel by viewModels()

    private val fetchMandatePaymentStatusResponse by lazy {
        val decoded = decodeUrl(args.fetchMandatePaymentStatusResponse)
        serializer.decodeFromString<FetchMandatePaymentStatusResponse>(decoded)
    }

    private val args: RoundOffAutoPaySuccessFragmentArgs by navArgs()

    private val labelAndValueAdapter = LabelAndValueAdapter()

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 9.dp)

    companion object {
        const val RoundOffAutoPaySuccessFragment = "RoundOffAutoPaySuccessFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun getData(){
        dismissProgressBar()
        viewModel.fetchOrderStatusDynamicCards()
        viewModel.fetchInitialRoundOffsData()
    }

    private fun setupUI() {
        binding.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvDetails.adapter = labelAndValueAdapter

        analyticsHandler.postEvent(
            RoundOffEventKey.AutomaticRoundoff_SuccessScreen,
            mapOf(RoundOffEventKey.Action to RoundOffEventKey.Shown)
        )
        binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.CONFETTI_FROM_TOP
        )
        binding.successLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            BaseConstants.LottieUrls.RUPEE_POST_PURCHASE_SUCCESS
        )
        binding.lottieCelebration.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                binding.lottieCelebration.isVisible = false
            }
        })

    }

    private fun createListAndSetAdapterData() {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        val localStartDate =
            Instant.ofEpochMilli(fetchMandatePaymentStatusResponse.startDate?.toLong() ?: 0)
                .atOffset(ZoneOffset.UTC)
        val list = ArrayList<LabelAndValue>()
        if (fetchMandatePaymentStatusResponse.provider.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                    fetchMandatePaymentStatusResponse.provider.orEmpty(),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )

        if (fetchMandatePaymentStatusResponse.upiId.isNullOrEmpty())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_subscription_id),
                    fetchMandatePaymentStatusResponse.subscriptionId.orEmpty().mask(7, 5),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        else
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                    fetchMandatePaymentStatusResponse.upiId.orEmpty(),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )

        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_amount_slash_frequency),
                "${roundOffAmount}/${fetchMandatePaymentStatusResponse.recurringFrequency}",
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        if ((fetchMandatePaymentStatusResponse.bankLogo
                ?: fetchMandatePaymentStatusResponse.bankName).isNullOrEmpty().not()
        )
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_bank_account),
                    fetchMandatePaymentStatusResponse.bankLogo
                        ?: fetchMandatePaymentStatusResponse.bankName.orEmpty(),
                    isTextualValue = fetchMandatePaymentStatusResponse.bankLogo.isNullOrEmpty(),
                    labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                    valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
            )
        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_start_date), localStartDate.format(formatter),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        labelAndValueAdapter.submitList(list)
    }

    private fun setupListener() {
        binding.btnGoToHome.setDebounceClickListener {
            analyticsHandler.postEvent(
                RoundOffEventKey.AutomaticRoundoff_SuccessScreen,
                mapOf(RoundOffEventKey.Action to RoundOffEventKey.GoToHomeClicked)
            )
            EventBus.getDefault().post(GoToHomeEvent(RoundOffAutoPaySuccessFragment))
        }
    }

    private fun observeLiveData() {
        viewModel.dynamicCardsLiveData.observe(viewLifecycleOwner) {
            binding.dynamicRecyclerView.isVisible = true
            controller?.cards = it
            binding.dynamicRecyclerView.invalidateItemDecorations()
        }
        viewModel.initialRoundOffLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                roundOffAmount =
                    it?.mandateAmount?.toInt() ?: remoteConfigManager.getRoundOffAmount()
                createListAndSetAdapterData()
            },
            onSuccessWithNullData = {
                roundOffAmount = remoteConfigManager.getRoundOffAmount()
                createListAndSetAdapterData()
            },
        )
    }
}