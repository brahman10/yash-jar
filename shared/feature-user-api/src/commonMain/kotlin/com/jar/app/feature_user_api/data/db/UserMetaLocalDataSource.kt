package com.jar.app.feature_user_api.data.db

import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.toLong
import com.jar.app.feature_user_api.domain.mappers.toUserMetaData
import com.jar.app.feature_user_api.domain.model.UserMetaData
import com.jar.app.feature_user_api.shared.UserMetaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class UserMetaLocalDataSource(database: UserMetaDatabase) {
    private val dbQuery = database.userMetaDatabaseQueries

    suspend fun insertUserMetaData(userMetaData: UserMetaData) = withContext(Dispatchers.IO) {
        dbQuery.insertUserMetaData(
            id = null,
            referAndEarnDescription = userMetaData.referAndEarnDescription,
            referralEarnings = userMetaData.referralEarnings,
            notificationCount = userMetaData.notificationCount?.toLong(),
            popupType = userMetaData.popupType,
            pendingGoldGift = userMetaData.pendingGoldGift?.toLong(),
            creditCardShow = userMetaData.creditCardShow?.toLong(),
            shouldShowLoanCard = userMetaData.shouldShowLoanCard?.toLong(),
            showVasooliCard = userMetaData.showVasooliCard?.toLong(),
            showDuoCard = userMetaData.showDuoCard?.toLong()
        )
    }

    suspend fun deleteUserMetaData() = withContext(Dispatchers.IO) {
        dbQuery.deleteUserMetaData()
    }

    suspend fun deleteAndInsertUserMetaData(userMetaData: UserMetaData) =
        withContext(Dispatchers.IO) {
            dbQuery.transaction {
                dbQuery.deleteUserMetaData()
                dbQuery.insertUserMetaData(
                    id = null,
                    referAndEarnDescription = userMetaData.referAndEarnDescription,
                    referralEarnings = userMetaData.referralEarnings,
                    notificationCount = userMetaData.notificationCount?.toLong(),
                    popupType = userMetaData.popupType,
                    pendingGoldGift = userMetaData.pendingGoldGift?.toLong(),
                    creditCardShow = userMetaData.creditCardShow?.toLong(),
                    shouldShowLoanCard = userMetaData.shouldShowLoanCard?.toLong(),
                    showVasooliCard = userMetaData.showVasooliCard?.toLong(),
                    showDuoCard = userMetaData.showDuoCard?.toLong()
                )
            }
        }

    suspend fun fetchUserMetaDataRowCount(): Long = withContext(Dispatchers.IO) {
        dbQuery.fetchUserMetaDataRowCount().executeAsOneOrNull().orZero()
    }

    suspend fun fetchUserMetaData(): UserMetaData? = withContext(Dispatchers.IO) {
        dbQuery.fetchUserMetaData().executeAsOneOrNull()?.toUserMetaData()
    }

    suspend fun updateNotificationMetaData(notificationCount: Long?) = withContext(Dispatchers.IO) {
        dbQuery.updateNotificationMetaData(notificationCount = notificationCount)
    }
}