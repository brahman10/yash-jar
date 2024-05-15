package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest

internal class ProcessInviteUseCaseImpl constructor(private val repostiory: IContactsSyncRepository) :
    ProcessInviteUseCase {
    override suspend fun processInvite(processInviteRequest: ProcessInviteRequest) =
        repostiory.processInvite(processInviteRequest)
}