package com.jar.refer_earn_v2.impl.ui.refer_earn_intro.referral_bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.jar.app.core_compose_ui.utils.sdp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.orTrue
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.GradientSeperator
import com.jar.app.core_compose_ui.views.AvatarIconCompose
import com.jar.app.core_compose_ui.views.RenderTimelineView
import com.jar.app.core_ui.R
import com.jar.app.feature_refer_earn_v2.shared.domain.model.Referral
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralProgress
import com.jar.app.core_compose_ui.utils.ssp

@Composable
fun RenderReferralItem(
    list: Referral,
    deeplinkNavigateFxn: (String) -> Unit
) {
    Card(Modifier.padding(horizontal = 16.sdp, vertical = 8.sdp), backgroundColor = colorResource(id = R.color.color_3C3357), shape = RoundedCornerShape(12.sdp)) {
        Column(Modifier.padding(16.sdp)) {
            Row (verticalAlignment = Alignment.CenterVertically) {
                AvatarIconCompose(list.referralName.orEmpty(), true)
                Column (Modifier.padding(start = 8.sdp)) {
                    Text(text = list.referralName.orEmpty(), style = JarTypography.dynamic.h5, color = colorResource(
                        id = R.color.white
                    ), fontSize = 14.sp)

                    Text(text = list.referralPhone.orEmpty(), style = JarTypography.dynamic.bodyRegular, color = colorResource(
                        id = com.jar.app.core_ui.R.color.color_ACA1D3
                    ), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(8.sdp))
            GradientSeperator(Modifier)
            Spacer(Modifier.height(16.sdp))
            RenderTimelineView(
                size = list.referralProgressList?.size.orZero(),
                RenderLeftIconContent = { RenderLeftIconContent(list.referralProgressList?.getOrNull(it)) },
                RenderRightContent = { RenderRightContent(list.referralProgressList?.getOrNull(it), isLast = it == list.referralProgressList?.size.orZero() - 1, onItemClick = deeplinkNavigateFxn) },
                colorForDivider = { generateColorForDivider(list, it) },
                dividierWidth = 1.sdp
            )
        }
    }
}

@Composable
fun generateColorForDivider(referral: Referral?, index: Int): Color {
    return if (referral?.referralProgressList?.getOrNull(index + 1)?.isNullTransaction().orTrue()) {
        colorResource(id = R.color.color_2E2942)
    } else {
        colorResource(id = R.color.color_58DDC8)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RenderRightContent(data: ReferralProgress?, isLast: Boolean, onItemClick: (String) -> Unit) {
    data ?: return
    val color = if (data.isNullTransaction().orTrue()) colorResource(id = R.color.color_776E94) else colorResource(id = R.color.color_EEEAFF)
    val attributionText = if (data.isNullTransaction().orTrue()) "" else data.attributionTime.orEmpty()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 4.sdp else 10.sdp)) {
        Column(Modifier.weight(1f)) {
            Text(data.key.orEmpty(), color = color, style = JarTypography.dynamic.bodyRegular)
            Text(
                attributionText,
                color = colorResource(id = R.color.color_776E94),
                style = JarTypography.dynamic.bodyRegular.copy(fontSize = 12.ssp),
                modifier = Modifier.padding(top = 4.sdp, bottom = if (attributionText.isBlank()) 0.sdp else 12.sdp)
            )
        }

        data.delayInMinutes?.let {
            Text(
                text = it,
                style = JarTypography.dynamic.bodyRegular.copy(fontSize = 12.ssp, lineHeight = 18.ssp),
                color = colorResource(id = R.color.color_776E94)
            )
        } ?: run {
            data.value?.let {
                RenderRewardIcon(data.icon.orEmpty(), data.value.orEmpty(), data.deeplink, onItemClick)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RenderRewardIcon(
    icon: String,
    value: String,
    deeplink: String?,
    deeplinkNavigateFunction: (String) -> Unit
) {
    Row(
        Modifier
            .background(
                colorResource(id = R.color.color_776E94).copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.sdp)
            )
            .padding(vertical = 6.sdp, horizontal = 10.sdp)
            .debounceClickable { deeplink?.let { deeplinkNavigateFunction(it) } },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        GlideImage(model = icon, contentDescription = "", modifier = Modifier
            .size(24.sdp)
            .padding(end = 4.sdp))
        Text(
            text = value,
            color = colorResource(id = R.color.color_D5CDF2),
            style = JarTypography.dynamic.largeBodyRegular,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 40.sdp)
        )
    }
}

@Composable
internal fun RenderLeftIconContent(data: ReferralProgress?) {
    val icon = if (data?.isNullTransaction().orTrue()) com.jar.app.feature_refer_earn_v2.R.drawable.feature_refer_earn_black else com.jar.app.feature_refer_earn_v2.R.drawable.feature_refer_earn_v2_tick
    Box(Modifier
        .size(24.sdp),
        contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            tint = Color.Unspecified,
            modifier = Modifier.size(24.sdp)
        )
    }
}