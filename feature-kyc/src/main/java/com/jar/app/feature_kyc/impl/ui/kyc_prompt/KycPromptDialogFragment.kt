package com.jar.app.feature_kyc.impl.ui.kyc_prompt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.CancelEvent
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentDialogKycPromptBinding
import com.jar.app.feature_kyc.shared.domain.model.KycStatus
import com.jar.app.feature_user_api.domain.model.UserKycStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
internal class KycPromptDialogFragment : BaseDialogFragment<FragmentDialogKycPromptBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<KycPromptDialogFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogKycPromptBinding
        get() = FragmentDialogKycPromptBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private val userKycStatus by lazy {
        serializer.decodeFromString<UserKycStatus>(decodeUrl(args.userKycStatus))
    }

    override fun setup() {
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        if (args.fromScreen == BaseConstants.KycFromScreen.SELL_GOLD) {
            binding.tvPromptDescription.text = getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_prompt_des_sell_gold)
        } else {
            binding.tvPromptDescription.text = getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_prompt_des_winnings)
        }
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            EventBus.getDefault().post(CancelEvent())
            dismissAllowingStateLoss()
        }

        binding.btnVerify.setDebounceClickListener {
            if (userKycStatus.kycStatus.isNullOrEmpty()) {
                navigateTo(
                    "android-app://com.jar.app/kycVerification/${args.fromScreen}",
                    shouldAnimate = true
                )
            } else {
                val kycStatus = KycStatus(
                    title = userKycStatus.kycScreenData?.title,
                    description = userKycStatus.kycScreenData?.desc,
                    shareMsg = userKycStatus.kycScreenData?.contactShareMsg,
                    verificationStatus = userKycStatus.kycStatus!!,
                    shouldTryAgain = userKycStatus.kycScreenData?.shouldTryAgain,
                    allRetryExhausted = userKycStatus.kycScreenData?.allRetryExhausted,
                    isFromFlow = false
                )
                val encoded = encodeUrl(serializer.encodeToString(kycStatus))
                navigateTo(
                    "android-app://com.jar.app/kycVerificationStatusFragment/$encoded/${args.fromScreen}",
                    shouldAnimate = true
                )
            }
            dismissAllowingStateLoss()
        }
    }
}