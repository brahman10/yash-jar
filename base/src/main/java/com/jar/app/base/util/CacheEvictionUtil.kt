package com.jar.app.base.util

import com.jar.app.base.data.event.RefreshMileStoneMetaEvent
import com.jar.app.base.data.event.RefreshSpinMetaDataEvent
import com.jar.app.base.data.event.RefreshUserGoldBalanceEvent
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheEvictionUtil @Inject constructor() {

    fun evictHomePageCache() {
        EventBus.getDefault().post(RefreshSpinMetaDataEvent())
        EventBus.getDefault().postSticky(RefreshMileStoneMetaEvent())
        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
    }

    fun refreshFirstCoinData() {
        EventBus.getDefault().postSticky(RefreshUserGoldBalanceEvent())
    }


}