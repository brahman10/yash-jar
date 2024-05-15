package com.jar.app.feature_lending_kyc.impl.ui.kyc_verified

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.getDateMonthNameAndYear
import com.jar.app.base.util.getMonthNameDateFromDDMMYYYY
import com.jar.app.core_base.domain.model.AADHAAR
import com.jar.app.core_base.domain.model.PAN
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentAllVerifiedBinding
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class LendingKycVerifiedFragment :
    BaseFragment<FeatureLendingKycFragmentAllVerifiedBinding>() {

    private val viewModelProvider by viewModels<LendingKycVerifiedViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentAllVerifiedBinding
        get() = FeatureLendingKycFragmentAllVerifiedBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    private var isPanVisible = false
    private var isAadhaarVisible = false

    override fun setup(savedInstanceState: Bundle?) {
        setStatusBarColor(com.jar.app.core_ui.R.color.bgColor)
        setupUI()
        setClickListener()
        observeFlow()
        viewModel.fetchUserLendingKycProgress()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verification)
        binding.toolbar.ivEndImage.isVisible = true
        binding.toolbar.ivEndImage.setImageResource(R.drawable.feature_lending_kyc_ic_help)
    }

    private fun setClickListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.toolbar.ivEndImage.setDebounceClickListener {
            navigateTo(
                "android-app://com.jar.app/lending-kyc-faq",
                true
            )
        }

        binding.tvShowPan.setDebounceClickListener {
            isPanVisible = !isPanVisible
            viewModel.getKycProgressResponse()?.kycProgress?.PAN?.let {
                renderPanDetails(it)
            }
        }
        binding.tvShowAadhaar.setDebounceClickListener {
            isAadhaarVisible = !isAadhaarVisible
            viewModel.getKycProgressResponse()?.kycProgress?.AADHAAR?.let {
                renderAadhaarDetails(it)
            }
        }

    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userLendingKycProgressFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            viewModel.setKycResponse(it)
                            it.kycProgress?.PAN?.let {
                                renderPanDetails(it)
                            }
                            it.kycProgress?.AADHAAR?.let {
                                renderAadhaarDetails(it)
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun renderAadhaarDetails(aadhaar: AADHAAR) {
        binding.tvVerifiedOnAadhaar.text = getCustomStringFormatted(
            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verified_on_s,
            aadhaar.verifiedAt?.getDateMonthNameAndYear().orEmpty()
        )
        binding.tvAadhaarNumber.text =
            if (isAadhaarVisible) aadhaar.aadhaarNo.orEmpty() else maskAadhaarNumber(aadhaar.aadhaarNo.orEmpty())
        binding.tvNameAadhaar.text = aadhaar.name.orEmpty()
        binding.tvDobAadhaar.text = aadhaar.dob.orEmpty().getMonthNameDateFromDDMMYYYY()
        binding.tvShowAadhaar.text = getCustomString(
            if (isAadhaarVisible) com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_aadhaar_number
            else com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_aadhaar_number
        )
    }

    private fun renderPanDetails(pan: PAN) {
        binding.tvVerifiedOnPan.text = getCustomStringFormatted(
            com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_verified_on_s, pan.verifiedAt?.getDateMonthNameAndYear().orEmpty()
        )
        binding.tvPanNumber.text =
            if (isPanVisible) pan.panNo.orEmpty() else maskPanNumber(pan.panNo.orEmpty())
        binding.tvNamePAN.text = pan.getPrintableName()
        binding.tvDobPAN.text = pan.dob.orEmpty().getMonthNameDateFromDDMMYYYY()
        binding.tvShowPan.text = getCustomString(
            if (isPanVisible) com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_hide_pan_number
            else com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_show_pan_number
        )
    }

    private fun maskPanNumber(text: String): String {
        if (text.isEmpty()) return ""
        val masked = StringBuilder()
        val maskedLength = text.length - 2
        masked.append(text.first())
        masked.append("*".repeat(maskedLength))
        masked.append(text.last())
        return masked.toString()
    }

    private fun maskAadhaarNumber(text: String): String {
        if (text.isEmpty()) return ""
        val masked = StringBuilder()
        val maskedLength = text.length - 4
        masked.append("*".repeat(maskedLength))
        masked.append(text.takeLast(4))
        return masked.toString()
    }
}