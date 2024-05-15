package com.jar.app.feature_gold_lease.impl.ui.user_lease_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_gold_lease.GoldLeaseNavigationDirections
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2UserLeaseDetailsBinding
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusBg
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusText
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusTextColor
import com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseV2Details
import com.jar.app.feature_gold_lease.impl.ui.jeweller_details.GoldLeaseV2TitleValuePairAdapter
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2UserLeaseDetailsViewModel
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldLeaseV2UserLeaseDetailsFragment : BaseFragment<FragmentGoldLeaseV2UserLeaseDetailsBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    private val args by navArgs<GoldLeaseV2UserLeaseDetailsFragmentArgs>()

    private val viewModelProvider by viewModels<GoldLeaseV2UserLeaseDetailsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2UserLeaseDetailsBinding
        get() = FragmentGoldLeaseV2UserLeaseDetailsBinding::inflate

    private var goldLeaseV2TitleValuePairAdapter: GoldLeaseV2TitleValuePairAdapter? = null
    private val spaceItemDecorationVertical4 = SpaceItemDecoration(0.dp, 4.dp)

    private var goldLeaseTransactionAdapter: GoldLeaseTransactionAdapter? = null

    private var spaceItemDecorationVertical8 = SpaceItemDecoration(0.dp, 8.dp)
    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun setupUI() {
        setupToolbar()

        goldLeaseV2TitleValuePairAdapter = GoldLeaseV2TitleValuePairAdapter(
            onClickedCopyTransactionId = {
                requireContext().copyToClipboard(it.value.orEmpty(), getCustomString(MR.strings.copied))
            },
            onWebsiteClicked = {
                openUrlInChromeTab(it, title = "", showToolbar = false)
            }
        )
        binding.rvLeaseInformation.adapter = goldLeaseV2TitleValuePairAdapter
        binding.rvLeaseInformation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeaseInformation.addItemDecorationIfNoneAdded(spaceItemDecorationVertical4)

        goldLeaseTransactionAdapter = GoldLeaseTransactionAdapter {
            postClickEvent(GoldLeaseEventKey.Values.TRANSACTION, it.title.orEmpty())
            //Actual Deeplink :- android-app://com.jar.app/newTransactionDetail/{orderId}/{txnId}/{sourceType}
            navigateTo(
                "android-app://com.jar.app/newTransactionDetail/${it.orderId}/${it.assetTransactionId}/${it.assetSourceType}"
            )
        }
        binding.rvTransactions.adapter = goldLeaseTransactionAdapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.addItemDecorationIfNoneAdded(spaceItemDecorationVertical8)
    }

    private fun getData() {
        viewModel.fetchLandingDetails(args.leaseId)
        viewModel.fetchTransactions(args.leaseId)
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        observeLeaseDetailsLiveData(weakReference)
        observeGoldLeaseTransactionsLiveData(weakReference)
    }

    private fun observeGoldLeaseTransactionsLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseTransactionsFlow.collect(
                    onLoading = {
                        binding.shimmerLayout.isVisible = true
                        binding.rvTransactions.isVisible = false
                        binding.shimmerLayout.startShimmer()
                    },
                    onSuccess = {
                        binding.shimmerLayout.isVisible = false
                        binding.rvTransactions.isVisible = true
                        binding.shimmerLayout.startShimmer()
                        goldLeaseTransactionAdapter?.submitList(it)
                    },
                    onError = { _,_ ->
                        binding.shimmerLayout.isVisible = false
                        binding.rvTransactions.isVisible = false
                        binding.shimmerLayout.stopShimmer()
                    }
                )
            }
        }
    }

    private fun observeLeaseDetailsLiveData(weakReference: WeakReference<View>) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupDataInUIAndClickListeners(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    }
                )
            }
        }
    }

    private fun setJewellerInfoAndStatus(goldLeaseV2Details: GoldLeaseV2Details) {
        Glide.with(requireContext())
            .load(goldLeaseV2Details.jewellerIcon.orEmpty())
            .into(binding.ivJewellerIcon)
        binding.tvJewellerName.setHtmlText(goldLeaseV2Details.jewellerName.orEmpty())
        binding.tvJewellerEst.setHtmlText(goldLeaseV2Details.jewellerEstablished.orEmpty())
        binding.tvJewellerName.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.INFO_ICON)
            openJewellerDetails(goldLeaseV2Details.jewellerId)
        }
        binding.tvJewellerEst.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.INFO_ICON)
            openJewellerDetails(goldLeaseV2Details.jewellerId)
        }
        val status = goldLeaseV2Details.getUserLeaseStatus()
        status?.let {
            binding.tvStatus.setText(it.getStatusText())
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    requireContext(), it.getStatusTextColor()
                )
            )
            binding.tvStatus.setBackgroundResource(it.getStatusBg())
        }
    }

    private fun setLeaseDetailsTabs(goldLeaseV2Details: GoldLeaseV2Details) {
        binding.tvLeasedGoldTitle.setHtmlText(goldLeaseV2Details.leasedGoldComponent?.title.orEmpty())
        binding.tvLeasedGoldValue.setHtmlText(goldLeaseV2Details.leasedGoldComponent?.value.orEmpty())

        binding.tvGoldXEarningsTitle.setHtmlText(goldLeaseV2Details.earningsTillDateComponent?.title.orEmpty())
        binding.tvGoldXEarningsValue.setHtmlText(goldLeaseV2Details.earningsTillDateComponent?.value.orEmpty())

        binding.tvLockerTitle.setHtmlText(goldLeaseV2Details.jarGoldUsedComponent?.title.orEmpty())
        binding.tvLockerValue.setHtmlText(goldLeaseV2Details.jarGoldUsedComponent?.value.orEmpty())

        binding.tvEarningMonthTitle.setHtmlText(goldLeaseV2Details.currentMonthEarningsComponent?.title.orEmpty())
        binding.tvEarningMonthValue.setHtmlText(goldLeaseV2Details.currentMonthEarningsComponent?.value.orEmpty())

        binding.tvGoldPurchaseTitle.setHtmlText(goldLeaseV2Details.goldPurchasedComponent?.title.orEmpty())
        binding.tvGoldPurchaseValue.setHtmlText(goldLeaseV2Details.goldPurchasedComponent?.value.orEmpty())

        binding.tvEarningInLockerTitle.setHtmlText(goldLeaseV2Details.earningsCreditedComponent?.title.orEmpty())
        binding.tvEarningInLockerValue.setHtmlText(goldLeaseV2Details.earningsCreditedComponent?.value.orEmpty())
    }

    private fun setTitleAndSubTitleComponent(goldLeaseV2Details: GoldLeaseV2Details) {
        Glide.with(requireContext())
            .load(goldLeaseV2Details.userCommunicationComponent?.iconLink.orEmpty())
            .into(binding.ivSubIcon)
        binding.tvSubTitle.setHtmlText(goldLeaseV2Details.userCommunicationComponent?.description.orEmpty())

        binding.tvLeaseInfoTitle.setHtmlText(goldLeaseV2Details.leaseOrderDetailsInformationComponent?.title.orEmpty())
        binding.tvEarningsPATitle.setHtmlText(goldLeaseV2Details.leaseOrderDetailsInformationComponent?.earningsPercentageComponent?.title.orEmpty())
        binding.tvJarBonusTag.isVisible =
            goldLeaseV2Details.leaseOrderDetailsInformationComponent?.jarBonusPercentage.orZero() != 0.0f
        val earningsPercentText =
            if (goldLeaseV2Details.leaseOrderDetailsInformationComponent?.jarBonusPercentage.orZero() != 0.0f) {
                "${goldLeaseV2Details.leaseOrderDetailsInformationComponent?.earningsPercentageComponent?.value.orEmpty()} + ${goldLeaseV2Details.leaseOrderDetailsInformationComponent?.jarBonusPercentage.orZero()}"
            } else {
                goldLeaseV2Details.leaseOrderDetailsInformationComponent?.earningsPercentageComponent?.value.orEmpty()
            }
        binding.tvEarningsPAValue.text = earningsPercentText
    }

    private fun setOrderDetailsAndAgreementComponent(goldLeaseV2Details: GoldLeaseV2Details) {
        goldLeaseV2TitleValuePairAdapter?.submitList(goldLeaseV2Details.leaseOrderDetailsInformationComponent?.leaseOrderInformationComponentValuesList)
        binding.groupAgreement.isVisible = goldLeaseV2Details.leaseOrderDetailsAgreementComponent?.agreementLink != null
        binding.tvAgreementTitle.setHtmlText(goldLeaseV2Details.leaseOrderDetailsAgreementComponent?.title.orEmpty())
        binding.btnViewAgreement.setText(goldLeaseV2Details.leaseOrderDetailsAgreementComponent?.ctaText.orEmpty())
        binding.btnViewAgreement.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.AGREEMENT)
            goldLeaseV2Details.leaseOrderDetailsAgreementComponent?.agreementLink?.let {
                webPdfViewerApi.openPdf(it)
            }
        }
    }

    private fun setupDataInUIAndClickListeners(goldLeaseV2Details: GoldLeaseV2Details) {

        setJewellerInfoAndStatus(goldLeaseV2Details)
        setLeaseDetailsTabs(goldLeaseV2Details)
        setTitleAndSubTitleComponent(goldLeaseV2Details)
        setOrderDetailsAndAgreementComponent(goldLeaseV2Details)


        binding.btnContactUs.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_ContactSupport,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.LEASE_STATUS to viewModel.goldLeaseDetailsFlow.value.data?.data?.getUserLeaseStatus()?.name.orEmpty(),
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LEASE_ORDER_DETAILS_SCREEN
                )
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), goldLeaseV2Details.whatsappMessage.orEmpty())
        }

        binding.svContent.isVisible = true

        analyticsApi.postEvent(
            GoldLeaseEventKey.UserLeaseDetailsScreen.Lease_OrderDetailsScreenLaunched,
            mapOf(
                GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                GoldLeaseEventKey.Properties.LEASE_TITLE to goldLeaseV2Details.jewellerName.orEmpty(),
                GoldLeaseEventKey.Properties.LEASED_GOLD to goldLeaseV2Details.leasedGoldComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.GOLD_X_EARNINGS to goldLeaseV2Details.earningsTillDateComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.LOCKER_GOLD_USED to goldLeaseV2Details.leasedGoldComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.EARNINGS_THIS_MONTH to goldLeaseV2Details.currentMonthEarningsComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.EARNINGS_ADDED_TO_LOCKER to goldLeaseV2Details.earningsCreditedComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.ADDITIONAL_GOLD to goldLeaseV2Details.goldPurchasedComponent?.value.orEmpty(),
                GoldLeaseEventKey.Properties.LEASE_STATUS to goldLeaseV2Details.getUserLeaseStatus()?.name.orEmpty(),
                GoldLeaseEventKey.Properties.YEARLY_EARNINGS to goldLeaseV2Details.earningsPercentage.orZero(),
                GoldLeaseEventKey.Properties.START_DATE to goldLeaseV2Details.startDate.orEmpty(),
                GoldLeaseEventKey.Properties.END_DATE to goldLeaseV2Details.endDate.orEmpty(),
                GoldLeaseEventKey.Properties.LOCK_IN to goldLeaseV2Details.lockInDays.orZero().toString()
            )
        )
    }

    private fun openJewellerDetails(jewellerId: String?) {
        jewellerId?.let {
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseV2JewellerDetailsBottomSheetFragment(
                    flowType = args.flowType,
                    jewellerId = it
                )
            )
        }
    }

    private fun setupListeners() {
        binding.ivExpandLeaseInformation.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.LEASE_INFORMATION)
            expandLeaseInformation()
        }

        binding.tvLeaseInfoTitle.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.LEASE_INFORMATION)
            expandLeaseInformation()
        }

        binding.ivExpandAgreement.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.AGREEMENT)
            expandAgreementInfo()
        }

        binding.tvAgreementTitle.setDebounceClickListener {
            postClickEvent(GoldLeaseEventKey.Values.AGREEMENT)
            expandAgreementInfo()
        }
    }

    private fun postClickEvent(buttonType: String, transactionType: String? = null) {
        transactionType?.let {
            analyticsApi.postEvent(
                GoldLeaseEventKey.UserLeaseDetailsScreen.Lease_OrderDetailsScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.BUTTON_TYPE to buttonType,
                    GoldLeaseEventKey.Properties.TRANSACTION_TYPE to transactionType
                )
            )
        } ?: kotlin.run {
            analyticsApi.postEvent(
                GoldLeaseEventKey.UserLeaseDetailsScreen.Lease_OrderDetailsScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.BUTTON_TYPE to buttonType
                )
            )
        }
    }

    private fun expandLeaseInformation() {
        binding.ivExpandLeaseInformation.animate()
            .rotation(if (binding.elLeaseInformation.isExpanded) 0f else 180f).start()
        binding.elLeaseInformation.toggle()
    }

    private fun expandAgreementInfo() {
        binding.ivExpandAgreement.animate()
            .rotation(if (binding.elAgreement.isExpanded) 0f else 180f).start()
        binding.elAgreement.toggle()
    }

    private fun setupToolbar() {
        binding.toolbar.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.bgColor
            )
        )
        binding.toolbar.tvTitle.isVisible = true
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.separator.isVisible = true

        binding.toolbar.tvTitle.text = getString(R.string.feature_gold_lease_lease_order_details)

        //Setup FAQ Button
        binding.toolbar.tvEnd.setBackgroundResource(com.jar.app.core_ui.R.drawable.bg_rounded_40_121127)
        binding.toolbar.tvEnd.setPadding(16.dp, 8.dp, 16.dp, 8.dp)
        binding.toolbar.tvEnd.setTextColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        binding.toolbar.tvEnd.text = getString(R.string.feature_gold_lease_faqs)
        binding.toolbar.tvEnd.isVisible = true

        binding.toolbar.tvEnd.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_FAQButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LEASE_ORDER_DETAILS_SCREEN
                )
            )
            navigateTo(
                GoldLeaseNavigationDirections.actionToGoldLeaseFaqBottomSheetFragment(
                    flowType = args.flowType
                )
            )
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_BackButtonClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LEASE_ORDER_DETAILS_SCREEN
                )
            )
            popBackStack()
        }
    }
}