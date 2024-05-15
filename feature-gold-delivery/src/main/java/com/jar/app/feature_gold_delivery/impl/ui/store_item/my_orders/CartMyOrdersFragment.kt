package com.jar.app.feature_gold_delivery.impl.ui.store_item.my_orders

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration

import com.jar.app.core_ui.view_holder.LoadStateAdapter
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartMyOrdersBinding
import com.jar.app.feature_gold_delivery.impl.ui.store_item.list.StoreItemAdapter
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class CartMyOrdersFragment : BaseFragment<FragmentCartMyOrdersBinding>() {

    private val viewModelProvider by viewModels<CartMyOrdersFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartMyOrdersBinding
        get() = FragmentCartMyOrdersBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var job: Job? = null
    private val hasAnimatedOnce = AtomicBoolean(false)

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi
    
    private var adapter: CartMyOrdersAdapter? = null
    
    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.my_orders), true, backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private var spaceItemDecoration = SpaceItemDecoration(16.dp, 8.dp)

    private fun setupUI() {
        adapter = CartMyOrdersAdapter {
            if (
                !TextUtils.isEmpty(it.orderId) ||
                !TextUtils.isEmpty(it.sourceType) ||
                !TextUtils.isEmpty(it.assetTransactionId)
            ) {
                val action =
                    CartMyOrdersFragmentDirections.actionCartMyOrdersFragmentToCartMyOrderDetailFragment(
                        it.orderId!!,
                        it.sourceType!!,
                        it.assetTransactionId!!
                    )
                navigateTo(action)
            }
        }

        binding.myOrdersRv.layoutManager = LinearLayoutManager(context)
        binding.myOrdersRv.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.myOrdersRv.adapter = adapter?.withLoadStateFooter(
            footer = LoadStateAdapter {
                adapter?.retry()
            }
        )

    }

    private fun fetchMyOrdersList() {
        job?.cancel()
//        viewModel.fetchMyOrders()
//        job = uiScope.launch {
//            viewModel.getPagingForMyOrders(WeakReference(requireContext())).collectLatest {
//                adapter?.submitData(it)
//            }
//        }
    }

    private fun setupListeners() {
    }

    private fun setEmptyLayout(listEmpty: Boolean) {
        binding.noCartIV.isVisible = listEmpty
        binding.noCartItemsTV.isVisible = listEmpty
        binding.myOrdersRv.isVisible = !listEmpty
        if (listEmpty) {
            showPopularRv()
        } else {
            binding.seperator.isVisible = false
            binding.header.isVisible = false
            binding.popularRv.isVisible = false
        }
    }

    private fun showPopularRv() {
        binding.seperator.isVisible = true
        binding.header.isVisible = true

        if (viewModel.storeItemsLiveData.value.status != RestClientResult.Status.NONE) {
            binding.popularRv.isVisible = true
        } else {
            viewModel.fetchProducts()
        }
    }

    private fun observeLiveData() {
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.storeItemsLiveData.collectUnwrapped(
            onSuccess = {
                val adapter: StoreItemAdapter = StoreItemAdapter({
                    val action =
                        CartMyOrdersFragmentDirections.actionCartMyOrdersFragmentToStoreItemDetailFragment(
                            it.label ?: "", ""
                        )
                    navigateTo(action)
                })
                binding.popularRv.isVisible = true
                binding.popularRv.adapter = adapter
                binding.popularRv.addItemDecorationIfNoneAdded(SpaceItemDecoration(8.dp, 0.dp))
                binding.popularRv.layoutManager =
                    object :
                        GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false) {
                        override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                            lp?.width = width / 2
                            return super.checkLayoutParams(lp)
                        }
                    }
                adapter?.submitList(it?.data?.products)
            }
        )}}
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.pagingData.collectLatest {
                    it?.let { it1 -> adapter?.submitData(it1) }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            adapter?.loadStateFlow?.collectLatest { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter?.itemCount == 0
                setEmptyLayout(isListEmpty)
                when (loadState.refresh is LoadState.Loading) {
                    true -> {
                        binding.myOrdersRv.isVisible = false
                        binding.shimmerPlaceholder.startShimmer()
                        binding.shimmerPlaceholder.isVisible = true
                    }

                    false -> {
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false

                        if (hasAnimatedOnce.getAndSet(true).not()) {
                            binding.myOrdersRv.runLayoutAnimation(R.anim.feature_gold_delivery_layout_animation_fall_down)
                        }
                    }
                }
            }
        }
    }

    private fun getData() {
        fetchMyOrdersList()
    }


}
