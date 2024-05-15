package com.jar.app.feature_spin.shared.domain.repository

import com.jar.app.feature_spin.shared.domain.model.IntroPageModel
import com.jar.app.feature_spin.shared.domain.model.UseWinningPopupCta
import com.jar.app.feature_spin.shared.domain.model.SpinsMetaData
import com.jar.internal.library.jar_core_network.api.data.BaseRepository
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.flow.Flow

interface SpinRepositoryExternal : BaseRepository {

    suspend fun fetchSpinsMetaData(includeView: Boolean): Flow<RestClientResult<ApiResponseWrapper<SpinsMetaData>>>

    suspend fun fetchIntroPageData(): Flow<RestClientResult<ApiResponseWrapper<IntroPageModel>>>

    suspend fun fetchUseWinning(): Flow<RestClientResult<ApiResponseWrapper<UseWinningPopupCta>>>

}