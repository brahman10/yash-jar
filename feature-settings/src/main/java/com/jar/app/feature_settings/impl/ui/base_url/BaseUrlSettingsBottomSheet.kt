package com.jar.app.feature_settings.impl.ui.base_url

import android.os.Process
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.orFalse
import com.jar.app.core_network.CoreNetworkBuildKonfig
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_preferences.api.RetainedPrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_settings.databinding.FragmentBaseUrlSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class BaseUrlSettingsBottomSheet :
    BaseBottomSheetDialogFragment<FragmentBaseUrlSettingsBinding>() {

    @Inject
    lateinit var pref: RetainedPrefsApi

    companion object {
        private const val BASE_URL_SUFFIX = ".myjar.app"
    }

    private var oldUrl = ""
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBaseUrlSettingsBinding
        get() = FragmentBaseUrlSettingsBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun getTheme(): Int {
        return com.jar.app.core_ui.R.style.BottomSheetDialogInput
    }

    override fun setup() {
        setupUI()
        setClickListener()
    }

    private fun setupUI() {
        val baseUrl = pref.getApiBaseUrl().ifEmpty { CoreNetworkBuildKonfig.BASE_URL_KTOR }
        val baseUrlPrefix = baseUrl.replace(BASE_URL_SUFFIX, "")
        binding.etBaseUrl.setText(baseUrlPrefix)
        binding.etBaseUrl.setSelection(baseUrlPrefix.length)
        binding.checkboxConsent.isChecked = pref.getIsAutomationEnabled()
        oldUrl = baseUrlPrefix
    }

    private fun setClickListener() {
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.btnSubmit.setDebounceClickListener {
            if (binding.etBaseUrl.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Base Url prefix could not be empty!", //ignore hard coded text. Since this is only for testing.
                    Toast.LENGTH_LONG
                ).show()
                return@setDebounceClickListener
            }
            val enteredUrl = if (binding.etBaseUrl.text?.contains(BASE_URL_SUFFIX).orFalse()) {
                binding.etBaseUrl.text?.toString()?.replace(BASE_URL_SUFFIX, "").orEmpty()
            } else {
                binding.etBaseUrl.text?.toString()
            }
            pref.setApiBaseUrl(enteredUrl + BASE_URL_SUFFIX)
            Toast.makeText(requireContext(), "Base Url Changed. Please restart.", Toast.LENGTH_LONG)
                .show()
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                dismiss()
                Runtime.getRuntime().exit(0)
            }
            pref.setIsAutomationEnabled(!pref.getIsAutomationEnabled())
        }
    }
}