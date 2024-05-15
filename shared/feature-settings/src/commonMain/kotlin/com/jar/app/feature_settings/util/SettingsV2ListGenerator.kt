package com.jar.app.feature_settings.util

import com.jar.app.core_base.shared.CoreBaseBuildKonfig
import com.jar.app.core_base.shared.CoreBaseMR
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.DeviceUtils
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_network.CoreNetworkBuildKonfig
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.feature_savings_common.shared.domain.model.GoalBasedSavingDetails
import com.jar.app.feature_savings_common.shared.domain.model.GoalProgressStatus
import com.jar.app.feature_savings_common.shared.domain.model.SubscriptionStatus
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingType
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingsDetails
import com.jar.app.feature_settings.domain.model.DailyInvestmentCancellationV2RedirectionDetails
import com.jar.app.feature_settings.domain.model.SettingSeparator
import com.jar.app.feature_settings.domain.model.Setting
import com.jar.app.feature_settings.domain.model.SettingGroup
import com.jar.app.feature_settings.domain.model.Settings
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_user_api.domain.model.UserGoldSipDetails
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsV2ListGenerator constructor(
    private val prefs: PrefsApi,
    private val retainedPrefsApi: RetainedPrefsApi,
    private val deviceUtils: DeviceUtils
) {

    suspend fun getSettingList(
        convertToString: (
            stringRes: StringResource,
            args: Array<Any>
        ) -> String?,
        userDailySavingDetails: UserSavingsDetails?,
        userGoldSipDetails: UserGoldSipDetails?,
        userRoundOffDetails: UserSavingsDetails?,
        goalBasedSavingDetails: GoalBasedSavingDetails?,
        dailyInvestmentCancellationV2RedirectionDetails: DailyInvestmentCancellationV2RedirectionDetails?
    ): List<Settings> =
        withContext(Dispatchers.Default) {
            val os = deviceUtils.getOsName()
            val isIos = os == "ios"  // REPLACE WITH ENUM

            val list = ArrayList<Settings>()

            /** PAYMENT SETTINGS **/
            list.add(
                SettingGroup(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_payment_settings,
                        emptyArray()
                    ).orEmpty(),
                    position = BaseConstants.SettingsV2CardPosition.HEADER_PAYMENTS
                )
            )
            //Payment Methods
            list.add(
                Setting(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_payment_methods,
                        emptyArray()
                    ),
                    desc = null,
                    startIconRes = SettingsMR.images.feature_settings_ic_credit_card_svg,
                    position = BaseConstants.SettingsV2CardPosition.PAYMENT_METHODS,
                    viewTag = BaseConstants.SettingV2CardViewTags.PAYMENT_METHODS
                )
            )

            //Daily Saving
            when (UserSavingType.fromString(goalBasedSavingDetails?.dailySavingsType)) {
                UserSavingType.DAILY_SAVINGS -> {
                    val version = dailyInvestmentCancellationV2RedirectionDetails?.version
                    val isSavingsPaused = userDailySavingDetails?.pauseStatus?.savingsPaused == true
                    val isEnabled = userDailySavingDetails?.enabled == true

                    list.add(
                        Setting(
                            title = convertToString(
                                SettingsMR.strings.feature_settings_daily_savings,
                                emptyArray()
                            ),
                            descIcon = when (version) {
                                BaseConstants.CancellationFlowVersion.v1 -> null
                                BaseConstants.CancellationFlowVersion.v2 -> when {
                                    isSavingsPaused -> SettingsMR.images.feature_settings_ic_paused_v2
                                    isEnabled -> SettingsMR.images.feature_settings_ic_active_v2
                                    else -> {
                                        null
                                    }
                                }

                                else -> when {
                                    isSavingsPaused -> SettingsMR.images.feature_settings_ic_stopped
                                    isEnabled -> SettingsMR.images.feature_settings_ic_active
                                    else -> {
                                        null
                                    }
                                }
                            },
                            desc = when (userDailySavingDetails?.subscriptionStatus) {
                                SubscriptionStatus.SUCCESS.name -> {
                                    convertToString(
                                        SettingsMR.strings.feature_settings_rs_value_int,
                                        arrayOf(
                                            userDailySavingDetails.subscriptionAmount.orZero()
                                                .toInt()
                                        )
                                    )
                                }

                                SubscriptionStatus.PENDING.name -> {
                                    // com.jar.app.core_ui.R.color.color_EBB46A
                                    convertToString(
                                        SettingsMR.strings.feature_settings_in_progress,
                                        emptyArray()
                                    )
                                }

                                SubscriptionStatus.FAILURE.name -> {
                                    // com.jar.app.core_ui.R.color.redAlertText
                                    convertToString(
                                        SettingsMR.strings.feature_settings_failed,
                                        emptyArray()
                                    )
                                }

                                else -> {
                                    // com.jar.app.core_ui.R.color.white
                                    convertToString(
                                        SettingsMR.strings.feature_settings_setup_now,
                                        emptyArray()
                                    )
                                }
                            },
                            descColor = when (userDailySavingDetails?.subscriptionStatus) {
                                SubscriptionStatus.SUCCESS.name -> {
                                    CoreBaseMR.colors.color_FFFFFF
                                }

                                SubscriptionStatus.PENDING.name -> {
                                    CoreBaseMR.colors.color_EBB46A
                                }

                                SubscriptionStatus.FAILURE.name -> {
                                    CoreBaseMR.colors.color_EB6A6E
                                }

                                else -> {
                                    // com.jar.app.core_ui.R.color.white
                                    CoreBaseMR.colors.color_FFFFFF
                                }
                            },
                            startIconRes = SettingsMR.images.feature_settings_ic_gold_sip,
                            position = BaseConstants.SettingsV2CardPosition.DAILY_SAVINGS,
                            viewTag = BaseConstants.SettingV2CardViewTags.DAILY_SAVINGS
                        )
                    )
                }

                UserSavingType.SAVINGS_GOAL -> {
                    list.add(
                        Setting(
                            title = goalBasedSavingDetails?.messageCta?.text.orEmpty(),
                            desc = when (GoalProgressStatus.fromString(goalBasedSavingDetails?.goalProgressStatus)) {
                                GoalProgressStatus.ACTIVE -> {
                                    goalBasedSavingDetails?.messageCta?.buttonText.orEmpty()
                                }

                                GoalProgressStatus.SETUP -> {
                                    goalBasedSavingDetails?.messageCta?.buttonText.orEmpty()
                                }
                            },
                            descColor = when (GoalProgressStatus.fromString(goalBasedSavingDetails?.goalProgressStatus)) {
                                GoalProgressStatus.ACTIVE -> {
                                    CoreBaseMR.colors.color_58DDC8
                                }

                                GoalProgressStatus.SETUP -> {
                                    CoreBaseMR.colors.color_FFFFFF
                                }
                            },
                            startIconRes = SettingsMR.images.feature_settings_ic_gold_sip,
                            position = BaseConstants.SettingsV2CardPosition.DAILY_SAVINGS,
                            viewTag = BaseConstants.SettingV2CardViewTags.DAILY_SAVINGS
                        )
                    )

                }
            }

            if (!isIos) {
                //Round Offs
                list.add(
                    Setting(
                        title = convertToString(
                            SettingsMR.strings.feature_settings_round_off,
                            emptyArray()
                        ),
                        desc = if (userRoundOffDetails?.enabled.orFalse()) {
                            if (userRoundOffDetails?.subscriptionStatus.isNullOrEmpty())
                                convertToString(
                                    SettingsMR.strings.feature_settings_enabled,
                                    emptyArray()
                                )
                            else
                                when (userRoundOffDetails?.subscriptionStatus) {
                                    SubscriptionStatus.SUCCESS.name -> {
                                        convertToString(
                                            SettingsMR.strings.feature_settings_upto_x,
                                            arrayOf(
                                                userRoundOffDetails.subscriptionAmount.orZero()
                                                    .toInt()
                                            )
                                        )
                                    }

                                    SubscriptionStatus.PENDING.name -> {
                                        // com.jar.app.core_ui.R.color.color_EBB46A
                                        convertToString(
                                            SettingsMR.strings.feature_settings_in_progress,
                                            emptyArray()
                                        )
                                    }

                                    SubscriptionStatus.FAILURE.name -> {
                                        // com.jar.app.core_ui.R.color.redAlertText
                                        convertToString(
                                            SettingsMR.strings.feature_settings_failed,
                                            emptyArray()
                                        )
                                    }

                                    else -> {
                                        convertToString(
                                            SettingsMR.strings.feature_settings_setup_now,
                                            emptyArray()
                                        )
                                    }
                                }
                        } else {
                            // com.jar.app.core_ui.R.color.white
                            convertToString(
                                SettingsMR.strings.feature_settings_setup_now,
                                emptyArray()
                            )
                        },
                        descColor = if (userRoundOffDetails?.enabled.orFalse()) {
                            if (userRoundOffDetails?.subscriptionStatus.isNullOrEmpty()) {
                                CoreBaseMR.colors.color_FFFFFF
                            } else {
                                when (userRoundOffDetails?.subscriptionStatus) {
                                    SubscriptionStatus.SUCCESS.name -> {
                                        CoreBaseMR.colors.color_FFFFFF
                                    }

                                    SubscriptionStatus.PENDING.name -> {
                                        CoreBaseMR.colors.color_EBB46A
                                    }

                                    SubscriptionStatus.FAILURE.name -> {
                                        CoreBaseMR.colors.color_EB6A6E
                                    }

                                    else -> {
                                        CoreBaseMR.colors.color_FFFFFF
                                    }
                                }
                            }
                        } else {
                            CoreBaseMR.colors.color_FFFFFF
                        },
                        startIconRes = SettingsMR.images.feature_settings_ic_round_off,
                        position = BaseConstants.SettingsV2CardPosition.ROUND_OFF,
                        viewTag = BaseConstants.SettingV2CardViewTags.ROUND_OFF
                    )
                )
            }

            //Savings Plan(SIP)
            list.add(
                Setting(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_savings_plan,
                        emptyArray()
                    ),
                    desc = when (userGoldSipDetails?.subscriptionStatus) {
                        SubscriptionStatus.SUCCESS.name -> {
                            convertToString(
                                SettingsMR.strings.feature_buy_gold_currency_sign_x_int,
                                arrayOf(userGoldSipDetails.subscriptionAmount.toInt().orZero())
                            )
                        }

                        SubscriptionStatus.PENDING.name -> {
                            // com.jar.app.core_ui.R.color.color_EBB46A
                            convertToString(
                                SettingsMR.strings.feature_settings_in_progress,
                                emptyArray()
                            )
                        }

                        SubscriptionStatus.FAILURE.name -> {
                            // com.jar.app.core_ui.R.color.redAlertText
                            convertToString(
                                SettingsMR.strings.feature_settings_failed,
                                emptyArray()
                            )
                        }

                        else -> {
                            convertToString(
                                SettingsMR.strings.feature_settings_setup_now,
                                emptyArray()
                            )
                        }
                    },
                    descColor = when (userGoldSipDetails?.subscriptionStatus) {
                        SubscriptionStatus.SUCCESS.name -> {
                            CoreBaseMR.colors.color_FFFFFF
                        }

                        SubscriptionStatus.PENDING.name -> {
                            CoreBaseMR.colors.color_EBB46A
                        }

                        SubscriptionStatus.FAILURE.name -> {
                            CoreBaseMR.colors.color_EB6A6E
                        }

                        else -> {
                            CoreBaseMR.colors.color_FFFFFF
                        }
                    },
                    startIconRes = SettingsMR.images.feature_settings_ic_gold_sip,
                    position = BaseConstants.SettingsV2CardPosition.GOLD_SIP,
                    viewTag = BaseConstants.SettingV2CardViewTags.GOLD_SIP
                )
            )


            /** APP SETTINGS **/

            if (!isIos) {
                list.add(
                    SettingSeparator(
                        position = BaseConstants.SettingsV2CardPosition.SEPARATOR_PAYMENTS,
                        uniqueId = "SEPARATOR_PAYMENTS"
                    )
                )

                list.add(
                    SettingGroup(
                        title = convertToString(
                            SettingsMR.strings.feature_settings_app_settings,
                            emptyArray()
                        ).orEmpty(),
                        position = BaseConstants.SettingsV2CardPosition.HEADER_APP_SETTINGS
                    )
                )

                list.add(
                    Setting(
                        title = convertToString(
                            SettingsMR.strings.feature_settings_notification_settings,
                            emptyArray()
                        ),
                        desc = null,
                        startIconRes = SettingsMR.images.feature_settings_ic_notification_svg,
                        position = BaseConstants.SettingsV2CardPosition.NOTIFICATION_SETTINGS,
                        viewTag = BaseConstants.SettingV2CardViewTags.NOTIFICATION_SETTINGS
                    )
                )

                list.add(
                    Setting(
                        title = convertToString(
                            SettingsMR.strings.feature_settings_language,
                            emptyArray()
                        ),
                        desc = prefs.getCurrentLanguageName(),
                        startIconRes = SettingsMR.images.feature_settings_ic_language_svg,
                        position = BaseConstants.SettingsV2CardPosition.LANGUAGE,
                        viewTag = BaseConstants.SettingV2CardViewTags.LANGUAGE
                    )
                )

            }
            /** PRIVACY AND PERMISSIONS **/

            list.add(
                SettingSeparator(
                    position = BaseConstants.SettingsV2CardPosition.SEPARATOR_APP_SETTINGS,
                    uniqueId = "SEPARATOR_APP_SETTINGS"
                )
            )

            list.add(
                SettingGroup(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_privacy_and_permission,
                        emptyArray()
                    ).orEmpty(),
                    position = BaseConstants.SettingsV2CardPosition.HEADER_PRIVACY_PERMISSION
                )
            )

            list.add(
                Setting(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_jar_security_shield,
                        emptyArray()
                    ),
                    desc = if (prefs.isJarShieldEnabled()) {
                        convertToString(
                            SettingsMR.strings.feature_settings_on,
                            emptyArray()
                        )
                    } else {
                        convertToString(
                            SettingsMR.strings.feature_settings_off,
                            emptyArray()
                        )
                    },
                    startIconRes = SettingsMR.images.feature_settings_ic_security_shield_svg,
                    position = BaseConstants.SettingsV2CardPosition.JAR_SECURITY_SHIELD,
                    viewTag = BaseConstants.SettingV2CardViewTags.JAR_SECURITY_SHIELD
                )
            )

            list.add(
                Setting(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_terms_and_conditions,
                        emptyArray()
                    ),
                    desc = null,
                    startIconRes = SettingsMR.images.feature_settings_ic_tnc_svg,
                    position = BaseConstants.SettingsV2CardPosition.TERMS_AND_CONDITIONS,
                    viewTag = BaseConstants.SettingV2CardViewTags.TERMS_AND_CONDITIONS
                )
            )

            list.add(
                Setting(
                    title = convertToString(
                        SettingsMR.strings.feature_settings_privacy_policy,
                        emptyArray()
                    ),
                    desc = null,
                    startIconRes = SettingsMR.images.feature_settings_ic_privacy_policy_svg,
                    position = BaseConstants.SettingsV2CardPosition.PRIVACY_POLICY,
                    viewTag = BaseConstants.SettingV2CardViewTags.PRIVACY_POLICY
                )
            )
            if (CoreBaseBuildKonfig.ENV == "staging") {
                list.add(SettingSeparator(
                    position = BaseConstants.SettingsV2CardPosition.SEPARATOR_TESTING,
                    uniqueId = "SEPARATOR_TESTING"
                ))
                list.add(
                    Setting(
                        title = convertToString(
                            SettingsMR.strings.feature_settings_base_api_url,
                            emptyArray()
                        ),
                        desc = retainedPrefsApi.getApiBaseUrl()
                            .ifEmpty { CoreNetworkBuildKonfig.BASE_URL_KTOR },
                        startIconRes = SettingsMR.images.feature_settings_ic_privacy_policy_svg,
                        position = BaseConstants.SettingsV2CardPosition.BASE_API_URL,
                        viewTag = BaseConstants.SettingV2CardViewTags.BASE_API_URL
                    )
                )
                list.add(
                    Setting(
                        title = convertToString(
                            SettingsMR.strings.feature_settings_testing_activity,
                            emptyArray()
                        ),
                        desc = null,
                        startIconRes = SettingsMR.images.feature_settings_ic_privacy_policy_svg,
                        position = BaseConstants.SettingsV2CardPosition.TEST_ACTIVITY,
                        viewTag = BaseConstants.SettingV2CardViewTags.TEST_ACTIVITY
                    )
                )
            }

            //Sort by position
            list.sortBy {
                it.position
            }

            return@withContext list
        }
}