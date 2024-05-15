package com.jar.app.core_ui.util


fun String.truncateAndAddDot(length: Int): String {
    return if (this.length > length) "…${this.takeLast(length - 1)}" else this
}