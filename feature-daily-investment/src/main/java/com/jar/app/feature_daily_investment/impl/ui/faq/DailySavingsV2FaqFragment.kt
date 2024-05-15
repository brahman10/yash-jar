package com.jar.app.feature_daily_investment.impl.ui.faq

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.core_ui.databinding.CoreUiFragmentGenericFaqBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_daily_investment.R
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class DailySavingsV2FaqFragment : BaseFragment<CoreUiFragmentGenericFaqBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiFragmentGenericFaqBinding
        get() = CoreUiFragmentGenericFaqBinding::inflate

    private val viewModel by viewModels<DailySavingsV2FaqViewModel> { defaultViewModelProviderFactory }

    private var adapter: DailySavingsV2ExpandableFaqAdapter? = null

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.fetchFaQData()
        super.onCreate(savedInstanceState)
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        setupUI()
        setupListener()

        val dividerDecorator =
            object : DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL) {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val current = adapter?.currentList?.getOrNull(position)?.question
                    val prev = adapter?.currentList?.getOrNull(position + 1)?.question
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
    }

    private fun setupUI() {
        binding.tvTitle.text = requireContext().getString(R.string.daily_investment_faq_header)

        adapter = DailySavingsV2ExpandableFaqAdapter {
            updateFaqList(it)
        }
        binding.rvFaq.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFaq.adapter = adapter

    }

    private fun setupListener() {
        binding.btnClose.setDebounceClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeLiveData() {
        viewModel.faqListLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                dismissProgressBar()
                adapter?.submitList(it)
            },
            onError = {  _,_ ->
                dismissProgressBar()
            },
            onLoading = {
                showProgressBar()
            }
        )
    }

    private fun updateFaqList(position: Int) {
        adapter?.let {
            viewModel.updateFaqList(it.currentList, position)
        }
    }
}