package com.jar.app.core_base.util

import android.util.Base64
import com.jar.app.core_base.shared.CoreBaseBuildKonfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

actual class EncryptionUtil {

    actual suspend fun encrypt(stringToBeEncrypted: String): String {
        return withContext(Dispatchers.IO) {
            val ivParameterSpec = IvParameterSpec(Base64.decode(CoreBaseBuildKonfig.INIT_VECTOR, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(CoreBaseBuildKonfig.SECRET_KEY.toCharArray(), Base64.decode(CoreBaseBuildKonfig.SALT, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            Base64.encodeToString(cipher.doFinal(stringToBeEncrypted.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        }
    }

    actual suspend fun decrypt(stringToBeDecrypted: String): String {
        return withContext(Dispatchers.IO) {
            val ivParameterSpec = IvParameterSpec(Base64.decode(CoreBaseBuildKonfig.INIT_VECTOR, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(CoreBaseBuildKonfig.SECRET_KEY.toCharArray(), Base64.decode(CoreBaseBuildKonfig.SALT, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            String(cipher.doFinal(Base64.decode(stringToBeDecrypted, Base64.DEFAULT)))
        }
    }
}