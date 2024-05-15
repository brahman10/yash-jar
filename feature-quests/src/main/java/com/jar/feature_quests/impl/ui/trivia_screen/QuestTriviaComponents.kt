package com.jar.feature_quests.impl.ui.trivia_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.feature_quests.impl.ui.splash_screen.percentageOffset
import com.jar.feature_quests.shared.domain.model.QuizViewItems

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TriviaScreenCoinBackground(
    modifier: Modifier,
    data: QuizViewItems?,
    content: @Composable () -> Unit
) {
    Box(modifier) {
        Divider(
            thickness = 1.dp,
            color = colorResource(id = com.jar.app.core_ui.R.color.color_403959),
            modifier = Modifier.align(
                Alignment.TopCenter
            )
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopStart)
                .percentageOffset(0, 0)
                .fillMaxWidth(0.1f),
            model = data?.bgCoin1.orEmpty(),
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .percentageOffset(0, -2)
                .fillMaxWidth(0.1f),
            model = data?.bgCoin2,
            contentDescription = ""
        )
        GlideImage(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .percentageOffset(0, 3)
                .fillMaxWidth(0.1f),
            model = data?.bgCoin3,
            contentDescription = ""
        )
        content()
    }
}