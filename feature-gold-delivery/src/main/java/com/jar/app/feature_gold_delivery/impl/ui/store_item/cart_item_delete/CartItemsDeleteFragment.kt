package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart_item_delete

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener

import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentCartItemsDeleteBinding
import com.jar.app.feature_gold_delivery.shared.domain.model.CartItemData
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CartItemsDeleteFragment : BaseBottomSheetDialogFragment<FragmentCartItemsDeleteBinding>() {

    private val args by navArgs<CartItemsDeleteFragmentArgs>()

    private val viewModelProvider by viewModels<CartItemsDeleteViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCartItemsDeleteBinding
        get() = FragmentCartItemsDeleteBinding::inflate

    override val bottomSheetConfig = DEFAULT_CONFIG

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.cartCartLayout.cartQuantity.isVisible = false
        binding.cartCartLayout.deleteIv.isVisible = false
        binding.cartCartLayout.editIv.isVisible = false
        binding.cartCartLayout.tvQuantity.text = args.cartItemData.volume.toString() + " gm"
        binding.cartCartLayout.tvCartName.text = args.cartItemData.label
        updatePricing(args.cartItemData)
    }


    private fun updatePricing(availableVolume: CartItemData) {
        binding.cartCartLayout.tvPrice.text = getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            availableVolume.amount?.getFormattedAmount()
        )
        binding.cartCartLayout.tvDiscountPrice.isVisible = false
    }

    private fun setupListeners() {
        val availableVolumeV2 = args.cartItemData
        binding.btnClose.setDebounceClickListener {
            dismiss()
        }
        binding.btnAction.setDebounceClickListener {
            viewModel.deleteCartItem(
                args.cartItemData.id ?: ""
            )
        }
        binding.btnAction2.setDebounceClickListener {
            dismiss()
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.deleteAddressLiveData.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        deliveryViewModel.updateCart(Unit)
                        activity?.onBackPressed()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                        deliveryViewModel.updateCart(Unit)
                        activity?.onBackPressed()
                        dismiss()
                    }
                )

            }
        }
    }

    private fun getData() {
    }

}
