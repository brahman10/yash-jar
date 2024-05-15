package com.jar.app.feature_daily_investment_cancellation.impl.ui.post_cancellation

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.utils.generateSpannedFromHtmlString
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.NotificationBlock
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.PostCancellationButtonBlock
import com.jar.app.feature_daily_investment_cancellation.impl.ui.component.PostCancellationTextBlock
import com.jar.app.feature_daily_investment_cancellation.impl.ui.daily_investment_status_screen.ProgressRedirectionFragmentArgs
import com.jar.app.feature_daily_investment_cancellation.impl.ui.intro_screen.DailyInvestmentSettingsV2ViewModel
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEventKey.Button_type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationKey
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStopKey.type
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class PostCancellationFragment : BaseComposeFragment() {

    private val viewModel by viewModels<PostCancellationFragmentViewModel> { defaultViewModelProviderFactory }
    private val dailyCancellationFragmentViewModel by viewModels<DailyInvestmentSettingsV2ViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        observeApiResponse()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    private fun getData() {
        dailyCancellationFragmentViewModel.fetchSettingsFragmentDataFlow(DailyInvestmentCancellationEnum.POST_CANCELLATION.name)
        viewModel.fetchCancellationFragmentDataFlow()
        viewModel.navigateFromSaveWeeklyFlow()
    }

    @Preview
    @Composable
    override fun RenderScreen() {
        val cancellationFragmentDetails by viewModel.cancellationFragmentFlow.collectAsState(initial = null)
        val userGoldDetailsLDFlow by viewModel.userGoldDetailsLDFlow.collectAsState(initial = null)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
        ) {
            cancellationFragmentDetails?.data?.data?.headerText?.let {
                NotificationBlock(tittle = it)
            }
            cancellationFragmentDetails?.data?.data?.statisticsText?.let {
                PostCancellationTextBlock(
                    generateSpannedFromHtmlString(
                        cancellationFragmentDetails?.data?.data?.buyGoldText,
                        true
                    ).toAnnotatedString(),
                    it
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            cancellationFragmentDetails?.data?.let {
                PostCancellationButtonBlock(
                    { saveWeeklyClicked(userGoldDetailsLDFlow?.data?.data?.enabled == true) },
                    { buyGoldClicked() },
                    { goHomeClicked() },
                    cancellationScreenData = it.data
                )
            }
        }
    }

    private fun saveWeeklyClicked(userGoldSipEnabled: Boolean) {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.DSCancellation_StopDSSuccessScreenClicked,
            mapOf(
                Button_type to DailyInvestmentStatusScreen.Save_Weekly,
                type to dailyCancellationFragmentViewModel.pauseState

            )
        )
        navigateForWeeklySavings(userGoldSipEnabled)
    }

    private fun buyGoldClicked() {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.DSCancellation_StopDSSuccessScreenClicked,
            mapOf(
                Button_type to DailyInvestmentStatusScreen.Buy_Gold,
                type to dailyCancellationFragmentViewModel.pauseState
            )
        )
        EventBus.getDefault()
            .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.BUY_GOLD + "/" + BaseConstants.BuyGoldFlowContext.BUY_GOLD))
    }

    private fun goHomeClicked() {
        analyticsHandler.postEvent(
            DailyInvestmentCancellationEventKey.DSCancellation_StopDSSuccessScreenClicked,
            mapOf(
                Button_type to DailyInvestmentStatusScreen.Go_To_Home,
                type to dailyCancellationFragmentViewModel.pauseState
            )
        )
        EventBus.getDefault().post(
            GoToHomeEvent(
                PostCancellationFragment::class.java.name,
                BaseConstants.HomeBottomNavigationScreen.HOME
            )
        )
    }



    private fun observeApiResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.cancellationFragmentFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userGoldDetailsLDFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                dailyCancellationFragmentViewModel.settingsScreenDataFlow.collectUnwrapped(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                    },
                    onError = { _, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popBackStack(id = R.id.dailyInvestmentSettingsV2Fragment, inclusive = true)
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    private fun navigateForWeeklySavings(userGoldSipEnabled: Boolean) {
        if (userGoldSipEnabled) {
            navigateTo(
                uri = BaseConstants.InternalDeepLinks.GOLD_SIP_DETAILS,
                popUpTo = R.id.postCancellationFragment,
                inclusive = false
            )
        } else {
            EventBus.getDefault()
                .post(HandleDeepLinkEvent(BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.GOLD_SIP_TYPE_SELECTION + "/WEEKLY_SIP"))

            popBackStack(
                id = R.id.postCancellationFragment,
                inclusive = false
            )
        }
    }
}
