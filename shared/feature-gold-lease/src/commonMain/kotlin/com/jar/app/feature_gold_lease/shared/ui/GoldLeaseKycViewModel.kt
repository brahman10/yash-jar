package com.jar.app.feature_gold_lease.shared.ui

import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.domain.model.User
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.domain.mapper.toKycProgressResponse
import com.jar.app.core_base.util.isValidEmail
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.app.feature_kyc.shared.domain.model.KycContext
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDetailsUseCase
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class GoldLeaseKycViewModel constructor(
    private val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
    private val fetchKycProgressUseCase: FetchKycProgressUseCase,
    private val fetchKycDetailsUseCase: FetchKycDetailsUseCase,
    private val postManualKycRequestUseCase: PostManualKycRequestUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    coroutineScope: CoroutineScope?
){

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _userKycStatusFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>()
    val userKycStatusFlow: CFlow<RestClientResult<ApiResponseWrapper<UserKycStatus?>>>
        get() = _userKycStatusFlow.toCommonFlow()

    private val _userLendingKycProgressFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>()
    val userLendingKycProgressFlow: CFlow<RestClientResult<ApiResponseWrapper<KycProgressResponse?>>>
        get() = _userLendingKycProgressFlow.toCommonFlow()

    private val _kycDetailsFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>()
    val kycDetailsFlow: CFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _kycDetailsFlow.toCommonFlow()

    private val _manualKycRequestFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>()
    val manualKycRequestFlow: CFlow<RestClientResult<ApiResponseWrapper<KYCStatusDetails?>>>
        get() = _manualKycRequestFlow.toCommonFlow()

    private val _updateUserFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<User?>>>()
    val updateUserFlow: CFlow<RestClientResult<ApiResponseWrapper<User?>>>
        get() = _updateUserFlow.toCommonFlow()

    var manualKycRequest: ManualKycRequest? = null

    var emailPreFillType = ""
    var panPreFillType = ""

    var userLendingEmail: String? = null



    private fun updateUserEmailLocally(email: String) {
        viewModelScope.launch {
            val userString = prefs.getUserStringSync()
            if (userString.isNullOrBlank().not()) {
                val user = serializer.decodeFromString<User?>(userString!!)
                if (user != null) {
                    user.email = email
                    prefs.setUserStringSync(serializer.encodeToString(user))
                }
            }
        }
    }

    fun postManualKycRequest(manualKycRequest: ManualKycRequest) {
        viewModelScope.launch {
            manualKycRequest.emailId?.takeIf { it.isNotEmpty() && it.isValidEmail }?.let { email ->
                updateUserEmailLocally(email)
            }
            postManualKycRequestUseCase.postManualKycRequest(
                manualKycRequest,
                kycContext = KycContext.GOLD_LEASE.name
            ).collect {
                _manualKycRequestFlow.emit(it)
            }
        }
    }

    fun fetchKycDetails() {
        viewModelScope.launch {
            fetchKycDetailsUseCase.fetchKycDetails(kycContext = KycContext.GOLD_LEASE.name)
                .collect {
                    _kycDetailsFlow.emit(it)
                }
        }
    }

    fun fetchUserLendingKycProgress() {
        viewModelScope.launch {
            fetchKycProgressUseCase.fetchKycProgress()
                .mapToDTO {
                    it?.toKycProgressResponse()
                }
                .collect {
                    userLendingEmail = it.data?.data?.kycProgress?.EMAIL?.email
                    _userLendingKycProgressFlow.emit(it)
                }
        }
    }

    fun fetchUserKycStatus() {
        viewModelScope.launch {
            fetchUserKycStatusUseCase.fetchUserKycStatus(kycContext = KycContext.GOLD_LEASE.name).collect {
                _userKycStatusFlow.emit(it)
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
                    updateUserUseCase.updateUser(user).collect { res ->
                        _updateUserFlow.emit(res)
                    }
                }
            }
        }
    }

}