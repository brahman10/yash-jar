package com.jar.app.feature_gold_delivery.impl.ui.cart_items_quantity_edit

import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration

import com.jar.app.core_ui.widget.zoom_layout_manager.CenterZoomLinearLayoutManager
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartQuantityEditItemsBinding
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.ui.store_item.detail.StoreItemImageAdapter
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.DotIndicatorDecoration
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_weight_added
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_weight_edited
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_weight_selected
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_wishlist_added
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.cart_bottomsheet_wishlist_removed
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.label
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.AnalyticsKeys.volume
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.AvailableVolumeV2
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class CartItemsQuantityEditFragment :
    BaseBottomSheetDialogFragment<FragmentCartQuantityEditItemsBinding>() {

    private val args by navArgs<CartItemsQuantityEditFragmentArgs>()
    private var imageAdapter: StoreItemImageAdapter? = null
    private val cartViewModelProvider by hiltNavGraphViewModels<CartItemsQuantityEditQuantityEditFragmentViewModelAndroid>(
        R.id.feature_delivery_navigation
    )

    internal val cartViewModel by  lazy {
        cartViewModelProvider.getInstance()
    }

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }
    private val spaceItemDecoration = SpaceItemDecoration(8.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartQuantityEditItemsBinding
        get() = FragmentCartQuantityEditItemsBinding::inflate

    override val bottomSheetConfig = DEFAULT_CONFIG

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private fun postClickEvent(clickTypeValue: String, eventName: String, eventValue: String) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonCheckoutGoldDelivery, mapOf(
                GoldDeliveryConstants.AnalyticsKeys.Click_type to clickTypeValue,
                eventName to eventValue
            )
        )
    }

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun getCurrentAvailableVolume(): AvailableVolumeV2? {
        return cartViewModel.currentProduct.value?.availableVolumes?.getOrNull(
            cartViewModel.currentSelectedVolumeIndex.value ?: -1
        )
    }

    private fun setupProduct() {
        val currentAvailableVolume1 = getCurrentAvailableVolume()
        imageAdapter?.submitList(currentAvailableVolume1?.media?.images)
        binding.tvName.text = currentAvailableVolume1?.description

        binding.chipGroupFilter.removeAllViews()
        cartViewModel.currentProduct.value?.availableVolumes?.forEachIndexed { index, availableVolumeV2 ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.test, binding.chipGroupFilter, false) as LinearLayout
            val findViewById = chip.findViewById<TextView>(R.id.allChip)
            findViewById.text = availableVolumeV2?.volume.toString() + " gm"
            chip.tag = availableVolumeV2?.volume
            chip.id = ViewCompat.generateViewId()

            chip.setDebounceClickListener {
                cartViewModel.setCurrentVolumeIndex(index)
                makeSelected(index)
                postClickEvent(
                    cart_bottomsheet_weight_selected,
                    volume,
                    availableVolumeV2?.volume.orZero().toString()
                )
            }

            binding.chipGroupFilter.addView(chip)
        }
        currentAvailableVolume1?.let { updatePricing(it) }
        cartViewModel.currentSelectedVolumeIndex.value?.let { makeSelected(it) }
    }

    private fun updatePricing(availableVolume: AvailableVolumeV2) {
        binding.tvPrice.text = getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            availableVolume.goldDeliveryPrice?.total?.getFormattedAmount()
        )
        when {
            availableVolume.goldDeliveryPrice?.discountOnTotal.orZero() == availableVolume.goldDeliveryPrice?.total -> {
                binding.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvPriceDiscount.text = getString(R.string.free_limited_time_offer)
                binding.tvPriceDiscount.isVisible = true
            }

            availableVolume.goldDeliveryPrice?.discountOnTotal.orZero() > 0 -> {
                binding.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvPriceDiscount.text =
                    getString(
                        R.string.rupee_x_in_double_strike,
                        (availableVolume.goldDeliveryPrice?.total!! - availableVolume.goldDeliveryPrice?.discountOnTotal.orZero())
                    ) // todo remove !!
                binding.tvPriceDiscount.isVisible = true
            }

            else -> {
                binding.tvPriceDiscount.isVisible = false
            }
        }
    }

    private fun setupUI() {
        binding.rvImages.layoutManager =
            CenterZoomLinearLayoutManager(
                requireContext(),
                1f,
                0f,
                RecyclerView.HORIZONTAL
            )

        imageAdapter = StoreItemImageAdapter()
        binding.rvImages.adapter = imageAdapter
        binding.rvImages.addItemDecorationIfNoneAdded(spaceItemDecoration)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)
        val circlePagerIndicatorDecoration = DotIndicatorDecoration(requireContext())
        binding.rvImages.addItemDecoration(circlePagerIndicatorDecoration)

        if (args.isItemAddFlow) {
            binding.btnGetDelivery.setText(getString(R.string.add_to_cart))
        } else {
            binding.btnGetDelivery.setText(getString(R.string.update_cart))
        }
    }

    override fun onDestroyView() {
        binding.rvImages.onFlingListener = null
        super.onDestroyView()
    }

    private fun observeLiveData() {
        val rootView = getRootView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                cartViewModel.isProductLiked.collectLatest {
                    isProductLiked(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                cartViewModel.showToast.collectLatest {
                    it.snackBar(
                        binding.root,
                        translationY = -4.dp.toFloat()
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                cartViewModel.currentSelectedVolumeIndex.collectLatest {
                    val availableVolume =
                        cartViewModel.currentProduct.value?.availableVolumes?.getOrNull(it)
                    availableVolume?.let { it1 -> updatePricing(it1) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

        cartViewModel.storeItemsLiveData.collectUnwrapped(
            onSuccess = {
                val product = it?.data?.products?.singleOrNull { product ->
                    product.availableVolumes?.forEachIndexed { index, it ->
                        if (args.item?.productId == it?.productId.toString()) {
                            setUi(product, index)
                            true
                        }
                    }
                    false
                }
                setupProduct()
            },
            onError = { _, _ ->

            }
        )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        cartViewModel.addItemsToCart.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                deliveryViewModel.updateCart(Unit)
                activity?.onBackPressed() // todo if any better?
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )
    }
}
    }

    private fun setUi(product: ProductV2, index: Int) {
        cartViewModel.setCurrentProduct(product, index)
    }

    private fun isProductLiked(it: Boolean) {
        binding.heartIcon.isVisible = it
        binding.notAddedHeartIcon.isVisible = !it
    }

    private fun setupListeners() {
        binding.btnGetDelivery.setDebounceClickListener {
            if (args.isItemAddFlow) {
                postClickEvent(cart_bottomsheet_weight_added, label, args.item?.label ?: "")
                addToCartMethod()
            } else {
                postClickEvent(cart_bottomsheet_weight_edited, label, args.item?.label ?: "")
                updateCartMethod()
            }
        }
        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
        binding.notAddedHeartIcon.setDebounceClickListener {
            val currentAvailableVolume = getCurrentAvailableVolume()
            cartViewModel.addToWishList(
                AddCartItemRequest(
                    currentAvailableVolume?.goldDeliveryPrice?.deliveryMakingCharge,
                    currentAvailableVolume?.productId,
                    currentAvailableVolume?.volume,
                    args.item?.label
                ), currentAvailableVolume
            )
            postClickEvent(cart_bottomsheet_wishlist_removed, label, args.item?.label ?: "")
        }
        binding.heartIcon.setDebounceClickListener {
            val currentAvailableVolume = getCurrentAvailableVolume()

            cartViewModel.removeFromWishList(
                currentAvailableVolume?.wishListId ?: "", currentAvailableVolume
            )
            postClickEvent(cart_bottomsheet_wishlist_added, label, args.item?.label ?: "")
        }
    }

    private fun addToCartMethod() {
        val currentAvailableVolume = getCurrentAvailableVolume()
        cartViewModel.addItemToCart(
            AddCartItemRequest(
                deliveryMakingCharge = args.item?.deliveryMakingCharge,
                volume = currentAvailableVolume?.volume,
                productId = currentAvailableVolume?.productId,
                quantity = 1,
                label = args.item?.label
            )
        )
    }

    private fun updateCartMethod() {
        val currentAvailableVolume = getCurrentAvailableVolume()
        if (args.item?.volume == currentAvailableVolume?.volume) {
            getString(R.string.already_in_cart).snackBar(
                binding.root,
                translationY = -4.dp.toFloat()
            )
        } else {
            cartViewModel.replaceItemFromCart(
                args.item?.id ?: "", AddCartItemRequest( //todo null check
                    currentAvailableVolume?.goldDeliveryPrice?.deliveryMakingCharge,
                    currentAvailableVolume?.productId,
                    currentAvailableVolume?.volume,
                    args.item?.label,
                    args.item?.quantity
                )
            )
        }
    }

    private fun getData() {
        cartViewModel.getAllStoreItems()
    }


    fun makeSelected(index: Int) {
        for (i in 0 until binding.chipGroupFilter.childCount) {
            val volume = cartViewModel.currentProduct.value?.availableVolumes?.getOrNull(i)
            val outOfStock =
                !(volume?.inStock
                    ?: true)
            val isOriginalCart: Boolean =
                args.item?.productId == volume?.productId.toString()
            val chipText = binding.chipGroupFilter.getChildAt(i)
                .findViewById<AppCompatTextView>(R.id.allChip)
            if (index == i) {
                val isWishListed = !TextUtils.isEmpty(
                    cartViewModel.currentProduct.value?.availableVolumes?.getOrNull(index)?.wishListId
                )
                // selected
                TextViewCompat.setTextAppearance(
                    chipText,
                    com.jar.app.core_ui.R.style.CommonBoldTextViewStyle
                )
                chipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)

                chipText.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (!outOfStock) R.drawable.round_1ea787_stroke else R.drawable.round_1ea787_stroke_top
                    )
                binding.btnGetDelivery.setDisabled((outOfStock || isOriginalCart))
                imageAdapter?.submitList(volume?.media?.images)
                isProductLiked(isWishListed)
            } else {
                TextViewCompat.setTextAppearance(
                    chipText,
                    com.jar.app.core_ui.R.style.CommonTextViewStyle
                )
                chipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                chipText.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        if (!outOfStock) R.drawable.round_3c3357_stroke else R.drawable.round_3c3357_stroke_top
                    )
            }
            binding.chipGroupFilter.getChildAt(i)
                .findViewById<View>(R.id.outOfStockBanner).isVisible = outOfStock

        }
    }
}