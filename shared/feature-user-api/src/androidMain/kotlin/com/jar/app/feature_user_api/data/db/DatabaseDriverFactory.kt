package com.jar.app.feature_user_api.data.db

import android.content.Context
import com.jar.app.feature_user_api.shared.UserMetaDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(UserMetaDatabase.Schema, context, "usermetadata.db")
    }

}