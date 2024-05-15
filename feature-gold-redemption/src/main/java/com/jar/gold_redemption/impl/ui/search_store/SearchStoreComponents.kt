package com.jar.gold_redemption.impl.ui.search_store

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_gold_redemption.R
import com.jar.gold_redemption.impl.ui.search_store.components.RenderSearchField
import com.jar.gold_redemption.impl.ui.search_store.model.StateListData


@Composable
internal fun RenderMainSearchStoreBottomSheet(
    searchStoreViewModel: SearchStoreViewModel,
    isSearchBottomSheetOpen: State<Boolean>,
    analyticsFunction: (it: String, map: Map<String, String>) -> Unit,
    function: () -> Unit,
) {
    val statesList = searchStoreViewModel.statesList.toList()
    val searchText = remember { mutableStateOf<String>("") }
    val cityMode = searchStoreViewModel.currentStateName.observeAsState()
    val listHeaderText = if (!searchText.value.isNullOrBlank()) {
        stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_showing_results_for, searchText.value)
    }  else if (cityMode.value.isNullOrBlank()) {
        stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.all_states)
    } else {
        pluralStringResource(
            id = com.jar.app.feature_gold_redemption.shared.R.plurals.feature_gold_redemption_showing_cities,
            count = statesList.size,
            statesList.size
        )
    }

    BackHandler(isSearchBottomSheetOpen.value) {
        if (!searchText.value.isNullOrBlank()) {
            searchText.value = ""
        } else if (!cityMode.value.isNullOrBlank()) {
            searchStoreViewModel.clearStateName()
        } else {
            function()
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)) {
        Row (Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            cityMode.value?.let {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    painter = painterResource(id = com.jar.app.core_ui.R.drawable.ic_arrow_back),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.debounceClickable {
                        searchStoreViewModel.clearStateName()
                    }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = (cityMode.value ?: stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.select_state)), style = JarTypography.h6, color = colorResource(
                id = com.jar.app.core_ui.R.color.white
            ), fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_close),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.debounceClickable {
                    function()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        if (cityMode.value.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
            RenderSearchField(searchText, analyticsFunction)
        }
        Text(text = listHeaderText, style = JarTypography.body1, color = colorResource(
            id = com.jar.app.core_ui.R.color.color_776E94
        ), modifier = Modifier.padding(start = 16.dp, top = 16.dp))
        Spacer(modifier = Modifier.height(20.dp))
        RenderMainList(
            statesList,
            cityMode,
            searchStoreViewModel,
            searchText,
            analyticsFunction
        )
    }

}

@Composable
internal fun RenderMainList(
    statesList: List<StateListData>,
    cityMode: State<String?>,
    searchStoreViewModel: SearchStoreViewModel,
    searchText: MutableState<String>,
    analyticsFunction: (it: String, map: Map<String, String>) -> Unit
) {
    val offsets = remember { mutableStateMapOf<Int, Float>() }
    val selectedHeaderIndex = remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val filteredList = statesList.filter {
        when (it) {
            is StateListData.StateHeader -> {
                it.title.startsWith(searchText.value, true)
            }
            is StateListData.StateCityNonExpanded -> {
                it.cityName.startsWith(searchText.value, true)
            }
        }
    }
    val finalList = if (searchText.value.isBlank()) statesList else filteredList

    Row(Modifier.fillMaxWidth()) {
        if (finalList.isNullOrEmpty()) {
            RenderEmptySection(searchText.value)
        } else {
            this.SearchStoreList(
                finalList,
                listState,
                cityMode,
                searchText,
                searchStoreViewModel,
                offsets,
                selectedHeaderIndex,
                statesList,
                analyticsFunction
            )
        }
    }
}

@Composable
fun RenderEmptySection(value: String) {
    Column(Modifier.fillMaxSize()) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(0.3f)
        )
        Text(
            stringResource(
                id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_no_search_results_state, value
            ),
            style = JarTypography.h6,
            fontSize = 22.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
        )
    }
}

fun generateKey(it: StateListData): String {
    return when (it) {
        is StateListData.StateHeader -> {
            it.title
        }
        is StateListData.StateCityNonExpanded -> {
            it.cityName
        }
    }
}


@Composable
fun RenderStoreName(title: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
            .padding(12.dp)) {
        Text(text = title, color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3), modifier = Modifier.padding(start = 4.dp))
    }
}


@Composable
@Preview
fun RenderMainListPreview() {
}