package com.jar.app.feature_homepage.impl.ui.detected_spends_info.steps

import com.airbnb.epoxy.EpoxyController
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendPaymentInfoStep

internal class DetectedSpendStepEpoxyController : EpoxyController() {

    var steps: List<DetectedSpendPaymentInfoStep>? = null
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        steps?.forEach {
            DetectedSpendInfoEpoxyModel(it).id(it.title).addTo(this)
        }
    }
}