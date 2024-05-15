package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R

@Composable
public fun RenderHorizontalFilterList(
    modifier: Modifier = Modifier,
    list: List<String?>,
    defaultIndex: Int,
    bgColor: Int = R.color.color_272239,
    selectedIndexPass: State<Int?>? = null,
    function: (Int) -> Unit
) {
    val selectedIndex = selectedIndexPass ?: remember { mutableStateOf(defaultIndex) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(id = bgColor)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 20.dp)
    ) {
        itemsIndexed(list) { index, title ->
            if (selectedIndex.value == index) {
                SelectedCard(title)
            } else {
                UnSelectedCard(title) {
                    function(index)
                }
            }
        }
    }
}

@Composable
fun UnSelectedCard(title: String?, function: () -> Unit) {
    Text(
        text = title.orEmpty(), modifier = Modifier
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_272239),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .debounceClickable { function.invoke() },
        style = JarTypography.body1,
        color = Color.White
    )
}

@Composable
fun SelectedCard(title: String?) {
    Text(
        text = title ?: "", modifier = Modifier
            .background(
                colorResource(id = com.jar.app.core_ui.R.color.color_7745FF),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        style = JarTypography.h6,
        color = Color.White
    )
}

@Composable
@Preview(backgroundColor = 0xff272239)
fun RenderHorizontalFilterPreview() {
    RenderHorizontalFilterList(Modifier, listOf("All", "Gold Vouchers", "On Sale"), 0) {

    }
}
