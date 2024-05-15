package com.jar.app.feature_lending.impl.ui.bank.enter_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.hideKeyboard
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentBankDetailsBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.v2.BankVerificationDetails
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
internal class BankDetailsFragment : BaseFragment<FragmentBankDetailsBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider by viewModels<BankDetailsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }

    private var adapter: BankCheckListAdapter? = null

    private val arguments by navArgs<BankDetailsFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBankDetailsBinding
        get() = FragmentBankDetailsBinding::inflate


    companion object {
        const val MIN_ACCOUNT_NO_LENGTH = 8
        const val EMPTY_SPACE = " "
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsHandler.postEvent(
            event = LendingEventKeyV2.Lending_BankDetailsScreenLaunched,
            values = mapOf(
                LendingEventKeyV2.action to LendingEventKeyV2.bank_details_screen_shown
            )
        )
    }

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
//        setupConfirmationFragmentListener()
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        toggleActionButton(true)
        adapter = BankCheckListAdapter()
        binding.rvStaticInfo.adapter = adapter

        parentViewModel.preApprovedData?.creditProviderLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivCreditProvider)
        }

        parentViewModel.preApprovedData?.npciLogoUrl?.let {
            Glide.with(requireContext()).load(it).into(binding.ivNpci)
        }
        EventBus.getDefault()
            .post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_select_complete_bank_details)))

    }

    private fun setupListeners() {
        binding.etIfscCode.textChanges()
            .debounce(500)
            .onEach {
                if (it?.length.orZero() == 11)
                    viewModel.verifyIfscCode(it.toString())
                else
                    resetIfscLayout(it)
            }.launchIn(uiScope)

        binding.etIfscCode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                analyticsHandler.postEvent(
                    event = LendingEventKeyV2.Lending_BankDetailsScreenLaunched,
                    values = mapOf(
                        LendingEventKeyV2.action to LendingEventKeyV2.IFSC_CODE_TYPED
                    )
                )
            }
        }

        binding.etConfirmAccountNumber.textChanges()
            .debounce(500)
            .onEach {
                toggleActionButton()
                toggleConfirmAccountError(false)
                if (it.isNullOrEmpty())
                    binding.ivConfirmClear.setImageResource(0)
                else {
                    binding.ivConfirmClear.setImageResource(
                        if (getRawAccountNumber() == getRawConfirmAccountNumber()) com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick
                        else R.drawable.feature_lending_ic_cross_circle_filled
                    )

                    val eventName =
                        if (getRawAccountNumber() == getRawConfirmAccountNumber()) LendingEventKeyV2.right else LendingEventKeyV2.wrong

                    analyticsHandler.postEvent(
                        LendingEventKeyV2.Lending_BankDetailsConfirmAccountNumber,
                        mapOf(
                            LendingEventKeyV2.status to eventName
                        )
                    )
                }
            }.launchIn(uiScope)

        binding.etConfirmAccountNumber.doAfterTextChanged { text ->
            getFormattedAccountNumber(text.toString())?.let {
                binding.etConfirmAccountNumber.setText(it)
                binding.etConfirmAccountNumber.setSelection(binding.etConfirmAccountNumber.length())
            }
        }

        binding.etConfirmAccountNumber.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                analyticsHandler.postEvent(
                    event = LendingEventKeyV2.Lending_BankDetailsScreenLaunched,
                    values = mapOf(
                        LendingEventKeyV2.action to LendingEventKeyV2.CONFIRM_ACCOUNT_NUMBER_TYPED
                    )
                )
            }
        }

        binding.etAccountNumber.textChanges()
            .debounce(500)
            .onEach {
                toggleActionButton()
                toggleAccountError(false)
                binding.ivAccountClear.isVisible = getRawAccountNumber().isNotEmpty()
            }.launchIn(uiScope)

        binding.etAccountNumber.doAfterTextChanged {
            getFormattedAccountNumber(it?.toString().orEmpty())?.let {
                binding.etAccountNumber.setText(it)
                binding.etAccountNumber.setSelection(binding.etAccountNumber.length())
            }
        }
        binding.etAccountNumber.setOnFocusChangeListener { _, hasFocus ->
            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                binding.ivAccountClear.isVisible = hasFocus && getRawAccountNumber().isNotEmpty()
            }
            if (hasFocus) {
                analyticsHandler.postEvent(
                    event = LendingEventKeyV2.Lending_BankDetailsScreenLaunched,
                    values = mapOf(
                        LendingEventKeyV2.action to LendingEventKeyV2.ACCOUNT_NUMBER_TYPED
                    )
                )
            }
        }

        binding.ivIfscClear.setDebounceClickListener {
            binding.etIfscCode.text?.clear()
        }

        binding.ivAccountClear.setDebounceClickListener {
            binding.etAccountNumber.text?.clear()
        }

        binding.ivConfirmClear.setDebounceClickListener {
            binding.etConfirmAccountNumber.text?.clear()
        }

        binding.btnAction.setDebounceClickListener {
            if (areDetailsValid()) {
                viewModel.verifyBankAccount(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId,
                        bankVerificationDetails = BankVerificationDetails(
                            accountNumber = getRawConfirmAccountNumber(),
                            bankName = viewModel.ifscData?.BANK,
                            ifsc = viewModel.ifscData?.IFSC,
                            bankLogo = viewModel.ifscData?.bankLogo
                        )
                    )
                )
                analyticsHandler.postEvent(
                    event = LendingEventKeyV2.Lending_BankDetailsScreenLaunched,
                    values = mapOf(
                        LendingEventKeyV2.action to LendingEventKeyV2.bank_details_screen_continue_clicked
                    )
                )
                //This will be commented for now as we are disabling BottomSheet

//                val bankData = BankDataDto(
//                    viewModel.ifscData?.bankLogo.orEmpty(),
//                    bankName = viewModel.ifscData?.BANK.orEmpty(),
//                    accountNumber = getRawConfirmAccountNumber()
//                )
//                val encodedData = encodeUrl(serializer.encodeToString(bankData))
//                navigateTo(
//                    LendingStepsNavigationDirections.actionToLendingConfirmDetailsFragment(
//                        title = getCustomString(MR.strings.feature_lending_review_details),
//                        des = getCustomString(MR.strings.feature_lending_make_sure_details_are_correct),
//                        positiveCtaText = getCustomString(MR.strings.feature_lending_continue),
//                        negativeCtaText = getCustomString(MR.strings.feature_lending_change_details_underline),
//                        bankDataEncoded = encodedData
//                    )
//                )
            }
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.ifscFlow.collect(
                    onSuccess = {
                        analyticsHandler.postEvent(
                            LendingEventKeyV2.Lending_BankDetailsIFSC,
                            mapOf(
                                LendingEventKeyV2.IFSC_CODE_TYPED to binding.etIfscCode.text.toString(),
                                LendingEventKeyV2.status to LendingEventKeyV2.right
                            )
                        )
                        toggleIfscError(false)
                        it?.let {
                            binding.ivBankLogo.isVisible = true
                            binding.tvBankName.isVisible = true
                            viewModel.ifscData = it
                            binding.ivIfscClear.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick)
                            Glide.with(requireContext())
                                .load(it.bankLogo)
                                .into(binding.ivBankLogo)
                            binding.tvBankName.text =
                                "${it.BANK.orEmpty()}, ${it.BRANCH.orEmpty()}, ${it.ADDRESS.orEmpty()}"
                            toggleActionButton()
                        }
                        requireContext().hideKeyboard(binding.root)
                    },
                    onError = { errorMessage, _ ->
                        analyticsHandler.postEvent(
                            LendingEventKeyV2.Lending_BankDetailsIFSC,
                            mapOf(
                                LendingEventKeyV2.IFSC_CODE_TYPED to binding.etIfscCode.text.toString(),
                                LendingEventKeyV2.status to LendingEventKeyV2.right
                            )
                        )
                        toggleIfscError(true, errorMessage)
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
                        it?.bankContent?.let {
                            setData(it)
                        }
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.accountVerificationFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        args.screenData?.let {
                            EventBus.getDefault().postSticky(
                                ReadyCashNavigationEvent(
                                    whichScreen = it.nextScreen,
                                    source = args.screenName,
                                    popupToId = R.id.bankDetailsFragment
                                )
                            )
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

    private fun getData() {
        parentViewModel.fetchStaticContent(
            LendingConstants.StaticContentType.BANK_SCREEN,
            args.loanId.orEmpty()
        )
    }

    private fun setData(list: List<String>) {
        adapter?.submitList(list)
    }

    private fun toggleIfscError(isError: Boolean, message: String = "") {
        binding.clIfscCode.setBackgroundResource(if (isError) com.jar.app.core_ui.R.drawable.bg_rounded_corner_red else com.jar.app.core_ui.R.drawable.rounded_black_bg_12dp)
        binding.tvIfscError.text = message
        binding.tvIfscError.isVisible = isError
    }

    private fun toggleConfirmAccountError(isError: Boolean, message: String = "") {
        binding.clConfirmAccount.setBackgroundResource(if (isError) com.jar.app.core_ui.R.drawable.bg_rounded_corner_red else com.jar.app.core_ui.R.drawable.rounded_black_bg_12dp)
        binding.tvConfirmError.text = message
        binding.tvConfirmError.isVisible = isError
    }

    private fun toggleAccountError(isError: Boolean, message: String = "") {
        binding.clAccountNumber.setBackgroundResource(if (isError) com.jar.app.core_ui.R.drawable.bg_rounded_corner_red else com.jar.app.core_ui.R.drawable.rounded_black_bg_12dp)
        binding.tvAccountError.text = message
        binding.tvAccountError.isVisible = isError
    }

    private fun areDetailsValid(): Boolean {
        val account = getRawAccountNumber()
        val confirmAccount = getRawConfirmAccountNumber()
        if (viewModel.ifscData == null) {
            toggleIfscError(true, getCustomString(MR.strings.feature_lending_enter_ifsc_code))
            return false
        } else if (account.isEmpty() || confirmAccount.isEmpty() || account != confirmAccount) {
            toggleConfirmAccountError(
                true,
                getCustomString(MR.strings.feature_lending_account_doesnt_match)
            )
            return false
        } else if (account.length.orZero() < MIN_ACCOUNT_NO_LENGTH) {
            toggleAccountError(
                true,
                getCustomString(MR.strings.feature_lending_account_number_8_digit_error)
            )
            return false
        }
        return true
    }

    private fun resetIfscLayout(ifsc: CharSequence?) {
        viewModel.ifscData = null
        toggleActionButton(true)
        if (ifsc.isNullOrEmpty())
            binding.ivIfscClear.setImageResource(0)
        else {
            binding.ivIfscClear.setImageResource(R.drawable.feature_lending_ic_cross_circle_filled)
        }
        toggleIfscError(false)
        binding.tvConfirmError.isVisible = false
        binding.ivBankLogo.isVisible = false
        binding.tvBankName.isVisible = false
    }

    private fun setupConfirmationFragmentListener() {
        setFragmentResultListener(
            LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_CTA_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_SELECTED_CTA)) {
                LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_POSITIVE_CTA -> {
                    viewModel.verifyBankAccount(
                        UpdateLoanDetailsBodyV2(
                            applicationId = args.loanId,
                            bankVerificationDetails = BankVerificationDetails(
                                accountNumber = getRawConfirmAccountNumber(),
                                bankName = viewModel.ifscData?.BANK,
                                ifsc = viewModel.ifscData?.IFSC,
                                bankLogo = viewModel.ifscData?.bankLogo
                            )
                        )
                    )
                }

                LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_NEGATIVE_CTA -> {
                    //Bottom sheet dismissed. Do Nothing
                }
            }
        }
    }

    private fun toggleActionButton(disableAnyway: Boolean = false) {
        val account = getRawAccountNumber()
        val confirmAccount = getRawConfirmAccountNumber()

        val isDisabled = (disableAnyway
                || viewModel.ifscData == null
                || account.isEmpty()
                || confirmAccount.isEmpty())

        binding.btnAction.setDisabled(isDisabled = isDisabled)
    }

    private fun getFormattedAccountNumber(input: String): String? {
        return if (input.length <= 10) {
            val formattedText = input.replace(EMPTY_SPACE, "").chunked(4).joinToString(EMPTY_SPACE)
            if (formattedText != input) formattedText else null
        } else {
            null
        }
    }

    private fun getRawAccountNumber() =
        binding.etAccountNumber.text?.toString().orEmpty().replace(EMPTY_SPACE, "")

    private fun getRawConfirmAccountNumber() =
        binding.etConfirmAccountNumber.text?.toString().orEmpty().replace(EMPTY_SPACE, "")

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        adapter = null
        super.onDestroyView()
    }

    private fun handleBackNavigation() {
        EventBus.getDefault()
            .post(LendingBackPressEvent(LendingEventKeyV2.BANK_DETAILS_LAUNCH_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.bankDetailsFragment,
                    isBackFlow = true
                )
            )
        }
    }
}