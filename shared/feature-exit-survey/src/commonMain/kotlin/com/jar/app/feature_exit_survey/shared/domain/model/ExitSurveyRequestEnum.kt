package com.jar.app.feature_exit_survey.shared.domain.model

enum class ExitSurveyRequestEnum {
    DAILY_SAVINGS,
    DAILY_SAVINGS_TRANSACTION_SCREEN,
    ROUND_OFFS,
    ROUND_OFFS_TRANSACTION_SCREEN,
    MANUAL_BUY,
    MANUAL_BUY_TRANSACTION_SCREEN;

    companion object {
        fun fromString(value: String): ExitSurveyRequestEnum? {
            return values().find { it.name == value }
        }
    }

    override fun toString(): String {
        return name
    }
}

