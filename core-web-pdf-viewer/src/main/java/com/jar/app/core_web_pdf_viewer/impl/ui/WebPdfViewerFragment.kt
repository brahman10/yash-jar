package com.jar.app.core_web_pdf_viewer.impl.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_web_pdf_viewer.BuildConfig
import com.jar.app.core_web_pdf_viewer.api.WEB_TYPE_BASE64
import com.jar.app.core_web_pdf_viewer.api.WEB_TYPE_URL
import com.jar.app.core_web_pdf_viewer.databinding.FragmentWebPdfViewerBinding
import com.jar.app.core_web_pdf_viewer.impl.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class WebPdfViewerFragment : BaseFragment<FragmentWebPdfViewerBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    private val args by navArgs<WebPdfViewerFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWebPdfViewerBinding
        get() = FragmentWebPdfViewerBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setup(savedInstanceState: Bundle?) {
        binding.webView.setBackgroundColor(Color.WHITE)
        binding.webView.webViewClient = CustomWebViewClient()
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webSettings.useWideViewPort = true
        binding.webView.setInitialScale(1)

        binding.webView.setDownloadListener { _, _, _, _, _ ->
            if (isAdded) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(decodeUrl(args.data))
                if (i.resolveActivity(requireContext().packageManager) != null)
                    startActivity(i)
            }
        }

        binding.webView.loadUrl(if (args.type == WEB_TYPE_BASE64) BuildConfig.PDF_VIEWER_BASE64 else Constants.PDF_VIEWER)
    }

    private fun setPdfData() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            val urlInjection = if (args.type == WEB_TYPE_URL) {
                "window.localStorage.setItem(\"pdfUrl\",'${decodeUrl(args.data)}');" +
                        "window.platform = \"android\";"
            } else {
                "window.localStorage.setItem(\"loanId\",'${args.data}');" +
                        "window.localStorage.setItem(\"accessToken\",'${prefs.getAccessToken()}');" +
                        "window.platform = \"android\";"
            }
            binding.webView.evaluateJavascript(urlInjection, null)
        }
    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            setPdfData()
        }
    }
}