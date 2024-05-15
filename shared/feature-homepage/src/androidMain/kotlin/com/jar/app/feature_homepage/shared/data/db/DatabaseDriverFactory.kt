package com.jar.app.feature_homepage.shared.data.db

import android.content.Context
import com.jar.app.feature_home.shared.HomePageDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(HomePageDatabase.Schema, context, "homepage.db")
    }

}