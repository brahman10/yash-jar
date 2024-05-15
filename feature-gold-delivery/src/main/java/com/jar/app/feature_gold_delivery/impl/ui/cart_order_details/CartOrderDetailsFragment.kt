package com.jar.app.feature_gold_delivery.impl.ui.cart_order_details

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.base.util.openWhatsapp
import com.jar.app.base.util.shareAsText
import com.jar.app.core_base.data.dto.GoldDeliveryTrackingStatusEnum
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.parseColorStringFromBackend
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter

import com.jar.app.core_ui.util.truncateAndAddDot
import com.jar.app.core_ui.widget.zoom_layout_manager.CenterZoomLinearLayoutManager
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartOrderDetailsBinding
import com.jar.app.feature_gold_delivery.impl.helper.Utils.getStatusEnum
import com.jar.app.feature_gold_delivery.impl.ui.cart_order_details.CartMyOrderHelper.constructLabelValueListForTracking
import com.jar.app.feature_gold_delivery.impl.ui.cart_order_details.CartMyOrderHelper.isCancelOrderVisible
import com.jar.app.feature_gold_delivery.impl.ui.store_item.detail.ExpandableHelper
import com.jar.app.feature_gold_delivery.impl.ui.store_item.detail.StoreItemImageAdapter
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.DotIndicatorDecoration
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.StoreItemAdapter
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.app.feature_gold_delivery.shared.domain.model.AddCartItemRequest
import com.jar.app.feature_transaction.shared.domain.model.NewTransactionDetails
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CartOrderDetailsFragment : BaseFragment<FragmentCartOrderDetailsBinding>() {

    private val args by navArgs<CartOrderDetailsFragmentArgs>()

    private val viewModelProvider by viewModels<CartOrderDetailFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 6.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartOrderDetailsBinding
        get() = FragmentCartOrderDetailsBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    companion object {
        const val Track_order_in_the_banner = "Track order in the banner"
        const val Track_order_in_the_card = "Track order in the card"
        const val Cancel_order = "Cancel order"
        const val Contact_support = "Contact support"
        const val Similar_products = "Similar products"
        const val Order_again = "Order again"
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.order_details), true, backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(eventName = GoldDeliveryConstants.AnalyticsKeys.ShownStatusScreenGoldDelivery)
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI(goldDeliveryTransactionDetails: NewTransactionDetails) {
        goldDeliveryTransactionDetails.paymentDetails?.let {
            binding.tvName.text = goldDeliveryTransactionDetails.productDetails?.productName
                ?: it.label
                        ?: ""
            binding.desc.text = context?.getString(
                R.string.item_quantity_gm,
                it.quantity ?: 1,
                it.volume
            )
        }
    }

    private fun setupUI() {

    }

    private fun setupListeners() {
        binding.shareIcon.setDebounceClickListener {
            val productName = viewModel.currentCartLiveData.value?.data?.data?.productDetails?.productName ?: ""
            val productId = viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.productId.orZero().toString()
            val deeplink = "dl.myjar.app/goldCoinStore/${productId}/"
            val strings = "Hey! Check out this ${productName} on Jar App! | 24k 99.5% Pure Gold Coins to Celebrate Every Occasion - ${deeplink}"
            requireContext().shareAsText("Share this Gold Coin", strings)
        }
        binding.cartMyDetailContainer.downloadInvoiceTv.setDebounceClickListener {
            viewModel.currentCartLiveData.value?.data?.data?.invoiceLink?.let {
                webPdfViewerApi.openPdf(it)
            } ?: run {
                getString(R.string.something_went_wrong).snackBar(binding.root)
            }
        }
        binding.cartMyDetailContainer.helpContainer.cancelOrderContainer.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                Cancel_order,
                viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                    ?: "CANCEL_ORDER",
            )
            navigateToCancelOrder()
        }
        binding.cartMyDetailContainer.helpContainer.contactSupportContainer.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                Contact_support,
                viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                    ?: "HELP_SUPPORT"
            )
            contactUsFlow()
        }
        binding.cartMyDetailContainer.trackOrder.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                Track_order_in_the_card,
                viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                    ?: "TRACK_ORDER"
            )
            trackOrderFlow()
        }
    }

    fun navigateToCart() {
        navigateTo(
            CartOrderDetailsFragmentDirections.actionCartMyOrderDetailFragmentToDeliveryStoreCartFragment(
                ""
            ), popUpTo = R.id.deliveryStoreItemListFragment, inclusive = false
        )
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.currentCartLiveData.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                it?.data?.let {
                    setupUI(it)
                    setupCollapisble()
                    setupOrderDetails(it)
                    setupRvImages(it)
                    setupTopbar(it)
                }
                setupSimilarProducts()
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.addToCartLiveData.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                navigateToCart()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                navigateToCart()
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

        viewModel.storeItemsLiveData.collectUnwrapped(
            onSuccess = {
                val adapter = StoreItemAdapter({
                    val action =
                        CartOrderDetailsFragmentDirections.actionCartMyOrderDetailFragmentToStoreItemDetailFragment(
                            it.label ?: "", ""
                        )
                    navigateTo(action)
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                        Similar_products,
                        it.label ?: "SIMILAR_PRODUCT"
                    )
                })
                binding.cartMyDetailContainer.similarRv.isVisible = true
                binding.cartMyDetailContainer.similarRv.adapter = adapter
                binding.cartMyDetailContainer.similarRv.addItemDecorationIfNoneAdded(
                    SpaceItemDecoration(
                        4.dp,
                        0.dp
                    )
                )
                binding.cartMyDetailContainer.similarRv.layoutManager =
                    object :
                        GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false) {
                        override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                            lp?.width = width / 2
                            return super.checkLayoutParams(lp)
                        }
                    }
                adapter.submitList(it?.data?.products)
            }
        )
            }
        }
    }

    private fun setupSimilarProducts() {
        viewModel.fetchProducts()
    }


    private fun setupTopbar(it: NewTransactionDetails) {
        binding.trackerBarGRoup.isVisible = true
        binding.trackerBarTV.setTextColor(
            Color.parseColor(
                it.trackingInfo?.statusColor ?: "#EBB46A"
            )
        )
        binding.trackerBarTV.text = it.trackingInfo?.statusHeader ?: it.trackingInfo?.statusText
        val bgColor = it.trackingInfo?.bgColor.parseColorStringFromBackend()

        when (getStatusEnum(it.trackingInfo?.status)) {
            GoldDeliveryTrackingStatusEnum.PENDING, GoldDeliveryTrackingStatusEnum.PACKAGE_PENDING -> {
                binding.trackerBar.setBackgroundColor(
                    bgColor ?: ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_43353B
                    )
                )
                binding.trackerBarButton.text = getString(R.string.track_order)
                binding.trackerBarButton.setDebounceClickListener {
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                        Track_order_in_the_banner,
                        viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                            ?: "Track_order_in_the_banner"
                    )
                    trackOrderFlow()
                }
            }

            GoldDeliveryTrackingStatusEnum.TN_TRANSIT, GoldDeliveryTrackingStatusEnum.PACKED, GoldDeliveryTrackingStatusEnum.DISPATCHED -> {
                binding.trackerBar.setBackgroundColor(
                    bgColor ?: ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_547be1
                    )
                )
                binding.trackerBarButton.text = getString(R.string.track_order)
                binding.trackerBarButton.setDebounceClickListener {
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                        Track_order_in_the_banner,
                        viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                            ?: "Track_order_in_the_banner"
                    )
                    trackOrderFlow()
                }
            }

            GoldDeliveryTrackingStatusEnum.DELIVERED -> {
                binding.trackerBar.setBackgroundColor(
                    bgColor ?: ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_1EA787
                    )
                )
                binding.trackerBarButton.text = getString(R.string.order_again)
                binding.trackerBarButton.setDebounceClickListener {
                    orderAgainFlow()
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                        Order_again,
                        viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                            ?: "Order_again"
                    )
                }
            }

            GoldDeliveryTrackingStatusEnum.FAILED -> {
                binding.trackerBar.setBackgroundColor(
                    bgColor ?: ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.color_EB6A6E
                    )
                )
                binding.trackerBarButton.text = getString(R.string.contact_support)
                binding.trackerBarButton.setDebounceClickListener {
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonStatusScreenGoldDelivery,
                        Contact_support,
                        viewModel.currentCartLiveData.value?.data?.data?.paymentDetails?.label
                            ?: "Contact_support"
                    )
                    contactUsFlow()
                }
            }

            null -> {
                throw RuntimeException()
            }
        }
    }

    private fun trackOrderFlow() {
        findNavController().navigate(Uri.parse("android-app://com.jar.app/transactionDetail/${args.orderId}/${args.assetTxnId}/${args.assetSourceType}"))
    }

    private fun orderAgainFlow() {
        val data = viewModel.currentCartLiveData.value?.data?.data
        data ?: return // todo

        viewModel.addToCart(
            AddCartItemRequest(
                deliveryMakingCharge = data.paymentDetails?.deliveryMakingCharge,
                productId = data.paymentDetails?.productId,
                volume = data.paymentDetails?.volume,
                label = data.paymentDetails?.label,
                quantity = data.paymentDetails?.quantity ?: 1,
            )
        )
    }

    private fun contactUsFlow() {
        val number = remoteConfigManager.getWhatsappNumber()
        val message = getString(R.string.hey_need_some_help_buying_a_product)
        requireContext().openWhatsapp(number, message)
    }

    private fun setupRvImages(it: NewTransactionDetails) {
        binding.rvImages.layoutManager =
            CenterZoomLinearLayoutManager(
                requireContext(),
                1f,
                0f,
                RecyclerView.HORIZONTAL
            )

        val imageAdapter = StoreItemImageAdapter()
        binding.rvImages.adapter = imageAdapter
        val spaceItemDecoration = SpaceItemDecoration(8.dp, 8.dp)
        binding.rvImages.addItemDecorationIfNoneAdded(spaceItemDecoration)
        imageAdapter.submitList(
            it.productDetails?.images ?: listOf<String>(
                it.productDetails?.productLink ?: ""
            )
        )

        binding.rvImages.onFlingListener = null

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvImages)
        val circlePagerIndicatorDecoration = DotIndicatorDecoration(requireContext())

        binding.rvImages.addItemDecoration(circlePagerIndicatorDecoration)
    }

    private fun setupCollapisble() {
        binding.cartMyDetailContainer.apply {
            ExpandableHelper.setupCollapsible(
                listOf(
                    trackingDetailsExpandable,
                    deliveryDetailsExpandable,
                    ourPartnersExpandable,
                    paymentDetailsExpandable,
                    sourcePaymentExpandable,
                ),
                listOf(
                    trackingDetailsContainer,
                    deliveryDetailsContainer,
                    ourPartnersContainer,
                    paymentDetailsContainer,
                    sourcePaymentContainer,
                ),
                listOf(
                    trackingDetailsArrow,
                    deliveryDetailsArrow,
                    ourPartnersArrow,
                    paymentDetailsIv,
                    sourcePaymentArrow,
                ),
            )
        }
    }

    private fun setupOrderDetails(data: NewTransactionDetails) {
        // Order section
        binding.cartMyDetailContainer.orderDetailsRv.layoutManager = LinearLayoutManager(context)
        binding.cartMyDetailContainer.orderDetailsRv.adapter = LabelAndValueAdapter {
            (it as? String?)?.let { string ->
                context?.copyToClipboard(data.headers?.orderId ?: string)
                getString(R.string.copied_to_clipboard).snackBar(
                    binding.root,
                    translationY = -4.dp.toFloat()
                )
            }
        }.apply {
            this.submitList(
                listOf(
                    LabelAndValue(
                        getString(R.string.order_id),
                        data.headers?.orderId?.truncateAndAddDot(9) ?: "",
                        showCopyToClipBoardIcon = true,
                        labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                        labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                    ),
                    LabelAndValue(
                        getString(R.string.ordered_on), data.headers?.date ?: "",
                        labelColorRes = com.jar.app.core_ui.R.color.color_ACA1D3,
                        labelTextStyle = com.jar.app.core_ui.R.style.CommonTextViewStyle
                    ),
                )
            )
        }
        binding.cartMyDetailContainer.orderDetailsRv.addItemDecorationIfNoneAdded(
            spaceItemDecoration
        )

        // Tracking Details
        val constructLabelValueListForTracking =
            constructLabelValueListForTracking(data, WeakReference( requireContext()))
        if (constructLabelValueListForTracking(data, WeakReference(requireContext())).isEmpty()) {
            binding.cartMyDetailContainer.trackingDetailsContainer.isVisible = false
        } else {
            binding.cartMyDetailContainer.trackingDetailsRv.layoutManager =
                LinearLayoutManager(context)
            binding.cartMyDetailContainer.trackingDetailsRv.adapter = LabelAndValueAdapter {
                (it as? String?)?.let {
                    context?.copyToClipboard(it)
                    getString(R.string.copied_to_clipboard).snackBar(
                        binding.root,
                        translationY = -4.dp.toFloat()
                    )
                }
            }.apply {
                this.submitList(
                    constructLabelValueListForTracking
                )
            }
            binding.cartMyDetailContainer.trackingDetailsRv.addItemDecorationIfNoneAdded(
                spaceItemDecoration
            )
        }

        // Delivery Details
        if (!TextUtils.isEmpty(data.addressDetails?.name)) {
            binding.cartMyDetailContainer.deliveryNameTv.text = data.addressDetails?.name
            binding.cartMyDetailContainer.deliveryDetailTv.text = data.addressDetails?.address
        } else {
            binding.cartMyDetailContainer.deliveryDetailsContainer.isVisible = false
        }

        // Payment Details
        val list = BreakdownViewRenderer.getBreakdownListFromCart(WeakReference(requireContext()), data)
        com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownViewRenderer.renderBreakdownView(
            binding.cartMyDetailContainer.paymentDetailsContent, list
        )

        // Contact support
        val cancelOrderVisible = isCancelOrderVisible(
            getStatusEnum(data.trackingInfo?.status) ?: GoldDeliveryTrackingStatusEnum.PENDING
        )
        binding.cartMyDetailContainer.helpContainer.cancelOrderContainer.isVisible =
            cancelOrderVisible
        binding.cartMyDetailContainer.helpContainer.cancelOrderSeperator.isVisible =
            cancelOrderVisible
        binding.cartMyDetailContainer.helpContainer.cancelOrderContainer.setDebounceClickListener {
            navigateToCancelOrder()
        }
        binding.cartMyDetailContainer.helpContainer.contactSupportContainer.setDebounceClickListener {
            contactUsFlow()
        }

        // Source of payment
        val paymentList =
            CartMyOrderHelper.constructLabelValueForPaymentSource(data, WeakReference(requireContext()))
        com.jar.app.feature_gold_delivery.impl.ui.store_item.cart.BreakdownViewRenderer.renderBreakdownView(
            binding.cartMyDetailContainer.sourcePaymentDetailsContainer, paymentList
        )
        if (data.invoiceAvailable == false) {
            binding.cartMyDetailContainer.downloadNowLabel.isVisible = false
            binding.cartMyDetailContainer.downloadInvoiceTv.isVisible = false
        }
    }

    private fun navigateToCancelOrder() {
        navigateTo(CartOrderDetailsFragmentDirections.actionCartMyOrderDetailFragmentToCartNoCancelForOrderFragment())
    }

    private fun getData() {
        viewModel.fetchOrderDetails(args.orderId, args.assetSourceType, args.assetTxnId)
    }

}