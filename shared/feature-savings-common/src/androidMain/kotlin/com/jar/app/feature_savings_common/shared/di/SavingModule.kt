package com.jar.app.feature_savings_common.shared.di

import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.feature_savings_common.shared.data.network.SavingsCommonDataSource
import com.jar.app.feature_savings_common.shared.data.repository.SavingsCommonRepository
import com.jar.app.feature_savings_common.shared.domain.use_case.*
import com.jar.app.feature_savings_common.shared.domain.use_case.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class SavingModule {

    @Provides
    @Singleton
    internal fun provideSavingsCommonModule(@AppHttpClient client: HttpClient): SavingsCommonModule {
        return SavingsCommonModule(client)
    }

    @Provides
    @Singleton
    internal fun provideSavingsCommonDataSource(savingsCommonModule: SavingsCommonModule): SavingsCommonDataSource {
        return savingsCommonModule.savingsCommonDataSource
    }

    @Provides
    @Singleton
    internal fun provideSavingsCommonRepository(savingsCommonModule: SavingsCommonModule): SavingsCommonRepository {
        return savingsCommonModule.savingsCommonRepository
    }

    @Provides
    @Singleton
    internal fun provideUserSavingsDetailsUseCase(savingsCommonModule: SavingsCommonModule): FetchUserSavingsDetailsUseCase {
        return savingsCommonModule.userSavingsDetailsUseCase
    }

    @Provides
    @Singleton
    internal fun provideDisableUserSavingsUseCase(savingsCommonModule: SavingsCommonModule): DisableUserSavingsUseCase {
        return savingsCommonModule.disableUserSavingsUseCase
    }

    @Provides
    @Singleton
    internal fun provideUpdateUserSavingUseCase(savingsCommonModule: SavingsCommonModule): UpdateUserSavingUseCase {
        return savingsCommonModule.updateUserSavingUseCase
    }

    @Provides
    @Singleton
    internal fun provideManageSavingPreferenceUseCase(savingsCommonModule: SavingsCommonModule): ManageSavingPreferenceUseCase {
        return savingsCommonModule.manageSavingPreferenceUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchSavingsSetupInfoUseCase(savingsCommonModule: SavingsCommonModule): FetchSavingsSetupInfoUseCase {
        return savingsCommonModule.fetchSavingsSetupInfoUseCase
    }

    @Provides
    @Singleton
    internal fun provideFetchGoalBasedSavingSettingUseCase(savingsCommonModule: SavingsCommonModule): FetchGoalBasedSavingSettingUseCase {
        return savingsCommonModule.fetchGoalBasedSavingSettingUseCase
    }

}