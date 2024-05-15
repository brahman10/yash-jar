package com.jar.app.core_base.domain.model.card_library

data class CardEventData(
    val map: MutableMap<String, String>,
    val fromSection: String? = null,
    val fromCard: String? = null
)