package com.jar.app.feature_lending.impl.ui.personal_details.address.select_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.LendingStepsNavigationDirections
import com.jar.app.feature_lending.databinding.FragmentLendingSelectAddressBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.app.feature_lending.shared.domain.model.temp.LendingAddress
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingSelectAddressFragment : BaseFragment<FragmentLendingSelectAddressBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var adapter: LendingAddressAdapter? = null

    private val args by navArgs<LendingSelectAddressFragmentArgs>()

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private val viewModelProvider by viewModels<LendingSelectAddressViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private var baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingSelectAddressBinding
        get() = FragmentLendingSelectAddressBinding::inflate

    private var isNewAddressSynced = false

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val employmentDetails =
                    lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_PersonalDetails_Address_Back,
                    mapOf(
                        LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                        LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                        LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome()
                            .orZero(),
                        LendingEventKey.entryPoint to args.flowType,
                        LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                        LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                        LendingEventKey.selectedAddress to viewModel.selectedAddress?.address.orEmpty()
                    )
                )
                lendingViewModel.toolbarBackNavigation(
                    findNavController().currentBackStackEntry,
                    contextRef = WeakReference(requireActivity()),
                    flowType = args.flowType
                )
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = true, LendingStep.KYC)
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupConfirmationFragmentListener()
        getData()
        observeFlow()
        setupUI()
        initClickListener()
        registerBackPressDispatcher()
    }

    private fun getData() {
        resetSelectedAddress()
        viewModel.fetchAddressList()
    }

    private fun observeFlow() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.addressListFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        adapter?.submitList(it.addresses)
                        if (viewModel.selectedAddress == null) {
                            if (args.isNewAddressAdded) {
                                it.addresses.getOrNull(0)?.let { address ->
                                    preSelectAddress(address)
                                } ?: kotlin.run {
                                    sendShownEvent()
                                }
                            } else {
                                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.ADDRESS?.let { address ->
                                    preSelectAddress(address)
                                } ?: kotlin.run {
                                    sendShownEvent()
                                }
                            }
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
                viewModel.updateAddressFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateAhead()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun preSelectAddress(address: Address) {
        selectAddress(address)
        toggleMainButton()
        sendShownEvent()
    }

    private fun initClickListener() {
        binding.btnContinue.setDebounceClickListener {
            val employmentDetails =
                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.OnClick_ReadyCash_PersonalDetails_Address_Continue,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                    LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                    LendingEventKey.selectedAddress to viewModel.selectedAddress?.address.orEmpty()
                )
            )
            viewModel.selectedAddress?.let {
                navigateTo(
                    LendingStepsNavigationDirections.actionToLendingConfirmDetailsFragment(
                        title = getCustomString(MR.strings.feature_lending_confirm_your_details),
                        des = getCustomString(MR.strings.feature_lending_confirm_details_desc),
                        positiveCtaText = getString(com.jar.app.core_ui.R.string.core_ui_confirm),
                        negativeCtaText = getCustomString(MR.strings.feature_lending_review_details),
                        bankDataEncoded = null
                    )
                )
            }
        }

        binding.tvAddNewAddress.setDebounceClickListener {
            val employmentDetails =
                lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddress,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                    LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                    LendingEventKey.selectedAddress to viewModel.selectedAddress?.address.orEmpty()
                )
            )
            navigateTo(
                LendingSelectAddressFragmentDirections.actionLendingSelectAddressFragmentToLendingAddressOptionFragment(
                    flowType = args.flowType
                )
            )
        }
    }

    private fun setupConfirmationFragmentListener() {
        setFragmentResultListener(
            LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_CTA_REQUEST_KEY
        ) { _, bundle ->
            when (bundle.getString(LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_SELECTED_CTA)) {
                LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_POSITIVE_CTA -> {
                    val employmentDetails =
                        lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_PersonalDetails_Confirm,
                        mapOf(
                            LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                            LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                            LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome()
                                .orZero(),
                            LendingEventKey.entryPoint to args.flowType,
                            LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                            LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                            LendingEventKey.selectedAddress to viewModel.selectedAddress?.address.orEmpty()
                        )
                    )
                    viewModel.selectedAddress?.addressCategory =
                        viewModel.selectedAddress?.addressCategory
                            ?: LendingConstants.ADDRESS_CATEGORY
                    val user =
                        serializer.decodeFromString<User?>(prefs.getUserStringSync().orEmpty())
                    if (viewModel.selectedAddress?.name.isNullOrEmpty()) {
                        viewModel.selectedAddress?.name = user?.getFullName().orEmpty()
                    }
                    if (viewModel.selectedAddress?.phoneNumber.isNullOrEmpty()) {
                        viewModel.selectedAddress?.phoneNumber = user?.phoneNumber
                    }
                    val lendingAddress = LendingAddress(
                        applicationId = lendingViewModel.getLoanId(),
                        address = viewModel.selectedAddress!!
                    )
                    if (lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.ADDRESS?.addressId.orEmpty() == viewModel.selectedAddress!!.addressId) {
                        navigateAhead()
                    } else {
                        viewModel.updateAddressDetails(lendingAddress)
                    }
                }
                LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_NEGATIVE_CTA -> {
                    val employmentDetails =
                        lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_PersonalDetails_Review,
                        mapOf(
                            LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                            LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                            LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome()
                                .orZero(),
                            LendingEventKey.entryPoint to args.flowType,
                            LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                            LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                            LendingEventKey.selectedAddress to viewModel.selectedAddress?.address.orEmpty()
                        )
                    )
//                    navigateTo(
//                        LendingConfirmDetailsBottomSheetFragmentDirections.actionToLendingEmploymentDetailsFragment(
//                            flowType = args.flowType
//                        ),
//                        popUpTo = R.id.lendingEmploymentDetailsFragment,
//                        inclusive = true
//                    )
                }
            }
        }
    }

    private fun navigateAhead() {
        resetSelectedAddress()
        lendingViewModel.fetchLendingProgress()
    }

    private fun setupUI() {
        toggleMainButton()
        adapter = LendingAddressAdapter(
            onAddressClicked = {
                selectAddress(it)
                toggleMainButton()
            },
            onEditAddressClicked = {
                val employmentDetails =
                    lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_PersonalDetails_EditAddress,
                    mapOf(
                        LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                        LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                        LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome()
                            .orZero(),
                        LendingEventKey.entryPoint to args.flowType,
                        LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                        LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                        LendingEventKey.selectedAddress to it.address.orEmpty()
                    )
                )
                navigateTo(
                    LendingSelectAddressFragmentDirections.actionLendingSelectAddressFragmentToLendingAddAddressFragment(
                        address = it,
                        null,
                        null,
                        null,
                        flowType = args.flowType,
                        newAddressAddedVia = args.newAddressAddedVia
                    )
                )
            }
        )
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvAddress.adapter = adapter

        binding.tvAddNewAddress.isVisible = args.isNewAddressAdded.not()
    }

    private fun toggleMainButton() {
        val isDisabled = viewModel.selectedAddress == null
        binding.btnContinue.setDisabled(isDisabled = isDisabled)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }

    override fun onDestroyView() {
        resetSelectedAddress()
        backPressCallback.isEnabled = false
        isNewAddressSynced = false
        super.onDestroyView()
    }

    private fun resetSelectedAddress() {
        viewModel.selectedAddress = null
    }

    private fun selectAddress(address: Address) {
        if (args.isNewAddressAdded && !isNewAddressSynced) {
            address.isEditable = args.isNewAddressAdded
            isNewAddressSynced = true
        }
        viewModel.selectAddress(address)
    }

    private fun sendShownEvent() {
        val employmentDetails =
            lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
        analyticsHandler.postEvent(
            LendingEventKey.Shown_ReadyCash_PersonalDetails_Address,
            mapOf(
                LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                LendingEventKey.entryPoint to args.flowType,
                LendingEventKey.chooseAddress to if (viewModel.selectedAddress != null) if (args.isNewAddressAdded) LendingEventKey.new else LendingEventKey.existing else "",
                LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                LendingEventKey.selectedAddress to viewModel.selectedAddress?.address.orEmpty()
            )
        )
    }
}