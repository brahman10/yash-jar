package com.jar.refer_earn_v2.impl.ui.refer_earn_intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.jar.app.core_compose_ui.utils.sdp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.frauncesFontFamily
import com.jar.app.core_compose_ui.utils.ssp
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RenderWithoutRewards(
    columnScope: ColumnScope,
    introScreenData: State<ReferIntroScreenData?>,
    RenderFooterViews: @Composable () -> Unit
) {
    columnScope.apply {
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_6038CE)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = introScreenData.value?.staticContent?.header.orEmpty(),
                style = JarTypography.dynamic.h5.copy(fontFamily = frauncesFontFamily, fontSize = 32.ssp, lineHeight = 32.ssp),
                modifier = Modifier.padding(top = 36.sdp, bottom = 12.sdp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            GlideImage(
                model = introScreenData.value?.staticContent?.icon.orEmpty(),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.sdp).weight(1f)
            )
            RenderFooterViews()
        }
    }
}