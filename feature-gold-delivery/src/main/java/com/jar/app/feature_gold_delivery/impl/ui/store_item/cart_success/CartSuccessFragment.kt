package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_success

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.core_base.domain.model.card_library.DynamicCard
import com.jar.app.core_base.domain.model.card_library.LibraryCardData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.DynamicCardUtil
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.dynamic_cards.DynamicEpoxyController
import com.jar.app.core_ui.dynamic_cards.base.EpoxyBaseEdgeEffectFactory
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartSuccessBinding
import com.jar.app.feature_gold_delivery.impl.helper.CartSuccessHelper.curateLabelAndValueForDetailsList
import com.jar.app.feature_gold_delivery.impl.helper.CartSuccessHelper.inflateItemView
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.app.feature_one_time_payments_common.shared.OrderDetails
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CartSuccessFragment : BaseFragment<FragmentCartSuccessBinding>() {

    private var controller: DynamicEpoxyController? = null
    private val args by navArgs<CartSuccessFragmentArgs>()
    private val labelAndValueAdapter = LabelAndValueAdapter {
        (it as? String?)?.let {
            getString(R.string.copied_to_clipboard).snackBar(binding.root)
        }
    }

    companion object {
        const val View_details = "View details"
        const val Feedback_response = "Feedback response"
        const val Spin_the_wheel = "Spin the wheel"
        const val Goto_homepage = "Goto homepage"
    }

    private val viewModelProvider by viewModels<CartSuccessFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartSuccessBinding
        get() = FragmentCartSuccessBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

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

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(eventName = GoldDeliveryConstants.AnalyticsKeys.ShownOrderSuccessGoldDelivery)
        setupUI()
        setupListeners()
        observeLiveData()
        setupDynamicCards()
        getData()
    }

    @SuppressLint("Range")
    private fun setupDynamicCards() {
        controller = DynamicEpoxyController(
            uiScope = uiScope,
            onPrimaryCtaClick = { primaryActionData, eventData ->
                popBackStack()
                EventBus.getDefault().post(HandleDeepLinkEvent(primaryActionData.value))
                analyticsHandler.postEvent(
                    Spin_the_wheel,
                    eventData.map
                )
            },
            onEndIconClick = { staticInfoData, eventData ->
                popBackStack()
                EventBus.getDefault().post(HandleDeepLinkEvent(staticInfoData.value))
                analyticsHandler.postEvent(
                    Spin_the_wheel,
                    eventData.map
                )
            }
        )
        val edgeEffectFactory = EpoxyBaseEdgeEffectFactory()
        val spaceItemDecoration = SpaceItemDecoration(0.dp, 10.dp, escapeEdges = false)
        val layoutManager = LinearLayoutManager(context)
        binding.dynamicRecyclerView2.layoutManager = layoutManager
        binding.dynamicRecyclerView2.setItemSpacingPx(0)
        binding.dynamicRecyclerView2.addItemDecorationIfNoneAdded(
            spaceItemDecoration
        )
        binding.dynamicRecyclerView2.edgeEffectFactory = edgeEffectFactory
        val visibilityTracker = EpoxyVisibilityTracker()
        visibilityTracker.partialImpressionThresholdPercentage = 50
        visibilityTracker.attach(binding.dynamicRecyclerView2)
        binding.dynamicRecyclerView2.setControllerAndBuildModels(controller!!)

    }

    private fun setupUI() {
        setupLottie()
        setupFeedbackClick()
        setupItems(args.deliveryProductResponse.orderDetails)
        setupRv()
    }

    private fun setupLottie() {
        uiScope.launch {
            binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.LottieUrls.CONFETTI_FROM_TOP
            )
            delay(300)
        }
    }

    private fun setupRv() {
        val labelAndValueAdapter = LabelAndValueAdapter {
            (args.orderId).let {
                context?.copyToClipboard(it)
                getString(R.string.copied_to_clipboard).snackBar(binding.root)
            }
        }
        val spaceItemDecoration = SpaceItemDecoration(0.dp, 9.dp)
        binding.rvDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvDetails.adapter = labelAndValueAdapter
        val list = curateLabelAndValueForDetailsList(
            args.orderId,
            args.paymentDate ?: args.deliveryProductResponse.created_at,
            args.paymentMethod,
            args.payerVpa,
            WeakReference( requireContext())
        )
        labelAndValueAdapter.submitList(list)
    }

    private fun setupItems(orderDetails: List<OrderDetails?>?) {
        orderDetails?.takeIf { it.isNotEmpty() }?.let {
            it.forEach {
                it?.let {
                    val root = inflateItemView(it, WeakReference(binding.itemsContainer)).root
                    root.setDebounceClickListener { view ->
                        navigateTo(
                            CartSuccessFragmentDirections.actionCartSuccessFragmentToCartMyOrderDetailFragment(
                                it.orderId ?: args.orderId,
                                it.assetSourceType ?: "ASSET_DELIVERY",
                                it.assetTransactionId ?: ""
                            ),
                            popUpTo = R.id.deliveryStoreItemListFragment,
                            inclusive = false
                        )
                    }
                    binding.itemsContainer.addView(root)
                    analyticsHandler.postEvent(
                        GoldDeliveryConstants.AnalyticsKeys.ClickButtonOrderSuccessGoldDelivery,
                        View_details,
                        it.label ?: it.orderId.orEmpty()
                    )
                }
            }
        }
    }

    private fun setupFeedbackClick() {
        val listOf =
            listOf<TextView>(binding.sad1, binding.sad2, binding.sad3, binding.sad4, binding.sad5)
        val listOf2 =
            listOf<TextView>(binding.sad1Label, binding.sad2Label, binding.sad3Label, binding.sad4Label, binding.sad5Label)
        listOf.forEachIndexed { index, textView ->
            textView.setDebounceClickListener {
                listOf.forEachIndexed { loopIndex, textView ->
                    if (loopIndex == index) {
                        textView.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bg_circle_2e2942_stroke_7745ff
                        )
                        TextViewCompat.setTextAppearance(
                            listOf2[loopIndex],
                            com.jar.app.core_ui.R.style.CommonBoldTextViewStyle
                        )
                        listOf2[loopIndex].setTextColor(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.white))
                    } else {
                        textView.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.bg_circle_2e2942)
                        TextViewCompat.setTextAppearance(
                            listOf2[loopIndex],
                            com.jar.app.core_ui.R.style.CommonTextViewStyle
                        )
                        listOf2[loopIndex].setTextColor(ContextCompat.getColor(requireContext(), com.jar.app.core_ui.R.color.color_ACA1D3))
                    }
                    listOf2[loopIndex].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnGoToHomePage.setDebounceClickListener {
            navigateTo(BaseConstants.InternalDeepLinks.HOME, shouldAnimate = false)
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonOrderSuccessGoldDelivery,
                Goto_homepage,
                "HOMEPAGE"
            )
        }
        binding.submitBtn.setDebounceClickListener {
            val listOf = listOf<TextView>(
                binding.sad1,
                binding.sad2,
                binding.sad3,
                binding.sad4,
                binding.sad5
            )
            var labelFeedback: String? = null
            val indexOfFirst = listOf.indexOfFirst {
                val isTrue = it.background.constantState == ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_circle_2e2942_stroke_7745ff
                )?.constantState
                if (isTrue) labelFeedback = it.text.toString()
                isTrue
            }
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonOrderSuccessGoldDelivery,
                Feedback_response,
                labelFeedback.orEmpty()
            )
            if (indexOfFirst == -1) {
                getString(R.string.something_went_wrong).snackBar(binding.root)
            } else {
                viewModel.submitFeedback(
                    args.orderId,
                    indexOfFirst + 1
                )
            }
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.submitFeedbackFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        showSubmitCelebration()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        showSubmitCelebration()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dynamicCardsFlow.collectUnwrapped(
                    onLoading = {
                    },
                    onSuccess = {
                        controller?.cards = createDynamicCards(it)
                        binding.dynamicRecyclerView2.invalidateItemDecorations()
                        binding.dynamicRecyclerView2.isVisible = true
                    },
                    onSuccessWithNullData = {
                    },
                    onError = { _, _ ->
                    }
                )
            }
        }
    }
    private fun createDynamicCards(result: ApiResponseWrapper<Unit?>): MutableList<DynamicCard> {
        val list = mutableListOf<DynamicCard>()
        try {
            val views: List<LibraryCardData?>? = result.getViewData()
            if (views.isNullOrEmpty()) {
                return mutableListOf<DynamicCard>()
            }
            for (view: LibraryCardData? in views) {
                view?.let {
                    if (it.showCard) list.add(it)
                }
            }
            DynamicCardUtil.rearrangeDynamicCards(list)
            return list
        } catch (e: Exception) {
            Timber.e(e)
        }
        return mutableListOf<DynamicCard>()
    }

    private fun showSubmitCelebration() {
        binding.celebrationFeedbackGroup.isVisible = true
        binding.smileyContainer.isVisible = false
        binding.tvBankAccount3.isVisible = false
        binding.submitBtn.isVisible = false
        binding.lottieFeedbackCelebration.playAnimation()
    }

    private fun getData() {
        viewModel.fetchOrderStatusDynamicCards(args.orderId)
    }
}
