package com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet

import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.core_ui.extension.runLayoutAnimation
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.dp
import com.jar.app.base.util.writeBitmap
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.view_holder.LoadStateAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.feature_buy_gold_v2.databinding.BottomSheetAuspiciousDatesBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.auspicious_sheet.adapter.AuspiciousDateAdapter
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.ui.AuspiciousDatesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class AuspiciousDatesBottomSheet :
    BaseBottomSheetDialogFragment<BottomSheetAuspiciousDatesBinding>() {

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<AuspiciousDatesViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val auspiciousDateAdapter = AuspiciousDateAdapter()
    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()
    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(20.dp, 6.dp)
    private val hasAnimatedOnce = AtomicBoolean(false)

    override val bottomSheetConfig = DEFAULT_CONFIG

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BottomSheetAuspiciousDatesBinding
        get() = BottomSheetAuspiciousDatesBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup() {
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun setupUI() {
        binding.rvAuspicious.adapter = auspiciousDateAdapter.withLoadStateFooter(
            footer = LoadStateAdapter {
                auspiciousDateAdapter.retry()
            }
        )
        binding.rvAuspicious.layoutManager = LinearLayoutManager(context)
        binding.rvAuspicious.addItemDecoration(spaceItemDecoration)
        binding.rvAuspicious.edgeEffectFactory = baseEdgeEffectFactory
    }

    private fun getData() {
        viewModel.fetchAuspiciousDates()
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                auspiciousDateAdapter.loadStateFlow.collect { loadState ->

                    when (loadState.refresh is LoadState.Loading) {
                        true -> {
                            binding.shimmerPlaceholder.startShimmer()
                        }
                        false -> {
                            val isListEmpty =
                                loadState.refresh is LoadState.NotLoading && auspiciousDateAdapter.itemCount == 0

                            binding.clEmptyPlaceHolder.isVisible = isListEmpty
                            binding.groupTimings.isVisible = !isListEmpty

                            binding.shimmerPlaceholder.isVisible = false
                            binding.shimmerPlaceholder.stopShimmer()

                            if (hasAnimatedOnce.getAndSet(true).not()) {
                                binding.rvAuspicious.runLayoutAnimation(com.jar.app.core_ui.R.anim.layout_animation_fall_down)
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.auspiciousDatedFlow.collect {
                    it?.let {
                        auspiciousDateAdapter.submitData(it)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.btnShareWithFriends.setDebounceClickListener {
            var auspiciousDatesStr = ""
            val auspiciousDateList = auspiciousDateAdapter.snapshot().items
            if (auspiciousDateList.isEmpty()) {
                auspiciousDatesStr = binding.shareImageTitle.text.toString()
            } else {
                auspiciousDateList.forEachIndexed { index, auspiciousDate ->
                    when (index) {
                        0 -> {
                            auspiciousDatesStr = auspiciousDate.date
                        }
                        auspiciousDateList.size-1 -> {
                            auspiciousDatesStr += " and ${auspiciousDate.date}"
                        }
                        else -> {
                            auspiciousDatesStr += ", ${auspiciousDate.date}"
                        }
                    }
                }
            }
            val shareMessage = getCustomStringFormatted(MR.strings.feature_buy_gold_v2_auspicious_share_message, auspiciousDatesStr)
            shareImage(shareMessage)
        }
    }

    private fun shareImage(shareMessage: String) {
        analyticsHandler.postEvent(EventKey.SubhMuhurtDatesShareWithFriends_BuyGoldScreen)
        binding.shareImage.post {
            val bitmap = binding.shareImage.drawToBitmap()
            uiScope.launch(Dispatchers.IO) {
                bitmap.let {
                    val parent =
                        File(requireContext().externalCacheDir, BaseConstants.CACHE_DIR_SHARED)
                    parent.mkdirs()
                    val file = File(parent, "auspicious.png")
                    file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 90)
                    withContext(Dispatchers.Main) {
                        fileUtils.shareImage(
                            requireContext(),
                            file,
                            shareMessage + "\n" + BaseConstants.PLAY_STORE_URL
                        )
                    }
                }
            }
        }
    }
}