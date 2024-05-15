package com.jar.app.feature_sell_gold.shared.di

import com.jar.app.feature_sell_gold.shared.data.network.WithdrawalDataSource
import com.jar.app.feature_sell_gold.shared.data.repository.WithdrawalRepositoryImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchDrawerDetailsUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.FetchKycDetailsForSellGoldUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchGoldSellOptionUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchSellGoldStaticContentUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalBottomSheetDataUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IFetchWithdrawalStatusUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostTransactionActionUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IPostWithdrawRequestUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.IUpdateWithdrawalReasonUseCase
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchDrawerDetailsUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchGoldSellOptionUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchKycDetailsForSellGoldUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchSellGoldStaticContentUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchWithdrawalBottomSheetDataImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.FetchWithdrawalStatusUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.PostTransactionActionUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.PostWithdrawRequestUseCaseImpl
import com.jar.app.feature_sell_gold.shared.domain.use_cases.impl.UpdateWithdrawalReasonUseCaseImpl
import io.ktor.client.HttpClient

class WithdrawalModule(client: HttpClient) {

    private val withdrawalDataSource by lazy {
        WithdrawalDataSource(client)
    }

    private val withdrawalRepository by lazy {
        WithdrawalRepositoryImpl(withdrawalDataSource)
    }

    val postWithdrawalRequestUseCase: IPostWithdrawRequestUseCase by lazy {
        PostWithdrawRequestUseCaseImpl(withdrawalRepository)
    }

    val fetchGoldSellOptionsUseCase: IFetchGoldSellOptionUseCase by lazy {
        FetchGoldSellOptionUseCaseImpl(withdrawalRepository)
    }

    val staticDataUseCase: IFetchSellGoldStaticContentUseCase by lazy {
        FetchSellGoldStaticContentUseCaseImpl(withdrawalRepository)
    }

    val fetchWithdrawalStatusUseCase: IFetchWithdrawalStatusUseCase by lazy {
        FetchWithdrawalStatusUseCaseImpl(withdrawalRepository)
    }

    val updateWithdrawalReasonUseCase: IUpdateWithdrawalReasonUseCase by lazy {
        UpdateWithdrawalReasonUseCaseImpl(withdrawalRepository)
    }

    val postTransactionActionUseCase: IPostTransactionActionUseCase by lazy {
        PostTransactionActionUseCaseImpl(withdrawalRepository)
    }

    val fetchWithdrawalBottomSheetDataUseCase: IFetchWithdrawalBottomSheetDataUseCase by lazy {
        FetchWithdrawalBottomSheetDataImpl(withdrawalRepository)
    }

    val fetchDrawerDetailsUseCase: FetchDrawerDetailsUseCase by lazy {
        FetchDrawerDetailsUseCaseImpl(withdrawalRepository)
    }

    val fetchKycDetailsForSellGoldUseCase: FetchKycDetailsForSellGoldUseCase by lazy {
        FetchKycDetailsForSellGoldUseCaseImpl(withdrawalRepository)
    }
}