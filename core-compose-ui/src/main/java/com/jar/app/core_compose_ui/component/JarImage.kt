package com.jar.app.core_compose_ui.component

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.RequestBuilderTransform
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants.BuyGoldFlowContext.HAMBURGER_MENU
import com.jar.app.core_compose_ui.utils.JarImageAnalyticsModel


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun JarImage(
    imageUrl: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    loading: Placeholder? = null,
    failure: Placeholder? = null,
    fromScreen: String? = null,
    startTime: Long? = null,
    apiExecutionCount: Int? = null,
    afterLoading: (jarImageAnalyticsModel: JarImageAnalyticsModel) -> Unit = {},
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it }
) {
    var screenRecomposeCount by remember { mutableStateOf(apiExecutionCount) }
    GlideImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        loading = loading,
        failure = failure,
        requestBuilderTransform = {
            it.addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {

                    if (screenRecomposeCount == 1 && (fromScreen == HAMBURGER_MENU || fromScreen == EventKey.HOME_SCREEN)) {
                        screenRecomposeCount = screenRecomposeCount?.plus(1)
                        val isFromCache =
                            dataSource === DataSource.MEMORY_CACHE || dataSource === DataSource.DATA_DISK_CACHE
                        val currentTime = System.currentTimeMillis()
                        afterLoading.invoke(JarImageAnalyticsModel(
                            startTime = startTime!!,
                            endTime = currentTime,
                            isItFromCache = isFromCache
                        ))
                    }
                    return false
                }
            })
        }
    )
}
