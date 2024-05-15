package com.jar.app.feature_homepage.shared.domain.repository

import com.jar.app.core_base.domain.model.InfoDialogResponse
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_homepage.shared.data.network.HomeDataSource
import com.jar.app.feature_homepage.shared.data.repository.HomeRepository
import com.jar.app.feature_homepage.shared.data.db.HomeFeedLocalDataSource
import com.jar.app.feature_homepage.shared.domain.model.FeatureFlag
import com.jar.app.feature_homepage.shared.domain.model.FirstGoldCoinIntro
import com.jar.app.feature_homepage.shared.domain.model.HelpVideosResponse
import com.jar.app.feature_homepage.shared.domain.model.HomeStaticCardOrderingData
import com.jar.app.feature_homepage.shared.domain.model.PreNotifyAutopay
import com.jar.app.feature_homepage.shared.domain.model.QuickActionResponse
import com.jar.app.feature_homepage.shared.domain.model.partner_banner.BannerList
import com.jar.app.feature_homepage.shared.domain.model.round_off.RoundOffData
import com.jar.app.feature_homepage.shared.domain.model.update_daily_saving.UpdateDailySavingInfo
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import kotlinx.coroutines.flow.Flow

internal class HomeRepositoryImpl constructor(
    private val homeDataSource: HomeDataSource,
    private val homeFeedLocalDataSource: HomeFeedLocalDataSource,
    private val serializer: Serializer
) : HomeRepository {

    override suspend fun fetchHomeScreenBottomSheetPrompt() =
        getFlowResult {
            homeDataSource.fetchHomeScreenBottomSheetPrompt()
        }

    override suspend fun fetchFeature(featureFlag: FeatureFlag) =
        getFlowResultV2<ApiResponseWrapper<Unit?>>(
            fetchResultFromServer = homeDataSource.fetchFeature(featureFlag),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchHomePageExperiments() = getFlowResultV2<ApiResponseWrapper<Unit?>>(
        fetchResultFromServer = homeDataSource.fetchHomePageExperiments(),
        storeResultInCache = { key, value ->
            homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
        },
        fetchResultFromCache = {
            homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
        },
        convertFromString = {
            serializer.decodeFromString(it)
        }
    )

    override suspend fun fetchStaticPopupInfo(contentType: String) =
        getFlowResultV2<ApiResponseWrapper<InfoDialogResponse>>(
            fetchResultFromServer = homeDataSource.fetchStaticPopupInfo(contentType),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchUpdateDailySavingAmountInfo(includeView: Boolean) =
        getFlowResultV2<ApiResponseWrapper<UpdateDailySavingInfo>>(
            fetchResultFromServer = homeDataSource.fetchUpdateDailySavingAmountInfo(includeView = includeView),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchHelpVideos(
        language: String,
        includeView: Boolean
    ) = getFlowResultV2<ApiResponseWrapper<HelpVideosResponse>>(
        fetchResultFromServer = homeDataSource.fetchHelpVideos(BaseConstants.StaticContentType.HELP_VIDEOS.name, language, includeView),
        storeResultInCache = { key, value ->
            homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
        },
        fetchResultFromCache = {
            homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
        },
        convertFromString = {
            serializer.decodeFromString(it)
        }
    )

    override suspend fun fetchPartnerBanners(includeView: Boolean) =
        getFlowResultV2<ApiResponseWrapper<BannerList>>(
            fetchResultFromServer = homeDataSource.fetchPartnerBanners(includeView),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun claimBonus(orderId: String) =
        getFlowResult { homeDataSource.claimBonus(orderId) }

    override suspend fun fetchRoundOffCardData() =
        getFlowResultV2<ApiResponseWrapper<RoundOffData?>>(
            fetchResultFromServer = homeDataSource.fetchRoundOffCardData(),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchHomeStaticCardsOrdering() =
        getFlowResultV2<ApiResponseWrapper<HomeStaticCardOrderingData>>(
            fetchResultFromServer = homeDataSource.fetchHomeStaticCardsOrdering(),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchUpcomingPreNotification(includeView: Boolean) =
        getFlowResultV2<ApiResponseWrapper<PreNotifyAutopay>>(
            fetchResultFromServer = homeDataSource.fetchUpcomingPreNotification(includeView),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun dismissUpcomingPreNotification(
        dismissalType: String,
        preNotificationIds: List<String>
    ) =
        getFlowResult {
            homeDataSource.dismissUpcomingPreNotification(
                dismissalType,
                preNotificationIds
            )
        }

    override suspend fun fetchQuickActions(type: String) =
        getFlowResultV2<ApiResponseWrapper<QuickActionResponse?>>(
            fetchResultFromServer = homeDataSource.fetchQuickActions(type),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchFirstGoldCoinIntro() =
        getFlowResultV2<ApiResponseWrapper<FirstGoldCoinIntro>>(
            fetchResultFromServer = homeDataSource.fetchFirstGoldCoinIntro(),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchFirstCoinHomeScreenData() =
        getFlowResultV2<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinHomeScreenData?>>(
            fetchResultFromServer = homeDataSource.fetchFirstCoinHomeScreenData(),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchFirstCoinProgressData() =
        getFlowResultV2<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinProgressData>>(
            fetchResultFromServer = homeDataSource.fetchFirstCoinProgressData(),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = {
                homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun fetchFirstCoinTransitionPageData() =
        getFlowResultV2<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.FirstCoinTransitionData>>(
            fetchResultFromServer = homeDataSource.fetchFirstCoinTransitionPageData(),
            storeResultInCache = { key, value ->
                homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
            },
            fetchResultFromCache = { key ->
                homeFeedLocalDataSource.fetchHomeFeedData(key)?.value_
            },
            convertFromString = {
                serializer.decodeFromString(it)
            }
        )

    override suspend fun sendFirstCoinOnboardingStatus()
            =
        getFlowResult {
            homeDataSource.sendFirstCoinOnboardingStatus()
        }

    override suspend fun updateFirstCoinDeliveryStatus(orderId: String) = getFlowResult {
        homeDataSource.updateFirstCoinDeliveryStatus(orderId)
    }

    override suspend fun updateUserInteraction(order: Int, featureType: String) = getFlowResult {
        homeDataSource.updateUserInteraction(order, featureType)
    }

    override suspend fun shouldSendSmsOnDemand() = getFlowResult {
        homeDataSource.shouldSendSmsOnDemand()
    }

    override suspend fun fetchHomeFeedActions() = getFlowResultV2<ApiResponseWrapper<Unit?>>(
        fetchResultFromServer = homeDataSource.fetchHomeFeedActions(),
        storeResultInCache = { key, value ->
            homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
        },
        fetchResultFromCache = {
            homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
        },
        convertFromString = {
            serializer.decodeFromString(it)
        }
    )

    override suspend fun fetchDSCardData() = getFlowResultV2<ApiResponseWrapper<com.jar.app.feature_homepage.shared.domain.model.DailySavingsV2CardResponse?>>(
        fetchResultFromServer = homeDataSource.fetchDSCardData(),
        storeResultInCache = { key, value ->
            homeFeedLocalDataSource.insertHomeFeed(key, serializer.encodeToString(value))
        },
        fetchResultFromCache = {
            homeFeedLocalDataSource.fetchHomeFeedData(it)?.value_
        },
        convertFromString = {
            serializer.decodeFromString(it)
        }
    )

    override suspend fun fetchInAppReviewStatus() = getFlowResult {
        homeDataSource.fetchInAppReviewStatus()
    }
    override suspend fun fetchHomeFeedImages() = getFlowResult {
        homeDataSource.fetchHomeFeedImages()
    }

    override suspend fun updateLockerViewShown() = getFlowResult {
        homeDataSource.updateLockerViewShown()
    }

    override suspend fun fetchAppWalkthrough() = getFlowResult {
        homeDataSource.fetchAppWalkthrough()
    }

    override suspend fun updateAppWalkthroughCompleted() = getFlowResult {
        homeDataSource.updateAppWalkthroughCompleted()
    }

    override suspend fun clearAllData() {
        homeFeedLocalDataSource.clearDatabase()
    }

    override suspend fun fetchVibaCardDetails() = getFlowResult {
        homeDataSource.fetchVibaCardDetails()
    }

    override suspend fun fetchHamburgerData() =
        getFlowResult { homeDataSource.fetchHamburgerData() }

    override suspend fun fetchBottomNavStickyCardData(): Flow<RestClientResult<ApiResponseWrapper<LibraryCardData?>>> =
        getFlowResult { homeDataSource.fetchBottomNavStickyCardData() }


    override suspend fun fetchUserGoldBreakdown() = getFlowResult {
        homeDataSource.fetchUserGoldBreakdown()
    }

    override suspend fun fetchFeatureRedirectionData() = getFlowResult {
        homeDataSource.fetchFeatureRedirectionData()
    }
}