package com.jar.app.feature_lending.impl.ui.realtime_flow.bank_statement.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.BankStatementPdfDetail
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.CtaType
import com.jar.app.feature_lending.impl.domain.model.realTimeFlow.UploadBankStatementUiState
import com.jar.app.feature_lending.impl.util.FileSelectorUtil
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.SuccessApiResponse
import com.jar.app.feature_lending.shared.domain.model.realTimeFlow.UpdatePasswordRequest
import com.jar.app.feature_lending.shared.domain.use_case.FetchUploadedBankStatementsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankStatementPasswordUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UploadBankStatementUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants.MAX_PDFS_ALLOWED_TO_UPLOAD
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.orFalse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class UploadBankStatementViewModel @Inject constructor(
    private val fileSelectorUtil: FileSelectorUtil,
    private val uploadBankStatementUseCase: UploadBankStatementUseCase,
    private val fetchUploadedBankStatementsUseCase: FetchUploadedBankStatementsUseCase,
    private val updateBankStatementPasswordUseCase: UpdateBankStatementPasswordUseCase
) : ViewModel() {

    companion object {
        private const val TEN_MB_IN_BYTES = 10000000L
    }

    private var filesUploaded = 0

    private val _uploadStatementUiState = MutableStateFlow(UploadBankStatementUiState())
    val uploadStatementUiState = _uploadStatementUiState.asStateFlow()

    private val _noOfItemsLeft = MutableSharedFlow<Int>()
    val noOfItemsLeft = _noOfItemsLeft.asSharedFlow()

    private val _submitPassword =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<SuccessApiResponse?>>>()
    val submitPassword = _submitPassword.asSharedFlow()

    private suspend fun getUriToBankStatement(uri: Uri): BankStatementPdfDetail {
        val displayNameAndSize = fileSelectorUtil.getSizeAndNameFromUri(uri)
        return BankStatementPdfDetail(
            uri = uri,
            name = displayNameAndSize.second,
            size = displayNameAndSize.first,
            sizeInBytes = displayNameAndSize.third
        )
    }

    fun fetchBankDetail() {
        viewModelScope.launch {
            fetchUploadedBankStatementsUseCase.fetchUploadedBankStatement().collect(
                onLoading = {},
                onSuccess = {
                    it?.let { response ->
                        _uploadStatementUiState.update {
                            it.copy(
                                bankDetail = response.bankDetail,
                                uploadedPdfs = response.bankStatements?.map {
                                    BankStatementPdfDetail(
                                        name = it.name,
                                        uri = Uri.parse(it.fileUrl),
                                        size = fileSelectorUtil.formatFileSize(it.sizeInBytes),
                                        isUploadSuccessful = true,
                                        showCrossButton = false,
                                    )
                                },
                                isPrimaryButtonEnabled = response.bankStatements?.size.orZero() in 1..MAX_PDFS_ALLOWED_TO_UPLOAD
                            )

                        }
                    }

                }
            )
        }
    }

    fun addPdfUris(uris: List<Uri>, shouldAddMore: Boolean = false) {
        viewModelScope.launch {
            val newUris = uris.map { getUriToBankStatement(it) }
            val newList = uploadStatementUiState.value.selectedPdfs.toMutableList()
            newList.addAll(newUris)
            val totalFiles =
                uploadStatementUiState.value.uploadedPdfs?.size.orZero() + newList.size
            val isGreaterThanTenMb = newList.any { it.sizeInBytes >= TEN_MB_IN_BYTES }
            val finalList = newList.map {
                if (it.sizeInBytes >= TEN_MB_IN_BYTES) it.copy(
                    isFailed = true,
                    failedReason = "File size should not exceed 10 MB"
                ) else it
            }
            _uploadStatementUiState.update {
                it.copy(
                    selectedPdfs = finalList,
                    ctaType = CtaType.CONFIRM,
                    totalFiles = totalFiles,
                    isPrimaryButtonEnabled = totalFiles in 1..MAX_PDFS_ALLOWED_TO_UPLOAD && isGreaterThanTenMb.not()
                )
            }
        }
    }

    fun removePdfUri(uri: Uri, position: Int) {
        viewModelScope.launch {
            val list = ArrayList(uploadStatementUiState.value.selectedPdfs)
            list.removeAt(position)
            val totalFiles = uploadStatementUiState.value.uploadedPdfs?.size.orZero() + list.size
            _uploadStatementUiState.update {
                it.copy(
                    ctaType = if (list.isEmpty()) CtaType.SUBMIT else CtaType.CONFIRM,
                    selectedPdfs = list,
                    totalFiles = totalFiles,
                    isPrimaryButtonEnabled = (totalFiles in 1..MAX_PDFS_ALLOWED_TO_UPLOAD) && !list.any { it.isFailed }
                )
            }
            _noOfItemsLeft.emit(totalFiles)
        }
    }


    fun submitBankStatements() {
        viewModelScope.launch {
            updateBankStatementPasswordUseCase.updateBankStatementPassword(
                UpdatePasswordRequest(
                    password = uploadStatementUiState.value.statementPassword,
                    submitBankStatements = true
                )
            ).collectLatest { _submitPassword.emit(it) }
        }
    }

    fun onPasswordChange(password: String) {
        viewModelScope.launch {
            _uploadStatementUiState.update {
                it.copy(statementPassword = password)
            }
        }
    }

    fun uploadBankStatementsPdf(isRetrying: Boolean = false) {
        _uploadStatementUiState.update {
            it.copy(
                uploadPercent = 0,
                shouldShowCta = false,
                shouldShowUploading = true
            )
        }
        tryToUpload(0, isRetrying)
    }

    private fun tryToUpload(index: Int, isRetrying: Boolean = false) {
        viewModelScope.launch {
            val totalFiles = uploadStatementUiState.value.selectedPdfs.size
            if (index < totalFiles) {
                val bankStatement = uploadStatementUiState.value.selectedPdfs[index]
                if (isRetrying && bankStatement.isUploadSuccessful) {//skip which are already uploaded.
                    tryToUpload(index + 1, isRetrying)
                } else {
                    fileSelectorUtil.getBytes(bankStatement.uri)?.let { byteArray ->
                        uploadBankStatementUseCase.uploadBankStatementPdf(
                            filename = bankStatement.name,
                            byteArray = byteArray
                        ).collect(
                            onLoading = {
                                _uploadStatementUiState.update {
                                    it.copy(shouldShowUploading = true)
                                }
                            },
                            onSuccess = {
                                if (it?.success.orFalse()) {
                                    updateUploadStatusAt(
                                        position = index,
                                        isUploadSuccess = true,
                                        isFailed = false,
                                        isRetrying = isRetrying
                                    )
                                } else {
                                    updateUploadStatusAt(
                                        position = index,
                                        isUploadSuccess = false,
                                        isFailed = true,
                                        failedReason = "Something went wrong!",
                                        isRetrying = isRetrying
                                    )
                                }

                            },
                            onError = { message, errorCode ->
                                updateUploadStatusAt(
                                    position = index,
                                    isUploadSuccess = false,
                                    isFailed = true,
                                    failedReason = message,
                                    isRetrying = isRetrying
                                )
                            }
                        )
                    }
                }
            } else {
                val isAtLeastOneFileUploaded =
                    uploadStatementUiState.value.selectedPdfs.any { it.isUploadSuccessful }
                val areAllFilesUploadedSuccessfully =
                    uploadStatementUiState.value.selectedPdfs.all { it.isUploadSuccessful }
                _uploadStatementUiState.update {
                    it.copy(
                        ctaType = if (isAtLeastOneFileUploaded) CtaType.SUBMIT else CtaType.CONFIRM,
                        shouldShowUploading = false,
                        shouldShowCta = false,
                        shouldShowUploadSuccess = areAllFilesUploadedSuccessfully,
                        shouldShowUploadError = areAllFilesUploadedSuccessfully.not(),
                        isPrimaryButtonEnabled = areAllFilesUploadedSuccessfully
                    )
                }
                filesUploaded = 0
                val uploaded = uploadStatementUiState.value.uploadedPdfs?.toMutableList()
                    ?: ArrayList<BankStatementPdfDetail>()
                val successfulFiles =
                    uploadStatementUiState.value.selectedPdfs.filter { it.isUploadSuccessful }
                val failedFiles = uploadStatementUiState.value.selectedPdfs.filter { it.isFailed }
                uploaded.addAll(successfulFiles)
                delay(1000L) //make delay to show success or error screen
                _uploadStatementUiState.update {
                    it.copy(
                        shouldShowCta = true,
                        shouldShowUploadSuccess = false,
                        shouldShowUploadError = false,
                        selectedPdfs = failedFiles,
                        uploadedPdfs = uploaded
                    )
                }
            }
        }
    }

    private fun updateUploadStatusAt(
        position: Int,
        isUploadSuccess: Boolean = false,
        isFailed: Boolean = false,
        failedReason: String? = null,
        isRetrying: Boolean = false
    ) {
        val totalFiles = uploadStatementUiState.value.selectedPdfs.size
        filesUploaded++
        val uploadPercent = (filesUploaded / totalFiles.toFloat()) * 100
        _uploadStatementUiState.update {
            it.copy(
                uploadPercent = uploadPercent.toInt(),
                selectedPdfs = it.selectedPdfs.mapIndexed { index, bankStatement ->
                    if (position == index) bankStatement.copy(
                        isUploadSuccessful = isUploadSuccess,
                        isFailed = isFailed,
                        failedReason = failedReason,
                        showCrossButton = isFailed
                    ) else bankStatement
                }
            )
        }
        if (position < totalFiles) {
            tryToUpload(position + 1, isRetrying)
        }
    }

    fun setCtaType(ctaType: String) {
        viewModelScope.launch {
            when (ctaType) {
                CtaType.CONFIRM.name -> {

                }

                CtaType.SUBMIT.name -> {
                    _uploadStatementUiState.update {
                        it.copy(ctaType = CtaType.SUBMIT)
                    }
                    fetchBankDetail()
                }
            }
        }
    }

    fun clearItems() {
        viewModelScope.launch {
            _uploadStatementUiState.update {
                it.copy(selectedPdfs = emptyList())
            }
        }

    }
}
