package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.JarCommonText
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R



@Composable
@Preview
fun GradientSeperatorPreview() {
    Column(Modifier.fillMaxWidth().height(100.dp)) {
        Spacer(Modifier.fillMaxWidth().height(50.dp))
        GradientSeperator(Modifier)
    }
}

@Composable
fun GradientSeperator(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .height(0.5.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.0f to colorResource(com.jar.app.core_compose_ui.R.color.color_00000000),
                        0.2f to colorResource(com.jar.app.core_compose_ui.R.color.color_00000000),
                        0.5f to colorResource(com.jar.app.core_compose_ui.R.color.color_FFFFFFFF),
                        0.8f to colorResource(com.jar.app.core_compose_ui.R.color.color_00000000),
                        1f to colorResource(com.jar.app.core_compose_ui.R.color.color_00000000),
                    )
                )
            )
    )
}

@Composable
fun RenderBaseToolBar(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit),
    title: String,
    RightSection: (@Composable () -> Unit)? = null,
    colorFilter: ColorFilter? = null,
    titleImage: Int? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(com.jar.app.core_ui.R.drawable.ic_arrow_back),
            "BackArrow",
            modifier = Modifier
                .padding(start = 18.dp)
                .debounceClickable { onBackClick() }
                .size(24.dp),
            colorFilter = colorFilter
        )
        if (titleImage != null) {
            Image(
                modifier = Modifier.padding(start = 10.dp),
                painter = painterResource(id = titleImage),
                contentDescription = "title image"
            )
        }
        JarCommonText(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            text = title,
            color = Color.White,
            style = JarTypography.h6
        )
        RightSection?.invoke()
    }
}
