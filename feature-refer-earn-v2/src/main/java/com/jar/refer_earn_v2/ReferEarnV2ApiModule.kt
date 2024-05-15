package com.jar.refer_earn_v2

import com.jar.refer_earn_v2.api.ReferEarnV2Api
import com.jar.refer_earn_v2.impl.data.ReferEarnV2ApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class ReferEarnV2ApiModule {
    @Binds
    @ActivityScoped
    internal abstract fun provideReferEarnV2Api(goldRedemptionApiImpl: ReferEarnV2ApiImpl): ReferEarnV2Api
}