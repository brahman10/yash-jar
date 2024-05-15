package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_lending.R


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RealTimeFlowStep(
    modifier: Modifier = Modifier,
    text: String,
    showDividerRight: Boolean,
    showDividerLeft: Boolean,
    model: Any
) {
    ConstraintLayout(modifier = modifier) {
        val (imageView, titleText, dividerLeft, dividerRight) = createRefs()

        if (showDividerLeft) {
            Divider(
                modifier = Modifier
                    .constrainAs(dividerLeft) {
                        width = Dimension.fillToConstraints
                        start.linkTo(parent.start)
                        end.linkTo(imageView.start)
                        top.linkTo(imageView.top)
                        bottom.linkTo(imageView.bottom)
                    },
                color = Color(0xFF776E94),
                thickness = 1.dp,
            )

        }
        GlideImage(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF2E2942), CircleShape)
                .padding(6.dp)
                .constrainAs(imageView) {
                    centerHorizontallyTo(parent)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
            model = model,
            contentDescription = null
        )
        if (showDividerRight) {
            Divider(
                modifier = Modifier
                    .constrainAs(dividerRight) {
                        width = Dimension.fillToConstraints
                        start.linkTo(imageView.end)
                        end.linkTo(parent.end)
                        top.linkTo(imageView.top)
                        bottom.linkTo(imageView.bottom)
                    },
                color = Color(0xFF776E94),
                thickness = 1.dp
            )
        }

        Text(
            modifier = Modifier
                .padding(top = 12.dp)
                .constrainAs(titleText) {
                top.linkTo(imageView.bottom)
                start.linkTo(imageView.start)
                end.linkTo(imageView.end)
            },
            text = text,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color(0xFFFFFFFF),
            textAlign = TextAlign.Center,
            fontFamily = jarFontFamily
        )

    }
}

@Preview
@Composable
fun previewRealTimeFlowStep() {
    RealTimeFlowStep(
        text = "Add Bank\n Account  ",
        showDividerRight = true,
        showDividerLeft = true,
        model = painterResource(id = R.drawable.feature_lending_cash_icon)
    )
}



