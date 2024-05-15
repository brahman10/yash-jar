package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository

internal class FetchContactFetchContactProcessingStatusUseCaseImpl constructor(private val repostiory: IContactsSyncRepository):
    FetchContactProcessingStatusUseCase {
    override suspend fun fetchContactProcessingStatus() = repostiory.fetchContactProcessingStatus()
}