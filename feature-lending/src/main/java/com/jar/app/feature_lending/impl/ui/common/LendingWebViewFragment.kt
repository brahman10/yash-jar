package com.jar.app.feature_lending.impl.ui.common

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.LendingToolbarVisibilityEventV2
import com.jar.app.base.data.event.RefreshUserMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_analytics.EventKey
import com.jar.app.feature_lending.BuildConfig
import com.jar.app.feature_lending.databinding.FragmentFeatureLendingWebviewBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.userexperior.UserExperior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingWebViewFragment : BaseFragment<FragmentFeatureLendingWebviewBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFeatureLendingWebviewBinding
        get() = FragmentFeatureLendingWebviewBinding::inflate

    companion object {
        private const val REQUEST_SELECT_FILE = 100
    }

    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        EventBus.getDefault()
            .post(LendingToolbarVisibilityEventV2(shouldHide = true))
    }

    private val args by navArgs<LendingWebViewFragmentArgs>()

    private var isBackPressed = false

    private var currentMandateSetupUrl: String? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isBackPressed = true
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()

                    if (args.isMandateFlow){
                       currentMandateSetupUrl?.let{
                           if (it.indexOf("?")!=-1){
                               val subUrl = it.substring(0,it.indexOf("?"))
                               analyticsHandler.postEvent(
                                   LendingEventKeyV2.Lending_MandateBackButtonClicked,
                                   subUrl
                               )
                           }
                       }
                    }
                }
            }
        }

    override fun setup(savedInstanceState: Bundle?) {
        setupWebView(decodeUrl(args.url))
        registerBackPressDispatcher()
        backPressCallback.isEnabled = true
        if (args.isMandateFlow)
            UserExperior.markSensitiveView(binding.webView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_FILE) {
            if (uploadMessage == null) return
            uploadMessage?.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            uploadMessage = null
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.webView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.bgColor)
        )
        binding.webView.webViewClient = MyWebViewClient()
        binding.webView.webChromeClient = MyWebChromeClient()
        binding.webView.addJavascriptInterface(WebAppInterface(), "Android")

        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        binding.webView.loadUrl(url)
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if (uploadMessage != null) {
                uploadMessage?.onReceiveValue(null)
                uploadMessage = null
            }

            uploadMessage = filePathCallback


            try {
                fileChooserParams?.createIntent()?.let {
                    startActivityForResult(it, REQUEST_SELECT_FILE)
                }
            } catch (e: ActivityNotFoundException) {
                uploadMessage = null
                Toast.makeText(
                    context?.applicationContext,
                    "Cannot open file chooser",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
            return true
        }
    }

    inner class MyWebViewClient : WebViewClient() {

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            view?.isVisible = false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (args.isInAppHelp && !isBackPressed) {
                postInAppHelpAnalytics(getQueryMap(url))
            }
            if (args.isMandateFlow) {
                currentMandateSetupUrl = url
                url?.let {
                    analyticsHandler.postEvent(
                        LendingEventKeyV2.Lending_MandateNextPageLoad,
                        url
                    )
                    //when user successfully done mandate setup
                    if (it.contains("razorpay/mandateStatus")) {
                        uiScope.launch {
                            delay(2000L)//delay to make sure server get callback from razorpay
                            popBackStack()
                        }
                    }
                }
            }
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            isBackPressed = false
            if (url.indexOf("myjar.app") > -1) return false

            if (url.contains("android:settings")) {
                //Even though this is a common WebView, but this block only executes in case of InAppHelp
                analyticsHandler.postEvent(EventKey.Clicked_BackArrowButton_InAppHelp)
                popBackStack()
                return true
            }

            if (url.contains("android:back")) {
                //Even though this is a common WebView, but this block only executes in case of InAppHelp
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    popBackStack()
                }
                isBackPressed = true
                postInAppHelpAnalytics(getQueryMapFromBackString(url))
                return true
            }

            if (url.startsWith("tel:") || url.startsWith("whatsapp:")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                    webView.goBack()
                }
                return true
            }

            webView.loadUrl(url)
            return true
        }
    }

    private fun postInAppHelpAnalytics(map: Map<String, String>) {
        val eventName = map["event"]
        val eventMap: MutableMap<String, String> = HashMap()

        map.forEach {
            if (it.key != "event") {
                eventMap[it.key] = it.value
            }
        }

        if (eventMap.isEmpty()) {
            eventName?.let {
                analyticsHandler.postEvent(it)
            }
        } else {
            eventName?.let {
                analyticsHandler.postEvent(it, eventMap)
            }
        }

    }

    private fun getQueryMap(urlString: String?): Map<String, String> {
        try {
            val url = URL(urlString)
            val query = url.query ?: return HashMap()
            val params = query.split("&").toTypedArray()
            val map: MutableMap<String, String> = HashMap()
            for (param in params) {
                try {
                    val name = param.split("=").toTypedArray()[0]
                    val value = param.split("=").toTypedArray()[1]
                    map[name] = value
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return map
        } catch (e: Exception) {
            return HashMap()
        }
    }

    private fun getQueryMapFromBackString(url: String?): Map<String, String> {
        try {
            val query = url?.split("?")?.get(1) ?: return HashMap()

            val params = query.split("&").toTypedArray()
            val map: MutableMap<String, String> = HashMap()
            for (param in params) {
                try {
                    val name = param.split("=").toTypedArray()[0]
                    val value = param.split("=").toTypedArray()[1]
                    map[name] = value
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return map
        } catch (e: Exception) {
            return HashMap()
        }
    }

    inner class WebAppInterface {
        @Suppress("UNCHECKED_CAST")
        @JavascriptInterface
        fun sendEvent(eventMap: String?) {
            eventMap?.let {
                try {
                    val map = serializer.decodeFromString<Map<String, String>>(it)
                    postInAppHelpAnalytics(map)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @JavascriptInterface
        fun closeWebview() {
            uiScope.launch {
                delay(100)
                popBackStack()
            }
        }

        @JavascriptInterface
        fun onSuccess() {
            EventBus.getDefault().post(RefreshUserMetaEvent())
        }
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}