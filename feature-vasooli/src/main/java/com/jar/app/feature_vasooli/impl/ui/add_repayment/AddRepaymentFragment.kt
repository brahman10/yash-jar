package com.jar.app.feature_vasooli.impl.ui.add_repayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.VasooliNavigationDirections
import com.jar.app.feature_vasooli.databinding.FragmentAddRepaymentBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import com.jar.app.feature_vasooli.impl.domain.model.RepaymentEntryRequest
import com.jar.app.feature_vasooli.impl.ui.VasooliViewModel
import com.jar.app.feature_vasooli.impl.util.VasooliConstants
import com.jar.app.feature_vasooli.impl.util.VasooliDateValidator
import com.jar.app.feature_vasooli.impl.util.VasooliEndDateValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import javax.inject.Inject

@AndroidEntryPoint
internal class AddRepaymentFragment : BaseFragment<FragmentAddRepaymentBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        private const val TAG = "#AddRepaymentFragment#"
    }

    private val viewModel by viewModels<AddRepaymentViewModel> { defaultViewModelProviderFactory }

    private val vasooliViewModel by viewModels<VasooliViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<AddRepaymentFragmentArgs>()

    private var repaymentDate = 0L

    private var adapter: PaymentModeAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddRepaymentBinding
        get() = FragmentAddRepaymentBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        initClickListeners()
    }

    private fun setupUI() {
        setupToolbar()
        toggleMainButton()

        adapter = PaymentModeAdapter {
            viewModel.updateSelectedPaymentMode(it)
        }

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        layoutManager.alignItems = AlignItems.FLEX_START
        binding.rvPaymentMode.adapter = adapter
        binding.rvPaymentMode.layoutManager = layoutManager
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_vasooli_add_repayment)
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun initClickListeners() {
        binding.amountSubHeading.setDebounceClickListener {
            navigateTo(VasooliNavigationDirections.actionToPromptBottomSheetFragment())
        }

        binding.btnSave.setDebounceClickListener {
            clearFocus()
            validate()?.let {
                analyticsHandler.postEvent(
                    VasooliEventKey.VasooliRepayment.Clicked_SaveRepayment_Vasooli,
                    mapOf(
                        VasooliEventKey.Amount to it.amount.toString(),
                        VasooliEventKey.Date to it.repaidOn?.epochToDate()
                            ?.getFormattedDate("dd LLLL yyyy").toString(),
                        VasooliEventKey.PaymentMode to it.paymentMode.orEmpty()
                    )
                )
                viewModel.postRepaymentEntryRequest(it)
            }
        }

        binding.tvDate.setDebounceClickListener {
            clearFocus()
            showDatePicker()
        }

        binding.etAmount.textChanges()
            .debounce(100)
            .onEach {
                toggleMainButton()
            }
            .launchIn(uiScope)
    }

    private fun validate(): RepaymentEntryRequest? {
        val repaymentEntryRequest = RepaymentEntryRequest(
            loanId = args.loanId,
            amount = binding.etAmount.text?.toString()?.toInt(),
            repaidOn = repaymentDate,
            paymentMode = if (viewModel.selectedPaymentMode == null) null else getString(viewModel.selectedPaymentMode!!)
        )

        if (repaymentEntryRequest.amount?.orZero() == 0) {
            getString(R.string.feature_vasooli_enter_valid_amount).snackBar(binding.root)
            return null
        } else if (repaymentEntryRequest.amount.orZero() > args.dueAmount) {
            getString(R.string.feature_vasooli_repayment_greater_than_due).snackBar(binding.root)
            return null
        } else if (repaymentEntryRequest.amount?.orZero()!! > VasooliConstants.MAX_AMOUNT) {
            getString(
                R.string.feature_vasooli_amount_cannot_be_greater_than_x,
                VasooliConstants.MAX_AMOUNT.toString()
            ).snackBar(binding.root)
            return null
        } else if (repaymentEntryRequest.repaidOn == null) {
            getString(R.string.feature_vasooli_select_a_date).snackBar(binding.root)
            return null
        } else if (repaymentEntryRequest.paymentMode == null) {
            getString(R.string.feature_vasooli_select_payment_mode).snackBar(binding.root)
            return null
        }

        return repaymentEntryRequest
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewModel.repaymentEntryRequestLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                popBackStack()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                popBackStack()
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.paymentModeListLiveData.observe(this) {
            adapter?.submitList(it)
            toggleMainButton()
        }

        vasooliViewModel.networkStateLiveData.observe(viewLifecycleOwner) {
            binding.toolbar.clNetworkContainer.isSelected = it
            binding.toolbar.tvInternetConnectionText.text =
                if (it) getString(com.jar.app.core_ui.R.string.core_ui_we_are_back_online) else getString(
                    com.jar.app.core_ui.R.string.core_ui_no_internet_available_please_try_again)
            binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off, 0, 0, 0
            )
            if (it) {
                if (binding.toolbar.networkExpandableLayout.isExpanded) {
                    uiScope.launch {
                        delay(500)
                        binding.toolbar.networkExpandableLayout.collapse(true)
                    }
                }
            } else {
                binding.toolbar.networkExpandableLayout.expand(true)
            }
        }
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable =
            if (disableAnyway) false else (!binding.etAmount.text.isNullOrEmpty()
                    && !binding.tvDate.text.isNullOrEmpty()
                    && viewModel.selectedPaymentMode != null)
        binding.btnSave.setDisabled(!shouldEnable)
    }

    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
        val end = Instant.now(Clock.systemDefaultZone()).toEpochMilli()

        if (args.takenOnDate != 0L) {
            constraintsBuilder.setValidator(VasooliDateValidator(args.takenOnDate, end))
        } else {
            constraintsBuilder.setValidator(VasooliEndDateValidator(end))
        }

        val materialDatePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTheme(com.jar.app.core_ui.R.style.ThemeOverlay_App_DatePicker)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            val formattedDate = it.epochToDate().getFormattedDate("dd LLLL yyyy")
            repaymentDate = it
            binding.tvDate.text = formattedDate
            toggleMainButton()
        }
        materialDatePicker.show(childFragmentManager, TAG)
    }

    private fun clearFocus() {
        binding.etAmount.clearFocus()
    }
}