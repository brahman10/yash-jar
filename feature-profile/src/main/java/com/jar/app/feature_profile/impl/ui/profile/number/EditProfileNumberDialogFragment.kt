package com.jar.app.feature_profile.impl.ui.profile.number

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.textChanges
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.FragmentDialogEditProfileNumberBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_profile.ui.EditProfileNumberViewModel
import com.jar.app.feature_profile.util.Constants
import com.jar.app.feature_user_api.domain.model.PhoneNumberWithCountryCode
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileNumberDialogFragment : BaseDialogFragment<FragmentDialogEditProfileNumberBinding>(),
    BaseResources {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogEditProfileNumberBinding
        get() = FragmentDialogEditProfileNumberBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(
            isCancellable = false,
            shouldShowFullScreen = true
        )

    private var phoneNumberWithCountryCode: PhoneNumberWithCountryCode? = null

    private val viewModelProvider by viewModels<EditProfileNumberViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    companion object {
        const val CAN_CLEAR = "CAN_CLEAR"
    }

    override fun setup() {
        initClickListeners()
        observeLiveData()
        binding.etNumber.showKeyboard()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_EditNumber_PhoneNumberPopUp)
    }

    private fun initClickListeners() {
        binding.tvPrivacyPolicy.setDebounceClickListener {
            openUrlInChromeTab(
                remoteConfigApi.getPrivacyPolicyUrl(),
                getCustomString(com.jar.app.feature_settings.shared.SettingsMR.strings.feature_settings_privacy_policy),
                true
            )
        }

        binding.tvTnC.setDebounceClickListener {
            openUrlInChromeTab(
                remoteConfigApi.getTermsAndConditionsUrl(),
                getCustomString(com.jar.app.feature_settings.shared.SettingsMR.strings.feature_settings_terms_and_conditions),
                true
            )
        }

        binding.etNumber.textChanges()
            .debounce(100)
            .onEach {
                toggleInputError(isError = verifyNumber(it))
            }
            .launchIn(uiScope)

        binding.btnGetOtp?.setOnTouchListener { v: View, _ ->
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_GetOTP_PhoneNumberPopUp, mapOf(
                    ProfileEventKey.Props.Enabled to binding.btnGetOtp.isEnabled
                )
            )
            false
        }

        binding.btnGetOtp.setDebounceClickListener {
            val number = binding.etNumber.text?.toString()

            if (number.isNullOrBlank() || number.trim().length.orZero() < 10) {
                toggleInputError(true, getCustomString(MR.strings.feature_profile_please_enter_a_valid_mobile))
                return@setDebounceClickListener
            }

            phoneNumberWithCountryCode =
                PhoneNumberWithCountryCode(number, Constants.DEFAULT_COUNTRY_CODE)

            viewModel.updatePhoneNumber(phoneNumberWithCountryCode!!)
        }

        binding.tvCancel.setDebounceClickListener {
            dismiss()
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Cancel_PhoneNumberPopUp)
        }

        binding.ivStatus.setDebounceClickListener {
            if(binding.ivStatus.tag == CAN_CLEAR) {
                binding.etNumber.setText("")
            }
        }

        binding.etNumber.setDebounceClickListener {
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_EnterNumber_PhoneNumberPopUp)
        }
    }

    private fun observeLiveData() {
        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.etNumber.setText(it.getPhoneNumberWithoutCountryCode())
                binding.etNumber.setSelection(binding.etNumber.text?.length.orZero())
                toggleInputError(isError = verifyNumber(it.getPhoneNumberWithoutCountryCode()))
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updatePhoneNumberLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateTo(
                            EditProfileNumberDialogFragmentDirections.actionEditProfileNumberDialogFragmentToEditProfileVerifyOtpFragment(
                                phoneNumberWithCountryCode!!,
                                it
                            )
                        )
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

    }

    private fun toggleInputError(isError: Boolean = false, errorMsg: String = "") {
        binding.tvNumberAlreadyExists.isInvisible = isError.not()
        binding.tvNumberAlreadyExists.text = errorMsg
        binding.clNumber.setBackgroundResource(
            if (isError && !binding.etNumber.text.isNullOrEmpty())
                R.drawable.feature_profile_et_border_red
            else
                com.jar.app.core_ui.R.drawable.bg_edit_text_rounded_corner_white
        )
        if(isError) {
            binding.ivStatus.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.feature_profile_ic_close_light
                )
            )
        }
        binding.btnGetOtp.isEnabled = isError.not()
        binding.ivStatus.tag = if (isError) CAN_CLEAR else ""
        binding.ivStatus.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (isError) R.drawable.feature_profile_ic_close_light else R.drawable.feature_profile_ic_tick_green
            )
        )
        if(isError && errorMsg.isNotEmpty()) {
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Shown_Error_PhoneNumberPopUp, mapOf(
                    ProfileEventKey.Props.ErrorMsg to errorMsg
                )
            )
        }
    }

    private fun verifyNumber(number: CharSequence?): Boolean {
        binding.ivStatus.isVisible = number?.length.orZero() != 0
        return when {
            userLiveData.value?.getPhoneNumberWithoutCountryCode().orEmpty() == number?.toString().orEmpty() -> true
            number?.length.orZero() == 10 -> false
            else -> true
        }
    }
}