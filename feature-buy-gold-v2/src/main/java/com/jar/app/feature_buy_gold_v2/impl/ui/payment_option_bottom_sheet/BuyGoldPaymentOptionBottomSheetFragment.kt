package com.jar.app.feature_buy_gold_v2.impl.ui.payment_option_bottom_sheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getAppNameFromPkgName
import com.jar.app.base.util.isPackageInstalled
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import com.jar.app.core_ui.item_decoration.HeaderItemDecoration
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentBuyGoldPaymentOptionBottomSheetBinding
import com.jar.app.feature_buy_gold_v2.impl.domain.model.getHeaderTextResourceId
import com.jar.app.feature_buy_gold_v2.shared.domain.model.InitiateBuyGoldData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentOptionsData
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentSectionHeaderType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldPaymentType
import com.jar.app.feature_buy_gold_v2.shared.domain.model.payment_option.BuyGoldUpiApp
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2Constants
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey
import com.jar.app.feature_one_time_payments.shared.domain.model.payment_method.OneTimePaymentMethodType
import com.jar.app.feature_payment.api.PaymentManager
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class BuyGoldPaymentOptionBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentBuyGoldPaymentOptionBottomSheetBinding>(){

    @Inject
    lateinit var paymentManager: PaymentManager

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<BuyGoldPaymentOptionBottomSheetFragmentArgs>()

    private val buyGoldPaymentOptionsData by lazy {
        try {
            args.buyGoldPaymentOptionsDataEncoded.takeIf { it.isEmpty().not() }?.let {
                return@lazy serializer.decodeFromString<BuyGoldPaymentOptionsData>(it)
            } ?: kotlin.run {
                return@lazy null
            }
        } catch (e: Exception) {
            return@lazy null
        }
    }

    private val viewModelProvider by viewModels<BuyGoldPaymentOptionsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.newInstance()
    }

    private var isFirstLaunch = true

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBuyGoldPaymentOptionBottomSheetBinding
        get() = FragmentBuyGoldPaymentOptionBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false, isDraggable = false, isHideable = false)

    private var paymentSectionAdapter: BuyGoldPaymentOptionAdapter? = null
    private var spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)
    private val headerItemDecoration =
        HeaderItemDecoration(object :
            BaseItemDecoration.SectionCallback {
            override fun isItemDecorationSection(position: Int): Boolean {
                return when {
                    paymentSectionAdapter?.currentList.isNullOrEmpty() -> false
                    else -> {
                        val prev = paymentSectionAdapter?.currentList?.getOrNull(position)?.headerType
                        val next = paymentSectionAdapter?.currentList?.getOrNull(position-1)?.headerType
                        prev != next
                    }
                }
            }

            override fun getItemDecorationLayoutRes(position: Int): Int {
                return R.layout.cell_buy_gold_payment_header
            }

            override fun bindItemDecorationData(view: View, position: Int) {
                val header = view.findViewById<AppCompatTextView>(R.id.tvHeader)
                val titleResourceId = paymentSectionAdapter?.currentList?.getOrNull(position)?.headerType?.getHeaderTextResourceId()
                titleResourceId?.let {
                    val title = getString(it)
                    header.isVisible = true
                    header.setHtmlText(title)
                }
            }
        }, 0f)

    override fun setup() {
        viewModel.maxPaymentMethodsCount = buyGoldPaymentOptionsData?.maxPaymentMethodsCount ?: BuyGoldV2Constants.DEFAULT_MAX_PAYMENT_METHODS_COUNT
        observeLiveData()
        setupUI()
        setupListeners()
        getData()
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            viewModel.fireClickEvent(buildMapForAnalytics().apply {
                this[BuyGoldV2EventKey.clickType] = BuyGoldV2EventKey.Cross
                this[BuyGoldV2EventKey.selected_upi] = getSelectedUpiAppName()
            })
            dismiss()
        }

        binding.btnCta.setDebounceClickListener {
            viewModel.selectedPaymentMethod?.let {
                viewModel.fireClickEvent(buildMapForAnalytics().apply {
                    this[BuyGoldV2EventKey.clickType] = BuyGoldV2EventKey.Buy_Now
                    this[BuyGoldV2EventKey.selected_upi] = getSelectedUpiAppName()
                })
                findNavController().getBackStackEntry(R.id.buyGoldV2Fragment).savedStateHandle[BuyGoldV2Constants.INITIATE_BUY_GOLD_DATA] = InitiateBuyGoldData(
                    buyGoldPaymentType = BuyGoldPaymentType.JUSPAY_UPI_INTENT,
                    selectedUpiApp = it
                )
                dismiss()
            }
        }
    }

    private fun setupUI() {
        binding.btnCta.setText(buyGoldPaymentOptionsData?.ctaText ?: getString(com.jar.app.feature_buy_gold_v2.shared.R.string.feature_buy_gold_v2_buy_now))

        paymentSectionAdapter = BuyGoldPaymentOptionAdapter {
            viewModel.updateSelectedPaymentMethod(it)
        }
        binding.rvPaymentSections.layoutManager = LinearLayoutManager(context)
        binding.rvPaymentSections.adapter = paymentSectionAdapter
        binding.rvPaymentSections.addItemDecorationIfNoneAdded(spaceItemDecoration, headerItemDecoration)
    }

    private fun getData() {
        viewModel.fetchEnabledPaymentMethod(null)
        viewModel.fetchRecentlyUsedPaymentMethods(
            isPackageInstalled = {
                context?.isPackageInstalled(it).orFalse()
            },
            flowContext = buyGoldPaymentOptionsData?.context ?: BaseConstants.BuyGoldFlowContext.BUY_GOLD
        )
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.recentlyUsedPaymentMethodFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.mergePaymentData(recentlyUsedPaymentMethods = it)
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(getRootView())
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.listFlow.collectLatest {
                    paymentSectionAdapter?.submitList(it)
                    if (isFirstLaunch)  {
                        viewModel.fireShownEvent(buildMapForAnalytics())
                        isFirstLaunch = false
                        it.getOrNull(0)?.let { upiApp ->
                            viewModel.updateSelectedPaymentMethod(upiApp)
                        }
                    }
                    binding.shimmerPlaceholder.stopShimmer()
                    binding.shimmerPlaceholder.isVisible = false
                    binding.rvPaymentSections.isVisible = true
                    binding.btnCta.isVisible = true
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.enabledPaymentMethodsFlow.collectUnwrapped(
                    onSuccess = {
                        val list = mutableListOf<OneTimePaymentMethodType>()
                        it.paymentMethods.forEach { type ->
                            val paymentMethod = OneTimePaymentMethodType.values().find { it.name == type }
                            if (paymentMethod != null) {
                                list.add(paymentMethod)
                            }
                        }
                        list.forEach { paymentMethodType ->
                            when (paymentMethodType) {
                                OneTimePaymentMethodType.UPI_INTENT -> {
                                    val installedUpiApps = paymentManager.fetchInstalledUpiApps()

                                    val buyGoldUpiAppsList: ArrayList<BuyGoldUpiApp> = ArrayList()
                                    installedUpiApps.forEach {
                                        buyGoldUpiAppsList.add(
                                            BuyGoldUpiApp(
                                                payerApp = it.packageName,
                                                headerType = BuyGoldPaymentSectionHeaderType.UPI_APPS
                                            )
                                        )
                                    }
                                    viewModel.setUpiAppsList(buyGoldUpiAppsList)
                                }
                                else -> {
                                    // Other payment methods not supported
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    private fun getSelectedUpiAppName() = viewModel.selectedPaymentMethod?.payerApp?.getAppNameFromPkgName(requireContext().applicationContext.packageManager).orEmpty()

    private fun buildMapForAnalytics(): MutableMap<String, String> {
        val packageManager = requireContext().applicationContext.packageManager
        return mutableMapOf<String, String>(
            BuyGoldV2EventKey.recommended_upi to viewModel.paymentMethodsList.getOrNull(0)?.payerApp.orEmpty().getAppNameFromPkgName(packageManager).orEmpty(),
            BuyGoldV2EventKey.available_upi to
                    if (viewModel.paymentMethodsList.size > 1)
                        viewModel.paymentMethodsList.subList(1, viewModel.paymentMethodsList.size).joinToString(",") { it.payerApp.getAppNameFromPkgName(packageManager).orEmpty() }
                    else
                        ""
        )
    }

    override fun onDestroyView() {
        paymentSectionAdapter = null
        super.onDestroyView()
    }
}