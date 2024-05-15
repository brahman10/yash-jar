package com.jar.app.core_utils.data

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricUtil @Inject constructor(@ApplicationContext private val context: Context) {

    private val biometricManager = BiometricManager.from(context)

    private fun canUseBiometric(): Boolean {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    fun authenticate(
        activity: WeakReference<FragmentActivity>,
        title: String,
        authenticationListener: AuthenticationListener
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .setConfirmationRequired(false)
            .build()

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                authenticationListener.onAuthFailed(errString)
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                authenticationListener.onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                authenticationListener.onAuthFailed()
            }
        }
        when (canUseBiometric()) {
            true -> {
                val biometricPrompt = activity.get()
                    ?.let { BiometricPrompt(it, ContextCompat.getMainExecutor(it), callback) }
                biometricPrompt?.authenticate(promptInfo)
            }
            false -> {
                authenticationListener.onAuthFailed()
            }
        }
    }

    fun authenticateInFragment(
        fragment: WeakReference<Fragment>,
        title: String,
        authenticationListener: AuthenticationListener
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .setConfirmationRequired(false)
            .build()

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)
                authenticationListener.onAuthFailed(errString)
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                authenticationListener.onAuthSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                authenticationListener.onAuthFailed()
            }
        }
        when (canUseBiometric()) {
            true -> {
                val biometricPrompt = fragment.get()
                    ?.let {
                        BiometricPrompt(
                            it,
                            ContextCompat.getMainExecutor(it.requireContext()),
                            callback
                        )
                    }
                biometricPrompt?.authenticate(promptInfo)
            }
            false -> {
                authenticationListener.onAuthFailed()
            }
        }
    }

    interface AuthenticationListener {
        fun onAuthSuccess()
        fun onAuthFailed(reason: CharSequence? = null)
    }
}