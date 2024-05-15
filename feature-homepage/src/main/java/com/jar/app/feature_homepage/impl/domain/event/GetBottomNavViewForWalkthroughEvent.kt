package com.jar.app.feature_homepage.impl.domain.event

import com.jar.app.feature_homepage.shared.domain.model.Tab

data class GetBottomNavViewForWalkthroughEvent(val tab: Tab, val title: String)