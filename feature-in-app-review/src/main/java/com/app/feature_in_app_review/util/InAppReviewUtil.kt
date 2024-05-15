package com.app.feature_in_app_review.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppReviewUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    fun showInAppReview(activity: Activity){
        val manager = ReviewManagerFactory.create(context)
        manager.requestReviewFlow().addOnCompleteListener {
            if(it.isSuccessful){
                analyticsHandler.postEvent("InAppRatingPopup_Shown")
                manager.launchReviewFlow(activity,it.result )
            }
        }
    }
}