package com.jar.gold_redemption.impl.ui.search_store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.views.AlphabeticalHeaderScroller
import com.jar.app.core_compose_ui.views.updateSelectedIndexIfNeeded
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.CITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.NO_OF_CITIES
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.NO_OF_STORES
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VStateBSCitySelected
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VStateBSStateSelected
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.STATE
import com.jar.gold_redemption.impl.ui.search_store.components.RenderStateCityNonExpanded
import com.jar.gold_redemption.impl.ui.search_store.components.RenderStateHeader
import com.jar.gold_redemption.impl.ui.search_store.model.StateListData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun RowScope.SearchStoreList(
    finalList: List<StateListData>,
    listState: LazyListState,
    cityMode: State<String?>,
    searchText: MutableState<String>,
    searchStoreViewModel: SearchStoreViewModel,
    offsets: SnapshotStateMap<Int, Float>,
    selectedHeaderIndex: MutableState<Int>,
    statesList: List<StateListData>,
    analyticsFunction: (it: String, map: Map<String, String>) -> Unit
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        Modifier
            .weight(1f)
            .background(colorResource(id = R.color.color_2E2942)),
        state = listState,
    ) {
        itemsIndexed(finalList) { index, it -> // todo , key = { generateKey(it) }
            when (it) {
                is StateListData.StateHeader -> {
                    cityMode
                    RenderStateHeader(it.title, it.count, searchText) {
                        if (it.count > 0) {
                            scope.launch {
                                delay(100L)
                                searchText.value = ""
                            }
                            searchStoreViewModel.fetchCities(it.title)
                            analyticsFunction(
                                Redemption_VStateBSStateSelected,
                                mapOf(
                                    STATE to it.title,
                                    NO_OF_CITIES to it.count.toString()
                                )
                            )
                        }
                    }
                    if (cityMode.value.isNullOrBlank()) {
                        // no-op
                    }
                }

                is StateListData.StateCityNonExpanded -> {
                    RenderStateCityNonExpanded(it) {
                        if (it.noStores > 0)
                            searchStoreViewModel.fetchStoreFromCity(it.cityName)
                        analyticsFunction(
                            Redemption_VStateBSCitySelected,
                            mapOf(
                                CITY to it.cityName,
                                NO_OF_STORES to it.noStores.orZero().toString()
                            )
                        )

                    }
                    if (cityMode.value.isNullOrBlank()) {
                        // no-op
                    }
                }
            }


            if (index < finalList.lastIndex)
                Divider(
                    color = colorResource(
                        id = R.color.color_3C3357,
                    ),
                    thickness = 1.dp
                )
        }
    }

    if (!cityMode.value.isNullOrBlank()) {
    AlphabeticalHeaderScroller(
        offsets,
    ) {
        updateSelectedIndexIfNeeded(
            it,
            scope,
            offsets,
            listState,
            selectedHeaderIndex
        ) {
            statesList.indexOfFirst { item ->
                when (item) {
                    is StateListData.StateCityNonExpanded -> {
                        item.cityName.firstOrNull()?.uppercase() == it
                    }

                    is StateListData.StateHeader -> {
                        item.title.firstOrNull()?.uppercase() == it
                    }
                }
            }
        }
    }
    }
}