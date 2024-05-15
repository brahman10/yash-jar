package com.jar.app.feature_lending.impl.ui.reason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.dp
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLoanDetailsBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.domain.model.TransitionStateScreenArgs
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.ReasonData
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.LoanNameData
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LoanReasonFragment : BaseFragment<FragmentLoanDetailsBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private var reasonsAdapter: ReasonsAdapter? = null

    private val arguments by navArgs<LoanReasonFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }
    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }

    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private val viewModelProvider by viewModels<LoanReasonViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoanDetailsBinding
        get() = FragmentLoanDetailsBinding::inflate

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateAhead()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_NameUrRCashScreenShown,
            mapOf(LendingEventKeyV2.user_type to getUserType())
        )
    }

    private fun getUserType() = if (args.isRepeatWithdrawal) "Repeat" else "New"
    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeFlow()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        setupToolbar()
        toggleButtonState(true)
        reasonsAdapter = ReasonsAdapter {
            viewModel.selectedReason = it.title
            viewModel.updateReasonList(it.id)
            //Other's case
            binding.clReason.isVisible = it.id == 0
            if (!binding.clReason.isVisible) binding.etReason.text = null
            toggleButtonState()
        }
        //update status of this screen to verified when user landing on this screen
        //without any data.
        submitData(false)
    }

    private fun setupToolbar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_jar_ready_cash)
        binding.toolbar.btnBack.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = true
        Glide.with(requireContext())
            .load(R.drawable.feature_lending_ic_close_white)
            .into(binding.toolbar.ivEndImage)
        binding.toolbar.ivEndImage.updateLayoutParams {
            width = 24.dp
            height = 24.dp
        }
        binding.toolbar.separator.isVisible = true

        binding.toolbar.ivEndImage.setDebounceClickListener {
            navigateAhead()
        }
    }

    private fun setupListeners() {
        binding.etReason.textChanges()
            .debounce(300)
            .onEach {
                binding.ivReasonClear.isVisible = it.isNullOrBlank().not()
                toggleButtonState()
            }
            .launchIn(uiScope)

        binding.btnAction.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_NameUrReadyCashScreenSaveClicked,
                values = mapOf(
                    LendingEventKeyV2.ready_cash_needed_for to viewModel.selectedReason.orEmpty(),
                    LendingEventKeyV2.user_type to getUserType()
                )
            )
            viewModel.selectedReason?.let {
                submitData(
                    shouldNotify = true,
                    readyCashName = if (it != "Other") it else binding.etReason.text.toString(),
                    readyCashReason = it
                )
            }
        }

        binding.ivReasonClear.setDebounceClickListener {
            binding.etReason.setText("")
        }
    }

    private fun submitData(
        shouldNotify: Boolean,
        readyCashName: String? = null,
        readyCashReason: String? = null
    ) {
        val loanDetail = UpdateLoanDetailsBodyV2(
            applicationId = args.loanId,
            readyCashSubmitDetails = LoanNameData(
                readyCashName = readyCashName,
                readyCashReason = readyCashReason,
                status = LoanStatus.VERIFIED.name
            )
        )
        viewModel.submitData(shouldNotify, loanDetail)
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.reasonFlow.collect {
                    it?.let {
                        setupRecyclerview(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateReasonFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        fetchWithdrawalStatus()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        fetchWithdrawalStatus()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.staticContentFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.loanNameChips?.let {
                            viewModel.setReasonData(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            if (it.applicationDetails?.withdrawal?.status == LoanStatus.IN_PROGRESS.name ||
                                it.applicationDetails?.withdrawal?.status == LoanStatus.VERIFIED.name
                            ) {
                                navigateAhead()
                            } else {
                                getCustomString(MR.strings.feature_lending_something_went_wrong_please_try_again_later).snackBar(
                                    binding.root
                                )
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun fetchWithdrawalStatus() {
        parentViewModel.fetchLoanDetails(
            LendingConstants.LendingApplicationCheckpoints.WITHDRAWAL,
            true,
            args.loanId.orEmpty()
        )
    }

    private fun navigateAhead() {
        EventBus.getDefault()
            .post(LendingBackPressEvent(LendingEventKeyV2.NAME_YOUR_READY_CASH_SCREEN))
        if (args.isRepeatWithdrawal) {
            args.screenData?.let {
                EventBus.getDefault().postSticky(
                    ReadyCashNavigationEvent(
                        whichScreen = it.nextScreen,
                        source = args.screenName,
                        popupToId = R.id.loanReasonFragment
                    )
                )
            }
        } else {
            navigateTo(
                LendingStepsNavigationDirections.actionGlobalTransitionFragmentState(
                    TransitionStateScreenArgs(
                        transitionType = LendingConstants.TransitionType.ALL_DONE,
                        destinationDeeplink = null,
                        flowType = BaseConstants.FROM_LENDING,
                        loanId = args.loanId.orEmpty(),
                        isFromRepeatWithdrawal = args.isRepeatWithdrawal,
                        lender = args.lender
                    )
                ),
                popUpTo = R.id.loanReasonFragment,
                inclusive = true
            )
        }
    }

    private fun getData() {
        parentViewModel.staticContent?.loanNameChips?.let {
            viewModel.setReasonData(it)
        } ?: kotlin.run {
            parentViewModel.fetchStaticContent(
                LendingConstants.StaticContentType.LOAN_NAME_CHIPS,
                args.loanId.orEmpty()
            )
        }
    }

    private fun setupRecyclerview(reasonsList: List<ReasonData>) {
        reasonsAdapter?.submitList(reasonsList)
        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.FLEX_START
        binding.rvReasons.adapter = reasonsAdapter
        binding.rvReasons.layoutManager = layoutManager
    }

    private fun toggleButtonState(disableAnyway: Boolean = false) {
        val disable = disableAnyway ||
                (if (binding.clReason.isVisible) binding.etReason.text.isNullOrBlank()
                else viewModel.selectedReason.isNullOrBlank())
        binding.btnAction.setDisabled(disable)
    }

    override fun onDestroyView() {
        reasonsAdapter = null
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }
}