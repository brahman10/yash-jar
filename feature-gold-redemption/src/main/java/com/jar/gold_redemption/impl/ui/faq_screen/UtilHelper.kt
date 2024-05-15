package com.jar.gold_redemption.impl.ui.faq_screen

import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.feature_gold_redemption.shared.data.network.model.GenericFAQs


fun curateToExpandableList(genericFAQs: GenericFAQs): List<ExpandableCardModel> {
    return genericFAQs?.genericFAQs?.mapIndexedNotNull { index, genericFAQ ->
        if (genericFAQ?.question.isNullOrBlank() || genericFAQ?.answer.isNullOrBlank()) null
        else ExpandableCardModel(index, genericFAQ?.question.orEmpty(), genericFAQ?.answer.orEmpty())
    } ?: listOf()
}