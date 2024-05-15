package com.jar.app.core_web_pdf_viewer.api

interface WebPdfViewerApi {

    fun openPdf(pdfUrl: String, type: String = WEB_TYPE_URL)
}

const val WEB_TYPE_URL = "url"
const val WEB_TYPE_BASE64 = "base64"