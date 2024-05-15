package com.jar.app.core_ui.pull_to_refresh_overlay

interface PullToRefreshListener {
    fun onPulledToRefresh()
    fun onClickedSomewhereElse()
}