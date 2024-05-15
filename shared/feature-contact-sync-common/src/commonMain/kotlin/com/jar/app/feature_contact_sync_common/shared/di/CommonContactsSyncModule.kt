package com.jar.app.feature_contact_sync_common.shared.di

import com.jar.app.feature_contact_sync_common.shared.data.network.ContactsSyncDataSource
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.repository.ContactsSyncRepositoryImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactFetchContactProcessingStatusUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListStaticDataUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListStaticDataUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactProcessingStatusUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchSentInviteListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchSentInviteListUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.ProcessInviteUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.ProcessInviteUseCaseImpl
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.SendInviteUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.SendInviteUseCaseImpl
import io.ktor.client.HttpClient

class CommonContactsSyncModule(
    client: HttpClient
) {

    val dataSource: ContactsSyncDataSource by lazy {
        ContactsSyncDataSource(client)
    }

    val contactsSyncRepository: IContactsSyncRepository by lazy {
        ContactsSyncRepositoryImpl(dataSource)
    }

    val provideFetchContactListUseCase: FetchContactListUseCase by lazy {
        FetchContactListUseCaseImpl(contactsSyncRepository)
    }

    val provideFetchContactListStaticDataUseCase: FetchContactListStaticDataUseCase by lazy {
        FetchContactListStaticDataUseCaseImpl(contactsSyncRepository)
    }

    val provideFetchSentInviteListUseCase: FetchSentInviteListUseCase by lazy {
        FetchSentInviteListUseCaseImpl(contactsSyncRepository)
    }

    val provideProcessingInviteUseCase: ProcessInviteUseCase by lazy {
        ProcessInviteUseCaseImpl(contactsSyncRepository)
    }

    val provideProcessingStatusUseCase: FetchContactProcessingStatusUseCase by lazy {
        FetchContactFetchContactProcessingStatusUseCaseImpl(contactsSyncRepository)
    }

    val provideSendInviteUseCase: SendInviteUseCase by lazy {
        SendInviteUseCaseImpl(contactsSyncRepository)
    }

    val provideAddContactsUseCase: AddContactsUseCase by lazy {
        AddContactsUseCaseImpl(contactsSyncRepository)
    }

    val provideGetPendingInvitesUseCase: FetchPendingInvitesUseCase by lazy {
        FetchPendingInvitesUseCaseImpl(contactsSyncRepository)
    }
}