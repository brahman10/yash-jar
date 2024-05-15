package com.jar.app.feature_gold_sip.impl.ui.post_autopay_setup.success

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
import com.jar.app.feature_gold_sip.api.GoldSipApi
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentAutopaySuccessBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldSipAutoPaySuccessFragment :
    BaseFragment<FeatureGoldSipFragmentAutopaySuccessBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentAutopaySuccessBinding
        get() = FeatureGoldSipFragmentAutopaySuccessBinding::inflate

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var goldSipApi: GoldSipApi

    private var setupDate = ""
    private var controller: DynamicEpoxyController? = null

    private val fetchMandatePaymentStatusResponse by lazy {
        val decoded = decodeUrl(args.fetchMandatePaymentStatusResponse)
        serializer.decodeFromString<FetchMandatePaymentStatusResponse>(decoded)
    }

    private val postSetupSipData by lazy {
        val decoded = decodeUrl(args.postSetupSipData)
        serializer.decodeFromString<PostSetupSipData>(decoded)
    }

    private val sipSubscriptionType by lazy {
        com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.valueOf(postSetupSipData.sipSubscriptionType.uppercase())
    }

    private val args: GoldSipAutoPaySuccessFragmentArgs by navArgs()

    private val labelAndValueAdapter = LabelAndValueAdapter()

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 9.dp)

    companion object {
        const val GoldSipAutoPaySuccessFragment = "GoldSipAutoPaySuccessFragment"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
    }

    private fun setupUI() {
        dismissProgressBar()
        binding.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvDetails.adapter = labelAndValueAdapter
        createListAndSetAdapterData()
        setViewsAccordingToIsSetupFlow()

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

        if (fetchMandatePaymentStatusResponse.bankLogo.isNullOrEmpty()) {
            binding.ivBankLogo.isVisible = false
            binding.tvBankName.isVisible = true
            binding.tvBankName.text = fetchMandatePaymentStatusResponse?.bankName
        } else {
            binding.ivBankLogo.isVisible = true
            binding.tvBankName.isVisible = false
            Glide.with(requireContext()).load(fetchMandatePaymentStatusResponse?.bankLogo)
                .into(binding.ivBankLogo)
        }

        binding.tvBankAccount.isVisible = (fetchMandatePaymentStatusResponse.bankLogo
            ?: fetchMandatePaymentStatusResponse?.bankName).isNullOrEmpty().not()
        analyticsHandler.postEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen, mapOf(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse.getAutoInvestStatus().name,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(sipSubscriptionType.textRes),
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse.recurringAmount.orZero(),
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to setupDate,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.FromFlow to if (postSetupSipData?.isSetupFlow.orFalse()) com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupFlow else com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.UpdateFlow
            )
        )
    }

    private fun setViewsAccordingToIsSetupFlow() {
//        binding.dynamicRecyclerView.isVisible = postSetupSipData.isSetupFlow
        if (postSetupSipData.isSetupFlow) {
//            viewModel.fetchOrderStatusDynamicCards()
            binding.tvDescription.setPadding(30, 18, 30, 18)
            binding.tvDescription.background = ContextCompat.getDrawable(
                requireContext(),
                com.jar.app.core_ui.R.drawable.core_ui_bg_rounded_2e2942_10dp
            )
            binding.btnGoToHome.setText(getString(com.jar.app.core_ui.R.string.core_ui_go_to_home))
            binding.tvDescription.setTextColor(
                ContextCompat.getColorStateList(requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF)
            )
            binding.tvTitle.text = getCustomStringFormatted(
                GoldSipMR.strings.feature_gold_sip_yay_s_sip_setup_successfully,
                getCustomString(sipSubscriptionType.textRes)
            )
            when (sipSubscriptionType) {
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.WEEKLY_SIP -> {
                    binding.tvDescription.text =
                        getCustomStringFormatted(
                            GoldSipMR.strings.feature_gold_sip_rs_x_will_be_auto_saved_every_week_on_s,
                            fetchMandatePaymentStatusResponse?.recurringAmount?.toInt().orZero(),
                            postSetupSipData.subscriptionDay
                        )
                }
                com.jar.app.feature_gold_sip.shared.domain.model.SipSubscriptionType.MONTHLY_SIP -> {
                    binding.tvDescription.text =
                        getCustomStringFormatted(
                            GoldSipMR.strings.feature_gold_sip_rs_x_will_be_auto_saved_on_s_of_every_month,
                            fetchMandatePaymentStatusResponse?.recurringAmount?.toInt().orZero(),
                            postSetupSipData.sipDayValue.getDayOfMonthAndItsSuffix()
                        )
                }
            }
        } else {
            binding.tvDescription.background = null
            binding.tvDescription.setPadding(0, 0, 0, 0)
            binding.tvDescription.setTextColor(
                ContextCompat.getColorStateList(requireContext(), com.jar.app.core_ui.R.color.color_EBB46A)
            )
            binding.btnGoToHome.setText(getCustomString(GoldSipMR.strings.feature_gold_sip_great_thanks))
            binding.tvTitle.text =
                getCustomStringFormatted(GoldSipMR.strings.feature_gold_sip_yay_gold_sip_updated_successfully)
            binding.tvDescription.text =
                getCustomStringFormatted(
                    GoldSipMR.strings.feature_gold_sip_updated_amount_will_be_debited_from_date_s,
                    postSetupSipData?.nextDeductionDate.orEmpty()
                )
        }
    }

    private fun createListAndSetAdapterData() {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yy")

        val localStartDate =
            Instant.ofEpochMilli(fetchMandatePaymentStatusResponse.startDate?.toLong() ?: 0)
                .atOffset(ZoneOffset.UTC)
        setupDate = localStartDate.format(formatter)
        val list = ArrayList<LabelAndValue>()
        if (fetchMandatePaymentStatusResponse.provider.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                    fetchMandatePaymentStatusResponse.provider.orEmpty()
                )
            )

        if (fetchMandatePaymentStatusResponse.upiId.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                    fetchMandatePaymentStatusResponse.upiId.orEmpty()
                )
            )

        list.add(
            LabelAndValue(
                getCustomString(fetchMandatePaymentStatusResponse.getRecurringFrequency()),
                fetchMandatePaymentStatusResponse.recurringAmount.toString()
            )
        )
        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_start_date),
                postSetupSipData?.nextDeductionDate.orEmpty()
            )
        )
        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_frequency),
                fetchMandatePaymentStatusResponse.recurringFrequency?.capitaliseFirstChar()
                    .orEmpty()
            )
        )
        labelAndValueAdapter.submitList(list)
    }

    private fun setupListener() {
        binding.btnGoToHome.setDebounceClickListener {
            analyticsHandler.postEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIP_PostSetupScreen, mapOf(
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Action to com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Homepage,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SetupStatus to fetchMandatePaymentStatusResponse.getAutoInvestStatus().name,
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Frequency to getCustomString(sipSubscriptionType.textRes),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Amount to fetchMandatePaymentStatusResponse.recurringAmount.orZero(),
                    com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.SIP_Date to setupDate,
                )
            )
            if (postSetupSipData.isSetupFlow)
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        GoldSipAutoPaySuccessFragment,
                        BaseConstants.HomeBottomNavigationScreen.HOME
                    )
                )
            else
                goldSipApi.openGoldSipDetails(isUpdateFlow = true)
        }
    }

}