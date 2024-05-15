package com.jar.app.feature_lending_kyc.impl.ui.digilocker

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.hideKeyboard
import com.jar.app.core_base.domain.model.KycEmailAndAadhaarProgressStatus
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingFragmentDigilockerWebviewBinding
import com.jar.app.feature_lending_kyc.impl.domain.model.KYCScreenArgs
import com.jar.app.feature_lending_kyc.impl.ui.choose_kyc_method.KYCOptionFragmentArgs
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


@AndroidEntryPoint
internal class DigiLockerWebViewFragment() :
    BaseFragment<FeatureLendingFragmentDigilockerWebviewBinding>() {

    private val viewModelProvider by viewModels<DigiLockerWebViewViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi


    private var pollingJob: Job? = null

    private var timer = 0

    private val arguments by navArgs<KYCOptionFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<KYCScreenArgs>(decodeUrl(arguments.screenArgs))
    }

    companion object {
        private const val OLD_WEBHOOK_URL = "https://www.jar.com/digilocker/start/success"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentDigilockerWebviewBinding
        get() = FeatureLendingFragmentDigilockerWebviewBinding::inflate

    override fun setupAppBar() {

        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = true))

    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }

    override fun setup(savedInstanceState: Bundle?) {
        args.url?.let {
            setupWebView(it)
        }
        registerBackPressDispatcher()
        backPressCallback.isEnabled = true
        binding.toolbar.tvTitle.text =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_digilocker)
        binding.toolbar.btnBack.setOnClickListener {
            backPressCallback.handleOnBackPressed()

        }
        setUpObservers()
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.digiLockerVerificationStatus.collect(
                onLoading = {},
                onSuccess = {
                    if (it?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name) {
                        pollingJob?.cancel()
                        uiScope.launch(Dispatchers.Main) {
                            dismissProgressBar()
                            navigateTo(
                                FeatureLendingKycStepsNavigationDirections.actionToSuccessStepDialog(
                                    LendingKycFlowType.AADHAAR,
                                    LendingKycEventKey.DigiLocker_Webview,
                                    lenderName = args.lenderName,
                                    kycFeatureFlowType = args.kycFeatureFlowType.name
                                ),
                                popUpTo = R.id.digiLockerWebViewFragment,
                                inclusive = true
                            )
                        }

                    } else if (it?.status == KycEmailAndAadhaarProgressStatus.FAILED.name) {
                        uiScope.launch(Dispatchers.Main) {
                            dismissProgressBar()
                            pollingJob?.cancel()
                            navigateToErrorScreen(LendingKycConstants.DIGILOCKER_PAN_AADHAR_MISMATCH_ERROR)
                        }
                    }
                },
                onError = { _, _ ->
                }
            )
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.webView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white)
        )
        binding.webView.webViewClient = MyWebViewClient()
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        binding.webView.loadUrl(url)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )

    }

    inner class MyWebViewClient : WebViewClient() {

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }


        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            request?.url?.let {
                analyticsHandler.postEvent(
                    LendingKycEventKey.Lending_DigiLocker_Screen_Next_Page_Load,
                    mapOf(LendingKycEventKey.Link to it.toString())
                )
                if (it.toString().startsWith(args.webhookUrl ?: OLD_WEBHOOK_URL)) {
                    try {
                        val redirectUrl = Uri.parse(request.url.toString())
                        if (redirectUrl.getQueryParameter("error") == null
                            || redirectUrl.getQueryParameter("error") == "null"
                        ) {
                            if (remoteConfigApi.shouldEnablePinlessDigilocker()) {
                                val code = redirectUrl.getQueryParameter("code")
                                val state = redirectUrl.getQueryParameter("state")
                                updateRedirectData(state = state.orEmpty(), code = code.orEmpty())
                            }
                            handleNavigation()
                        } else {
                            navigateToErrorScreen(LendingKycConstants.DIGILOCKER_TIMEOUT_ERROR)
                        }
                    } catch (e: Exception) {
                        getString(com.jar.app.core_ui.R.string.something_went_wrong).snackBar(
                            binding.root
                        )
                        popBackStack()
                    }
                    return true
                }
            }
            return super.shouldOverrideUrlLoading(view, request)
        }


    }

    private fun updateRedirectData(code: String, state: String) {
        viewModel.updateRedirectData(
            kycFeatureFlowType = args.kycFeatureFlowType,
            state = state,
            code = code
        )
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = uiScope.doRepeatingTask(1_000) {
            if (timer == 7) { //Time out after 7 Sec
                pollingJob?.cancel()
                navigateToErrorScreen(LendingKycConstants.DIGILOCKER_TIMEOUT_ERROR)
            } else {
                if (timer >= 2) // start poling after 2 sec.
                    viewModel.getDigiLockerVerificationStatus(
                        args.kycFeatureFlowType,
                        remoteConfigApi.shouldEnablePinlessDigilocker()
                    )
            }
            timer += 1
        }
    }

    private fun navigateToErrorScreen(errorType: String) {
        navigateTo(
            "android-app://com.jar.app/digiLockerErrorFragment/$errorType",
            shouldAnimate = true,
            popUpTo = R.id.digiLockerWebViewFragment,
            inclusive = true
        )
    }

    private fun handleNavigation() {
        showProgressBar()
        startPolling()
    }


    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        binding.webView.clearCache(true)
        pollingJob?.cancel()
        binding.webView.clearCache(true)
        super.onDestroyView()
    }

    private fun showAlertDialog() {

        context?.hideKeyboard(binding.webView)
        val builder = MaterialAlertDialogBuilder(requireContext())

        val positiveButtonText =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_continue)
        val positiveSpannableString = SpannableString(positiveButtonText)
        val negativeButtonText =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_exit)
        val negativeSpannableString = SpannableString(negativeButtonText)

        positiveSpannableString.setSpan(UnderlineSpan(), 0, positiveSpannableString.length, 0)
        negativeSpannableString.setSpan(UnderlineSpan(), 0, negativeSpannableString.length, 0)

        // Set dialog title
        val titleText =
            getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_are_you_sure_you_want_to_exit)
        val titleSpannable = SpannableString(titleText)
        titleSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, titleText.length, 0)
        builder.setTitle(titleSpannable)
        builder.setMessage(getCustomString(com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_your_progress_will_be_lost_if_you_go_back))


        // Set positive button and its click listener
        builder.setPositiveButton(positiveSpannableString) { dialog: DialogInterface, which: Int ->
            // Handle button click
            dialog.dismiss()
        }


        // Set negative button and its click listener
        builder.setNegativeButton(negativeSpannableString) { dialog: DialogInterface, which: Int ->
            // Handle button click
            analyticsHandler.postEvent(
                LendingKycEventKey.Lending_DigiLocker_Screen_Exit,
                mapOf(
                    LendingKycEventKey.Link to binding.webView.url.toString()
                )

            )

            popBackStack()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.feature_lending_dialog_rounded_background) // setting the rounded corners

        dialog.show()


        //  Change button text color
        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(resources.getColor(com.jar.app.core_ui.R.color.color_1ea787))

        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(resources.getColor(com.jar.app.core_ui.R.color.color_1ea787))

        // Set the dialog position to the bottom
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT // Set width to match parent
        window?.attributes = layoutParams
    }


}