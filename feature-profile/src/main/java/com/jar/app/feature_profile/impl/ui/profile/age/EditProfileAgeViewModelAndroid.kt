package com.jar.app.feature_profile.impl.ui.profile.age

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EditProfileAgeViewModelAndroid @Inject constructor(
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        EditProfileAgeViewModel(
            prefs,
            serializer,
            updateUserUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

}