package com.jar.app.feature_vasooli.impl.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

internal interface DeleteVasooliEntryUseCase {

    suspend fun deleteVasooliEntry(loanId: String): Flow<RestClientResult<ApiResponseWrapper<Unit?>>>

}