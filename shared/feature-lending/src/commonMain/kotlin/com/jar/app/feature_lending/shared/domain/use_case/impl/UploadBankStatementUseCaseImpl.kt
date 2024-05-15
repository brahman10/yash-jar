package com.jar.app.feature_lending.shared.domain.use_case.impl

import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.use_case.UploadBankStatementUseCase

internal class UploadBankStatementUseCaseImpl constructor(
    private val lendingRepository: LendingRepository
) : UploadBankStatementUseCase {
    override suspend fun uploadBankStatementPdf(filename: String, byteArray: ByteArray)  =  lendingRepository.uploadBankStatement(filename = filename, byteArray = byteArray)

}