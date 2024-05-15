package com.jar.feature_quests.impl.ui.dashboard_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_quests.R


@Composable
fun showPopup(function: () -> Unit) {
    Popup(
        alignment = Alignment.Center, properties = PopupProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = { function() }
    ) {
        Column(
            Modifier
                .background(
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_A9EBC5),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.feature_quests_quest_unlocked),
                style = JarTypography.body1,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                stringResource(R.string.feature_quests_congratulations),
                style = JarTypography.h3,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                stringResource(R.string.feature_quests_unlocked_welcome_bonus),
                style = JarTypography.body1,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                modifier = Modifier.padding(top = 2.dp)
            )
            JarPrimaryButton(
                text = stringResource(R.string.feature_quests_play_quest), onClick = { function() },
                modifier = Modifier.padding(top = 24.dp),
                color = colorResource(id = com.jar.app.core_ui.R.color.color_383250),
                borderBrush = null
            )
        }
    }
}