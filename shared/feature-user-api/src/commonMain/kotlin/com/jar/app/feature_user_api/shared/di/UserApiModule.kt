package com.jar.app.feature_user_api.shared.di

import com.jar.app.feature_user_api.data.db.DatabaseDriverFactory
import com.jar.app.feature_user_api.data.db.UserMetaLocalDataSource
import com.jar.app.feature_user_api.shared.UserMetaDatabase

class UserApiModule(databaseDriverFactory: DatabaseDriverFactory) {

    private val driver by lazy {
        databaseDriverFactory.createDriver()
    }

    private val database: UserMetaDatabase by lazy {
        UserMetaDatabase(driver)
    }

    val provideUserMetaLocalDataSource: UserMetaLocalDataSource by lazy {
        UserMetaLocalDataSource(database)
    }

}