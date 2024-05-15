package com.jar.app.feature_gold_lease.impl.ui.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseFaqBottomSheetBinding
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseFaqViewModel
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldLeaseFaqBottomSheetFragment : BaseBottomSheetDialogFragment<FragmentGoldLeaseFaqBottomSheetBinding>(){

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private var adapter: GoldLeaseFaqAdapter? = null

    private val viewModelProvider by viewModels<GoldLeaseFaqViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseFaqBottomSheetBinding
        get() = FragmentGoldLeaseFaqBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(shouldShowFullHeight = true, isCancellable = false, isDraggable = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup() {
        observeLiveData()
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        adapter = GoldLeaseFaqAdapter(
            onFaqClicked = {
                analyticsApi.postEvent(
                    GoldLeaseEventKey.Faq.Lease_FAQClicked,
                    mapOf(
                        GoldLeaseEventKey.Faq.faq_type to it.header.orEmpty()
                    )
                )
            }
        )
        binding.rvFaqs.adapter = adapter
        binding.rvFaqs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFaqs.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun getData() {
        viewModel.fetchFaqs()
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.goldLeaseFaqsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            adapter?.submitList(it.leaseFaqSubObjects)
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

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }
}