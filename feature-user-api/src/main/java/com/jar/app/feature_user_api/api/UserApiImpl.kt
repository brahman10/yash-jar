package com.jar.app.feature_user_api.api

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.feature_user_api.R
import dagger.Lazy
import javax.inject.Inject

internal class UserApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : UserApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openUserSavedAddress(fromScreen: String) {
        if (navController.currentBackStackEntry?.destination?.id != R.id.userSavedAddressFragment) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/userSavedAddresses/$fromScreen")
            )
        }
    }

}