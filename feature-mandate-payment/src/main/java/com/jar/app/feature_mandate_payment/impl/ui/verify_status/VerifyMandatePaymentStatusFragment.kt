package com.jar.app.feature_mandate_payment.impl.ui.verify_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.isFragmentInBackStack
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentFragmentVerifyStatusBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandatePaymentResultFromSDK
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.PaymentPageHeaderDetail
import com.jar.app.feature_mandate_payments_common.shared.util.MandatePaymentCommonConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class VerifyMandatePaymentStatusFragment :
    BaseFragment<FeatureMandatePaymentFragmentVerifyStatusBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<VerifyMandatePaymentStatusFragmentArgs>()

    private val mandatePaymentResultFromSDK by lazy {
        val decoded = decodeUrl(args.mandatePaymentResultFromSDK)
        serializer.decodeFromString<MandatePaymentResultFromSDK>(decoded)
    }

    private val paymentPageHeaderDetails by lazy {
        val decoded = decodeUrl(args.paymentPageHeaderDetails)
        serializer.decodeFromString<PaymentPageHeaderDetail>(decoded)
    }

    private val appSelectionFragmentId by lazy {
        args.appSelectionFragmentId
    }

    private val viewModelProvider by viewModels<VerifyMandatePaymentStatusFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureMandatePaymentFragmentVerifyStatusBinding
        get() = FeatureMandatePaymentFragmentVerifyStatusBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchMandatePaymentStatus(mandatePaymentResultFromSDK)
    }

    private fun setupUI() {
        paymentPageHeaderDetails.toolbarIcon?.let {
            binding.ivToolbarIcon.setImageResource(it)
        }
        binding.tvToolbarHeader.text = paymentPageHeaderDetails.toolbarHeader
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.mandatePaymentStatusLiveData.collect(
                    onSuccess = {
                        if (findNavController().isFragmentInBackStack(appSelectionFragmentId))
                            findNavController().getBackStackEntry(appSelectionFragmentId)
                                .savedStateHandle[MandatePaymentCommonConstants.MANDATE_PAYMENT_STATUS_FROM_API] =
                                it
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }
}