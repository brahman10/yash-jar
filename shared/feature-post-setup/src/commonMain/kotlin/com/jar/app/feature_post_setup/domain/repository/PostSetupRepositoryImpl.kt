package com.jar.app.feature_post_setup.domain.repository

import com.jar.app.feature_post_setup.data.network.PostSetupDataSource
import com.jar.app.feature_post_setup.data.repository.PostSetupRepository

internal class PostSetupRepositoryImpl constructor(
    private val postSetupDataSource: PostSetupDataSource
) : PostSetupRepository {

    override suspend fun fetchPostSetupUserData() = getFlowResult {
        postSetupDataSource.fetchPostSetupUserData()
    }

    override suspend fun fetchPostSetupCalendarData(
        startDate: String,
        endDate: String
    ) = getFlowResult {
        postSetupDataSource.fetchPostSetupCalendarData(startDate, endDate)
    }

    override suspend fun fetchPostSetupQuickActions() = getFlowResult {
        postSetupDataSource.fetchPostSetupQuickActions()
    }

    override suspend fun fetchPostSetupSavingOperations() = getFlowResult {
        postSetupDataSource.fetchPostSetupSavingOperations()
    }

    override suspend fun fetchPostSetupFaq() = getFlowResult {
        postSetupDataSource.fetchPostSetupFaq()
    }

    override suspend fun fetchPostSetupFailureInfo() = getFlowResult {
        postSetupDataSource.fetchPostSetupFailureInfo()
    }

    override suspend fun initiatePaymentForFailedTransactions(
        amount: Float,
        paymentProvider: String,
        type: String,
        roundOffsLinked: List<String>
    ) = getFlowResult {
        postSetupDataSource.initiatePaymentForFailedTransactions(
            amount, paymentProvider, type, roundOffsLinked
        )
    }
}