package com.jar.app.feature_lending.impl.ui.bank.confirm_bottomsheet

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentLendingConfirmDetailsBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.v2.BankDataDto
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingConfirmDetailsBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentLendingConfirmDetailsBinding>() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<LendingConfirmDetailsBottomSheetFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingConfirmDetailsBinding
        get() = FragmentLendingConfirmDetailsBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        binding.tvTitle.text = args.title
        binding.tvDescription.text = args.des
        binding.btnPositiveCta.setText(args.positiveCtaText.toSpannable())
        binding.btnNegativeCta.text = args.negativeCtaText
        binding.btnNegativeCta.paintFlags = binding.btnNegativeCta.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        args.bankDataEncoded?.let {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_ReviewBankDetailsBSShown,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.shown
                )
            )
            val bankData = serializer.decodeFromString<BankDataDto>(decodeUrl(it))
            binding.clBankDetails.isVisible = true
            binding.tvBankName.text = bankData.bankName
            Glide.with(requireContext()).load(bankData.bankLogoUrl).into(binding.ivBankLogo)
            binding.tvAccountNumber.text = bankData.accountNumber
        }
    }

    private fun initClickListeners() {
        binding.ivClose.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_CrossButtonClicked,
                values = mapOf(
                    LendingEventKeyV2.screen_name to LendingEventKeyV2.BANK_DETAILS_REVIEW_SCREEN
                )
            )
            dismissAllowingStateLoss()
        }

        binding.btnPositiveCta.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_ReviewBankDetailsBSShown,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.continue_clicked
                )
            )
            findNavController().navigateUp()
            setFragmentResult(
                LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_CTA_REQUEST_KEY,
                bundleOf(
                    Pair(
                        LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_SELECTED_CTA,
                        LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_POSITIVE_CTA
                    )
                )
            )
        }

        binding.btnNegativeCta.setDebounceClickListener {
            analyticsApi.postEvent(
                event = LendingEventKeyV2.Lending_ReviewBankDetailsBSShown,
                values = mapOf(
                    LendingEventKeyV2.action to LendingEventKeyV2.change_details_clicked
                )
            )
            findNavController().navigateUp()
            setFragmentResult(
                LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_CTA_REQUEST_KEY,
                bundleOf(
                    Pair(
                        LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_SELECTED_CTA,
                        LendingConstants.LendingConfirmDetails.LENDING_CONFIRM_DETAILS_NEGATIVE_CTA
                    )
                )
            )
        }
    }
}