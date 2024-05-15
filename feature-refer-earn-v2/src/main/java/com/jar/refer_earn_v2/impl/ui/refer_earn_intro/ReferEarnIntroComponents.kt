package com.jar.refer_earn_v2.impl.ui.refer_earn_intro

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.jar.app.core_compose_ui.utils.sdp
import com.jar.app.core_compose_ui.component.noRippleDebounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_refer_earn_v2.R


@Composable
fun RenderToolBar(backPress: () -> Unit) {
    RenderBaseToolBar(
        modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_221D32)),
        onBackClick = {
            backPress()
        },
        title = stringResource(com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_earn_v2_title),
        {

        }
    )
}

@Composable
fun RenderTransparentButton(text: String, onClick: () -> Unit, @DrawableRes icon: Int, modifier: Modifier) {
    Row(modifier = modifier.noRippleDebounceClickable { onClick() }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = icon), contentDescription = "", modifier = Modifier.size(16.sdp))
        Text(text, color = Color.White, modifier = Modifier.padding(start = 6.sdp), style = JarTypography.dynamic.pBold)
    }
}

@Composable
fun RenderBottomSeperatorText(
    inviteContacts: (() -> Unit)? = null,
    shareVia: (() -> Unit)? = null,
    inviteContactsViaWhatsapp: () -> Unit,
    shouldHaveWABtnOnTop: Boolean
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
    ) {
        RenderTransparentButton(
            text = stringResource(id = if (!shouldHaveWABtnOnTop) com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_invite_via_whatsapp else com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_invite_contacts),
            onClick = { if (!shouldHaveWABtnOnTop) inviteContactsViaWhatsapp() else inviteContacts?.invoke() },
            icon = if (!shouldHaveWABtnOnTop) com.jar.app.core_ui.R.drawable.ic_whatsapp else com.jar.app.core_ui.R.drawable.core_ui_phonebook,
            modifier = Modifier.weight(1f),
        )
        Spacer(
            modifier = Modifier
                .width(1.sdp)
                .fillMaxHeight()
                .background(Color(0x4DFFFFFF))
                .padding(vertical = 2.sdp)
        )

        RenderTransparentButton(
            text = stringResource(id = com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_share_via),
            onClick = { shareVia?.invoke() },
            icon = R.drawable.feature_contacts_sync_share,
            modifier = Modifier.weight(1f),
        )
    }
}