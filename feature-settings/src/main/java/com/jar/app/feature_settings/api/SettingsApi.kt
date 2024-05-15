package com.jar.app.feature_settings.api

import androidx.fragment.app.Fragment

/**
 * Settings Api (to be used by other modules)
 * **/
interface SettingsApi {
    /**
     * Method to start setup settings screen
     * **/
    fun openSettingFragment(): Fragment
    /**
     * Method to open round off Fragment
     * **/
    fun openRoundOffFragment()
    /**
     * Method to open add UPI address screen
     * **/
    fun openAddUpiFragment()
}