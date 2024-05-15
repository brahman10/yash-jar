package com.jar.app.feature_gold_lease.impl.ui.order_summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.RefreshGoldLeaseHomeCardEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseAppDeeplink
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.extension.toast
import com.jar.app.feature_buy_gold_v2.shared.domain.model.BuyGoldByVolumeRequest
import com.jar.app.feature_gold_lease.GoldLeaseNavigationDirections
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2OrderSummaryBinding
import com.jar.app.feature_gold_lease.impl.ui.GoldLeaseViewModelAndroid
import com.jar.app.feature_gold_lease.shared.domain.model.*
import com.jar.app.feature_gold_lease.impl.ui.kyc.GoldLeaseKycBottomSheetFragment
import com.jar.app.feature_gold_lease.impl.ui.kyc.GoldLeaseKycViewModelAndroid
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseConstants
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
internal class GoldLeaseV2OrderSummaryFragment :
    BaseFragment<FragmentGoldLeaseV2OrderSummaryBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var sharedPreferencesUserLiveData: SharedPreferencesUserLiveData

    private var user: User? = null

    private var paymentJob: Job? = null

    private val goldLeaseV2OrderSummaryArgs by lazy {
        try {
            args.goldLeaseV2OrderSummaryArgsString.takeIf { it.isEmpty().not() }?.let {
                return@lazy serializer.decodeFromString<GoldLeaseV2OrderSummaryArgs>(
                    decodeUrl(it)
                )
            } ?: kotlin.run {
                return@lazy null
            }
        } catch (e: Exception) {
            return@lazy null
        }
    }

    private val viewModelProvider by viewModels<GoldLeaseV2OrderSummaryViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val kycViewModelProvider by viewModels<GoldLeaseKycViewModelAndroid> { defaultViewModelProviderFactory }

    private val kycViewModel by lazy {
        kycViewModelProvider.getInstance()
    }

    private val goldLeaseViewModelProvider by viewModels<GoldLeaseViewModelAndroid> { defaultViewModelProviderFactory }

    private val goldLeaseViewModel by lazy {
        goldLeaseViewModelProvider.getInstance()
    }

    private val args by navArgs<GoldLeaseV2OrderSummaryFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2OrderSummaryBinding
        get() = FragmentGoldLeaseV2OrderSummaryBinding::inflate

    private var isTimerFinished = false
    private var isFirstTimeGoldFetched = true
    private var shouldFetchSummaryScreenData = true
    private var isShownEventSynced = false

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        setupUI()
    }

    private fun getData() {
        goldLeaseViewModel.fetchCurrentGoldBuyPrice()
    }

    private fun setupListeners(goldLeaseV2OrderSummaryScreenData: GoldLeaseV2OrderSummaryScreenData) {
        registerFragmentListenerForKycStatus()

        binding.ivJewellerInfo.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.JEWELLER_INFO)
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseV2JewellerDetailsBottomSheetFragment(
                    flowType = goldLeaseV2OrderSummaryArgs?.flowType.orEmpty(),
                    jewellerId = goldLeaseV2OrderSummaryScreenData.leasePlanList.jewellerId.orEmpty()
                )
            )
        }

        binding.ivAmountInfo.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.PRICE_INFO)
            navigateTo(
                GoldLeaseV2OrderSummaryFragmentDirections.actionGoldLeaseV2OrderSummaryFragmentToGoldLeaseBreakdownBottomSheetFragment(
                    totalPayableAmount = viewModel.amountToPay,
                    goldPrice = goldLeaseViewModel.currentGoldPrice,
                    gstPercent = goldLeaseViewModel.currentGoldTax,
                    goldVolume = goldLeaseV2OrderSummaryScreenData.totalVolume - goldLeaseV2OrderSummaryScreenData.jarVolumeUsed //Volume the user is paying for
                )
            )
        }

        binding.btnKycVerify.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.VERIFY)
            navigateTo(
                GoldLeaseV2OrderSummaryFragmentDirections.actionGoldLeaseV2OrderSummaryFragmentToGoldLeaseKycBottomSheetFragment(viewModel.goldLeaseV2OrderSummary?.emailRequired.orTrue())
            )
        }

        binding.cbAgreeTerms.setOnCheckedChangeListener { _, _ ->
            postClickEvent(GoldLeaseEventKey.Values.TICK)
            toggleMainButton()
        }

        binding.tvRiskFactor.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.RISK_FACTOR)
            navigateTo(
                GoldLeaseV2OrderSummaryFragmentDirections.actionGoldLeaseV2OrderSummaryFragmentToGoldLeaseRiskFactorBottomSheetFragment()
            )
        }

        binding.tvTNC.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.TNC)
            navigateTo(
                GoldLeaseV2OrderSummaryFragmentDirections.actionGoldLeaseV2OrderSummaryFragmentToGoldLeaseTNCBottomSheetFragment()
            )
        }

        binding.btnPay.setDebounceClickListener {
            postClickEvent(binding.btnPay.getText())
            if (binding.cbAgreeTerms.isChecked) {
                viewModel.isInitiateFlow = true
                kycViewModel.fetchUserLendingKycProgress()
            }
        }
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)

        observeUserLiveData()
        observeGoldLeaseSummaryLiveData(weakRef)
        observeCurrentBuyGoldPriceLiveData(weakRef)
        observeAmountFromVolumeLiveData(weakRef)
        observeUserKycStatus(weakRef)
        observerLendingKycProgress(weakRef)
        observeKycDetailsLiveData(weakRef)
        observeInitiateGoldLease(weakRef)
        observeBuyGoldLiveData(weakRef)
        observeGoldLeaseRetryDataLiveData(weakRef)
    }

    private fun observeUserLiveData() {
        sharedPreferencesUserLiveData.distinctUntilChanged().observe(viewLifecycleOwner) {
            user = it
        }
    }

    private fun observeGoldLeaseRetryDataLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseRetryDataFlow.collect(
                    onLoading = {
                        showProgressBar()
                        binding.svContent.isVisible = false
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            binding.svContent.isVisible = true
                            setOrderSummaryDataInUI(it)
                            setupListeners(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun observeBuyGoldLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                goldLeaseViewModel.buyGoldFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        it?.let {
                            dismissProgressBar()
                            paymentJob = appScope.launch(dispatcherProvider.main) {
                                paymentManager.initiateOneTimePayment(it).collectUnwrapped(
                                    onLoading = {
                                        showProgressBar()
                                    },
                                    onSuccess = { fetchManualPaymentStatusResponse ->
                                        dismissProgressBar()
                                        fetchManualPaymentStatusResponse.leaseId?.let { leaseId ->
                                            navigateTo(
                                                "${BaseAppDeeplink.GoldLease.GOLD_LEASE_POST_ORDER_SCREEN}/$leaseId/${goldLeaseV2OrderSummaryArgs?.flowType.orEmpty()}",
                                                popUpTo = R.id.goldLeaseLandingFragment,
                                                inclusive = true
                                            )
                                        }
                                    },
                                    onError = { message, errorCode ->
                                        view?.let {
                                            uiScope.launch {
                                                message.toast(it)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun observeInitiateGoldLease(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseInitiateFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = { initiateResponse ->
                        dismissProgressBar()
                        initiateResponse?.let {
                            EventBus.getDefault().post(RefreshGoldLeaseHomeCardEvent())
                            EventBus.getDefault()
                                .post(
                                    com.jar.app.feature_gold_lease.shared.domain.event.RefreshLeaseLandingScreenEvent(
                                        isNewLeaseUser = false
                                    )
                                )
                            if (it.pendingVolume.orZero() == 0.0f) {
                                it.leaseId?.let { leaseId ->
                                    navigateTo(
                                        "${BaseAppDeeplink.GoldLease.GOLD_LEASE_POST_ORDER_SCREEN}/$leaseId/${goldLeaseV2OrderSummaryArgs?.flowType.orEmpty()}",
                                        popUpTo = R.id.goldLeaseLandingFragment,
                                        inclusive = true
                                    )
                                }
                            } else {
                                val currentGoldPrice =
                                    goldLeaseViewModel.currentGoldBuyPriceFlow.value.data?.data
                                if (currentGoldPrice != null) {
                                    it.pendingVolume?.let { pendingVolume ->
                                        val buyGoldByVolumeRequest = BuyGoldByVolumeRequest(
                                            volume = pendingVolume,
                                            fetchCurrentGoldPriceResponse = currentGoldPrice,
                                            paymentGateway = paymentManager.getCurrentPaymentGateway(),
                                            leaseId = it.leaseId
                                        )
                                        goldLeaseViewModel.buyGoldByVolume(buyGoldByVolumeRequest)
                                    }
                                }
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun observeKycDetailsLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                kycViewModel.kycDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (viewModel.isInitiateFlow) {
                            initiateGoldLeaseV2(
                                pan = it?.panData?.panNumber
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun observeUserKycStatus(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                kycViewModel.userKycStatusFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            showKycLayout(it.isVerified())
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun observerLendingKycProgress(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                kycViewModel.userLendingKycProgressFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (viewModel.isInitiateFlow) {
                            if (it?.kycVerified.orFalse()) {
                                it?.kycProgress?.PAN?.panNo?.takeIf { pan -> pan.isNotEmpty() }?.let { panNo ->
                                    initiateGoldLeaseV2(
                                        pan = panNo
                                    )
                                } ?: kotlin.run {
                                    kycViewModel.fetchKycDetails()
                                }
                            } else {
                                kycViewModel.fetchKycDetails()
                            }
                        } else {
                            if (it?.kycVerified.orFalse()) {
                                it?.kycProgress?.PAN?.panNo?.takeIf { pan -> pan.isNotEmpty() }?.let {
                                    showKycLayout(true)
                                } ?: kotlin.run {
                                    kycViewModel.fetchUserKycStatus()
                                }
                            } else {
                                kycViewModel.fetchUserKycStatus()
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun getUserEmail() = kycViewModel.userLendingEmail ?: user?.email

    private fun showKycLayout(isVerified: Boolean) {
        viewModel.isKycVerified = isVerified
        viewModel.goldLeaseV2OrderSummary?.let {
            binding.tvKycDescription.setHtmlText(
                it.kycPendingDescription ?:
                if (it.emailRequired.orTrue()) getString(R.string.feature_gold_lease_your_pan_and_email_are_mandatory_to_lease_gold)
                else getString(R.string.feature_gold_lease_your_pan_mandatory_to_lease_gold)
            )
            setKycVerifiedTitle()
            if (it.kycVerificationRequired.orTrue()) {
                if (it.emailRequired.orTrue()) {
                    val isUserEmailExist = getUserEmail().isNullOrEmpty().not()
                    binding.clKycPending.isVisible = isVerified.not() || isUserEmailExist.not()
                    binding.clKycVerified.isVisible = isVerified && isUserEmailExist
                } else {
                    binding.clKycPending.isVisible = isVerified.not()
                    binding.clKycVerified.isVisible = isVerified
                }
            } else {
                binding.clKycPending.isVisible = false
                binding.clKycVerified.isVisible = false
            }
        }
        binding.svContent.isVisible = true
        toggleMainButton()
        val goldLeaseV2OrderSummaryScreenData =
            goldLeaseV2OrderSummaryArgs?.goldLeaseV2OrderSummaryScreenData
                ?: viewModel.goldLeaseRetryData
        postShownEvent(goldLeaseV2OrderSummaryScreenData, isVerified)
    }

    private fun setKycVerifiedTitle() {
        viewModel.goldLeaseV2OrderSummary?.let {
            binding.tvKycVerifiedTitle.setHtmlText(
                it.kycVerifiedTitle ?:
                if (it.emailRequired.orTrue()) getString(R.string.feature_gold_lease_pan_and_email_details)
                else getString(R.string.feature_gold_lease_pan_details)
            )
        }
    }

    private fun observeAmountFromVolumeLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                goldLeaseViewModel.amountFromVolumeFlow.collectUnwrapped(
                    onSuccess = {
                        setPayableAmount(it)
                    }
                )
            }
        }
    }

    private fun observeCurrentBuyGoldPriceLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                goldLeaseViewModel.currentGoldBuyPriceFlow.collect(
                    onLoading = {
                        if (!isFirstTimeGoldFetched) {
                            getString(com.jar.app.core_ui.R.string.fetching_new_gold_prices).snackBar(
                                binding.root,
                                com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                                progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                                duration = 2000,
                                translationY = 0f
                            )
                        }
                        isFirstTimeGoldFetched = false
                        binding.btnPay.isClickable = false
                    },
                    onSuccess = {
                        binding.btnPay.isClickable = true
                        binding.goldPriceTimer.start(
                            livePriceMessage = getString(
                                R.string.feature_gold_lease_live_buy_price,
                                it.price
                            ),
                            validityInMillis = it.getValidityInMillis(),
                            uiScope = uiScope,
                            onFinish = {
                                isTimerFinished = true
                                goldLeaseViewModel.fetchCurrentGoldBuyPrice()
                            }
                        )
                        fetchUpdatedPriceCallCaseHandling(it.price)
                        goldLeaseViewModel.currentGoldPrice = it.price
                        goldLeaseViewModel.currentGoldTax = it.applicableTax.orZero()

                        //To Update UI whenever new price is fetched
                        if (shouldFetchSummaryScreenData) {
                            getOrderSummaryScreenData()
                            shouldFetchSummaryScreenData = false
                        } else {
                            setOrderSummaryDataAndListener()
                        }
                    },
                    onError = { _, errorCode ->
                        dismissProgressBar()
                        if (errorCode == BaseConstants.ErrorCode.INVALID_BUY_PRICE_EXCEPTION) {
                            goldLeaseViewModel.fetchCurrentGoldBuyPrice()
                        }
                    }
                )
            }
        }
    }

    private fun getOrderSummaryScreenData() {
        goldLeaseV2OrderSummaryArgs?.goldLeaseV2OrderSummaryScreenData?.leasePlanList?.let { leasePlan ->
            viewModel.fetchGoldLeaseOrderSummary(leasePlan.planId.orEmpty())
        } ?: kotlin.run {
            viewModel.goldLeaseRetryData?.leasePlanList?.let { leasePlan ->
                viewModel.fetchGoldLeaseOrderSummary(leasePlan.planId.orEmpty())
            }
        }
    }

    private fun setOrderSummaryDataAndListener() {
        goldLeaseV2OrderSummaryArgs?.goldLeaseV2OrderSummaryScreenData?.let { goldLeaseV2OrderSummaryScreenData ->
            setOrderSummaryDataInUI(goldLeaseV2OrderSummaryScreenData)
            setupListeners(goldLeaseV2OrderSummaryScreenData)
        } ?: kotlin.run {
            viewModel.goldLeaseRetryData?.let {
                setOrderSummaryDataInUI(it)
                setupListeners(it)
            }
        }
    }

    private fun fetchUpdatedPriceCallCaseHandling(price: Float) {
        if (isTimerFinished) {
            isTimerFinished = false
            if (goldLeaseViewModel.currentGoldPrice > price) {
                getString(
                    R.string.feature_gold_lease_latest_buy_price_fetched_decreased_by_x,
                    abs(goldLeaseViewModel.currentGoldPrice - price)
                ).snackBar(
                    binding.root,
                    R.drawable.feature_gold_lease_ic_checkmark,
                    progressColor = com.jar.app.core_ui.R.color.color_1EA787
                )
            } else if (goldLeaseViewModel.currentGoldPrice < price) {
                getString(
                    R.string.feature_gold_lease_latest_buy_price_fetched_increased_by_x,
                    abs(goldLeaseViewModel.currentGoldPrice - price)
                ).snackBar(
                    binding.root,
                    R.drawable.feature_gold_lease_ic_checkmark,
                    progressColor = com.jar.app.core_ui.R.color.color_1EA787
                )
            }
        }
    }

    private fun observeGoldLeaseSummaryLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseOrderSummaryFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        goldLeaseV2OrderSummaryArgs?.goldLeaseV2OrderSummaryScreenData?.let {
                            setOrderSummaryDataInUI(it)
                            setupListeners(it)
                        } ?: kotlin.run {
                            viewModel.fetchGoldLeaseRetryData(goldLeaseV2OrderSummaryArgs?.leaseId.orEmpty())
                        }
                        kycViewModel.fetchUserLendingKycProgress()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun postShownEvent(goldLeaseV2OrderSummaryScreenData: GoldLeaseV2OrderSummaryScreenData?, isKycVerified: Boolean?) {
        if (goldLeaseV2OrderSummaryScreenData != null && isKycVerified != null && isShownEventSynced.not()) {
            isShownEventSynced = true
            val goldToPurchase = goldLeaseV2OrderSummaryScreenData.totalVolume - goldLeaseV2OrderSummaryScreenData.jarVolumeUsed
            analyticsApi.postEvent(
                GoldLeaseEventKey.LeaseSummaryScreen.Lease_SummaryScreenShown,
                mapOf(
                    GoldLeaseEventKey.Properties.USER_TYPE to if (goldLeaseV2OrderSummaryArgs?.isNewLeaseUser.orFalse()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.FROM_FLOW to goldLeaseV2OrderSummaryArgs?.flowType.orEmpty(),
                    GoldLeaseEventKey.Properties.PAN_CARD_STATUS to if (isKycVerified) GoldLeaseEventKey.Values.VERIFIED else GoldLeaseEventKey.Values.VERIFY,
                    GoldLeaseEventKey.Properties.TOTAL_GOLD_QTY to goldLeaseV2OrderSummaryScreenData.totalVolume,
                    GoldLeaseEventKey.Properties.LOCKER_GOLD_USED to goldLeaseV2OrderSummaryScreenData.jarVolumeUsed,
                    GoldLeaseEventKey.Properties.NON_LOCKER_GOLD_QUANTITY to goldToPurchase,
                    GoldLeaseEventKey.Properties.NON_LOCKER_GOLD_PRICE to goldLeaseViewModel.getAmountForXVolume(
                        goldToPurchase
                    ),
                    GoldLeaseEventKey.Properties.TOTAL_GOLD_PRICE to goldLeaseViewModel.getAmountForXVolume(
                        goldLeaseV2OrderSummaryScreenData.totalVolume
                    )
                )
            )
        }
    }

    private fun initiateGoldLeaseV2(pan: String?) {
        viewModel.isInitiateFlow = false
        val goldLeaseV2OrderSummaryScreenData =
            goldLeaseV2OrderSummaryArgs?.goldLeaseV2OrderSummaryScreenData
                ?: viewModel.goldLeaseRetryData
        goldLeaseV2OrderSummaryScreenData?.let {
            val goldLeaseV2InitiateRequest = GoldLeaseV2InitiateRequest(
                assetLeaseConfigId = it.leasePlanList.planId.orEmpty(),
                amountToPay = viewModel.amountToPay,
                jarVolumeUsed = it.jarVolumeUsed,
                totalLeaseVolume = it.totalVolume,
                emailId = getUserEmail().orEmpty(),
                panNumber = pan.orEmpty(),
                context = if (goldLeaseV2OrderSummaryArgs?.goldLeaseV2OrderSummaryScreenData == null) InitiateLeaseContext.RETRY.name else null
            )
            viewModel.initiateGoldLeasePayment(goldLeaseV2InitiateRequest)
        }
    }

    private fun setOrderSummaryDataInUI(goldLeaseV2OrderSummaryScreenData: GoldLeaseV2OrderSummaryScreenData) {
        viewModel.goldLeaseV2OrderSummary?.let {
            binding.tvTotalLeaseTitle.setHtmlText(it.leaseQuantityTitle.orEmpty())
            binding.tvTotalLeaseVolume.text = getString(
                R.string.feature_gold_lease_x_gm_round_to_4,
                goldLeaseV2OrderSummaryScreenData.totalVolume
            )
            Glide.with(requireContext())
                .load(goldLeaseV2OrderSummaryScreenData.leasePlanList.jewellerIcon.orEmpty())
                .into(binding.ivJewellerIcon)
            binding.tvJewellerName.setHtmlText(goldLeaseV2OrderSummaryScreenData.leasePlanList.jewellerName.orEmpty())
            binding.tvJewellerEst.setHtmlText(goldLeaseV2OrderSummaryScreenData.leasePlanList.jewellerEstablishedText.orEmpty())
            binding.tvGoldLeaseEarningsTitle.setHtmlText(it.goldEarningsTitle.orEmpty())
            binding.tvGoldLeaseLockInTitle.setHtmlText(it.lockInTitle.orEmpty())
            binding.tvLockInPeriod.text = getString(
                R.string.feature_gold_lease_x_days_string,
                goldLeaseV2OrderSummaryScreenData.leasePlanList.lockInComponent?.value.orEmpty()
            )
            val earningsText =
                if (goldLeaseV2OrderSummaryScreenData.leasePlanList.bonusPercentage.orZero() != 0.0f) {
                    requireContext().getFormattedTextForXStringValues(
                        R.string.feature_gold_lease_x_earnings_plus_y_bonus,
                        listOf(
                            goldLeaseV2OrderSummaryScreenData.leasePlanList.earningsPercentage.orZero()
                                .toString(),
                            goldLeaseV2OrderSummaryScreenData.leasePlanList.bonusPercentage.orZero()
                                .toString()
                        )
                    )
                } else {
                    requireContext().getFormattedTextForOneStringValue(
                        R.string.feature_gold_lease_x_earnings,
                        goldLeaseV2OrderSummaryScreenData.leasePlanList.earningsPercentage.orZero()
                            .toString()
                    )
                }
            binding.tvEarningsPercent.text = earningsText
            binding.tvJarBonusTag.isVisible =
                goldLeaseV2OrderSummaryScreenData.leasePlanList.bonusPercentage.orZero() != 0.0f
            binding.tvActivationDelay.setHtmlText(it.leaseActivationDelayText.orEmpty())
            it.leaseActivationDelayIcon?.takeIf { iconUrl -> iconUrl.isNotEmpty() }?.let { iconUrl ->
                Glide.with(requireContext()).load(iconUrl).into(binding.ivActivationDelay)
            } ?: kotlin.run {
                binding.ivActivationDelay.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.feature_gold_lease_ic_clock
                    )
                )
            }
            binding.tvGoldLeaseLockerTitle.setHtmlText(it.jarSavingsUsedTitle.orEmpty())
            binding.tvAdditionalGoldTitle.setHtmlText(it.goldPurchasedTitle.orEmpty())
            binding.tvGoldLeaseLockerValue.text = getString(
                R.string.feature_gold_lease_x_gm_round_to_4,
                goldLeaseV2OrderSummaryScreenData.jarVolumeUsed
            )
            val goldToPurchase =
                goldLeaseV2OrderSummaryScreenData.totalVolume - goldLeaseV2OrderSummaryScreenData.jarVolumeUsed
            binding.tvAdditionalGoldValue.text = getString(
                R.string.feature_gold_lease_x_gm_round_to_4, goldToPurchase
            )
            binding.tvTotalPayableTitle.setHtmlText(
                it.amountPayableTitle.orEmpty()
                    .replace(GoldLeaseConstants.VALUE_PLACEHOLDER, goldToPurchase.volumeToString())
            )
            getAmountForVolume(goldToPurchase)
            postShownEvent(goldLeaseV2OrderSummaryScreenData, viewModel.isKycVerified)
        }
    }

    private fun postClickEvent(buttonType: String) {
        analyticsApi.postEvent(
            GoldLeaseEventKey.LeaseSummaryScreen.Lease_SummaryScreenClicked,
            mapOf(
                GoldLeaseEventKey.Properties.USER_TYPE to if (goldLeaseV2OrderSummaryArgs?.isNewLeaseUser.orFalse()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                GoldLeaseEventKey.Properties.FROM_FLOW to goldLeaseV2OrderSummaryArgs?.flowType.orEmpty(),
                GoldLeaseEventKey.Properties.BUTTON_TYPE to buttonType
            )
        )
    }

    private fun getAmountForVolume(goldVolume: Float) {
        if (goldVolume == 0f) {
            setPayableAmount(0f)
        } else {
            goldLeaseViewModel.calculateAmountFromVolume(goldVolume)
        }
    }

    private fun setupUI() {
        setupToolbar()
        toggleMainButton(disableAnyway = true)
        binding.cbAgreeTerms.isChecked = false
    }

    private fun setPayableAmount(amount: Float) {
        viewModel.amountToPay = amount
        binding.tvTotalPayableValue.text = getString(
            R.string.feature_gold_lease_rupee_prefix_string,
            amount.toDouble().roundOffDecimal().getFormattedAmount()
        )
        binding.btnPay.setText(
            if (amount == 0f) getString(R.string.feature_gold_lease_confirm_and_lease) else getString(
                R.string.feature_gold_lease_pay_and_lease
            )
        )
    }

    private fun setupToolbar() {
        binding.toolbar.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.bgColor
            )
        )
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.separator.isVisible = true

        binding.toolbar.tvTitle.text = getString(R.string.feature_gold_lease_order_summary)

        //Setup FAQ Button
        binding.toolbar.tvEnd.setBackgroundResource(com.jar.app.core_ui.R.drawable.bg_rounded_40_121127)
        binding.toolbar.tvEnd.setPadding(16.dp, 8.dp, 16.dp, 8.dp)
        binding.toolbar.tvEnd.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        binding.toolbar.tvEnd.text = getString(R.string.feature_gold_lease_faqs)
        binding.toolbar.tvEnd.isVisible = true

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_FAQButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to goldLeaseV2OrderSummaryArgs?.flowType.orEmpty(),
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.SUMMARY_SCREEN
                )
            )
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseFaqBottomSheetFragment(
                    flowType = goldLeaseV2OrderSummaryArgs?.flowType.orEmpty()
                )
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_BackButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to goldLeaseV2OrderSummaryArgs?.flowType.orEmpty(),
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.SUMMARY_SCREEN
                )
            )
            popBackStack()
        }
    }

    private fun registerFragmentListenerForKycStatus() {
        setFragmentResultListener(
            GoldLeaseKycBottomSheetFragment.GOLD_LEASE_KYC_STATUS_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(GoldLeaseKycBottomSheetFragment.GOLD_LEASE_KYC_STATUS)) {
                GoldLeaseKycBottomSheetFragment.KYC_VERIFIED -> {
                    binding.clKycPending.isVisible = false
                    binding.clKycVerified.isVisible = true
                    setKycVerifiedTitle()
                    toggleMainButton()
                }
                GoldLeaseKycBottomSheetFragment.KYC_CANCELLED -> {}
            }
        }
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val isKycVerificationRequired =
            viewModel.goldLeaseV2OrderSummary?.kycVerificationRequired.orTrue()
        val shouldEnable = if (disableAnyway) false else
            if (isKycVerificationRequired) {
                binding.clKycVerified.isVisible
                        && binding.cbAgreeTerms.isChecked
            } else binding.cbAgreeTerms.isChecked
        binding.btnPay.setDisabled(shouldEnable.not())
    }

    override fun onDestroyView() {
        isFirstTimeGoldFetched = true
        shouldFetchSummaryScreenData = true
        viewModel.isInitiateFlow = false
        viewModel.isKycVerified = null
        isShownEventSynced = false
        super.onDestroyView()
    }
}