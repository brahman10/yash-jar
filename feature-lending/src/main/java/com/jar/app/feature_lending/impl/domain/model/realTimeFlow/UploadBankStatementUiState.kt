package com.jar.app.feature_lending.impl.domain.model.realTimeFlow

import com.jar.app.feature_lending.shared.domain.model.v2.BankAccount

data class UploadBankStatementUiState(
    val ctaType: CtaType = CtaType.CONFIRM,
    val uploadPercent: Int = 0,
    val bankDetail: BankAccount? = null,
    val statementPassword: String = "",
    val shouldShowCta: Boolean = true,
    val isPrimaryButtonEnabled: Boolean = true,
    val shouldShowUploading: Boolean = false,
    val shouldShowUploadSuccess: Boolean = false,
    val shouldShowUploadError: Boolean = false,
    val totalFiles: Int = 0,
    val selectedPdfs: List<BankStatementPdfDetail> = emptyList(),
    val uploadedPdfs: List<BankStatementPdfDetail>? = null
) {
    fun shouldHideToolbar() =
        shouldShowUploading || shouldShowUploadSuccess || shouldShowUploadError

    fun getFinalList(): List<BankStatementPdfDetail> {
        return uploadedPdfs?.let {
            val items = selectedPdfs.toMutableList()
            items.addAll(it)
            items
        } ?: kotlin.run {
            selectedPdfs
        }
    }
}

enum class CtaType {
    CONFIRM, SUBMIT
}