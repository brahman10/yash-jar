package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.pan.error_screen

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.ui.common_component.ToolbarWithHelpButton
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class PanErrorStatesFragment : BaseComposeFragment() {

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var serializer: Serializer


    private val args: PanErrorStatesFragmentArgs by navArgs()

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        PanErrorStatesScreen(
            arguments = args.panErrorStatesArguments,
            onBackButtonClicked = {
                popBackStack()
            },
            onHelpButtonClicked = {
                openHelpSection()
            },
            onPrimaryButtonClicked = {
                if (args.panErrorStatesArguments.haveTechnicalError) {
                    // TODO : OPEN OTP SCREEN FOR RETRY...
                } else {
                    if (args.panErrorStatesArguments.isInvalidPan) {
                        popBackStack()
                    } else {
                        EventBus.getDefault().post(GoToHomeEvent("PAN_LIMIT_EXCEED_SCREEN"))
                    }
                }
            },
            onSecondaryButtonClicked = {
                // TODO : OPEN DOCUMENT SELECTION SCREEN
            },
            onNavigateToHome = {
                EventBus.getDefault().post(GoToHomeEvent("TECHNICAL_ERROR_SCREEN"))
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
    }

    private fun openHelpSection() {
        requireContext().openWhatsapp(
            remoteConfigApi.getWhatsappNumber(), ""
        )
    }
}

@Composable
private fun PanErrorStatesScreen(
    arguments: PanErrorStatesArguments,
    onBackButtonClicked: () -> Unit = {},
    onHelpButtonClicked: () -> Unit = {},
    onPrimaryButtonClicked: () -> Unit = {},
    onSecondaryButtonClicked: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    Column {
            ToolbarWithHelpButton(
                onBackButtonClick = onBackButtonClicked,
                title = if (!arguments.haveTechnicalError) arguments.heading else "",
                onHelpButtonClick = onHelpButtonClicked
            )
        Column(
            Modifier
                .fillMaxSize()
                .background(color = JarColors.bgColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Image(
                modifier = if (arguments.isInvalidPan) {
                    Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942))
                } else {
                    Modifier
                        .size(88.dp)
                },
                painter = painterResource(id = arguments.imageId),
                contentDescription = "Error Image",
                contentScale = ContentScale.Inside
            )

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = arguments.title,
                fontSize = 18.sp,
                fontFamily = jarInterFontFamily,
                fontWeight = FontWeight(700),
                color = colorResource(id = com.jar.app.core_ui.R.color.white)
            )

            if (!arguments.haveTechnicalError) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = arguments.subTitle,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontFamily = jarInterFontFamily,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                )
            }

            JarPrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (arguments.haveTechnicalError) 64.dp else 16.dp)
                    .padding(top = 40.dp),
                text =
                if (arguments.haveTechnicalError) {
                    stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_try_again.resourceId)
                } else {
                    if (arguments.isInvalidPan) stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_enter_PAN_again.resourceId) else stringResource(
                        id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_go_to_homescreen.resourceId
                    )
                },
                isAllCaps = false,
                onClick = onPrimaryButtonClicked,
                iconPadding = 0.dp
            )

            if (arguments.haveTechnicalError) {
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_or.resourceId),
                    fontSize = 14.sp,
                    fontFamily = jarInterFontFamily,
                    fontWeight = FontWeight(400),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2)
                )

                JarSecondaryButton(
                    modifier = Modifier.padding(top = 24.dp),
                    text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_upload_bank_statement.resourceId),
                    onClick = onSecondaryButtonClicked,
                    icon = R.drawable.feature_lending_real_time_flow_pdf_icon,
                    iconSize = DpSize(20.dp, 24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                        .debounceClickable {
                            onNavigateToHome.invoke()
                        },
                    text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_go_to_homescreen.resourceId),
                    fontSize = 12.sp,
                    fontFamily = jarInterFontFamily,
                    fontWeight = FontWeight(600),
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                    textDecoration = TextDecoration.Underline
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
private fun PanEntryLimitExceedScreenPreview() {
    PanErrorStatesScreen(
        PanErrorStatesArguments(
            heading = "PAN Status",
            title = stringResource(id = com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_pan_entry_attempt_limit_exceeded.resourceId),
            subTitle = stringResource(id = com.jar.app.feature_lending_kyc.shared.MR.strings.feature_lending_kyc_please_try_again_tomorrow.resourceId),
            imageId = com.jar.app.feature_lending.R.drawable.feature_lending_ic_error_i,
            haveTechnicalError = true
        )
    )
}