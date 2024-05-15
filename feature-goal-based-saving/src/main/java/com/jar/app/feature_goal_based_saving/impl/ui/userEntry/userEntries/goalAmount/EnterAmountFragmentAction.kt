package com.jar.app.feature_goal_based_saving.impl.ui.userEntry.userEntries.goalAmount

internal sealed class EnterAmountFragmentAction {
    object Init: EnterAmountFragmentAction()
    data class SentAmountChangedEvent(
        val screenType: String,
        val action: String,
        val errorMessageShown: String
    ): EnterAmountFragmentAction()
    data class OnNextButtonClicked(
        val amount: String
    ): EnterAmountFragmentAction()

    data class FetchDuration(
        val amount: String
    ): EnterAmountFragmentAction()

    data class FetchBreakDown(
        val amount: Int,
        val months: Int
    ): EnterAmountFragmentAction()

    object ClearOldDurationData : EnterAmountFragmentAction()
    data class OnEditGoalIconClicked(
        val amountentered: String,
        val duration: Int?,
        val dailySavingsAmount: Int?
    ) : EnterAmountFragmentAction()

    data class OnConfirmButtonClicked(
        val amountentered: String,
        val duration: Int?,
        val dailySavingsAmount: Int?
    ) : EnterAmountFragmentAction()

}