package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_ui.R


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun DropDownStatesPreview() {
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    DropDownMenu(options) { }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownMenu(options: List<String>, onStatesDropDownClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorResource(R.color.color_3C3357),
                shape = RoundedCornerShape(12.dp)
            ).debounceClickable {
                onStatesDropDownClick()
            },
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            trailingIcon = {
                IconButton(onClick = {  }, modifier = Modifier.clearAndSetSemantics { }) {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        "Trailing icon for exposed dropdown menu",
                        Modifier.rotate(
                            if (expanded)
                                180f
                            else
                                360f
                        ),
                        tint = colorResource(id = R.color.color_ACA1D3)
                    )
                }
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(textColor = colorResource(id = R.color.color_ACA1D3))
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}