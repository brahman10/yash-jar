package com.jar.app.feature_homepage.shared.di

import android.content.Context
import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_home.shared.HomePageDatabase
import com.jar.app.feature_homepage.shared.CommonHomeModule
import com.jar.app.feature_homepage.shared.data.db.DatabaseDriverFactory
import com.jar.app.feature_homepage.shared.data.db.HomeFeedLocalDataSource
import com.jar.app.feature_homepage.shared.data.network.HomeDataSource
import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.di.qualifiers.HomePageDBDriver
import com.jar.app.feature_homepage.shared.domain.use_case.*
import com.jar.app.feature_homepage.shared.domain.use_case.impl.*
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class HomeModule {

    @Provides
    @Singleton
    @HomePageDBDriver
    internal fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
        return DatabaseDriverFactory(context).createDriver()
    }

    @Provides
    @Singleton
    internal fun provideCommonHomeModule(
        @AppHttpClient client: HttpClient,
        @HomePageDBDriver driver: SqlDriver,
        serializer: Serializer
    ): CommonHomeModule {
        return CommonHomeModule(client, driver, serializer)
    }

    @Provides
    @Singleton
    internal fun provideHomePageDatabase(
        commonHomeModule: CommonHomeModule
    ): HomePageDatabase {
        return commonHomeModule.homePageDatabase
    }

    @Provides
    @Singleton
    internal fun provideHomeFeedLocalDataSource(commonHomeModule: CommonHomeModule): HomeFeedLocalDataSource {
        return commonHomeModule.homeFeedLocalDataSource
    }

    @Provides
    @Singleton
    internal fun provideHomeDataSource(commonHomeModule: CommonHomeModule): HomeDataSource {
        return commonHomeModule.homeDataSource
    }

    @Provides
    @Singleton
    internal fun provideHomeRepository(
        commonHomeModule: CommonHomeModule
    ): HomeRepository {
        return commonHomeModule.homeRepository
    }

    @Provides
    @Singleton
    internal fun provideFetchFeatureViewUseCase(commonHomeModule: CommonHomeModule): FetchFeatureViewUseCase {
        return commonHomeModule.provideFetchFeatureViewUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchHomePageExperimentsUseCase(commonHomeModule: CommonHomeModule): FetchHomePageExperimentsUseCase {
        return commonHomeModule.provideFetchHomePageExperimentsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchStaticPopupInfoUseCase(commonHomeModule: CommonHomeModule): FetchStaticPopupInfoUseCase {
        return commonHomeModule.provideFetchStaticPopupInfoUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchPartnerBannerUseCase(commonHomeModule: CommonHomeModule): FetchPartnerBannerUseCase {
        return commonHomeModule.provideFetchPartnerBannerUseCase
    }

    @Provides
    @Singleton
    internal fun provideClaimBonusUseCase(commonHomeModule: CommonHomeModule): ClaimBonusUseCase {
        return commonHomeModule.provideClaimBonusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchUpdateDailySavingAmountInfoUseCase(commonHomeModule: CommonHomeModule): FetchUpdateDailySavingAmountInfoUseCase {
        return commonHomeModule.provideFetchUpdateDailySavingAmountInfoUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchRoundOffCardDataUseCase(commonHomeModule: CommonHomeModule): FetchRoundOffCardDataUseCase {
        return commonHomeModule.provideFetchRoundOffCardDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchHelpVideosUseCase(commonHomeModule: CommonHomeModule): FetchHelpVideosUseCase {
        return commonHomeModule.provideFetchHelpVideosUseCase
    }

    @Provides
    @Singleton
    internal fun provideHomeStaticCardsOrderingUseCase(commonHomeModule: CommonHomeModule): FetchHomeStaticCardsOrderingUseCase {
        return commonHomeModule.provideHomeStaticCardsOrderingUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpcomingPreNotificationUseCase(commonHomeModule: CommonHomeModule): FetchUpcomingPreNotificationUseCase {
        return commonHomeModule.provideUpcomingPreNotificationUseCase
    }

    @Provides
    @Singleton
    internal fun provideDismissUpcomingPreNotificationUseCase(commonHomeModule: CommonHomeModule): DismissUpcomingPreNotificationUseCase {
        return commonHomeModule.provideDismissUpcomingPreNotificationUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchVibaCardUseCase(commonHomeModule: CommonHomeModule): FetchVibaCardUseCase {
        return commonHomeModule.provideFetchVibaCardUseCase
    }

    @Provides
    @Singleton
    internal fun provideQuickActionsUseCase(commonHomeModule: CommonHomeModule): FetchQuickActionsUseCase {
        return commonHomeModule.provideQuickActionsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFirstGoldCoinIntroUseCase(commonHomeModule: CommonHomeModule): FetchFirstGoldCoinIntroUseCase {
        return commonHomeModule.provideFirstGoldCoinIntroUseCase
    }

    @Provides
    @Singleton
    internal fun provideFirstCoinHomeScreenDataUseCase(commonHomeModule: CommonHomeModule): FetchFirstCoinHomeScreenDataUseCase {
        return commonHomeModule.provideFirstCoinHomeScreenDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchFirstCoinOnboardingStatusUseCase(commonHomeModule: CommonHomeModule): FetchFirstCoinOnboardingStatusUseCase {
        return commonHomeModule.provideFetchFirstCoinOnboardingStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchFirstCoinProgressUseCase(commonHomeModule: CommonHomeModule): FetchFirstCoinProgressUseCase {
        return commonHomeModule.provideFetchFirstCoinProgressUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchFirstCoinTransitionUseCase(commonHomeModule: CommonHomeModule): FetchFirstCoinTransitionUseCase {
        return commonHomeModule.provideFetchFirstCoinTransitionUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchDailySavingsCardUseCase(commonHomeModule: CommonHomeModule): FetchDailySavingsCardUseCase {
        return commonHomeModule.provideFetchDailySavingsCardUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchHomeFeedActionsUseCase(commonHomeModule: CommonHomeModule): FetchHomeFeedActionsUseCase {
        return commonHomeModule.provideFetchHomeFeedActionsUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpdateUserInteractionUseCase(commonHomeModule: CommonHomeModule): UpdateUserInteractionUseCase {
        return commonHomeModule.provideUpdateUserInteractionUseCase
    }

    @Provides
    @Singleton
    internal fun provideSmsIngestionUseCase(commonHomeModule: CommonHomeModule): FetchSmsIngestionUseCase {
        return commonHomeModule.provideSmsIngestionUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchInAppReviewStatusUseCase(commonHomeModule: CommonHomeModule): FetchInAppReviewStatusUseCase {
        return commonHomeModule.provideFetchInAppReviewStatusUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchHomeFeedImagesUseCase(commonHomeModule: CommonHomeModule): FetchHomeFeedImagesUseCase {
        return commonHomeModule.provideFetchHomeFeedImagesUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchUpdateFirstCoinOrderIdUseCase(commonHomeModule: CommonHomeModule): FetchUpdateFirstCoinOrderIdUseCase {
        return commonHomeModule.provideFetchUpdateFirstCoinOrderIdUseCase
    }

    @Provides
    @Singleton
    internal fun provideClearCachedHomeFeedUseCase(commonHomeModule: CommonHomeModule): ClearCachedHomeFeedUseCase {
        return commonHomeModule.provideClearCachedHomeFeedUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpdateLockerViewShownUseCase(commonHomeModule: CommonHomeModule): UpdateLockerViewShownUseCase {
        return commonHomeModule.provideUpdateLockerViewShownUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchHomeScreenBottomSheetPromptUseCase(commonHomeModule: CommonHomeModule): FetchHomeScreenBottomSheetPromptUseCase {
        return commonHomeModule.provideFetchHomeScreenBottomSheetPromptUseCase
    }


    @Provides
    @Singleton
    internal fun provideFetchHamburgerMenuItemsUseCase(commonHomeModule: CommonHomeModule): FetchHamburgerMenuItemsUseCase {
        return commonHomeModule.provideFetchHamburgerMenuItemsUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchAppWalkthroughUseCase(commonHomeModule: CommonHomeModule): FetchAppWalkthroughUseCase {
        return commonHomeModule.provideFetchAppWalkthroughUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpdateAppWalkthroughCompletedUseCase(commonHomeModule: CommonHomeModule): UpdateAppWalkthroughCompletedUseCase {
        return commonHomeModule.provideUpdateAppWalkthroughCompletedUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchBottomNavStickyCardUseCase(commonHomeModule: CommonHomeModule): FetchBottomNavStickyCardDataUseCase {
        return commonHomeModule.fetchBottomNavStickyCardDataUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchUserGoldBreakdownUseCase(commonHomeModule: CommonHomeModule): FetchUserGoldBreakdownUseCase {
        return commonHomeModule.fetchUserGoldBreakdownUseCase
    }

    @Provides
    @Singleton
    internal fun providefetchFeatureRedirectionUseCase(commonHomeModule: CommonHomeModule): FetchFeatureRedirectionUseCase {
        return commonHomeModule.fetchFeatureRedirectionUseCase
    }

}