package com.jar.app.feature_jar_duo.shared.domain.model.v2

@kotlinx.serialization.Serializable
data class DuoHeaderData(
    val headerTextResource:Int,
    val itemCount:Int?
)
