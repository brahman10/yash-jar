package com.jar.app.feature_homepage.shared.domain.use_case

import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface FetchBottomNavStickyCardDataUseCase {

    suspend fun fetchBottomNavStickyCardData() : Flow<RestClientResult<ApiResponseWrapper<LibraryCardData?>>>

}