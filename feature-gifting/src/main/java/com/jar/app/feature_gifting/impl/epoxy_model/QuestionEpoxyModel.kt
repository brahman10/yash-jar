package com.jar.app.feature_gifting.impl.epoxy_model

import com.jar.app.core_ui.dynamic_cards.base.ViewBindingKotlinModel
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.databinding.FeatureGiftingCellAskQuestionBinding
import com.jar.app.feature_gifting.shared.domain.model.Question

internal class QuestionEpoxyModel(private val question: Question) :
    ViewBindingKotlinModel<FeatureGiftingCellAskQuestionBinding>(R.layout.feature_gifting_cell_ask_question) {

    override fun FeatureGiftingCellAskQuestionBinding.bind() {
        tvQuestion.text = question.text
    }
}