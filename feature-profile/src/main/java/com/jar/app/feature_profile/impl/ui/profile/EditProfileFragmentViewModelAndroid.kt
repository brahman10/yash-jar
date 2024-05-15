package com.jar.app.feature_profile.impl.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_profile.ui.EditProfileFragmentViewModel
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.GetUserSavedAddressUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileFragmentViewModelAndroid @Inject constructor(
    private val getUserSavedAddressUseCase: GetUserSavedAddressUseCase,
    private val fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
    private val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase,
    private val fetchUserSettingsUseCase: FetchUserSettingsUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
) : ViewModel() {

    private val viewModel by lazy {
        EditProfileFragmentViewModel(
            getUserSavedAddressUseCase,
            fetchUserKycStatusUseCase,
            fetchDashboardStaticContentUseCase,
            fetchUserSettingsUseCase,
            updateUserUseCase,
            prefs,
            serializer,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}
