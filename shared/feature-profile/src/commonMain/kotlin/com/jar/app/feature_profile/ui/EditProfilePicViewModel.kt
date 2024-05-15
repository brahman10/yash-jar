package com.jar.app.feature_profile.ui

import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_profile.domain.model.AvatarInfo
import com.jar.app.feature_profile.domain.model.GenderType
import com.jar.app.feature_profile.domain.use_case.FetchDashboardStaticContentUseCase
import com.jar.app.feature_user_api.domain.model.UserProfilePicture
import com.jar.app.feature_user_api.domain.use_case.UpdateUserProfilePicUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class EditProfilePicViewModel constructor(
    private val fetchDashboardStaticContentUseCase: FetchDashboardStaticContentUseCase,
    private val updateUserProfilePicUseCase: UpdateUserProfilePicUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _updatePhotoLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<UserProfilePicture>>>()
    val updatePhotoLiveData: CFlow<RestClientResult<ApiResponseWrapper<UserProfilePicture>>>
        get() = _updatePhotoLiveData.toCommonFlow()

    private val _profileListLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<List<AvatarInfo>>>>()
    val profileListLiveData: CFlow<RestClientResult<ApiResponseWrapper<List<AvatarInfo>>>>
        get() = _profileListLiveData.toCommonFlow()

    private val _userLiveData = MutableSharedFlow<RestClientResult<ApiResponseWrapper<User?>>>()
    val networkUserLiveData: CFlow<RestClientResult<ApiResponseWrapper<User?>>>
        get() = _userLiveData.toCommonFlow()

    fun fetchProfileList(gender: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val type = if (gender != null) GenderType.valueOf(gender) else GenderType.MALE
            fetchDashboardStaticContentUseCase.fetchDashboardStaticContent(BaseConstants.StaticContentType.AVATAR_INFO)
                .collect(
                    onLoading = {
                        _profileListLiveData.emit(RestClientResult.loading())
                    },
                    onSuccess = {
                        val avatarInfo = it.avatarInfo!!
                        val data = when (type) {
                            GenderType.MALE -> avatarInfo.maleAvatarInfo
                            GenderType.FEMALE -> avatarInfo.femaleAvatarInfo
                            GenderType.OTHER -> avatarInfo.otherAvatarInfo
                        }
                        _profileListLiveData.emit(
                            RestClientResult.success(
                                ApiResponseWrapper(
                                    data = data,
                                    true
                                )
                            )
                        )
                    },
                    onError = { errorMessage, errorCode ->
                        _profileListLiveData.emit(RestClientResult.error(errorMessage))
                    }
                )
        }
    }

    fun updateUserProfilePic(byteArray: ByteArray) {
        viewModelScope.launch(Dispatchers.Default) {
            updateUserProfilePicUseCase.updateUserProfilePhoto(byteArray).collect {
                _updatePhotoLiveData.emit(it)
            }
        }
    }

    fun updateUserImage(url: String) {
        viewModelScope.launch(Dispatchers.Default) {
            prefs.getUserString()?.let { userString ->
                val user = serializer.decodeFromString<User>(userString)
                updateUserUseCase.updateUser(
                    user.apply { profilePicUrl = url }
                ).collect {
                    it.data?.data?.let { updatedUser ->
                        prefs.setUserStringSync(serializer.encodeToString(updatedUser))
                    }
                    _userLiveData.emit(it)
                }
            }
        }
    }

    fun updateUserPicLocally(url: String?) {
        viewModelScope.launch {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User>(it)
                user.profilePicUrl = url
                prefs.setUserStringSync(serializer.encodeToString(user))
            }
        }
    }
}