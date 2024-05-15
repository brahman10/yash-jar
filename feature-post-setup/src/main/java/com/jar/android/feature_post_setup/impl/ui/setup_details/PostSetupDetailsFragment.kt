package com.jar.android.feature_post_setup.impl.ui.setup_details

import android.animation.ValueAnimator
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.android.feature_post_setup.CalendarUtil
import com.jar.android.feature_post_setup.NavigationPostSetupDirections
import com.jar.android.feature_post_setup.R
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupFragmentDetailsBinding
import com.jar.android.feature_post_setup.impl.data.event.UpdateBankEvent
import com.jar.android.feature_post_setup.impl.ui.setup_details.adapter.PostSetupAdapter
import com.jar.android.feature_post_setup.impl.ui.setup_details.delegates.BottomSectionAdapterDelegate
import com.jar.android.feature_post_setup.impl.ui.setup_details.delegates.CalendarViewAdapterDelegate
import com.jar.android.feature_post_setup.impl.ui.setup_details.delegates.PostSetupFaqAdapterDelegate
import com.jar.android.feature_post_setup.impl.ui.setup_details.delegates.SettingsAdapterDelegate
import com.jar.android.feature_post_setup.impl.ui.setup_details.delegates.SetupDetailsAdapterDelegate
import com.jar.android.feature_post_setup.impl.ui.setup_details.delegates.StateAmountInfoAdapterDelegate
import com.jar.app.base.data.event.*
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.PauseSavingOption
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getDateShortMonthNameAndYear
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.generic_post_action.data.PostActionStatus
import com.jar.app.core_ui.pause_savings.GenericPauseData
import com.jar.app.feature_mandate_payment.api.MandatePaymentApi
import com.jar.app.feature_savings_common.shared.domain.model.SavingsPauseStatusData
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_post_setup.domain.model.calendar.SavingOperations
import com.jar.app.feature_post_setup.util.PostSetupConstants
import com.jar.app.feature_post_setup.util.PostSetupEventKey
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class PostSetupDetailsFragment : BaseFragment<FeaturePostSetupFragmentDetailsBinding>(),
    BaseResources {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeaturePostSetupFragmentDetailsBinding
        get() = FeaturePostSetupFragmentDetailsBinding::inflate

    @Inject
    lateinit var coreUiApi: CoreUiApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var calendarUtil: CalendarUtil

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var mandatePaymentApi: MandatePaymentApi

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider
    private var mandatePaymentJob: Job? = null

    companion object {
        const val SHOW_DS_EDUCATION_SCREEN_MAX_VALUE = 2
        const val PAUSE_SAVINGS_BOTTOM_SHEET = "pauseSavingsBottomSheet"
    }

    private val addAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        val progress = (it.animatedValue.toString().toFloatOrZero() * 100).toInt()
        if (progress == 100) {
            if (prefs.isAutomaticallyDailySavingEducationScreenShown().not()) {
                prefs.setAutomaticallyDailySavingEducationScreenShown(true)
                binding.toolbar.ivEndImage.performClick()
            }
        }
    }

    private val viewModel: PostSetupDetailsViewModel by viewModels()
    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()
    private var isScreenLaunchEventTriggered = false
    private var isBottomReachedEventTriggered = false
    private var savingsPauseStatusData: SavingsPauseStatusData? = null
    private var dailySavingAmount = 0
    private var mandateAmount = 0
    private var isMonthChangeEventTriggered = false
    private var postSetupScreenState = PostSetupEventKey.active
    private var isRoundOffsEnabled = false
    private var pauseSavingOption: PauseSavingOption? = null

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = false
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                    PostSetupEventKey.button_type to PostSetupEventKey.back
                )
            )
            popBackStack()
        }
    }

    private val glide by lazy {
        Glide.with(requireContext())
    }

    private val calendarViewAdapterDelegate: CalendarViewAdapterDelegate by lazy {
        CalendarViewAdapterDelegate(
            uiScope = uiScope,
            onDayClick = {
                analyticsApi.postEvent(
                    PostSetupEventKey.PostSetupDS_ScreenClicked,
                    mapOf(
                        PostSetupEventKey.button_type to PostSetupEventKey.Calendar_date,
                        PostSetupEventKey.day_state to it.status
                    )
                )
                viewModel.updateTransactionStateAmountShimmer(adapter.items, it)
                binding.rvDsPostSetup.smoothScrollToPosition(2)
            }, onNextMonthClicked = {
                isMonthChangeEventTriggered = false
                viewModel.monthIndex++
                viewModel.fetchPostSetupCalenderData()
            }, onPrevMonthClicked = {
                isMonthChangeEventTriggered = false
                viewModel.monthIndex--
                viewModel.fetchPostSetupCalenderData()
            }, onDSOperationCtaClicked = {
                analyticsApi.postEvent(
                    PostSetupEventKey.PostSetupDS_ScreenClicked,
                    mapOf(PostSetupEventKey.button_type to it.cardType.orEmpty())
                )
                handleDeeplinkPostSetupScreen(it)
            }
        )
    }

    private val stateAmountInfoAdapterDelegate: StateAmountInfoAdapterDelegate by lazy {
        StateAmountInfoAdapterDelegate {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_SaveNowClicked, getCommonAnalyticsParams()
            )
            navigateTo(
                NavigationPostSetupDirections.actionToFailedTransactionFragment(viewModel.monthIndex)
            )
        }
    }

    private val settingAdapterDelegate: SettingsAdapterDelegate by lazy {
        SettingsAdapterDelegate {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                    PostSetupEventKey.button_type to it.cardType.orEmpty()
                )
            )
            if (it.deeplink.isNullOrEmpty().not()) fireHandleDeepLinkEvent(it.deeplink.orEmpty())
        }
    }

    private val postSetupFaqAdapterDelegate: PostSetupFaqAdapterDelegate by lazy {
        PostSetupFaqAdapterDelegate {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ScreenClicked,
                mapOf(
                    PostSetupEventKey.faq_title to it.question,
                    PostSetupEventKey.button_type to PostSetupEventKey.FAQ
                )
            )
        }
    }

    private val adapter by lazy {
        PostSetupAdapter(
            listOf(
                SetupDetailsAdapterDelegate(),
                calendarViewAdapterDelegate,
                stateAmountInfoAdapterDelegate,
                settingAdapterDelegate,
                postSetupFaqAdapterDelegate,
                BottomSectionAdapterDelegate()
            )
        )
    }

    private var layoutManager: LinearLayoutManager? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun getData() {
        viewModel.fetchPostSetupUserData()
        viewModel.fetchPauseDetailsDataFlow()
    }

    private fun setupUI() {
        viewModel.fetchUserRoundOffDetails()
        viewModel.fetchPauseOptions()
        setupToolbar()
        layoutManager = LinearLayoutManager(requireContext())
        binding.rvDsPostSetup.layoutManager = null
        binding.rvDsPostSetup.layoutManager = layoutManager
        binding.rvDsPostSetup.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvDsPostSetup.adapter = adapter
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
        binding.btnStateAction.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                    PostSetupEventKey.button_type to PostSetupEventKey.back
                )
            )
            popBackStack()
        }

        binding.toolbar.ivEndImage.setDebounceClickListener {
            analyticsApi.postEvent(
                PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                    PostSetupEventKey.button_type to PostSetupEventKey.DS_education_icon
                )
            )
            fireHandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.DAILY_SAVING_EDUCATION + "/${false}")
        }

        binding.bgOverlayView.setOnTouchListener { view, motionEvent ->
            return@setOnTouchListener true
        }

        binding.rvDsPostSetup.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isBottomReachedEventTriggered.not()) layoutManager?.let {
                    val visibleItemCount: Int = layoutManager!!.childCount
                    val totalItemCount: Int = layoutManager!!.itemCount
                    val firstVisibleItemPosition: Int =
                        layoutManager!!.findFirstVisibleItemPosition()

                    // Fire event if we have reach the end to the recyclerView
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isBottomReachedEventTriggered = true
                        analyticsApi.postEvent(
                            PostSetupEventKey.PostSetupDS_ScreenBottomReached,
                            getCommonAnalyticsParams()
                        )
                    }
                }
            }
        })

        binding.btnStateAction.setDebounceClickListener {
            when {
                viewModel.dsFailureInfo != null -> {
                    analyticsApi.postEvent(
                        PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                            PostSetupEventKey.button_type to PostSetupEventKey.resolve_now
                        )
                    )
                    redirectDSFailureInfo()
                }

                viewModel.isSavingPaused -> {
                    analyticsApi.postEvent(
                        PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                            PostSetupEventKey.button_type to PostSetupEventKey.resume_now
                        )
                    )
                    viewModel.pauseOrResumeDailySavings(false, null)
                }

                viewModel.isSavingsEnabled.not() -> {
                    analyticsApi.postEvent(
                        PostSetupEventKey.PostSetupDS_ScreenClicked, mapOf(
                            PostSetupEventKey.button_type to PostSetupEventKey.setup_now
                        )
                    )
                    fireHandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.DAILY_SAVINGS)
                }
            }
        }

    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.postSetupUserDataLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                if (it.newUser.orFalse() && prefs.shouldShowPostSetupScreenForNewSetup()) {
                    binding.lottieCelebration.isVisible = true
                    binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
                        requireContext(), BaseConstants.LottieUrls.CONFETTI_FROM_TOP
                    )
                    binding.lottieCelebration.addAnimatorUpdateListener(addAnimatorUpdateListener)
                    postSetupScreenState = PostSetupEventKey.first_time
                    prefs.setShouldShowPostSetupScreenForNewSetup(false)
                }
                viewModel.fetchPostSetupSavingOperations()
                viewModel.mergeApiResponse(userPostSetupData = it)
            }
        )

        viewModel.savingOperationsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                viewModel.fetchPostSetupCalenderData()
                viewModel.mergeApiResponse(calendarSavingOperations = it)
            }
        )

        viewModel.calenderLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                if (viewModel.shouldFetchQuickActions) viewModel.fetchPostSetupQuickAction()
                viewModel.mergeApiResponse(calendarDataResp = it)
            }
        )

        viewModel.quickActionsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                viewModel.fetchPostSetupFAQS()
                viewModel.mergeApiResponse(quickActionList = it)
            }
        )

        viewModel.faqsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                viewModel.mergeApiResponse(genericFaqList = it)
            }
        )

        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pauseDetailsFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewModel.dsFailureInfoLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                it?.let {
                    viewModel.dsFailureInfo = it
                    binding.clDSStatesContainer.isVisible = it.noOfDays != null
                    binding.tvStateTitle.text = getCustomStringFormatted(
                        requireContext(),
                        PostSetupMR.strings.feature_post_setup_we_are_unable_to_execute_for_x_days,
                        it.noOfDays.orZero()
                    )
                    binding.tvStateTitle.setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(), com.jar.app.core_ui.R.color.color_2e2942
                        )
                    )
                    binding.btnStateAction.text =
                        getCustomString(PostSetupMR.strings.feature_post_setup_resolve_now)
                    binding.btnStateAction.setTextColor(
                        ContextCompat.getColorStateList(
                            requireContext(), com.jar.app.core_ui.R.color.color_7745FF
                        )
                    )
                    binding.clDSStatesContainer.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.jar.app.core_ui.R.color.color_F2AFB1
                        )
                    )

                    glide.load(com.jar.app.core_ui.R.drawable.core_ui_ic_alert_filled)
                        .into(binding.ivStateIcon)
                }
            }
        )

        viewModel.postSetupPageLiveData.observe(viewLifecycleOwner) {
            when (it.status) {
                RestClientResult.Status.LOADING -> {
                    showProgressBar()
                }
                RestClientResult.Status.SUCCESS -> {
                    if (it.data?.size.orZero() >= 3) {
                        dismissProgressBar()
                        it.data?.let { adapter.items = it }
                        if (isScreenLaunchEventTriggered.not()) {
                            isScreenLaunchEventTriggered = true
                            analyticsApi.postEvent(
                                PostSetupEventKey.PostSetupDS_ScreenLaunched,
                                getCommonAnalyticsParams()
                            )
                        } else if (viewModel.calenderMonthName != viewModel.currentMonth && isMonthChangeEventTriggered.not()) {
                            isMonthChangeEventTriggered = true
                            analyticsApi.postEvent(
                                PostSetupEventKey.PostSetupDS_CalenderMonthChanged,
                                getCommonAnalyticsParams()
                            )
                        }
                    }
                }
                RestClientResult.Status.ERROR -> {
                    dismissProgressBar()
                }
                RestClientResult.Status.NONE -> {}
            }
        }

        viewModel.dailySavingsDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                viewModel.isSavingPaused = it.pauseStatus?.savingsPaused.orFalse()
                savingsPauseStatusData = it.pauseStatus
                dailySavingAmount = it.subscriptionAmount.toInt()
                mandateAmount = it.mandateAmount?.toInt().orZero()
                viewModel.isSavingsEnabled = it.enabled.orFalse()
                postSetupScreenState =
                    if (viewModel.isSavingPaused) PostSetupEventKey.paused else if (viewModel.isSavingsEnabled.not()) PostSetupEventKey.disabled else PostSetupEventKey.active
                togglePausedOrDisabledView(it)
                viewModel.fetchDsFailureInfo()
            },
            onError = { dismissProgressBar() }
        )

        viewModel.dailySavingPauseLiveData.observeNetworkResponse(viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                dismissProgressBar()
                if (it.isSavingPaused.orFalse()) {
                    coreUiApi.openGenericPostActionStatusFragment(
                        GenericPostActionStatusData(
                            postActionStatus = PostActionStatus.DISABLED.name,
                            header = getCustomString(PostSetupMR.strings.feature_post_setup_daily_saving_paused),
                            description = getCustomStringFormatted(
                                PostSetupMR.strings.feature_post_setup_we_will_not_save_for_you_for_the_next_s,
                                "${pauseSavingOption?.timeValue} ${getString(pauseSavingOption?.durationType?.durationRes.orZero())}"
                            ),
                            descriptionColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                            title = null,
                            imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_pause,
                            headerTextSize = 20f,
                        )
                    ) {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        viewModel.fetchUserDailySavingsDetails()
                    }
                } else {
                    coreUiApi.openGenericPostActionStatusFragment(
                        GenericPostActionStatusData(
                            postActionStatus = PostActionStatus.RESUMED.name,
                            header = null,
                            title = getCustomString(PostSetupMR.strings.feature_post_setup_ds_resumed_successfully),
                            description = getCustomString(PostSetupMR.strings.feature_post_setup_your_saving_will_continue_from_tomorrow),
                            descriptionColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                            imageRes = com.jar.app.core_ui.R.drawable.core_ui_ic_tick,
                            headerTextSize = 20f,
                        )
                    ) {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        viewModel.fetchUserDailySavingsDetails()
                    }
                }
            },
            onError = { dismissProgressBar() }
        )


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            PostSetupConstants.SUCCESSFUL_TRANSACTION_CALLBACK
        )?.observe(viewLifecycleOwner)
        {
            viewModel.fetchPostSetupCalenderData()
        }
    }


    private fun togglePausedOrDisabledView(userSavingsDetails: UserSavingsDetails) {
        if (userSavingsDetails.pauseStatus?.savingsPaused == true) {
            binding.clDSStatesContainer.isVisible = true
            binding.bgOverlayView.isVisible = true
            userSavingsDetails.pauseStatus?.let {
                binding.tvStateTitle.text = getCustomStringFormatted(
                    PostSetupMR.strings.feature_post_setup_your_savings_are_paused_till_s,
                    it.willResumeOn?.getDateShortMonthNameAndYear().orEmpty()
                )
                binding.btnStateAction.text =
                    getCustomString(PostSetupMR.strings.feature_post_setup_resume_now)
                binding.tvStateTitle.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(), com.jar.app.core_ui.R.color.color_2e2942
                    )
                )
                binding.btnStateAction.setTextColor(
                    ContextCompat.getColorStateList(
                        requireContext(), com.jar.app.core_ui.R.color.color_724508
                    )
                )
                binding.clDSStatesContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_EBB46A
                    )
                )
                val pauseIcon = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_post_setup_ic_pause
                )
                pauseIcon?.setTint(
                    ContextCompat.getColor(
                        requireContext(), com.jar.app.core_ui.R.color.color_3c3357
                    )
                )
                glide.load(pauseIcon).into(binding.ivStateIcon)
            }
        } else if (userSavingsDetails.enabled.orFalse().not()) {
            binding.clDSStatesContainer.isVisible = true
            binding.bgOverlayView.isVisible = true
            binding.tvStateTitle.text =
                getCustomString(PostSetupMR.strings.feature_post_setup_restart_your_daily_savings)
            binding.btnStateAction.text = getCustomString(PostSetupMR.strings.feature_post_setup_setup_now)
            binding.tvStateTitle.setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(), com.jar.app.core_ui.R.color.color_2e2942
                )
            )
            binding.btnStateAction.setTextColor(
                ContextCompat.getColorStateList(
                    requireContext(), com.jar.app.core_ui.R.color.color_730A0D
                )
            )
            binding.clDSStatesContainer.setBackgroundColor(
                ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_EB6A6E)
            )
            glide.load(com.jar.app.core_ui.R.drawable.core_ui_ic_daily_saving_lined)
                .into(binding.ivStateIcon)
        } else {
            binding.clDSStatesContainer.isVisible = false
            binding.bgOverlayView.isVisible = false
        }
    }

    private fun getCommonAnalyticsParams(): Map<String, Any> {
        return mapOf(
            PostSetupEventKey.cal_month_success_days_count to viewModel.stateAmountInfoPageItem?.stateInfoDetails?.successInfo?.noOfDays.orZero(),
            PostSetupEventKey.cal_month_success_amount to viewModel.stateAmountInfoPageItem?.stateInfoDetails?.successInfo?.amount.orZero(),
            PostSetupEventKey.cal_month_failed_days_count to viewModel.stateAmountInfoPageItem?.stateInfoDetails?.failureInfo?.noOfDays.orZero(),
            PostSetupEventKey.cal_month_failed_amount to viewModel.stateAmountInfoPageItem?.stateInfoDetails?.failureInfo?.amount.orZero(),
            PostSetupEventKey.cal_month_inprogress_days_count to viewModel.stateAmountInfoPageItem?.stateInfoDetails?.pendingInfo?.noOfDays.orZero(),
            PostSetupEventKey.cal_month_inprogress_amount to viewModel.stateAmountInfoPageItem?.stateInfoDetails?.pendingInfo?.amount.orZero(),
            PostSetupEventKey.current_month to viewModel.currentMonth,
            PostSetupEventKey.calender_month to viewModel.calenderMonthName,
            PostSetupEventKey.current_date to viewModel.currentDate,
            PostSetupEventKey.is_current_month to (viewModel.currentMonth == viewModel.calenderMonthName),
            PostSetupEventKey.total_savings to viewModel.postSetupUserDataLiveData.value?.data?.data?.totalAmount.orZero(),
            PostSetupEventKey.daily_savings_spins to viewModel.postSetupUserDataLiveData.value?.data?.data?.spinsCount.orZero(),
            PostSetupEventKey.active_days_count to viewModel.postSetupUserDataLiveData.value?.data?.data?.noOfDaysActive.orZero(),
            PostSetupEventKey.post_setup_screen_state to postSetupScreenState,
        )
    }

    private fun handleDeeplinkPostSetupScreen(savingOperations: SavingOperations) {
        if (savingOperations.deeplink.orEmpty().contains(PAUSE_SAVINGS_BOTTOM_SHEET)) {
            viewModel.pauseDetailsFlow.value.data?.data?.let {
                if (it.subVersion == "V3") {
                    navigateTo(
                        BaseConstants.InternalDeepLinks.POST_SETUP_DS_CANCELLATION_BOTTOMSHEET
                    )
                } else {
                    viewModel.pauseOptionsLiveData.value?.data?.let {
                        analyticsApi.postEvent(
                            PostSetupEventKey.Shown_PauseScreen_DSSettings,
                            mapOf(
                                PostSetupEventKey.action to PostSetupEventKey.shown,
                                PostSetupEventKey.feature_type to PostSetupEventKey.post_setup_daily_savings
                            )
                        )
                        coreUiApi.openGenericPauseSavingsDialog(GenericPauseData(it),
                            onPauseActionSubmitted = {
                                analyticsApi.postEvent(
                                    PostSetupEventKey.Shown_PauseScreen_DSSettings, mapOf(
                                        PostSetupEventKey.action to PostSetupEventKey.paused,
                                        PostSetupEventKey.duration to "${it.name} ${it.durationType.name}",
                                        PostSetupEventKey.feature_type to PostSetupEventKey.post_setup_daily_savings
                                    )
                                )
                                pauseSavingOption = it
                                viewModel.pauseOrResumeDailySavings(shouldPause = true, it.name)
                            },
                            onDialogDismiss = {
                                analyticsApi.postEvent(
                                    PostSetupEventKey.Shown_PauseScreen_DSSettings,
                                    mapOf(
                                        PostSetupEventKey.action to PostSetupEventKey.close,
                                        PostSetupEventKey.feature_type to PostSetupEventKey.post_setup_daily_savings
                                    )
                                )
                            }
                        )
                    }
                }
            }
        } else savingOperations.deeplink?.let {
            fireHandleDeepLinkEvent(it)
        }
    }

    private fun redirectDSFailureInfo() {
        viewModel.dsFailureInfo?.let {
            val dSFailureInfo = encodeUrl(serializer.encodeToString(it))
            navigateTo("android-app://com.jar.app/failedRenewalBottomSheet/$dSFailureInfo")
        }
    }

    private fun fireHandleDeepLinkEvent(deeplink: String) {
        EventBus.getDefault().post(HandleDeepLinkEvent(deeplink,fromScreen = prefs.getUserLifeCycleForMandate()))
    }

    override fun onDestroy() {
        super.onDestroy()
        layoutManager = null
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDailySavingEvent(refreshDailySavingEvent: RefreshDailySavingEvent) {
        viewModel.fetchUserDailySavingsDetails()
        dismissProgressBar()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateBankEvent(updateBankEvent: UpdateBankEvent) {
        if (isRoundOffsEnabled) {
            fireHandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.PRE_DAILY_SAVING_AUTOPAY)
        } else {
            fireHandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.UPDATE_DAILY_SAVING_MANDATE_SETUP + "/${dailySavingAmount.orZero()}/${mandateAmount.orZero()}/${BaseConstants.DailySavingUpdateFlow.HOME}")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchDsFailureInfo()
        viewModel.fetchUserDailySavingsDetails()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
        backPressCallback.isEnabled = true
    }

}