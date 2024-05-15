package com.jar.app.feature_gold_delivery.impl.ui.add_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.FragmentAddAddressBinding
import com.jar.app.feature_gold_delivery.impl.ui.base.BaseAddressFragment
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants
import com.jar.app.feature_gold_delivery.impl.util.GoldDeliveryConstants.BACK_FACTOR_SCALE
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class AddDeliveryAddressFragment : BaseAddressFragment() {

    private val viewModelProvider by viewModels<AddDeliveryAddressFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    @Inject
    lateinit var analyticsApi: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddAddressBinding
        get() = FragmentAddAddressBinding::inflate

    private val args by navArgs<AddDeliveryAddressFragmentArgs>()

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(getString(R.string.add_delivery_details), showSeparator = true, backFactorScale = BACK_FACTOR_SCALE)
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        super.setup(savedInstanceState)
        setupUI()
        observeLiveData()
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewModel.addressLiveData.collectUnwrapped(
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                it?.let { it1 -> deliveryViewModel.selectAddress(it1?.data) }
                requireActivity().onBackPressed()
            },
            onError = { _, _ ->
                dismissProgressBar()
            }
        )
            }
        }

        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.etName.setText(it.firstName.plus(" ").plus(it.lastName))
                binding.etNumber.setText(getParsedNumber(it.phoneNumber))
            }
        }
    }

    private fun setupEvents() {
    }

    override fun onValidFormSubmit(address: Address) {
        analyticsApi.postEvent(
            GoldDeliveryConstants.AnalyticsKeys.ClickButtonAddressGoldDelivery,
            GoldDeliveryConstants.AnalyticsKeys.Click_type,
            GoldDeliveryConstants.AnalyticsKeys.save_new_address_clicked)
        viewModel.addAddress(address)
    }
}