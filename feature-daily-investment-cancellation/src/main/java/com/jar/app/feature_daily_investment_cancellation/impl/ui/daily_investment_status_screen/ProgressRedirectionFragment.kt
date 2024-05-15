package com.jar.app.feature_daily_investment_cancellation.impl.ui.daily_investment_status_screen

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.navArgs
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.getFormattedDate
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.utils.HorizontalProgressBar
import com.jar.app.feature_daily_investment_cancellation.impl.util.DailyInvestmentCancellationEnum
import com.jar.app.feature_daily_investment_tempering.R
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
internal class ProgressRedirectionFragment : BaseComposeFragment() {

    private val args by navArgs<ProgressRedirectionFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    companion object {
        private const val SCREEN_TIMER = 2000L
    }

    @Preview
    @Composable
    override fun RenderScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(JarColors.bgColor)
        ) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                InfoBlock(
                    image = returnImageResourceForProgressScreen(args),
                    heading =
                    if ((args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name && args.progressScreenData.pauseDailySaving) || (args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name && args.progressScreenData.stopDailySaving))
                        headingText(args.progressScreenData.numberOfDays.toInt())
                    else if (args.progressScreenData.version == DailyInvestmentCancellationEnum.V4.name && args.progressScreenData.stopDailySaving)
                        headingText(args.progressScreenData.numberOfDays.toInt(), args.progressScreenData.version)
                    else if (args.progressScreenData.stopDailySaving)
                        args.progressScreenData.heading
                    else
                        args.progressScreenData.heading,
                    subText =
                    if ((args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name && args.progressScreenData.pauseDailySaving) || (args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name && args.progressScreenData.stopDailySaving))
                        subHeadingText(args.progressScreenData.numberOfDays.toInt())
                    else if (args.progressScreenData.version == DailyInvestmentCancellationEnum.V4.name && args.progressScreenData.stopDailySaving)
                        subHeadingText(args.progressScreenData.numberOfDays.toInt(), args.progressScreenData.version)
                    else if (args.progressScreenData.stopDailySaving)
                        args.progressScreenData.subHeading
                    else
                        args.progressScreenData.subHeading,
                    highlightedText = if (args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name) "" else args.progressScreenData.highlightedText
                )
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .align(Alignment.TopStart)
            ) {
                Column {
                    HorizontalProgressBar(SCREEN_TIMER.toInt())
                }
            }
        }
    }

    private fun returnImageResourceForProgressScreen(args: ProgressRedirectionFragmentArgs): Int {
        return (if (args.progressScreenData.stopDailySaving && args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name) {
            com.jar.app.core_ui.R.drawable.core_ui_stop_watch_stop
        } else if (args.progressScreenData.stopDailySaving && args.progressScreenData.version == DailyInvestmentCancellationEnum.V4.name) {
            R.drawable.stop_watch_stop
        } else if (args.progressScreenData.resumeDailySaving) {
            R.drawable.stop_watch_resume
        } else if (args.progressScreenData.continueDailySaving) {
            R.drawable.stop_watch_ok
        } else if (args.progressScreenData.pauseDailySaving) {
            com.jar.app.core_ui.R.drawable.core_ui_stop_watch_stop
        } else if (args.progressScreenData.stopDailySaving) {
            R.drawable.stop_watch_stop
        } else {
            R.drawable.stop_watch_stop
        })
    }

    private fun navigateToNextScreen() {
        uiScope.launch {
            EventBus.getDefault().post(RefreshDailySavingEvent())
            delay(SCREEN_TIMER)
            if (args.progressScreenData.stopDailySaving || (args.progressScreenData.version == DailyInvestmentCancellationEnum.V3.name && args.progressScreenData.pauseDailySaving)) {
                navigateTo(
                    ProgressRedirectionFragmentDirections.actionProgressRedirectionFragmentToPostCancellationFragment(),
                    shouldAnimate = true,
                    popUpTo = R.id.progressRedirectionFragment,
                    inclusive = true
                )
            } else {
                navigateTo(
                    ProgressRedirectionFragmentDirections.actionProgressRedirectionFragmentToDailyInvestmentSettingsV2Fragment(),
                    shouldAnimate = true,
                    popUpTo = R.id.dailyInvestmentSettingsV2Fragment,
                    inclusive = true
                )
            }
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBackPressDispatcher()
        navigateToNextScreen()
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun headingText(numberOfDays: Int, version: String? = null): String {
        return if (version == DailyInvestmentCancellationEnum.V4.name) {
            requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.your_daily_saving_stopped)
        } else if (numberOfDays != 0) {
            requireContext().resources.getString(
                com.jar.app.feature_daily_investment_cancellation.shared.R.string.saving_stoped_x,
                getPauseSavingData(args.progressScreenData.numberOfDays.toInt())
            )
        } else {
            requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.saving_stoped_permanently)
        }
    }

    private fun subHeadingText(numberOfDays: Int, version: String? = null): String {
        return  if (version == DailyInvestmentCancellationEnum.V4.name) {
            requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.no_money_will_be_debited_from_your_account)
        } else if (numberOfDays != 0) {
            requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.your_saving_will_resume_on) + convertDaysDifferenceToDate(
                args.progressScreenData.numberOfDays.toInt()
            )
        } else {
            requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.you_can_restart_when_ever_you_are_ready)
        }
    }

    private fun getPauseSavingData(numberOfDays: Int): String {
        return when (numberOfDays) {
            1 -> requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.till_tomorrow)
            7 -> {
                requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.for_1_week)
            }
            else -> {
                requireContext().resources.getString(com.jar.app.feature_daily_investment_cancellation.shared.R.string.for_2_week)
            }
        }
    }

    private fun convertDaysDifferenceToDate(numberOfDays: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDays)

        return " " + calendar.time.getFormattedDate("dd MMM''yy")
    }
}
