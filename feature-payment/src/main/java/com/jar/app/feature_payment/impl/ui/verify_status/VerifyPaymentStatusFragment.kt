package com.jar.app.feature_payment.impl.ui.verify_status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_one_time_payments.shared.data.model.base.OneTimePaymentResult
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.app.feature_payment.PaymentNavigationDirections
import com.jar.app.feature_payment.databinding.FragmentVerifyPaymentStatusBinding
import com.jar.app.feature_payment.impl.domain.ManualPaymentStatusFetchedEvent
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class VerifyPaymentStatusFragment : BaseFragment<FragmentVerifyPaymentStatusBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val viewModelProvider by viewModels<VerifyPaymentStatusFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<VerifyPaymentStatusFragmentArgs>()

    private val oneTimePaymentResult by lazy {
        serializer.decodeFromString<OneTimePaymentResult>(decodeUrl(args.oneTimePaymentResult))
    }

    private val paymentPageFragmentId by lazy {
        args.paymentPageFragmentId
    }

    private var rotate: RotateAnimation? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVerifyPaymentStatusBinding
        get() = FragmentVerifyPaymentStatusBinding::inflate

    override fun setupAppBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        rotate = RotateAnimation(
            0f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate?.duration = 500
        rotate?.interpolator = LinearInterpolator()
        rotate?.repeatCount = Animation.INFINITE
        binding.ivLoader.startAnimation(rotate)
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchManualPaymentStatusFlow.collect(
                    onSuccess = { response ->
                        when (response.getManualPaymentStatus()) {
                            ManualPaymentStatus.FAILURE,
                            ManualPaymentStatus.PENDING -> {
                                if(oneTimePaymentResult.isRetryAllowed.orFalse()) {
                                    oneTimePaymentResult.fetchCurrentGoldPriceResponse?.let {
                                        navigateTo(
                                            PaymentNavigationDirections.actionToTransactionFailedBottomSheet(
                                                oneTimePaymentResult,
                                                response,
                                                paymentPageFragmentId = paymentPageFragmentId
                                            )
                                        )
                                    } ?: run {
                                        EventBus.getDefault().post(ManualPaymentStatusFetchedEvent(response))
                                    }
                                } else {
                                    EventBus.getDefault().post(ManualPaymentStatusFetchedEvent(response))
                                }
                            }
                            else -> {
                                EventBus.getDefault().post(ManualPaymentStatusFetchedEvent(response))
                                popBackStack()
                            }
                        }
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        popBackStack(paymentPageFragmentId, true)
                    }
                )
            }
        }
    }

    private fun getData() {
        viewModel.fetchManualPaymentStatus(oneTimePaymentResult)
    }

    override fun onDestroyView() {
        rotate?.cancel()
        binding.ivLoader.clearAnimation()
        super.onDestroyView()
    }
}