package com.jar.app.feature_transaction.impl.ui.retry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentRetryGoldWithdrawalBinding
import com.jar.app.feature_user_api.domain.model.SavedVPA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class RetryGoldWithdrawalFragment : BaseFragment<FeatureTransactionFragmentRetryGoldWithdrawalBinding>() {

    private var vpaAdapter: VpaChipAdapter? = null
    private var savedUPIAddressAdapter: SelectUpiAddressAdapter? = null
    private val chipsSpaceItemDecoration = SpaceItemDecoration(6.dp, 6.dp)
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 6.dp)
    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()
    private var currentlySelectedVPA: SavedVPA? = null

    private val retryViewModel by viewModels<RetryGoldWithdrawalViewModel> { defaultViewModelProviderFactory }

    private val upiAddressViewModel by viewModels<SavedUPIAddressViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<RetryGoldWithdrawalFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentRetryGoldWithdrawalBinding
        get() = FeatureTransactionFragmentRetryGoldWithdrawalBinding::inflate


    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarDefault(title = getString(R.string.feature_transaction_select_wthdraw_method)))))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListeners()
        observerLiveData()
    }

    private fun getData() {
        upiAddressViewModel.fetchUserSavedVPAs()
        upiAddressViewModel.fetchVpaChips()
    }

    private fun setupUI() {
        OverScrollDecoratorHelper.setUpOverScroll(binding.root)

        vpaAdapter = VpaChipAdapter {
            val text = binding.etVPA.text.toString()
            binding.etVPA.setText("${text.replaceAfter("@", it.removePrefix("@"), text + it)}")
            binding.etVPA.setSelection(binding.etVPA.text?.length.orZero())
        }
        binding.rvUpiSuffix.adapter = vpaAdapter
        binding.rvUpiSuffix.addItemDecorationIfNoneAdded(chipsSpaceItemDecoration)

        savedUPIAddressAdapter = SelectUpiAddressAdapter(
            onClick = {
                binding.etVPA.setText("")
                binding.etVPA.clearFocus()
                binding.btnWithdrawNow.requestFocus()
                currentlySelectedVPA = it
                upiAddressViewModel.updateListOnVpaSelection(it.id)
            }
        )
        binding.rvUpiAddress.adapter = savedUPIAddressAdapter
        binding.rvUpiAddress.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvUpiAddress.edgeEffectFactory = baseEdgeEffectFactory
    }

    private fun setupListeners() {
        binding.etVPA.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (currentlySelectedVPA != null) {
                    currentlySelectedVPA = null
                    upiAddressViewModel.updateListOnVpaSelection(null)
                }
            }
        }

        binding.etVPA.textChanges()
            .debounce(100)
            .onEach {
                if (it?.contains("@") == true) {
                    it.split("@").let { list ->
                        upiAddressViewModel.vpaSearch(list.last())
                    }
                } else {
                    upiAddressViewModel.vpaSearch(null)
                }
            }
            .launchIn(uiScope)

        binding.btnWithdrawNow.setDebounceClickListener {
            openConfirmationDialog(
                currentlySelectedVPA?.vpaHandle ?: binding.etVPA.text?.toString()?.trim()
            )
        }
    }

    private fun observerLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        upiAddressViewModel.userVPAsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                if (savedUPIAddressAdapter?.itemCount == 0)
                    binding.shimmerPlaceholder.startShimmer()
            },
            onSuccess = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                binding.tvSavedupis.isVisible = true
                binding.rvUpiAddress.isVisible = true
                savedUPIAddressAdapter?.submitList(it.payoutSavedVpas)
            },
            onError = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                binding.rvUpiAddress.isVisible = false
                binding.tvSavedupis.isVisible = false
                it.snackBar(binding.root)
            }
        )

        upiAddressViewModel.vpaChipsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                dismissProgressBar()
                vpaAdapter?.submitList(it.vpaChips)
            }
        )

        upiAddressViewModel.searchVpaChipsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                vpaAdapter?.submitList(it.vpaChips)
            }
        )

        retryViewModel.withdrawAcceptanceLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                popBackStack()
//                EventBus.getDefault().post(RetryResponseEvent(it.status ?: getString(R.string.request_placed)))
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                popBackStack()
//                EventBus.getDefault().post(RetryResponseEvent(getString(R.string.request_placed)))
            },
            onError = {
                dismissProgressBar()
                popBackStack()
//                EventBus.getDefault().post(RetryResponseEvent(it))
            }
        )
    }

    private fun openConfirmationDialog(vpaId: String?) {
        if (vpaId.isNullOrBlank() || (currentlySelectedVPA == null && !vpaId.isValidUpiAddress()))
            getString(com.jar.app.core_ui.R.string.feature_payment_please_enter_a_valid_upi_id).snackBar(binding.root)
        else
            withdrawIntoBank(vpaId)
    }

    private fun withdrawIntoBank(vpaId: String) {
        retryViewModel.postRetryWithdrawRequest(
            args.orderId,
            vpaId
        )
    }
}