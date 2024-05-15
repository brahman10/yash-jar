package com.jar.app.feature_lending_kyc.impl.ui.digilocker

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDigilockerErrorFragmentBinding
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class DigiLockerErrorFragment :
    BaseFragment<FeatureLendingKycDigilockerErrorFragmentBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDigilockerErrorFragmentBinding
        get() = FeatureLendingKycDigilockerErrorFragmentBinding::inflate

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    private val args by navArgs<DigiLockerErrorFragmentArgs>()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = true))
    }

    override fun setup(savedInstanceState: Bundle?) {
        if (args.errorType == LendingKycConstants.DIGILOCKER_PAN_AADHAR_MISMATCH_ERROR) {
            binding.tvTitle.text = getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_aadhar_mismatch)
        } else {
            binding.tvTitle.text =
                getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_due_to_some_technical_error_we_couldnt_verify_your_aadhar)
        }
        binding.btnSecondaryAction.paintFlags = binding.btnSecondaryAction.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        analyticsHandler.postEvent(
            LendingKycEventKey.Lending_KYCDigiLockerErrorScreen,
            mapOf(
                LendingKycEventKey.action to LendingKycEventKey.Shown,
                LendingKycEventKey.errorMessage to binding.tvTitle.text.toString()
            )
        )
        setUpClickListners()

    }

    private fun setUpClickListners() {
        binding.ivBack.setOnClickListener {
            popBackStack()

        }

        binding.btnPrimaryAction.setOnClickListener {
            popBackStack()
            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_KYCDigiLockerErrorScreen,
                mapOf(
                    LendingKycEventKey.action to LendingKycEventKey.Clicked_Try_Again
                )
            )
        }
        binding.btnSecondaryAction.setOnClickListener {


            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_KYCDigiLockerErrorScreen,
                mapOf(
                    LendingKycEventKey.action to LendingKycEventKey.Clicked_Contact_Support
                )
            )
            val sendTo = remoteConfigApi.getWhatsappNumber()
            val number = prefs.getUserPhoneNumber()
            val name = prefs.getUserName()
            val message = getCustomStringFormatted(
                com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_contact_support_s_s_s,
                if (args.errorType == LendingKycConstants.DIGILOCKER_PAN_AADHAR_MISMATCH_ERROR) getCustomString(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_pan_and_aadhaar_details_are_not_matching
                ) else getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_my_aadhar_verification_has_failed),
                name.orEmpty(),
                number.orEmpty()
            )
            requireContext().openWhatsapp(sendTo, message)
        }
    }
}