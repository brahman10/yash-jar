package com.jar.app.feature_lending.impl.ui.host_container

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingAadharVerificationDoneEvent
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingKycCompletedEventV2
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.PanVerificationDoneEvent
import com.jar.app.base.data.event.RefreshLendingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentHostContainerBinding
import com.jar.app.feature_lending.impl.domain.event.LendingNavigateToRepaymentFlow
import com.jar.app.feature_lending.impl.domain.event.LendingNavigateToSellGoldEvent
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.experiment.ReadyCashScreen
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

@AndroidEntryPoint
internal class LendingHostContainerFragment :
    BaseFragment<FeatureLendingFragmentHostContainerBinding>() {
    private lateinit var navController: NavController

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var kycApi: LendingKycApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args by navArgs<LendingHostContainerFragmentArgs>()

    private val viewModelProvider: LendingHostViewModelAndroid by activityViewModels { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var readyCashNavigationEvent: ReadyCashNavigationEvent? = null

    private var isPanNavigationPending = false
    private var isLendingJourneyDataFetched = false
    private var isFirstTimeNavigationDone = false
    private var aadhaarLaunchType = "ckyc_not_found"

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentHostContainerBinding
        get() = FeatureLendingFragmentHostContainerBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchPreApprovedData()
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
        setClickListener()
        getData()
    }

    private fun getData() {
        viewModel.fetchReadyCashJourney()
    }

    private fun setupUI() {
        binding.lendingToolbar.setTitle(getCustomString(MR.strings.feature_lending_loan_application))
        setStatusBarColor(com.jar.app.core_ui.R.color.color_322C48)
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = nestedNavHostFragment.navController
        navController.graph =
            nestedNavHostFragment.navController.navInflater.inflate(R.navigation.lending_steps_navigation)
    }

    private fun observeFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.toolbarItemsFlow.collect {
                    binding.lendingToolbar.addSteps(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.readyCashJourneyFlow.collect(
                    onLoading = {
                        binding.somethingWentWrongHolder.isVisible = false
                        if (isLendingJourneyDataFetched.not()) {
                            binding.shimmerPlaceholder.shimmerLayout.isVisible = true
                            binding.shimmerPlaceholder.shimmerLayout.startShimmer()
                        } else {
                            showProgressBar()
                        }
                    },
                    onSuccess = {
                        dismissProgressBar()
                        binding.somethingWentWrongHolder.isVisible = false
                        binding.shimmerPlaceholder.shimmerLayout.stopShimmer()
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = false

                        it?.let {
                            isLendingJourneyDataFetched = true
                            viewModel.readyCashJourney = it
                            it.progressBar?.let {
                                viewModel.createLendingProgress(it)
                            }
                            navigateAsPerExperiment(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        binding.shimmerPlaceholder.shimmerLayout.stopShimmer()
                        binding.shimmerPlaceholder.shimmerLayout.isVisible = false
                        binding.somethingWentWrongHolder.isVisible = true

                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loanDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (isPanNavigationPending) {
                            it?.applicationDetails?.pan?.let {
                                navigateToPanAlreadyPresentScreen(
                                    CreditReportPAN(
                                        it.panNo.orEmpty(),
                                        it.firstName.orEmpty(),
                                        it.lastName.orEmpty(),
                                        it.dob.orEmpty()
                                    )
                                )
                            }
                            isPanNavigationPending = false
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun navigateAsPerExperiment(
        data: ReadyCashJourney,
        screenName: String = readyCashNavigationEvent?.whichScreen ?: (
                data.currentScreen ?: ReadyCashScreen.LANDING_SCREEN_NEW
                ),
        popupToId: Int? = readyCashNavigationEvent?.popupToId,
        isBackFlow: Boolean = readyCashNavigationEvent?.isBackFlow.orFalse()
    ) {
        Timber.d("MK:: navigating to -> $screenName")
        viewModel.currentScreen = screenName
        val screenData = data.screenData?.get(screenName)
        var shouldShowToolbar = screenData?.shouldShowProgress.orFalse()
        val argsData = encodeUrl(
            serializer.encodeToString(
                ReadyCashScreenArgs(
                    loanId = data.applicationId,
                    source = args.source,
                    type = data.type.orEmpty(),
                    lender = data.lender,
                    screenName = screenName,
                    screenData = screenData,
                    isRepeatWithdrawal = data.repeatLoanEnabled,
                    isRepayment = readyCashNavigationEvent?.isRepaymentFlow.orFalse()
                )
            )
        )
        if (isFirstTimeNavigationDone.not()) {
            analyticsApi.setUserProperty(
                listOf(
                    LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to data.type.orEmpty(),
                    LendingEventKeyV2.lenderName to data.lender.orEmpty()
                )
            )
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_FlowOpen,
                mapOf(
                    LendingEventKeyV2.source to args.source,
                    LendingEventKeyV2.screen_name to screenName,
                    LendingEventKeyV2.lenderName to data.lender.orEmpty(),
                    LendingEventKeyV2.LENDING_EXPERIMENT_TYPE to data.type.orEmpty()
                )
            )
        }
        //Handle special cases like sell gold flow, repeat loan, down time ..
        val uri = if (args.source == BaseConstants.LendingFlowType.SELL_GOLD
            && isFirstTimeNavigationDone.not()
        ) {
            shouldShowToolbar = false
            "android-app://com.jar.app/sellGoldLandingFragment/$argsData"
        } else if (data.appUnderMaintenance) {
            shouldShowToolbar = false
            "android-app://com.jar.app/partnerDownTimeFragment/$argsData"
        } else if (data.repeatLoanEnabled
            && isFirstTimeNavigationDone.not()
        ) {
            shouldShowToolbar = false
            "android-app://com.jar.app/lendingRepeatWithdrawal/$argsData"
        } else {
            ReadyCashScreen.getScreenNavigationUri(screenName, argsData, screenData?.status)
        }
        shouldShowToolbar(shouldShowToolbar)
        //Skip forward logic
        if (isBackFlow.not() && screenData?.status == LoanStatus.VERIFIED.name && screenData.skipForward) {
            navigateToNext(screenData.nextScreen)
            return
        }
        isFirstTimeNavigationDone = true
        //Cases like PAN, AADHAR, SELFIE, Final screen and application rejection handling
        when (uri) {
            ReadyCashScreen.PAN -> {
                if (screenData?.status == LoanStatus.IN_PROGRESS.name) {
                    isPanNavigationPending = true
                    viewModel.fetchLoanDetails(
                        LendingConstants.LendingApplicationCheckpoints.KYC, false
                    )
                } else if (screenData?.status == LoanStatus.VERIFIED.name) {
                    navigateToNext(screenData.nextScreen)
                } else {
                    navigateToPanFetch(data.lender)
                }

            }

            ReadyCashScreen.AADHAAR -> {
                if (screenData?.status == LoanStatus.VERIFIED.name) {
                    //since Aadhar is already verified move to next step
                    navigateToNext(screenData.nextScreen)
                } else {
                    navigateToAadharFlow(data.applicationId)
                }
            }

            ReadyCashScreen.SELFIE -> {
                if (screenData?.status == LoanStatus.VERIFIED.name) {
                    navigateToNext(screenData.nextScreen)
                } else {
                    navigateToSelfieFlow(data.applicationId)
                }
            }

            ReadyCashScreen.BANK_VERIFICATION, ReadyCashScreen.BANK_VERIFICATION_V2 -> {
                if (screenData?.status == LoanStatus.FAILED.name) {
                    navigateToApplicationRejected(data.lender)
                } else {
                    navigateToUri(uri, popupToId, isBackFlow)
                }
            }

            ReadyCashScreen.DISBURSAL -> {
                navigateToFinalDetailScreen(argsData)
            }

            else -> navigateToUri(uri, popupToId, isBackFlow)
        }
    }

    private fun navigateToApplicationRejected(lender:String?) {
        navController.navigate(
            LendingStepsNavigationDirections.actionGlobalBankApplicationRejectedFragment(lender),
            getNavOptions(
                shouldAnimate = true
            )
        )
    }

    private fun navigateToNext(nextScreen: String) {
        viewModel.readyCashJourney?.let {
            isFirstTimeNavigationDone = true
            Timber.d("MK:: navigating next -> $nextScreen")
            navigateAsPerExperiment(it, nextScreen)
        }
    }

    private fun navigateToPrevious(previousScreen: String, popupToId: Int? = null) {
        Timber.d("MK:: navigating previousScreen -> $previousScreen")
        if (previousScreen == ReadyCashScreen.HOME_SCREEN) {
            EventBus.getDefault().post(RefreshLendingEvent())
            popBackStack()
        }else{
            viewModel.readyCashJourney?.let {
                navigateAsPerExperiment(
                    data = it,
                    screenName = previousScreen,
                    popupToId = popupToId,
                    isBackFlow = true
                )
            }
        }
    }

    private fun navigateToUri(
        uri: String,
        @IdRes popupToId: Int? = null,
        isBackFlow: Boolean = false
    ) {
        Timber.d("MK:: navigating -> $uri")
        navController.navigate(
            Uri.parse(uri),
            getNavOptions(
                shouldAnimate = true,
                showBackwardNavigationAnimation = isBackFlow,
                popUpToId = popupToId,
                inclusive = true
            )
        )
    }

    private fun shouldShowToolbar(shouldShow: Boolean = false) {
        binding.lendingToolbar.isVisible = shouldShow
        setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
    }


    private fun navigateToPanAlreadyPresentScreen(creditReportPAN: CreditReportPAN?) {
        kycApi.openCreditReportFetchedScreen(
            creditReportPAN,
            BaseConstants.FROM_LENDING,
            navController,
            KycFeatureFlowType.LENDING
        )
    }

    private fun navigateToPanFetch(lenderName:String?) {
        kycApi.openPANFetchFlow(
            fromScreen = lenderName.orEmpty(),
            kycFeatureFlowType = KycFeatureFlowType.LENDING,
            shouldOpenPanInBackground = true,
            childNavController = navController
        ) { isLoading, error ->
            error?.snackBar(binding.root)
            if (isLoading) {
                showProgressBar()
            } else {
                dismissProgressBar()
            }
        }
    }

    private fun navigateToFinalDetailScreen(argData: String) {
        navigateTo(
            "android-app://com.jar.app/loanFinalDetails/$argData",
            popUpTo = R.id.lendingHostFragment,
            inclusive = true,
            shouldAnimate = true
        )
    }

    private fun navigateToAadharFlow(applicationId: String?) {
        kycApi.openAadharVerificationFlow(
            kycScreenArgs = KYCScreenArgs(
                fromScreen = aadhaarLaunchType,
                kycFeatureFlowType = KycFeatureFlowType.LENDING,
                lenderName = viewModel.preApprovedData?.lenderName,
                applicationId = applicationId
            ),
            childNavController = navController,
        ) { isLoading, error ->
            error?.snackBar(binding.root)
            if (isLoading) {
                showProgressBar()
            } else {
                dismissProgressBar()
            }
        }
    }

    private fun navigateToSelfieFlow(applicationId: String?) {
        kycApi.openSelfieFlow(
            kycScreenArgs = KYCScreenArgs(
                fromScreen = BaseConstants.FROM_LENDING,
                kycFeatureFlowType = KycFeatureFlowType.LENDING,
                lenderName = viewModel.preApprovedData?.lenderName,
                applicationId = applicationId
            ),
            childNavController = navController
        ) { isLoading, error ->
            error?.snackBar(binding.root)
            if (isLoading) {
                showProgressBar()
            } else {
                dismissProgressBar()
            }
        }
    }

    private fun setClickListener() {
        binding.btnRetry.setDebounceClickListener {
            getData()
        }
        binding.lendingToolbar.setBackButtonClickListener {
            getCurrentScreenForAnalytics()?.let { screenName ->
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_BackButtonClicked,
                    values = mapOf(
                        LendingEventKeyV2.screen_name to screenName,
                        LendingEventKeyV2.lender to viewModel.readyCashJourney?.lender.orEmpty()
                    )
                )
            }
            handleBackNavigation()
        }

        binding.lendingToolbar.setNeedHelpButtonClickListener {
            getCurrentScreenForAnalytics()?.let { screenName ->
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_NeedHelpButtonClicked,
                    values = mapOf(
                        LendingEventKeyV2.screen_name to screenName
                    )
                )
            }
            openNeedHelp()
        }
    }

    private fun openNeedHelp() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getString(
            MR.strings.feature_lending_kyc_contact_support_real_time_help_s_s.resourceId,
            name,
            number
        )
        requireContext().openWhatsapp(sendTo, message)
    }
    private fun performChildBackNavigation() {
        navController.popBackStack()
    }

    private fun shouldPopToExitLending(): Boolean {
        return navController.backQueue.size == 1
    }

    private fun getCurrentScreenForAnalytics(): String? {
        return when (navController.currentBackStackEntry?.destination?.id) {
            R.id.lendingEmploymentDetailsFragment -> LendingEventKeyV2.PDETAILS_MAIN_SCREEN
            R.id.confirmLendingKycFragment -> LendingEventKeyV2.EKYC_RECORDS_SHOWN_SCREEN
            R.id.loanSummaryV2Fragment -> LendingEventKeyV2.RCASH_SUMMARY_SCREEN
            R.id.bankDetailsFragment -> LendingEventKeyV2.BANK_DETAILS_LAUNCH_SCREEN
            R.id.loanMandateFailureFragment -> LendingEventKeyV2.MANDATE_FAILURE_SCREEN
            R.id.loanMandateConsentFragment -> LendingEventKeyV2.BANK_ACCOUNTS_SHOWN_SCREEN
            R.id.loanAgreementV2Fragment -> LendingEventKeyV2.LOAN_AGREEMENT_SCREEN
            R.id.confirmCkycDetailsFragment -> LendingEventKeyV2.CONFIRM_CKYC_SCREEN
            com.jar.app.feature_lending_kyc.R.id.creditReportFetchedFragment -> LendingEventKeyV2.PAN_CARD_FETCHED_SCREEN
            com.jar.app.feature_lending_kyc.R.id.enterPanManuallyFragment -> LendingEventKeyV2.PAN_MANUAL_ENTRY_SCREEN
            com.jar.app.feature_lending_kyc.R.id.aadhaarCkycfetchFragment -> LendingEventKeyV2.CHECKING_CKYC_RECORDS_SCREEN
            com.jar.app.feature_lending_kyc.R.id.KYCOptionFragment -> LendingEventKeyV2.KYC_SCREEN
            com.jar.app.feature_lending_kyc.R.id.aadhaarManualEntryFragment -> LendingEventKeyV2.AADHAR_MANUAL_ENTRY_SCREEN
            com.jar.app.feature_lending_kyc.R.id.aadhaarCapturePhotoFragment -> LendingEventKeyV2.AADHAR_OCR_FIRST_SCREEN
            com.jar.app.feature_lending_kyc.R.id.selfieCheckFragment -> LendingEventKeyV2.SELFIE_LAUNCH_SCREEN
            com.jar.app.feature_lending_kyc.R.id.creditReportFetchedFragment -> LendingEventKeyV2.PAN_CARD_FETCHED_SCREEN
            com.jar.app.core_image_picker.R.id.previewV2Fragment -> LendingEventKeyV2.CAMERA_PREVIEW_SCREEN
            com.jar.app.core_image_picker.R.id.cropV2Fragment -> LendingEventKeyV2.CAMERA_CROP_SCREEN
            else -> null
        }
    }

    private fun shouldNavigateBackInChildGraph() =
        when (navController.currentBackStackEntry?.destination?.id) {
            com.jar.app.core_image_picker.R.id.previewV2Fragment,
            com.jar.app.core_image_picker.R.id.cropV2Fragment,
            com.jar.app.feature_lending_kyc.R.id.aadhaarManualEntryFragment,
            com.jar.app.feature_lending_kyc.R.id.aadhaarCapturePhotoFragment,
            com.jar.app.feature_lending_kyc.R.id.aadhaarUploadFailedFragment -> true

            else -> false
        }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReadyCashNavigationEvent(event: ReadyCashNavigationEvent) {
        Timber.d("MK:: Received Event -> $event")
        EventBus.getDefault().removeStickyEvent(event)
        isFirstTimeNavigationDone = true
        if (event.whichScreen == ReadyCashScreen.HOME_SCREEN) {
            EventBus.getDefault().post(RefreshLendingEvent())
            popBackStack()
        } else {
            if (event.isBackFlow) {
                this.readyCashNavigationEvent = event
                viewModel.readyCashJourney?.let {
                    navigateAsPerExperiment(it)
                }
            } else {
                event.launchType?.let {
                    aadhaarLaunchType = it
                }
                this.readyCashNavigationEvent = if (event.shouldCacheThisEvent)
                    event else null
                getData()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onLendingAadharVerificationDoneEvent(onEvent: LendingAadharVerificationDoneEvent) {
        EventBus.getDefault().removeStickyEvent(onEvent)
        readyCashNavigationEvent = null
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onPanVerificationComplete(onPanVerificationDoneEvent: PanVerificationDoneEvent) {
        EventBus.getDefault().removeStickyEvent(onPanVerificationDoneEvent)
        readyCashNavigationEvent = null
        getData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onKycCompleteEvent(kycCompletedEventV2: LendingKycCompletedEventV2) {
        EventBus.getDefault().removeStickyEvent(kycCompletedEventV2)
        readyCashNavigationEvent = null
        getData()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLendingToolbarVisibilityEventV2(
        lendingToolbarVisibilityEventV2: LendingToolbarVisibilityEventV2
    ) {
        binding.lendingToolbar.isGone = lendingToolbarVisibilityEventV2.shouldHide
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLendingTitleEventV2(
        lendingToolbarTitleEventV2: LendingToolbarTitleEventV2
    ) {
        binding.lendingToolbar.setTitle(lendingToolbarTitleEventV2.title)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavigateToSellGoldEvent(event: LendingNavigateToSellGoldEvent) {
        navigateTo(
            BaseConstants.InternalDeepLinks.SELL_GOLD_REVAMP,
            popUpTo = R.id.lendingHostFragment,
            inclusive = true
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavigateToRepaymentFlowEvent(event: LendingNavigateToRepaymentFlow) {
        navigateTo(
            uri = "android-app://com.jar.app/repaymentOverviewFragment/${event.loanId}"
        )
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReadyCashBackPressEvent(lendingBackPressEvent: LendingBackPressEvent) {
        if (lendingBackPressEvent.logAnalyticsEvent) {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_BackButtonClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_name to lendingBackPressEvent.screenName,
                    LendingEventKeyV2.lender to viewModel.readyCashJourney?.lender.orEmpty()
                )
            )
        }
        if (lendingBackPressEvent.shouldNavigateBack) {
            handleBackNavigation(lendingBackPressEvent.popupId)
        }
    }

    private fun handleBackNavigation(popupToId: Int? = null) {
        if (shouldPopToExitLending()) {
            popBackStack()
        } else {
            if (shouldNavigateBackInChildGraph()) {
                performChildBackNavigation()
            } else {
                viewModel.currentScreen?.let {
                    viewModel.getScreenDataByScreenName(it)?.let {
                        navigateToPrevious(it.backScreen, popupToId)
                    }
                } ?: run {
                    performChildBackNavigation()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    override fun onDestroyView() {
        EventBus.getDefault().post(RefreshLendingEvent())
        super.onDestroyView()
    }

}