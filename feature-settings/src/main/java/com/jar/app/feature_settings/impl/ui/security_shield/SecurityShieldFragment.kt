package com.jar.app.feature_settings.impl.ui.security_shield

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBarWithGenericFallback
import com.jar.app.core_utils.data.BiometricUtil
import com.jar.app.feature_settings.databinding.FragmentSecurityShieldBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.app.feature_settings.shared.SettingsMR
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SecurityShieldFragment : BaseFragment<FragmentSecurityShieldBinding>(), BiometricUtil.AuthenticationListener {

    @Inject
    lateinit var biometricUtil: BiometricUtil

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModel by viewModels<SecurityShieldViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSecurityShieldBinding
        get() = FragmentSecurityShieldBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        initClickListeners()
        Glide.with(this)
            .load(BaseConstants.ImageUrlConstants.FEATURE_SETTINGS_IC_JAR_SECURITY_SHIELD)
            .fitCenter()
            .into(binding.ivSecurityShield)
        analyticsHandler.postEvent(
            SettingsEventKey.Shown_SecurityShieldScreen, mapOf(
                SettingsEventKey.currentState to
                        if (prefs.isJarShieldEnabled())
                            getCustomString(SettingsMR.strings.feature_settings_on)
                        else
                            getCustomString(SettingsMR.strings.feature_settings_off
                )
            )
        )
    }

    private fun observeLiveData() {
        viewModel.updateSecurityShieldLiveData.observe(
            viewLifecycleOwner
        ) {
            popBackStack()
        }
    }

    private fun setupUI() {
        if(prefs.isJarShieldEnabled()) {
            binding.btnAction.setText(getCustomString(SettingsMR.strings.feature_settings_disable))
        } else {
            binding.btnAction.setText(getCustomString(SettingsMR.strings.feature_settings_enable))
        }
    }

    private fun initClickListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.tvLater.setDebounceClickListener {
            analyticsHandler.postEvent(SettingsEventKey.Clicked_DoLater_SecurityShieldScreen)
            popBackStack()
        }

        binding.btnAction.setDebounceClickListener {
            if (prefs.isJarShieldEnabled()) {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Disable_SecurityShieldScreen)
            } else {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Enable_SecurityShieldScreen)
            }
            biometricUtil.authenticateInFragment(
                WeakReference(this),
                getCustomString(SettingsMR.strings.feature_settings_confirm_your_password),
                this
            )
        }
    }

    override fun onAuthSuccess() {
        //Toggle the current value as it will be changed in next step
        analyticsHandler.postEvent(
            EventKey.JarSecurityShieldToggled_SettingsScreen,
            mapOf(BaseConstants.STATE to prefs.isJarShieldEnabled().not())
        )
        viewModel.toggleJarShieldStatus()
    }

    override fun onAuthFailed(reason: CharSequence?) {
        reason?.toString().snackBarWithGenericFallback(
            binding.root,
            genericMessage = getCustomString(SettingsMR.strings.feature_settings_authentication_failed)
        )
    }
}