package com.jar.app.feature_mandate_payment.impl.ui.mandate_video

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentMandateVideoBottomSheetBinding
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class MandateVideoBottomSheet :
    BaseBottomSheetDialogFragment<FeatureMandatePaymentMandateVideoBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureMandatePaymentMandateVideoBottomSheetBinding
        get() = FeatureMandatePaymentMandateVideoBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    private val args: MandateVideoBottomSheetArgs by navArgs()

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    override fun setup() {
        setupWebView(args.videoUrl)
        binding.ivCross.setDebounceClickListener {
            dismiss()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.webView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.bgColor)
        )
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        binding.webView.loadUrl(url)

        analyticsApi.postEvent(MandatePaymentEventKey.Shown_Mandate_Education_Video)
    }

}