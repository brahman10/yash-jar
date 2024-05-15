package com.jar.app.feature_spends_tracker.impl.ui.report_transaction_bs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_spends_tracker.R
import com.jar.app.feature_spends_tracker.databinding.FeatureTransactionReportBottomsheetBinding
import com.jar.app.feature_spends_tracker.shared.domain.events.SpendsTrackerEvent
import com.jar.app.feature_spends_tracker.shared.domain.model.report_transaction.ReportType

import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class ReportTransactionBottomSheet :
    BaseBottomSheetDialogFragment<FeatureTransactionReportBottomsheetBinding>() {

    private val args by navArgs<ReportTransactionBottomSheetArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val transactionData by lazy {
        args.transactionDetails
    }
    private var reportReason = ""

    private val viewModel by viewModels<ReportTransactionViewModel> {
        defaultViewModelProviderFactory
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionReportBottomsheetBinding
        get() = FeatureTransactionReportBottomsheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.reportTransactionLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccessWithNullData = {
                popBackStack()
            }, onSuccess = {
                popBackStack()
            }
        )
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            analyticsHandler.postEvent(
                SpendsTrackerEvent.ST_CrossreportClicked
            )
            popBackStack()
        }
        binding.selfTransfer.setOnClickListener { onRadioButtonClicked(it) }
        binding.wrongAmount.setOnClickListener { onRadioButtonClicked(it) }
        binding.btnSubmit.setDebounceClickListener {

            analyticsHandler.postEvent(
                SpendsTrackerEvent.ST_SubmitreportClicked,
                mapOf(
                    SpendsTrackerEvent.reason_selected to reportReason,
                )
            )
            if (reportReason.isNotBlank()) {
                viewModel.reportTransaction(transactionData.txnId, reportReason)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        Glide.with(binding.root.context)
            .load(transactionData.spendsIcon)
            .override(46.dp, 51.dp)
            .into(binding.ivTransactionIcon)
        binding.apply {
            tvSpendsTodayLabel.text = transactionData.header
            tvTransactionDateTime.text =
                "${transactionData.txnDate} | ${transactionData.txnTime}"
            tvTransactionBeneficiary.text =
                "${transactionData.paidToText} : ${transactionData.beneDetails}"
            tvAmountSpent.text = "-${transactionData.amount}"

            ivReportFlag.isVisible = false


        }
    }

    private fun onRadioButtonClicked(view: View) {
        // Manually handle the selection logic
        when (view.id) {
            R.id.selfTransfer -> {
                reportReason = ReportType.SELF_TRANSFER.name
                binding.checkboxWrongAmount.isChecked = false
                binding.checkboxSelfTransfer.isChecked = true

            }

            R.id.wrongAmount -> {
                reportReason = ReportType.WRONG_AMOUNT.name
                binding.checkboxSelfTransfer.isChecked = false
                binding.checkboxWrongAmount.isChecked = true

            }
        }
    }
}