package com.jar.app.feature_user_api.di

import android.content.Context
import com.jar.app.core_network.di.qualifier.AppHttpClient
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_user_api.data.db.DatabaseDriverFactory
import com.jar.app.feature_user_api.data.db.UserMetaLocalDataSource
import com.jar.app.feature_user_api.data.network.UserDataSource
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.di.qualifiers.UserMetaDataDBDriver
import com.jar.app.feature_user_api.domain.network.UserRepositoryImpl
import com.jar.app.feature_user_api.domain.use_case.*
import com.jar.app.feature_user_api.domain.use_case.impl.*
import com.jar.app.feature_user_api.shared.UserMetaDatabase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class UserDataSourceModule {

    @Provides
    @Singleton
    @UserMetaDataDBDriver
    internal fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver {
        return DatabaseDriverFactory(context).createDriver()
    }

    @Provides
    @Singleton
    internal fun provideUserMetaDatabase(@UserMetaDataDBDriver driver: SqlDriver): UserMetaDatabase {
        return UserMetaDatabase(driver)
    }

    @Provides
    @Singleton
    internal fun provideHomeFeedLocalDataSource(userMetaDatabase: UserMetaDatabase): UserMetaLocalDataSource {
        return UserMetaLocalDataSource(userMetaDatabase)
    }

    @Provides
    @Singleton
    internal fun provideUserDataSource(@AppHttpClient httpClient: HttpClient): UserDataSource {
        return UserDataSource(client = httpClient)
    }

    @Provides
    @Singleton
    internal fun provideUserRepository(userDataSource: UserDataSource, prefsApi: PrefsApi, serializer: Serializer): UserRepository {
        return UserRepositoryImpl(userDataSource, serializer, prefsApi)
    }

    @Provides
    @Singleton
    internal fun provideUpdatePauseSavingUseCase(userRepository: UserRepository): UpdatePauseSavingUseCase {
        return UpdatePauseSavingUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUserSettingsUseCase(userRepository: UserRepository): FetchUserSettingsUseCase {
        return FetchUserSettingsUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateUserSettingsUseCase(userRepository: UserRepository): UpdateUserSettingsUseCase {
        return UpdateUserSettingsUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchManualPaymentInfoUseCase(userRepository: UserRepository): FetchDetectedSpendInfoUseCase {
        return FetchDetectedSpendInfoUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateUserUseCase(userRepository: UserRepository): UpdateUserUseCase {
        return UpdateUserUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateUserProfilePicUseCase(userRepository: UserRepository): UpdateUserProfilePicUseCase {
        return UpdateUserProfilePicUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideUpdateUserPhoneNumberUseCase(userRepository: UserRepository): UpdateUserPhoneNumberUseCase {
        return UpdateUserPhoneNumberUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideVerifyNumberUseCase(userRepository: UserRepository): VerifyNumberUseCase {
        return VerifyNumberUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideValidateAddressPinCodeUseCase(userRepository: UserRepository): ValidateAddressPinCodeUseCase {
        return ValidateAddressPinCodeUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideDeleteUserAddressUseCase(userRepository: UserRepository): DeleteUserAddressUseCase {
        return DeleteUserAddressUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideGetUserSavedAddressUseCase(userRepository: UserRepository): GetUserSavedAddressUseCase {
        return GetUserSavedAddressUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideAddDeliveryAddressUseCase(userRepository: UserRepository): AddUserAddressUseCase {
        return AddUserAddressUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideEditAddressUseCase(userRepository: UserRepository): EditUserAddressUseCase {
        return EditUserAddressUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUserVpaUseCase(userRepository: UserRepository): FetchUserVpaUseCase {
        return FetchUserVpaUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideDeleteUserVpaUseCase(userRepository: UserRepository): DeleteUserVpaUseCase {
        return DeleteUserVpaUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideAddNewUserVpaUseCase(userRepository: UserRepository): AddNewUserVpaUseCase {
        return AddNewUserVpaUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUserGoldBalanceUseCase(userRepository: UserRepository): FetchUserGoldBalanceUseCase {
        return FetchUserGoldBalanceUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUserKycStatusUseCase(userRepository: UserRepository): FetchUserKycStatusUseCase {
        return FetchUserKycStatusUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchUserWinningsUseCase(userRepository: UserRepository): FetchUserWinningsUseCase {
        return FetchUserWinningsUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideIsAutoInvestResetRequiredUseCase(userRepository: UserRepository): IsAutoInvestResetRequiredUseCase {
        return IsAutoInvestResetRequiredUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchGoldSipDetailsUseCase(userRepository: UserRepository): FetchGoldSipDetailsUseCase {
        return FetchGoldSipDetailsUseCaseImpl(userRepository)
    }

    @Provides
    @Singleton
    internal fun provideFetchCreditCardPromoStatusUseCase(
        userRepository: UserRepository
    ): FetchUserMetaUseCase {
        return FetchUserMetaUseCaseImpl(userRepository)
    }
}