package com.jar.app.feature_goal_based_saving.shared.domain.repository

import com.jar.app.feature_goal_based_saving.shared.data.model.*
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_goal_based_saving.shared.data.network.GoalBasedSavingDataSource
import com.jar.app.feature_goal_based_saving.shared.data.repository.GoalBasedSavingRepository
import kotlinx.coroutines.flow.Flow

internal class GoalBasedSavingRepositoryImpl constructor(
    private val goalBasedSavingDataSource: GoalBasedSavingDataSource
): GoalBasedSavingRepository {
    override suspend fun fetchGoalIntroPageStaticResponse(): Flow<RestClientResult<ApiResponseWrapper<GoalSavingsIntoPage>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchIntroPageStaticData()
        }
    }

    override suspend fun fetchGoalNameScreenResponse(): Flow<RestClientResult<ApiResponseWrapper<GoalFirstQuestionResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchGoalNameScreenResponse()
        }
    }

    override suspend fun fetchGoalAmountScreenDetails(): Flow<RestClientResult<ApiResponseWrapper<GoalAmountResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchGoalAmountScreenResponse()
        }
    }

    override suspend fun fetchGoalDurationScreenDetails(amount: Int): Flow<RestClientResult<ApiResponseWrapper<GoalDurationResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchGoalDurationScreenResponse(amount)
        }
    }

    override suspend fun fetchDailyAmount(amount: Int, duration: Int): Flow<RestClientResult<ApiResponseWrapper<CalculateDailyAmountResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchDailyCalculatedAmountResponse(amount, duration)
        }
    }

    override suspend fun fetchMergeGoalResposnse(): Flow<RestClientResult<ApiResponseWrapper<MergeGoalResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchMergeGoalResponse()
        }
    }

    override suspend fun fetchMandateResposnse(amount: Int, savingsType: String): Flow<RestClientResult<ApiResponseWrapper<MandateInfo>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchMandateInfoResponse(amount, savingsType)
        }
    }

    override suspend fun fetchAbandonScreenResposnse(): Flow<RestClientResult<ApiResponseWrapper<AbandonedScreenResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchAbandonScreenResponse()
        }
    }

    override suspend fun createGoal(createGoalRequest: CreateGoalRequest): Flow<RestClientResult<ApiResponseWrapper<CreateGoalResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.createGoal(createGoalRequest)
        }
    }

    override suspend fun fetchQnA(): Flow<RestClientResult<ApiResponseWrapper<QnAResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchQnA()
        }
    }

    override suspend fun endGoal(goalEndRequest: GoalEndRequest): Flow<RestClientResult<ApiResponseWrapper<GoalEndResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.endGoal(goalEndRequest)
        }
    }

    override suspend fun fetchMangeGoal(): Flow<RestClientResult<ApiResponseWrapper<AbandonedScreenResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchMangeGoal()
        }
    }

    override suspend fun fetchEndGoalScreenResponse(): Flow<RestClientResult<ApiResponseWrapper<EndScreenResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchEndGoalScreenResponse()
        }
    }

    override suspend fun fetchHomefeedScreen(): Flow<RestClientResult<ApiResponseWrapper<HomefeedGoalProgressReponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchHomefeedScreen()
        }
    }

    override suspend fun fetchGBSSettings(): Flow<RestClientResult<ApiResponseWrapper<GBSSettingResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.fetchGBSSettings()
        }
    }

    override suspend fun endScreenViewed(goalId: String): Flow<RestClientResult<ApiResponseWrapper<EndScreenViewedResponse?>>> {
        return getFlowResult {
            goalBasedSavingDataSource.endScreenViewed(goalId)
        }
    }

    override suspend fun getGoalTransactionResponse(goalId: String): Flow<RestClientResult<ApiResponseWrapper<GoalStatusResponse>>> {
        return getFlowResult {
            goalBasedSavingDataSource.getGoalTransactionResponse(goalId)
        }
    }

    override suspend fun updateDailyGoalRecurringAmount(amount: Float): Flow<RestClientResult<ApiResponseWrapper<DailyInvestmentStatus?>>> {
        return getFlowResult {
            goalBasedSavingDataSource.updateDailyGoalRecurringAmount(amount)
        }
    }
}