package com.jar.app.core_base.util

expect class EncryptionUtil {

    suspend fun encrypt(stringToBeEncrypted: String): String

    suspend fun decrypt(stringToBeDecrypted: String): String
}