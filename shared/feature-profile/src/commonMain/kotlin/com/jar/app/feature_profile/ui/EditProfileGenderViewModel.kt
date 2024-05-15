package com.jar.app.feature_profile.ui

import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_profile.domain.model.GenderData
import com.jar.app.feature_profile.domain.model.GenderType
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


class EditProfileGenderViewModel constructor(
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    private val updateUserUseCase: UpdateUserUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _updateUserLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<User?>>>()
    val updateUserLiveData: CFlow<RestClientResult<ApiResponseWrapper<User?>>>
        get() = _updateUserLiveData.toCommonFlow()

    private val _genderListLiveData = MutableSharedFlow<List<GenderData>>()
    val genderListLiveData: CFlow<List<GenderData>> get() = _genderListLiveData.toCommonFlow()

    private val genderList = listOf(
        GenderData(
            MR.strings.feature_profile_male,
            GenderType.MALE
        ),
        GenderData(
            MR.strings.feature_profile_female,
            GenderType.FEMALE
        ),
        GenderData(
            MR.strings.feature_profile_other,
            GenderType.OTHER
        )
    )

    var currentlySelected: GenderData? = null

    fun updateGender(gender: String) {
        viewModelScope.launch {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User?>(it)
                if (user != null) {
                    user.gender = gender
                    updateUser(user)
                }
            }
        }
    }

    fun fetchGenderList() {
        viewModelScope.launch {
            _genderListLiveData.emit(genderList)
        }
    }

    fun updateUserGenderLocally(gender: String?) {
        viewModelScope.launch {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User>(it)
                user.gender = gender
                prefs.setUserStringSync(serializer.encodeToString(user))
            }
        }
    }

    private suspend fun updateUser(user: User) {
        updateUserUseCase.updateUser(user).collect {
            _updateUserLiveData.emit(it)
        }
    }

    fun preselect(gender: String): GenderData? {
        val genderType = GenderType.valueOf(gender)
        val genderData = genderList.find { it.genderType == genderType }
        updatedGenderSelection(genderData)
        return genderData
    }

    fun updatedGenderSelection(selectedGender: GenderData?) {
        selectedGender ?: return
        currentlySelected = selectedGender
        viewModelScope.launch(Dispatchers.Default) {
            _genderListLiveData.emit(
                genderList.map {
                    it.copy(isSelected = it.genderStringId == selectedGender.genderStringId)
                }
            )
        }
    }

}