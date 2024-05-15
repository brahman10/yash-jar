package com.jar.app.feature_settings.impl.ui.payment_methods.dialog_add_upi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_settings.R
import com.jar.app.feature_settings.databinding.FragmentAddUpiBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.app.feature_settings.domain.event.PaymentMethodsAlteredEvent
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_settings.util.SettingsConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class AddUpiFragment : BaseDialogFragment<FragmentAddUpiBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddUpiBinding
        get() = FragmentAddUpiBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private var vpaAdapter: VpaChipsAdapter? = null

    private val viewModelProvider by viewModels<AddUpiViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        const val CAN_CLEAR = "CAN_CLEAR"
    }

    override fun setup() {
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
        analyticsHandler.postEvent(SettingsEventKey.Shown_EnterUPI_AddUPIPopUp)
    }

    private fun getData() {
        viewModel.fetchVpaChips()
    }

    private fun setupUI() {
        binding.etVPA.showKeyboard()
        resetInputState()
        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )

        vpaAdapter = VpaChipsAdapter {
            val text = binding.etVPA.text.toString()
            binding.etVPA.setText(text.replaceAfter("@", it.removePrefix("@"), text + it))
            binding.etVPA.setSelection(binding.etVPA.text?.length.orZero())
        }
        binding.btnSave.setDisabled(true)
        binding.rvVpaChip.adapter = vpaAdapter
        binding.rvVpaChip.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupListeners() {
        binding.etVPA.textChanges()
            .debounce(100)
            .onEach {
                when {
                    it.isNullOrEmpty() -> {
                        resetInputState()
                    }

                    it.contains("@") -> {
                        it.split("@").let { list ->
                            viewModel.vpaSearch(list.last())
                        }
                        setInputStatus("", true)
                    }

                    else -> {
                        viewModel.vpaSearch(null)
                        setInputStatus("", false)
                    }
                }
            }
            .launchIn(uiScope)

        binding.btnSave.setDebounceClickListener {
            val text = binding.etVPA.text

            if (text?.toString().isNullOrBlank().not().orFalse() && text.isValidUpiAddress()) {
                analyticsHandler.postEvent(
                    SettingsEventKey.Clicked_Verify_AddUPIPopUp, mapOf(
                        SettingsEventKey.id to text?.toString().orEmpty()
                    )
                )
                viewModel.verifyUpiAddress(binding.etVPA.text.toString())
            } else
                setInputStatus(
                    getCustomStringFormatted(SettingsMR.strings.feature_settings_invalid_upi_id),
                    false
                )
        }

        binding.tvCancel.setDebounceClickListener {
            analyticsHandler.postEvent(SettingsEventKey.Clicked_Cancel_AddUPIPopUp)
            dismiss()
        }

        binding.ivStatus.setDebounceClickListener {
            if (binding.ivStatus.tag == CAN_CLEAR) {
                binding.etVPA.setText("")
            }
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                viewModel.verifyUpiLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it.valid) {
                            viewModel.addVpa(it.vpa)
                        } else {
                            analyticsHandler.postEvent(SettingsEventKey.Shown_Error_AddUPIPopUp)
                            setInputStatus(
                                getCustomString(SettingsMR.strings.feature_settings_invalid_upi_enter_correct),
                                false
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.vpaChipsLiveData.collect(
                    onSuccess = {
                        dismissProgressBar()
                        vpaAdapter?.submitList(it.vpaChips)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.searchVpaChipsLiveData.collect(
                    onSuccess = {
                        vpaAdapter?.submitList(it.vpaChips)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.addNewVPALiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            binding.clContent.slideToRevealNew(
                                viewToReveal = binding.clSuccess,
                                onAnimationEnd = {
                                    binding.lottieView.playAnimation()
                                    binding.tvSuccessDes.text = getCustomStringFormatted(
                                        SettingsMR.strings.feature_settings_upi_id_added_successfully,
                                        it.vpaHandle
                                    )
                                    uiScope.launch {
                                        analyticsHandler.postEvent(SettingsEventKey.Shown_Success_AddUPI_PopUp)
                                        EventBus.getDefault()
                                            .post(
                                                PaymentMethodsAlteredEvent(
                                                    SettingsConstants.PaymentMethodsPosition.UPI,
                                                    it.id
                                                )
                                            )
                                        delay(3000)
                                        dismissAllowingStateLoss()
                                    }
                                }
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun handleButtonState(shouldEnable: Boolean) {
        binding.btnSave.setDisabled(shouldEnable.not())
        binding.ivStatus.setImageResource(if (shouldEnable) R.drawable.feature_settings_ic_tick_green else R.drawable.feature_settings_ic_circle_close)
    }

    private fun setInputStatus(errorMsg: String, isValid: Boolean) {
        binding.ivStatus.isVisible = true
        binding.clAddUpi.setBackgroundResource(if (isValid) R.drawable.feature_settings_bg_dark_rounded_16dp else R.drawable.feature_settings_bg_error_upi)
        binding.tvError.isInvisible = isValid
        binding.tvError.text = errorMsg
        toggleRecommendations(isValid)
        handleButtonState(isValid)
        binding.ivStatus.tag = if (isValid) "" else CAN_CLEAR
    }

    private fun resetInputState() {
        binding.clAddUpi.setBackgroundResource(R.drawable.feature_settings_bg_dark_rounded_16dp)
        binding.tvError.isInvisible = true
        binding.tvError.text = ""
        toggleRecommendations()
        handleButtonState(false)
        binding.ivStatus.tag = ""
        binding.ivStatus.isVisible = false
    }

    private fun toggleRecommendations(shouldShow: Boolean = false) {
        binding.rvVpaChip.isInvisible = shouldShow.not()
    }
}