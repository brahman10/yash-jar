package com.jar.app.feature_lending.impl.ui.personal_details.employment

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.AccountPicker
import com.jar.app.base.data.event.LendingBackPressEvent
import com.jar.app.base.data.event.LendingToolbarTitleEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.isValidEmail
import com.jar.app.base.util.textChanges
import com.jar.app.base.util.toFloatOrZero
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setTypeAmount
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingEmploymentDetailsBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.impl.ui.agreement.BreakdownInfoBottomSheetFragment
import com.jar.app.feature_lending.impl.ui.host_container.LendingHostViewModelAndroid
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.EmploymentType
import com.jar.app.feature_lending.shared.domain.model.v2.EmploymentDetailsBody
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
internal class LendingEmploymentDetailsFragment :
    BaseFragment<FragmentLendingEmploymentDetailsBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var kycApi: LendingKycApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val parentViewModelProvider by activityViewModels<LendingHostViewModelAndroid> { defaultViewModelProviderFactory }
    private val parentViewModel by lazy {
        parentViewModelProvider.getInstance()
    }


    private val viewModelProvider by viewModels<LendingEmploymentDetailsViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var isSalaried = true

    private val emailPickIntentResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            sendEmailPopupEvent(LendingEventKeyV2.dismissed)
            if (result.resultCode == Activity.RESULT_OK) {
                val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                binding.etEmail.setText(accountName.orEmpty())
                binding.etEmail.setSelection(accountName.orEmpty().length)
                sendEmailPopupEvent(LendingEventKeyV2.account_selected,accountName)
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingEmploymentDetailsBinding
        get() = FragmentLendingEmploymentDetailsBinding::inflate

    private val arguments by navArgs<LendingEmploymentDetailsFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }
    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_PDetailsMainScreenLaunched,
            mapOf(LendingEventKeyV2.lender to args.lender.orEmpty())
            )
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        initClickListeners()
        setUpToolbar()
        setupUI()
        observeFlow()
        registerBackPressDispatcher()
        getData()
    }

    private fun setUpToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_lending_employment_details)
        binding.toolbar.separator.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.ivTitleImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                LendingEventKeyV2.Lending_BackButtonClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.EMPLOYMENT_SCREEN
                )
            )
            handleBackNavigation()
        }
    }

    private fun setupUI() {
        toggleActionButton(disableAnyway = true)
        setupEditTextFilters()
        args.screenData?.let {
            binding.toolbar.root.isVisible = it.shouldShowProgress.not()
        }
        EventBus.getDefault().post(LendingToolbarTitleEventV2(getCustomString(MR.strings.feature_lending_complete_kyc)))
        viewModelProvider.minSalaryAllowed = parentViewModel.readyCashJourney?.minSalary?.toFloat() ?: MIN_SALARY_DEFAULT
    }

    private fun initClickListeners() {
        binding.tvSalaried.setDebounceClickListener {
            isSalaried = true
            setRadioSelected(binding.tvSalaried)
            setRadioUnselected(binding.tvSelfEmployed)
            toggleEmploymentType()
        }

        binding.tvPreApprovedTitle.setDebounceClickListener {
            navigateTo(
                LendingEmploymentDetailsFragmentDirections.actionLendingEmploymentDetailsFragmentToBreakdownInfoBottomSheetFragment2(
                    parentViewModel.getLoanId(),
                    BreakdownInfoBottomSheetFragment.ARGS_ELIGIBILITY_RANGE
                )
            )
        }

        binding.tvSelfEmployed.setDebounceClickListener {
            isSalaried = false
            setRadioSelected(binding.tvSelfEmployed)
            setRadioUnselected(binding.tvSalaried)
            toggleEmploymentType()
        }

        binding.etIncome.textChanges()
            .debounce(100)
            .onEach {
                showErrorOnMonthlySalaryField(false)
                toggleActionButton()
                binding.ivIncomeClear.isVisible = it?.length.orZero() > 0
            }
            .launchIn(uiScope)
        binding.ivIncomeClear.setDebounceClickListener{
            binding.etIncome.text?.clear()
        }

        binding.etIncome.setOnFocusChangeListener { _, b ->
            binding.ivIncomeClear.isVisible = b && binding.etIncome.text?.length.orZero()>0
            if (b.not()) {
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_PDetailsSalaryAdded,
                    values = mapOf(
                        LendingEventKeyV2.salary to getRawAmount().toFloatOrZero()
                    )
                )
            } else {
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_EntryFieldSelected,
                    values = mapOf(
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.EMPLOYMENT_SCREEN,
                        LendingEventKeyV2.field_name to binding.headingIncome.text.toString()
                    )
                )
            }
        }

        binding.etEmail.setOnFocusChangeListener { _, b ->
            if (b) {
                if (!viewModelProvider.isPickerShownOnce) {
                    showPicker()
                }
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_EntryFieldSelected,
                    values = mapOf(
                        LendingEventKeyV2.screen_name to LendingEventKeyV2.EMPLOYMENT_SCREEN,
                        LendingEventKeyV2.field_name to binding.headingEmail.text.toString()
                    )
                )
            }
        }

        binding.etEmail.textChanges()
            .debounce(300)
            .onEach {
                binding.headingEmailError.isVisible = false
                toggleActionButton()
            }
            .launchIn(uiScope)

        binding.btnContinue.setDebounceClickListener {
            if (areInputsValid()) {
                analyticsApi.postEvent(
                    event = LendingEventKeyV2.Lending_PDetailsMainScreenContinueClicked,
                    values = mapOf(
                        LendingEventKeyV2.emp_type_selected to if (isSalaried) EmploymentType.SALARIED.name else EmploymentType.SELF_EMPLOYED.name,
                        LendingEventKeyV2.salary to getRawAmount().toFloatOrZero(),
                        LendingEventKeyV2.email to binding.etEmail.text?.toString().orEmpty(),
                        LendingEventKeyV2.lender to args.lender.orEmpty()
                    )
                )
                viewModel.updateEmploymentDetails(
                    UpdateLoanDetailsBodyV2(
                        applicationId = args.loanId,
                        emailId = binding.etEmail.text?.toString(),
                        pinCode = null,
                        employmentDetails = EmploymentDetailsBody(
                            companyName = null,
                            employmentType = if (isSalaried) EmploymentType.SALARIED.name else EmploymentType.SELF_EMPLOYED.name,
                            monthlyIncome = getRawAmount().toFloatOrZero()
                        )
                    )
                )
            }
        }
    }

    private fun areInputsValid(): Boolean {
        if (isIncomeInRange().not()) {
            val income = getRawAmount()
            if (income?.toFloatOrNull() == 0f) {
              showErrorOnMonthlySalaryField(true,getCustomString(MR.strings.feature_lending_monthly_income_cannot_be_0))
            } else if (income?.toFloatOrNull().orZero() < viewModelProvider.minSalaryAllowed) {
                showErrorOnMonthlySalaryField(true,getCustomStringFormatted(
                    MR.strings.feature_lending_monthly_income_less_than_min,
                    viewModelProvider.minSalaryAllowed.toInt().getFormattedAmount()
                ))
            } else if (income?.toFloatOrNull().orZero() > viewModelProvider.maxSalaryAllowed) {
                showErrorOnMonthlySalaryField(true,getCustomString(MR.strings.feature_lending_please_enter_an_amount_lesser_than_1cr))
            }
            return false
        } else if (binding.etEmail.text?.toString().orEmpty().isValidEmail().not()) {
            binding.headingEmailError.isVisible = true
            return false
        }
        return true
    }

    private fun showErrorOnMonthlySalaryField(isError:Boolean, errorMessage:String = ""){
        binding.etIncome.setBackgroundResource(if (isError) com.jar.app.core_ui.R.drawable.bg_rounded_corner_red else com.jar.app.core_ui.R.drawable.rounded_black_bg_12dp)
        binding.headingIncomeError.isVisible = isError
        binding.headingIncomeError.text = errorMessage
        if (isError){
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_PDetailsErrorScreen,
                values = mapOf(
                    LendingEventKeyV2.emp_type_selected to if (isSalaried) EmploymentType.SALARIED.name else EmploymentType.SELF_EMPLOYED.name,
                    LendingEventKeyV2.error_message to errorMessage,
                    LendingEventKeyV2.lender to args.lender.orEmpty()
                )
            )
        }
    }
    private fun updateText(et: EditText, text: String) {
        if (et.text.toString() != text) {
            et.setText(text)
            et.setSelection(et.length())
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                parentViewModel.loanApplicationFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        parentViewModel.loanApplicationItem = it?.getOrNull(0)
                        parentViewModel.loanApplicationItem?.let {
                            parentViewModel.fetchLoanDetails(LendingConstants.LendingApplicationCheckpoints.EMPLOYMENT_DETAILS)
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
                viewModel.updateEmploymentDetailsFlow.collect(
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
                                    popupToId = R.id.lendingEmploymentDetailsFragment,
                                    shouldCacheThisEvent = false
                                )
                            )
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
                parentViewModel.preApprovedDataFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        binding.tvOfferAmount.text = getCustomStringFormatted(
                            MR.strings.feature_lending_rupee_prefix_string,
                            it?.offerAmount.orZero().toInt().getFormattedAmount()
                        )
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
                    onSuccess = {
                        it?.let {
                            prefillEmploymentDetails(it)
                        }
                    }
                )
            }
        }
    }

    private fun prefillEmploymentDetails(loanDetailsV2: LoanDetailsV2) {
        loanDetailsV2.applicationDetails?.employment?.let {
            binding.etIncome.setText("${it.monthlyIncome?.orZero()}")
            if (it.employmentType.orEmpty() == EmploymentType.SALARIED.name) {
                binding.tvSalaried.performClick()
            } else
                binding.tvSelfEmployed.performClick()
        }
        loanDetailsV2.applicationDetails?.email?.let {
            uiScope.launch {
                delay(100L)
                binding.etEmail.setText(it.emailId.orEmpty())
            }
        }

        toggleActionButton()
    }

    private fun toggleEmploymentType() {
        analyticsApi.postEvent(
            event = LendingEventKeyV2.Lending_PDetailsEmpTypeSelected,
            values = mapOf(
                LendingEventKeyV2.emp_type_selected to if (isSalaried) EmploymentType.SALARIED.name else EmploymentType.SELF_EMPLOYED.name
            )
        )
        toggleInputView()
    }

    private fun toggleInputView() {
//        binding.groupCompanyName.isVisible = isSalaried
    }

    private fun setRadioSelected(textView: TextView) {
        textView.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        textView.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.feature_lending_ic_radio_selected, 0, 0, 0
        )
    }

    private fun setRadioUnselected(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_ACA1D3))
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.feature_lending_ic_radio_unselected, 0, 0, 0)
    }

    private fun toggleActionButton(disableAnyway: Boolean = false) {
        val isDisabled = if (disableAnyway) {
            true
        } else if (isSalaried) {
            getRawAmount().isNullOrBlank()
                    || binding.etIncome.text.isNullOrBlank()
                    || isValidEmail().not()
        } else {
            getRawAmount().isNullOrBlank()
                    || binding.etIncome.text.isNullOrBlank()
                    || isValidEmail().not()
        }

        binding.btnContinue.setDisabled(isDisabled = isDisabled)
    }

    private fun isValidEmail():Boolean{
        return binding.etEmail.text.isNullOrBlank().not() && binding.etEmail.text?.matches(Patterns.EMAIL_ADDRESS.toRegex()).orFalse()
    }

    private fun setupEditTextFilters() {
        binding.etIncome.filters = arrayOf(InputFilter.LengthFilter(8))
        binding.etIncome.setTypeAmount(shouldAllowDecimal = false)
    }

    private fun isIncomeInRange() =
        getRawAmount()?.toFloatOrNull().orZero() in viewModelProvider.minSalaryAllowed..viewModelProvider.maxSalaryAllowed

    private fun getRawAmount() = binding.etIncome.text?.toString()?.replace(",", "")

    private fun getData() {
        parentViewModel.preApprovedData?.let {
            binding.tvOfferAmount.text = getCustomStringFormatted(
                MR.strings.feature_lending_rupee_prefix_string,
                it.offerAmount.orZero().toInt().getFormattedAmount()
            )
        } ?: run {
            parentViewModel.fetchPreApprovedData()
        }

        parentViewModel.loanApplicationItem?.let {
            parentViewModel.fetchLoanDetails(LendingConstants.LendingApplicationCheckpoints.EMPLOYMENT_DETAILS)
        } ?: run {
            parentViewModel.fetchLoanList()
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun handleBackNavigation() {
        EventBus.getDefault().post(LendingBackPressEvent(LendingEventKeyV2.PDETAILS_MAIN_SCREEN))

        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.lendingEmploymentDetailsFragment,
                    isBackFlow = true
                )
            )
        }
    }

    private fun showPicker() {
        viewModelProvider.isPickerShownOnce = true
        val intent: Intent = AccountPicker.newChooseAccountIntent(
            AccountPicker.AccountChooserOptions.Builder()
                .setAllowableAccountsTypes(mutableListOf("com.google"))
                .setTitleOverrideText(getCustomString(MR.strings.feature_lending_choose_your_email))
                .build()
        )

        emailPickIntentResultLauncher.launch(intent)
        sendEmailPopupEvent(LendingEventKeyV2.shown)
    }

    private fun sendEmailPopupEvent(action:String, email:String? = null){
        analyticsApi.postEvent(
            LendingEventKeyV2.Lending_PDetailsEmailPopUpLaunched,
            mapOf(
                LendingEventKeyV2.action to action,
                LendingEventKeyV2.lender to args.lender.orEmpty()
            ).apply {
                email?.let {
                    LendingEventKeyV2.email to it
                }
            }
        )
    }

    companion object {
        const val MIN_SALARY_DEFAULT = 5_000f
        const val MAX_SALARY_DEFAULT = 1_00_00_000f
    }
}