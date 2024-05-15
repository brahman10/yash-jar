package com.jar.app.feature_spin.impl.custom.component.models

data class TextAnimationFields(
    var textSize:Int = 20,
    var textStyleString: String = "Bold",
    var maxNumbers:Int = 4,
    var animationDuration: Long = 100,
    var gapBetweenTwoNumbersDuration: Long = 50
)