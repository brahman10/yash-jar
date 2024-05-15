package com.jar.app.feature_coupon_api

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_coupon_api.data.network.CouponDataSource
import com.jar.app.feature_coupon_api.data.repository.CouponRepository
import com.jar.app.feature_coupon_api.domain.repository.CouponRepositoryImpl
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_coupon_api.domain.use_case.impl.ApplyCouponUseCaseImpl
import com.jar.app.feature_coupon_api.domain.use_case.impl.FetchCouponCodeUseCaseImpl
import com.jar.app.feature_coupon_api.util.CouponOrderUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class CouponApiModule {
    @Provides
    @Singleton
    internal fun provideCouponApiDataSource(@AppHttpClient client: HttpClient): CouponDataSource {
        return CouponDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideCouponApiRepository(couponDataSource: CouponDataSource): CouponRepository {
        return CouponRepositoryImpl(couponDataSource)
    }

    @Provides
    @Singleton
    internal fun provideFetchCouponCodeUseCase(couponRepository: CouponRepository): FetchCouponCodeUseCase {
        return FetchCouponCodeUseCaseImpl(couponRepository)
    }

    @Provides
    @Singleton
    internal fun provideApplyCouponUseCase(couponRepository: CouponRepository): ApplyCouponUseCase {
        return ApplyCouponUseCaseImpl(couponRepository)
    }

    @Provides
    @Singleton
    internal fun provideCouponOrderUtil(): CouponOrderUtil {
        return CouponOrderUtil()
    }
}