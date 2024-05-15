package com.jar.app.feature_gifting.impl.ui.gift_summary

import com.airbnb.epoxy.EpoxyController
import com.jar.app.feature_gifting.shared.domain.model.GiftSummary

class GiftSummaryController : EpoxyController() {

    var cards: MutableList<GiftSummary>? = null
        set(value) {
            field = value
            cancelPendingModelBuild()
            requestModelBuild()
        }

    override fun buildModels() {
        cards?.forEach {
            GoldSummaryEpoxyModel(it)
                .id(it.title.plus(it.value))
                .addTo(this)
        }
    }

}