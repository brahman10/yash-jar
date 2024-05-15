package com.jar.gold_redemption.impl.ui.search_store

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_redemption.shared.data.network.model.StateData
import com.jar.gold_redemption.impl.ui.search_store.model.StateListData

internal fun parseStatesList(list: List<StateData?>?): List<StateData?>? {
    val finalList = list?.toMutableList()
    return finalList?.sortedWith(Comparator { a, b ->
        when {
            a?.count == 0 -> 1
            b?.count == 0 -> -1
            else -> b?.count.orZero().compareTo(a?.count.orZero())
        }
    })
}
internal fun getStatesList(apiResponseWrapper: List<StateData?>?): List<StateListData> {
    return apiResponseWrapper?.map { StateListData.StateHeader(it?.name.orEmpty(), it?.count.orZero()) }
        ?: listOf()
}


fun addStoreInCitiesList(
    data: List<String?>,
    cityName: String,
    statesList: SnapshotStateList<StateListData>
) {
    val iterator = statesList.listIterator()
    while (iterator.hasNext()) {
        when (val next = iterator.next()) {
            is StateListData.StateCityNonExpanded -> {
                if (next.cityName == cityName) {
                    iterator.remove()
                    iterator.add(
                        StateListData.StateCityNonExpanded(
                            cityName = cityName,
                            noStores = next.noStores,
                            storesList = if (next.storesList.isNullOrEmpty()) data else null
                        )
                    )
                } else if (!next.storesList.isNullOrEmpty()) {
                    iterator.remove()
                    iterator.add(
                        StateListData.StateCityNonExpanded(
                            cityName = next.cityName,
                            noStores = next.noStores,
                            storesList = null
                        )
                    )
                }
            }

            is StateListData.StateHeader -> {
                // not possible
            }

        }
    }
}
