package com.jar.app.feature_gold_delivery.impl.ui.cart_items

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.BaseEdgeEffectFactory

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener

import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartItemsBinding
import com.jar.app.feature_gold_delivery.impl.helper.Utils.calculateQuantityItemsString
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.shared.util.CartDataHelper.calculateTotalAmountFromCart
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CartItemsFragment : BaseBottomSheetDialogFragment<FragmentCartItemsBinding>() {

    private val args by navArgs<CartItemsFragmentArgs>()

    private var adapter: CartItemsAdapter? = null

    private val viewModelProvider by hiltNavGraphViewModels<CartItemsFragmentViewModelAndroid>(R.id.feature_delivery_navigation)

    private val cartViewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(8.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartItemsBinding
        get() = FragmentCartItemsBinding::inflate

    override val bottomSheetConfig = DEFAULT_CONFIG

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.cartItemsRV.layoutManager = LinearLayoutManager(requireContext())
        adapter = CartItemsAdapter(
            onDeleteClick = {
                it.id?.let { it1 -> cartViewModel.deleteCartItem(it1) }
            },
        )
        binding.cartItemsRV.adapter = adapter
        binding.cartItemsRV.edgeEffectFactory = baseEdgeEffectFactory
        binding.cartItemsRV.addItemDecorationIfNoneAdded(spaceItemDecoration)

        binding.tvCartPrice.text = getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            calculateTotalAmountFromCart(args.cartData.cartItemData).getFormattedAmount()
        )
        binding.tvCartQuantity.text =
            calculateQuantityItemsString(WeakReference(requireContext()), args.cartData)

        binding.tvCartPrice.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            if (binding.tvCartPrice.length() > 6) 20f else 22f
        )
        adapter?.submitList(args.cartData.cartItemData)
    }

    private fun observeLiveData() {
        val rootView = binding.root
        val weakReference: WeakReference<View> = WeakReference(rootView)


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                cartViewModel.deleteAddressLiveData.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        cartViewModel.getCartItems()
                        deliveryViewModel.updateCart(Unit)
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        cartViewModel.getCartItems()
                        deliveryViewModel.updateCart(Unit)
                    },
                    onError = { _, _ ->
                        dismissProgressBar()

                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                cartViewModel.fetchCartLiveData.collectUnwrapped(
                    onSuccess = {
                        dismissProgressBar()
                        if (it?.data?.cartItemData.isNullOrEmpty()) {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                GoldDeliveryConstants.DELETE_ALL_ITEMS_FROM_BOTTOMSHEET,
                                true
                            )
                            dismiss()
                        } else {
                            adapter?.submitList(it?.data?.cartItemData)
                        }
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setupListeners() {
        binding.btnProceedCart.setDebounceClickListener {
            this.dismiss()
            navigateTo(
                CartItemsFragmentDirections.actionCartItemsFragmentToDeliveryStoreCartFragment(""),
                popUpTo = R.id.deliveryStoreCartFragment,
                inclusive = true
            )
        }
    }

    private fun getData() {
        cartViewModel.getCartItems()
    }
}