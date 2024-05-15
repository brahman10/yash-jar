package com.jar.app.feature_user_api.impl.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.hideKeyboard
import com.jar.app.base.util.isFormValid
import com.jar.app.base.util.textChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_user_api.R
import com.jar.app.feature_user_api.databinding.FragmentBaseUserAddressBinding
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.PinCodeEligibility
import com.jar.app.feature_user_api.util.UserApiConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.ref.WeakReference
import javax.inject.Inject

internal abstract class BaseUserAddressFragment : BaseFragment<FragmentBaseUserAddressBinding>() {

    @Inject
    lateinit var phoneNumberUtil: PhoneNumberUtil

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBaseUserAddressBinding
        get() = FragmentBaseUserAddressBinding::inflate

    val baseAddressFragmentViewModel by viewModels<BaseUserAddressViewModel> { defaultViewModelProviderFactory }

    protected var snackbar: Snackbar? = null

    private var isPinCodeValid = false

    abstract fun onValidFormSubmit(address: Address)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
        observeLiveData()
    }

    private fun setupListeners() {
        binding.etPinCode.textChanges()
            .debounce(300)
            .onEach {
                isPinCodeValid = false
                binding.tvPinCodeEligibilityText.isVisible = false
                binding.clPinCode.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_rounded_black_bg_16dp)
                binding.btnCheck.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_58DDC8
                    )
                )
                if (it?.length == 6) {
                    baseAddressFragmentViewModel.validatePinCode(it.toString())
                }
            }
            .launchIn(uiScope)


        binding.btnCheck.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            requireContext().hideKeyboard(binding.root)
            if (!pinCode.isNullOrBlank() && pinCode.length == 6) {
                baseAddressFragmentViewModel.validatePinCode(pinCode.toString())
            } else {
                snackbar = getString(R.string.please_enter_a_valid_pincode).snackBar(binding.root)
            }
        }

        binding.btnSubmitDetails.setDebounceClickListener {
            snackbar?.dismiss()
            val isFormValid = binding.clForm.isFormValid(binding.etStreetAddressTwo.id)

            if (!isFormValid) {
                snackbar = getString(R.string.please_fill_all_the_details).snackBar(binding.root)
                return@setDebounceClickListener
            }

            if (!isPinCodeValid) {
                snackbar = getString(R.string.please_enter_a_valid_pincode).snackBar(binding.root)
                return@setDebounceClickListener
            }

            val data = Address(
                binding.etName.text.toString(),
                BaseConstants.DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN + binding.etNumber.text.toString(),
                binding.etPinCode.text.toString(),
                binding.etStreetAddressOne.text.toString(),
                binding.etStreetAddressTwo.text.toString(),
                binding.etCity.text.toString(),
                binding.etState.text.toString()
            )

            onValidFormSubmit(data)

            analyticsHandler.postEvent(
                UserApiConstants.AnalyticsKeys.ClickedSubmitDetails_GoldDeliveryScreen,
                mapOf(
                    "Gold in Gms" to baseAddressFragmentViewModel.userGoldBalance.toString(),
                    "Name" to data.name.orEmpty(),
                    "Pincode" to data.pinCode,
                    "City" to data.city,
                    "Phone Number" to data.phoneNumber.orEmpty(),
                )
            )
        }
    }

    private fun observeLiveData() {
        baseAddressFragmentViewModel.validatePinCodeLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                when (it.getEligibilityStatus()) {
                    PinCodeEligibility.DELIVERABLE -> {
                        isPinCodeValid = true
                        binding.etCity.setText(it.city)
                        binding.etState.setText(it.state)
                        binding.clPinCode.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_rounded_black_bg_16dp)
                        binding.tvPinCodeEligibilityText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_ACA1D3
                            )
                        )
                        binding.tvPinCodeEligibilityText.text = "${it.city},${it.state}"
                        binding.tvPinCodeEligibilityText.isVisible = true
                        binding.btnCheck.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_58DDC8
                            )
                        )
                    }
                    PinCodeEligibility.NOT_DELIVERABLE -> {
                        isPinCodeValid = false
                        binding.etCity.text?.clear()
                        binding.etState.text?.clear()
                        binding.clPinCode.setBackgroundResource(R.drawable.bg_pin_code_error)
                        binding.tvPinCodeEligibilityText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_EB6A6E
                            )
                        )
                        binding.tvPinCodeEligibilityText.text =
                            getString(R.string.this_area_is_not_servicable_currently)
                        binding.tvPinCodeEligibilityText.isVisible = true
                        binding.btnCheck.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_58DDC8_opacity_30
                            )
                        )
                    }
                }
            }

        )
    }

    protected fun getParsedNumber(number: String): String{
        return phoneNumberUtil.parse(number, BaseConstants.REGION_CODE).nationalNumber.toString()
    }

}