package com.jar.app.feature_gold_lease.impl.di

import com.jar.app.feature_gold_lease.api.GoldLeaseApi
import com.jar.app.feature_gold_lease.impl.GoldLeaseApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class GoldLeaseApiModule {

    @Binds
    @ActivityScoped
    internal abstract fun provideLendingApi(goldLeaseApiImpl: GoldLeaseApiImpl): GoldLeaseApi

}