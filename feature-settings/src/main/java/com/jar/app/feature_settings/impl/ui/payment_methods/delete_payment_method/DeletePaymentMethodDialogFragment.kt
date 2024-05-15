package com.jar.app.feature_settings.impl.ui.payment_methods.delete_payment_method

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.orFalse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_settings.databinding.FragmentDialogDeletePaymentMethodBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.app.feature_settings.domain.event.PaymentMethodsAlteredEvent
import com.jar.app.feature_settings.domain.model.DeleteCard
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_settings.util.SettingsConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DeletePaymentMethodDialogFragment :
    BaseDialogFragment<FragmentDialogDeletePaymentMethodBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogDeletePaymentMethodBinding
        get() = FragmentDialogDeletePaymentMethodBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private val args by navArgs<DeletePaymentMethodDialogFragmentArgs>()

    private val viewModelProvider by viewModels<DeletePaymentMethodViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
    }

    private fun setupUI() {
        if (args.source == SettingsConstants.PaymentMethodsPosition.UPI) {
            analyticsHandler.postEvent(SettingsEventKey.Shown_Delete_DeleteUPIPopUp)
            binding.tvHeader.text =
                getCustomString(SettingsMR.strings.feature_settings_delete_upi_des)
        } else {
            analyticsHandler.postEvent(SettingsEventKey.Shown_Delete_DeleteCardPopUp)
            binding.tvHeader.text =
                getCustomString(SettingsMR.strings.feature_settings_delete_card_des)
        }

        binding.tvAutoPayAlert.isVisible = args.isAutopay.orFalse()
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            if (args.source == SettingsConstants.PaymentMethodsPosition.UPI) {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Cancel_DeleteUPIPopUp)
            } else {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Cancel_DeleteCardPopUp)
            }
            dismissAllowingStateLoss()
        }

        binding.btnDelete.setDebounceClickListener {
            if (args.source == SettingsConstants.PaymentMethodsPosition.UPI) {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Delete_DeleteUPIPopUp)
                viewModel.deleteUpiId(args.paymentMethodId)
            } else {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Delete_DeleteCardPopUp)
                viewModel.deleteCard(
                    DeleteCard(cardToken = args.paymentMethodId)
                )
            }
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.deleteUpiIdLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        EventBus.getDefault()
                            .post(PaymentMethodsAlteredEvent(SettingsConstants.PaymentMethodsPosition.UPI))
                        dismissAllowingStateLoss()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.deleteCardLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        EventBus.getDefault()
                            .post(PaymentMethodsAlteredEvent(SettingsConstants.PaymentMethodsPosition.CARDS))
                        dismissAllowingStateLoss()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        EventBus.getDefault()
                            .post(PaymentMethodsAlteredEvent(SettingsConstants.PaymentMethodsPosition.CARDS))
                        dismissAllowingStateLoss()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }
}