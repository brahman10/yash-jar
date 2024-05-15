package com.jar.app.feature_gold_delivery.impl.ui.store_item.wishlist

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration

import com.jar.app.core_ui.view_holder.LoadStateAdapter
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartWishlistBinding
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.StoreItemAdapter
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.app.feature_gold_delivery.shared.data.WishlistData
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData
import com.jar.app.feature_gold_delivery.shared.domain.model.WishlistAPIData
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class CartWishListFragment : BaseFragment<FragmentCartWishlistBinding>() {

    private val viewModelProvider by viewModels<CartWishListViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartWishlistBinding
        get() = FragmentCartWishlistBinding::inflate
    private var job: Job? = null
    private val hasAnimatedOnce = AtomicBoolean(false)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi
    
    var adapter: WishlistAdapter? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.wishlist), true, backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    companion object {
        const val Add_to_cart_from_wishlist = "Add to cart from wishlist"
        const val Explore_More = "Explore More"
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(GoldDeliveryConstants.AnalyticsKeys.ShownWishlistScreenGoldDelivery)
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private var spaceItemDecoration = SpaceItemDecoration(16.dp, 0.dp)

    private fun WishlistAPIData.generateCartItemData(): CartItemData {
        return CartItemData(
            amount = this.amount?.toDouble(),
            quantity = 1,
            label = this.label,
            productId = this.productId.toString(),
            volume = this.volume?.toDouble(),
            inStock = this.inStock,
            deliveryMakingCharge = null,
            id = this.id,
            icon = this.icon,
            discountOnTotal = this.discountOnTotal?.toFloat(),
            totalAmount = null
        )
    }

    private fun setupUI() {
        adapter = WishlistAdapter({
            val actionDeliveryStoreCartFragmentToCartItemsQuantityEditFragment =
                CartWishListFragmentDirections.actionWishlistFragmentToCartItemsQuantityEditFragment(
                    it.generateCartItemData(), false
                )
            navigateTo(actionDeliveryStoreCartFragmentToCartItemsQuantityEditFragment)
        }, {
            it.id?.let { it1 -> viewModel.deleteCartItem(it1) }
        }) {
            binding.btnConfirm.isVisible =
                adapter?.snapshot()?.items?.toList()?.any { (it is WishlistData.WishlistBody && it.isChecked) }
                    ?: false
        }

        binding.wishlistRv.layoutManager = LinearLayoutManager(context)
        binding.wishlistRv.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.wishlistRv.adapter = adapter?.withLoadStateFooter(
            footer = LoadStateAdapter {
                adapter?.retry()
            }
        )
        viewLifecycleOwner.lifecycleScope.launch {
            adapter?.loadStateFlow?.collectLatest { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && checkIfAdapterIsEmpty(adapter)
                when (loadState.refresh is LoadState.Loading) {
                    true -> {
                        binding.wishlistRv.isVisible = false
                        binding.shimmerPlaceholder.startShimmer()
                        binding.shimmerPlaceholder.isVisible = true
                    }

                    false -> {
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false

                        if (hasAnimatedOnce.getAndSet(true).not()) {
                            binding.wishlistRv.runLayoutAnimation(R.anim.feature_gold_delivery_layout_animation_fall_down)
                        }
                    }
                }
                setEmptyLayout(isListEmpty)
            }
        }


        val animation = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f)
        animation.duration = 1000

        val animation2 = ObjectAnimator.ofFloat(view, "translationY", 0f, 100f)
        animation2.duration = 1000

        val itemLayoutTransition = LayoutTransition()
        itemLayoutTransition.setAnimator(LayoutTransition.APPEARING, animation)
        itemLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, animation2)

        binding.constraintLayout.layoutTransition = itemLayoutTransition
    }

    private fun checkIfAdapterIsEmpty(adapter: WishlistAdapter?): Boolean {
        return (adapter == null || !adapter.snapshot().toList().any { it is WishlistData.WishlistBody })
    }

    private fun setEmptyLayout(listEmpty: Boolean) {
        binding.noCartIV.isVisible = listEmpty
        binding.noCartItemsTV.isVisible = listEmpty
        binding.wishlistRv.isVisible = !listEmpty
        binding.popularRv.isVisible = listEmpty
        if (listEmpty) {
            binding.btnConfirm.isVisible = false
            binding.btnExploreMore.isVisible = true
            showSimilarRV()
        }
    }

    private fun showSimilarRV() {
        binding.seperator.isVisible = true
        binding.header.isVisible = true
        if (viewModel.storeItemsLiveData.value.status != RestClientResult.Status.NONE) {
            binding.popularRv.isVisible = true
        } else {
            viewModel.fetchProducts()
        }

    }

    private fun fetchMyOrdersList() {
        job?.cancel()
        viewModel.resetSeperators()
    }

    private fun setupListeners() {
        binding.btnConfirm.setDebounceClickListener {
            val snapshot =
                adapter?.snapshot()?.toList()
                    ?.mapNotNull { if (it is WishlistData.WishlistBody && it.isChecked) it else null }
            if (snapshot.isNullOrEmpty()) {
                getString(R.string.please_select_an_item).snackBar(binding.root)
            } else {
                val joinToString = snapshot.joinToString { " ${it.body.productId.orZero()}" }
                analyticsHandler.postEvent(
                    GoldDeliveryConstants.AnalyticsKeys.ClickButtonWishlistScreenGoldDelivery,
                    mapOf<String, String>(
                        Add_to_cart_from_wishlist to (joinToString ?: "Add_to_cart_from_wishlist")
                    )
                )
                viewModel.addItemsToCart(snapshot)
            }
        }
        binding.btnExploreMore.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.ClickButtonWishlistScreenGoldDelivery,
                Explore_More,
                "From_Wishlist"
            )
            navigateTo(CartWishListFragmentDirections.actionWishlistFragmentToDeliveryStoreItemListFragment())
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                deliveryViewModel.cartUpdate.collectLatest {
                    fetchMyOrdersList()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.storeItemsLiveData.collectUnwrapped(
                    onSuccess = {
                        val adapter: StoreItemAdapter = StoreItemAdapter({
                            val action =
                                CartWishListFragmentDirections.actionWishlistFragmentToStoreItemDetailFragment(
                                    it.label ?: "", ""
                                )
                            navigateTo(action)
                        })
                        binding.popularRv.isVisible = true
                        binding.popularRv.adapter = adapter
                        binding.popularRv.addItemDecorationIfNoneAdded(
                            SpaceItemDecoration(
                                8.dp,
                                0.dp
                            )
                        )
                        binding.popularRv.layoutManager =
                            object :
                                GridLayoutManager(
                                    requireContext(),
                                    1,
                                    RecyclerView.HORIZONTAL,
                                    false
                                ) {
                                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                                    lp?.width = width / 2
                                    return super.checkLayoutParams(lp)
                                }
                            }
                        adapter.submitList(it.data?.products)
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.cartItemsAdded.collectLatest {
                    if (it) {
                        navigateTo(
                            CartWishListFragmentDirections.actionWishlistFragmentToDeliveryStoreCartFragment(
                                ""
                            ),
                            popUpTo = R.id.deliveryStoreItemListFragment,
                            inclusive = false
                        )
                    } else {
                        getString(R.string.something_went_wrong_in_adding_items_to_cart).snackBar(
                            binding.root
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.deleteAddressLiveData.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.resetSeperators()
                        adapter?.refresh()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.pagingData.collectLatest {
                    it?.let { it1 -> adapter?.submitData(it1) }
                }
            }
        }
    }


    private fun getData() {
        fetchMyOrdersList()
    }
}
