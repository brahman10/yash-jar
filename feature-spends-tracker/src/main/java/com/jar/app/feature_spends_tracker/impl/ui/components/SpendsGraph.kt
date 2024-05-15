package com.jar.app.feature_spends_tracker.impl.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.jar.app.core_base.util.roundUp
import com.jar.app.feature_spends_tracker.R
import com.jar.app.feature_spends_tracker.shared.utils.light_carmine_pink
import kotlin.math.pow

@Composable
fun GradientBarChart(data: List<Int>, yAxis: List<String>, maxValue: Int, xAxis: List<String>) {
    val startColor = Color(light_carmine_pink)
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom

    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight(Alignment.CenterVertically)
                .height(200.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //y-axis layout
            Column(Modifier.weight(2f)) {
                Row {

                    //y axis values
                    Yxis(yAxis)
                }

            }

            Column(
                Modifier
                    .weight(8f)
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    data.forEachIndexed { index, it ->
                        val heightFraction = (it.toFloat() / maxValue.toFloat()).roundUp(1)
                        Bar(index, data, it, heightFraction, startColor)
                    }

                }
            }


        }



        Row(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(2f)) {

            }
            Column(Modifier.weight(8f)) {
                Row(Modifier.fillMaxWidth()) {
                    this.Xaxis(xAxis)
                }

            }


        }

    }

}

@Composable
private fun RowScope.Xaxis(xAxis: List<String>) {
    xAxis.forEach {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight(700)
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
private fun Yxis(yAxis: List<String>) {
    Column(
        Modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
            .background(Color.Transparent),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        yAxis.forEach { yValue ->
            Text(yValue, color = Color(0xFF776E94))
        }
    }
    Spacer(modifier = Modifier.width(8.dp))

    // y-axis line
    Box(
        modifier = Modifier
            .wrapContentHeight(Alignment.CenterVertically)
            .width(1.dp)
            .fillMaxHeight()
            .background(Color(0xFF776E94))
    )
}

@Composable
private fun RowScope.Bar(
    index: Int,
    data: List<Int>,
    it: Int,
    heightFraction: Float,
    color: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
        verticalArrangement = Arrangement.Bottom
    ) {

        if (index == data.size - 1) {
            Box(modifier = Modifier
                .zIndex(5f)
                .align(Alignment.CenterHorizontally)) {
                if (heightFraction <= 0.3f) {
                    AmountSpendsView(it, heightFraction)
                    Spacer(modifier = Modifier.padding(bottom = 4.dp))
                }
                Image(
                    painter = painterResource(R.drawable.ic_glimmer),
                    contentDescription = "Image",
                    modifier = Modifier
                        .offset(y = 24.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            if (heightFraction <= 0.3f) {
                AmountSpendsView(it, heightFraction)
                Spacer(modifier = Modifier.padding(bottom = 4.dp))
            }
        }

        Card(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 4.dp
                    )
                )
                .fillMaxHeight(if (heightFraction > 0) heightFraction else 0.3f)
                .fillMaxWidth()
                .background(
                    when {
                        heightFraction <= 0 -> {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            )
                        }

                        index == data.size - 1 -> {
                            Brush.verticalGradient(
                                colors = listOf(
                                    color,
                                    color
                                )
                            )
                        }

                        else -> {
                            val maxOpacity = 0.5f
                            val minOpacity =
                                0.0f // Adjust this value to control the minimum opacity

                            Brush.verticalGradient(
                                colors = listOf(
                                    color.copy(alpha = maxOpacity),
                                    color.copy(
                                        alpha = maxOpacity - (maxOpacity - minOpacity) * heightFraction.pow(
                                            0.5f
                                        )
                                    )
                                )
                            )
                        }

                    }

                ),
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            border = if (heightFraction <= 0) BorderStroke(
                2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF776E94).copy(alpha = 0.8f),
                        Color(0xFF776E94).copy(alpha = 0f)
                    )
                )
            ) else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                if (heightFraction >= 0.4) {
                    AmountSpendsView(it, heightFraction)
                }

            }

        }


    }


    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
}

@Composable
private fun AmountSpendsView(it: Int, heightFraction: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "₹$it",
            color = Color.White
        )

        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            text = if (heightFraction <= 0) "No spends" else "Total Spends",
            color = Color.White.copy(alpha = 0.3f),
            style = TextStyle(fontSize = 10.sp),
        )

    }
}


@Preview
@Composable
fun PreviewMyApp() {
    GradientBarChart(
        listOf(1170, 4300, 712),
        listOf("0.0k", "0.6k", "1.3k", "2.0k").sortedDescending(),
        maxValue = 4300,
        xAxis = listOf("22 May- 28 May", "22 May- 28 May", "22 May- 28 May")
    )
}
