package com.jar.app.feature_gold_delivery.impl.ui.edit_address

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.impl.ui.base.BaseAddressFragment
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import androidx.lifecycle.Lifecycle

@AndroidEntryPoint
internal class EditAddressFragment : BaseAddressFragment() {

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarDefault(getString(R.string.edit_address)))))
    }


    private val viewModelProvider by viewModels<EditAddressFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<EditAddressFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        binding.etName.setText(args.address.name)
        binding.etNumber.setText(getParsedNumber(args.address.phoneNumber))
        binding.etPinCode.setText(args.address.pinCode)
        binding.etStreetAddressOne.setText(args.address.address1)
        binding.etStreetAddressTwo.setText(args.address.address2)
        binding.etCity.setText(args.address.city)
        binding.etState.setText(args.address.state)

        binding.btnSubmitDetails.setText(getString(R.string.update_address))

        baseAddressFragmentViewModel.validatePinCode(args.address.pinCode)
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.editAddressLiveData.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()

                        deliveryViewModel.updateRefreshAdress(it?.data?.addressId.orEmpty())
                        requireActivity().onBackPressed()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    override fun onValidFormSubmit(address: Address) {
        viewModel.editAddress(args.address.addressId!!, address)
    }
}