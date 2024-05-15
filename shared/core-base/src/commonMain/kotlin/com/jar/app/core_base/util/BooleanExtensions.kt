package com.jar.app.core_base.util

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}

fun Boolean.toLong(): Long {
    return if (this) 1 else 0
}