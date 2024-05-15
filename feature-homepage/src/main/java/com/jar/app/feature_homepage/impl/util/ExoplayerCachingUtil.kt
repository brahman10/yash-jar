package com.jar.app.feature_homepage.impl.util

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoplayerCachingUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private lateinit var cache: SimpleCache
    private val cacheSize: Long = 90 * 1024 * 1024

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
        /*Ignore*/
    }

    private val exoplayerDatabaseProvider by lazy {
        StandaloneDatabaseProvider(context)
    }
    private val cacheEvictor by lazy {
        LeastRecentlyUsedCacheEvictor(cacheSize)
    }

    private val mHttpDataSourceFactory by lazy {
        DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
    }

    val mCacheDataSourceFactory by lazy {
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
    }

    init {
        cache = SimpleCache(File(context.cacheDir, "cache"), cacheEvictor, exoplayerDatabaseProvider)
    }

    fun cache(videoUrl: String, scope: CoroutineScope) {
        val videoUri = Uri.parse(videoUrl)
        val dataSpec = DataSpec(videoUri)

        scope.launch(coroutineExceptionHandler) {
            writeToCache(dataSpec)
        }
    }

    private suspend fun writeToCache(mDataSpec: DataSpec) {
        withContext(Dispatchers.IO) {
            CacheWriter(
                mCacheDataSourceFactory.createDataSource(),
                mDataSpec,
                null,
                null,
            ).cache()
        }
    }
}