package com.jar.app.feature_daily_investment.impl.ui.setup_daily_savings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment.api.domain.event.SetupMandateEvent
import com.jar.app.feature_daily_investment.databinding.FeatureDailysavingsFragmentSetupBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Clicked_SetupNow_DailySavingsScreen
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.Shown_DailySavingsScreen
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.currentState
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.disabled
import com.jar.app.feature_mandate_payment.impl.util.MandatePaymentEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
internal class SetupDailySavingsFragment :
    BaseFragment<FeatureDailysavingsFragmentSetupBinding>() {

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailysavingsFragmentSetupBinding
        get() = FeatureDailysavingsFragmentSetupBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefsApi: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            Shown_DailySavingsScreen,
            mapOf(currentState to disabled)
        )
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_daily_savings)
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_daily_investment_ic_daily_saving_tab)
        binding.toolbar.separator.isVisible = true
        OverScrollDecoratorHelper.setUpOverScroll(binding.root)
    }

    private fun setupListeners() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
        binding.btnAction.setDebounceClickListener {
            analyticsHandler.postEvent(Clicked_SetupNow_DailySavingsScreen)
            navigateTo(
                SetupDailySavingsFragmentDirections.actionSetupDailySavingsFragmentToSetupDailyInvestmentFragment(
                    encodeUrl(serializer.encodeToString(FeatureFlowData(fromScreen = "setupDailySavings"))),
                ),
                popUpTo = R.id.setupDailySavingsFragment,
                inclusive = true
            )
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSetupMandateEvent(setupMandateEvent: SetupMandateEvent) {
        setupMandateEvent.newDailySavingAmount?.let {
            dailyInvestmentApi.updateDailySavingAndSetupItsAutopay(
                mandateAmount = it,
                source = MandatePaymentEventKey.FeatureFlows.SetupDailySaving,
                authWorkflowType = com.jar.app.feature_mandate_payments_common.shared.domain.model.initiate_mandate.MandateWorkflowType.PENNY_DROP,
                newDailySavingAmount = it,
                popUpToId = R.id.setupDailySavingsFragment,
                userLifecycle = prefsApi.getUserLifeCycleForMandate()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}