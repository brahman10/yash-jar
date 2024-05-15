package com.jar.app.core_ui.dynamic_cards.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface HomeFeedCard {

    var cardShownEventJob: Job?

    fun startShowEventJob(
        uiScope: CoroutineScope, isCardFullyVisible: () -> Boolean, onCardShownEvent: () -> Unit
    ) {
        cardShownEventJob?.cancel()
        cardShownEventJob = uiScope.launch {
            delay(2000)
            if (isCardFullyVisible.invoke()) onCardShownEvent.invoke()
        }
    }

}