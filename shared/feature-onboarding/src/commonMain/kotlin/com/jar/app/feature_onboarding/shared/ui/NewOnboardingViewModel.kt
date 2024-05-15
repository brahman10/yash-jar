package com.jar.app.feature_onboarding.shared.ui

import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.domain.model.UserResponseData
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.domain.model.GetPhoneRequest
import com.jar.app.feature_onboarding.shared.domain.model.Language
import com.jar.app.feature_onboarding.shared.domain.model.PhoneNumberResponse
import com.jar.app.feature_onboarding.shared.domain.usecase.FetchSupportedLanguagesUseCase
import com.jar.app.feature_onboarding.shared.domain.usecase.IGetPhoneByDeviceUseCase
import com.jar.app.feature_onboarding.shared.util.OnboardingConstants
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class NewOnboardingViewModel constructor(
    private val updateUserUseCase: UpdateUserUseCase,
    private val getPhoneByDeviceUseCase: IGetPhoneByDeviceUseCase,
    private val fetchSupportedLanguagesUseCase: FetchSupportedLanguagesUseCase,
    private val weeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val prefs: PrefsApi,
    private val serializer: Serializer,
    private val deviceUtils: DeviceUtils,
    private val analyticsHandler: AnalyticsApi,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _storyTouchEventFlow = MutableStateFlow<Int?>(null)
    val storyTouchEventFlow: CFlow<Int?>
        get() = _storyTouchEventFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    val timeSpentMap = mutableMapOf<String, Long>()

    var numberOfOnBoardingScreens: Int? = null

    var totalTimeSpentOnBoarding: Long? = null

    var responseCount = 0

    private val _languageFlow = MutableStateFlow<Language?>(null)
    val languageFlow: CFlow<Language?>
        get() = _languageFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private val _phoneNumberFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<PhoneNumberResponse>>>(RestClientResult.none())
    val phoneNumberFlow: CFlow<RestClientResult<ApiResponseWrapper<PhoneNumberResponse>>>
        get() = _phoneNumberFlow.shareIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), 0
        ).toCommonFlow()

    private fun updateUser(user: User) {
        GlobalScope.launch {
            updateUserUseCase.updateUser(user).collect(onSuccess = {
                it?.let {
                    saveUserLocally(it)
                }
            })
        }
    }

    fun updateWeeklyChallengeData() {
        viewModelScope.launch {
            weeklyChallengeMetaDataUseCase.fetchWeeklyChallengeMetaData(false)
                .collectUnwrapped(onSuccess = {
                    it.data?.let {
                        prefs.setWonMysteryCardCount(it.cardsWon.orZero())
                        prefs.setWonMysteryCardChallengeId(it.challengeId ?: "")
                    }
                })
        }
    }

    private fun saveUserLocally(user: User) {
        GlobalScope.launch {
            prefs.setUserStringSync(serializer.encodeToString(user))
            val fullName = user.getFullName()
            if (fullName.isNullOrBlank().not())
                prefs.setUserName(fullName!!)
        }
    }

    fun saveUserData(userResponseData: UserResponseData) {
        GlobalScope.launch {
            analyticsHandler.postEvent("SETTING_USER_IDENTITY", userResponseData.user.userId.orEmpty())
            prefs.setRefreshToken(userResponseData.refreshToken)
            prefs.setAccessToken(userResponseData.accessToken)
            saveUserLocally(userResponseData.user)
            setUserForAnalytics(userResponseData.user)
        }
    }

    fun updateName(
        firstName: String, lastName: String?
    ) {
        viewModelScope.launch {
            prefs.getUserString()?.let {
                val user = serializer.decodeFromString<User?>(it)
                if (user != null) {

                    user.firstName = firstName
                    user.lastName =
                        lastName
                    updateUser(user)
                }
            }
        }
    }

    fun getPhoneNumberByDeviceId() {
        viewModelScope.launch {
            getPhoneByDeviceUseCase.getPhoneByDevice(getPhoneNumberRequest()).collect {
                _phoneNumberFlow.emit(it)
            }
        }
    }

    private suspend fun getPhoneNumberRequest(): GetPhoneRequest {
        return GetPhoneRequest(
            deviceId = deviceUtils.getDeviceId(),
            advertisingId = deviceUtils.getAdvertisingId().orEmpty()
        )
    }

    fun getLanguageForCode(code: String) {
        viewModelScope.launch {
            fetchSupportedLanguagesUseCase.fetchSupportedLanguages().collect {
                if (it.status == RestClientResult.Status.SUCCESS) {
                    val language = it.data?.data?.languages?.find { it.code == code }
                    _languageFlow.emit(language)
                }
            }
        }
    }

    private fun setUserForAnalytics(user: User?) {
        user?.let {
            val profileUpdate = HashMap<String, Any>()
            profileUpdate[OnboardingConstants.AnalyticsKeys.CLEVERTAP_NAME] =
                it.firstName.toString().plus(it.lastName.toString())
            profileUpdate[OnboardingConstants.AnalyticsKeys.CLEVERTAP_IDENTITY] =
                it.userId.orEmpty()
            profileUpdate[OnboardingConstants.AnalyticsKeys.CLEVERTAP_GENDER] = it.gender.orEmpty()
            profileUpdate[OnboardingConstants.AnalyticsKeys.CLEVERTAP_PHONE] = it.phoneNumber
            profileUpdate[OnboardingConstants.AnalyticsKeys.CLEVERTAP_MOBILE] =
                it.getPhoneNumberWithoutPlus()
            analyticsHandler.onUserLogin(it.userId.orEmpty(), profileUpdate)
        }
    }

    fun updateScreenTime(
        screenName: OnboardingStateMachine.State, timeSpentOnScreen: Long
    ) {
        val screen = screenName.toString()
        if (timeSpentMap.containsKey(screen)) {
            val timeAlreadySpent = timeSpentMap[screen].orZero()
            val totalTime = timeAlreadySpent + timeSpentOnScreen
            timeSpentMap[screen] = totalTime
        } else {
            timeSpentMap[screen] = timeSpentOnScreen
            numberOfOnBoardingScreens = numberOfOnBoardingScreens.orZero() + 1
        }
    }

    fun updateStoryTouchEvent(motionEventAction: Int) {
        viewModelScope.launch {
            _storyTouchEventFlow.emit(motionEventAction)
        }
    }
}