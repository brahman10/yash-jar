package com.jar.app.feature_promo_code.impl.di

import com.jar.app.feature_promo_code.api.PromoCodeApi
import com.jar.app.feature_promo_code.impl.PromoCodeApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
internal abstract class PromoCodeApiModule {
    @Binds
    @ActivityScoped
    internal abstract fun providePromoCodeApi(promoCodeApiImpl: PromoCodeApiImpl): PromoCodeApi
}