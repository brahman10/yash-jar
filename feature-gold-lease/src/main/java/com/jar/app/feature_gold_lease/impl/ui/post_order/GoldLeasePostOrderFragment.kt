package com.jar.app.feature_gold_lease.impl.ui.post_order

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseAppDeeplink
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeasePostOrderBinding
import com.jar.app.feature_gold_lease.impl.domain.model.getStatusIcon
import com.jar.app.feature_gold_lease.shared.domain.model.*
import com.jar.app.feature_gold_lease.impl.ui.jeweller_details.GoldLeaseV2TitleValuePairAdapter
import com.jar.app.feature_gold_lease.shared.ui.GoldLeasePostOrderViewModel
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.app.feature_transaction.impl.domain.model.getStatusColor
import com.jar.app.feature_transaction.impl.domain.model.getStatusIcon
import com.jar.app.feature_transactions_common.shared.NewTransactionRoutineStatus
import com.jar.app.feature_transaction.impl.ui.new_details.adapter.NewTransactionRoutineAdapter
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldLeasePostOrderFragment : BaseFragment<FragmentGoldLeasePostOrderBinding>(){

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    private val args by navArgs<GoldLeasePostOrderFragmentArgs>()

    private val viewModelProvider by viewModels<GoldLeasePostOrderViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val spaceItemDecorationVertical4 = SpaceItemDecoration(0.dp, 4.dp)
    private val spaceItemDecorationVertical12 = SpaceItemDecoration(0.dp, 12.dp)

    private var goldLeaseTxnRoutineAdapter: NewTransactionRoutineAdapter? = null

    private var goldLeaseV2TitleValuePairAdapter: GoldLeaseV2TitleValuePairAdapter? = null

    private var goldLeaseOrderDetailsAdapter: GoldLeaseOrderDetailsAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeasePostOrderBinding
        get() = FragmentGoldLeasePostOrderBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(ToolbarNone)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchGoldLeaseStatus(args.leaseId)
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeasStatusFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setupDataInUI(it)
                            setupListeners(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakRef.get()!!)
                    }
                )
            }
        }
    }

    private fun setupDataInUI(goldLeasePostOrder: GoldLeaseV2StatusResponse) {
        binding.tvPostOrderTitle.setHtmlText(goldLeasePostOrder.title.orEmpty())

        val leaseStatus = goldLeasePostOrder.getLeaseTransactionStatus()
        leaseStatus?.getStatusIcon()?.let {
            binding.ivStatus.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), it
                )
            )
        }

        leaseStatus?.let {
            val eventName = when (it) {
                LeaseV2TransactionStatus.SUCCESS -> GoldLeaseEventKey.LeasePostOrderScreen.Lease_GoldLeaseSuccessful
                LeaseV2TransactionStatus.PENDING -> GoldLeaseEventKey.LeasePostOrderScreen.Lease_GoldLeaseProcessing
                LeaseV2TransactionStatus.FAILURE -> GoldLeaseEventKey.LeasePostOrderScreen.Lease_GoldLeaseFailed
            }
            val stage = goldLeasePostOrder.transactionStatusDetails?.txnRoutineList?.find { it.currentStep.orFalse() }?.let { it.title.orEmpty() } ?: ""
            analyticsApi.postEvent(
                eventName,
                mapOf(
                    GoldLeaseEventKey.Properties.TRANSACTION_STAGE to stage,
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.LEASED_GOLD to goldLeasePostOrder.leasePostOrderHeaderInfo?.volume.orEmpty(),
                    GoldLeaseEventKey.Properties.GOLD_AMOUNT to goldLeasePostOrder.totalGoldVolume.orZero(),
                    GoldLeaseEventKey.Properties.LOCKER_GOLD_USED to goldLeasePostOrder.lockerGoldVolume.orZero(),
                    GoldLeaseEventKey.Properties.NON_LOCKER_GOLD_QUANTITY to goldLeasePostOrder.totalGoldVolume.orZero() - goldLeasePostOrder.lockerGoldVolume.orZero(),
                    GoldLeaseEventKey.Properties.NON_LOCKER_GOLD_PRICE to goldLeasePostOrder.purchasedGoldAmount.orZero(),
                    GoldLeaseEventKey.Properties.TOTAL_GOLD_PRICE to goldLeasePostOrder.totalGoldAmount.orZero()
                )
            )
        }

        binding.groupContactUs.isVisible = leaseStatus != LeaseV2TransactionStatus.SUCCESS

        if (leaseStatus == LeaseV2TransactionStatus.SUCCESS) {
            binding.lottieCelebration.cancelAnimation()
            binding.lottieCelebration.playLottieWithUrlAndExceptionHandling(
                requireContext(),
                BaseConstants.LottieUrls.CONFETTI_FROM_TOP
            )
        }

        goldLeasePostOrder.primaryCta?.let {
            binding.btnPrimaryCta.isVisible = true
            binding.btnPrimaryCta.setText(it)
        } ?: kotlin.run {
            binding.btnPrimaryCta.isVisible = false
        }

        goldLeasePostOrder.secondaryCta?.let {
            binding.btnSecondaryCta.isVisible = true
            binding.btnSecondaryCta.setText(it)
        } ?: kotlin.run {
            binding.btnSecondaryCta.isVisible = false
        }

        //Set Header UI
        setupHeaderInfoUI(goldLeasePostOrder.leasePostOrderHeaderInfo)

        //Set Transaction Status UI
        setupTransactionDetailsUI(goldLeasePostOrder.transactionStatusDetails)

        //Set Lease Details UI
        setupLeaseDetailsUI(goldLeasePostOrder.postOrderLeaseDetailsCard)

        //Set Order Details UI
        setupOrderDetailsUI(goldLeasePostOrder.leasePostOrderDetailsComponent)

        binding.clRootContainer.isVisible = true
    }

    private fun setupOrderDetailsUI(leaseOrderDetails: LeasePostOrderDetailsComponent?) {
        binding.clOrderDetails.isVisible = leaseOrderDetails != null

        binding.tvOrderTitle.setHtmlText(leaseOrderDetails?.title.orEmpty())
        goldLeaseOrderDetailsAdapter = GoldLeaseOrderDetailsAdapter()
        binding.rvOrderDetails.adapter = goldLeaseOrderDetailsAdapter
        binding.rvOrderDetails.layoutManager = LinearLayoutManager(requireContext())

        val dividerDecorator =
            object : DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL) {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val current = goldLeaseOrderDetailsAdapter?.currentList?.getOrNull(position)?.title
                    val prev = goldLeaseOrderDetailsAdapter?.currentList?.getOrNull(position + 1)?.title
                    if (current != prev)
                        outRect.setEmpty()
                    else
                        super.getItemOffsets(outRect, view, parent, state)
                }
            }
        ContextCompat.getDrawable(
            requireContext(),
            com.jar.app.core_ui.R.drawable.core_ui_line_separator
        )?.let {
            dividerDecorator.setDrawable(it)
        }

        binding.rvOrderDetails.addItemDecorationIfNoneAdded(spaceItemDecorationVertical12, dividerDecorator)

        leaseOrderDetails?.leasePostOrderDetailsItemList?.let {
            goldLeaseOrderDetailsAdapter?.submitList(it)
        }
    }

    private fun setupLeaseDetailsUI(postOrderLeaseDetails: LeasePostOrderDetailsCard?) {
        binding.clLeaseDetails.isVisible = postOrderLeaseDetails != null

        binding.tvLeaseTitle.setHtmlText(postOrderLeaseDetails?.title.orEmpty())
        goldLeaseV2TitleValuePairAdapter = GoldLeaseV2TitleValuePairAdapter(
            onClickedCopyTransactionId = {
                requireContext().copyToClipboard(it.value.orEmpty(), getCustomString(MR.strings.copied))
            },
            onWebsiteClicked = {
                openUrlInChromeTabOrExternalBrowser(requireContext(), it)
            }
        )
        binding.rvLeaseDetails.adapter = goldLeaseV2TitleValuePairAdapter
        binding.rvLeaseDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeaseDetails.addItemDecorationIfNoneAdded(spaceItemDecorationVertical4)

        postOrderLeaseDetails?.valueCommonLeaseComponentList?.let {
            goldLeaseV2TitleValuePairAdapter?.submitList(it)
        }
    }

    private fun setupHeaderInfoUI(postOrderHeaderInfo: LeasePostOrderHeaderInfo?) {
        binding.clDetails.isVisible = postOrderHeaderInfo != null
        Glide.with(requireContext())
            .load(postOrderHeaderInfo?.icon.orEmpty())
            .into(binding.ivDetailsIcon)
        binding.tvDetailsTitle.setHtmlText(postOrderHeaderInfo?.title.orEmpty())
        binding.tvDetailsDescription.setHtmlText(postOrderHeaderInfo?.description.orEmpty())
        binding.tvDetailsValue.setHtmlText(postOrderHeaderInfo?.volume.orEmpty())
    }

    private fun setupTransactionDetailsUI(transactionStatus: LeaseTransactionStatusDetails?) {
        binding.llTransactionStatus.isVisible = transactionStatus != null

        binding.tvTransactionTitle.setHtmlText(transactionStatus?.title.orEmpty())
        goldLeaseTxnRoutineAdapter = NewTransactionRoutineAdapter {
            popBackStack()
            EventBus.getDefault().post(HandleDeepLinkEvent(it.txnRoutineCtaDetails?.ctaButtonDeeplink.orEmpty()))
        }
        val timelineItemDecoration =
            com.jar.app.core_ui.item_decoration.TimelineItemDecoration(object :
                com.jar.app.core_ui.item_decoration.TimelineItemDecoration.SectionCallback {
                override fun isHeaderSection(position: Int): Boolean {
                    return true
                }

                override fun getHeaderLayoutRes(position: Int): Int {
                    return com.jar.app.core_ui.R.layout.core_ui_cell_progress_timeline
                }

                override fun bindHeaderData(view: View, position: Int) {
                    goldLeaseTxnRoutineAdapter?.currentList?.getOrNull(position)?.let {
                        val routineStatus = it.getTxnRoutineStatus()
                        routineStatus.let { status ->
                            view.findViewById<AppCompatImageView>(com.jar.app.core_ui.R.id.ivIcon)
                                .setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(), status.getStatusIcon()
                                    )
                                )
                            val line = view.findViewById<View>(com.jar.app.core_ui.R.id.line)
                            val isLastItem =
                                (position == (goldLeaseTxnRoutineAdapter?.itemCount.orZero() - 1))
                            val nextItem = if (isLastItem.not()) goldLeaseTxnRoutineAdapter?.currentList?.getOrNull(position+1) else null
                            view.findViewById<AppCompatImageView>(com.jar.app.core_ui.R.id.ivDashedCircle).isInvisible = it.currentStep.orFalse().not()

                            line.setBackgroundColor(
                                ContextCompat.getColor(
                                    binding.root.context,
                                    if (isLastItem) com.jar.app.core_ui.R.color.lightBgColor else if (nextItem?.getTxnRoutineStatus() == NewTransactionRoutineStatus.INACTIVE) com.jar.app.core_ui.R.color.color_776E94  else status.getStatusColor()
                                )
                            )
                        }
                    }
                }
            })
        binding.rvTransactionRoutine.adapter = goldLeaseTxnRoutineAdapter
        binding.rvTransactionRoutine.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactionRoutine.addItemDecorationIfNoneAdded(timelineItemDecoration, spaceItemDecorationVertical4)

        transactionStatus?.txnRoutineList?.let {
            goldLeaseTxnRoutineAdapter?.submitList(it)
        }

        transactionStatus?.invoiceLink?.let {
            binding.btnDownloadInvoice.isVisible = true
        } ?: kotlin.run {
            binding.btnDownloadInvoice.isVisible = false
        }
    }

    private fun setupListeners(goldLeasePostOrder: GoldLeaseV2StatusResponse) {
        goldLeasePostOrder.transactionStatusDetails?.invoiceLink?.let { inVoiceLink ->
            binding.btnDownloadInvoice.setDebounceClickListener {
                webPdfViewerApi.openPdf(inVoiceLink)
            }
        }

        binding.ivExpandLeaseDetails.setDebounceClickListener {
            expandLeaseDetails()
        }

        binding.tvLeaseTitle.setDebounceClickListener {
            expandLeaseDetails()
        }

        binding.ivExpandOrderDetails.setDebounceClickListener {
            expandOrderDetails()
        }

        binding.tvOrderTitle.setDebounceClickListener {
            expandOrderDetails()
        }

        binding.llContactUs.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_ContactSupport,
                mapOf(
                    GoldLeaseEventKey.Properties.FROM_FLOW to args.flowType,
                    GoldLeaseEventKey.Properties.LEASE_STATUS to goldLeasePostOrder.getLeaseTransactionStatus()?.name.orEmpty(),
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.POST_ORDER
                )
            )
            requireContext().openWhatsapp(remoteConfigManager.getWhatsappNumber(), goldLeasePostOrder.whatsappMessage.orEmpty())
        }

        val status = goldLeasePostOrder.getLeaseTransactionStatus()

        binding.btnPrimaryCta.setDebounceClickListener {
            status?.let {
                when (it) {
                    LeaseV2TransactionStatus.SUCCESS -> {
                        popBackStack()
                        navigateTo(
                            "${BaseAppDeeplink.GoldLease.GOLD_LEASE_USER_LEASE_DETAILS_SCREEN}/${args.flowType}/${goldLeasePostOrder.leaseId.orEmpty()}"
                        )
                    }
                    LeaseV2TransactionStatus.PENDING, LeaseV2TransactionStatus.FAILURE -> {
                        goToHome()
                    }
                }
            }
        }

        binding.btnSecondaryCta.setDebounceClickListener {
            goToHome()
        }
    }

    private fun goToHome() {
        popBackStack()
    }

    private fun expandOrderDetails() {
        binding.ivExpandOrderDetails.animate()
            .rotation(if (binding.elOrderDetails.isExpanded) 0f else 180f).start()
        binding.elOrderDetails.toggle()
    }

    private fun expandLeaseDetails() {
        binding.ivExpandLeaseDetails.animate()
            .rotation(if (binding.elLeaseDetails.isExpanded) 0f else 180f).start()
        binding.elLeaseDetails.toggle()
    }
}