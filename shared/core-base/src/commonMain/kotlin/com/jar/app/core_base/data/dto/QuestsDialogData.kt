package com.jar.app.core_base.data.dto

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class
QuestsDialogData(
    val title: String,
    val subtitle: String,
    val imagesList: List<String>,
    val primaryButtonText: String? = null,
    val secondaryButtonText: String,
    val context: String,
    val chancesLeft: Int?
): Parcelable

enum class QuestDialogContext {
    SPINS,
    TRIVIA
}