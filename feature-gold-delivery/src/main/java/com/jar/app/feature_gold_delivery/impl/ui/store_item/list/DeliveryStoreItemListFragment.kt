package com.jar.app.feature_gold_delivery.impl.ui.store_item.list

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.hideKeyboard
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.getStringOrNull
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.util.TypefaceContainer
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentDeliveryStoreBinding
import com.jar.app.feature_gold_delivery.impl.helper.Utils.calculateQuantityItemsString
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_user_api.domain.model.PinCodeEligibility
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2
import com.jar.app.feature_transaction.shared.domain.model.TransactionData
import com.jar.app.feature_gold_delivery.shared.util.CartDataHelper
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryStoreItemListFragment : BaseFragment<FragmentDeliveryStoreBinding>() {

    private val args by navArgs<DeliveryStoreItemListFragmentArgs>()
    override fun setupAppBar() {
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeliveryStoreBinding
        get() = FragmentDeliveryStoreBinding::inflate

    private val viewModelProvider by viewModels<DeliveryStoreItemListFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }


    private var adapter: StoreItemAdapter? = null
    private var otherOptionsAdapter: StoreItemAdapter? = null

    private var spaceItemDecoration: SpaceItemDecoration? = null

    private var baseEdgeEffectFactory: BaseEdgeEffectFactory? = null

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val hasAnimatedOnce = AtomicBoolean(false)

    companion object {
        const val Pincode_entry = "Pincode entry"
        const val Categories = "Categories"
        const val Add_to_wishlist = "Add to wishlist"
        const val My_orders = "My orders"
        const val My_wishlist = "My wishlist"
        const val My_cart = "My cart"
        const val Gold_coin = "Gold coin"
        const val Order_again = "Order again"
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(GoldDeliveryConstants.AnalyticsKeys.ShownHomeScreenGoldDelivery)
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    override fun onDestroyView() {
        baseEdgeEffectFactory = null
        spaceItemDecoration = null
        adapter = null
        binding.orderAgainRv.onFlingListener = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        analyticsHandler.postEvent(GoldDeliveryConstants.AnalyticsKeys.BackClick_GoldDeliveryScreen)
    }

    private fun setupUI() {
        binding.rvGoldOptions.layoutManager = GridLayoutManager(requireContext(), 2)
        Glide.with(this)
            .load(BaseConstants.ImageUrlConstants.DELIVERY_BANNER)
            .into(binding.image)
        adapter = StoreItemAdapter({
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                Gold_coin,
                it.label ?: ""
            )
            val action =
                DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToStoreItemDetailFragment(
                    it?.label ?: "", binding.etPinCode.text?.getStringOrNull() ?: ""
                )
            navigateTo(action)
        })
        binding.rvGoldOptions.adapter = adapter
        baseEdgeEffectFactory = BaseEdgeEffectFactory()
        binding.rvGoldOptions.edgeEffectFactory = baseEdgeEffectFactory!!
        spaceItemDecoration = SpaceItemDecoration(4.dp, 8.dp)
        binding.rvGoldOptions.addItemDecorationIfNoneAdded(spaceItemDecoration!!)
        binding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val firstCheckedId = checkedIds.getOrNull(0)
            if (firstCheckedId != null) {
                val chip = binding.root.findViewById<Chip>(firstCheckedId)
                val tag = chip.tag as? String?
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                    Categories,
                    tag ?: ""
                )
                viewModel.getAllStoreItems(tag)
                viewModel.lastCategoryTitle = tag
                updateStrokes()
            }
        }
    }

    private fun updateStrokes() {
        binding.chipGroupFilter.forEach {
            (it as? Chip)?.let { chip ->
                chip.chipStrokeWidth = if (chip.isChecked) 0f.dp else 2f.dp
                TypefaceContainer(
                    null,
                    if (chip.isChecked) com.jar.app.core_ui.R.font.inter_bold else com.jar.app.core_ui.R.font.inter
                ).applyTo(chip)
            }
        }
    }

    private fun checkIfRVIsEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            binding.noItemsRvIv.isVisible = true
            binding.noProductsTv.isVisible = true
            binding.rvGoldOptions.isVisible = false
            viewModel.lastCategoryTitle?.let {
                binding.noProductsTv.text =
                    getString(R.string.no_products_available_in, viewModel.lastCategoryTitle ?: "")
            } ?: run {
                binding.noProductsTv.text = getString(R.string.no_products_available)
            }
            setupOtherProducts()
        } else {
            binding.noItemsRvIv.isVisible = false
            binding.noProductsTv.isVisible = false
            binding.rvGoldOptions.isVisible = true
            binding.cartOtherProductsRv.isVisible = false
            binding.bottomDivider02.isVisible = false
            binding.subtitle.isVisible = false
        }
    }

    private fun setupOtherProducts() {
        if (otherOptionsAdapter == null) {
            otherOptionsAdapter = StoreItemAdapter({
                val action =
                    DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToStoreItemDetailFragment(
                        it.label ?: "", ""
                    )
                navigateTo(action)
            })
            binding.cartOtherProductsRv.adapter = otherOptionsAdapter
            binding.cartOtherProductsRv.addItemDecorationIfNoneAdded(
                SpaceItemDecoration(8.dp, 0.dp)
            )
            binding.cartOtherProductsRv.layoutManager = object :
                GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    lp?.width = width / 2
                    return super.checkLayoutParams(lp)
                }
            }
            viewModel.otherProductsRv.value?.data?.data?.products?.let {
                otherOptionsAdapter?.submitList(it)
            }
        }
        binding.cartOtherProductsRv.isVisible = true
        binding.bottomDivider02.isVisible = true
        binding.subtitle.isVisible = true
    }

    private fun setupListeners() {
        Glide.with(binding.root)
            .load(BaseConstants.ImageUrlConstants.DELIVERY_BOTTOM)
            .into(binding.ivDeliveryBottom)
        binding.btnProceedCart.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                CartDataHelper.createMapForAnalytics(
                    My_cart,
                    viewModel.fetchCartLiveData.value?.data?.data
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_proceed_click
                    )
                }
            )
            navigateTo(
                DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToDeliveryStoreCartFragment(
                    binding.etPinCode.text?.getStringOrNull() ?: ""
                )
            )
        }
        binding.etPinCode.setOnFocusChangeListener { view, focus ->
            if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                if (focus) {
                    val isSixDigit = binding.etPinCode.length() == 6
                    binding.btnClear.isVisible = isSixDigit
                    binding.btnCheck.isVisible = !isSixDigit
                } else {
                    binding.btnClear.isVisible = false
                    binding.btnCheck.isVisible = false
                }
            }
        }
        binding.etPinCode.textChanges()
            .debounce(300)
            .onEach {
                binding.tvPinCodeEligibilityText.visibility = View.GONE
                binding.tvPinCodeEligibilityText.text = ""
                binding.tvPinCodeEligibilityText.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
                binding.clPinCode.setBackgroundResource(com.jar.app.core_ui.R.drawable.round_black_bg_16dp)
                binding.rvGoldOptions.isVisible = true
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
                    binding.cartContainer.isVisible = false
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

        binding.listBtn.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                My_orders,
                "My orders screen opened"
            )
            navigateTo(DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToCartMyOrdersFragment())
        }
        binding.heartBtn.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                My_wishlist,
                "Wishlist screen opened"
            )
            navigateTo(DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToWishlistFragment())
        }
        binding.cartBtn.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                CartDataHelper.createMapForAnalytics(
                    My_cart,
                    viewModel.fetchCartLiveData.value?.data?.data
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.cart_tab_click
                    )
                }
            )
            val actionDeliveryStoreItemListFragmentToDeliveryStoreCartFragment =
                DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToDeliveryStoreCartFragment(
                    binding.etPinCode.text?.getStringOrNull() ?: ""
                )
            navigateTo(actionDeliveryStoreItemListFragmentToDeliveryStoreCartFragment)
        }

        binding.btnCheck.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            requireContext().hideKeyboard(binding.etPinCode)
            if (!pinCode.isNullOrBlank() && pinCode.length == 6) {
                viewModel.validatePinCode(pinCode.toString())
            }
            binding.etPinCode.clearFocus()
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                Pincode_entry,
                pinCode?.toString() ?: ""
            )
        }
        binding.btnClear.setDebounceClickListener {
            val pinCode = binding.etPinCode.text ?: ""
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.pincode_cleared,
                Pincode_entry,
                pinCode.toString() ?: ""
            )
            binding.etPinCode.showKeyboard()
            binding.etPinCode.requestFocus()
            binding.etPinCode.setText("")
            binding.btnClear.isVisible = false
            binding.btnCheck.isVisible = true
            viewModel.clearPincode()
            binding.tvPinCodeEligibilityText.text = ""
            binding.tvPinCodeEligibilityText.isVisible = false
            binding.tvPinCodeEligibilityText.setCompoundDrawables(null, null, null, null)
        }

        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun postClickEvent(s: String, pincodeEntry: String, s1: String) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery, mapOf(
                GoldDeliveryConstants.AnalyticsKeys.Click_type to s,
                pincodeEntry to s1
            )
        )
    }

    private fun updateShowingLabel(count: Int) {
        binding.subtitlet.isVisible = count > 0
        binding.subtitlet.text = getString(R.string.showing_all_products, count)
    }

    private fun setupOrderAgainCard(it: List<TransactionData>?) {
        if (it.isNullOrEmpty()) {
            binding.orderAgainCardContainer.isVisible = false
        } else {
            binding.orderAgainRv.onFlingListener = null
            val orderAgainAdapter = OrderAgainRVAdapter({
                orderAgainFlow(it)
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery, mapOf(
                        DeliveryStoreItemListFragment.Order_again to (it?.title
                            ?: it.subTitle ?: "ORDER AGAIN FLOW")
                    )
                )
            }, {
                findNavController().navigate(Uri.parse("android-app://com.jar.app/transactionDetail/${it.orderId}/${it.assetTransactionId}/${it.sourceType}"))
            })

            binding.orderAgainRv.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

//            binding.orderAgainRv.layoutManager =
//                CenterZoomLinearLayoutManager(
//                    requireContext(),
//                    1f,
//                    0f,
//                    RecyclerView.HORIZONTAL
//                )
            binding.orderAgainRv.adapter = orderAgainAdapter
            orderAgainAdapter.submitList(it)
            binding.orderAgainCardContainer.isVisible = true


            if (it.size > 1) {
                val circlePagerIndicatorDecoration = DotIndicatorDecoration(requireContext())
                binding.orderAgainRv.addItemDecoration(circlePagerIndicatorDecoration)
                binding.orderAgainRv.setPadding(0, 0, 0, 30.dp)
            } else {
                binding.orderAgainRv.setPadding(0, 0, 0, 0)
            }
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(binding.orderAgainRv)
        }
    }

    private fun orderAgainFlow(transaction: TransactionData) {
        navigateTo(
            DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToStoreItemDetailFragment(
                transaction.title ?: "",
                ""
            ), popUpTo = R.id.deliveryStoreItemListFragment, inclusive = true
        )
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.storeItemsLiveData.collect(
                    onSuccess = {
                        if (checkForProductIdDeeplink(
                                args.productItToNavigate,
                                it?.products
                            )
                        ) return@collect
                        binding.progressBar.isVisible = false
                        adapter?.submitList(it?.products)
                        if (hasAnimatedOnce.getAndSet(true).not()) {
                            binding.rvGoldOptions.runLayoutAnimation(com.jar.app.core_ui.R.anim.grid_layout_animation_from_bottom)
                        }
                        updateShowingLabel(it?.products?.size ?: 0)
                        checkIfRVIsEmpty(it?.products.isNullOrEmpty())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.otherProductsRv.collect(
                    onSuccess = {
                        otherOptionsAdapter?.submitList(it?.products)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.getLandingDetails.collect(
                    onSuccess = {
                        setupCategories(it?.categories)
                        setupOrderAgainCard(it?.previousOrder)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                deliveryViewModel.cartUpdate.collectLatest {
                    viewModel.fetchCartItems()
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fetchCartLiveData.collect(
                    onSuccess = {
                        it?.let { it1 -> setupCart(it1) }
                    }
                )
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.validatePinCodeLiveData.collect(
                    onSuccess = {
                        analyticsHandler.postEvent(GoldDeliveryConstants.AnalyticsKeys.PinCodeCheckClick_GoldDeliveryScreen)
                        it ?: return@collect
                        when (it.getEligibilityStatus()) {
                            PinCodeEligibility.DELIVERABLE -> {
//                        binding.clPlaceholderPinCodeNotServiceable.isVisible = false
//                        binding.tvHeaderOptions.isVisible = true
                                binding.rvGoldOptions.isVisible = true
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
                                    getString(
                                        R.string.city_state_is_serviceable,
                                        it?.city.orEmpty()
                                    )
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
                                binding.btnClear.isVisible = false
                            }

                            PinCodeEligibility.NOT_DELIVERABLE -> {
//                        binding.clPlaceholderPinCodeNotServiceable.isVisible = true
//                        binding.tvHeaderOptions.isVisible = false
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
                                binding.clPinCode.setBackgroundResource(R.drawable.bg_error_pin_code)
                                binding.tvPinCodeEligibilityText.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        com.jar.app.core_ui.R.color.color_EB6A6E
                                    )
                                )
                                if (TextUtils.isEmpty(it?.city)) {
                                    binding.tvPinCodeEligibilityText.text =
                                        getString(R.string.invalid_pincode)
                                } else {
                                    binding.tvPinCodeEligibilityText.text =
                                        getString(
                                            R.string.city_state_is_unserviceable,
                                            it?.city.orEmpty(),
                                            it?.state.orEmpty()
                                        )
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
                    })
            }
        }
    }

    private fun checkForProductIdDeeplink(
        productItToNavigate: String?,
        products: List<ProductV2>?
    ): Boolean {
        productItToNavigate?.takeIf { it.isNotBlank() }?.let { productId ->
            products?.forEach { product ->
                product.availableVolumes?.forEach {
                    if (it?.productId.toString() == productId) {
                        arguments?.clear()
                        requireArguments().remove("productItToNavigate")
                        navigateTo(
                            DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToStoreItemDetailFragment(
                                product.label ?: "",
                                ""
                            ), popUpTo = R.id.deliveryStoreItemListFragment, inclusive = true
                        )
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun setupCart(it: CartAPIData) {
        binding.cartContainer.isVisible = !it.cartItemData.isNullOrEmpty()
        it.cartItemData?.takeIf { it.isNotEmpty() }?.let {
            binding.cartNoItems.setText(it.size.toString())
            binding.cartNoItems.isVisible = true
        } ?: run {
            binding.cartNoItems.isVisible = false
        }
        if (it.cartItemData.isNullOrEmpty()) return
        binding.tvCartPrice.text = context?.getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            CartDataHelper.calculateTotalAmountFromCart(it.cartItemData).getFormattedAmount()
        )
        binding.tvCartQuantity.text =
            calculateQuantityItemsString(WeakReference(requireContext()), it)
        binding.cartContainer.setDebounceClickListener { view ->
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonHomeScreenGoldDelivery,
                CartDataHelper.createMapForAnalytics(
                    My_cart,
                    it
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_open
                    )
                }
            )
            val action =
                DeliveryStoreItemListFragmentDirections.actionDeliveryStoreItemListFragmentToCartItemsFragment(
                    binding.etPinCode.text?.getStringOrNull() ?: "", it
                )
            action?.let { it1 -> navigateTo(it1) }
        }
    }

    private fun setupCategories(categories: List<String>?) {
        binding.chipGroupFilter.removeAllViews()
        categories?.forEachIndexed { index, category ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.test2, binding.chipGroupFilter, false) as Chip
            chip.text = category
            chip.tag = category
            if (category == getString(R.string.all)) {
                chip.isChecked = true
                chip.chipStrokeWidth = 0f.dp
                TypefaceContainer(null, com.jar.app.core_ui.R.font.inter).applyTo(chip)
            } else {
                chip.isChecked = false
                TypefaceContainer(null, com.jar.app.core_ui.R.font.inter).applyTo(chip)
            }
            chip.id = ViewCompat.generateViewId()
            binding.chipGroupFilter.addView(chip)
        }
    }

    private fun getData() {
        viewModel.getAllStoreItems()
        viewModel.getDeliveryLandingScreenDetails()
        viewModel.fetchCartItems()
    }
}