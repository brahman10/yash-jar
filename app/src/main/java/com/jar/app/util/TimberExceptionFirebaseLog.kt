package com.jar.app.util
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class TimberExceptionFirebaseLog : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val instance = FirebaseCrashlytics.getInstance()
        t?.let {
            instance.recordException(it)
        }
    }
}