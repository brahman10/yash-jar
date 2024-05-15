package com.jar.app.feature_transaction.impl.ui.new_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.pull_to_refresh_overlay.PullToRefreshListener
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentNewTransactionDetailsBinding
import com.jar.app.feature_transaction.impl.ui.new_details.adapter.*
import com.jar.app.feature_transaction.impl.ui.new_details.adapter.NewTransactionDetailsScreenAdapter
import com.jar.app.feature_transaction.impl.ui.new_details.adapter.NewTransactionHeaderInfoAdapter
import com.jar.app.feature_transaction.impl.ui.new_details.adapter.NewTransactionStatusAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * New Transaction Details will be only opened from Transactions Tab and Gold Lease Flow as of now,
 * we will implement at other places once design for other flows is finalised.
 * **/
@AndroidEntryPoint
internal class NewTransactionDetailsFragment : BaseFragment<FeatureTransactionFragmentNewTransactionDetailsBinding>(){

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi

    private val viewModel by viewModels<NewTransactionsDetailsViewModel>{ defaultViewModelProviderFactory }

    private var baseEdgeEffectFactory: BaseEdgeEffectFactory? = null
    private var layoutManager: LinearLayoutManager? = null
    private val spaceItemDecoration = SpaceItemDecoration(16.dp, 8.dp)

    private var screenAdapter: NewTransactionDetailsScreenAdapter? = null

    private val newTransactionStatusAdapter = NewTransactionStatusAdapter(
        onCtaClicked = {
            EventBus.getDefault().post(HandleDeepLinkEvent(it.txnRoutineCtaDetails?.ctaButtonDeeplink.orEmpty()))
        },
        onDownloadInvoiceClicked = {
            webPdfViewerApi.openPdf(it)
        }
    )

    private val newTransactionWhatsAppAdapter = NewTransactionWhatsAppAdapter(
        onClickListener = {
            val number = remoteConfigManager.getWhatsappNumber()
            requireContext().openWhatsapp(number, viewModel.transactionDetailsLiveData.value?.whatsappMessage.orEmpty())
        }
    )

    private val newTransactionHeaderInfoAdapter = NewTransactionHeaderInfoAdapter()

    private val newTransactionOrderDetailsAdapter = NewTransactionOrderDetailsAdapter()

    private val adapterDelegates = listOf(
        newTransactionStatusAdapter,
        newTransactionHeaderInfoAdapter,
        newTransactionOrderDetailsAdapter,
        newTransactionWhatsAppAdapter
    )

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentNewTransactionDetailsBinding
        get() = FeatureTransactionFragmentNewTransactionDetailsBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        getData()
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {
        binding.pullToRefreshOverlay.setPullListener(object : PullToRefreshListener {
            override fun onPulledToRefresh() {
                binding.swipeRefresh.isEnabled = true
                binding.pullToRefreshOverlay.isVisible = false
                prefs.setShowTransactionDetailOverLay(false)
                binding.swipeRefresh.isRefreshing = true
                getData()
            }

            override fun onClickedSomewhereElse() {
                binding.swipeRefresh.isEnabled = true
                binding.pullToRefreshOverlay.isVisible = false
                prefs.setShowTransactionDetailOverLay(false)
            }
        })

        binding.rvTransactionDetails.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefresh.isEnabled =
                    binding.pullToRefreshOverlay.isVisible.not() && recyclerView.canScrollVertically(
                        -1
                    ).not()
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            getData()
        }
    }

    private fun setupUI() {
        setupToolbar()

        if (prefs.shouldShowTransactionDetailOverLay()) {
            binding.pullToRefreshOverlay.isVisible = true
            binding.swipeRefresh.isEnabled = false
        }

        layoutManager = LinearLayoutManager(requireContext())
        layoutManager?.isItemPrefetchEnabled = true
        layoutManager?.initialPrefetchItemCount = 10
        binding.rvTransactionDetails.layoutManager = layoutManager
        binding.rvTransactionDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        baseEdgeEffectFactory = BaseEdgeEffectFactory()
        binding.rvTransactionDetails.edgeEffectFactory = baseEdgeEffectFactory!!
        screenAdapter = NewTransactionDetailsScreenAdapter(adapterDelegates)
        binding.rvTransactionDetails.adapter = screenAdapter
        screenAdapter?.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)

        viewModel.cardsLiveData.observe(viewLifecycleOwner) { t ->
            if (!t.isNullOrEmpty()) {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.isVisible = false
                binding.rvTransactionDetails.isVisible = true
                binding.swipeRefresh.isRefreshing = false
            }
            screenAdapter?.items = t
        }

        viewModel.transactionDetailsLiveData.observe(viewLifecycleOwner) {
            it?.let {
                setupToolbarTitle(it.toolbarTitle.orEmpty())
            }
        }
    }

    private fun setupToolbarTitle(title: String) {
        binding.toolbar.tvTitle.text = title
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

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun getData() {
        viewModel.fetchTransactionDetails()
    }

    override fun onDestroyView() {
        screenAdapter = null
        layoutManager = null
        baseEdgeEffectFactory = null
        super.onDestroyView()
    }
}