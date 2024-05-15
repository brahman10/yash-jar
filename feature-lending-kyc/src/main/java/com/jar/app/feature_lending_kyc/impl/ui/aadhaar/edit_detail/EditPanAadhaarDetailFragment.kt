package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.edit_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycFragmentEditPanAadhaarDetailBinding
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.feature_lending_kyc.impl.domain.event.ToolbarStepsVisibilityEvent
import com.jar.app.feature_lending_kyc.impl.ui.pan.report_not_fetched.CreditReportNotAvailableFragment
import com.jar.app.core_base.data.dto.KycFeatureFlowType
import com.jar.app.feature_lending_kyc.shared.domain.model.ManualPanEntryScreenArguments
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.action_prompt.ActionPromptViewModel.Companion.DOCUMENT_AADHAAR
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.action_prompt.ActionPromptViewModel.Companion.DOCUMENT_PAN
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class EditPanAadhaarDetailFragment :
    BaseFragment<FeatureLendingKycFragmentEditPanAadhaarDetailBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<EditPanAadhaarDetailFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycFragmentEditPanAadhaarDetailBinding
        get() = FeatureLendingKycFragmentEditPanAadhaarDetailBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setClickListener()
    }

    private fun setupUI() {
        EventBus.getDefault()
            .post(ToolbarStepsVisibilityEvent(shouldShowSteps = false, Step.AADHAAR))
        when (args.documentType) {
            DOCUMENT_PAN -> {
                binding.icvDocument.setIdentity(args.detail.aadhaarNumber.orEmpty())
                binding.icvDocument.setIdentityHeading(
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_permanent_account_number)
                )
                binding.tvEditDocumentDetails.text = getCustomStringFormatted(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_edit_s_details,
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan)
                )
                binding.tvYourSavedDetail.text = getCustomStringFormatted(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_edit_s_details,
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan)
                )
            }
            DOCUMENT_AADHAAR -> {
                binding.icvDocument.setIdentity(args.detail.maskAadhaarNumber())
                binding.icvDocument.setIdentityHeading(
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar_number)
                )
                binding.tvEditDocumentDetails.text = getCustomStringFormatted(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_edit_s_details,
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar)
                )
                binding.tvYourSavedDetail.text = getCustomStringFormatted(
                    com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_edit_s_details,
                    getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_aadhaar)
                )
            }
        }
        binding.icvDocument.setName(args.detail.name.orEmpty())
        binding.icvDocument.setDob(args.detail.dob.orEmpty())
    }

    private fun setClickListener() {
        binding.btnEditDetail.setDebounceClickListener {
            when (args.documentType) {
                DOCUMENT_PAN -> {
                    val screenArgs = encodeUrl(
                        serializer.encodeToString(
                            ManualPanEntryScreenArguments(
                                if (args.detail.jarVerifiedPAN.orFalse())
                                    CreditReportNotAvailableFragment.USER_HAS_DONE_PAN_VERIFICATION_ON_JAR_BEFORE
                                else
                                    CreditReportNotAvailableFragment.USER_HAS_NOT_DONE_PAN_VERIFICATION_ON_JAR_BEFORE,
                                isPanAadhaarMismatch = true,
                                jarVerifiedPAN = args.detail.jarVerifiedPAN.orFalse(),
                                kycFeatureFlowType = KycFeatureFlowType.UNKNOWN
                            )
                        )
                    )
                    navigateTo(
                        navDirections = FeatureLendingKycStepsNavigationDirections.actionToEnterPanManuallyStep(
                            screenArgs
                        ),
                        popUpTo = R.id.enterPanManuallyFragment,
                        inclusive = false,
                        shouldAnimate = true
                    )
                }
                DOCUMENT_AADHAAR -> {
                    popBackStack(R.id.aadhaarManualEntryFragment, false)
                }
            }
        }

        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }
}