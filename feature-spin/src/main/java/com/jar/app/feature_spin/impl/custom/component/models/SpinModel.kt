package com.jar.app.feature_spin.impl.custom.component.models

data class SpinModel(
    var totalSpins: Event<Int> = Event(0),
    var totalWinnings: Event<TotalWinning?> = Event(null),
    val onStartRotation: Event<Long?> = Event(null),
    val onSpinComplete: Event<SpinCompleteModel?> = Event(null),
    var onYChange: Event<Float?> = Event(null)
)

data class SpinCompleteModel(
    val winningAmount:Int?,
    val RemainingSpinModel: RemainingSpinModel
)

data class RemainingSpinModel(
    val daySpin: Int = 10,
    val remainingSpins: Int = 0
)

data class TotalWinning(
    val currentTotal: Int,
    val outCome: Int?
)


