package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_complete_payment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener

import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentPaymentStatusCartBinding
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryTransactionState
import com.jar.app.feature_gold_delivery.shared.domain.model.OrderStatusAPIResponse
import com.jar.app.feature_gold_delivery.impl.helper.PaymentUIHelper
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CompletePaymentFragment : BaseFragment<FragmentPaymentStatusCartBinding>() {

    private val args by navArgs<CompletePaymentFragmentArgs>()

    private val viewModelProvider by viewModels<CompletePaymentFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPaymentStatusCartBinding
        get() = FragmentPaymentStatusCartBinding::inflate
    private var pollingJob: Job? = null


    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var serializer: Serializer

    private var goldDeliveryPlaceOrderDataRequest: GoldDeliveryPlaceOrderDataRequest? = null
    private var fetchManualPaymentStatusResponse: FetchManualPaymentStatusResponse? = null

    companion object {
        //After 30 seconds of no API response UI should switch to pending state
        const val TIMEOUT_IN_MILLIS = 30000L
        const val POLLING_ATTEMPTS = 10
        const val POLLING_INTERVAL = 5 * 1000L

        const val Refresh = "Refresh"
        const val Go_back = "Go back"
        const val Retry = "Retry"
        const val Go_to_cart = "Go to cart"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        "", false, backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    private fun initiatePayment(initiatePaymentResponse: InitiatePaymentResponse) {
        appScope.launch(dispatcherProvider.main) {
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateToPaymentScreen(it, initiatePaymentResponse.orderId)
                    },
                    onError = { message, _ ->

                    }
                )
        }
    }
    private fun buildForAnalytics(): MutableMap<String, String> {
        return mutableMapOf<String, String>(
            "PaymentProvider" to (goldDeliveryPlaceOrderDataRequest?.paymentProvider ?: "")
        )
    }

    private fun navigateToPaymentScreen(
        it: com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse? = null,
        orderId: String
    ) {
        val fetchResponse =
            encodeUrl(serializer.encodeToString(it))
        var placeOrderRequestString: String? = null
        goldDeliveryPlaceOrderDataRequest?.let {
            placeOrderRequestString =
                encodeUrl(serializer.encodeToString(goldDeliveryPlaceOrderDataRequest))
        }

        navigateTo("android-app://com.jar.app/completePaymentFrag/${orderId}/${fetchResponse}/${placeOrderRequestString}", popUpTo = R.id.deliveryStoreItemListFragment, inclusive = true)
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ShownPaymentGoldDelivery,
        )
        registerBackPressDispatcher()
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        args.placeOrderRequest?.let {
            goldDeliveryPlaceOrderDataRequest =
                serializer.decodeFromString(
                    decodeUrl(it)
                )
        }
        args.paymentResponse?.let {
            fetchManualPaymentStatusResponse =
                serializer.decodeFromString(decodeUrl(it))
        }
    }

    private fun setupListeners() {
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.placeOrderAPILiveData.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                it.data ?: return@collectUnwrapped
                initiatePayment(it.data!!)
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )}}

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.orderStatusLiveData.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                handleOrderStatus(it.data)
                dismissProgressBar()
            },
            onError = { _, message ->
                if (!TextUtils.isEmpty(message)) {
                    binding.firstContainterFirstText.text = message
                }
                pollingJob?.cancel()
                dismissProgressBar()
            }
        )}}
    }

    private fun handleOrderStatus(it: OrderStatusAPIResponse?) {
        if (it?.paymentState != null)
            handlePaymentStatus(it.paymentState!!)

        // Handle the order status

        if (it?.paymentState?.getManualPaymentStatus() == ManualPaymentStatus.PENDING) {
            handlePaymentDoneOrder(null)
            // if Payment state is in pending, then show the order state as un-initialized
            // Ideally backend shouldn't send the order state as pending if the transaction is pending
        } else {
            handlePaymentDoneOrder(it?.orderState)
        }
    }

    private fun AppCompatTextView.showAndSetText(it: String?) {
        this.text = it
        this.isVisible = !TextUtils.isEmpty(it)
    }

    private fun handlePaymentStatus(paymentStatus: GoldDeliveryTransactionState) {
        binding.firstContainterFirstText.showAndSetText(paymentStatus.subTitle)
        binding.firstText.showAndSetText(paymentStatus.title)
        when (val manualPaymentStatus = paymentStatus.getManualPaymentStatus()) {
            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {
                updateUI(manualPaymentStatus, null)
                binding.line.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_1EA787
                    )
                )
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> {
                updateUI(manualPaymentStatus, null)
                binding.firstContainer.isVisible = true
                binding.firstContainerFirstBtn.isVisible = true
                binding.firstContainerFirstBtn.setText(getString(R.string.feature_buy_gold_refresh))
                binding.firstContainerFirstBtn.setDebounceClickListener {
                    doManualPoll()
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, buildForAnalytics().apply {
                        this[Refresh] = paymentStatus.status ?: "Refresh"
                    })
                }
                binding.firstContainerSecondBtn.setText(getString(R.string.go_back))
                binding.firstContainerSecondBtn.setDebounceClickListener {
                    navigateBackToCart()
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, buildForAnalytics().apply {
                        this[Go_back] = paymentStatus.status ?: "Go back"
                    })
                }
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                updateUI(manualPaymentStatus, null)
                binding.firstContainerFirstBtn.setText(getString(R.string.retry))
                binding.firstContainerSecondBtn.setText(getString(R.string.go_to_cart))
                binding.firstContainer.isVisible = true

                if (paymentStatus.retryAllowed == true) {
                    binding.firstContainerFirstBtn.isVisible = true
                    binding.firstContainerFirstBtn.setDebounceClickListener {
                        retryPayment()
                        analyticsHandler.postEvent(
                            GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, buildForAnalytics().apply {
                            this[Retry] = paymentStatus.status ?: "Go back"
                        })
                    }
                }
                binding.firstContainerSecondBtn.isVisible = true
                binding.firstContainerSecondBtn.setDebounceClickListener {
                    navigateBackToCart()
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, buildForAnalytics().apply {
                        this[Go_to_cart] = paymentStatus.status ?: "Go back"
                    })
                }
                pollingJob?.cancel()
            }
        }
    }

    private fun navigateBackToCart() {
        navigateTo(
            CompletePaymentFragmentDirections.actionCompletePaymentFragmentToDeliveryStoreCartFragment(""),
            popUpTo = R.id.completePaymentFragment,
            inclusive = true
        )
    }

    private fun retryPayment() {
        viewModel.placeOrder(goldDeliveryPlaceOrderDataRequest)
    }

    private fun doManualPoll() {
        viewModel.fetchOrderStatus(args.orderId)
    }

    private fun handlePaymentDoneOrder(orderStatus: GoldDeliveryTransactionState? = null) {
        if (orderStatus == null) {
            binding.secondText.showAndSetText(getString(R.string.order_placed))
            TextViewCompat.setTextAppearance(
                binding.secondText,
                com.jar.app.core_ui.R.style.CommonTextViewStyle
            )
            binding.secondText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    com.jar.app.core_ui.R.color.color_ACA1D3
                )
            )
            binding.secondIcon.setImageDrawable(null)
            binding.secondContainer.isVisible = false
            return
        }

        val status = orderStatus.getManualPaymentStatus()

        binding.secondText.showAndSetText(orderStatus.title)
        binding.secondContainerFirstText.showAndSetText(orderStatus.subTitle)
        updateUI(null, status)

        when (status) {
            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {
                pollingJob?.cancel()
                dismissProgressBar()
                binding.secondContainer.isVisible = false
                navigateToCartSuccessFragment()
            }

            com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                // Failure
                pollingJob?.cancel()
                updateStatus(status, binding.secondText, binding.secondIcon, binding.secondPill)
                TextViewCompat.setTextAppearance(
                    binding.secondText,
                    com.jar.app.core_ui.R.style.CommonBoldTextViewStyle
                )
                binding.secondText.text = orderStatus.title
                binding.secondContainerFirstText.text = orderStatus.subTitle
                binding.secondContainer.isVisible = true
                binding.btnGoToCart.setDebounceClickListener {
                    navigateBackToCart()
                }
                binding.goToCartBtn.setDebounceClickListener {
                    navigateBackToCart()
                }
                dismissProgressBar()
            }

            else -> {
                // Pending
                dismissProgressBar()
                updateStatus(status, binding.secondText, binding.secondIcon, binding.secondPill)
                TextViewCompat.setTextAppearance(
                    binding.secondText,
                    com.jar.app.core_ui.R.style.CommonBoldTextViewStyle
                )
                binding.secondText.text = orderStatus.title
                orderStatus.subTitle?.let {
                    binding.secondContainerFirstText.text = it
                    binding.secondContainerFirstText.isVisible = true
                    binding.secondContainer.isVisible = true
                }
                binding.goToMyOrders.isVisible = true
                binding.goToMyOrders.setDebounceClickListener {
                    navigateTo(
                        CompletePaymentFragmentDirections.actionCompletePaymentFragmentToCartMyOrdersFragment(),
                        popUpTo = R.id.deliveryStoreItemListFragment,
                        inclusive = true
                    )
                }
            }
        }
    }

    private fun navigateToCartSuccessFragment() {
        uiScope.launch {
            delay(1500)
            val data = fetchManualPaymentStatusResponse?.goldDeliveryResponse ?: return@launch
            val actionCompletePaymentFragmentToCartSuccessFragment =
                CompletePaymentFragmentDirections.actionCompletePaymentFragmentToCartSuccessFragment(
                    args.orderId,
                    fetchManualPaymentStatusResponse?.paymentDate,
                    fetchManualPaymentStatusResponse?.paymentMethod,
                    fetchManualPaymentStatusResponse?.payerVpa,
                    data
                )
            navigateTo(
                actionCompletePaymentFragmentToCartSuccessFragment,
                popUpTo = R.id.completePaymentFragment,
                inclusive = true
            )
        }
    }

    private fun getData() {
        viewModel.fetchOrderStatus(args.orderId)
        fetchOrderStatus(args.orderId)
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToCart()
            }
        }

    private fun fetchOrderStatus(orderId: String) {
        pollingJob?.cancel()
        pollingJob = uiScope.doRepeatingTask(POLLING_INTERVAL) {
            if (viewModel.pollingCounter <= POLLING_ATTEMPTS) {
                viewModel.pollingCounter++
                viewModel.fetchOrderStatus(orderId)
            }
        }
        pollingJob?.start()
    }

    private fun updateUI(paymentStatus: com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus?, orderStatus: com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus?) {
        paymentStatus?.let {
            updateStatus(it, binding.firstText, binding.firstIcon, binding.firstPill)
            when (it) {
                com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.SUCCESS -> {
                    binding.firstContainer.isVisible = false
                }

                com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.PENDING -> {
                    binding.firstContainer.isVisible = true
                    binding.firstContainterFirstText.text = getString(R.string.placing_order_is_taking_a_little_longer)
                    binding.firstContainerFirstBtn.setText(getString(R.string.feature_buy_gold_refresh))
                    binding.firstContainerSecondBtn.setText(getString(R.string.go_back))
                    binding.firstContainerSecondBtn.setDebounceClickListener {
                        navigateBackToCart()
                    }
                }

                com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus.FAILURE -> {
                    binding.firstContainer.isVisible = true
                    binding.firstContainerSecondBtn.setText(getString(R.string.retry))
                    binding.firstContainterFirstText.text =
                        getString(R.string.experiencing_bank_server_issue_please_try_again_to_place_your_order)
                    binding.firstContainerSecondBtn.setText(getString(R.string.go_to_cart))

                    binding.firstContainerFirstBtn.setDebounceClickListener {
//                        initiatePayment(args.)
                    }
                    binding.firstContainerSecondBtn.setDebounceClickListener {
                        analyticsHandler.postEvent(
                            GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, buildForAnalytics().apply {
                            this[Go_to_cart] = paymentStatus.name
                        })
                        navigateBackToCart()
                    }
                }
            }
        }
        orderStatus?.let {
            updateStatus(it, binding.secondText, binding.secondIcon, binding.secondPill)
//            binding.firstText.text = constructFirstOrderText(it)
        }
    }

    private fun updateStatus(
        it: com.jar.app.feature_one_time_payments_common.shared.ManualPaymentStatus,
        firstText: AppCompatTextView,
        firstIcon: AppCompatImageView,
        firstPill: AppCompatTextView
    ) {
        firstPill.isVisible = true
        PaymentUIHelper.setPillStatus(it, WeakReference(firstPill))
        PaymentUIHelper.setIconStatus(it, WeakReference(firstIcon))
    }
}
