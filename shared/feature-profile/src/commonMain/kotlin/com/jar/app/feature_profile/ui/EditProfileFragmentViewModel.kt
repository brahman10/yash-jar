package com.jar.app.feature_profile.ui

import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
//import com.jar.app.feature_lending_kyc.api.usecase.FetchKycProgressUseCase
import com.jar.app.feature_profile.domain.model.PrimaryUpi
import com.jar.app.feature_profile.domain.model.ProfileStaticData
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_user_api.domain.mappers.toUserSettings
import com.jar.app.feature_user_api.domain.model.Addresses
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.app.feature_user_api.domain.model.UserSettings
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditProfileFragmentViewModel constructor(
    private val getUserSavedAddressUseCase: GetUserSavedAddressUseCase,
    private val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
    private val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase,
    private val fetchUserSettingsUseCase: FetchUserSettingsUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _userKycStatusLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>()
    val userKycStatusLiveData: CFlow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>
        get() = _userKycStatusLiveData.toCommonFlow()

    private val _userLendingKycProgressLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>()
    val userLendingKycProgressLiveData: CFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>
        get() = _userLendingKycProgressLiveData.toCommonFlow()

    private val _savedAddressLiveData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<Addresses>>>(RestClientResult.none())
    val savedAddressLiveData: CStateFlow<RestClientResult<ApiResponseWrapper<Addresses>>>
        get() = _savedAddressLiveData.toCommonStateFlow()

    private val _primaryUpiIdLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ProfileStaticData>>>()
    val primaryUpiIdLiveData: CFlow<RestClientResult<ApiResponseWrapper<ProfileStaticData>>>
        get() = _primaryUpiIdLiveData.toCommonFlow()

    private val _fetchUserFlow = MutableSharedFlow<User?>()
    val fetchUserFlow: CFlow<User?>
        get() = _fetchUserFlow.toCommonFlow()

    private val _updateUserFlow = MutableSharedFlow<RestClientResult<ApiResponseWrapper<User?>>>()
    val updateUserFlow: CFlow<RestClientResult<ApiResponseWrapper<User?>>>
        get() = _updateUserFlow.toCommonFlow()

    private val _userSettingsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserSettings?>>>()
    val userSettingsFlow: CFlow<RestClientResult<ApiResponseWrapper<UserSettings?>>>
        get() = _userSettingsFlow.toCommonFlow()

    fun fetchUserKycStatus() {
        viewModelScope.launch {
            fetchUserKycStatusUseCase.fetchUserKycStatus().collect {
                _userKycStatusLiveData.emit(it)
            }
        }
    }

    // TODO uncomment after lending is moved
//    fun fetchUserLendingKycProgress() {
//        viewModelScope.launch {
//            fetchKycProgressUseCase.fetchKycProgress()
//                .mapToDTO {
//                    it?.toKycProgressResponse()
//                }
//                .collect {
//                    _userLendingKycProgressLiveData.emit(it)
//                }
//        }
//    }

    fun getSavedAddress() {
        viewModelScope.launch {
            getUserSavedAddressUseCase.getSavedAddress().collect {
                _savedAddressLiveData.emit(it)
            }
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            val userString = prefs.getUserString()
            if (userString.isNullOrBlank().not()) {
                val user = serializer.decodeFromString<User?>(userString!!)
                _fetchUserFlow.emit(user)
            }
        }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch {
            val userString = prefs.getUserString()
            if (userString.isNullOrBlank().not()) {
                val user = serializer.decodeFromString<User?>(userString!!)
                if (user != null) {
                    user.email = email
                    updateUserUseCase.updateUser(user).collectLatest {
                        _updateUserFlow.emit(it)
                    }
                }
            }
        }
    }

    fun fetchUserEmail() {
        viewModelScope.launch {
            fetchUserSettingsUseCase.fetchUserSettings()
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect {
                    _userSettingsFlow.emit(it)
                }
        }
    }

    fun fetchPrimaryUpiId() {
        viewModelScope.launch {
            fetchDashboardStaticContentUseCase.fetchDashboardStaticContent(BaseConstants.StaticContentType.PRIMARY_UPI_ID)
                .collect {
                    _primaryUpiIdLiveData.emit(it)
                }
        }
    }

    fun updateUserLocally(user: User) {
        viewModelScope.launch {
            prefs.setUserStringSync(serializer.encodeToString(user))
        }
    }

}