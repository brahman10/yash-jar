package com.jar.app.core_utils.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRatingUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var reviewInfo: ReviewInfo? = null
    val manager = ReviewManagerFactory.create(context)

    init {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                //Received ReviewInfo object
                reviewInfo = request.result
            } else {
                //Problem in receiving object
                reviewInfo = null
            }
        }
    }

    fun openRatingDialog(activity: FragmentActivity) {
        reviewInfo?.let {
            val flow = manager.launchReviewFlow(activity, it)
            flow.addOnCompleteListener {
                //Irrespective of the result, the app flow should continue
            }
        }
    }
}