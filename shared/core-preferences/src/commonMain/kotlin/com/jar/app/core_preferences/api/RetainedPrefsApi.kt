package com.jar.app.core_preferences.api

/**
 * These prefes will be retained after logout
 */
interface RetainedPrefsApi {
    fun getApiBaseUrl(): String
    fun setApiBaseUrl(url: String)
    fun getIsAutomationEnabled(): Boolean
    fun setIsAutomationEnabled(value: Boolean)
}