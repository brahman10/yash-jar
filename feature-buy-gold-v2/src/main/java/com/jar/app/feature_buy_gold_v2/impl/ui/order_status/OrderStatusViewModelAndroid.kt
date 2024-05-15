package com.jar.app.feature_buy_gold_v2.impl.ui.order_status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.ui.OrderStatusViewModel
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchManualPaymentStatusUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchOrderStatusDynamicCardsUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserSettingsUseCase
import com.jar.app.feature_weekly_magic_common.shared.domain.usecase.FetchWeeklyChallengeMetaDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OrderStatusViewModelAndroid @Inject constructor(
    private val fetchManualPaymentStatusUseCase: FetchManualPaymentStatusUseCase,
    private val fetchWeeklyChallengeMetaDataUseCase: FetchWeeklyChallengeMetaDataUseCase,
    private val fetchOrderStatusDynamicCardsUseCase: FetchOrderStatusDynamicCardsUseCase,
    private val fetchUserSettingsUseCase: FetchUserSettingsUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        OrderStatusViewModel(
            fetchManualPaymentStatusUseCase,
            fetchWeeklyChallengeMetaDataUseCase,
            fetchOrderStatusDynamicCardsUseCase,
            fetchUserSettingsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}