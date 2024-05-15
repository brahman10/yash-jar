package com.jar.app.feature_gold_delivery.impl.ui.saved_address

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar

import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentSavedAddressBinding
import com.jar.app.feature_gold_delivery.impl.viewmodels.DeliveryViewModelAndroid
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class SavedAddressFragment : BaseFragment<FragmentSavedAddressBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<SavedAddressFragmentArgs>()

    private var adapter: SavedAddressAdapter? = null

    private val viewModelProvider by viewModels<SavedAddressFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val deliveryViewModelProvider by hiltNavGraphViewModels<DeliveryViewModelAndroid>(R.id.feature_delivery_navigation)

    private val deliveryViewModel by lazy {
        deliveryViewModelProvider.getInstance()
    }

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(8.dp, 8.dp)

    private fun postClickEvent(clickTypeValue: String, eventName: String, eventValue: String) {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonAddressGoldDelivery, mapOf(
                GoldDeliveryConstants.AnalyticsKeys.Click_type to clickTypeValue,
                eventName to eventValue
            )
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSavedAddressBinding
        get() = FragmentSavedAddressBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getString(R.string.select_delivery_address),
                        true,
                        backFactorScale = BACK_FACTOR_SCALE
                    )
                )
            )
        )
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(GoldDeliveryConstants.AnalyticsKeys.ShownAddressGoldDelivery)
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        adapter = SavedAddressAdapter(
            onSavedAddressClick = {
                viewModel.setIndex(it)
                val address =
                    viewModel.savedAddressLiveData.value?.data?.data?.addresses?.getOrNull(it)
                postClickEvent(
                    GoldDeliveryConstants.AnalyticsKeys.saved_address_selected,
                    GoldDeliveryConstants.AnalyticsKeys.pincode,
                    address?.pinCode ?: ""
                )
            },
            onEditClick = {
                val action =
                    SavedAddressFragmentDirections.actionSavedAddressFragmentToEditAddressFragment(
                        it
                    )
                navigateTo(action)
            },
            isRadioChecked = {
                viewModel.currentlySelected.value == it
            }
        )
        binding.rvAddress.adapter = ConcatAdapter(
            adapter,
            NewAddressAdapter { addNewAddress() }
        )
        binding.rvAddress.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvAddress.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun addNewAddress() {
        analyticsHandler.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonAddressGoldDelivery,
            GoldDeliveryConstants.AnalyticsKeys.Click_type,
            GoldDeliveryConstants.AnalyticsKeys.add_new_address_clicked
        )
        val action = SavedAddressFragmentDirections
            .actionSavedAddressFragmentToAddDeliveryAddressFragment(false)
        navigateTo(action)
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.savedAddressLiveData.collectUnwrapped(
                    onSuccess = {
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                        binding.rvAddress.isVisible = true
                        adapter?.submitList(it?.data?.addresses)
                        binding.tvNoAddress.isVisible = it?.data?.addresses.isNullOrEmpty()

                        if (!it?.data?.addresses.isNullOrEmpty()) {
                            it?.data?.addresses?.forEachIndexed { index, address ->
                                if (address.addressId == args.selectedAddressId) {
                                    viewModel.setIndex(index)
                                }
                            }
                        }
                    },
                    onError = { _, _ ->
                        binding.shimmerPlaceholder.stopShimmer()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.currentlySelected.collectLatest {
                    adapter?.notifyDataSetChanged()
                    val isSelected = it != -1
                    binding.contiunueButton.setDisabled(!isSelected)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.deleteAddressLiveData.collectUnwrapped(
                    onSuccess = {
                        getData()
                    },
                    onSuccessWithNullData = {
                        getData()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                deliveryViewModel.refreshAddressAction.collectLatest {
                    viewModel.getSavedAddress(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                deliveryViewModel.selectedAddressLiveData.collectLatest {
                    viewModel.getSavedAddress(it?.addressId)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.contiunueButton.setDebounceClickListener {
            val address = viewModel.savedAddressLiveData.value?.data?.data?.addresses?.getOrNull(
                viewModel.currentlySelected.value ?: -1
            )
            address?.let {
                activity?.onBackPressed()
                deliveryViewModel.selectAddress(address)
            } ?: run {
                getString(R.string.please_select_delivery_address).snackBar(binding.root)
            }
            analyticsHandler.postEvent(
                GoldDeliveryConstants.AnalyticsKeys.Click_ShownSaveAddressPopUp_GoldDelivery,
            )
            postClickEvent(
                GoldDeliveryConstants.AnalyticsKeys.selected_address_continue_clicked,
                GoldDeliveryConstants.AnalyticsKeys.pincode,
                address?.pinCode.orEmpty()
            )
        }
    }

    private fun submitAddress() {
    }

    private fun getData() {
        viewModel.getSavedAddress()
    }
}