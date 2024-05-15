package com.jar.app.feature_in_app_stories.impl

import android.net.Uri
import androidx.navigation.NavController
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.encodeUrl
import com.jar.app.feature_in_app_stories.api.InAppStoriesApi
import dagger.Lazy
import javax.inject.Inject

class InAppStoriesApiImpl @Inject constructor(
    private val navControllerRef: Lazy<NavController>
) : InAppStoriesApi, BaseNavigation {
    private val navController by lazy {
        navControllerRef.get()
    }
    override fun openStoriesPage(storyId: String?) {
        val url = if (storyId == null) {
            "android-app://com.jar.app/stories"
        } else {
            "android-app://com.jar.app/stories/main/$storyId"
        }
        navController.navigate(
            Uri.parse(url),
            getAnimNavOptions()
        )
    }
}