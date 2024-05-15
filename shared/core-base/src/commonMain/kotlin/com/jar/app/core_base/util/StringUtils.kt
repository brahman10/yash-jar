package com.jar.app.core_base.util

object StringUtils {
    fun isValidPhoneNumber(phnNumber: String?) : Boolean {
        phnNumber?.let {
             return phnNumber.isNotBlank() && phnNumber.length == 10 && phnNumber.all { it.isDigit() } && phnNumber[0].digitToInt() > 5 && phnNumber.toCharArray().distinct().count() != 1
        }?: run {
            return false
        }

    }

    fun isValidIndianPhoneNumberWithoutExtension(phoneNumber: String, extension: Int): Boolean {
        return phoneNumber.length == 10 && extension == 91
    }

    fun asInitials(str: String, limit: Int = 2, skipSpecialChars: Boolean = false): String {
        val builder = StringBuilder()
        str.trim().split(" ").filter {
            it.isNotEmpty()
        }.joinTo(
            buffer = builder,
            separator = "",
            transform = { s ->
                if (skipSpecialChars) {
                    s.firstOrNull { it.isLetter() }?.uppercase().toString()
                } else {
                    s.first().uppercase()
                }
            },
            limit = limit
        )
        return builder.toString()
    }

    fun hasNoInitial(str: String): Boolean{
        return str.startsWith("+") || str.getOrNull(0)?.isLetter() == false
    }

}