package com.jar.app.feature_lending.impl.ui.repeat_withdrawal_landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_lending.impl.domain.model.IntroItem
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.ui.repeat_withdrawal_landing.RepeatWithdrawalLandingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class RepeatWithdrawalLandingViewModelAndroid @Inject constructor(
    private val fetchPreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    private val fetchLoanApplicationListUseCase: FetchLoanApplicationListUseCase
) : ViewModel() {

    private val viewModel by lazy {
        RepeatWithdrawalLandingViewModel(
            fetchPreApprovedDataUseCase = fetchPreApprovedDataUseCase,
            fetchLoanApplicationListUseCase = fetchLoanApplicationListUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel

    fun getIntroData(): List<IntroItem> {
        val introItems = ArrayList<IntroItem>()
        introItems.add(
            IntroItem(
                com.jar.app.core_ui.R.drawable.core_ui_ic_bank,
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_money_in_your_account_is_few_clicks_away.resourceId
            )
        )
        introItems.add(
            IntroItem(
                com.jar.app.core_ui.R.drawable.core_ui_ic_calendar,
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_flexible_emi_options_to_choose_from.resourceId
            )
        )
        introItems.add(
            IntroItem(
                com.jar.app.core_ui.R.drawable.core_ui_ic_money,
                com.jar.app.feature_lending.shared.MR.strings.feature_lending_withdraw_as_low_as_1000_and_foreclose_anytime.resourceId
            )
        )
        return introItems
    }
}