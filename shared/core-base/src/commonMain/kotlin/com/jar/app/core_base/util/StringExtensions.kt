package com.jar.app.core_base.util

fun String.isSpecialCharacters(): Boolean {
    val specialRegex = Regex("[!@#$%&*()_+=|<>?{}\\[\\]~-]")
    return specialRegex.containsMatchIn(this)
}

val String.isValidVpa: Boolean
    get() = this.isNotBlank() && this.matches("^[a-zA-Z0-9.-]{2,256}@[a-zA-Z][a-zA-Z]{2,64}$".toRegex())

val String.isFirstCharacterSpecial: Boolean
    get() = this.isNotBlank() && this.matches("^[^a-zA-Z0-9]".toRegex())

val String.isValidEmail: Boolean
    get() = this.isNotBlank() && this.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})\$".toRegex())

val String.formatNumber: String
    get() = if (this.contains("+91"))
        this.removePrefix("+91")
    else
        this.removePrefix("91")
