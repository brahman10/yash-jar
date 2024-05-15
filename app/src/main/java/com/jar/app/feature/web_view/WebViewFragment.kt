package com.jar.app.feature.web_view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshUserMetaEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.databinding.FragmentWebViewBinding
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.net.URL
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@AndroidEntryPoint
class WebViewFragment : BaseFragment<FragmentWebViewBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var sharedPreferencesUserLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    private var glide: RequestManager? = null
    private var target: CustomTarget<Bitmap>? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWebViewBinding
        get() = FragmentWebViewBinding::inflate

    companion object {
        private const val REQUEST_SELECT_FILE = 100
    }

    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
        uiScope.launch {
            withContext(dispatcherProvider.main) {
                dismissProgressBar()
            }
        }
    }

    override fun setupAppBar() { }

    private val args by navArgs<WebViewFragmentArgs>()

    var isBackPressed = false

    var isFirstLanding = true

    private var loadingJob: Job? = null

    private var shareJob: Job? = null

    private var copyToClipboardJob: Job? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var isUserDataUpdated = false

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isBackPressed = true
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                }
            }
        }

    override fun setup(savedInstanceState: Bundle?) {
        setupWebView(decodeUrl(args.url))
        registerBackPressDispatcher()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        backPressCallback.isEnabled = false
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
        binding.webView.addJavascriptInterface(WebAppInterface(), BaseConstants.WEB_APP_INTERFACE)

        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true

        binding.webView.loadUrl(url)
    }

    private fun writeUserData() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            isUserDataUpdated = true
            val userDataInjection =
                "window.localStorage.setItem(\"accessToken\",'${prefs.getAccessToken()}');" +
                        "window.localStorage.setItem(\"refreshToken\",'${prefs.getRefreshToken()}');" +
                        "window.localStorage.setItem(\"userId\",'${sharedPreferencesUserLiveData.value?.userId}');" +
                        "window.platform = \"android\";"
            binding.webView.evaluateJavascript(
                userDataInjection,
                null
            )
        }
    }

    private fun writeUserDataForViba() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            isUserDataUpdated = true
            val userDataInjection =
                "window.localStorage.setItem(\"@jar/accessToken\",'${prefs.getAccessToken()}');" +
                        "window.localStorage.setItem(\"@jar/refreshToken\",'${prefs.getRefreshToken()}');" +
                        "window.localStorage.setItem(\"@jar/userId\",'${sharedPreferencesUserLiveData.value?.userId}');" +
                        "window.localStorage.setItem(\"@jar/platform\",'android');"
            binding.webView.evaluateJavascript(
                userDataInjection,
                null
            )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    inner class MyWebChromeClient : WebChromeClient() {
        private var customView: View? = null
        private val matchParentLayout = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        var content: ViewGroup = binding.webView
        var parent: ViewGroup = binding.root
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

        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            customView = view
            view?.layoutParams = matchParentLayout
            binding.root.addView(view)
            content.visibility = View.GONE
        }

        override fun onHideCustomView() {
            content.visibility = View.VISIBLE
            parent.removeView(customView)
            customView = null
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
            if (isFirstLanding) {
                isFirstLanding = false
                startWebViewLoading()
            }
            when {
                isUserDataUpdated -> return
                url?.contains("v1/credit-card/").orFalse() -> writeUserData()
                url?.contains("wiki.myjar.app/web/v1/lending/").orFalse() -> writeUserData()
                url?.contains("myviba.in").orFalse() -> writeUserDataForViba()
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            dismissWebViewLoading()
            backPressCallback.isEnabled = view?.canGoBack().orFalse()

            if (args.shouldPostAnalyticsFromUrl && !isBackPressed) {
                postInAppHelpAnalytics(getQueryMap(url))
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
                if (intent.resolveActivity(requireContext().packageManager) != null)
                    startActivity(intent)
                return true
            }

            if(url.contains("myviba.in") || url.contains("cashfree")) {
                return false
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (intent.resolveActivity(requireContext().packageManager) != null)
                startActivity(intent)
            return true
        }
    }

    private fun postInAppHelpAnalytics(map: Map<String, String>) {
        val eventName = map["event"]
        val eventMap: MutableMap<String, String> = HashMap()

        eventMap[BaseConstants.FlowType] = args.flowType
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

        @JavascriptInterface
        fun onHandleDeepLink(deepLink: String?, shouldCloseWebView: Boolean = true) {
            deepLink?.takeIf { it.isNotEmpty() }?.let {
                if (shouldCloseWebView) {
                    closeWebview()
                }
                prefs.setUserLifeCycleForMandate(EventKey.UserLifecycles.WebFlow)
                EventBus.getDefault().post(HandleDeepLinkEvent(it,EventKey.UserLifecycles.WebFlow))
            }
        }

        @JavascriptInterface
        fun onCopyToClipboard(copyText: String?, toastMessage: String?) {
            copyToClipboardJob?.cancel()
            copyToClipboardJob = uiScope.launch {
                copyText?.takeIf { it.isNotEmpty() }?.let {
                    requireContext().copyToClipboard(it, toastMessage)
                }
            }
        }

        @JavascriptInterface
        fun onShare(shareText: String?, imageUrl: String?) {
            shareJob?.cancel()
            shareJob = uiScope.launch(coroutineExceptionHandler) {
                showProgressBar()
                imageUrl?.let { imageLink ->
                    glide = Glide.with(requireContext())
                    target = object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            shareMessage(resource, shareText)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            uiScope.launch {
                                withContext(dispatcherProvider.main) {
                                    dismissProgressBar()
                                }
                            }
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            shareMessage(null, shareText)
                        }
                    }
                    glide?.asBitmap()
                        ?.load(imageLink)
                        ?.diskCacheStrategy(DiskCacheStrategy.ALL)
                        ?.priority(Priority.HIGH)
                        ?.into(target!!) // !! because it is just set above
                } ?: kotlin.run {
                    shareMessage(null, shareText)
                }
            }
        }
    }

    private fun shareMessage(imageBitmap: Bitmap?, shareText: String?) {
        shareText?.takeIf { it.isNotEmpty() }?.let { shareMessage ->
            uiScope.launch {
                imageBitmap?.let {
                    fileUtils.copyBitmap(
                        imageBitmap,
                        "web_image_${
                            Calendar.getInstance().timeInMillis.toString().takeLast(4)
                        }"
                    )?.let { imageFile ->
                        withContext(dispatcherProvider.main) {
                            dismissProgressBar()
                            fileUtils.shareImage(requireContext(), imageFile, shareMessage)
                        }
                    } ?: kotlin.run {
                        withContext(dispatcherProvider.main) {
                            dismissProgressBar()
                            fileUtils.shareText(shareMessage, "")
                        }
                    }
                } ?: kotlin.run {
                    withContext(dispatcherProvider.main) {
                        dismissProgressBar()
                        fileUtils.shareText(shareMessage, "")
                    }
                }
            }
        }
    }

    private fun startWebViewLoading() {
        loadingJob?.cancel()
        loadingJob = uiScope.launch {
            withContext(dispatcherProvider.main) {
                binding.webView.isVisible = false
                showProgressBar()
            }
        }
    }

    private fun dismissWebViewLoading() {
        loadingJob?.cancel()
        loadingJob = uiScope.launch {
            withContext(dispatcherProvider.main) {
                binding.webView.isVisible = true
                dismissProgressBar()
            }
        }
    }

    override fun onDestroyView() {
        glide?.clear(target)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        loadingJob?.cancel()
        copyToClipboardJob?.cancel()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (args.showToolbar) {
            EventBus.getDefault()
                .post(UpdateAppBarEvent(AppBarData(ToolbarDefault(title = args.title))))
        } else {
            EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        }
    }
}