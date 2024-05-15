package com.jar.app.feature_homepage.shared.data.db

import com.jar.app.feature_home.shared.HomePageDatabase
import com.jar.app.featurehomepage.shared.HomeFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class HomeFeedLocalDataSource(database: HomePageDatabase) {

    private val dbQuery = database.homePageDatabaseQueries

    suspend fun fetchHomeFeedData(key: String): HomeFeed? = withContext(Dispatchers.IO) {
        dbQuery.fetchHomeFeedData(key).executeAsOneOrNull()
    }

    suspend fun insertHomeFeed(key: String, feed: String) = withContext(Dispatchers.IO) {
        dbQuery.insertHomeFeed(key = key, value_ = feed)
    }

    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        dbQuery.transaction {
            dbQuery.removeAllHomeFeed()
        }
    }
}