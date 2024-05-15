package com.jar.app.feature_daily_investment_cancellation.impl.ui.daily_investment_status_screen

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.FeatureFlowData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_daily_investment.api.data.DailyInvestmentApi
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationConstants
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.Back
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.DSCancellation_ErrorStateDSSetupClicked
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.DSCancellation_ErrorStateDSSetupShown
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.Error_Type
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.Failed
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.In_Progress
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.Notify_me
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.Retry
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentStatusScreen.button_type
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class DailyInvestmentStatusFragment : BaseComposeFragment() {
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Inject
    lateinit var dailyInvestmentApi: DailyInvestmentApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<DailyInvestmentStatusFragmentArgs>()

    private var backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            popBackStack()
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, backPressCallback
        )
    }

    @Preview
    @Composable
    override fun RenderScreen() {
        val notifyClick = remember { mutableStateOf(false) }
        Column {
            RenderBaseToolBar(
                modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)),
                onBackClick = {
                    popBackStack()
                    analyticsHandler.postEvent(
                        DSCancellation_ErrorStateDSSetupClicked,
                        mapOf(
                            Error_Type to if (args.status == DailyInvestmentCancellationConstants.FAILURE) Failed else In_Progress,
                            button_type to Back
                        )
                    )
                },
                title = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.daily_saving),
                titleImage = R.drawable.calender
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(JarColors.bgColor)
            ) {
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f))
                    if (args.status == DailyInvestmentCancellationConstants.FAILURE) {
                        InfoBlock(
                            image = R.drawable.triangle_error,
                            heading = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.we_are_facing_technical_issue),
                            subText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.please_try_again_after_some_time)
                        )
                    } else {
                        InfoBlock(
                            image = R.drawable.stopwatch,
                            heading = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.you_are_one_step_away_from_wealth),
                            subText = stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.status_of_your_daily_savings_will_be_updated)
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .weight(2f))
                }

                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Column {
                        if (notifyClick.value) {
                            NotificationBlock {

                            }
                        }

                        JarPrimaryButton(
                            isEnabled = !notifyClick.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                                .padding(start = 16.dp)
                                .padding(end = 16.dp),
                            color = if (notifyClick.value) {
                                colorResource(id = com.jar.app.core_ui.R.color.color_6637E4)
                            } else {
                                JarColors.primaryButtonBgColor
                            },
                            text = if (args.status == DailyInvestmentCancellationConstants.FAILURE)
                                stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.retry)
                            else
                                stringResource(id = com.jar.app.feature_daily_investment_cancellation.shared.R.string.notify_me),
                            isAllCaps = false,
                            onClick = {
                                if (args.status == DailyInvestmentCancellationConstants.FAILURE) {
                                    dailyInvestmentApi.openDailySavingFlow(
                                        fromSettingsFlow = false,
                                        featureFlowData = FeatureFlowData(
                                            fromScreen = requireContext().getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.technical_issue)
                                        ),
                                        popUpToId = R.id.dailyInvestmentStatusFragment
                                    )
                                    analyticsHandler.postEvent(
                                        DSCancellation_ErrorStateDSSetupClicked,
                                        mapOf(
                                            Error_Type to Failed,
                                            button_type to Retry
                                        )
                                    )
                                } else {
                                    notifyClick.value = true
                                    analyticsHandler.postEvent(
                                        DSCancellation_ErrorStateDSSetupClicked,
                                        mapOf(
                                            Error_Type to In_Progress,
                                            button_type to Notify_me
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        if (args.status == DailyInvestmentCancellationConstants.FAILURE) {
            analyticsHandler.postEvent(
                DSCancellation_ErrorStateDSSetupShown,
                mapOf(Error_Type to Failed)
            )
        } else {
            analyticsHandler.postEvent(
                DSCancellation_ErrorStateDSSetupShown,
                mapOf(Error_Type to In_Progress)
            )
        }
    }
}