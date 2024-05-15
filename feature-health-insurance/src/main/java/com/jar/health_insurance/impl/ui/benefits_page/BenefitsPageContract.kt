package com.jar.health_insurance.impl.ui.benefits_page

import androidx.compose.runtime.Immutable
import com.jar.app.feature_health_insurance.shared.data.models.benefits.Benefit

@Immutable
data class BenefitsPageState(
    val benefitsList: List<Benefit> = emptyList(),
    val toolBarTitle: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

sealed class BenefitsEvent {
    data class OnCardExpanded(val id: String) : BenefitsEvent()
    data class LoadBenefits(val insuranceId: String? = null) : BenefitsEvent()
    object ErrorMessageDisplayed : BenefitsEvent()
}
