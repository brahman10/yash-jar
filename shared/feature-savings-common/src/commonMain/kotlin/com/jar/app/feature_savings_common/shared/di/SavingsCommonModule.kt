package com.jar.app.feature_savings_common.shared.di

import com.jar.app.feature_savings_common.shared.data.network.SavingsCommonDataSource
import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.repository.SavingsCommonRepositoryImpl
import com.jar.app.feature_savings_common.shared.domain.use_case.DisableUserSavingsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchGoalBasedSavingSettingUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchSavingsSetupInfoUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.FetchUserSavingsDetailsUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.ManageSavingPreferenceUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.UpdateUserSavingUseCase
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.DisableUserSavingsUseCaseImpl
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.FetchGoalBasedSavingSettingUseCaseImpl
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.FetchSavingsSetupInfoUseCaseImpl
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.FetchUserSavingsDetailsUseCaseImpl
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.ManageSavingPreferenceUseCaseImpl
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.UpdateUserSavingUseCaseImpl
import io.ktor.client.HttpClient

class SavingsCommonModule(
    client: HttpClient
) {

    val savingsCommonDataSource by lazy {
        SavingsCommonDataSource(client)
    }

    val savingsCommonRepository: SavingsCommonRepository by lazy {
        SavingsCommonRepositoryImpl(savingsCommonDataSource)
    }

    val userSavingsDetailsUseCase: FetchUserSavingsDetailsUseCase by lazy {
        FetchUserSavingsDetailsUseCaseImpl(savingsCommonRepository)
    }

    val disableUserSavingsUseCase: DisableUserSavingsUseCase by lazy {
        DisableUserSavingsUseCaseImpl(savingsCommonRepository)
    }

    val updateUserSavingUseCase: UpdateUserSavingUseCase by lazy {
        UpdateUserSavingUseCaseImpl(savingsCommonRepository)
    }

    val manageSavingPreferenceUseCase: ManageSavingPreferenceUseCase by lazy {
        ManageSavingPreferenceUseCaseImpl(savingsCommonRepository)
    }

    val fetchSavingsSetupInfoUseCase: FetchSavingsSetupInfoUseCase by lazy {
        FetchSavingsSetupInfoUseCaseImpl(savingsCommonRepository)
    }

    val fetchGoalBasedSavingSettingUseCase: FetchGoalBasedSavingSettingUseCase by lazy {
        FetchGoalBasedSavingSettingUseCaseImpl(savingsCommonRepository)
    }

}