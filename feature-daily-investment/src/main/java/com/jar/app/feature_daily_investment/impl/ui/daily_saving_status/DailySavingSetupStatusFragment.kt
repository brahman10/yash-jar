package com.jar.app.feature_daily_investment.impl.ui.daily_saving_status

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.event.UpdateGoalBasedSavingCard
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.dp
import com.jar.app.base.util.mask
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_analytics.EventKey.DSSetup_ShownSuccessAnimation
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.domain.event.SetupAutoPayEvent
import com.jar.app.feature_daily_investment.api.util.EventKey.GoToHome
import com.jar.app.feature_daily_investment.api.util.EventKey.Home
import com.jar.app.feature_daily_investment.api.util.EventKey.Okay
import com.jar.app.feature_daily_investment.api.util.EventKey.Onboarding
import com.jar.app.feature_daily_investment.api.util.EventKey.Retry
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentFragmentDailySavingSetupStatusBinding
import com.jar.app.feature_daily_investment.impl.data.DailySavingSetupStatusData
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.FetchMandatePaymentStatusResponse
import com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus
import com.jar.app.feature_weekly_magic_common.shared.domain.model.WeeklyChallengeMetaData
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingSetupStatusFragment :
    BaseFragment<FeatureDailyInvestmentFragmentDailySavingSetupStatusBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentFragmentDailySavingSetupStatusBinding
        get() = FeatureDailyInvestmentFragmentDailySavingSetupStatusBinding::inflate

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi
    private val labelAndValueAdapter = LabelAndValueAdapter()
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 9.dp)
    private var shouldShowPostSetupData = false
    private var authWorkFlowType: String? = null

    private val viewModelProvider by viewModels<DailySavingSetupStatusViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    companion object {
        private const val DailySavingSetupStatusFragment = "DailySavingSetupStatusFragment"
    }

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigate()
            isEnabled = false
        }
    }

    private val args by navArgs<DailySavingSetupStatusFragmentArgs>()

    private val fetchAutoInvestStatusResponse by lazy {
        serializer.decodeFromString<FetchMandatePaymentStatusResponse>(
            decodeUrl(args.fetchAutoInvestStatusResponse)
        )
    }

    private val mandatePaymentResultFromSDK by lazy {
        serializer.decodeFromString<com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK>(
            decodeUrl(args.mandatePaymentResultFromSDK)
        )
    }

    private val dailySavingSetupStatusData by lazy {
        serializer.decodeFromString<DailySavingSetupStatusData>(
            decodeUrl(args.dailySavingSetupStatusData)
        )
    }

    private val tutorialAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {}
        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) {
            binding.dailySavingTutorialLayout.slideToRevealNew(binding.layoutSuccess.root)
        }
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        EventBus.getDefault().post(RefreshDailySavingEvent())
        setupUI()
        setupListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        dismissProgressBar()
        fetchAutoInvestStatusResponse.let {
            when (it.getAutoInvestStatus()) {
                com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.SUCCESS -> setSuccessViews()
                com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.PENDING -> setPendingViews()
                com.jar.app.feature_mandate_payments_common.shared.domain.model.verify_status.MandatePaymentProgressStatus.FAILURE -> setFailureViews()
            }
            analyticsHandler.postEvent(
                EventKey.Shown_PostOrderScreen_DailySavings_DailySavings, mapOf(
                    EventKey.KEY to it.getAutoInvestStatus().name,
                    DailySavingsEventKey.FromScreen to dailySavingSetupStatusData.flowName.toString(),
                    DailySavingsEventKey.UserLifecycle to dailySavingSetupStatusData.userLifecycle.toString(),
                    DailySavingsEventKey.UpiApp to it.provider.orEmpty(),
                )
            )
        }
    }

    private fun setSuccessViews() {
        viewModel.fetchUserDSDetails()

        fetchAutoInvestStatusResponse.autoPaySuccessData?.lottie?.let { lottieUrl ->
            binding.successLoadingLottie.apply {

                analyticsHandler.postEvent(DSSetup_ShownSuccessAnimation)

                playLottieWithUrlAndExceptionHandling(requireContext(), lottieUrl)
                addAnimatorListener(tutorialAnimationListener)
            }
        }

        binding.layoutSuccess.tvTitle.text = fetchAutoInvestStatusResponse.title
            ?: if (dailySavingSetupStatusData.flowName.orEmpty() == MandatePaymentEventKey.FeatureFlows.UpdateDailySaving) getString(
                R.string.feature_daily_investment_setting_yay_daily_saving_updated_successfully
            )
            else getString(R.string.feature_daily_investment_daily_investment_setup_successfully)
        binding.layoutSuccess.successLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(), BaseConstants.LottieUrls.SMALL_CHECK
        )
        binding.layoutSuccess.lottieCelebration.playLottieWithUrlAndExceptionHandling(
            requireContext(), BaseConstants.LottieUrls.CONFETTI_FROM_TOP
        )
        binding.layoutSuccess.lottieCelebration.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {}
            override fun onAnimationCancel(p0: Animator) {}
            override fun onAnimationRepeat(p0: Animator) {}
            override fun onAnimationEnd(p0: Animator) {
                if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    binding.layoutSuccess.lottieCelebration.isVisible = false
                }
            }
        })
        createListAndSetAdapterData()
    }

    private fun createListAndSetAdapterData() {
        binding.layoutSuccess.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.layoutSuccess.rvDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.layoutSuccess.rvDetails.adapter = labelAndValueAdapter
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        val localStartDate =
            Instant.ofEpochMilli(fetchAutoInvestStatusResponse.startDate?.toLong() ?: 0)
                .atOffset(ZoneOffset.UTC)
        val list = ArrayList<LabelAndValue>()
        if (fetchAutoInvestStatusResponse.provider.isNullOrEmpty().not()) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_upi_app),
                fetchAutoInvestStatusResponse.provider.orEmpty(),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )

        if (fetchAutoInvestStatusResponse.upiId.isNullOrEmpty()) list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_subscription_id),
                fetchAutoInvestStatusResponse.subscriptionId.orEmpty().mask(7, 5),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        else list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_upi_id),
                fetchAutoInvestStatusResponse.upiId.orEmpty(),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )

        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_per_day_limit),
                getString(
                    com.jar.app.core_ui.R.string.core_ui_rs_x_int,
                    dailySavingSetupStatusData.dailySavingAmount.toInt()
                ),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )

        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_frequency),
                "${fetchAutoInvestStatusResponse.recurringFrequency}",
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )

        fetchAutoInvestStatusResponse.bankLogo?.let {
            binding.layoutSuccess.ivBankLogo.isVisible = true
            Glide.with(binding.root).load(it).into(binding.layoutSuccess.ivBankLogo)
        }
        fetchAutoInvestStatusResponse.bankName?.let {
            binding.layoutSuccess.tvBankName.isVisible = true
            binding.layoutSuccess.tvBankName.text = it
        }

        binding.layoutSuccess.tvBankAccount.isVisible =
            fetchAutoInvestStatusResponse.bankLogo.isNullOrEmpty()
                .not() || fetchAutoInvestStatusResponse.bankName.isNullOrEmpty().not()

        list.add(
            LabelAndValue(
                getString(com.jar.app.core_ui.R.string.core_ui_start_date),
                localStartDate.format(formatter),
                labelTextStyle = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle,
                valueTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
        )
        labelAndValueAdapter.submitList(list)
    }

    private fun setPendingViews() {
        binding.layoutPending.root.isVisible = true
        binding.layoutPending.tvTitle.text = fetchAutoInvestStatusResponse.title
            ?: getString(R.string.feature_daily_investment_updating_ds_in_progress)
        binding.layoutPending.tvDescription.text = fetchAutoInvestStatusResponse.description
            ?: getString(R.string.feature_daily_investment_it_is_taking_little_longer)
    }

    private fun setFailureViews() {
        viewModel.isAutoPayResetRequired(dailySavingSetupStatusData.dailySavingAmount)
        binding.layoutFailure.root.isVisible = true
        binding.layoutFailure.root.isVisible = true
        binding.layoutFailure.tvTitle.text = fetchAutoInvestStatusResponse.title
            ?: getString(R.string.feature_daily_investment_updating_ds_failed)
        binding.layoutFailure.tvDescription.text = fetchAutoInvestStatusResponse.description
            ?: getString(R.string.feature_daily_investment_any_amount_debited_will_be_credited)
    }

    private fun setupListener() {
        setSuccessClickListener()
        setFailureClickListener()
        setPendingClickListener()
    }

    private fun setSuccessClickListener() {
        binding.layoutSuccess.btnDSSetupAction.setDebounceClickListener {
            if (shouldShowPostSetupData) {
                analyticsHandler.postEvent(
                    DailySavingsEventKey.DS_Setup_ScreenClicked,
                    mapOf(
                        DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                        DailySavingsEventKey.ButtonType to DailySavingsEventKey.TrackMySavings,
                        DailySavingsEventKey.Status to MandatePaymentProgressStatus.SUCCESS.name
                    )
                )
                val fromScreen = EventKey.UserLifecycles.DS_Setup_Success
                prefs.setUserLifeCycleForMandate(fromScreen)
                EventBus.getDefault()
                    .post(
                        HandleDeepLinkEvent(
                            BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.POST_SETUP_DETAILS,
                            fromScreen = fromScreen
                        )
                    )
                navigate(shouldNavigateToHome = false)
            } else {
                analyticsHandler.postEvent(
                    DailySavingsEventKey.DS_Setup_ScreenClicked,
                    mapOf(
                        DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                        DailySavingsEventKey.ButtonType to DailySavingsEventKey.GoToHome,
                        DailySavingsEventKey.Status to MandatePaymentProgressStatus.SUCCESS.name
                    )
                )
                navigate(true)
            }
        }

        binding.layoutSuccess.btnGoToHomePage.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.GoToHome,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.SUCCESS.name
                )
            )
            EventBus.getDefault().post(
                UpdateGoalBasedSavingCard()
            )
            navigate(true)
        }
    }

    private fun setFailureClickListener() {
        binding.layoutFailure.tvContactSupport.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.ContactSupport,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.FAILURE.name
                )
            )
            val number = remoteConfigManager.getWhatsappNumber()
            val message = getString(R.string.feature_daily_investment_message_auto_invest_failure)
            it.context.openWhatsapp(number, message)
        }

        binding.layoutFailure.btnGoToHome.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.GoToHome,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.FAILURE.name
                )
            )
            navigate(shouldNavigateToHome = true)
        }

        binding.layoutFailure.btnRetry.setDebounceClickListener {
            analyticsHandler.postEvent(com.jar.app.feature_daily_investment.api.util.EventKey.Clicked_Retry_DailySavingFailureScreen)
            analyticsHandler.postEvent(
                com.jar.app.feature_daily_investment.api.util.EventKey.Clicked_Button_DailySavings,
                mapOf(
                    com.jar.app.feature_daily_investment.api.util.EventKey.buttonType to Retry,
                    com.jar.app.feature_daily_investment.api.util.EventKey.fromScreen to if (dailySavingSetupStatusData.isFromOnboarding) Onboarding else Home
                )
            )
            if (dailySavingSetupStatusData.isMandateBottomSheetFlow.orFalse()) {
                dailyInvestmentApi.openDailySavingFlow(
                    fromSettingsFlow = false,
                    featureFlowData = FeatureFlowData(
                        fromScreen = BaseConstants.ScreenFlowType.PRE_DAILY_SAVING_SETUP_SCREEN.name
                    ),
                    popUpToId = R.id.dailySavingSetupStatusFragment
                )
            } else {
                EventBus.getDefault().post(
                    SetupAutoPayEvent(
                        isDailySavingAutoPayFlow = true,
                        shouldDirectlyShowAppSelectionScreen = true,
                        newDailySavingAmount = dailySavingSetupStatusData.dailySavingAmount,
                        flowName = MandatePaymentEventKey.FeatureFlows.RetryDailySaving,
                        authWorkFlowType = authWorkFlowType
                    )
                )
            }
        }
    }

    private fun setPendingClickListener() {
        binding.layoutPending.tvContactSupport.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.ContactSupport,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.FAILURE.name
                )
            )

            val number = remoteConfigManager.getWhatsappNumber()
            val message = getString(R.string.feature_daily_investment_message_auto_invest_pending)
            it.context.openWhatsapp(number, message)
        }

        binding.layoutPending.btnGoToHome.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DS_Setup_ScreenClicked,
                mapOf(
                    DailySavingsEventKey.DailySavingsAmount to dailySavingSetupStatusData.dailySavingAmount,
                    DailySavingsEventKey.ButtonType to DailySavingsEventKey.GoToHome,
                    DailySavingsEventKey.Status to MandatePaymentProgressStatus.FAILURE.name
                )
            )
            navigate(shouldNavigateToHome = true)
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.mandateStatusFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = { dismissProgressBar() },
                    onSuccessWithNullData = { dismissProgressBar() },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isAutoPayResetRequiredFlow.collect(
                    onSuccess = {
                        authWorkFlowType = it.authWorkflowType
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dailySavingDetailFlow.collect(
                    onSuccess = {
                        shouldShowPostSetupData = it.savingsMetaData?.showPostSetupData.orFalse()
                        binding.layoutSuccess.btnGoToHomePage.isVisible =
                            it.savingsMetaData?.showPostSetupData.orFalse()
                        binding.layoutSuccess.btnDSSetupAction.setText(
                            getString(
                                if (shouldShowPostSetupData) R.string.feature_daily_investment_track_my_savings
                                else com.jar.app.core_ui.R.string.core_ui_go_to_homepage
                            )
                        )
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.weeklyChallengeMetaFlow.collect(
                    onSuccess = {
                        it?.let { setPrefData(it) }
                    }
                )
            }
        }
    }

    private fun setPrefData(data: WeeklyChallengeMetaData) {
        data.let {
            if (it.cardsWon.orZero() > prefs.getWonMysteryCardCount() || it.challengeId != prefs.getWonMysteryCardChallengeId()) {
                prefs.setWonMysteryCardCount(it.cardsWon.orZero())
                prefs.setWonMysteryCardChallengeId(it.challengeId ?: "")
            }
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun navigate(shouldNavigateToHome: Boolean = false) {
        if (dailySavingSetupStatusData.isFromOnboarding || shouldNavigateToHome) {
            analyticsHandler.postEvent(
                com.jar.app.feature_daily_investment.api.util.EventKey.Clicked_Button_DailySavings,
                mapOf(
                    com.jar.app.feature_daily_investment.api.util.EventKey.buttonType to GoToHome,
                    com.jar.app.feature_daily_investment.api.util.EventKey.fromScreen to Onboarding
                )
            )
            navigateTo(
                uri = BaseConstants.InternalDeepLinks.HOME,
                popUpTo = R.id.dailySavingSetupStatusFragment,
                inclusive = true
            )
        } else {
            popBackStack(R.id.dailySavingSetupStatusFragment, inclusive = true)
            analyticsHandler.postEvent(
                com.jar.app.feature_daily_investment.api.util.EventKey.Clicked_Button_DailySavings,
                mapOf(
                    com.jar.app.feature_daily_investment.api.util.EventKey.buttonType to Okay,
                    com.jar.app.feature_daily_investment.api.util.EventKey.fromScreen to Home
                )
            )
        }
    }

    override fun onDestroyView() {
        binding.successLoadingLottie.removeAnimatorListener(tutorialAnimationListener)
        super.onDestroyView()
    }
}