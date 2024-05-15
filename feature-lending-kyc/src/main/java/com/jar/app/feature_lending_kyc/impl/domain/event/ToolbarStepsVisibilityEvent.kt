package com.jar.app.feature_lending_kyc.impl.domain.event

import com.jar.app.feature_lending_kyc.impl.data.Step

data class ToolbarStepsVisibilityEvent(
    val shouldShowSteps: Boolean = true,
    val step: Step,
    val shouldShowToolbarSeparator: Boolean = false
)
