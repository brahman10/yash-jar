package com.jar.app.feature_homepage.shared.data.db

import com.jar.app.feature_home.shared.HomePageDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(HomePageDatabase.Schema, "homepage.db")
    }

}