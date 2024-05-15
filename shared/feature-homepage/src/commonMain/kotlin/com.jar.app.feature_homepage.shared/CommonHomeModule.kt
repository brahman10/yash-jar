package com.jar.app.feature_homepage.shared

import com.jar.app.feature_home.shared.HomePageDatabase
import com.jar.app.feature_homepage.shared.data.db.HomeFeedLocalDataSource
import com.jar.app.feature_homepage.shared.data.network.HomeDataSource
import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.domain.repository.HomeRepositoryImpl
import com.jar.app.feature_homepage.shared.domain.use_case.ClaimBonusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.ClearCachedHomeFeedUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.DismissUpcomingPreNotificationUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchAppWalkthroughUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchBottomNavStickyCardDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchDailySavingsCardUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureRedirectionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFeatureViewUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinHomeScreenDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinOnboardingStatusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinProgressUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstCoinTransitionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchFirstGoldCoinIntroUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHamburgerMenuItemsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHelpVideosUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedActionsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeFeedImagesUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomePageExperimentsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeScreenBottomSheetPromptUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchHomeStaticCardsOrderingUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchInAppReviewStatusUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchPartnerBannerUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchQuickActionsUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchRoundOffCardDataUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchSmsIngestionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchStaticPopupInfoUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpcomingPreNotificationUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateDailySavingAmountInfoUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUpdateFirstCoinOrderIdUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchUserGoldBreakdownUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.FetchVibaCardUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateAppWalkthroughCompletedUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateLockerViewShownUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.UpdateUserInteractionUseCase
import com.jar.app.feature_homepage.shared.domain.use_case.impl.ClaimBonusUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.ClearCachedHomeFeedUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.DismissUpcomingPreNotificationUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchAppWalkthroughUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchBottomNavStickyCardDataUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchDailySavingsCardUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFeatureRedirectionUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFeatureViewUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFetchFirstGoldCoinIntroUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFirstCoinHomeScreenDataUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFirstCoinOnboardingStatusUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFirstCoinProgressUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchFirstCoinTransitionUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHamburgerMenuItemsUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHelpVideosUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHomeFeedActionsUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHomeFeedImagesUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHomePageExperimentsUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHomeScreenBottomSheetPromptUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchHomeStaticCardsOrderingUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchInAppReviewStatusImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchPartnerBannerUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchQuickActionsUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchRoundOffCardDataUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchSmsIngestionUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchStaticPopupInfoUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchUpcomingPreNotificationUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchUpdateDailySavingAmountInfoUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchUpdateFirstCoinOrderIdUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchUserGoldBreakdownUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.FetchVibaCardUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.UpdateAppWalkthroughCompletedUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.UpdateLockerViewShownUseCaseImpl
import com.jar.app.feature_homepage.shared.domain.use_case.impl.UpdateUserInteractionUseCaseImpl
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.HttpClient

