package com.jar.app.core_ui.widget.button

enum class ButtonType(id: Int) {
    primaryButton(0),
    secondaryButton(1),
    secondaryHollowButton(2);

    companion object {
        fun fromParams(id: Int): ButtonType {
            return when (id) {
                0 -> primaryButton
                1 -> secondaryButton
                2 -> secondaryHollowButton
                else -> primaryButton
            }
        }
    }
}