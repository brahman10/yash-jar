package com.jar.app.feature_user_api.impl.ui.add_address

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_user_api.R
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.impl.ui.base.BaseUserAddressFragment
import com.jar.app.feature_user_api.impl.ui.base.BaseUserAddressViewModel
import com.jar.app.feature_user_api.util.UserApiConstants
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class UserAddAddressFragment : BaseUserAddressFragment() {

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        title = getString(R.string.add_delivery_details), showSeparator = true
                    )
                )
            )
        )
    }

    private val args by navArgs<UserAddAddressFragmentArgs>()

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    private val viewModel by viewModels<UserAddAddressViewModel> { defaultViewModelProviderFactory }

    private val baseUserAddressViewModel by activityViewModels<BaseUserAddressViewModel> { defaultViewModelProviderFactory }

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        setupUI()
        observeLiveData()
        analyticsHandler.postEvent(
            UserApiConstants.AnalyticsKeys.ShowNewAddressScreen_GoldDelivery, mapOf(
                UserApiConstants.AnalyticsKeys.GoldInAccount to baseUserAddressViewModel.userGoldBalance.toString(),
                EventKey.FromScreen to args.fromScreen
            )
        )
        analyticsHandler.postEvent(
            UserApiConstants.AnalyticsKeys.ShownFillDetails_GoldDeliveryScreen,
            baseUserAddressViewModel.userGoldBalance.toString()
        )
        setupEvents()
    }

    private fun setupUI() {
        if (args.isFirstAddress) {
            binding.btnSubmitDetails.setText(getString(R.string.save_and_proceed))
        } else {
            binding.btnSubmitDetails.setText(getString(R.string.save_new_address))
        }
    }

    private fun observeLiveData() {
        viewModel.addressLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                baseUserAddressViewModel.selectAddress(it)
                requireActivity().onBackPressed()
            },
            onError = {
                dismissProgressBar()
            })

        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.etName.setText(it.firstName.plus(" ").plus(it.lastName))
                binding.etNumber.setText(getParsedNumber(it.phoneNumber))
            }
        }
    }

    private fun setupEvents() {
        analyticsHandler.postEvent(
            UserApiConstants.AnalyticsKeys.ShowNewAddressScreen_GoldDelivery, mapOf(
                UserApiConstants.AnalyticsKeys.GoldInAccount to baseUserAddressViewModel.userGoldBalance.toString(),
                EventKey.FromScreen to args.fromScreen
            )
        )
        analyticsHandler.postEvent(
            UserApiConstants.AnalyticsKeys.ShownFillDetails_GoldDeliveryScreen,
            baseUserAddressViewModel.userGoldBalance.toString()
        )
    }

    override fun onValidFormSubmit(address: Address) {
        viewModel.addAddress(address)
    }

}