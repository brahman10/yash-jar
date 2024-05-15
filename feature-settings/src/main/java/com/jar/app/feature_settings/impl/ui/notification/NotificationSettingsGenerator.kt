package com.jar.app.feature_settings.impl.ui.notification

import androidx.fragment.app.FragmentActivity
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_settings.domain.model.NotificationSettingsSwitch
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_user_api.domain.model.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationSettingsGenerator @Inject constructor() {

    //Todo: Add other notification switch(on/off) options
    suspend fun getNotificationSettingsList(
        userSetting: UserSettings,
        contextRef: WeakReference<FragmentActivity>,
    ): List<NotificationSettingsSwitch> =
        withContext(Dispatchers.Default) {

            val list = ArrayList<NotificationSettingsSwitch>()

            val context = contextRef.get()!!

            list.add(
                NotificationSettingsSwitch(
                    title = SettingsMR.strings.feature_settings_gold_price_alerts,
                    desc = SettingsMR.strings.feature_settings_gold_price_alerts_description,
                    isEnabled = userSetting.isGoldPriceAlertEnabled ?: false,
                    position = BaseConstants.ManageNotificationPosition.GOLD_PRICE_ALERT,
                )
            )
//            list.add(
//                NotificationSettingsSwitch(
//                    title = R.string.feature_settings_shubh_muhurat_alerts,
//                    desc = R.string.feature_settings_shubh_muhurat_alerts_description,
//                    isEnabled = userSetting.isAuspiciousDateAlertEnabled ?: false,
//                    position = BaseConstants.ManageNotificationPosition.AUSPICIOUS_ALERT,
//                )
//            )


            //Sort by position
            list.sortBy {
                it.position
            }

            return@withContext list
        }
}