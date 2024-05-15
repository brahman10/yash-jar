@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jar.app.core_compose_ui.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.util.truncateAndAddDot


data class LabelAndValueCompose(
    val label: String,
    val value: String,
    val showCopyToClipBoardIconAndTruncate: Boolean = false,
    val valueTextStyle: TextStyle = JarTypography.h6.copy(color = Color(0xFFACA1D3)),
    val labelTextStyle: TextStyle = JarTypography.body1.copy(color = Color.White),
    val valueIconLink: String? = null

)

@Composable
@Preview
fun LabelAndValueComposeViewPreview() {

    LabelValueComposeView(Modifier,
        listOf(
            LabelAndValueCompose("Label", "Value"),
            LabelAndValueCompose("Label", "Value"),
            LabelAndValueCompose(
                "Label",
                "12387123876765768",
                showCopyToClipBoardIconAndTruncate = true
            ),
        )
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LabelValueComposeView(modifier: Modifier = Modifier, list: List<LabelAndValueCompose>) {
    Column(modifier.fillMaxWidth()) {
        list.forEach {
            LabelValueComposeRowView(it)
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
@ExperimentalGlideComposeApi
private fun LabelValueComposeRowView(it: LabelAndValueCompose) {
    val finalValue = remember { if (it.showCopyToClipBoardIconAndTruncate) it.value.truncateAndAddDot(9) else it.value }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = it.label,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            style = it.labelTextStyle
        )
        Row(
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            it.valueIconLink?.let {valueIconLink->
                JarImage(
                    imageUrl = valueIconLink,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = finalValue,
                textAlign = TextAlign.End,
                style = it.valueTextStyle
            )
        }
        if (it.showCopyToClipBoardIconAndTruncate) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_copy),
                contentDescription = "Copy to clipboard",
                modifier = Modifier
                    .size(18.dp)
                    .debounceClickable {
                        Toast
                            .makeText(
                                context,
                                context.getString(com.jar.app.core_ui.R.string.core_ui_copied_to_clipboard),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                        it.value?.let {
                            clipboardManager.setText(AnnotatedString(it))
                        }
                    }
            )
        }

    }
}
