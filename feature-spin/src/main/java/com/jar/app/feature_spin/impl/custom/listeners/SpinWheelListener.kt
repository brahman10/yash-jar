package com.jar.app.feature_spin.impl.custom.listeners

internal interface SpinWheelListener {
    fun onSpinComplete(outcome: Int? = null, spinId: String? = null)
    fun onCloseWinnings() {}
    fun onBackIconClicked() {}
    fun onUseWinningClicked(deeplink: String) {}

    // these were used for events
    fun onDragCancel() {}
    fun onDragComplete(outComeType: String?, outcome: Int? = null, spinId: String? = null) {}
    fun onShownWinnings(amount: Int) {}
    fun onShownTotalWinnings() {}

    fun onLiverReachedToMax() {}
}