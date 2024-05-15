package com.jar.app.feature_sell_gold.impl.ui.vpa.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_2E2942
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_58DDC8
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_58DDC81A
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_7745FF
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_776E94
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_AB8CFF
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_D5CDF2
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R.drawable
import com.jar.app.feature_sell_gold.shared.MR.strings.add_new_upi_id
import com.jar.app.feature_sell_gold.shared.MR.strings.suggested
import com.jar.app.feature_sell_gold.shared.MR.strings.upi_deletion_message
import com.jar.app.feature_user_api.domain.model.SavedVPA

@Composable
fun UpiIdRadioGroup(
    upiIds: List<SavedVPA>,
    onVpaSelected: (SavedVPA) -> Unit
) {
    var selectedOption by remember(upiIds) { mutableStateOf(upiIds[0]) }
    LazyColumn(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(
            key = { it.id },
            items = upiIds.sortedByDescending { it.isPrimaryUpi },
            contentType = { it }
        ) { upiId ->
            UpiIdRowItem(
                savedVpa = upiId,
                selectedOption = selectedOption,
                onOptionSelected = {
                    selectedOption = it
                    onVpaSelected(it)
                }
            )
        }
    }
}

@Composable
private fun UpiIdRowItem(
    savedVpa: SavedVPA,
    selectedOption: SavedVPA,
    onOptionSelected: (SavedVPA) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(
                Modifier
                    .takeIf { savedVpa == selectedOption }
                    ?.border(
                        width = 1.dp,
                        color = colorResource(id = color_7745FF.resourceId),
                        shape = RoundedCornerShape(size = 10.dp)
                    ) ?: Modifier
            )
            .background(
                color = colorResource(id = color_2E2942.resourceId),
                shape = RoundedCornerShape(size = 10.dp)
            )
            .selectable(
                selected = (savedVpa == selectedOption),
                onClick = { onOptionSelected(savedVpa) },
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.RadioButton
            )
            .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (savedVpa == selectedOption),
            onClick = null,
            modifier = Modifier.size(20.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = colorResource(id = color_AB8CFF.resourceId)
            )
        )
        Text(
            text = savedVpa.vpaHandle,
            style = JarTypography.body2,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(start = 8.dp)
                .then(if (savedVpa.isPrimaryUpi == true) Modifier.width(194.dp) else Modifier)
        )

        if (savedVpa.isPrimaryUpi == true) {
            Spacer(modifier = Modifier.weight(1f))
            SuggestedLabel()
        }
    }
}

@Composable
private fun SuggestedLabel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = colorResource(id = color_58DDC81A.resourceId),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = stringResource(id = suggested.resourceId),
            style = JarTypography.caption,
            color = colorResource(id = color_58DDC8.resourceId)
        )
    }
}

@Composable
internal fun AddNewUpiIdButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(id = drawable.ic_add),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(colorResource(id = color_D5CDF2.resourceId))
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(id = add_new_upi_id.resourceId),
            style = JarTypography.body2,
            color = colorResource(id = color_D5CDF2.resourceId)
        )
    }
}

@Composable
fun UpiDeletionMessage() {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = stringResource(id = upi_deletion_message.resourceId),
        style = JarTypography.body2,
        color = colorResource(id = color_776E94.resourceId)
    )
}