package com.jar.app.feature_contact_sync_common.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_contact_sync_common.shared.data.network.ContactsSyncDataSource
import com.jar.app.feature_contact_sync_common.shared.data.repository.IContactsSyncRepository
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListStaticDataUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactProcessingStatusUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchSentInviteListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.ProcessInviteUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.SendInviteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class ContactsSyncModule {

    @Provides
    @Singleton
    internal fun provideCommonContactsSyncModule(@AppHttpClient client: HttpClient): CommonContactsSyncModule {
        return CommonContactsSyncModule(client)
    }

    @Provides
    @Singleton
    internal fun provideContactsSyncRepository(commonContactsSyncModule: CommonContactsSyncModule): IContactsSyncRepository {
        return commonContactsSyncModule.contactsSyncRepository
    }

    @Provides
    @Singleton
    internal fun provideDataSource(commonContactsSyncModule: CommonContactsSyncModule): ContactsSyncDataSource {
        return commonContactsSyncModule.dataSource
    }

    @Provides
    @Singleton
    internal fun provideFetchContactListUseCase(commonContactsSyncModule: CommonContactsSyncModule): FetchContactListUseCase {
        return commonContactsSyncModule.provideFetchContactListUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchContactListStaticDataUseCase(commonContactsSyncModule: CommonContactsSyncModule): FetchContactListStaticDataUseCase {
        return commonContactsSyncModule.provideFetchContactListStaticDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchSentInviteListUseCase(commonContactsSyncModule: CommonContactsSyncModule): FetchSentInviteListUseCase {
        return commonContactsSyncModule.provideFetchSentInviteListUseCase
    }

    @Provides
    @Singleton
    internal fun provideProcessingInviteUseCase(commonContactsSyncModule: CommonContactsSyncModule): ProcessInviteUseCase {
        return commonContactsSyncModule.provideProcessingInviteUseCase
    }

    @Provides
    @Singleton
    internal fun provideProcessingStatusUseCase(commonContactsSyncModule: CommonContactsSyncModule): FetchContactProcessingStatusUseCase {
        return commonContactsSyncModule.provideProcessingStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideSendInviteUseCase(commonContactsSyncModule: CommonContactsSyncModule): SendInviteUseCase {
        return commonContactsSyncModule.provideSendInviteUseCase
    }


    @Provides
    @Singleton
    internal fun provideAddContactsUseCase(commonContactsSyncModule: CommonContactsSyncModule): AddContactsUseCase {
        return commonContactsSyncModule.provideAddContactsUseCase
    }

    @Provides
    @Singleton
    internal fun provideGetPendingInvitesUseCase(commonContactsSyncModule: CommonContactsSyncModule): FetchPendingInvitesUseCase {
        return commonContactsSyncModule.provideGetPendingInvitesUseCase
    }

}