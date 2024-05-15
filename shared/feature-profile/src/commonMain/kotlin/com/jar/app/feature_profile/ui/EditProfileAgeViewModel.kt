package com.jar.app.feature_profile.impl.ui.profile.age


import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EditProfileAgeViewModel constructor(
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    private val updateUserUseCase: UpdateUserUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _ageListFlow = MutableStateFlow<List<Int>>(emptyList())
    val ageListFlow: CFlow<List<Int>>
        get() = _ageListFlow.toCommonFlow()

    private val _updateUserFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<User?>>>(RestClientResult.none())
    val updateUserFlow: CFlow<RestClientResult<ApiResponseWrapper<User?>>>
        get() = _updateUserFlow.toCommonFlow()

    var currentlySelected: Int? = null

    fun fetchAgeList() {
        viewModelScope.launch {
            _ageListFlow.emit((18..100).toList())
        }
    }

    fun updateAge(age: Int) {
        viewModelScope.launch {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User?>(it)
                if (user != null) {
                    user.age = age
                    updateUser(user)
                }
            }
        }
    }

    fun updateUserAgeLocally(age: Int?) {
        viewModelScope.launch {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User>(it)
                user.age = age
                prefs.setUserStringSync(serializer.encodeToString(user))
            }
        }
    }

    private suspend fun updateUser(user: User) {
        updateUserUseCase.updateUser(user).collect {
            _updateUserFlow.emit(it)
        }
    }
}