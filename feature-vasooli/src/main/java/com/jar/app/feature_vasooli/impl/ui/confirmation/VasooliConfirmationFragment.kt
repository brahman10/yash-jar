package com.jar.app.feature_vasooli.impl.ui.confirmation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.FragmentVasooliConfirmationBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import com.jar.app.feature_vasooli.impl.domain.event.RepaymentUpdatedEvent
import com.jar.app.feature_vasooli.impl.domain.model.RepaymentEntryRequest
import com.jar.app.feature_vasooli.impl.domain.model.UpdateStatusRequest
import com.jar.app.feature_vasooli.impl.domain.model.VasooliConfirmation
import com.jar.app.feature_vasooli.impl.domain.model.VasooliStatus
import com.jar.app.feature_vasooli.impl.ui.add_repayment.PaymentModeAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class VasooliConfirmationFragment: BaseBottomSheetDialogFragment<FragmentVasooliConfirmationBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<VasooliConfirmationViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<VasooliConfirmationFragmentArgs>()

    private var adapter: PaymentModeAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVasooliConfirmationBinding
        get() = FragmentVasooliConfirmationBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false)

    override fun setup() {
        observeLiveData()
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        binding.tvTitle.setText(args.vasooliConfirmation.title)
        binding.btnAction.setText(getString(args.vasooliConfirmation.actionText))
        binding.tvCancel.setText(args.vasooliConfirmation.dismissText)
        binding.clPayment.isVisible = args.vasooliConfirmation.showPaymentMode
        binding.ivIcon.isVisible = args.vasooliConfirmation.iconRes != null
        args.vasooliConfirmation.iconRes?.let {
            binding.ivIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), it
                )
            )
        }

        if (args.vasooliConfirmation.name == VasooliConfirmation.MARK_AS_PAID.name) {
            viewModel.getPaymentModeList()
            //IF STATUS IS MARKED AS FULLY REPAID
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
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            if (args.vasooliConfirmation.name == VasooliConfirmation.MARK_AS_PAID.name) {
                analyticsHandler.postEvent(
                    VasooliEventKey.VasooliConfirmation.Clicked_FullyPaidScreen_Vasooli,
                    mapOf(
                        VasooliEventKey.Button to VasooliEventKey.No,
                    )
                )
            }
            dismissAllowingStateLoss()
        }

        binding.btnAction.setDebounceClickListener {
            when (args.vasooliConfirmation.name) {
                VasooliConfirmation.MARK_DEFAULT.name -> {
                    val updateStatusRequest = UpdateStatusRequest(
                        loanId = args.loanId,
                        status = VasooliStatus.DEFAULT.name
                    )
                    viewModel.updateVasooliStatus(updateStatusRequest)
                }
                VasooliConfirmation.MARK_AS_PAID.name -> {
                    validate()?.let {
                        analyticsHandler.postEvent(
                            VasooliEventKey.VasooliConfirmation.Clicked_FullyPaidScreen_Vasooli,
                            mapOf(
                                VasooliEventKey.Button to VasooliEventKey.FullVasool,
                                VasooliEventKey.PaymentMode to it.paymentMode.orEmpty()
                            )
                        )
                        viewModel.postRepaymentEntryRequest(it)
                    }
                }
                VasooliConfirmation.DELETE_RECORD.name -> {
                    viewModel.deleteVasooliEntry(args.loanId)
                }
            }
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewModel.deleteVasooliEntryLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                popBackStack(R.id.vasooliHomeFragment, inclusive = false)
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                popBackStack(R.id.vasooliHomeFragment, inclusive = false)
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.updateVasooliStatusLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                EventBus.getDefault().post(RepaymentUpdatedEvent())
                dismissAllowingStateLoss()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                EventBus.getDefault().post(RepaymentUpdatedEvent())
                dismissAllowingStateLoss()
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.repaymentEntryRequestLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                EventBus.getDefault().post(RepaymentUpdatedEvent())
                dismissAllowingStateLoss()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                EventBus.getDefault().post(RepaymentUpdatedEvent())
                dismissAllowingStateLoss()
            },
            onError = {
                dismissProgressBar()
            }
        )

        //IF STATUS IS MARKED AS FULLY REPAID
        viewModel.paymentModeListLiveData.observe(this) {
            adapter?.let { adapter ->
                adapter.submitList(it)
                toggleMainButton()
            }
        }
    }

    //IF STATUS IS MARKED AS FULLY REPAID
    private fun validate(): RepaymentEntryRequest? {
        val repaymentEntryRequest = RepaymentEntryRequest(
            loanId = args.loanId,
            amount = args.dueAmount,
            repaidOn = System.currentTimeMillis(),
            paymentMode = if (viewModel.selectedPaymentMode == null) null else getString(viewModel.selectedPaymentMode!!)
        )

        if (repaymentEntryRequest.paymentMode == null) {
            getString(R.string.feature_vasooli_select_payment_mode).snackBar(binding.root)
            return null
        }

        return repaymentEntryRequest
    }

    //IF STATUS IS MARKED AS FULLY REPAID
    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable =
            if (disableAnyway) false else (viewModel.selectedPaymentMode != null)
        binding.btnAction.setDisabled(!shouldEnable)
    }
}