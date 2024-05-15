package com.jar.app.core_ui.api

import com.jar.app.base.data.model.PauseSavingOption
import com.jar.app.core_base.domain.model.InfoDialogData
import com.jar.app.core_ui.explanatory_video.model.ExplanatoryVideoData
import com.jar.app.core_ui.generic_post_action.data.GenericPostActionStatusData
import com.jar.app.core_ui.pause_savings.GenericPauseData

interface CoreUiApi {

    fun openInfoDialog(infoDialogData: InfoDialogData)

    fun openTestComponentsFragment()

    fun openExplanatoryVideoFragment(
        explanatoryVideoData: ExplanatoryVideoData,
        onVideoStarted: () -> Unit,
        onVideoEnded: (Boolean) -> Unit,
    )

    fun openGenericPostActionStatusFragment(
        genericPostActionStatusData: GenericPostActionStatusData,
        onScreenDismiss: () -> Unit
    )

    fun openGenericPostActionStatusDialog(
        genericPostActionStatusData: GenericPostActionStatusData,
        onDialogDismiss: () -> Unit
    )

    fun openGenericPauseSavingsDialog(
        genericPauseData: GenericPauseData,
        onPauseActionSubmitted: (PauseSavingOption) -> Unit,
        onDialogDismiss: () -> Unit
    )
}