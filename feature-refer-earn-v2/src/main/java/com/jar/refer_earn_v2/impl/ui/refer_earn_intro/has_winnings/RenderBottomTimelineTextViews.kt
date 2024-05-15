package com.jar.refer_earn_v2.impl.ui.refer_earn_intro.has_winnings

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.jar.app.core_compose_ui.utils.sdp
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.utils.ssp
import com.jar.refer_earn_v2.impl.ui.refer_earn_intro.RenderBottomSeperatorText
import kotlinx.coroutines.delay

@Composable
public fun RenderBottomTimelineTextViews(
    shouldHaveWABtnOnTop: Boolean,
    shouldAnimateContactsButton: Boolean,
    inviteContactsViaWhatsapp: () -> Unit,
    inviteContacts: () -> Unit,
    shareVia: () -> Unit,
) {
    var shouldAnimate by remember { mutableStateOf(false) }
    if (shouldAnimateContactsButton) {
        LaunchedEffect(true) {
            delay(3000) // Wait for 3 seconds
            shouldAnimate = true // Enable animation
        }
    }
    val animateShakeX by animateFloatAsState(
        targetValue = if (shouldAnimate) -20f else 0f,
        animationSpec = repeatable(
            iterations = 10,
            animation = tween(durationMillis = 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "",
        finishedListener = {
            shouldAnimate = false
        })

    JarPrimaryButton(
        modifier = Modifier
            .padding(bottom = 8.sdp, start = 16.sdp, end = 16.sdp)
            .fillMaxWidth()
            .graphicsLayer {
                translationX = animateShakeX
            },
        text = stringResource(id = if (shouldHaveWABtnOnTop) com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_invite_via_whatsapp else com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_invite_contacts),
        onClick = { if (shouldHaveWABtnOnTop) inviteContactsViaWhatsapp() else inviteContacts() },
        icon = if (shouldHaveWABtnOnTop) com.jar.app.core_ui.R.drawable.ic_whatsapp else com.jar.app.core_ui.R.drawable.core_ui_phonebook,
        isAllCaps = false,
        color = colorResource(id = com.jar.app.core_ui.R.color.color_121127),
        elevation = 0.sdp,
        fontSize = 12.ssp,
    )
    RenderBottomSeperatorText(inviteContacts, shareVia, inviteContactsViaWhatsapp, shouldHaveWABtnOnTop)
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(20.sdp)
    )
}