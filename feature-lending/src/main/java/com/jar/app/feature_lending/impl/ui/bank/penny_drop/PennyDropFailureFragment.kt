package com.jar.app.feature_lending.impl.ui.bank.penny_drop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_lending.databinding.FragmentPennyDropFailureBinding
import com.jar.app.feature_lending.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class PennyDropFailureFragment : BaseFragment<FragmentPennyDropFailureBinding>() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPennyDropFailureBinding
        get() = FragmentPennyDropFailureBinding::inflate

    override fun setupAppBar() {
        binding.toolBar.tvTitle.text = getString(com.jar.app.feature_lending.shared.R.string.feature_lending_back_to_home)
        binding.toolBar.btnNeedHelp.isInvisible = false
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
    }

    private fun setupListeners() {
        binding.toolBar.btnNeedHelp.setDebounceClickListener {
            popBackStack()
        }

        binding.btnContactUs.setDebounceClickListener {
            val message = getCustomStringFormatted(
                MR.strings.feature_lending_jar_ready_cash_need_help_template,
                getCustomString(MR.strings.feature_lending_i_need_help_regarding_choose_ammount),
                prefs.getUserName().orEmpty(),
                prefs.getUserPhoneNumber().orEmpty()
            )
            requireContext().openWhatsapp(remoteConfigApi.getWhatsappNumber(), message)
        }
    }
}