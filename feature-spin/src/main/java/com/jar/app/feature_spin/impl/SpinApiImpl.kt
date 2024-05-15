package com.jar.app.feature_spin.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.feature_spin.api.SpinApi
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import dagger.Lazy
import javax.inject.Inject

internal class SpinApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : SpinApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openSpinFragmentV2(context: SpinsContextFlowType, backstackId: String?) {
        backstackId?.takeIf { it.isNotEmpty() }?.let {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/spinGameFragmentV2/${context.name}/$it"),
                getNavOptions(shouldAnimate = true)
            )
        } ?: kotlin.run {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/spinGameFragmentV2/${context.name}"),
                getNavOptions(shouldAnimate = true)
            )
        }
    }
}