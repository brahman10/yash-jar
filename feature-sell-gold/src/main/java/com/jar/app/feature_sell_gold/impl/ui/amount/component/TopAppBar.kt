package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.JarCommonText
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R

@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    endIcon: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(57.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.ic_arrow_back),
            "BackArrow",
            modifier = Modifier
                .debounceClickable { onBackClick() }
                .size(20.dp),
        )
        JarCommonText(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            text = title,
            color = Color.White,
            style = JarTypography.h6
        )
        if (endIcon != null) {
            endIcon()
        }
    }
}