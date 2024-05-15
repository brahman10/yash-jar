package com.jar.health_insurance.impl.ui.insurance_plan_comparison

import androidx.lifecycle.viewModelScope
import com.jar.app.core_compose_ui.base.BaseComposeMviViewModel
import com.jar.app.core_compose_ui.base.BaseViewState
import com.jar.app.feature_health_insurance.shared.domain.use_cases.FetchPlanComparisonUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanComparisonViewModel @Inject constructor(
    private val fetchPlanComparisonUseCase: FetchPlanComparisonUseCase
) : BaseComposeMviViewModel<BaseViewState<PlanComparisonState>, PlanComparisonEvent>() {

    override fun onTriggerEvent(eventType: PlanComparisonEvent) {
        when (eventType) {
            is PlanComparisonEvent.LoadPlanComparison -> onLoadPlanComparison(eventType.providerId)
        }
    }

    private fun onLoadPlanComparison(providerId: String) {
        setState(BaseViewState.Loading)
        viewModelScope.launch {
            fetchPlanComparisonUseCase.fetchPlanComparisons(providerId)
                .collect(
                    onSuccess = {
                        setState(
                            BaseViewState.Data(
                                PlanComparisonState(
                                    planData = it.plans,
                                    planMetadata = it.metadata
                                )
                            )
                        )
                    },

                    onError = { errorMessage, errorCode ->
                        setState(
                            BaseViewState.Error(
                                PlanComparisonState(
                                    errorMessage = errorMessage
                                )
                            )
                        )
                    }
                )
        }
    }
}