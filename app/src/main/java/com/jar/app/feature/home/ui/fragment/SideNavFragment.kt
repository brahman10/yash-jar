package com.jar.app.feature.home.ui.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.jar.app.BuildConfig
import com.jar.app.HomeNavigationDirections
import com.jar.app.R
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateSideNavProfilePicEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.domain.model.IconBackgroundTextComponent
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.databinding.NavDrawerBinding
import com.jar.app.feature_homepage.shared.domain.model.hamburger.HamburgerItem
import com.jar.app.feature.home.ui.activity.HomeActivityViewModel
import com.jar.app.feature.home.util.HomeConstants
import com.jar.app.feature.notification_list.NotificationEvents
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SideNavFragment : BaseFragment<NavDrawerBinding>() {

    companion object{
        const val SCREEN_NAME = "hamburger"
    }

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> NavDrawerBinding
        get() = NavDrawerBinding::inflate

    private val homeViewModel by activityViewModels<HomeActivityViewModel> { defaultViewModelProviderFactory }
    private val navDrawerViewModelProvider by activityViewModels<SideNavDrawerViewModelAndroid> { defaultViewModelProviderFactory }

    private val navDrawerViewModel by lazy {
        navDrawerViewModelProvider.getInstance()
    }

    private var adapter: HamburgerMenuItemAdapter? = null

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(6.dp, 6.dp)

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private var user: User? = null

    override fun setupAppBar() {}

    override fun setup(savedInstanceState: Bundle?) {
        if (prefs.isLoggedIn())
            navDrawerViewModel.fetchHamburgerMenuItems()
        observeData()
        handleClicks()
        setupUI()
        observeNotificationMetaLiveData(WeakReference(binding.root))
    }

    private fun setupUI() {
        loadImage()
        adapter = HamburgerMenuItemAdapter { position: Int, hamburgerItem: HamburgerItem ->
            analyticsHandler.postEvent(
                HomeConstants.AnalyticsKeys.Clicked_Tile_HamburgerMenu,
                mapOf(
                    HomeConstants.AnalyticsKeys.tileFunction to hamburgerItem.text.toString(),
                    HomeConstants.AnalyticsKeys.Type to hamburgerItem.itemType.orEmpty(),
                    HomeConstants.AnalyticsKeys.position to position.toString(),
                    HomeConstants.AnalyticsKeys.newNotification to HomeConstants.AnalyticsKeys.no,
                )
            )

            hamburgerItem.type?.let {
                if (it == PrimaryActionType.IN_APP_BROWSER.name)
                    openUrlInChromeTab(
                        url = hamburgerItem.deepLink,
                        title = "",
                        showToolbar = true
                    )
                else
                    fireNavigationEvent(hamburgerItem.deepLink)
            } ?: run {
                fireNavigationEvent(hamburgerItem.deepLink)
            }
            prefs.setUserLifeCycleForMandate(EventKey.UserLifecycles.Hamburger)
            homeViewModel.closeNavDrawer()
        }

        binding.updateAvailable.setDebounceClickListener {
            homeViewModel.updateApp()
        }
        binding.appVersion.text = getString(
            R.string.version_name_number,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        binding.navDrawerMenu.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.navDrawerMenu.adapter = adapter
        binding.navDrawerMenu.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun handleClicks() {
        binding.updateAvailable.setDebounceClickListener {
            analyticsHandler.postEvent(HomeConstants.AnalyticsKeys.Clicked_UpdateAvailable)
            homeViewModel.updateApp()
        }
        binding.logout.setDebounceClickListener {
            analyticsHandler.postEvent(HomeConstants.AnalyticsKeys.Clicked_Logout_HamburgerMenu)
            navigateTo(HomeNavigationDirections.actionToLogoutConfirmation())
        }
        binding.profileImage.setDebounceClickListener {
            analyticsHandler.postEvent(HomeConstants.AnalyticsKeys.Clicked_Avatar_HamburgerMenu)
            navigateToProfile()
        }
        binding.viewProfile.setDebounceClickListener {
            analyticsHandler.postEvent(HomeConstants.AnalyticsKeys.Clicked_ViewProfile_HamburgerMenu)
            navigateToProfile()
        }
        binding.ivNotification.setDebounceClickListener {
            navigateTo(
                HomeNavigationDirections.actionToNotificationListFragment(), navOptions = getBottomNavOptions()
            )
            val count = homeViewModel.userMetaLiveData.value?.data?.data?.notificationCount.orZero()
            analyticsHandler.postEvent(
                NotificationEvents.ClickedNotifications_Homescreen, mapOf(
                    NotificationEvents.State to if (count == 0L) NotificationEvents.NotificationState.Read else NotificationEvents.NotificationState.Read,
                    NotificationEvents.Count to count,
                    NotificationEvents.FromScreen to SCREEN_NAME
                )
            )
        }
    }

    private fun navigateToProfile() {
        homeViewModel.closeNavDrawer()
        fireNavigationEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.PROFILE)
    }

    private fun fireNavigationEvent(uri: String) {
        EventBus.getDefault().post(
            HandleDeepLinkEvent(
                deepLink = uri,
                fromScreen = BaseConstants.FROM_HAMBURGER_MENU
            )
        )
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                navDrawerViewModel.hamburgerMenuFlow.collect(
                    onSuccess = {
                        it?.hamburgerItems?.hamburgerHeader?.let { hamburgerHeader ->
                            setHamburgerHeaderData(hamburgerHeader)
                        }
                        it?.hamburgerItems?.let {
                            adapter?.submitList(it.hamburgerItems?.filter { item -> item.enabled })
                        }
                    }
                )
            }
        }
        homeViewModel.appUpdateAvailable.observe(viewLifecycleOwner) {
            binding.updateAvailable.visibility = View.VISIBLE
        }
        userLiveData.observe(viewLifecycleOwner) {
            user = it
            it?.let {
                binding.tvTitle.text = it.getFullName()
            }
            loadImage()
        }
    }

    private fun setHamburgerHeaderData(hamburgerHeader: IconBackgroundTextComponent) {
        binding.cvHamburgerHeader.isVisible = hamburgerHeader.text.isNullOrEmpty().not()
        hamburgerHeader.bgColor?.takeIf { it.isNotEmpty() }?.let { bgColor ->
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 4f.dp
                setColor(Color.parseColor(bgColor))
            }
            binding.llHamburgerHeader.background = bgDrawable
        }
        binding.tvHamburgerHeader.setHtmlText(hamburgerHeader.text.orEmpty())
        binding.ivHamburgerHeader.isVisible = hamburgerHeader.iconUrl.isNullOrEmpty().not()
        hamburgerHeader.iconUrl?.takeIf { it.isNotEmpty() }?.let { iconUrl ->
            Glide.with(requireContext()).load(iconUrl).into(binding.ivHamburgerHeader)
        }
    }

    private fun observeNotificationMetaLiveData(viewRef: WeakReference<View>) {
        if (remoteConfigApi.isShowInAppStory()) {
            homeViewModel.userMetaLiveData.observeNetworkResponse(this, viewRef, onSuccess = {
                if ( binding.ivNotification.isVisible && it?.notificationCount.orZero() > 0) {
                    binding.tvNotificationDot.isVisible = true
                    binding.tvNotificationDot.text = it?.notificationCount.toString()
                } else binding.tvNotificationDot.isVisible = false
            })
        } else {
            binding.notificationItem.isVisible = false
        }
    }

    private fun loadImage() {
        binding.profileImage.setUserImage(
            user,
            borderWidth = 4f * resources.displayMetrics.density,
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateSideNavProfilePicEvent(updateSideNavProfilePicEvent: UpdateSideNavProfilePicEvent) {
        loadImage()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}