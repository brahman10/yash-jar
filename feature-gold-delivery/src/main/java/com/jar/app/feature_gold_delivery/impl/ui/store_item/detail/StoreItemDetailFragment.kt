package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail

import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
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
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.base.util.hideKeyboard
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.expandable_rv.DailySavingsV2ExpandableFaqAdapter
import com.jar.app.core_ui.extension.getStringOrNull
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setUpOverScroll
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.widget.zoom_layout_manager.CenterZoomLinearLayoutManager
import com.jar.app.feature_gold_delivery.R
import kotlinx.coroutines.flow.collectLatest
import com.jar.app.feature_gold_delivery.databinding.FragmentStoreItemDetailBinding
import com.jar.app.feature_gold_delivery.impl.helper.Utils.buildSpannableStringForGoldPrice
import com.jar.app.feature_gold_delivery.impl.helper.Utils.calculateQuantityItemsString
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.DotIndicatorDecoration
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.StoreItemAdapter
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.DELETE_ALL_ITEMS_FROM_BOTTOMSHEET
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_gold_delivery.shared.domain.model.AvailableVolumeV2
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData
import com.jar.app.feature_user_api.domain.model.PinCodeEligibility
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductV2
import com.jar.app.feature_gold_delivery.shared.domain.model.ProductsV2
import com.jar.app.feature_gold_delivery.shared.util.CartDataHelper.calculateTotalAmountFromCart
import com.jar.app.feature_gold_delivery.shared.util.CartDataHelper.createMapForAnalytics
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class StoreItemDetailFragment : BaseFragment<FragmentStoreItemDetailBinding>() {

    private val args by navArgs<StoreItemDetailFragmentArgs>()

    private var imageAdapter: StoreItemImageAdapter? = null

    private val spaceItemDecoration =
        SpaceItemDecoration(8.dp, 8.dp)

    private val viewModelProvider by viewModels<StoreItemDetailFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    private var isFirstAddress = true

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStoreItemDetailBinding
        get() = FragmentStoreItemDetailBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    companion object {
        const val My_wishlist = "My wishlist"
        const val My_cart = "My cart"
        const val Add_to_wishlist = "Add to wishlist"
        const val Weights_of_the_coin = "Weights of the coin"
        const val Add_to_cart = "Add to cart"
        const val Pincode_entry = "Pincode entry"

        const val More_info = "More info"
        const val product_details = "product_details"
        const val delivery_details = "delivery_details"
        const val Cancelltion = "cancelltion_refund"
        const val refund = "refund"
        const val FAQs = "FAQs"
        const val Contact_support = "Contact support"
        const val Similar_products = "Similar products"

        const val Quantity_counter = "Quantity counter"
        const val Proceed = "Proceed CTA"
        const val Notify_me = "Notify me"
        const val Add_to_wishlist_CTA = "Add to wishlist CTA"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ShownPDPGoldDelivery,
        )
        setupUI()
        setupListeners()
        observeLiveData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun setupLiveCounter(fetchCurrentGoldPriceResponse: FetchCurrentGoldPriceResponse) {
        binding.goldPriceProgressLayout.start(
            livePriceMessage = "",
            spannedPriceMessage = buildSpannableStringForGoldPrice(
                fetchCurrentGoldPriceResponse.price,
                WeakReference(requireContext())
            ),
            validityInMillis = fetchCurrentGoldPriceResponse.getValidityInMillis(),
            uiScope = uiScope,
            onFinish = {
                viewModel.fetchCurrentBuyPrice()
            },
        )
        binding.goldPriceProgressLayout.setProgressColor("#993C4568")
        binding.goldPriceProgressLayout.setTextAppearance(com.jar.app.core_ui.R.style.CommonTextViewStyle)
    }

    fun generateStringFunction(): (id: Int) -> String {
        return {
            getString(id)
        }
    }
    override fun onDestroyView() {
        imageAdapter = null
        binding.rvImages.onFlingListener = null
        super.onDestroyView()
    }

    fun switchToWistListButton(outOfStock: Boolean, isWishListed: Boolean) {

        if (outOfStock) {
            // show wishlist
            binding.btnGetDelivery.setDisabled(isWishListed)

            binding.btnGetDelivery.isVisible = !isWishListed
            binding.addedToWishlist.isVisible = isWishListed

            binding.btnGetDelivery.setText(getString(R.string.add_to_wishlist))
            binding.btnGetDelivery.setDebounceClickListener {
                val currentAvailableVolume = getCurrentAvailableVolume()
                viewModel.addToWishList(
                    AddCartItemRequest(
                        currentAvailableVolume?.goldDeliveryPrice?.deliveryMakingCharge,
                        currentAvailableVolume?.productId,
                        currentAvailableVolume?.volume,
                        getProductResponse()?.label,
                    ), currentAvailableVolume
                ) { stringResource ->
                    context?.let { getCustomString(it, stringResource) } ?: ""
                }

                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery, mapOf<String, String>(
                        GoldDeliveryConstants.AnalyticsKeys.volume to currentAvailableVolume?.volume.toString(),
                        GoldDeliveryConstants.AnalyticsKeys.label to getProductResponse()?.label.toString(),
                        GoldDeliveryConstants.AnalyticsKeys.Click_type to Add_to_wishlist_CTA
                    )
                )
            }
        } else {
            // show cart
            binding.btnGetDelivery.setDisabled(false)
            binding.btnGetDelivery.setText(getString(R.string.add_to_cart))
            binding.btnGetDelivery.setDebounceClickListener {
                val currentAvailableVolume = getCurrentAvailableVolume()
                viewModel.addToCart(
                    AddCartItemRequest(
                        currentAvailableVolume?.goldDeliveryPrice?.deliveryMakingCharge,
                        currentAvailableVolume?.productId,
                        currentAvailableVolume?.volume,
                        getProductResponse()?.label,
                    )
                )
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery, mapOf<String, String>(
                        GoldDeliveryConstants.AnalyticsKeys.volume to currentAvailableVolume?.volume.toString(),
                        GoldDeliveryConstants.AnalyticsKeys.title to (getProductResponse()?.label
                            ?: GoldDeliveryConstants.AnalyticsKeys.label),
                        GoldDeliveryConstants.AnalyticsKeys.Click_type to Add_to_cart
                    )
                )
            }
        }
    }

    fun setupWithData(productsV2: ProductsV2) {
        val productFromLabel = viewModel.getProductFromLabel(productsV2, args.label)
        val currentAvailableVolume1 = productFromLabel?.availableVolumes?.get(0)

        imageAdapter?.submitList(currentAvailableVolume1?.media?.images)
        binding.chipGroupFilter.removeAllViews()
        productFromLabel?.availableVolumes?.forEachIndexed { index, availableVolumeV2 ->
            val chip = LayoutInflater.from(context)
                .inflate(R.layout.test, binding.chipGroupFilter, false) as LinearLayout
            val findViewById = chip.findViewById<TextView>(R.id.allChip)
            findViewById.text = availableVolumeV2?.volume.toString() + " gm"
            chip.id = ViewCompat.generateViewId()
            findViewById.tag = availableVolumeV2?.productId

            chip.setDebounceClickListener {
                viewModel.setCurrentVolumeIndex(index)
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                    Weights_of_the_coin,
                    availableVolumeV2?.volume.toString()
                )
            }

            binding.chipGroupFilter.addView(chip)
        }
        setIndex(viewModel.currentSelectedVolumeIndex.value)

        viewModel.currentCartLiveData.value?.data?.data?.let {
            showCartIconInPills(it.cartItemData)
        }
    }

    private fun setupUI() {
        Glide.with(binding.root)
            .load(BaseConstants.ImageUrlConstants.DELIVERY_BOTTOM)
            .into(binding.ivDeliveryBottom)
        binding.nsv.setUpOverScroll()
        binding.rvImages.layoutManager =
            CenterZoomLinearLayoutManager(
                requireContext(),
                1f,
                0f,
                RecyclerView.HORIZONTAL
            )
        binding.moreOrderedContainer.isVisible = false

        imageAdapter = StoreItemImageAdapter()
        binding.rvImages.adapter = imageAdapter
        binding.rvImages.addItemDecorationIfNoneAdded(spaceItemDecoration)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)
        val circlePagerIndicatorDecoration = DotIndicatorDecoration(requireContext())
        binding.rvImages.addItemDecoration(circlePagerIndicatorDecoration)
        binding.notAddedHeartIcon.setDebounceClickListener {
            val currentAvailableVolume = getCurrentAvailableVolume()
            viewModel.addToWishList(
                AddCartItemRequest(
                    currentAvailableVolume?.goldDeliveryPrice?.deliveryMakingCharge,
                    currentAvailableVolume?.productId,
                    currentAvailableVolume?.volume,
                    getProductResponse()?.label
                ), currentAvailableVolume
            ) { stringResource ->
                context?.let { getCustomString(it, stringResource) } ?: ""
            }
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                Add_to_wishlist,
                currentAvailableVolume?.productId.orZero().toString()
            )
        }
        binding.heartIcon.setDebounceClickListener {
            val currentAvailableVolume = getCurrentAvailableVolume()

            viewModel.removeFromWishList(
                currentAvailableVolume
            ) { stringResource ->
                context?.let { getCustomString(it, stringResource) } ?: ""
            }
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.remove_from_wishlist,
                GoldDeliveryConstants.AnalyticsKeys.remove_from_wishlist,
                currentAvailableVolume?.productId.orZero().toString()
            )
        }
        binding.cartContainer.setDebounceClickListener {
            val data = viewModel.currentCartLiveData.value?.data?.data
            val action = data?.let { it1 ->
                StoreItemDetailFragmentDirections
                    .actionStoreItemDetailFragmentToCartItemsFragment2(
                        binding.etPinCode.text?.getStringOrNull() ?: "", it1
                    )
            }
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                createMapForAnalytics(My_cart, data).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.cart_container
                    )
                }
            )
            action?.let { it1 -> navigateTo(it1) }
        }

        setupCollapsibleMoreInfo()
        setupCollapsibleFaq()
        binding.cartCounter.setListener(object : CartItemQuantityViewListener {
            override fun counterAdded() {
                showProgressBar()
                val currentAvailableVolumeV2 = getCurrentAvailableVolume()
                val added = AddCartItemRequest(
                    currentAvailableVolumeV2?.goldDeliveryPrice?.deliveryMakingCharge,
                    currentAvailableVolumeV2?.productId,
                    currentAvailableVolumeV2?.volume,
                    getProductResponse()?.label
                )
                viewModel.addToCart(added)
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                    Quantity_counter,
                    "Added"
                )
            }

            override fun counterSubtracted(quantity: Int) {
                val cartItemData = viewModel.currentCartLiveData.value?.data?.data
                val currentAvailableVolume = getCurrentAvailableVolume()
                if (currentAvailableVolume == null || cartItemData == null || getProductResponse()?.label == null)
                    return // todo handle this

                val checkIfVolumeExistsInCart = checkIfVolumeExistsInCart(
                    currentAvailableVolume,
                    cartItemData,
                    getProductResponse()?.label ?: ""
                )
                checkIfVolumeExistsInCart?.id?.let {
                    viewModel.removeFromCart(it, quantity)
                }
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                    Quantity_counter,
                    "Subtracted"
                )
            }
        })
        binding.btnCheck.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            requireContext().hideKeyboard(binding.etPinCode)
            if (!pinCode.isNullOrBlank() && pinCode.length == 6) {
                viewModel.validatePinCode(pinCode.toString())
            }
            binding.etPinCode.clearFocus()
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                Pincode_entry,
                pinCode.toString()
            )
        }
        binding.btnClear.setDebounceClickListener {
            val pinCode = binding.etPinCode.text
            binding.etPinCode.showKeyboard()
            binding.etPinCode.requestFocus()
            binding.etPinCode.setText("")
            binding.btnClear.isVisible = false
            binding.btnCheck.isVisible = true
            viewModel.clearPincode()
            binding.tvPinCodeEligibilityText.text = ""
            binding.tvPinCodeEligibilityText.isVisible = false
            binding.tvPinCodeEligibilityText.setCompoundDrawables(null, null, null, null)
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.pincode_clear,
                Pincode_entry,
                pinCode.toString() ?: ""
            )
        }
        binding.notifyMeTv.setDebounceClickListener {
            viewModel.notifyUser(getCurrentAvailableVolume(), getProductResponse()?.label)
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                Notify_me,
                args.label ?: "Notify_me"
            )
        }
        args.pinCodeEntered.takeIf { !TextUtils.isEmpty(it) }?.let {
            binding.etPinCode.setText(it)
        }


        similarRVAdapter = StoreItemAdapter({
            val action =
                StoreItemDetailFragmentDirections.actionStoreItemDetailFragmentSelf(
                    it.label ?: "", binding.etPinCode.text?.getStringOrNull() ?: ""
                )
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.similar_item,
                Similar_products,
                args.label ?: ""
            )
            navigateTo(action)
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                Similar_products,
                args.label ?: ""
            )
        })
        binding.similarRv.isVisible = true
        binding.similarRv.adapter = similarRVAdapter
        binding.similarRv.addItemDecorationIfNoneAdded(
            SpaceItemDecoration(
                4.dp,
                0.dp,
                escapeEdges = false,
                orientation = RecyclerView.HORIZONTAL
            )
        )
        binding.similarRv.layoutManager =
            object :
                GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    lp?.width = width / 2
                    return super.checkLayoutParams(lp)
                }
            }
    }

    private var similarRVAdapter: StoreItemAdapter? = null
    private fun makeSelected(index: Int) {
        for (i in 0 until binding.chipGroupFilter.childCount) {
            val volumeV2 = getProductResponse()?.availableVolumes?.getOrNull(i)
            val outOfStock = !(volumeV2?.inStock ?: true)
            val chipText = binding.chipGroupFilter.getChildAt(i)
                .findViewById<AppCompatTextView>(R.id.allChip)
            if (index == i) {
                val isWishListed =
                    !TextUtils.isEmpty(volumeV2?.wishListId)
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
                isProductLiked(isWishListed)
                imageAdapter?.submitList(volumeV2?.media?.images)
                switchToWistListButton(outOfStock, isWishListed)
                binding.tvName.text = args.label
                binding.notifiedContainer.isVisible = outOfStock
                if (volumeV2?.isSetToNotify == true) {
                    binding.notifyMeTv.isVisible = false
                    binding.notifiedLabel.isVisible = false
                    binding.notified.isVisible = true
                } else {
                    binding.notifyMeTv.isVisible = true
                    binding.notifiedLabel.isVisible = true
                    binding.notified.isVisible = false
                }
                val curateMoreInfoList = curateMoreInfoList(volumeV2)
                curateMoreInfoList?.let {
                    viewModel.setMoreInfoList(it)
                }
                volumeV2?.noOfPeopleOrdered?.takeIf { it.isNotBlank() }?.let {
                    binding.moreOrderedContainer.isVisible = true
                    binding.moreOrderedTv.text =
                        getString(R.string.people_have_orderded_this_item, it)
                    volumeV2?.peopleImages?.takeIf { it.isNotEmpty() }?.let {
                        binding.overlappingView2.submitProfilePics(it)
                        binding.overlappingView2.isVisible = true
                    } ?: run {
                        binding.overlappingView2.isVisible = false
                    }
                } ?: run {
                    binding.moreOrderedContainer.isVisible = false
                }

                volumeV2?.alertStrip?.let {
                    renderAlertStrip(
                        it,
                        binding.productLabelBg,
                        binding.productLabelIv,
                        binding.productLabelTv,
                        binding.productLabelBgShadow,
                    )
                } ?: run {
                    binding.productLabelBg.isVisible = false
                    binding.productLabelIv.isVisible = false
                    binding.productLabelTv.isVisible = false
                    binding.productLabelBgShadow.isVisible = false
                }
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

    private fun setupCollapsibleMoreInfo() {
        binding.moreInfoContent.apply {
            moreInfoAdapter = DailySavingsV2ExpandableFaqAdapter {
                updateMoreInfoList(it)
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                    More_info,
                    (viewModel.moreInfoLiveData.value?.getOrNull(it) as? ExpandableDataItem.LeftIconIsExpandedDataType)?.question
                        ?: ""
                )
            }
            this.adapter = moreInfoAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private var moreInfoAdapter: DailySavingsV2ExpandableFaqAdapter? = null
    private var faqAdapter: DailySavingsV2ExpandableFaqAdapter? = null

    private fun updateMoreInfoList(position: Int) {
        moreInfoAdapter?.let {
            viewModel.updateMoreInfoList(it.currentList, position)
        }
    }

    private fun updateFaqList(position: Int) {
        faqAdapter?.let {
            viewModel.updateFAQList(it.currentList, position)
        }
    }

    private fun setupCollapsibleFaq() {
        binding.faqContent.apply {
            faqAdapter = DailySavingsV2ExpandableFaqAdapter {
                updateFaqList(it)
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                    FAQs,
                    (viewModel.faqLiveData.value?.getOrNull(it) as? ExpandableDataItem.CardHeaderIsExpandedDataType)?.question
                        ?: ""
                )
            }
            this.adapter = faqAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getCurrentAvailableVolume(): AvailableVolumeV2? {
        val items = getProductResponse()
        return items?.availableVolumes?.getOrNull(viewModel.currentSelectedVolumeIndex.value ?: -1)
    }

    private fun setupListeners() {
        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.heartBtn.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                My_wishlist,
                "Wishlist screen opened"
            )
            navigateTo(StoreItemDetailFragmentDirections.actionStoreItemDetailFragmentToWishlistFragment())
        }
        binding.cartBtn.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                createMapForAnalytics(
                    My_cart,
                    viewModel.currentCartLiveData.value?.data?.data
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.cart_tab
                    )
                }
            )
            val actionDeliveryStoreItemListFragmentToDeliveryStoreCartFragment =
                StoreItemDetailFragmentDirections.actionStoreItemDetailFragmentToDeliveryStoreCartFragment(
                    binding.etPinCode.text.toString()
                )
            navigateTo(actionDeliveryStoreItemListFragmentToDeliveryStoreCartFragment)
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
        binding.topImageRight.setDebounceClickListener {
            binding.deliveringContainer.isVisible = false
            binding.pincodeContainer.isVisible = true
            binding.btnCheck.isVisible = false
            binding.btnClear.isVisible = true
        }
        binding.contactSupportTv.setDebounceClickListener {
            val number = remoteConfigManager.getWhatsappNumber()
            val message = getString(R.string.hey_need_some_help_buying_a_product)
            it.context.openWhatsapp(number, message)
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                Contact_support,
                args.label + "::" + getCurrentAvailableVolume()?.productId?.orZero().toString()
            )
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
//                binding.clPlaceholderPinCodeNotServiceable.isVisible = false
//                binding.tvHeaderOptions.isVisible = true
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
        binding.btnProceedCart.setDebounceClickListener {
            val action =
                StoreItemDetailFragmentDirections.actionStoreItemDetailFragmentToDeliveryStoreCartFragment(
                    binding.etPinCode.text.toString()
                )
            navigateTo(action)

            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery,
                createMapForAnalytics(
                    Proceed,
                    viewModel.currentCartLiveData.value?.data?.data
                ).toMutableMap().apply {
                    put(
                        GoldDeliveryConstants.AnalyticsKeys.Click_type,
                        GoldDeliveryConstants.AnalyticsKeys.cart_proceed
                    )
                }
            )
        }
    }

    private fun postClickEvent(s: String, contactSupport: String, s1: String) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickPDPGoldDelivery, mapOf(
                GoldDeliveryConstants.AnalyticsKeys.Click_type to s,
                contactSupport to s1
            )
        )
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
                        (availableVolume.goldDeliveryPrice?.total.orZero() - availableVolume.goldDeliveryPrice?.discountOnTotal.orZero())
                    )
                binding.tvPriceDiscount.isVisible = true
            }

            else -> {
                binding.tvPriceDiscount.isVisible = false
            }
        }
    }

    private fun getProductResponse(): ProductV2? {
        return viewModel.getProductFromLabel(args.label)
    }

    private fun setIndex(index: Int) {
        val items = getProductResponse()
        val availableVolume = items?.availableVolumes?.getOrNull(index)
        makeSelected(index)
        availableVolume?.let { it1 ->
            updatePricing(it1)
            updateCartCounter(availableVolume, items.label ?: "")
        } ?: run {
//                throw java.lang.RuntimeException("Volume shouldn't be null") // todo gracefully handle this
        }

    }
    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentSelectedVolumeIndex.collectLatest {
                    setIndex(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                deliveryViewModel.cartUpdate.collectLatest {
                    viewModel.fetchNewCart()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.moreInfoLiveData.collectLatest {

                    moreInfoAdapter?.submitList(it)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.moreInfoLiveData.collectLatest {
                    faqAdapter?.submitList(it)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.storeItemsLiveData.collectUnwrapped(
                    onSuccess = {
                        it.data?.let { it1 ->
                            setupWithData(it1)
                            similarRVAdapter?.submitList(it1.products)
                        }
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.buyPriceLiveData.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let { it1 -> setupLiveCounter(it1.data) }
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isProductLiked.collectLatest {
                    isProductLiked(it)
                    val equals = binding.btnGetDelivery.getText()
                        .equals(getString(R.string.add_to_wishlist), ignoreCase = true)
                    if (equals && it) {
                        binding.btnGetDelivery.isVisible = false
                        binding.addedToWishlist.isVisible = true
                    } else if (equals && it == false) {
                        binding.addedToWishlist.isVisible = false
                        binding.btnGetDelivery.setDisabled(false)
                        binding.btnGetDelivery.isVisible = true
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.showToast.collectLatest {
                    it.snackBar(
                        binding.root,
                        translationY = -4.dp.toFloat(),
                        iconRes = R.drawable.checkmark_icon
                    )
                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isProceedBtnEnabled.collectLatest {
                    binding.btnProceedCart.isEnabled = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.validatePinCodeLiveData.collectUnwrapped(
                    onSuccess = {
                        requireContext().hideKeyboard(binding.root)
                        it ?: return@collectUnwrapped
                        val it = it.data
                        when (it?.getEligibilityStatus()) {
                            PinCodeEligibility.DELIVERABLE -> {
                                binding.deliveringContainer.isVisible = true
                                binding.pincodeContainer.isVisible = false
                                binding.deliveryLocationTv.text =
                                    "${it.city} (${it.pinCode})"
                                binding.btnCheck.isVisible = false
                                binding.btnClear.isVisible = false


                                binding.deliveringTV.text =
                                    getString(R.string.we_deliver_to_your_location)
                                binding.deliveringTV.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        com.jar.app.core_ui.R.color.color_58DDC8
                                    )
                                )
                                binding.labelDelivery.text =
                                    getString(R.string.delivering_to)
                                postClickEvent(
                                    GoldDeliveryConstants.AnalyticsKeys.pincode_serviceable,
                                    GoldDeliveryConstants.AnalyticsKeys.pincode_check,
                                    it.pinCode ?: ""
                                )
                            }

                            PinCodeEligibility.NOT_DELIVERABLE -> {
                                postClickEvent(
                                    GoldDeliveryConstants.AnalyticsKeys.pincode_not_serviceable,
                                    GoldDeliveryConstants.AnalyticsKeys.pincode_check,
                                    it.pinCode ?: ""
                                )
                                binding.deliveringContainer.isVisible = false
                                binding.pincodeContainer.isVisible = true
                                binding.tvPinCodeEligibilityText.isVisible = true
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
                                if (TextUtils.isEmpty(it.city)) {
                                    binding.tvPinCodeEligibilityText.text =
                                        getString(R.string.invalid_pincode)
                                } else {
                                    binding.tvPinCodeEligibilityText.text =
                                        getString(
                                            R.string.city_state_is_unserviceable,
                                            it.city,
                                            it.state
                                        )
                                }
                                binding.tvPinCodeEligibilityText.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        com.jar.app.core_ui.R.color.color_EB6A6E
                                    )
                                )
                                binding.deliveryLocationTv.text = ""

                                binding.btnCheck.isVisible = false
                                binding.btnClear.isVisible = true
                                binding.deliveringTV.text =
                                    getString(R.string.your_area_is_unservicable_at_the_moment)
                                binding.labelDelivery.text =
                                    getString(R.string.your_area_is_unservicable_at_the_moment)
                                binding.deliveringTV.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        com.jar.app.core_ui.R.color.color_EB6A6E
                                    )
                                )
                            }

                            else -> {}
                        }
                    })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentCartLiveData.collectUnwrapped(
                    onLoading = {
                        binding.btnGetDelivery.isEnabled = false
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it.data?.let { it1 -> setupCart(it1) }
                        getCurrentAvailableVolume()?.let { it1 ->
                            updateCartCounter(
                                it1,
                                getProductResponse()?.label ?: ""
                            )
                        }
                        binding.btnGetDelivery.setDisabled(false)
                    },
                    onError = { _, _ ->
                        binding.btnGetDelivery.isEnabled = true
                        dismissProgressBar()
                    }
                )
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            DELETE_ALL_ITEMS_FROM_BOTTOMSHEET
        )?.observe(viewLifecycleOwner) {
            viewModel.fetchNewCart()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.notifyLiveData.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        getString(R.string.product_will_be_notified).snackBar(
                            binding.root,
                            translationY = -4.dp.toFloat()
                        )
                        binding.notifyMeTv.isVisible = false
                        binding.notifiedLabel.isVisible = false
                        binding.notified.isVisible = true
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        getString(R.string.product_will_be_notified).snackBar(
                            binding.root,
                            translationY = -4.dp.toFloat()
                        )
                        binding.notifyMeTv.isVisible = false
                        binding.notifiedLabel.isVisible = false
                        binding.notified.isVisible = true
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun curateMoreInfoList(it: AvailableVolumeV2?): List<ExpandableDataItem>? {
        return it?.productSpecifications?.takeIf { !it.isNullOrEmpty() }?.mapNotNull {
            ExpandableDataItem.LeftIconIsExpandedDataType(
                imageUrl = it?.icon,
                question = it?.key ?: "",
                answer = it?.value ?: ""
            )
        } as List<ExpandableDataItem>?
    }

    private fun isProductLiked(it: Boolean) {
        binding.heartIcon.isVisible = it
        binding.notAddedHeartIcon.isVisible = !it
    }

    private fun updateCartCounter(availableVolume: AvailableVolumeV2, label: String) {
        viewModel.currentCartLiveData.value?.data?.data?.let {
            val cartItem = checkIfVolumeExistsInCart(availableVolume, it, label)
            if (cartItem != null && cartItem.quantity.orZero() >= 1) {
                binding.cartCounter.isVisible = true
                binding.addedToWishlist.isVisible = false
                binding.btnGetDelivery.isVisible = false
                cartItem.quantity?.let { it1 -> binding.cartCounter.setCount(it1) }
            } else {
                val currentAvailableVolume = getCurrentAvailableVolume()
                if (currentAvailableVolume?.inStock == true) {
                    binding.btnGetDelivery.isVisible = true
                } else if (currentAvailableVolume?.wishListId.isNullOrBlank()) {
                    binding.btnGetDelivery.isVisible = true
                    binding.addedToWishlist.isVisible = false
                } else {
                    binding.btnGetDelivery.isVisible = false
                    binding.addedToWishlist.isVisible = true
                }
                binding.cartCounter.isVisible = false
            }
        } ?: run {

        }
    }

    private fun checkIfVolumeExistsInCart(
        availableVolume: AvailableVolumeV2,
        cartAPIData: CartAPIData,
        currentLabel: String
    ): CartItemData? {
        cartAPIData.cartItemData?.forEach {
            if (it?.volume == availableVolume.volume && it?.label == currentLabel) {
                return it
            }
        }
        return null
    }

    private fun setupCart(it: CartAPIData) {
        binding.cartContainer.isVisible = !it.cartItemData.isNullOrEmpty()
        showCartIconInPills(it.cartItemData)
        it.cartItemData?.takeIf { it.isNotEmpty() }?.let {
            binding.cartNoItems.setText(it.size.toString())
            binding.cartNoItems.isVisible = true
        } ?: run {
            binding.cartNoItems.isVisible = false
        }
        if (it.cartItemData.isNullOrEmpty()) return
        binding.tvCartPrice.text = context?.getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            calculateTotalAmountFromCart(it.cartItemData).getFormattedAmount()
        )
        binding.tvCartQuantity.text =
            calculateQuantityItemsString(WeakReference(requireContext()), it)

        // Determine if current selection is in cart so set the counter value
        getCurrentAvailableVolume()?.apply {
            val checkIfVolumeExistsInCart =
                checkIfVolumeExistsInCart(this, it, getProductResponse()?.label ?: "")
            if (checkIfVolumeExistsInCart != null) {
                checkIfVolumeExistsInCart.quantity?.let { it1 -> binding.cartCounter.setCount(it1) }
            }
        }

    }

    private fun showCartIconInPills(cartItemData: List<CartItemData?>?) {
        val cartIcon = ContextCompat.getDrawable(requireContext(), R.drawable.cart_with_check)
        val hashSet = HashSet<String?>()
        cartItemData?.forEach { hashSet.add(it?.productId) }
        for (i in 0 until binding.chipGroupFilter.childCount) {
            val chipText = binding.chipGroupFilter.getChildAt(i)
                .findViewById<AppCompatTextView>(R.id.allChip)
            val productID = chipText.tag as Int?
            if (hashSet.contains(productID.toString())) {
                chipText.setCompoundDrawablesWithIntrinsicBounds(cartIcon, null, null, null)
                // cart contains this
            } else {
                // cart doesn't has this item
                chipText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
    }

    private fun getData() {
        viewModel.getAllStoreItems()
        viewModel.fetchNewCart()
        viewModel.fetchFaqs()
        args.pinCodeEntered.takeIf { !TextUtils.isEmpty(it) }?.let {
            viewModel.validatePinCode(it)
        }
        viewModel.fetchCurrentBuyPrice()
    }
}
