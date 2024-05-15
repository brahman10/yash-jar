package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_bottomsheet_pincode

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.hideKeyboard

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.util.textChanges
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard

import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartBottomsheetPincodeBinding
import com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.StoreCartFragmentViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_user_api.domain.model.PinCodeEligibility
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CartBottomSheetPincodeFragment :
    BaseBottomSheetDialogFragment<FragmentCartBottomsheetPincodeBinding>() {

    private val viewModelProvider by hiltNavGraphViewModels<StoreCartFragmentViewModelAndroid>(R.id.feature_delivery_navigation)
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartBottomsheetPincodeBinding
        get() = FragmentCartBottomsheetPincodeBinding::inflate

    override val bottomSheetConfig = DEFAULT_CONFIG

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun postClickEvent(clickTypeValue: String, eventName: String, eventValue: String) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, mapOf(
                GoldDeliveryConstants.AnalyticsKeys.Click_type to clickTypeValue,
                eventName to eventValue
            )
        )
    }

    private fun setupListeners() {
        binding.etPinCode.textChanges()
            .debounce(300)
            .onEach {
                binding.clPinCode.setBackgroundResource(com.jar.app.core_ui.R.drawable.round_black_bg_16dp)
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
                    postClickEvent(
                        GoldDeliveryConstants.AnalyticsKeys.pincode_typed,
                        GoldDeliveryConstants.AnalyticsKeys.pincode_entered,
                        it?.toString().orEmpty()
                    )
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
                binding.tvPinCodeEligibilityText.isVisible = false
            }
            .launchIn(uiScope)
        binding.btnCheck.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            requireContext().hideKeyboard(binding.etPinCode)
            if (!pinCode.isNullOrBlank() && pinCode.length == 6) {
                viewModel.validatePinCode(pinCode.toString())
            }
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.pincode_check_clicked,
                GoldDeliveryConstants.AnalyticsKeys.pincode_entered,
                pinCode?.toString() ?: ""
            )
        }
        binding.btnClose.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            findNavController().navigateUp()
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.pincode_typed,
                GoldDeliveryConstants.AnalyticsKeys.pincode_entered,
                pinCode?.toString() ?: ""
            )
        }
        binding.btnClear.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            binding.etPinCode.showKeyboard()
            binding.etPinCode.requestFocus()
            binding.etPinCode.setText("")
            binding.btnClear.isVisible = false
            binding.btnCheck.isVisible = true
            binding.tvPinCodeEligibilityText.isVisible = false
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.pincode_cleared,
                GoldDeliveryConstants.AnalyticsKeys.pincode_entered,
                pinCode?.toString() ?: ""
            )
        }
    }

    private fun observeLiveData() {
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.validatePinCodeLiveData.collectUnwrapped(
            onSuccess = {
                val it = it.data
                it ?: return@collectUnwrapped
                when (it.getEligibilityStatus()) {
                    PinCodeEligibility.DELIVERABLE -> {
                        binding.clPinCode.setBackgroundResource(com.jar.app.core_ui.R.drawable.round_black_bg_16dp)
                        binding.tvPinCodeEligibilityText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_ACA1D3
                            )
                        )
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            com.jar.app.core_ui.R.drawable.core_ui_ic_green_tick
                        )
                        binding.tvPinCodeEligibilityText.setCompoundDrawablesWithIntrinsicBounds(
                            drawable,
                            null,
                            null,
                            null
                        )
                        binding.tvPinCodeEligibilityText.text =
                            getString(R.string.city_state_is_serviceable, it.city)
                        binding.tvPinCodeEligibilityText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_58DDC8
                            )
                        )

                        binding.tvPinCodeEligibilityText.visibility = View.VISIBLE
                        binding.btnCheck.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_58DDC8
                            )
                        )

                        binding.btnCheck.isVisible = false
                        binding.btnClear.isVisible = true
                        postClickEvent(
                            GoldDeliveryConstants.AnalyticsKeys.pincode_serviceable,
                            GoldDeliveryConstants.AnalyticsKeys.pincode_entered,
                            it.pinCode ?: ""
                        )
                        findNavController().navigateUp()
                    }

                    PinCodeEligibility.NOT_DELIVERABLE -> {
                        val drawable = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.close_circle_red_outline
                        )
                        binding.tvPinCodeEligibilityText.setCompoundDrawablesWithIntrinsicBounds(
                            drawable,
                            null,
                            null,
                            null
                        )
                        postClickEvent(
                            GoldDeliveryConstants.AnalyticsKeys.pincode_not_serviceable,
                            GoldDeliveryConstants.AnalyticsKeys.pincode_entered,
                            it.pinCode ?: ""
                        )
                        binding.clPinCode.setBackgroundResource(R.drawable.bg_error_pin_code)
                        binding.tvPinCodeEligibilityText.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_EB6A6E
                            )
                        )
                        if (TextUtils.isEmpty(it.city)) {
                            binding.tvPinCodeEligibilityText.text =
                                getString(R.string.invalid_pincode)
                        } else {
                            binding.tvPinCodeEligibilityText.text =
                                getString(R.string.city_state_is_unserviceable, it.city, it.state)
                        }
                        binding.btnCheck.isVisible = false
                        binding.btnClear.isVisible = true
                        binding.tvPinCodeEligibilityText.visibility = View.VISIBLE
                        binding.btnCheck.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_58DDC8_opacity_30
                            )
                        )
                    }
                }
            })}}
    }

    private fun getData() {
    }

}
