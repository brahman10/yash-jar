package com.jar.app.feature_lending.impl.ui.realtime_flow.finding_offer

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.RefreshLendingEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class FindingBestOfferFragment : BaseComposeFragment() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ToolbarWithHelpButton(
                    onBackButtonClick = {  },
                    title = "",
                    onHelpButtonClick = { openNeedHelp() },
                    shouldShowBackArrow = false,
                    shouldShowTitle = false
                )
            },
        ){ paddingValues ->
            FindingBestOfferScreen(
                modifier = Modifier.padding(paddingValues),
                onGoToHomeButtonClick = {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
                        mapOf(
                            LendingEventKeyV2.screen_name to LendingEventKeyV2.FINDING_OFFER_SCREEN,
                            LendingEventKeyV2.action to LendingEventKeyV2.GO_TO_HOMESCREEN_CLICKED
                        )
                    )
                    handleBackPress()
                }
            )
        }
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        }

    override fun setup(savedInstanceState: Bundle?) {
        EventBus.getDefault().post(RefreshLendingEvent())
        registerBackPressDispatcher()

        analyticsApi.postEvent(
            LendingEventKeyV2.RLENDING_AFTERBANKSTATEMENTUPLOADFLOW,
            mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.FINDING_OFFER_SCREEN,
                LendingEventKeyV2.action to LendingEventKeyV2.FINDING_OFFER_SCREEN_LAUNCHED
            )
        )
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun handleBackPress() {
        EventBus.getDefault().post(GoToHomeEvent("FindingBestOfferScreen"))
    }
    private fun openNeedHelp() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_kyc_contact_support_real_time_help_s_s,
            name.orEmpty(),
            number.orEmpty()
        )
        requireContext().openWhatsapp(sendTo, message)
    }
}

@Composable
fun FindingBestOfferScreen(
    modifier:Modifier = Modifier,
    onGoToHomeButtonClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))


        val composition by rememberLottieComposition(LottieCompositionSpec.Url(LendingConstants.LottieUrls.SEARCHING_LOADER))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

        LottieAnimation(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally),
            composition = composition,
            progress = { progress },
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_finding_best_possible_offer_for_you.resourceId),
            fontSize = 18.sp,
            fontFamily = jarFontFamily,
            fontWeight = FontWeight(700),
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_it_may_take_us_few_days.resourceId),
            fontSize = 14.sp,
            fontFamily = jarFontFamily,
            color = colorResource(id = R.color.color_D5CDF2),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        JarSecondaryButton(
            modifier = Modifier
                .fillMaxWidth(),
            isAllCaps = false,
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_go_to_homescreen.resourceId),
            onClick = onGoToHomeButtonClick,
            borderColor = colorResource(id = R.color.color_846FC0)
        )
    }
}

@Preview
@Composable
fun ConsentRejectedScreenPreview() {
    FindingBestOfferScreen()
}