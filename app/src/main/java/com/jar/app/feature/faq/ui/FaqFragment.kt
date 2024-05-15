package com.jar.app.feature.faq.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import com.jar.app.core_ui.item_decoration.HeaderItemDecoration
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.databinding.FragmentAppFaqBinding
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.core_ui.faq.FaqAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class FaqFragment : BaseFragment<FragmentAppFaqBinding>(),
    BaseItemDecoration.SectionCallback {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAppFaqBinding
        get() = FragmentAppFaqBinding::inflate

    private val spaceItemDecoration =
        SpaceItemDecoration(16.dp, 4.dp)

    private val headerItemDecoration =
        HeaderItemDecoration(this)

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private var adapter: FaqAdapter? = null

    private val viewModel by viewModels<FaqViewModel> { defaultViewModelProviderFactory }

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarDefault(title = getString(R.string.faqs)))))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        getData()
    }

    private fun setupUI() {
        binding.rvFaq.layoutManager = LinearLayoutManager(context)
        adapter = FaqAdapter()
        binding.rvFaq.addItemDecorationIfNoneAdded(spaceItemDecoration, headerItemDecoration)
        binding.rvFaq.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvFaq.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.faqLiveData.observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                binding.rvFaq.isVisible = true
                adapter?.submitList(it)
            }
        )
    }

    private fun getData() {
        viewModel.fetchFaqData()
    }

    override fun isItemDecorationSection(position: Int): Boolean {
        val currentList = adapter?.currentList
        return if (!currentList.isNullOrEmpty()) {
            if (position == 0)
                true
            else {
                val prev = currentList.getOrNull(position - 1)
                val current = currentList.getOrNull(position)
                prev?.type != current?.type
            }
        } else false
    }

    override fun getItemDecorationLayoutRes(position: Int): Int {
        return com.jar.app.core_ui.R.layout.core_ui_item_faq_header
    }

    override fun bindItemDecorationData(view: View, position: Int) {
        view.findViewById<AppCompatTextView>(R.id.tvHeader)?.text =
            adapter?.currentList?.getOrNull(position)?.type
    }
}