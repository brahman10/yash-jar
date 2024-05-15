package com.jar.app.feature_lending.impl.ui.bank.confirm_bank

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingBottomsheetChangeYourBankBinding
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
internal class ChangeYourBankBottomSheet :
    BaseBottomSheetDialogFragment<FeatureLendingBottomsheetChangeYourBankBinding>() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingBottomsheetChangeYourBankBinding
        get() = FeatureLendingBottomsheetChangeYourBankBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false)

    override fun setup() {
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        analyticsApi.postEvent(LendingEventKeyV2.Lending_RepeatWBankDetailScreenBSShown)
    }

    private fun initClickListeners() {
        binding.btnClose.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Lending_RepeatWBankDetailScreenBSClicked,
            mapOf(LendingEventKeyV2.button_type to "Close")
            )
            dismissAllowingStateLoss()
        }
        binding.btnContactUs.setDebounceClickListener {
            analyticsApi.postEvent(LendingEventKeyV2.Lending_RepeatWBankDetailScreenBSClicked,
                mapOf(LendingEventKeyV2.button_type to "ContactUs")
            )
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_want_to_change_my_account),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), message)
        }
    }
}