package com.jar.app.feature.notification_list.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.R
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.view_holder.LoadStateAdapter
import com.jar.app.databinding.FragmentNotificationListBinding
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.notification_list.NotificationEvents
import com.jar.app.feature.notification_list.domain.model.NotificationMetaData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
internal class NotificationListFragment : BaseFragment<FragmentNotificationListBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModelProvider by viewModels<NotificationListFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }

    private var adapter: NotificationAdapter? = null
    private var baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()
    private val hasAnimatedOnce = AtomicBoolean(false)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNotificationListBinding
        get() = FragmentNotificationListBinding::inflate

    private var startTime: Long = 0
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        init()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun init() {
        startTime = System.currentTimeMillis()
        analyticsHandler.postEvent(NotificationEvents.Shown_NotificationScreen)
        adapter = NotificationAdapter {
            it.deepLink?.let { deeplink ->
                prefs.setUserLifeCycleForMandate(EventKey.UserLifecycles.InAppNotifications)
                if (deeplink.contains(BaseConstants.BASE_EXTERNAL_DEEPLINK))
                    EventBus.getDefault().post(
                        HandleDeepLinkEvent(
                            deepLink = deeplink,
                            fromScreen = EventKey.UserLifecycles.InAppNotifications
                        )
                    )
                else
                    openUrlInChromeTab(deeplink, getString(R.string.jar), false)
            }
            val count = homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.orZero()
            val category = it.category.orEmpty()
            val callToAction = it.callToAction.orEmpty()
            analyticsHandler.postEvent(
                NotificationEvents.Clicked_Notification_NotificationScreen,
                mapOf(
                    NotificationEvents.State to if (count == 0L) NotificationEvents.NotificationState.Read else NotificationEvents.NotificationState.Read,
                    NotificationEvents.Count to category,
                    NotificationEvents.Trigger to callToAction
                )
            )
        }
        binding.rvNotifications.adapter = adapter?.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter {
                adapter?.retry()
            },
            footer = LoadStateAdapter {
                adapter?.retry()
            }
        )

        homeViewModel.fetchUserMetaData()
        fetchNotifications()
    }

    private fun setupUI() {
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.edgeEffectFactory = baseEdgeEffectFactory
        val decorator = DividerItemDecoration(
            requireContext(), LinearLayoutManager.VERTICAL
        )
        ContextCompat.getDrawable(
            requireContext(),
            com.jar.app.core_ui.R.drawable.core_ui_line_separator
        )?.let {
            decorator.setDrawable(it)
        }
        binding.rvNotifications.addItemDecorationIfNoneAdded(decorator)
    }

    private fun setupListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter?.loadStateFlow?.collect { loadState ->
                    when (loadState.refresh is LoadState.Loading) {
                        true -> {
                            binding.shimmerPlaceholder.isVisible = true
                            binding.shimmerPlaceholder.startShimmer()
                        }

                        false -> {
                            val isListEmpty =
                                loadState.refresh is LoadState.NotLoading && adapter?.itemCount == 0

                            binding.clEmptyNotificationPlaceholder.isVisible = isListEmpty
                            binding.rvNotifications.isVisible = !isListEmpty
                            binding.shimmerPlaceholder.isVisible = false
                            binding.shimmerPlaceholder.stopShimmer()

                            if (hasAnimatedOnce.getAndSet(true).not()) {
                                binding.rvNotifications.runLayoutAnimation(com.jar.app.core_ui.R.anim.layout_animation_fall_down)
                            }
                        }
                    }
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            adapter?.refresh()
            homeViewModel.fetchUserMetaData()
        }

        binding.btnBack.setOnClickListener {
            val timeSpent = (startTime - System.currentTimeMillis()) / 1000 % 60
            analyticsHandler.postEvent(
                NotificationEvents.Clicked_BackButton_NotificationScreen,
                mapOf(NotificationEvents.TimeSpent to timeSpent)
            )
            popBackStack()
        }

        binding.rvNotifications.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.swipeRefresh.isEnabled =
                    (binding.rvNotifications.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition() == 0
            }
        })
    }

    private fun observeLiveData() {
        homeViewModel.userMetaLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.tvNotificationCount.isVisible = false
            },
            onSuccess = {
                it?.notificationCount?.let { count ->
                    binding.tvNotificationCount.isVisible = count != 0L
                    binding.tvNotificationCount.text = if (count < 100) getString(
                        R.string.x_new, count
                    ) else getString(R.string.x_plus_new, count)
                }
                homeViewModel.updateLocalNotificationMetaData(NotificationMetaData(it?.notificationCount))
            },
            onError = {
                binding.tvNotificationCount.isVisible = false
            }
        )
    }

    private fun fetchNotifications() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.pagingData
                    .collect {
                        binding.swipeRefresh.isRefreshing = false
                        adapter?.submitData(it)
                    }
            }
        }
    }

    override fun onDestroyView() {
        adapter = null
        super.onDestroyView()
    }

}