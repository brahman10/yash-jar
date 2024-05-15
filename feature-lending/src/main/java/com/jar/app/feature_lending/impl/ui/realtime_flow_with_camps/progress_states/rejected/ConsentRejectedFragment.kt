package com.jar.app.feature_lending.impl.ui.realtime_flow_with_camps.progress_states.rejected

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarColors
import com.jar.app.core_compose_ui.theme.jarInterFontFamily
import com.jar.app.core_ui.R
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
internal class ConsentRejectedFragment : BaseComposeFragment() {
    override fun setupAppBar() {
    }

    @Composable
    override fun RenderScreen() {
        ConsentRejectedScreen(
            onGoToHomeButtonClick = {
                EventBus.getDefault().post(GoToHomeEvent("CONSENT_REJECTED_SCREEN"))
            }
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
    }
}

@Composable
fun ConsentRejectedScreen(
    onGoToHomeButtonClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = JarColors.bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Image(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_2E2942)),
            painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_close_white),
            contentDescription = "Error Image",
            contentScale = ContentScale.Inside
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_you_have_rejected_your_consent.resourceId),
            fontSize = 18.sp,
            fontFamily = jarInterFontFamily,
            fontWeight = FontWeight(700),
            color = colorResource(id = R.color.white)
        )

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_taking_you_to_homescreen.resourceId),
            fontSize = 14.sp,
            fontFamily = jarInterFontFamily,
            color = colorResource(id = R.color.color_D5CDF2)
        )

        Spacer(modifier = Modifier.weight(1f))

        JarSecondaryButton(
            modifier = Modifier
                .fillMaxWidth(),
            isAllCaps = false,
            text = stringResource(id = com.jar.app.feature_lending.shared.MR.strings.feature_lending_go_to_homescreen.resourceId),
            onClick = onGoToHomeButtonClick
        )
    }
}

@Preview
@Composable
fun ConsentRejectedScreenPreview() {
    ConsentRejectedScreen()
}