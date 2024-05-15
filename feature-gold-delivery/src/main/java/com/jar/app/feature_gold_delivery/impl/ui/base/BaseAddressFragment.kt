package com.jar.app.feature_gold_delivery.impl.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.hideKeyboard
import com.jar.app.base.util.isFormValid
import com.jar.app.base.util.textChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentAddAddressBinding
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.PinCodeEligibility
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

internal abstract class BaseAddressFragment : BaseFragment<FragmentAddAddressBinding>() {

    @Inject
    lateinit var phoneNumberUtil: PhoneNumberUtil

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddAddressBinding
        get() = FragmentAddAddressBinding::inflate

    private val viewModelProvider by viewModels<BaseAddressFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    internal val baseAddressFragmentViewModel by lazy {
        viewModelProvider.getInstance()
    }
    protected var snackbar: Snackbar? = null

    private var isPinCodeValid = false

    abstract fun onValidFormSubmit(address: Address)

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
        observeLiveData()
    }

    private fun setupListeners() {
        binding.etPinCode.textChanges()
            .debounce(300)
            .onEach {
                isPinCodeValid = false
                binding.btnSubmitDetails.setDisabled(true)

                binding.tvPinCodeEligibilityText.isVisible = false
                binding.clPinCode.setBackgroundResource(com.jar.app.core_ui.R.drawable.core_ui_rounded_black_bg_16dp)
                if (it?.length == 6) {
                    binding.btnCheck.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.jar.app.core_ui.R.color.color_58DDC8
                        )
                    )
                    binding.btnCheck.isVisible = true
                    binding.btnCheck.isEnabled = true
                    binding.btnClear.isVisible = false
                } else {
                    binding.btnCheck.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            com.jar.app.core_ui.R.color.color_58DDC8_30
                        )
                    )
                    binding.btnCheck.isEnabled = false
                    binding.btnClear.isVisible = false
                    binding.btnCheck.isVisible = true
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

        binding.btnClear.setDebounceClickListener {
            binding.etPinCode.showKeyboard()
            binding.etPinCode.requestFocus()
            binding.etPinCode.setText("")
            binding.btnClear.isVisible = false
            binding.btnCheck.isVisible = true
            binding.tvPinCodeEligibilityText.isVisible = false
            binding.btnSubmitDetails.setDisabled(true)
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
                GoldDeliveryConstants.DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN + binding.etNumber.text.toString(),
                binding.etPinCode.text.toString(),
                binding.etStreetAddressOne.text.toString(),
                binding.etStreetAddressTwo.text.toString(),
                binding.etCity.text.toString(),
                binding.etState.text.toString()
            )

            onValidFormSubmit(data)
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                baseAddressFragmentViewModel.validatePinCodeLiveData.collectUnwrapped(
                    onSuccess = {
                        val it = it.data
                        it ?: return@collectUnwrapped
                        when (it.getEligibilityStatus()) {
                            PinCodeEligibility.DELIVERABLE -> {
                                isPinCodeValid = true

                                binding.btnSubmitDetails.setDisabled(false)
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

                                binding.btnCheck.isVisible = false
                                binding.btnClear.isVisible = true
                            }

                            PinCodeEligibility.NOT_DELIVERABLE -> {
                                isPinCodeValid = false


                                binding.btnCheck.isVisible = false
                                binding.btnClear.isVisible = true

                                binding.btnSubmitDetails.setDisabled(true)
                                binding.etCity.text?.clear()
                                binding.etState.text?.clear()
                                binding.clPinCode.setBackgroundResource(R.drawable.bg_error_pin_code)
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
                    })
            }
        }
    }

    protected fun getParsedNumber(number: String?): String {
        return phoneNumberUtil.parse(
            number,
            GoldDeliveryConstants.REGION_CODE
        ).nationalNumber.toString()
    }

}