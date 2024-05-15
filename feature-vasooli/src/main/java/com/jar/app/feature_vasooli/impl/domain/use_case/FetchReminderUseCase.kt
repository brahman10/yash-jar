package com.jar.app.feature_vasooli.impl.domain.use_case

import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_vasooli.impl.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

internal interface FetchReminderUseCase {

    suspend fun fetchReminder(loanId: String, medium: String): Flow<RestClientResult<ApiResponseWrapper<Reminder>>>

}