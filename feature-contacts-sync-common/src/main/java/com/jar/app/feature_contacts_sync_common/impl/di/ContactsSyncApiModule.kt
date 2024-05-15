package com.jar.app.feature_contacts_sync_common.impl.di

import com.jar.app.feature_contacts_sync_common.api.ContactsSyncApi
import com.jar.app.feature_contacts_sync_common.impl.ContactsSyncApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ContactsSyncApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideContactsSyncApi(contactsSyncApiImpl: ContactsSyncApiImpl): ContactsSyncApi
}