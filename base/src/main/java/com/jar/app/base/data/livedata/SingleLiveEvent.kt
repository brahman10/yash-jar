package com.jar.app.base.data.livedata

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MediatorLiveData<T>() {
    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Timber.w("Multiple observers registered but only one will be notified of changes.")
        }

        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        pending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun call() {
        value = null
    }
}