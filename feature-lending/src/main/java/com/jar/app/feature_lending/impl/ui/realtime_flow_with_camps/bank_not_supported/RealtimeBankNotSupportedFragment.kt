package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.bank_not_supported

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_ui.R
import com.jar.app.feature_lending.impl.ui.common_component.mokoStringResource
import com.jar.app.feature_lending.shared.MR
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class RealtimeBankNotSupportedFragment : BaseComposeFragment() {
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    @Composable
    override fun RenderScreen() {
        RealtimeBankNotSupportedScreen(
            onPrimaryButtonClicked = {
                popBackStack()
            },
            onNavigateToHome = {
                EventBus.getDefault().post(GoToHomeEvent("REALTIME_BANK_NOT_SUPPORTED"))
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
    }
}

@Composable
private fun RealtimeBankNotSupportedScreen(
    onPrimaryButtonClicked: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(
            modifier = Modifier
                .size(114.dp)
                .clip(CircleShape),
            painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_hour_glass),
            contentDescription = "Not Supported Image",
            contentScale = ContentScale.Inside
        )

        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_we_ve_got_your_response),
            fontSize = 18.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(700),
            color = colorResource(id = R.color.white),
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = mokoStringResource(MR.strings.feature_lending_unfortunately_your_bank_is_not_supported_at_the_moment),
            fontSize = 14.sp,
            fontFamily = jarInterFontFamily,
            color = colorResource(id = R.color.color_ACA1D3),
            textAlign = TextAlign.Center
        )

        JarPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp, top = 40.dp),
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_change_bank),
            isAllCaps = false,
            onClick = onPrimaryButtonClicked,
            iconPadding = 0.dp
        )

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
                .debounceClickable {
                    onNavigateToHome.invoke()
                },
            text = mokoStringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_go_to_home),
            fontSize = 12.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(600),
            color = colorResource(id = R.color.color_D5CDF2),
            textDecoration = TextDecoration.Underline
        )
    }
}

@Preview
@Composable
private fun RealtimeBankNotSupportedScreenPreview() {
    RealtimeBankNotSupportedScreen()
}