package com.jar.app.feature_lending.impl.domain.model.realTimeFlow

import android.net.Uri

data class BankStatementPdfDetail(
    val uri: Uri,
    val name: String,
    val size: String,
    val sizeInBytes: Long = 0L,
    val isFailed: Boolean = false,
    val isUploadSuccessful: Boolean = false,
    val showCrossButton: Boolean = true,
    val failedReason: String? = null
)
