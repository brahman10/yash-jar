package com.jar.app.feature_contact_sync_common.shared.domain.usecases

import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest

internal class AddContactsUseCaseImpl constructor(
    private val repostiory: IContactsSyncRepository
) : AddContactsUseCase {
    override suspend fun addContacts(addContactRequest: AddContactRequest) =
        repostiory.addContacts(addContactRequest)
}