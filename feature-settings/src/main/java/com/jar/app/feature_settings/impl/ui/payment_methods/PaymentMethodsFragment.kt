package com.jar.app.feature_settings.impl.ui.payment_methods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_settings.databinding.FragmentPaymentMethodsBinding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.app.feature_settings.domain.event.PaymentMethodsAlteredEvent
import com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.SavedCardsAdapterDelegate
import com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.SavedUpiIdsAdapterDelegate
import com.jar.app.feature_settings.impl.ui.payment_methods.adapter_delegate.adapters.PaymentMethodAdapter
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_settings.util.SettingsConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class PaymentMethodsFragment : BaseFragment<FragmentPaymentMethodsBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPaymentMethodsBinding
        get() = FragmentPaymentMethodsBinding::inflate

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp, escapeEdges = true)

    private var savedCardsAdapterDelegate = SavedCardsAdapterDelegate(
        onDeleteClick = { savedCard, _ ->
            analyticsHandler.postEvent(SettingsEventKey.Clicked_DeleteCard_PaymentMethodsScreen)
            navigateTo(
                PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToDeletePaymentMethodDialogFragment(
                    source = SettingsConstants.PaymentMethodsPosition.CARDS,
                    paymentMethodId = savedCard.cardToken.orEmpty()
                )
            )
        }
    )

    private var savedUpiIdsAdapterDelegate = SavedUpiIdsAdapterDelegate(
        onDeleteClick = { savedVPA, position ->
            analyticsHandler.postEvent(
                SettingsEventKey.Clicked_DeleteUPI_PaymentMethodsScreen, mapOf(
                    SettingsEventKey.id to savedVPA.vpaHandle
                )
            )
            navigateTo(
                PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToDeletePaymentMethodDialogFragment(
                    source = SettingsConstants.PaymentMethodsPosition.UPI,
                    paymentMethodId = savedVPA.id,
                    isAutopay = savedVPA.autopay.orFalse()
                )
            )
        },
        onAddCLick = {
            analyticsHandler.postEvent(SettingsEventKey.Clicked_AddUPI_PaymentMethodsScreen)
            navigateTo(PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToAddUpiFragment())
        }
    )

    private var adapter: PaymentMethodAdapter? = null

    private val viewModelProvider by viewModels<PaymentMethodsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val adapterDelegates = listOf(
        savedCardsAdapterDelegate, savedUpiIdsAdapterDelegate
    )

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(
                UpdateAppBarEvent(
                    AppBarData(
                        ToolbarDefault(
                            getCustomStringFormatted(SettingsMR.strings.feature_settings_payment_methods),
                            showSeparator = true
                        )
                    )
                )
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getData()
        EventBus.getDefault().register(this)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        binding.rvPaymentMethods.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPaymentMethods.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvPaymentMethods.edgeEffectFactory = baseEdgeEffectFactory
        adapter = PaymentMethodAdapter(adapterDelegates)
        binding.rvPaymentMethods.adapter = adapter
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.errorMessageFlow.collectLatest {
                    it.snackBar(binding.root)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.paymentMethodLiveData.collectLatest {
                    if (it.isNotEmpty()) {
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                        binding.rvPaymentMethods.isVisible = true
                        adapter?.items = it
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun fetchPaymentMethods(paymentMethodsAlteredEvent: PaymentMethodsAlteredEvent) {
        when (paymentMethodsAlteredEvent.position) {
            SettingsConstants.PaymentMethodsPosition.CARDS -> {
                viewModel.fetchUserSavedCards()
            }

            SettingsConstants.PaymentMethodsPosition.UPI -> {
                viewModel.fetchUserSavedVPAs()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}