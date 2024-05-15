package com.jar.app.feature_profile.impl.ui.profile.number

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_profile.domain.use_case.RequestOtpUseCase
import com.jar.app.feature_profile.ui.EditProfileNumberViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserPhoneNumberUseCase
import com.jar.app.feature_user_api.domain.use_case.VerifyNumberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EditProfileNumberViewModelAndroid @Inject constructor(
    private val updatePhoneNumberUseCase: UpdateUserPhoneNumberUseCase,
    private val verifyNumberUseCase: VerifyNumberUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val deviceUtils: DeviceUtils,
) : ViewModel() {

    private val viewModel by lazy {
        EditProfileNumberViewModel(
            updatePhoneNumberUseCase,
            verifyNumberUseCase,
            requestOtpUseCase,
            deviceUtils,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}