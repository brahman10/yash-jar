package com.myjar.app.feature_graph_manual_buy.impl.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.calendarView.adapter.CalendarViewAdapterDelegate
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.myjar.app.feature_graph_manual_buy.R
import com.myjar.app.feature_graph_manual_buy.databinding.GraphManualBuyLayoutBinding
import com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate.BottomImageDeligate
import com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate.ManualBuyGraphAdapter
import com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate.ManualBuyGraphDelegate
import com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate.ManualBuyGraphFaqsDeligate
import com.myjar.app.feature_graph_manual_buy.impl.ui.adapterDelegate.NeedHelpManualBuyGraphDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class GraphManualBuyFragment: BaseFragment<GraphManualBuyLayoutBinding>() {

    private val viewModel: GraphManualBuyFragmentViewModel by viewModels { defaultViewModelProviderFactory }

    private val graphViewAdapterDelegate: ManualBuyGraphDelegate by lazy {
        ManualBuyGraphDelegate()
    }
    private val calendarViewAdapterDelegate: CalendarViewAdapterDelegate by lazy {
        CalendarViewAdapterDelegate(
            uiScope = uiScope,
            onDayClick = {
                viewModel.handleAction(
                    GraphManualBuyFragmentAction.OnDayClick
                )
            },
            onNextMonthClicked = {
                viewModel.handleAction(
                    GraphManualBuyFragmentAction.OnClickOnNextOnCalender
                )
            }, onPrevMonthClicked = {
                viewModel.handleAction(
                    GraphManualBuyFragmentAction.OnClickOnPreviousOnCalender
                )
            }, onDSOperationCtaClicked = {
                viewModel.handleAction(
                    GraphManualBuyFragmentAction.OnClickOnCalenderCta
                )
                EventBus.getDefault().post(
                    HandleDeepLinkEvent(it.deeplink ?: "")
                )
            }
        )
    }

    private val faqsDelegate: ManualBuyGraphFaqsDeligate by lazy {
        ManualBuyGraphFaqsDeligate {
            viewModel.handleAction(
                GraphManualBuyFragmentAction.OnClickOnFaqs
            )
        }
    }

    private val needHelpDelegate: NeedHelpManualBuyGraphDelegate by lazy {
        NeedHelpManualBuyGraphDelegate {
            viewModel.handleAction(
                GraphManualBuyFragmentAction.ClickOnNeedHelp
            )
        }
    }

    private val bottomImageDeligate: BottomImageDeligate by lazy {
        BottomImageDeligate()
    }

    private val adapter by lazy {
        ManualBuyGraphAdapter(
            listOf(
                graphViewAdapterDelegate,
                calendarViewAdapterDelegate,
                faqsDelegate,
                needHelpDelegate,
                bottomImageDeligate
            )
        )
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> GraphManualBuyLayoutBinding
        get() = GraphManualBuyLayoutBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleAction(
            GraphManualBuyFragmentAction.Init
        )
        setUpView()
        observerStates()
    }

    private fun observerStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.manualBuyGraphStateFlow.collect() {
                    it.let { adapter.items = it }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.openWhatsApp.collect {
                    it?.let {
                        requireContext().openWhatsapp(
                            it, getString(R.string.feature_help_string)
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.manualBuyGraphData.collect {
                    it?.let {
                        binding.tvToolBarHeading.text = it.title
                    }
                }
            }
        }
    }

    private fun setUpView() {
        with(binding) {
            rvList.adapter = adapter
            rvList.layoutManager = LinearLayoutManager(this@GraphManualBuyFragment.requireContext())
            rvList.edgeEffectFactory = BaseEdgeEffectFactory()
            ivClose.setDebounceClickListener {
                viewModel.handleAction(
                    GraphManualBuyFragmentAction.OnClickOnBack
                )
                popBackStack()
            }
            ivFqa.setDebounceClickListener {
                viewModel.handleAction(
                    GraphManualBuyFragmentAction.OnClickOnInfoIcon
                )
                (rvList.layoutManager as? LinearLayoutManager)?.smoothScrollToPosition(rvList, null, 3)
            }
        }
    }

}