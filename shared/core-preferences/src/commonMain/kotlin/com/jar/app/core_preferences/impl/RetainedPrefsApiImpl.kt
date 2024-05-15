package com.jar.app.core_preferences.impl

import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.core_preferences.util.PrefConstants
import com.jar.internal.library.jar_core_preferences.api.PreferenceApi

internal class RetainedPrefsApiImpl constructor(
    preferenceApi: PreferenceApi
) : JarPreference(preferenceApi), RetainedPrefsApi {

    override fun getApiBaseUrl() =
        readNonNullableSync(PrefConstants.API_BASE_URL, "")

    override fun setApiBaseUrl(url: String) = writeSync(
        PrefConstants.API_BASE_URL,
        url
    )

    override fun getIsAutomationEnabled() =
        readNonNullableSync(PrefConstants.IS_AUTOMATION_ENABLED, false)

    override fun setIsAutomationEnabled(value: Boolean) = writeSync(
        PrefConstants.IS_AUTOMATION_ENABLED,
        value
    )
}