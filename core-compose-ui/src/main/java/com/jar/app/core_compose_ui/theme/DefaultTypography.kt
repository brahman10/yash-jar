package com.jar.app.core_compose_ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.airbnb.android.showkase.annotation.ShowkaseTypography

object JarTypography {
    val dynamic: JarTypographyData // Eventually this has to be renamed to typography
        @Composable
        get() = LocalJarTypography.current

    @ShowkaseTypography(name = "body1", group = "Jar Typography")
    val body1 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )

    @ShowkaseTypography(name = "body2", group = "Jar Typography")
    val body2 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )

    @ShowkaseTypography(name = "overline", group = "Jar Typography")
    val overline = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
    )

    @ShowkaseTypography(name = "h6", group = "Jar Typography")
    val h6 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )

    @ShowkaseTypography(name = "h5", group = "Jar Typography")
    val h5 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    @ShowkaseTypography(name = "h4", group = "Jar Typography")
    val h4 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    @ShowkaseTypography(name = "h3", group = "Jar Typography")
    val h3 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )

    @ShowkaseTypography(name = "h2", group = "Jar Typography")
    val h2 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )

    @ShowkaseTypography(name = "h1", group = "Jar Typography")
    val h1 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )

    @ShowkaseTypography(name = "caption", group = "Jar Typography")
    val caption = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )

    val largeBodyRegular = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    val bodyBold = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val bodySemiBold = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val bodyRegular = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    val pBold = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 12.sp
    )
    val pSemiBold = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
    val pRegular = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
    val spRegular = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )

    val label = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 14.sp
    )
    val smallLabel = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 12.sp
    )
    val t1 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    val t2 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    val t3 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    val t4 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
    val b1 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )
    val b2 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
    val b3 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp
    )
    val b4 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp
    )
    val b5 = TextStyle(
        fontFamily = jarFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 16.sp
    )
}
