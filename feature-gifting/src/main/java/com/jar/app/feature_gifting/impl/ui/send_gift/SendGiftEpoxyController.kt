package com.jar.app.feature_gifting.impl.ui.send_gift

import com.airbnb.epoxy.EpoxyController
import com.jar.app.feature_gifting.shared.domain.model.AmountAndMessageDetail
import com.jar.app.feature_gifting.shared.domain.model.GiftView
import com.jar.app.feature_gifting.shared.domain.model.Question
import com.jar.app.feature_gifting.shared.domain.model.ReceiverDetail
import com.jar.app.feature_gifting.impl.epoxy_model.AmountAndMessageEpoxyModel
import com.jar.app.feature_gifting.impl.epoxy_model.QuestionEpoxyModel
import com.jar.app.feature_gifting.impl.epoxy_model.ReceiverDetailEpoxyModel

internal class SendGiftEpoxyController(
    private val onEditNumberClick: () -> Unit,
    private val onEditAmountClick: () -> Unit,
    private val onEditMessageClick: (message: String?) -> Unit
) : EpoxyController() {

    var cards: List<GiftView>? = null
        set(value) {
            field = value
            cancelPendingModelBuild()
            requestModelBuild()
        }

    override fun buildModels() {
        cards?.forEach {
            when (it) {
                is Question -> {
                    QuestionEpoxyModel(it)
                        .id(it.text.plus(it.questionOrder))
                        .addTo(this)
                }
                is ReceiverDetail -> {
                    ReceiverDetailEpoxyModel(it, onEditNumberClick)
                        .id(it.number.plus(it.name))
                        .addTo(this)
                }
                is AmountAndMessageDetail -> {
                    AmountAndMessageEpoxyModel(it, onEditAmountClick, onEditMessageClick)
                        .id(it.amountInRupees.toString().plus(it.volumeInGm).plus(it.message))
                        .addTo(this)
                }
            }
        }
    }
}