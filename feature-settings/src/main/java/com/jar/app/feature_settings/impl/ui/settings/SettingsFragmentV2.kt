package com.jar.app.feature_settings.impl.ui.settings

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshGoalBasedSavingEvent
import com.jar.app.base.data.event.RefreshGoldSipEvent
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openUrlInChromeTab
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.api.CoreUiApi
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationConstants
import com.jar.app.feature_gold_sip.api.GoldSipApi
import com.jar.app.feature_gold_sip.shared.domain.event.GoldSipUpdateEvent
import com.jar.app.feature_round_off.api.RoundOffApi
import com.jar.app.feature_savings_common.shared.domain.model.SubscriptionStatus
import com.jar.app.feature_savings_common.shared.domain.model.UserSavingType
import com.jar.app.feature_settings.databinding.FragmentSettingsV2Binding
import com.jar.app.feature_settings.domain.SettingsEventKey
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class SettingsFragmentV2 : BaseFragment<FragmentSettingsV2Binding>() {

    companion object {
        const val FROM_SCREEN = "Settings"
        fun newInstance() = SettingsFragmentV2()
    }

    @Inject
    lateinit var roundOffApi: RoundOffApi

    private val coreUiApi by lazy {
        coreUiApiRef.get()
    }

    @Inject
    lateinit var coreUiApiRef: Lazy<CoreUiApi>

    private var adapter: SettingsAdapter? = null

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val viewModelProvider: SettingsV2ViewModelAndroid by viewModels()

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var dailyInvestmentCancellationSettingsV2Api: com.jar.app.feature_daily_investment_cancellation.api.DailyInvestmentCancellationSettingsV2Api

    @Inject
    lateinit var goldSipApi: GoldSipApi

    var appStartTime: Long = 0L

    private val convertToString: (stringRes: StringResource, args: Array<Any>) -> String? =
        { stringRes, args ->
            if (args.isEmpty())
                getCustomString(stringRes)
            else
                getCustomStringFormatted(stringRes, *args)
        }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsV2Binding
        get() = FragmentSettingsV2Binding::inflate

    override fun setupAppBar() {}

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel.observeFlows(convertToString)
        viewModel.getData()
    }

    private fun setupUI() {
        binding.rvSettings.layoutManager = LinearLayoutManager(context)
        adapter = SettingsAdapter {
            handleSettingClick(it.position)
        }
        binding.rvSettings.adapter = adapter
        binding.rvSettings.edgeEffectFactory = baseEdgeEffectFactory
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.errorFlow.collectLatest { errorMessage ->
                    errorMessage.snackBar(binding.root)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.settingListLiveData.collectLatest {
                    if (it.isNotEmpty()) {
                        binding.shimmerPlaceholder.stopShimmer()
                        binding.shimmerPlaceholder.isVisible = false
                        binding.clContent.isVisible = true
                        adapter?.submitList(it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun handleSettingClick(position: Int) {
        prefs.setUserLifeCycleForMandate(EventKey.UserLifecycles.Settings)
        when (position) {
            BaseConstants.SettingsV2CardPosition.PAYMENT_METHODS -> {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_PaymentMethods_SettingsScreen)
                val uri = "android-app://com.jar.app/paymentMethods"
                navigateTo(uri)
            }

            BaseConstants.SettingsV2CardPosition.DAILY_SAVINGS -> {
                when (UserSavingType.fromString(viewModel.userGoalBasedSavingDetails?.dailySavingsType)) {
                    UserSavingType.DAILY_SAVINGS -> {
                        val currentStatus =
                            viewModel.userDailySavingDetails?.subscriptionStatus ?: "Disabled"
                        analyticsHandler.postEvent(
                            SettingsEventKey.Clicked_DailySavings_SettingsScreen,
                            mapOf(SettingsEventKey.CurrentStatus to currentStatus)
                        )

                        when (viewModel.dailySavingDetailsLiveData.value?.data?.data?.subscriptionStatus) {
                            SubscriptionStatus.PENDING.name -> {
                                val dailyInvestmentDisabledFragmentUri =
                                    Uri.parse("android-app://com.jar.app/dailyInvestmentStatusFragment/${DailyInvestmentCancellationConstants.PENDING}")
                                findNavController().navigate(dailyInvestmentDisabledFragmentUri)
                            }

                            SubscriptionStatus.FAILURE.name -> {
                                val dailyInvestmentDisabledFragmentUri =
                                    Uri.parse("android-app://com.jar.app/dailyInvestmentStatusFragment/${DailyInvestmentCancellationConstants.FAILURE}")
                                findNavController().navigate(dailyInvestmentDisabledFragmentUri)
                            }

                            else -> {
                                val dailySavingRedirectionData =
                                    viewModel.dailyInvestmentCancellationV2DetailsLiveData.value?.data?.data
                                if (dailySavingRedirectionData?.version == BaseConstants.CancellationFlowVersion.v1) {
                                    dailyInvestmentApi.openDailySavingFlow(
                                        fromSettingsFlow = true,
                                        featureFlowData = FeatureFlowData(
                                            fromScreen = BaseConstants.ScreenFlowType.SETTINGS_SCREEN.name
                                        )
                                    )
                                } else {
                                    if (dailySavingRedirectionData?.experiment == true) {
                                        if (viewModel.userDailySavingDetails?.enabled == true) {
                                            dailyInvestmentCancellationSettingsV2Api.openDailyInvestmentCancellationSettingsV2Flow()
                                        } else {
                                            if (dailySavingRedirectionData.isFirstSetup == true) {
                                                dailyInvestmentApi.openDailySavingOnboardingStories()
                                            } else {
                                                dailyInvestmentApi.openDailySavingFlow(
                                                    fromSettingsFlow = false,
                                                    featureFlowData = FeatureFlowData(
                                                        fromScreen = BaseConstants.ScreenFlowType.SETTINGS_SCREEN.name
                                                    )
                                                )
                                            }
                                        }
                                    } else {
                                        dailyInvestmentApi.openDailySavingFlow(
                                            fromSettingsFlow = false,
                                            featureFlowData = FeatureFlowData(
                                                fromScreen = BaseConstants.ScreenFlowType.SETTINGS_SCREEN.name
                                            )
                                        )
                                    }
                                }
                            }
                        }

                    }

                    com.jar.app.feature_savings_common.shared.domain.model.UserSavingType.SAVINGS_GOAL -> {
                        EventBus.getDefault().post(
                            HandleDeepLinkEvent(
                                viewModel.userGoalBasedSavingDetails?.messageCta?.deeplink ?: ""
                            )
                        )
                    }
                }
            }

            BaseConstants.SettingsV2CardPosition.ROUND_OFF -> {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_RoundOff_SettingsScreen)
                roundOffApi.openRoundOffFlow()
            }

            BaseConstants.SettingsV2CardPosition.GOLD_SIP -> {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_GoldSip_SettingsScreen)
                goldSipApi.setupGoldSip()
            }

            BaseConstants.SettingsV2CardPosition.NOTIFICATION_SETTINGS -> {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Notifications_SettingsTab)
                val uri = "android-app://com.jar.app/notificationSettings"
                navigateTo(uri)
            }

            BaseConstants.SettingsV2CardPosition.LANGUAGE -> {
                analyticsHandler.postEvent(
                    SettingsEventKey.Clicked_Language_SettingsScreen, mapOf(
                        SettingsEventKey.currentLanguage to prefs.getCurrentLanguageName()
                    )
                )
                val uri = "android-app://com.jar.app/language/$FROM_SCREEN"
                navigateTo(uri)
            }

            BaseConstants.SettingsV2CardPosition.BATTERY_OPTIMIZATION -> {
                analyticsHandler.postEvent(SettingsEventKey.Clicked_BatteryOptimization_SettingsScreen)
                val uri = "android-app://com.jar.app/batteryOptimization"
                navigateTo(uri)
            }

            BaseConstants.SettingsV2CardPosition.JAR_SECURITY_SHIELD -> {
                analyticsHandler.postEvent(
                    SettingsEventKey.Clicked_SecurityShield_SettingsScreen, mapOf(
                        SettingsEventKey.currentState to if (prefs.isJarShieldEnabled()) getCustomString(
                            SettingsMR.strings.feature_settings_on
                        ) else getCustomString(
                            SettingsMR.strings.feature_settings_off
                        )
                    )
                )
                val uri = "android-app://com.jar.app/jarSecurityShield"
                navigateTo(uri)
            }

            BaseConstants.SettingsV2CardPosition.TERMS_AND_CONDITIONS -> {
                openUrlInChromeTab(
                    remoteConfigApi.getTermsAndConditionsUrl(),
                    getCustomString(SettingsMR.strings.feature_settings_terms_and_conditions),
                    true
                )
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Terms_SettingsScreen)
            }

            BaseConstants.SettingsV2CardPosition.PRIVACY_POLICY -> {
                openUrlInChromeTab(
                    remoteConfigApi.getPrivacyPolicyUrl(),
                    getCustomString(SettingsMR.strings.feature_settings_privacy_policy),
                    true
                )
                analyticsHandler.postEvent(SettingsEventKey.Clicked_Privacy_SettingsScreen)
            }

            BaseConstants.SettingsV2CardPosition.TEST_ACTIVITY -> {
                coreUiApi.openTestComponentsFragment()
            }

            BaseConstants.SettingsV2CardPosition.BASE_API_URL -> {
                navigateTo("android-app://com.jar.app/baseUrlSettingsBottomSheet")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        appStartTime = System.currentTimeMillis()
        analyticsHandler.postEvent(SettingsEventKey.Shown_SettingsScreen_Account)
    }

    override fun onPause() {
        super.onPause()
        analyticsHandler.postEvent(
            SettingsEventKey.Exit_SettingsTab_Account, mapOf(
                SettingsEventKey.timeSpent to System.currentTimeMillis() - appStartTime
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshRoundOffState(refreshRoundOffStateEvent: com.jar.app.feature_round_off.shared.domain.event.RefreshRoundOffStateEvent) {
        viewModel.fetchRoundOffDetails()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshDailySavingEvent(refreshDailySavingEvent: RefreshDailySavingEvent) {
        viewModel.fetchDailySavingDetails()
        viewModel.fetchRoundOffDetails()
        viewModel.fetchDailyInvestmentCancellationV2RedirectionDetails()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoldSipUpdateEvent(goldSipUpdateEvent: GoldSipUpdateEvent) {
        viewModel.fetchGoldSipDetails()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGoldSipEvent(refreshGoldSipEvent: RefreshGoldSipEvent) {
        viewModel.fetchGoldSipDetails()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGoalBasedSavingDetails(refreshGoalBasedSavingEvent: RefreshGoalBasedSavingEvent) {
        viewModel.fetchGbSSetting()
    }
}