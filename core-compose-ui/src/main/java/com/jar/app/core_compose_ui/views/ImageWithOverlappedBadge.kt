package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageWithBadge(
    imageUrl: String,
    badgeUrl: String,
    badgeOffset: Dp = 6.dp,
    imageBackgroundColor: Int = com.jar.app.core_ui.R.color.color_423C5C,
    badgeBackgroundColor: Int = com.jar.app.core_ui.R.color.color_423C5C,
) {
    Box {
        CircularLayout(backgroundColor = colorResource(id = imageBackgroundColor)) {
            JarImage(
                modifier = Modifier
                    .padding(12.dp)
                    .size(25.dp),
                imageUrl = imageUrl,
                contentDescription = null
            )

        }
        CircularLayout(
            modifier = Modifier
                .offset(x = badgeOffset, y = badgeOffset)
                .align(Alignment.BottomEnd)
                .zIndex(10f)
                .clipToBounds(),
            backgroundColor = colorResource(id = badgeBackgroundColor)
        ) {
            JarImage(
                imageUrl = badgeUrl,
                contentDescription = null,
                modifier = Modifier
                    .padding(2.dp)
                    .size(20.dp),
                contentScale = ContentScale.Fit
            )
        }

    }
}
