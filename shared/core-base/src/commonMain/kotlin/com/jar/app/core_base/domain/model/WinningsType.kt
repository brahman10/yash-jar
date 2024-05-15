package com.jar.app.core_base.domain.model

enum class WinningsType {
    SPINS,
    MYSTERY_CARDS,
    WEEKLY_MAGIC,
    WEEKLY_MAGIC_NEW,
    MYSTERY_CARD_HERO;
    companion object {
        fun getWinningsType(typeString: String): WinningsType {
            return when(typeString) {
                "SPINS" -> SPINS
                "MYSTERY_CARDS" -> MYSTERY_CARDS
                "WEEKLY_MAGIC_NEW"-> WEEKLY_MAGIC_NEW
                "MYSTERY_CARD_HERO"-> MYSTERY_CARD_HERO
                else -> WEEKLY_MAGIC
            }
        }
    }
}