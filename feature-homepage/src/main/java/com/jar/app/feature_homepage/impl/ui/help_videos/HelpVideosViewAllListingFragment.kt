package com.jar.app.feature_homepage.impl.ui.help_videos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_base.domain.model.card_library.StaticInfoData
import com.jar.app.core_base.domain.model.card_library.StaticInfoType
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.api.event.HandleKnowMoreDeepLinkEvent
import com.jar.app.feature_homepage.databinding.FeatureHomepageFragmentHelpVideosViewAllBinding
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class HelpVideosViewAllListingFragment :
    BaseFragment<FeatureHomepageFragmentHelpVideosViewAllBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<HelpVideosViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureHomepageFragmentHelpVideosViewAllBinding
        get() = FeatureHomepageFragmentHelpVideosViewAllBinding::inflate

    private var adapter: HelpVideosRecyclerAdapter? = null
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
        binding.toolbar.separator.isVisible = true
        binding.toolbar.tvTitle.text = getString(R.string.help_and_support)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        fetchData()
        setClickListener()
    }

    private fun fetchData() {
        viewModel.fetchHelpVideos()
    }

    private fun setupUI() {
        binding.rvHelpVideos.layoutManager = LinearLayoutManager(requireContext())
        adapter = HelpVideosRecyclerAdapter(false) {
            analyticsHandler.postEvent(
                EventKey.Shown_HelpVideoList_Homefeed,
                mapOf(
                    EventKey.Action to "cardClicked",
                    EventKey.Data to it.link
                )
            )
            EventBus.getDefault().post(
                HandleKnowMoreDeepLinkEvent(
                    StaticInfoData(
                        StaticInfoType.CUSTOM_WEB_VIEW.name,
                        it.link,
                        null
                    )
                )
            )
        }
        binding.rvHelpVideos.adapter = adapter
        binding.rvHelpVideos.addItemDecoration(
            SpaceItemDecoration(
                0.dp,
                4.dp,
                RecyclerView.VERTICAL,
                true
            )
        )
        analyticsHandler.postEvent(EventKey.Shown_HelpVideoList_Homefeed)
    }

    private fun setClickListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                EventKey.Shown_HelpVideoList_Homefeed,
                mapOf(EventKey.Action to EventKey.BACK_BUTTON_PRESSED)
            )
            popBackStack()
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.helpVideosLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        adapter?.submitList(it.helpVideoData)
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

    }
}