package com.jar.app.feature_user_api.di

import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_user_api.data.network.UserDataSource
import com.jar.app.feature_user_api.data.network.UserRepository
import com.jar.app.feature_user_api.domain.network.UserRepositoryImpl
import com.jar.app.feature_user_api.domain.use_case.AddNewUserVpaUseCase
import com.jar.app.feature_user_api.domain.use_case.AddUserAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.DeleteUserAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.DeleteUserVpaUseCase
import com.jar.app.feature_user_api.domain.use_case.EditUserAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchDetectedSpendInfoUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchGoldSipDetailsUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserMetaUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserVpaUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserWinningsUseCase
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.IsAutoInvestResetRequiredUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdatePauseSavingUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserPhoneNumberUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserProfilePicUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.app.feature_user_api.domain.use_case.ValidateAddressPinCodeUseCase
import com.jar.app.feature_user_api.domain.use_case.VerifyNumberUseCase
import com.jar.app.feature_user_api.domain.use_case.impl.AddNewUserVpaUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.AddUserAddressUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.DeleteUserAddressUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.DeleteUserVpaUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.EditUserAddressUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchDetectedSpendInfoUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchGoldSipDetailsUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchUserGoldBalanceUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchUserKycStatusUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchUserMetaUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchUserSettingsUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchUserVpaUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.FetchUserWinningsUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.GetUserSavedAddressUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.IsAutoInvestResetRequiredUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.UpdatePauseSavingUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.UpdateUserPhoneNumberUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.UpdateUserProfilePicUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.UpdateUserSettingsUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.UpdateUserUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.ValidateAddressPinCodeUseCaseImpl
import com.jar.app.feature_user_api.domain.use_case.impl.VerifyNumberUseCaseImpl
import com.jar.internal.library.jar_core_network.api.util.Serializer
import io.ktor.client.HttpClient

class UserDataSourceModule(
    httpClient: HttpClient,
    prefsApi: PrefsApi,
    serializer: Serializer
) {

    private val userDataSource by lazy {
        UserDataSource(client = httpClient)
    }

    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userDataSource,
            serializer,
            prefsApi
        )
    }

    val updatePauseSavingUseCase: UpdatePauseSavingUseCase by lazy {
        UpdatePauseSavingUseCaseImpl(userRepository)
    }

    val fetchUserSettingsUseCase: FetchUserSettingsUseCase by lazy {
        FetchUserSettingsUseCaseImpl(userRepository)
    }

    val updateUserSettingsUseCase: UpdateUserSettingsUseCase by lazy {
        UpdateUserSettingsUseCaseImpl(userRepository)
    }

    val fetchDetectedSpendInfoUseCase: FetchDetectedSpendInfoUseCase by lazy {
        FetchDetectedSpendInfoUseCaseImpl(userRepository)
    }

    val updateUserUseCase: UpdateUserUseCase by lazy {
        UpdateUserUseCaseImpl(userRepository)
    }

    val updateUserProfilePicUseCase: UpdateUserProfilePicUseCase by lazy {
        UpdateUserProfilePicUseCaseImpl(userRepository)
    }

    val updateUserPhoneNumberUseCase: UpdateUserPhoneNumberUseCase by lazy {
        UpdateUserPhoneNumberUseCaseImpl(userRepository)
    }

    val verifyNumberUseCase: VerifyNumberUseCase by lazy {
        VerifyNumberUseCaseImpl(userRepository)
    }

    val validateAddressPinCodeUseCase: ValidateAddressPinCodeUseCase by lazy {
        ValidateAddressPinCodeUseCaseImpl(userRepository)
    }

    val deleteUserAddressUseCase: DeleteUserAddressUseCase by lazy {
        DeleteUserAddressUseCaseImpl(userRepository)
    }

    val getUserSavedAddressUseCase: GetUserSavedAddressUseCase by lazy {
        GetUserSavedAddressUseCaseImpl(userRepository)
    }

    val addUserAddressUseCase: AddUserAddressUseCase by lazy {
        AddUserAddressUseCaseImpl(userRepository)
    }

    val editUserAddressUseCase: EditUserAddressUseCase by lazy {
        EditUserAddressUseCaseImpl(userRepository)
    }

    val fetchUserVpaUseCase: FetchUserVpaUseCase by lazy {
        FetchUserVpaUseCaseImpl(userRepository)
    }

    val deleteUserVpaUseCase: DeleteUserVpaUseCase by lazy {
        DeleteUserVpaUseCaseImpl(userRepository)
    }

    val addNewUserVpaUseCase: AddNewUserVpaUseCase by lazy {
        AddNewUserVpaUseCaseImpl(userRepository)
    }

    val fetchUserGoldBalanceUseCase: FetchUserGoldBalanceUseCase by lazy {
        FetchUserGoldBalanceUseCaseImpl(userRepository)
    }

    val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase by lazy {
        FetchUserKycStatusUseCaseImpl(userRepository)
    }

    val fetchUserWinningsUseCase: FetchUserWinningsUseCase by lazy {
        FetchUserWinningsUseCaseImpl(userRepository)
    }

    val isAutoInvestResetRequiredUseCase: IsAutoInvestResetRequiredUseCase by lazy {
        IsAutoInvestResetRequiredUseCaseImpl(userRepository)
    }

    val fetchGoldSipDetailsUseCase: FetchGoldSipDetailsUseCase by lazy {
        FetchGoldSipDetailsUseCaseImpl(userRepository)
    }

    val fetchUserMetaUseCase: FetchUserMetaUseCase by lazy {
        FetchUserMetaUseCaseImpl(userRepository)
    }
}