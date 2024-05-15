package com.jar.app.feature_homepage.impl.domain.model

data class ScrollState(
    val scrolled: Boolean,  // Represent if the view is scrolled
    val offsetX: Int = 0,   // Horizontal Scroll Offset (for ScrollView or RecyclerView)
    val offsetY: Int = 0    // Vertical Scroll Offset (for ScrollView or RecyclerView)
)