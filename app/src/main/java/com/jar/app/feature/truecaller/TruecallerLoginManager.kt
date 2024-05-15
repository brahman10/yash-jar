package com.jar.app.feature.truecaller

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.jar.app.R
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.isPackageInstalled
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_remote_config.RemoteConfigApi
import com.truecaller.android.sdk.*
import timber.log.Timber
import java.lang.ref.WeakReference

class TruecallerLoginManager constructor(
    private val context: Context,
    private val remoteConfigApi: RemoteConfigApi,
) {

    private var truecallerSdk: TruecallerSDK? = null

    private var listener: TruecallerLoginListener? = null

    private val sdkCallback = object : ITrueCallback {
        override fun onFailureProfileShared(error: TrueError) {
            Timber.d("onFailureProfileShared ${error.errorType}")
            listener?.onTruecallerLoginFailure(error.errorType)
        }

        override fun onSuccessProfileShared(profile: TrueProfile) {
            Timber.d("onSuccessProfileShared")
            listener?.onTruecallerLoginSuccess(
                profile.payload,
                profile.signature,
                profile.signatureAlgorithm
            )
        }

        override fun onVerificationRequired(error: TrueError?) {
            Timber.d("onVerificationRequired ${error?.errorType}")
            listener?.onVerificationRequired()
        }
    }

    init {
        TruecallerSDK.init(provideTrueCallerScope())
        truecallerSdk = TruecallerSDK.getInstance()
    }

    private fun provideTrueCallerScope(): TruecallerSdkScope {
        return TruecallerSdkScope.Builder(context, sdkCallback)
            .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
            .buttonColor(ContextCompat.getColor(context, com.jar.app.core_ui.R.color.color_3664FF))
            .buttonTextColor(ContextCompat.getColor(context, com.jar.app.core_ui.R.color.white))
            .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
            .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
            .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
            .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
            .privacyPolicyUrl(remoteConfigApi.getPrivacyPolicyUrl())
            .termsOfServiceUrl(remoteConfigApi.getTermsAndConditionsUrl())
            .footerType(TruecallerSdkScope.FOOTER_TYPE_CONTINUE)
            .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
            .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITHOUT_OTP)
            .build()
    }

    fun isTruecallerUsable() =
        truecallerSdk?.isUsable.orFalse() && context.isPackageInstalled(BaseConstants.TRUECALLER_PACKAGE)

    fun showTruecallerPopup(
        fragmentRef: WeakReference<Fragment>,
        listener: TruecallerLoginListener,
    ) {
        this.listener = listener
        val fragment = fragmentRef.get()
        if (isTruecallerUsable() && fragment != null) {
            truecallerSdk?.getUserProfile(fragment)
        } else {
            this.listener?.onTruecallerNotUsable()
        }
    }

    fun onActivityResult(
        activityRef: WeakReference<FragmentActivity>,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        try {
            val activity = activityRef.get()
            if (requestCode == TruecallerSDK.SHARE_PROFILE_REQUEST_CODE && activity != null) {
                truecallerSdk?.onActivityResultObtained(
                    activity,
                    requestCode,
                    resultCode,
                    data
                )
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    fun clear() {
        TruecallerSDK.clear()
        truecallerSdk = null
        listener = null
    }

    interface TruecallerLoginListener {

        fun onTruecallerLoginSuccess(payload: String, signature: String, signatureAlgorithm: String)

        fun onTruecallerLoginFailure(errorType: Int)

        fun onTruecallerNotUsable()

        fun onVerificationRequired()

    }

}