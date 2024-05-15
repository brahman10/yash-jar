package com.jar.app.feature_gold_lease.shared.di

import com.jar.app.feature_gold_lease.shared.data.network.GoldLeaseDataSource
import com.jar.app.feature_gold_lease.shared.data.repository.GoldLeaseRepository
import com.jar.app.feature_gold_lease.shared.domain.repository.GoldLeaseRepositoryImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseFaqsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseGoldOptionsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerDetailsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseJewellerListingsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseLandingDetailsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseMyOrdersUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseOrderSummaryUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlanFiltersUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeasePlansUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRetryDataUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseRiskFactorUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseStatusUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseTermsAndConditionsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchGoldLeaseV2TransactionsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeaseDetailsUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.FetchUserLeasesUseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.InitiateGoldLeaseV2UseCase
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseFaqsUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseGoldOptionsUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseJewellerDetailsUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseJewellerListingsImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseLandingDetailsUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseMyOrdersUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseOrderSummaryUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeasePlanFiltersUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeasePlansUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseRetryDataUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseRiskFactorUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseStatusUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseTermsAndConditionsUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchGoldLeaseV2TransactionsImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchUserLeaseDetailsUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.FetchUserLeasesUseCaseImpl
import com.jar.app.feature_gold_lease.shared.domain.use_case.impl.InitiateGoldLeaseV2UseCaseImpl
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseUtil
import io.ktor.client.HttpClient

class CommonGoldLeaseModule(
    client: HttpClient
) {

    val goldLeaseDataSource: GoldLeaseDataSource by lazy {
        GoldLeaseDataSource(client)
    }

    val goldLeaseRepository: GoldLeaseRepository by lazy {
        GoldLeaseRepositoryImpl(goldLeaseDataSource)
    }

    val fetchGoldLeaseFaqsUseCase: FetchGoldLeaseFaqsUseCase by lazy {
        FetchGoldLeaseFaqsUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseTermsAndConditionsUseCase: FetchGoldLeaseTermsAndConditionsUseCase by lazy {
        FetchGoldLeaseTermsAndConditionsUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseRiskFactorUseCase: FetchGoldLeaseRiskFactorUseCase by lazy {
        FetchGoldLeaseRiskFactorUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseStatusUseCase: FetchGoldLeaseStatusUseCase by lazy {
        FetchGoldLeaseStatusUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeasePlanFiltersUseCase: FetchGoldLeasePlanFiltersUseCase by lazy {
        FetchGoldLeasePlanFiltersUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeasePlansUseCase: FetchGoldLeasePlansUseCase by lazy {
        FetchGoldLeasePlansUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseJewellerDetailsUseCase: FetchGoldLeaseJewellerDetailsUseCase by lazy {
        FetchGoldLeaseJewellerDetailsUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseLandingDetailsUseCase: FetchGoldLeaseLandingDetailsUseCase by lazy {
        FetchGoldLeaseLandingDetailsUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseJewellerListingsUseCase: FetchGoldLeaseJewellerListingsUseCase by lazy {
        FetchGoldLeaseJewellerListingsImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseGoldOptionsUseCase: FetchGoldLeaseGoldOptionsUseCase by lazy {
        FetchGoldLeaseGoldOptionsUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseOrderSummaryUseCase: FetchGoldLeaseOrderSummaryUseCase by lazy {
        FetchGoldLeaseOrderSummaryUseCaseImpl(goldLeaseRepository)
    }

    val initiateGoldLeaseV2UseCase: InitiateGoldLeaseV2UseCase by lazy {
        InitiateGoldLeaseV2UseCaseImpl(goldLeaseRepository)
    }

    val fetchUserLeasesUseCase: FetchUserLeasesUseCase by lazy {
        FetchUserLeasesUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseMyOrdersUseCase: FetchGoldLeaseMyOrdersUseCase by lazy {
        FetchGoldLeaseMyOrdersUseCaseImpl(goldLeaseRepository)
    }

    val fetchUserLeaseDetailsUseCase: FetchUserLeaseDetailsUseCase by lazy {
        FetchUserLeaseDetailsUseCaseImpl(goldLeaseRepository)
    }

    val fetchGoldLeaseV2TransactionsUseCase: FetchGoldLeaseV2TransactionsUseCase by lazy {
        FetchGoldLeaseV2TransactionsImpl(goldLeaseRepository)
    }

    val fFetchGoldLeaseRetryDataUseCase: FetchGoldLeaseRetryDataUseCase by lazy {
        FetchGoldLeaseRetryDataUseCaseImpl(goldLeaseRepository)
    }

    val goldLeaseUtil: GoldLeaseUtil by lazy {
        GoldLeaseUtil()
    }
}