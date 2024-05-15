package com.jar.app.feature_user_api.impl.ui.saved_address

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_user_api.R
import com.jar.app.feature_user_api.databinding.FragmentUserSavedAddressBinding
import com.jar.app.feature_user_api.impl.ui.base.BaseUserAddressViewModel
import com.jar.app.feature_user_api.util.UserApiConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class UserSavedAddressFragment : BaseFragment<FragmentUserSavedAddressBinding>() {

    private val args by navArgs<UserSavedAddressFragmentArgs>()

    private var adapter: UserSavedAddressAdapter? = null

    private val viewModel by viewModels<UserSavedAddressViewModel> { defaultViewModelProviderFactory }

    private val baseUserAddressViewModel by activityViewModels<BaseUserAddressViewModel> { defaultViewModelProviderFactory }

    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(8.dp, 8.dp)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUserSavedAddressBinding
        get() = FragmentUserSavedAddressBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        title = getString(
                            R.string.saved_addresses
                        )
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
        analyticsHandler.postEvent(UserApiConstants.AnalyticsKeys.Shown_SavedAddressesScreen)
    }

    private fun setupUI() {
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserSavedAddressAdapter(
            onSavedAddressClick = {
                if (args.fromScreen != BaseConstants.PROFILE) {
                    baseUserAddressViewModel.selectAddress(it)
                    analyticsHandler.postEvent(
                        UserApiConstants.AnalyticsKeys.Click_ShownSaveAddressPopUp_GoldDelivery,
                        mapOf(
                            UserApiConstants.AnalyticsKeys.GoldInAccount to baseUserAddressViewModel.userGoldBalance.toString(),
                            EventKey.FromScreen to args.fromScreen
                        )
                    )
                    popBackStack()
                }
            },
            onDeleteClick = {
                viewModel.deleteAddress(it.addressId!!)
            },
            onEditClick = {
                val action =
                    UserSavedAddressFragmentDirections.actionSavedAddressFragmentToEditAddressFragment(
                        it
                    )
                navigateTo(action)
            }
        )
        binding.rvAddress.adapter = adapter
        binding.rvAddress.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvAddress.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.savedAddressLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                binding.rvAddress.isVisible = true
                adapter?.submitList(it.addresses)
                binding.tvNoAddress.isVisible = it.addresses.isNullOrEmpty()
            },
            onError = {
                binding.shimmerPlaceholder.stopShimmer()
            }
        )

        viewModel.deleteAddressLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccessWithNullData = {
                getData()
            },
            onSuccess = {
                getData()
            }
        )

        baseUserAddressViewModel.refreshAddressAction.observe(viewLifecycleOwner) {
            getData()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.btnAddNewAddress.setDebounceClickListener {
            val action = UserSavedAddressFragmentDirections
                .actionSavedAddressFragmentToAddDeliveryAddressFragment(false)
            navigateTo(action)
            analyticsHandler.postEvent(
                UserApiConstants.AnalyticsKeys.Click_AddNewAddressScreen,
                mapOf(
                    UserApiConstants.AnalyticsKeys.GoldInAccount to baseUserAddressViewModel.userGoldBalance.toString(),
                    EventKey.FromScreen to args.fromScreen
                )
            )
        }
    }

    private fun getData() {
        viewModel.getSavedAddress()
    }
}