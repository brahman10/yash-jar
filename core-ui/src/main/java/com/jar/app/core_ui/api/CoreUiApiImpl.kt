package com.jar.app.core_ui.api

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.base.data.model.PauseSavingOption
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.domain.model.InfoDialogData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.R
import com.jar.app.core_ui.explanatory_video.model.ExplanatoryVideoData
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.pause_savings.GenericPauseData
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.Lazy
import javax.inject.Inject

internal class CoreUiApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val serializer: Serializer,
    private val fragmentActivity: FragmentActivity
) : CoreUiApi {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openTestComponentsFragment() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/testComponents/")
        )
    }

    override fun openInfoDialog(infoDialogData: InfoDialogData) {
        if (navController.currentBackStackEntry?.destination?.id != R.id.infoDialog) {
            val encoded = encodeUrl(serializer.encodeToString(infoDialogData))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/infoDialog/$encoded")
            )
        }
    }

    override fun openExplanatoryVideoFragment(
        explanatoryVideoData: ExplanatoryVideoData,
        onVideoStarted: () -> Unit,
        onVideoEnded: (Boolean) -> Unit
    ) {
        if (navController.currentBackStackEntry?.destination?.id != R.id.explanatoryVideoFragment) {
            val encoded = encodeUrl(serializer.encodeToString(explanatoryVideoData))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/explanatoryVideoFragment/$encoded")
            )
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                BaseConstants.ON_VIDEO_STARTED
            )
                ?.observe(fragmentActivity) {
                    onVideoStarted.invoke()
                }
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                BaseConstants.ON_VIDEO_ENDED
            )
                ?.observe(fragmentActivity) {isSkipped->
                    onVideoEnded.invoke(isSkipped)
                }
        }
    }

    override fun openGenericPostActionStatusFragment(
        genericPostActionStatusData: GenericPostActionStatusData,
        onScreenDismiss: () -> Unit
    ) {
        if (navController.currentBackStackEntry?.destination?.id != R.id.genericPostActionStatusFragment) {
            val encoded = encodeUrl(serializer.encodeToString(genericPostActionStatusData))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/genericPostActionStatusFragment/$encoded")
            )
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                BaseConstants.ON_GENERIC_SCREEN_DISMISSED
            )
                ?.observe(fragmentActivity) {
                    onScreenDismiss.invoke()
                }
        }
    }

    override fun openGenericPostActionStatusDialog(
        genericPostActionStatusData: GenericPostActionStatusData,
        onDialogDismiss: () -> Unit
    ) {
        if (navController.currentBackStackEntry?.destination?.id != R.id.genericPostActionStatusDialog) {
            val encoded = encodeUrl(serializer.encodeToString(genericPostActionStatusData))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/genericPostActionStatusDialog/$encoded")
            )
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                BaseConstants.ON_GENERIC_DIALOG_DISMISSED
            )
                ?.observe(fragmentActivity) {
                    onDialogDismiss.invoke()
                }
        }
    }

    override fun openGenericPauseSavingsDialog(
        genericPauseData: GenericPauseData,
        onPauseActionSubmitted: (PauseSavingOption) -> Unit,
        onDialogDismiss: () -> Unit
    ) {
        if (navController.currentBackStackEntry?.destination?.id != R.id.pauseSavingsBottomSheet) {
            val encoded = encodeUrl(serializer.encodeToString(genericPauseData))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/pauseSavingsBottomSheet/$encoded")
            )
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<PauseSavingOption>(
                BaseConstants.PAUSE_SAVING_DIALOG_PAUSE_ACTION
            )?.observe(fragmentActivity) { onPauseActionSubmitted.invoke(it) }

            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                BaseConstants.PAUSE_SAVING_DIALOG_DISMISSED
            )?.observe(fragmentActivity) { onDialogDismiss.invoke() }
        }
    }
}