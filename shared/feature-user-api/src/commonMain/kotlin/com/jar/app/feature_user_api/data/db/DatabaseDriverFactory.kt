package com.jar.app.feature_user_api.data.db

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}