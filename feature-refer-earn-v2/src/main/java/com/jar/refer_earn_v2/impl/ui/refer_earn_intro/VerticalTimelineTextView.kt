package com.jar.refer_earn_v2.impl.ui.refer_earn_intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.jar.app.core_compose_ui.utils.sdp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderTimelineView
import com.jar.app.feature_refer_earn_v2.R
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferralBreakup

@Composable
@Preview()
fun  RenderVerticalTimelineTextViewsPreview() {
    RenderVerticalTimelineTextViews(
        listOf(
            ReferralBreakup("₹15 off on buying gold", "https://cdn.myjar.app/ReferAndEarn/referral-gold.png", "Coupons for you & your friend on sign up."),
            ReferralBreakup("Free gold upto ₹50", "https://cdn.myjar.app/ReferAndEarn/referral-gold.png", "Coupons for you & your friend on sign up."),
        ),
        Modifier.padding(horizontal = 26.sdp)
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RenderLeftIconContent(drawableIcon: String) {
    GlideImage(
        model = drawableIcon,
        contentDescription = "",
        modifier = Modifier
            .size(48.sdp)
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_4220A1), shape = CircleShape)
            .padding(8.sdp)
    )
}


@Composable
private fun RenderRightContent(title: String, subtitle: String, isLast: Boolean) {
    Column(Modifier
        .padding(bottom = if (isLast) 20.sdp else 16.sdp)
    ) {
        Text(text = title, style = JarTypography.dynamic.h6, modifier = Modifier.padding(bottom = 4.sdp), color = colorResource(
            id = R.color.color_FFFAF2
        ))
        Text(text = subtitle, style = JarTypography.dynamic.pRegular, color = colorResource(
            id = R.color.color_FFFAF2
        ).copy(alpha = 0.8f))
    }
}

@Composable
fun RenderVerticalTimelineTextViews(list: List<ReferralBreakup?>, modifier: Modifier) {
    RenderTimelineView(
        modifier = modifier,
        size = list.size,
        RenderLeftIconContent = {
            RenderLeftIconContent(list[it]?.icon.orEmpty())
        },
        RenderRightContent = {
            RenderRightContent(list[it]?.title.orEmpty(), list[it]?.description.orEmpty(), isLast = list.size - 1 == it)
        },
        colorForDivider = {
            colorResource(id = com.jar.app.core_ui.R.color.color_4220A1)
        },
        dividierWidth = 2.sdp
    )
}