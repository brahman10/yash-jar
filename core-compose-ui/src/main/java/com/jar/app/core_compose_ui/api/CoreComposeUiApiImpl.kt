package com.jar.app.core_compose_ui.api

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.airbnb.android.showkase.ui.ShowkaseBrowserActivity
import com.jar.app.core_compose_ui.ShowkaseRootModule
import dagger.Lazy
import javax.inject.Inject

internal class CoreComposeUiApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val fragmentActivity: FragmentActivity
) : CoreComposeUiApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openShowkaseActivity() {
        navController.context.startActivity(ShowkaseBrowserActivity.getIntent(navController.context, ShowkaseRootModule::class.java.canonicalName!!))
    }
}