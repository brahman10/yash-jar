package com.jar.app.feature_profile.impl.ui.profile.email

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
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class EditEmailViewModel constructor(
    private val updateUserUseCase: UpdateUserUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    coroutineScope: CoroutineScope?
){
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _updateUserLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<User?>>>()
    val updateUserLiveData: CFlow<RestClientResult<ApiResponseWrapper<User?>>>
        get() = _updateUserLiveData.toCommonFlow()

    fun updateUserEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User>(it)
                user.email = email
                prefs.setUserStringSync(serializer.encodeToString(user))
                updateUserUseCase.updateUser(user).collect {
                    _updateUserLiveData.emit(it)
                }
            }
        }
    }

}