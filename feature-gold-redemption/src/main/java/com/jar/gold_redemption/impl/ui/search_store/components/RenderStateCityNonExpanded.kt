package com.jar.gold_redemption.impl.ui.search_store.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jar.gold_redemption.impl.ui.search_store.RenderStoreName
import com.jar.gold_redemption.impl.ui.search_store.model.StateListData

@Composable
internal fun RenderStateCityNonExpanded(
    it: StateListData.StateCityNonExpanded,
    function: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        RenderStateHeader(it = it.cityName, it.noStores, null, childCount = it.storesList?.size) {
            function()
        }
        it.storesList?.forEach {
            RenderStoreName(it.orEmpty())
        }
    }
}
