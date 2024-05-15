package com.jar.app.feature_promo_code.impl.ui.promo_code_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_promo_code.shared.domain.use_cases.ApplyPromoCodeUseCase
import com.jar.app.feature_promo_code.shared.ui.PromoCodeDialogViewModel
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PromoCodeDialogViewModelAndroid @Inject constructor(
    applyPromoCodeUseCase: ApplyPromoCodeUseCase,
    private val analyticsApi: AnalyticsApi,
): ViewModel() {

    private val viewModel by lazy {
        PromoCodeDialogViewModel(
            applyPromoCodeUseCase,
            analyticsApi,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}