class CommonHomeModule(
    client: HttpClient,
    driver: SqlDriver,
    serializer: Serializer
) {

    val homeDataSource: HomeDataSource by lazy {
        HomeDataSource(client = client)
    }

    val homePageDatabase by lazy {
        HomePageDatabase(driver)
    }

    val homeFeedLocalDataSource by lazy {
        HomeFeedLocalDataSource(homePageDatabase)
    }

    val homeRepository: HomeRepository by lazy {
        HomeRepositoryImpl(
            homeDataSource,
            homeFeedLocalDataSource,
            serializer
        )
    }

    val fetchHamburgerMenuItemsUseCase: FetchHamburgerMenuItemsUseCase by lazy {
        FetchHamburgerMenuItemsUseCaseImpl(homeRepository)
    }

    val clearCachedHomeFeedUseCase: ClearCachedHomeFeedUseCase by lazy {
        ClearCachedHomeFeedUseCaseImpl(homeRepository)
    }

    val provideFetchFeatureViewUseCase: FetchFeatureViewUseCase by lazy {
        FetchFeatureViewUseCaseImpl(homeRepository)
    }

    val provideFetchHomePageExperimentsUseCase: FetchHomePageExperimentsUseCase by lazy {
        FetchHomePageExperimentsUseCaseImpl(homeRepository)
    }

    val provideFetchStaticPopupInfoUseCase: FetchStaticPopupInfoUseCase by lazy {
        FetchStaticPopupInfoUseCaseImpl(homeRepository)
    }

    val provideFetchPartnerBannerUseCase: FetchPartnerBannerUseCase by lazy {
        FetchPartnerBannerUseCaseImpl(homeRepository)
    }

    val provideClaimBonusUseCase: ClaimBonusUseCase by lazy {
        ClaimBonusUseCaseImpl(homeRepository)
    }

    val provideFetchUpdateDailySavingAmountInfoUseCase: FetchUpdateDailySavingAmountInfoUseCase by lazy {
        FetchUpdateDailySavingAmountInfoUseCaseImpl(homeRepository)
    }

    val provideFetchRoundOffCardDataUseCase: FetchRoundOffCardDataUseCase by lazy {
        FetchRoundOffCardDataUseCaseImpl(homeRepository)
    }

    val provideFetchHelpVideosUseCase: FetchHelpVideosUseCase by lazy {
        FetchHelpVideosUseCaseImpl(homeRepository)
    }

    val provideHomeStaticCardsOrderingUseCase: FetchHomeStaticCardsOrderingUseCase by lazy {
        FetchHomeStaticCardsOrderingUseCaseImpl(homeRepository)
    }

    val provideUpcomingPreNotificationUseCase: FetchUpcomingPreNotificationUseCase by lazy {
        FetchUpcomingPreNotificationUseCaseImpl(homeRepository)
    }

    val provideDismissUpcomingPreNotificationUseCase: DismissUpcomingPreNotificationUseCase by lazy {
        DismissUpcomingPreNotificationUseCaseImpl(homeRepository)
    }

    val provideFetchVibaCardUseCase: FetchVibaCardUseCase by lazy {
        FetchVibaCardUseCaseImpl(homeRepository)
    }

    val provideQuickActionsUseCase: FetchQuickActionsUseCase by lazy {
        FetchQuickActionsUseCaseImpl(homeRepository)
    }

    val provideFirstGoldCoinIntroUseCase: FetchFirstGoldCoinIntroUseCase by lazy {
        FetchFetchFirstGoldCoinIntroUseCaseImpl(homeRepository)
    }

    val provideFirstCoinHomeScreenDataUseCase: FetchFirstCoinHomeScreenDataUseCase by lazy {
        FetchFirstCoinHomeScreenDataUseCaseImpl(homeRepository)
    }

    val provideFetchFirstCoinOnboardingStatusUseCase: FetchFirstCoinOnboardingStatusUseCase by lazy {
        FetchFirstCoinOnboardingStatusUseCaseImpl(homeRepository)
    }

    val provideFetchFirstCoinProgressUseCase: FetchFirstCoinProgressUseCase by lazy {
        FetchFirstCoinProgressUseCaseImpl(homeRepository)
    }

    val provideFetchFirstCoinTransitionUseCase: FetchFirstCoinTransitionUseCase by lazy {
        FetchFirstCoinTransitionUseCaseImpl(homeRepository)
    }

    val provideFetchDailySavingsCardUseCase: FetchDailySavingsCardUseCase by lazy {
        FetchDailySavingsCardUseCaseImpl(homeRepository)
    }

    val provideFetchHomeFeedActionsUseCase: FetchHomeFeedActionsUseCase by lazy {
        FetchHomeFeedActionsUseCaseImpl(homeRepository)
    }

    val provideUpdateUserInteractionUseCase: UpdateUserInteractionUseCase by lazy {
        UpdateUserInteractionUseCaseImpl(homeRepository)
    }

    val provideSmsIngestionUseCase: FetchSmsIngestionUseCase by lazy {
        FetchSmsIngestionUseCaseImpl(homeRepository)
    }

    val provideFetchInAppReviewStatusUseCase: FetchInAppReviewStatusUseCase by lazy {
        FetchInAppReviewStatusImpl(homeRepository)
    }

    val provideFetchHomeFeedImagesUseCase: FetchHomeFeedImagesUseCase by lazy {
        FetchHomeFeedImagesUseCaseImpl(homeRepository)
    }

    val provideFetchUpdateFirstCoinOrderIdUseCase: FetchUpdateFirstCoinOrderIdUseCase by lazy {
        FetchUpdateFirstCoinOrderIdUseCaseImpl(homeRepository)
    }

    val provideClearCachedHomeFeedUseCase: ClearCachedHomeFeedUseCase by lazy {
        ClearCachedHomeFeedUseCaseImpl(homeRepository)
    }

    val provideUpdateLockerViewShownUseCase: UpdateLockerViewShownUseCase by lazy {
        UpdateLockerViewShownUseCaseImpl(homeRepository)
    }

    val provideFetchHomeScreenBottomSheetPromptUseCase: FetchHomeScreenBottomSheetPromptUseCase by lazy {
        FetchHomeScreenBottomSheetPromptUseCaseImpl(homeRepository)
    }

    val provideFetchHamburgerMenuItemsUseCase: FetchHamburgerMenuItemsUseCase by lazy {
        FetchHamburgerMenuItemsUseCaseImpl(homeRepository)
    }

    val provideFetchAppWalkthroughUseCase: FetchAppWalkthroughUseCase by lazy {
        FetchAppWalkthroughUseCaseImpl(homeRepository)
    }

    val provideUpdateAppWalkthroughCompletedUseCase: UpdateAppWalkthroughCompletedUseCase by lazy {
        UpdateAppWalkthroughCompletedUseCaseImpl(homeRepository)
    }

    val fetchUserGoldBreakdownUseCase: FetchUserGoldBreakdownUseCase by lazy {
        FetchUserGoldBreakdownUseCaseImpl(homeRepository)
    }

    val fetchBottomNavStickyCardDataUseCase: FetchBottomNavStickyCardDataUseCase by lazy {
        FetchBottomNavStickyCardDataUseCaseImpl(homeRepository)
    }

    val fetchFeatureRedirectionUseCase: FetchFeatureRedirectionUseCase by lazy {
        FetchFeatureRedirectionUseCaseImpl(homeRepository)
    }

}