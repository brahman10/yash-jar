package com.jar.app.feature.onboarding.ui.experian_bottomsheet

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.BottomSheetExperianConsentBinding
import com.jar.app.feature_homepage.shared.util.EventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class ExperianConsentBottomSheet :
    BaseBottomSheetDialogFragment<BottomSheetExperianConsentBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<ExperianConsentBottomSheetArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetExperianConsentBinding
        get() = BottomSheetExperianConsentBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = true,
            isDraggable = true,
        )

    override fun setup() {
        analyticsHandler.postEvent(
            EventKey.Shown_CreditConsent,
            mapOf(EventKey.FromScreen to if(args.backstackId == R.id.selectNumberFragment) "accountDetectedScreen" else "Login screen"))
        setupUI()
        setupListeners()
    }

    fun setupListeners() {
        binding.ivCross.setDebounceClickListener {
            dismiss()
        }

        binding.btnExperianConsent.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Shown_CreditConsent,
                mapOf(EventKey.Action to "Allow"))

            findNavController().getBackStackEntry(args.backstackId).savedStateHandle[BaseConstants.EXPERIAN_CONSENT] =
                true
            dismiss()
        }

        binding.tvFooter.setDebounceClickListener {

            analyticsHandler.postEvent(
                EventKey.Shown_CreditConsent,
                mapOf(EventKey.Action to "DontAllow"))

            findNavController().getBackStackEntry(args.backstackId).savedStateHandle[BaseConstants.EXPERIAN_CONSENT] =
                false
            dismiss()
        }

        binding.tvExperianDescription.setDebounceClickListener {

            analyticsHandler.postEvent(
                EventKey.Shown_CreditConsent,
                mapOf(EventKey.Action to "experianT&C"))

            navigateTo(
                ExperianConsentBottomSheetDirections.actionExperianConsentBottomSheetToExperianTCBottomSheet(),
                true
            )
        }
    }

    private fun setupUI() {
        binding.tvHeader.text = getString(R.string.feature_experian_bottom_sheet_header)
//        binding.tvExperianDescription.text = Html.fromHtml(getString(R.string.feature_experian_bottom_sheet_description)).toString()
        binding.btnExperianConsent.setText(getString(R.string.feature_experian_bottom_sheet_cta_text))
        binding.tvFooter.text = getString(R.string.feature_experian_bottom_sheet_footer)
        binding.tvFooter.paintFlags = Paint.UNDERLINE_TEXT_FLAG

    }
}