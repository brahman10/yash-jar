package com.jar.app.feature_settings.impl.ui.notification

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.feature_settings.domain.model.NotificationSettingsSwitch
import com.jar.app.feature_settings.BuildConfig
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_user_api.data.dto.UserSettingsDTO
import com.jar.app.feature_user_api.domain.mappers.toUserSettings
import com.jar.app.feature_user_api.domain.model.UserSettings
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserSettingsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject
import com.jar.internal.library.jar_core_network.api.model.RestClientResult as LibraryRestClientResult
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper as LibraryApiResponseWrapper

@HiltViewModel
internal class NotificationSettingsViewModel @Inject constructor(
    private val notificationSettingsGenerator: NotificationSettingsGenerator,
    private val fetchUserSettingsUseCase: FetchUserSettingsUseCase,
    private val updateUserSettingsUseCase: UpdateUserSettingsUseCase,
    private val analyticsHandler: AnalyticsApi
) : ViewModel() {

    private val _settingListLiveData =
        MutableLiveData<LibraryRestClientResult<List<NotificationSettingsSwitch>>>()
    val settingListLiveData: LiveData<LibraryRestClientResult<List<NotificationSettingsSwitch>>>
        get() = _settingListLiveData

    private val _updateUserSettingsLiveData =
        MutableLiveData<LibraryRestClientResult<LibraryApiResponseWrapper<UserSettings?>>>()
    val updateUserSettingsLiveData: LiveData<LibraryRestClientResult<LibraryApiResponseWrapper<UserSettings?>>>
        get() = _updateUserSettingsLiveData

    var isEnabled = false

    lateinit var description: StringResource

    fun fetchManageNotificationsList(activity: WeakReference<FragmentActivity>) {
        viewModelScope.launch {
            fetchUserSettingsUseCase.fetchUserSettings()
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect(
                onLoading = {
                    _settingListLiveData.postValue(LibraryRestClientResult.loading())
                },
                onSuccess = {
                    _settingListLiveData.postValue(
                        LibraryRestClientResult.success(
                            notificationSettingsGenerator.getNotificationSettingsList(
                                it!!, activity
                            )
                        )
                    )
                },
                onError = { errorMessage, errorCode ->
                    _settingListLiveData.postValue(LibraryRestClientResult.error(errorMessage))
                }
            )
        }
    }

    fun toggleSwitchAccordingToIdentifier(identifier: Int, status: Boolean) {
        isEnabled = status
        var switchType = ""
        when (identifier) {
            BaseConstants.ManageNotificationPosition.GOLD_PRICE_ALERT -> {
                toggleGoldPriceAlert(status)
                switchType = BaseConstants.GoldPriceAlerts
                description =
                    if (status) SettingsMR.strings.feature_settings_gold_price_alert_enabled else SettingsMR.strings.feature_settings_gold_price_alert_disabled
            }
            BaseConstants.ManageNotificationPosition.AUSPICIOUS_ALERT -> {
                toggleAuspiciousAlert(status)
                switchType = BaseConstants.ShubhMuhuratAlerts
                description =
                    if (status) SettingsMR.strings.feature_settings_shubh_muhurat_alert_enabled else SettingsMR.strings.feature_settings_shubh_muhurat_alert_disabled

            }
        }

        analyticsHandler.postEvent(
            EventKey.TOGGLEDPREMINDERSETUP_SETTINGSSCREEN,
            mapOf(
                BaseConstants.STATE to (if (status) BaseConstants.ON else BaseConstants.OFF),
                BaseConstants.TYPE to switchType
            )
        )
    }

    private fun toggleGoldPriceAlert(status: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(isGoldPriceAlertEnabled = status))
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect {
                    _updateUserSettingsLiveData.postValue(it)
                }
        }
    }

    private fun toggleAuspiciousAlert(status: Boolean) {
        viewModelScope.launch {
            updateUserSettingsUseCase.updateUserSettings(UserSettingsDTO(isAuspiciousDateAlertEnabled = status))
                .mapToDTO {
                    it?.toUserSettings()
                }
                .collect(
                    onLoading = {
                        _updateUserSettingsLiveData.postValue(LibraryRestClientResult.loading())
                    },
                    onSuccess = {
                        _updateUserSettingsLiveData.postValue(
                            LibraryRestClientResult.success(
                                LibraryApiResponseWrapper(
                                    data = it,
                                    success = true
                                )
                            )
                        )

                        if (it?.isAuspiciousDateAlertEnabled.orFalse())
                            FirebaseMessaging.getInstance()
                                .subscribeToTopic(BuildConfig.AUSPICIOUS_TIME_TOPIC)
                        else
                            FirebaseMessaging.getInstance()
                                .unsubscribeFromTopic(BuildConfig.AUSPICIOUS_TIME_TOPIC)

                    },
                    onError = { errorMessage, errorCode ->
                        _updateUserSettingsLiveData.postValue(
                            LibraryRestClientResult.error(
                                errorMessage
                            )
                        )
                    }
                )
        }
    }

}