package com.jar.app.core_compose_ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.jar.app.core_ui.R

@SuppressLint("NewApi")
@Composable
fun JarTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // This use to derive appropriate dimens from the intuit library
    val current = LocalContext.current
    val fontResolver: (id: Int) -> TextUnit = {
        with(current.resources) {
            (getDimension(it) / displayMetrics.scaledDensity).sp
        }
    }
    val typography = remember {
        generateTypography(fontResolver)
    }

    // https://developer.android.com/jetpack/compose/designsystems/custom#replacing-systems
    CompositionLocalProvider(
        LocalJarTypography provides typography,
    ) {
        MaterialTheme(
            content = content,
            colors = MaterialTheme.colors.copy(
                surface = colorResource(id = R.color.color_2e2942)
            )
        )
    }
}

fun generateTypography(fontResolver: (id: Int) -> TextUnit): JarTypographyData {
    return JarTypographyData(
        h1 = h1Fun(fontResolver),
        h2 = h2Fun(fontResolver),
        h3 = h3Fun(fontResolver),
        h4 = h4Fun(fontResolver),
        h5 = h5Fun(fontResolver),
        h6 = h6Fun(fontResolver),
        t1 = t1Fun(fontResolver),
        t2 = t2Fun(fontResolver),
        t3 = t3Fun(fontResolver),
        t4 = t4Fun(fontResolver),
        b1 = b1Fun(fontResolver),
        b2 = b2Fun(fontResolver),
        b3 = b3Fun(fontResolver),
        b4 = b4Fun(fontResolver),
        b5 = b5Fun(fontResolver),
        largeBodyRegular = largeBodyRegularFun(fontResolver),
        bodyBold = bodyBoldFun(fontResolver),
        bodySemiBold = bodySemiBoldFun(fontResolver),
        bodyRegular = bodyRegularFun(fontResolver),
        pBold = pBoldFun(fontResolver),
        pSemiBold = pSemiBoldFun(fontResolver),
        pRegular = pRegularFun(fontResolver),
        spRegular = spRegularFun(fontResolver),
        label = labelFun(fontResolver),
        smallLabel = smallLabelFun(fontResolver),
    )
}