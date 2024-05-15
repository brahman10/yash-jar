package com.jar.app.feature_lending.impl.ui.realtime_flow.launching_soon

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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
internal class LaunchingSoonOfferFragment : BaseComposeFragment() {

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyticsApi.postEvent(
            LendingEventKeyV2.EmiCalculator_MainScreenLaunched,
            values = mapOf(
                LendingEventKeyV2.action to LendingEventKeyV2.launching_soon_screen_shown,
                LendingEventKeyV2.screen_name to LendingEventKeyV2.launching_soon_screen,
            )
        )
    }
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ToolbarWithHelpButton(
                    onBackButtonClick = { handleBackPress() },
                    title = "",
                    onHelpButtonClick = {
                        analyticsApi.postEvent(
                            LendingEventKeyV2.EmiCalculator_NeedHelpClicked,
                            values = mapOf(
                                LendingEventKeyV2.screen_name to LendingEventKeyV2.launching_soon_screen,
                            )
                        )
                        openNeedHelp()
                    },
                    shouldShowBackArrow = true,
                    shouldShowTitle = false
                )
            },
        ) { paddingValues ->
            FindingBestOfferScreen(
                modifier = Modifier.padding(paddingValues),
                onGoToHomeButtonClick = {
                    analyticsApi.postEvent(
                        LendingEventKeyV2.EmiCalculator_MainScreenLaunched,
                        values = mapOf(
                            LendingEventKeyV2.action to LendingEventKeyV2.go_back_clicked,
                            LendingEventKeyV2.screen_name to LendingEventKeyV2.launching_soon_screen,
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

    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backPressCallback)
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }

    private fun handleBackPress() {
        analyticsApi.postEvent(
            LendingEventKeyV2.EmiCalculator_BackButtonClicked,
            values = mapOf(
                LendingEventKeyV2.screen_name to LendingEventKeyV2.launching_soon_screen,
            )
        )
        popBackStack()
    }

    private fun openNeedHelp() {
        val sendTo = remoteConfigApi.getWhatsappNumber()
        val number = prefs.getUserPhoneNumber()
        val name = prefs.getUserName()
        val message = getCustomStringFormatted(
            com.jar.app.feature_lending.shared.MR.strings.feature_lending_launching_calculator_help,
            name.orEmpty(),
            number.orEmpty()
        )
        requireContext().openWhatsapp(sendTo, message)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FindingBestOfferScreen(
    modifier: Modifier = Modifier,
    onGoToHomeButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = JarColors.bgColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                model = LendingConstants.ImageUrls.IMAGE_STARS_WITH_CHECK,
                contentDescription = "",
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                modifier = Modifier.padding(top = 54.dp),
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_bringing_exciting_offers.resourceId),
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    fontFamily = jarFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEEEAFF),
                    textAlign = TextAlign.Center,
                )
            )

            ConstraintLayout(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
            ) {
                val (image, text) = createRefs()
                GlideImage(
                    modifier = Modifier
                        .size(232.dp)
                        .constrainAs(image) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.wrapContent
                        },
                    model = LendingConstants.ImageUrls.IMAGE_LAUNCHING_SOON,
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .offset(y = (-12).dp)
                        .constrainAs(text) {
                            top.linkTo(image.bottom)
                            bottom.linkTo(image.bottom)
                            start.linkTo(image.start)
                            end.linkTo(image.end)
                        }
                        .background(color = Color(0xFF7745FF), shape = RoundedCornerShape(size = 4.dp))
                        .padding(start = 12.dp, top = 4.dp, end = 12.dp, bottom = 4.dp),
                    text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_launching_soon.resourceId),
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontFamily = jarFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.color_DFD4FF),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            Text(
                modifier = Modifier.padding(top = 72.dp),
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_notify_after_offer.resourceId),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = jarFontFamily,
                color = colorResource(id = R.color.color_D5CDF2),
                textAlign = TextAlign.Center
            )

            JarSecondaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                isAllCaps = false,
                text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_go_back.resourceId),
                onClick = onGoToHomeButtonClick,
                borderColor = colorResource(id = R.color.color_846FC0)
            )
        }
    }
}

@Preview
@Composable
fun ConsentRejectedScreenPreview() {
    FindingBestOfferScreen()
}