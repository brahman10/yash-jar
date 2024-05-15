package com.jar.gold_redemption.impl.ui.search_store.model

sealed class StateListData {
    internal data class StateHeader(val title: String, val count: Int) : StateListData()
    internal data class StateCityNonExpanded(
        val cityName: String,
        val noStores: Int,
        var storesList: List<String?>? = null
    ) : StateListData()
}