package com.jar.android.feature_post_setup.impl.ui.failed_transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jar.android.feature_post_setup.CalendarUtil
import com.jar.android.feature_post_setup.R
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupFragmentFailedTransactionBinding
import com.jar.android.feature_post_setup.impl.ui.status.failure_or_pending.FailureOrPendingData
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.app.feature_post_setup.util.PostSetupEventKey
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class FailedTransactionFragment :
    BaseFragment<FeaturePostSetupFragmentFailedTransactionBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeaturePostSetupFragmentFailedTransactionBinding
        get() = FeaturePostSetupFragmentFailedTransactionBinding::inflate

    @Inject
    lateinit var calendarUtil: CalendarUtil

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var serializer: Serializer

    private var totalSelectedAmount = 0
    private var isSelected = true

    private val spaceItemDecoration = SpaceItemDecoration(1.dp, 1.dp, escapeEdges = true)
    private val viewModel: FailedTransactionViewModel by viewModels()
    private val args: FailedTransactionFragmentArgs by navArgs()
    private var adapter: FailedDayTransactionAdapter? = null
    private var roundOffIdsList = ArrayList<String>()
    private var job: Job? = null
    private var isLaunchScreenEventLaunched = true
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchPostSetupCalenderData(args.monthIndex)
    }

    private fun setupUI() {
        setupToolbar()
        adapter = FailedDayTransactionAdapter { calendarInfo, position ->
            toggleSelectionFailedTransaction(calendarInfo, position)
        }
        binding.calenderView.rvCalender.layoutManager =
            GridLayoutManager(requireContext(), CalendarUtil.NUMBER_OF_DAYS_IN_WEEK)
        binding.calenderView.rvCalender.adapter = adapter
        binding.calenderView.rvCalender.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.calenderView.tvMonthYear.text =
            calendarUtil.getMonthAndYearString(monthIndex = args.monthIndex)
        binding.calenderView.ivNext.isVisible = false
        binding.calenderView.ivPrevious.isVisible = false
        binding.calenderView.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_383250
            )
        )
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(PostSetupMR.strings.feature_post_setup_daily_saving)
        binding.toolbar.ivEndImage.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_custom_question_mark)
        binding.toolbar.ivEndImage.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), com.jar.app.core_ui.R.color.white)
        binding.toolbar.ivEndImage.imageTintList =
            ContextCompat.getColorStateList(binding.root.context, com.jar.app.core_ui.R.color.white)
        binding.toolbar.ivEndImage.isVisible = true
        binding.toolbar.separator.isVisible = true
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ClearAmountScreenLaunched,
                mapOf(
                    PostSetupEventKey.button_type to PostSetupEventKey.back
                )
            )
            popBackStack()
        }

        binding.toolbar.ivEndImage.setDebounceClickListener {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ClearAmountScreenLaunched,
                mapOf(
                    PostSetupEventKey.button_type to PostSetupEventKey.help
                )
            )
            EventBus.getDefault()
                .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.DAILY_SAVING_EDUCATION + "/${false}"))
        }

        binding.btnSaveNow.setDebounceClickListener {
            viewModel.selectedFailedDaysLiveData.value?.let {
                val selectedDaysCount = it.filter { it.isSelected }.size
                analyticsApi.postEvent(
                    PostSetupEventKey.PostSetupDS_ClearAmountPaymentClicked,
                    mapOf(
                        PostSetupEventKey.cal_month_failed_days_count to viewModel.calenderDataResp?.failureInfo?.noOfDays.orZero(),
                        PostSetupEventKey.cal_month_failed_amount to viewModel.calenderDataResp?.failureInfo?.amount.orZero(),
                        PostSetupEventKey.pre_selected_days_count to viewModel.calenderDataResp?.failureInfo?.noOfDays.orZero(),
                        PostSetupEventKey.deselected_days_count to it.size - selectedDaysCount,
                        PostSetupEventKey.final_selected_days_count to selectedDaysCount,
                    )
                )
                val listOffRoundOffIds = ArrayList<String>()
                it.filter { it.isSelected }.map { listOffRoundOffIds.add(it.roundOffId) }
                fetchPaymentResponseForId(totalSelectedAmount.toFloat(), listOffRoundOffIds)
            }
        }
    }

    private fun observeLiveData() {
        viewModel.calenderLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RestClientResult.Status.LOADING -> {
                    showProgressBar()
                }

                RestClientResult.Status.SUCCESS -> {
                    dismissProgressBar()
                    it.data?.let {
                        adapter?.submitList(it)
                    }
                    uiScope.launch {
                        delay(200)
                        if (isLaunchScreenEventLaunched) {
                            analyticsApi.postEvent(
                                PostSetupEventKey.PostSetupDS_ClearAmountScreenLaunched,
                                mapOf(
                                    PostSetupEventKey.cal_month_failed_days_count to viewModel.calenderDataResp?.failureInfo?.noOfDays.orZero(),
                                    PostSetupEventKey.cal_month_failed_amount to viewModel.calenderDataResp?.failureInfo?.amount.orZero(),
                                    PostSetupEventKey.selected_days_count to viewModel.calenderDataResp?.failureInfo?.noOfDays.orZero(),
                                )
                            )
                            isLaunchScreenEventLaunched = false
                        }
                    }
                }

                RestClientResult.Status.ERROR -> {
                    dismissProgressBar()
                }

                RestClientResult.Status.NONE -> {}
            }
        }

        viewModel.selectedFailedDaysLiveData.observe(viewLifecycleOwner) { list ->
            list?.let {
                uiScope.launch {
                    totalSelectedAmount = 0
                    binding.btnSaveNow.setDisabled(it.none { it.isSelected })
                    binding.tvSelectedByTotal.text =
                        ("${it.filter { it.isSelected }.size} / ${it.size}")
                    it.filter { it.isSelected }.let {
                        it.map { totalSelectedAmount += it.amount.toInt() }
                    }
                    binding.tvTotalAmount.text =
                        getString(
                            com.jar.app.core_ui.R.string.core_ui_rs_x_int, totalSelectedAmount
                        )
                }
            }
        }


        viewModel.failedPaymentLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                performFailedTransactionPaymentEvent(initiatePaymentResponse = it)
            },
            onError = { dismissProgressBar() }
        )
    }

    private fun toggleSelectionFailedTransaction(calendarInfo: FeaturePostSetUpCalendarInfo, position: Int) {
        adapter?.currentList?.let {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ClearAmountScreenLaunched,
                mapOf(
                    PostSetupEventKey.cal_month_failed_days_count to viewModel.calenderDataResp?.failureInfo?.noOfDays.orZero(),
                    PostSetupEventKey.cal_month_failed_amount to viewModel.calenderDataResp?.failureInfo?.amount.orZero(),
                    PostSetupEventKey.selected_days_count to it.filter { it.isSelected == true }.size.orZero()
                        .plus(if (calendarInfo.isSelected == true) -1 else +1),
                    PostSetupEventKey.current_date to calendarInfo.day,
                    PostSetupEventKey.is_selected to (calendarInfo.isSelected == false),
                )
            )
            viewModel.updateFailedTransactionSelection(it, calendarInfo.id, position)
        }
    }

    private fun fetchPaymentResponseForId(amount: Float, roundOffIds: List<String>) {
        roundOffIdsList.clear()
        roundOffIdsList.addAll(roundOffIds)
        viewModel.initiateFailedPayment(
            amount,
            paymentManager.getCurrentPaymentGateway().name,
            roundOffIdsList
        )
    }

    private fun performFailedTransactionPaymentEvent(initiatePaymentResponse: InitiatePaymentResponse) {
        job?.cancel()
        job = appScope.launch(dispatcherProvider.main) {
            initiatePaymentResponse.screenSource =
                BaseConstants.ManualPaymentFlowType.PostSetupFlow
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        if (it.getManualPaymentStatus() == ManualPaymentStatus.SUCCESS) {
                            navigateTo(
                                "android-app://com.jar.app/postSetupSuccessStatus/${it.transactionId.orEmpty()}",
                                popUpTo = R.id.postSetupDetailsFragment,
                                inclusive = false
                            )
                        } else {
                            val data = encodeUrl(
                                serializer.encodeToString(
                                    FailureOrPendingData(
                                        title = it.title,
                                        description = it.description,
                                        isPendingFlow = it.getManualPaymentStatus() == ManualPaymentStatus.PENDING,
                                        transactionId = it.transactionId,
                                        amount = it.amount.orZero(),
                                        roundOffIds = roundOffIdsList,
                                    )
                                )
                            )
                            navigateTo(
                                "android-app://com.jar.app/postSetupStatus/$data",
                                popUpTo = R.id.postSetupDetailsFragment,
                                inclusive = true
                            )
                        }
                    }, onError = { message, errorCode ->
                        uiScope.launch {
                            whenResumed {
                                dismissProgressBar()
                                message.snackBar(binding.root)
                            }
                        }
                    }
                )
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}