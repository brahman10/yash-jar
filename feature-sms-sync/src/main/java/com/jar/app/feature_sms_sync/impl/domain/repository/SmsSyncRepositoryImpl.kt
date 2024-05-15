package com.jar.app.feature_sms_sync.impl.domain.repository

import com.jar.app.feature_sms_sync.impl.data.network.SmsSyncDataSource
import com.jar.app.feature_sms_sync.impl.data.repository.ISmsSyncRepository
import com.jar.app.feature_sms_sync.impl.domain.model.SmsSyncRequest

internal class SmsSyncRepositoryImpl(
    private val dataSource: SmsSyncDataSource
) : ISmsSyncRepository {
    override suspend fun postSmsToServer(smsSyncRequest: SmsSyncRequest) = getFlowResult {
        dataSource.postData(smsSyncRequest)
    }
}