package com.jar.app.feature_promo_code.impl.ui.promo_code_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doBeforeTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.feature_promo_code.databinding.FragmentPromoCodeDialogBinding
import com.jar.app.feature_promo_code.shared.MR
import com.jar.app.feature_promo_code.shared.domain.event.PromoCodeEvents
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PromoCodeDialogFragment : BaseDialogFragment<FragmentPromoCodeDialogBinding>() {

    companion object{
        const val MAX_PROMO_CODE_LENGTH = 16
    }


    private val viewModelProvider by viewModels<PromoCodeDialogViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPromoCodeDialogBinding
        get() = FragmentPromoCodeDialogBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    override fun setup() {
        viewModel.postAnalyticsEvent(PromoCodeEvents.Shown_PromoDialogScreen)
        initClickListeners()
        observeFlows()
        binding.etPromoCode.showKeyboard()
    }
    private fun observeFlows() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.applyPromoCodeFlow.collect(
                    onLoading = {
                        showProgressBar()
                        binding.tvError.isVisible = false
                    },
                    onSuccess = {
                        binding.tvError.isVisible = false
                        dismissProgressBar()
                        it?.orderId?.let { orderId->
                        navigateTo(
                            PromoCodeDialogFragmentDirections.actionPromoCodeDialogFragmentToPromoCodeStatusFragment(
                                orderId
                            )
                        )

                        }
                    },
                    onError = {error,_->
                        dismissProgressBar()
                       binding.tvError.text = error
                        binding.tvError.isVisible = true
                    }
                )
            }
        }




    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            viewModel.postAnalyticsEvent(PromoCodeEvents.Clicked_Cancel_PromoDialogScreen)
            dismiss()
        }

        binding.btnSubmit.setDebounceClickListener {
            viewModel.postAnalyticsEvent(PromoCodeEvents.Clicked_Submit_PromoDialogScreen)
            val promoCode = binding.etPromoCode.text?.trim()
            when{
                promoCode.isNullOrBlank()->{
                    binding.tvError.isVisible = true
                    binding.tvError.text = getCustomString( MR.strings.empty_promo_code_error)
                }
                promoCode.length != MAX_PROMO_CODE_LENGTH ->{
                    binding.tvError.isVisible = true
                    binding.tvError.text = getCustomString( MR.strings.invalid_promo_code_length_error)
                }
                else->{
                    viewModel.applyPromoCode(promoCode.toString())
                    binding.etPromoCode.clearFocus()
                }

            }
        }
        binding.etPromoCode.doBeforeTextChanged { _, _, _, _ ->
            if(binding.tvError.isVisible) {
                binding.tvError.isVisible = false
            }
        }

        binding.ivStatus.setDebounceClickListener {
            binding.etPromoCode.setText("")
        }
    }


}