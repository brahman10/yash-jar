package com.jar.app.feature_homepage.shared.data.db

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}