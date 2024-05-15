package com.jar.health_insurance.impl.ui.insurance_plan_comparison

import androidx.compose.runtime.Immutable

@Immutable
data class PlanComparisonState(
    val planData: Map<String, Map<String, String?>> = emptyMap(),
    val planMetadata:Map<String, String> = emptyMap(),
    val errorMessage: String = ""
)

sealed class PlanComparisonEvent {
    data class LoadPlanComparison(val providerId: String) : PlanComparisonEvent()
}
