package com.jar.app.feature_spin.impl.custom.component.models

import java.util.concurrent.atomic.AtomicBoolean

class Event<T>(private val data:T) {
    private val isAlreadyHandled = AtomicBoolean(false)

    fun getIfNotHandled(): T? {
        return if (isAlreadyHandled.get().not()) {
            isAlreadyHandled.set(true)
            data
        } else {
            null
        }
    }
}