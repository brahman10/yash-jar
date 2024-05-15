package com.jar.app.feature_kyc.shared.di

import com.jar.app.feature_kyc.shared.api.use_case.PostFaceMatchRequestUseCase
import com.jar.app.feature_kyc.shared.api.use_case.PostKycOcrRequestUseCase
import com.jar.app.feature_kyc.shared.data.network.KycDataSource
import com.jar.app.feature_kyc.shared.data.repository.KycRepository
import com.jar.app.feature_kyc.shared.domain.repository.KycRepositoryImpl
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDetailsUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDocumentsListUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycFaqUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.PostPanOcrRequestUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.impl.FetchKycDetailsUseCaseImpl
import com.jar.app.feature_kyc.shared.domain.use_case.impl.FetchKycDocumentsListUseCaseImpl
import com.jar.app.feature_kyc.shared.domain.use_case.impl.FetchKycFaqUseCaseImpl
import com.jar.app.feature_kyc.shared.domain.use_case.impl.PostFaceMatchRequestUseCaseImpl
import com.jar.app.feature_kyc.shared.domain.use_case.impl.PostKycOcrRequestUseCaseImpl
import com.jar.app.feature_kyc.shared.domain.use_case.impl.PostManualKycRequestUseCaseImpl
import com.jar.app.feature_kyc.shared.domain.use_case.impl.PostPanOcrRequestUseCaseImpl
import io.ktor.client.HttpClient

class CommonKycModule(client: HttpClient) {

    val kycDataSource: KycDataSource by lazy {
        KycDataSource(client)
    }

    val kycRepository: KycRepository by lazy {
        KycRepositoryImpl(kycDataSource)
    }

    val provideFetchKycDetailsUseCase: FetchKycDetailsUseCase by lazy {
        FetchKycDetailsUseCaseImpl(kycRepository)
    }

    val provideFetchKycFaqUseCase: FetchKycFaqUseCase by lazy {
        FetchKycFaqUseCaseImpl(kycRepository)
    }

    val providePostManualKycRequestUseCase: PostManualKycRequestUseCase by lazy {
        PostManualKycRequestUseCaseImpl(kycRepository)
    }

    val provideFetchKycDocumentsListUseCase: FetchKycDocumentsListUseCase by lazy {
        FetchKycDocumentsListUseCaseImpl(kycRepository)
    }

    val providePostKycOcrRequestUseCase: PostKycOcrRequestUseCase by lazy {
        PostKycOcrRequestUseCaseImpl(kycRepository)
    }

    val providePostFaceMatchRequestUseCase: PostFaceMatchRequestUseCase by lazy {
        PostFaceMatchRequestUseCaseImpl(kycRepository)
    }

    val providePostPanOcrRequestUseCase: PostPanOcrRequestUseCase by lazy {
        PostPanOcrRequestUseCaseImpl(kycRepository)
    }
}