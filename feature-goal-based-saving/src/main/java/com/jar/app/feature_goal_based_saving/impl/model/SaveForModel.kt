package com.jar.app.feature_goal_based_saving.impl.model

data class SaveForModel(
    val icon: Int,
    val iconWithShadow: Int,
    val title: String,
    var isSelected: Boolean = false
)