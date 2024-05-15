package com.jar.gold_redemption.impl.ui.faq_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.views.RenderBaseToolBar


@Composable
fun RenderToolBar(RightSectionClick: () -> Unit, backPress: () -> Unit) {
    RenderBaseToolBar(
        modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_272239)),
        onBackClick = {
            backPress()
        },
        title = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_faqs),
        {
            RenderImagePillButton(
                modifier = Modifier.padding(end = 16.dp).debounceClickable { RightSectionClick() },
                drawableRes = com.jar.app.core_ui.R.drawable.ic_whatsapp,
                text = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_contact_support),
                bgColor = com.jar.app.core_ui.R.color.color_3c3357,
                textColor = com.jar.app.core_ui.R.color.white,
                maxLines = 1
            )
        }
    )
}