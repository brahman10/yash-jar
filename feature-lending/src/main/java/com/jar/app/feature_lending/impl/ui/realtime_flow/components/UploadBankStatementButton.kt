package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.feature_lending.R
import java.io.File

@Composable
fun UploadBankStatementButton(title: String, subTitle: String, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_492B9D),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = true, onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.feature_lending_real_time_flow_add_icon),
                modifier = Modifier.padding(top = 28.dp, bottom = 8.dp),
                contentDescription = null
            )
            Text(
                text = title,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = colorResource(id = com.jar.app.core_ui.R.color.white),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(600),
                fontFamily = jarFontFamily,

                )
            Text(
                modifier = Modifier.padding(bottom = 12.dp, top = 28.dp),
                text = subTitle,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(400),
                fontFamily = jarFontFamily,

                )


        }

    }


}

@Preview
@Composable
fun PreviewUploadBankStatementButton() {
    UploadBankStatementButton(
        title = "Upload Axis Bank Statement",
        subTitle = "Only PDF files are allowed | Smaller than 10 MB",
        onClick = {}
    )
}
