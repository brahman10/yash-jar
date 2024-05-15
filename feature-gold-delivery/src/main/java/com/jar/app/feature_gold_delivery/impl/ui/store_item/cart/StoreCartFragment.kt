package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentStoreCartBinding
import com.jar.app.feature_gold_delivery.impl.helper.Utils
import com.jar.app.feature_gold_delivery.impl.helper.Utils.calculateQuantityItemsString
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.DeliveryStoreItemListFragment
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.StoreItemAdapter
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIBreakdownData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.GoldDeliveryPlaceOrderDataRequest
import com.jar.app.feature_gold_delivery.shared.util.CartDataHelper
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_one_time_payments.shared.data.model.base.InitiatePaymentResponse
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.domain.model.PinCodeEligibility
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CartDetailFragment : BaseFragment<FragmentStoreCartBinding>() {

    private var adapter: StoreCartAdapter? = null
    private val args by navArgs<CartDetailFragmentArgs>()

    private val viewModelProvider by hiltNavGraphViewModels<StoreCartFragmentViewModelAndroid>(R.id.feature_delivery_navigation)

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    companion object {
        const val Jar_savings_used_toggle = "Jar savings used toggle"
        const val Edit_in_address = "Edit in address"
        const val Place_order_CTA = "Place order CTA"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStoreCartBinding
        get() = FragmentStoreCartBinding::inflate

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var appScope: CoroutineScope

    private var paymentJob: Job? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.cart), true, backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun postClickEvent(clickTypeValue: String, eventName: String, eventValue: String) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, mapOf(
                GoldDeliveryConstants.AnalyticsKeys.Click_type to clickTypeValue,
                eventName to eventValue,
                GoldDeliveryConstants.AnalyticsKeys.jar_savings_enabled to binding.fragmentListDetail.jarSavingsSwitch.isChecked.toString()
            )
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                deliveryViewModel.selectedAddressLiveData.value?.let {
                    setCheckoutMode(false, it)
                } ?: run {
                    setCheckoutMode(false, null)
                }
            }
        }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ShownCheckoutGoldDelivery,
        )
        registerBackPressDispatcher()
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun rebuildCart(jarSavingsUsed: Boolean) {
        viewModel.currentCartBreakdownLiveData.value?.data?.data?.let {
            val list =
                BreakdownViewRenderer.getBreakdownListFromCart(
                    WeakReference(requireContext()),
                    it,
                    jarSavingsUsed
                )
            BreakdownViewRenderer.renderBreakdownView(
                binding.fragmentListDetail.priceBreakdownContainer,
                list
            )
            val netPayable = if (jarSavingsUsed) it.netAmountWithJarSavingsUsed else it.netAmount
            binding.tvCartPrice.text = BreakdownViewRenderer.getFormattedAmount(
                WeakReference(requireContext()),
                netPayable.orZero().getFormattedAmount()
            )
        }
    }

    private fun setupUI() {
        binding.fragmentListDetail.rvImages.layoutManager = LinearLayoutManager(requireContext())
        binding.btnProceedCart.setDisabled(true)
        adapter = StoreCartAdapter(
            onDeleteClick = {
                navigateTo(
                    CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToCartItemDeleteFragemnt(
                        it
                    )
                )
                postClickEvent(
                    GoldDeliveryConstants.AnalyticsKeys.gold_item_deleted,
                    GoldDeliveryConstants.AnalyticsKeys.Label,
                    it.label.orEmpty()
                )
            },
            onMinusClick = { it, quantity ->
                viewModel.removeFromCart(it.id ?: "", quantity)
                postClickEvent(
                    GoldDeliveryConstants.AnalyticsKeys.gold_item_quantity_decreased,
                    GoldDeliveryConstants.AnalyticsKeys.Label,
                    it.label.orEmpty()
                )
            },
            onAddClick = {
                navigateTo(
                    CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToCartItemAddFragemnt(
                        it
                    )
                )
                postClickEvent(
                    GoldDeliveryConstants.AnalyticsKeys.gold_item_quantity_increased,
                    GoldDeliveryConstants.AnalyticsKeys.Label,
                    it.label.orEmpty()
                )
            },
            onEditClick = {
                val actionDeliveryStoreCartFragmentToCartItemsQuantityEditFragment =
                    CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToCartItemsQuantityEditFragment(
                        it, false
                    )
                navigateTo(actionDeliveryStoreCartFragmentToCartItemsQuantityEditFragment)
                postClickEvent(
                    GoldDeliveryConstants.AnalyticsKeys.gold_item_edit_clicked,
                    GoldDeliveryConstants.AnalyticsKeys.Label,
                    it.label.orEmpty()
                )
            },
            isCheckoutMode = { isCheckoutMode() }
        )
        binding.fragmentListDetail.rvImages.adapter = adapter
    }

    private fun setupLiveCounter(fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse?) {
        binding.goldPriceProgressLayout.start(
            livePriceMessage = "",
            spannedPriceMessage = Utils.buildSpannableStringForGoldPrice(
                fetchCurrentGoldPriceResponse?.price.orZero(),
                WeakReference(requireContext())
            ),
            validityInMillis = fetchCurrentGoldPriceResponse?.getValidityInMillis().orZero(),
            uiScope = uiScope,
            onFinish = {
                viewModel.fetchCurrentBuyPrice()
            },
        )
        binding.goldPriceProgressLayout.setProgressColor("#993C4568")
        binding.goldPriceProgressLayout.setTextAppearance(com.jar.app.core_ui.R.style.CommonTextViewStyle)
    }

    private fun setupListeners() {
        binding.clickViewDeliveringContainer.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery,
                Edit_in_address,
                binding.deliveryLocationTv.text.toString()
            )
            viewModel.clearPinCode()
            navigateTo(CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToCartPinCodeFragment())
        }
        binding.fragmentListDetail.jarSavingsSwitch.setOnCheckedChangeListener { compoundButton, b ->
            rebuildCart(b)
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery,
                CartDataHelper.createMapForAnalytics(
                    DeliveryStoreItemListFragment.My_cart,
                    viewModel.currentCartLiveData.value?.data?.data
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        if (b) GoldDeliveryConstants.AnalyticsKeys.jar_savings_enabled else GoldDeliveryConstants.AnalyticsKeys.jar_savings_disabled
                    )
                })
        }
        binding.tvCartQuantity.setDebounceClickListener {
            val childView = binding.fragmentListDetail.textView10
            val scrollTo: Int = (childView.parent.parent as View).top + childView.top
            binding.nsv.smoothScrollTo(0, scrollTo)
            val drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.feature_gold_delivery_bg_rounded_gradient
            )
            binding.fragmentListDetail.priceParentContainer.background = drawable

            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.view_breakdown_clicked,
                Edit_in_address,
                binding.deliveryLocationTv.text.toString()
            )
            val x = AnimatorSet()
            x.playSequentially(
                ObjectAnimator
                    .ofPropertyValuesHolder(
                        drawable,
                        PropertyValuesHolder.ofInt("alpha", 0, 255)
                    ).setDuration(800),
                ObjectAnimator
                    .ofPropertyValuesHolder(
                        drawable,
                        PropertyValuesHolder.ofInt("alpha", 255, 0)
                    ).setDuration(800),
                ObjectAnimator
                    .ofPropertyValuesHolder(
                        drawable,
                        PropertyValuesHolder.ofInt("alpha", 0, 255)
                    ).setDuration(800),
                ObjectAnimator
                    .ofPropertyValuesHolder(
                        drawable,
                        PropertyValuesHolder.ofInt("alpha", 255, 0)
                    ).setDuration(800),
                ObjectAnimator
                    .ofPropertyValuesHolder(
                        drawable,
                        PropertyValuesHolder.ofInt("alpha", 0, 255)
                    ).setDuration(800),
                ObjectAnimator
                    .ofPropertyValuesHolder(
                        drawable,
                        PropertyValuesHolder.ofInt("alpha", 255, 0)
                    ).setDuration(800),
            )
            x.start()
            x.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    uiScope.launch {
                        whenResumed {
                            val drawable = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.bg_rounded_2e2942_10dp_without_stroke
                            )
                            binding.fragmentListDetail.priceParentContainer.background = drawable
                        }
                    }
                }
            })
        }
        binding.exploreMoreButton.setDebounceClickListener {
            navigateTo(
                CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToDeliveryStoreItemListFragment(),
                popUpTo = R.id.deliveryStoreItemListFragment,
                inclusive = true
            )
        }
        binding.btnProceedCart.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery,
                CartDataHelper.createMapForAnalytics(
                    Place_order_CTA,
                    viewModel.currentCartLiveData.value?.data?.data
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.place_order_clicked
                    )
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.jar_savings_enabled, binding.fragmentListDetail.jarSavingsSwitch.isChecked.toString()
                    )
                }
            )

            if (!isCheckoutMode()) {
                val value = viewModel.selectedAddress.value

                if (value == null) {
                    viewModel.navigateToCheckOutOnDeliveryAddressChange = true
                    val actionDeliveryStoreCartFragmentToSavedAddressFragment =
                        CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToSavedAddressFragment(

                        )
                    navigateTo(actionDeliveryStoreCartFragmentToSavedAddressFragment)
                } else {
                    setCheckoutMode(true, value)
                }
            } else {
                val addressId = viewModel.selectedAddress.value?.addressId ?: ""
                val request = GoldDeliveryPlaceOrderDataRequest(
                    addressId,
                    binding.fragmentListDetail.jarSavingsSwitch.isChecked,
                    paymentManager.getCurrentPaymentGateway().name,
                    viewModel.buyPriceLiveData.value?.data?.data
                )
                viewModel.placeOrderRequest = request
                viewModel.placeGoldDeliveryOrder(request)
            }
        }
    }

    private fun updateToolBar(title: String) {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        title, true
                    )
                )
            )
        )

    }

    private fun setCheckoutMode(checkout: Boolean, deliveryAddress: Address? = null) {
        if (checkout) {
            adapter?.notifyDataSetChanged()
            updateToolBar(getString(R.string.checkout))
            binding.fragmentListDetail.deliveryInfoContainer.isVisible = true
            binding.fragmentListDetail.jarSavingsContainer.isVisible = false
            binding.clickViewDeliveringContainer.isVisible = false
            binding.deliveringTextContainer.isVisible = false
            deliveryAddress?.let {
                binding.fragmentListDetail.deliveryNameTv.text =
                    "${it.name} (${it.phoneNumber})"
                binding.fragmentListDetail.deliveryTextTv.text = "${it.address}"
            }
            binding.deliveryGroup.isVisible = false
            binding.fragmentListDetail.textView8.text = getString(R.string.delivery_address)
            binding.fragmentListDetail.imageView10.setImageResource(R.drawable.ic_location_pin)
            backPressCallback.isEnabled = true
        } else {
            adapter?.notifyDataSetChanged()
            binding.clickViewDeliveringContainer.isVisible = true
            binding.deliveringTextContainer.isVisible = true
            updateToolBar(getString(R.string.cart))
            binding.fragmentListDetail.deliveryInfoContainer.isVisible = false
            binding.deliveryGroup.isVisible = true
            binding.fragmentListDetail.jarSavingsContainer.isVisible = true
            binding.fragmentListDetail.textView8.text = getString(R.string.select_payment_method)
            binding.fragmentListDetail.imageView10.setImageResource(R.drawable.rupee_coin)
            backPressCallback.isEnabled = false
            viewModel.clearAddress()
        }
    }

    private fun isCheckoutMode(): Boolean {
        return binding.fragmentListDetail.deliveryInfoContainer.isVisible
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
        viewModel.validatePinCodeLiveData.collectUnwrapped(
            onSuccess = {
                it.data ?: return@collectUnwrapped
                when (it.data?.getEligibilityStatus()) {
                    PinCodeEligibility.DELIVERABLE -> {
                        binding.topImageRight.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_edit)
                        binding.topImageRight.rotation = 0f
                        binding.labelDelivery.setText(R.string.delivering_to)
                        binding.labelDelivery.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_ACA1D3
                            )
                        )
                        binding.labelDelivery.isAllCaps = true
                        binding.deliveryLocationTv.text = "${it?.data?.city} (${it?.data?.pinCode})"
                        binding.deliveryLocationTv.isVisible = true
                        binding.deliveringTV.isVisible = false
                        binding.btnProceedCart.setDisabled(false)
                        binding.deliveryLocationTv.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.white
                            )
                        )
                    }

                    PinCodeEligibility.NOT_DELIVERABLE -> {
                        binding.topImageRight.setImageResource(R.drawable.up_arrow)
                        binding.topImageRight.rotation = 90f
                        binding.btnProceedCart.setDisabled(true)
                        binding.labelDelivery.setText(R.string.check_if_we_deliver_to_your_area)
                        binding.labelDelivery.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.white
                            )
                        )
                        binding.deliveryLocationTv.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                com.jar.app.core_ui.R.color.color_EB6A6E
                            )
                        )
                        binding.deliveryLocationTv.isVisible = true
                        if (TextUtils.isEmpty(it?.data?.city)) {
                            binding.deliveryLocationTv.text =
                                getString(R.string.invalid_pincode)
                        } else {
                            binding.deliveryLocationTv.text = getString(
                                R.string.city_state_is_unserviceable,
                                it?.data?.city,
                                it?.data?.state
                            )
                        }
                    }

                    else -> {}
                }
            },
            onLoading = {
                binding.topImageRight.setImageResource(R.drawable.up_arrow)
                binding.topImageRight.rotation = 90f
                binding.btnProceedCart.setDisabled(true)

                binding.labelDelivery.setText(R.string.check_if_we_deliver_to_your_area)
                binding.labelDelivery.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.white
                    )
                )
                binding.deliveryLocationTv.isVisible = false
                binding.deliveringTV.isVisible = false
            })
        }
    }

    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.buyPriceLiveData.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                setupLiveCounter(it?.data)
                updateCart()
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )
        }
    }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                deliveryViewModel.cartUpdate.collectLatest {
                    updateCart()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.deleteAddressLiveData.collectUnwrapped(
                    onSuccess = {
                        updateCart()
                    },
                    onSuccessWithNullData = {
                        updateCart()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.deleteItemFromcartLiveData.collectUnwrapped(
                    onSuccessWithNullData = {
                dismissProgressBar()
                updateCart()
            },
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                updateCart()
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )}}


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        deliveryViewModel.selectedAddressLiveData.collectLatest {
            if (viewModel.navigateToCheckOutOnDeliveryAddressChange) {
                setCheckoutMode(true, it)
            }
            it?.let { it1 -> viewModel.setSelectedAddress(it1) }
        }}}


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {

        viewModel.currentCartLiveData.collectUnwrapped(
            onLoading = {
                binding.exploreMoreButton.setDisabled(true)
                showProgressBar()
            },
            onSuccess = {
                binding.exploreMoreButton.setDisabled(false)
                dismissProgressBar()
                if (it?.data?.cartItemData.isNullOrEmpty()) {
                    binding.fragmentListDetail.cartContainerRoot.isVisible = false
                    binding.nsv2.isVisible = true
                    binding.emptyCart.isVisible = true
                    binding.goldPriceProgressLayout.isVisible = false
                    binding.deliveringContainer.isVisible = false
                    setCheckoutMode(false, null)
                    setupExclusiveProducts()
                    backPressCallback.isEnabled = false
                } else {
                    binding.nsv2.isVisible = false
                    binding.emptyCart.isVisible = false
                    binding.deliveringContainer.isVisible = true
                    binding.fragmentListDetail.cartContainerRoot.isVisible = true
                    binding.goldPriceProgressLayout.isVisible = true
                    binding.fragmentListDetail.productsTV.text =
                        calculateQuantityItemsString(WeakReference(requireContext()), it.data)
                }
                setupCart(it?.data)
            },
            onError = { _, _ ->
                binding.exploreMoreButton.setDisabled(false)
                dismissProgressBar()
                // todo handle it here
            }
        )}}

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.currentCartBreakdownLiveData.collectUnwrapped(
            onLoading = {
                binding.exploreMoreButton.setDisabled(true)
                showProgressBar()
            },
            onSuccess = {
                binding.exploreMoreButton.setDisabled(false)
                dismissProgressBar()
                setupCartBreakdown(it?.data)
                binding.fragmentListDetail.jarSavingsSwitch.isEnabled = it?.data?.jarSavings.orZero() > 0
                if (!(it?.data?.jarSavings.orZero() > 0)) {
                    binding.fragmentListDetail.jarSavingsSwitch.isChecked = false
                }
            },
            onError = { _, _ ->
            binding.exploreMoreButton.setDisabled(false)
                dismissProgressBar()
            }
        )}}

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.placeOrderAPILiveData.collectUnwrapped(
                    onLoading = {
                        binding.exploreMoreButton.setDisabled(true)
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        if (it.data != null && it.success) {
                            initiatePayment(it?.data)
                        } else {
                            it.errorMessage?.snackBar(binding.root)
                        }
                    },
                    onError = { errorMessage, errorCode ->
                        errorMessage.snackBar(binding.root)
                        binding.exploreMoreButton.setDisabled(false)
                        dismissProgressBar()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.storeItemsLiveData.collectUnwrapped(
                    onSuccess = {
                val adapter: StoreItemAdapter = StoreItemAdapter({
                    val action =
                        CartDetailFragmentDirections.actionDeliveryStoreCartFragmentToStoreItemDetailFragment(
                            it.label ?: "", viewModel.selectedAddress?.value?.pinCode ?: ""
                        )
                    navigateTo(action)
                })
                binding.cartOtherProductsRv.isVisible = true
                binding.cartOtherProductsRv.adapter = adapter
                binding.cartOtherProductsRv.addItemDecorationIfNoneAdded(
                    SpaceItemDecoration(
                        8.dp,
                        0.dp
                    )
                )
                binding.cartOtherProductsRv.layoutManager =
                    object :
                        GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false) {
                        override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                            lp?.width = width / 2
                            return super.checkLayoutParams(lp)
                        }
                    }
                adapter.submitList(it?.data?.products)
            }
        )}}
    }

    private fun setupExclusiveProducts() {
        if (viewModel.storeItemsLiveData.value.status != RestClientResult.Status.NONE) {
            binding.cartOtherProductsRv.isVisible = true
        } else {
            viewModel.fetchProducts()
        }
    }

    private fun navigateToPaymentScreen(
        it: com.jar.app.feature_one_time_payments_common.shared.FetchManualPaymentStatusResponse? = null,
        orderId: String
    ) {
        val fetchResponse =
            encodeUrl(serializer.encodeToString(it))
        val placeOrderRequestString =
            encodeUrl(serializer.encodeToString(viewModel.placeOrderRequest))

        navigateTo(
            "android-app://com.jar.app/completePaymentFrag/${orderId}/${fetchResponse}/${placeOrderRequestString}",
            popUpTo = R.id.deliveryStoreItemListFragment,
            inclusive = true
        )
    }

    private fun initiatePayment(initiatePaymentResponse: InitiatePaymentResponse?) {
        initiatePaymentResponse ?: return
        paymentJob?.cancel()
        paymentJob = appScope.launch(dispatcherProvider.main) {
            paymentManager.initiateOneTimePayment(initiatePaymentResponse)
                .collectUnwrapped(
                    onSuccess = {
                        uiScope.launch(dispatcherProvider.main) {
                            navigateToPaymentScreen(it, initiatePaymentResponse.orderId)
                            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                dismissProgressBar()
                            }
                        }
                    },
                    onError = { _, message ->
                        uiScope.launch(dispatcherProvider.main) {
                            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                dismissProgressBar()
                                message?.snackBar(binding.root)
                            }
                        }
                    }
                )
        }

    }

    private fun updateCart() {
        viewModel.fetchNewCart()
        viewModel.fetchCartBreakdown()
    }

    private fun setupCart(it: CartAPIData?) {
        adapter?.submitList(it?.cartItemData)
    }

    private fun setupCartBreakdown(it: CartAPIBreakdownData?) {
        binding.fragmentListDetail.text2.text =
            getString(R.string.feature_gold_delivery_n_gm, it?.jarSavingsInGm.orZero().toString())
        rebuildCart(binding.fragmentListDetail.jarSavingsSwitch.isChecked)
    }

    private fun getData() {
        updateCart()
        viewModel.fetchCurrentBuyPrice()
        args.pinCodeEntered?.takeIf { it.isNotBlank() }?.let {
            viewModel.validatePinCode(it)
        }
    }
}
