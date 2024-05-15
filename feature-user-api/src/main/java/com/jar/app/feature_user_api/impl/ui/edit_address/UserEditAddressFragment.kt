package com.jar.app.feature_user_api.impl.ui.edit_address

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_user_api.R
import com.jar.app.feature_user_api.domain.model.Address
import com.jar.app.feature_user_api.impl.ui.base.BaseUserAddressFragment
import com.jar.app.feature_user_api.impl.ui.base.BaseUserAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class UserEditAddressFragment : BaseUserAddressFragment() {

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        title = getString(
                            R.string.edit_address
                        )
                    )
                )
            )
        )
    }

    private val viewModel by viewModels<UserEditAddressViewModel> { defaultViewModelProviderFactory }

    private val baseUserAddressViewModel by activityViewModels<BaseUserAddressViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<UserEditAddressFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        binding.etName.setText(args.address.name.orEmpty())
        if (!args.address.phoneNumber.isNullOrEmpty()) {
            binding.etNumber.setText(getParsedNumber(args.address.phoneNumber.orEmpty()))
        }
        binding.etPinCode.setText(args.address.pinCode)
        binding.etStreetAddressOne.setText(args.address.address1)
        binding.etStreetAddressTwo.setText(args.address.address2)
        binding.etCity.setText(args.address.city)
        binding.etState.setText(args.address.state)

        binding.btnSubmitDetails.setText(getString(R.string.update_address))

        baseAddressFragmentViewModel.validatePinCode(args.address.pinCode)
    }

    private fun observeLiveData() {
        viewModel.editAddressLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                baseUserAddressViewModel.refreshAddressAction.call()
                requireActivity().onBackPressed()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    override fun onValidFormSubmit(address: Address) {
        viewModel.editAddress(args.address.addressId!!, address)
    }

}