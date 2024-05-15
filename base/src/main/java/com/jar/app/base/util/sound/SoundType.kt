package com.jar.app.base.util.sound

sealed class SoundType {
    object ALWAYS_ON : SoundType()
    object PULL_DOWN : SoundType()
    object SPIN_WHEEL : SoundType()
    object OH_NO : SoundType()
    object JACKPOT_CELEBRATION : SoundType()
    object CELEBRATION : SoundType()
    object BUTTON_RESET : SoundType()
    data class CustomSound(val soundUrl: String) : SoundType()
}