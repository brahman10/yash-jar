package com.jar.app.feature_profile.api

import androidx.fragment.app.Fragment

/**
 * Profile Api (to be used by other modules)
 * **/
interface ProfileApi {
    /**
     * Method to start setup profile screen
     * **/
    fun openProfileFlow(): Fragment
}