package com.jar.app.core_web_pdf_viewer.impl.data

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_web_pdf_viewer.api.WEB_TYPE_URL
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import dagger.Lazy
import javax.inject.Inject

internal class WebPdfViewerImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : WebPdfViewerApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openPdf(pdfUrl: String, type: String) {
        val encodedUrl = if (type == WEB_TYPE_URL) encodeUrl(pdfUrl) else pdfUrl
        navController.navigate(
            Uri.parse("android-app://com.jar.app/webPdfViewerFragment/$encodedUrl/$type"),
            getNavOptions(shouldAnimate = true)
        )
    }
}