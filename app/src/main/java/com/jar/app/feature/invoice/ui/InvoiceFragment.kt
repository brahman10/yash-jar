package com.jar.app.feature.invoice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.view_holder.LoadStateAdapter
import com.jar.app.databinding.FragmentInvoiceBinding
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.base.util.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
internal class InvoiceFragment : BaseFragment<FragmentInvoiceBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInvoiceBinding
        get() = FragmentInvoiceBinding::inflate

    private var job: Job? = null
    private var invoiceAdapter: InvoiceAdapter? = null
    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()
    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(5.dp, 6.dp)
    private val hasAnimatedOnce = AtomicBoolean(false)

    private val viewModel by viewModels<InvoiceViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarDefault(title = getString(R.string.invoices)))))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        getData()
        observeLiveData()
        setupListeners()
    }

    private fun setupUI() {
        viewModel.fetchInvoices()
        invoiceAdapter = InvoiceAdapter {
            openUrlInChromeTab(it.invoiceLink, getString(R.string.invoices), true)
            analyticsHandler.postEvent(EventKey.INVOICE_CLICKED)
        }
        binding.rvInvoice.adapter = invoiceAdapter?.withLoadStateFooter(
            footer = LoadStateAdapter {
                invoiceAdapter?.retry()
            }
        )
        binding.rvInvoice.layoutManager = LinearLayoutManager(context)
        binding.rvInvoice.addItemDecoration(spaceItemDecoration)
        binding.rvInvoice.edgeEffectFactory = baseEdgeEffectFactory
    }

    private fun getData() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchInvoices()
                    .collect {
                        binding.swipeRefresh.isRefreshing = false
                        invoiceAdapter?.submitData(it)
                    }
            }
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                invoiceAdapter?.loadStateFlow?.collect { loadState ->

                    val isRefreshing = loadState.refresh is LoadState.Loading
                    binding.swipeRefresh.isRefreshing = isRefreshing
                    when (isRefreshing) {
                        true -> {
                            binding.shimmerPlaceholder.startShimmer()
                        }
                        false -> {
                            val isListEmpty =
                                loadState.refresh is LoadState.NotLoading && invoiceAdapter?.itemCount == 0

                            binding.clEmptyPlaceHolder.isVisible = isListEmpty
                            binding.rvInvoice.isVisible = !isListEmpty

                            binding.shimmerPlaceholder.isVisible = false
                            binding.shimmerPlaceholder.stopShimmer()

                            if (hasAnimatedOnce.getAndSet(true).not()) {
                                binding.rvInvoice.runLayoutAnimation(com.jar.app.core_ui.R.anim.layout_animation_fall_down)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            invoiceAdapter?.refresh()
        }
    }

}