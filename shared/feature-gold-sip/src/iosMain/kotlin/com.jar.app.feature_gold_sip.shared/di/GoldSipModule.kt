package com.jar.app.feature_gold_sip.shared.di

import com.jar.app.feature_gold_sip.shared.data.network.GoldSipDataSource
import com.jar.app.feature_gold_sip.shared.data.repository.GoldSipRepository
import com.jar.app.feature_gold_sip.shared.domain.repository.GoldSipRepositoryImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.DisableGoldSipUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipIntroUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchGoldSipTypeSetupInfoUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.FetchIsEligibleForGoldSipUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.UpdateGoldSipDetailsUseCase
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.DisableGoldSipUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.FetchGoldSipIntroUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.FetchGoldSipTypeSetupInfoUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.FetchIsEligibleForGoldSipUseCaseImpl
import com.jar.app.feature_gold_sip.shared.domain.use_case.impl.UpdateGoldSipDetailsUseCaseImpl
import io.ktor.client.HttpClient

class GoldSipModule(client: HttpClient) {


    private val goldSipDataSource by lazy {
        GoldSipDataSource(client)
    }
    private val goldSipRepository: GoldSipRepository by lazy {
        GoldSipRepositoryImpl(goldSipDataSource)
    }

    val disableGoldSipUseCase: DisableGoldSipUseCase by lazy {
        DisableGoldSipUseCaseImpl(goldSipRepository)
    }

    val fetchGoldSipIntroUseCase: FetchGoldSipIntroUseCase by lazy {
        FetchGoldSipIntroUseCaseImpl(goldSipRepository)
    }

    val fetchGoldSipTypeSetupInfoUseCase: FetchGoldSipTypeSetupInfoUseCase by lazy {
        FetchGoldSipTypeSetupInfoUseCaseImpl(goldSipRepository)
    }

    private val fetchIsEligibleForGoldSipUseCase: FetchIsEligibleForGoldSipUseCase by lazy {
        FetchIsEligibleForGoldSipUseCaseImpl(goldSipRepository)
    }

    val updateGoldSipDetailsUseCase: UpdateGoldSipDetailsUseCase by lazy {
        UpdateGoldSipDetailsUseCaseImpl(goldSipRepository)
    }
}