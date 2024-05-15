package com.jar.app.feature_gifting.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_gifting.shared.data.network.GiftGoldDataSource
import com.jar.app.feature_gifting.shared.data.repository.GiftGoldRepositoryImpl
import com.jar.app.feature_gifting.shared.domain.repository.GiftGoldRepository
import com.jar.app.feature_gifting.shared.domain.use_case.FetchGiftGoldOptionsUseCase
import com.jar.app.feature_gifting.shared.domain.use_case.FetchReceivedGiftsUseCase
import com.jar.app.feature_gifting.shared.domain.use_case.MarkGiftViewedUseCase
import com.jar.app.feature_gifting.shared.domain.use_case.SendGiftUseCase
import com.jar.app.feature_gifting.shared.domain.use_case.impl.FetchGiftGoldOptionsUseCaseImpl
import com.jar.app.feature_gifting.shared.domain.use_case.impl.FetchReceivedGiftsUseCaseImpl
import com.jar.app.feature_gifting.shared.domain.use_case.impl.MarkGiftViewedUseCaseImpl
import com.jar.app.feature_gifting.shared.domain.use_case.impl.SendGiftUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class GiftingModule {

    @Provides
    @Singleton
    fun provideGiftGoldDataSource(@AppHttpClient client: HttpClient): GiftGoldDataSource {
        return GiftGoldDataSource(client)
    }

    @Provides
    @Singleton
    internal fun provideGiftingRepository(giftGoldDataSource: GiftGoldDataSource): GiftGoldRepository {
        return GiftGoldRepositoryImpl(giftGoldDataSource)
    }

    @Provides
    @Singleton
    internal fun provideSendGiftUseCase(giftGoldRepository: GiftGoldRepository): SendGiftUseCase {
        return SendGiftUseCaseImpl(giftGoldRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchReceivedGiftsUseCase(giftGoldRepository: GiftGoldRepository): FetchReceivedGiftsUseCase {
        return FetchReceivedGiftsUseCaseImpl(giftGoldRepository)
    }

    @Provides
    @Singleton
    internal fun provideMarkGiftViewedUseCase(giftGoldRepository: GiftGoldRepository): MarkGiftViewedUseCase {
        return MarkGiftViewedUseCaseImpl(giftGoldRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGiftGoldOptionsUseCase(giftGoldRepository: GiftGoldRepository): FetchGiftGoldOptionsUseCase {
        return FetchGiftGoldOptionsUseCaseImpl(giftGoldRepository)
    }
}