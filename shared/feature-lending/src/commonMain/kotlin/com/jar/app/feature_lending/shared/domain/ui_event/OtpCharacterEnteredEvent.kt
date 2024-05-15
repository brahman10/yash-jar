package com.jar.app.feature_lending.shared.domain.ui_event

sealed class OtpCharacterEnteredEvent {
    data class Char(val from : Int,val char: String) : OtpCharacterEnteredEvent()
}
