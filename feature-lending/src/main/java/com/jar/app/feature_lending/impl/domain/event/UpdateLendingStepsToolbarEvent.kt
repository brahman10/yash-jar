package com.jar.app.feature_lending.impl.domain.event

import androidx.annotation.StringRes
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep

data class UpdateLendingStepsToolbarEvent(
    val shouldShowSteps: Boolean = true,
    val step: LendingStep,
    @StringRes val toolbarTitle:Int? = null
)