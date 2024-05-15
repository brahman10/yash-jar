package com.jar.app.base.util

import com.jar.app.core_base.util.orZero

fun getNameInitials(name: String): String {
    var nameInitials = ""
    name.getOrNull(0).let {
        nameInitials = it?.toString().orEmpty()
    }
    var lastWord = name.split("\\s".toRegex()).size.orZero()
    if (lastWord >= 2) {
        lastWord--
        name.split("\\s".toRegex()).getOrNull(lastWord)?.getOrNull(0).let {
            nameInitials += it?.toString().orEmpty()
        }
    }
    return nameInitials.uppercase()
}