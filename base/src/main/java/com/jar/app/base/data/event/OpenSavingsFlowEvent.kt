package com.jar.app.base.data.event

import com.jar.app.base.data.model.ExperimentSavingType

data class OpenSavingsFlowEvent(
    val savingsType: ExperimentSavingType
)