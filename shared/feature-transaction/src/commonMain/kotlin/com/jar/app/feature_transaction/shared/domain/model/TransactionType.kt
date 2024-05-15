package com.jar.app.feature_transaction.shared.domain.model

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
enum class TransactionType: Parcelable {
    INVESTMENTS,
    GOLD_GIFT,
    WINNINGS,
    WITHDRAWALS,
    PARTNERSHIPS,
    NONE,
    GOLD
}