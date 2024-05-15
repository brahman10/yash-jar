package com.jar.app.feature_profile.impl.ui.profile.pic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_profile.ui.EditProfilePicViewModel
import com.jar.app.feature_user_api.domain.use_case.UpdateUserProfilePicUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfilePicViewModelAndroid @Inject constructor(
    private val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase,
    private val updateUserProfilePicUseCase: UpdateUserProfilePicUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer
) : ViewModel() {

    private val viewModel by lazy {
        EditProfilePicViewModel(
            fetchDashboardStaticContentUseCase,
            updateUserProfilePicUseCase,
            updateUserUseCase,
            prefs,
            serializer,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}