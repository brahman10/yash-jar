package com.jar.app.feature_kyc.impl.data

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_kyc.shared.domain.model.KycStatus
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.Lazy
import javax.inject.Inject

internal class KycApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>,
    private val serializer: Serializer,
    private val fragmentActivity: FragmentActivity
) : KycApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    override fun openKYC(userKycStatus: UserKycStatus, fromScreen: String) {
        if ((userKycStatus.kycStatus == null || fromScreen == BaseConstants.KycFromScreen.SELL_GOLD || fromScreen == BaseConstants.KycFromScreen.WINNINGS)
            && !userKycStatus.isVerified()
            && fromScreen != BaseConstants.KycFromScreen.PROFILE
        ) { //Open Prompt if Sell Gold/Winnings
            val encoded = encodeUrl(serializer.encodeToString(userKycStatus))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/kycPromptDialogFragment/$encoded/$fromScreen"),
                getNavOptions(shouldAnimate = true)
            )
        } else if (userKycStatus.kycStatus.isNullOrEmpty()) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/kycVerification/$fromScreen"),
                getNavOptions(shouldAnimate = true)
            )
        } else if (userKycStatus.isVerified()) {
            navController.navigate(
                Uri.parse("android-app://com.jar.app/kycDetails/$fromScreen"),
                getNavOptions(shouldAnimate = true)
            )
        } else {
            val kycStatus = KycStatus(
                title = userKycStatus.kycScreenData?.title,
                description = userKycStatus.kycScreenData?.desc,
                shareMsg = userKycStatus.kycScreenData?.contactShareMsg,
                verificationStatus = userKycStatus.kycStatus,
                shouldTryAgain = userKycStatus.kycScreenData?.shouldTryAgain,
                allRetryExhausted = userKycStatus.kycScreenData?.allRetryExhausted,
                isFromFlow = false
            )
            val encoded = encodeUrl(serializer.encodeToString(kycStatus))
            navController.navigate(
                Uri.parse("android-app://com.jar.app/kycVerificationStatusFragment/$encoded/$fromScreen"),
                getNavOptions(shouldAnimate = true)
            )
        }
    }

    override fun initiateUserIdVerification(
        fromScreen: String,
        shouldShowOnlyPan: Boolean,
        onKycFlowExecution: (String) -> Unit
    ) {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/kycVerification/$fromScreen"),
            getNavOptions(shouldAnimate = true)
        )

        navController.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(KycConstants.KYC_FLOW_STATE)
            ?.observe(fragmentActivity) {
                onKycFlowExecution.invoke(it)
            }

    }

}