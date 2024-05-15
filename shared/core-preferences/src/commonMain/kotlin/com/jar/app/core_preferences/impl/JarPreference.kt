package com.jar.app.core_preferences.impl

import com.jar.internal.library.jar_core_preferences.api.PreferenceApi
import kotlin.reflect.KClass

open class JarPreference(
    private val preferenceApi: PreferenceApi
) {

    protected suspend fun <T : Any> readNullable(param: String, returnType: KClass<T>): T? {
        val value = when (returnType) {
            String::class -> preferenceApi.getString(param)
            Boolean::class -> preferenceApi.getBoolean(param)
            Long::class -> preferenceApi.getLong(param)
            Int::class -> preferenceApi.getInt(param)
            Float::class -> preferenceApi.getFloat(param)
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
        @Suppress("UNCHECKED_CAST") return (value as T?)
    }

    protected suspend fun <T : Any> readNonNullable(
        param: String,
        defaultValue: T,
        returnType: KClass<T>
    ): T {
        val value = when (returnType) {
            String::class -> preferenceApi.getString(param, defaultValue as String)
            Boolean::class -> preferenceApi.getBoolean(param, defaultValue as Boolean)
            Long::class -> preferenceApi.getLong(param, defaultValue as Long)
            Int::class -> preferenceApi.getInt(param, defaultValue as Int)
            Float::class -> preferenceApi.getFloat(param, defaultValue as Float)
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
        @Suppress("UNCHECKED_CAST") return (value as T)
    }

    protected suspend fun <T : Any> write(
        param: String, value: T, returnType: KClass<T>
    ) {
        when (returnType) {
            String::class -> preferenceApi.putString(param, value as String)
            Boolean::class -> preferenceApi.putBoolean(param, value as Boolean)
            Long::class -> preferenceApi.putLong(param, value as Long)
            Int::class -> preferenceApi.putInt(param, value as Int)
            Float::class -> preferenceApi.putFloat(param, value as Float)
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
    }

    protected suspend inline fun <reified T : Any> readNullable(param: String): T? =
        readNullable(param, T::class)

    protected suspend inline fun <reified T : Any> readNonNullable(
        param: String,
        defaultValue: T
    ): T = readNonNullable(param, defaultValue, T::class)

    protected suspend inline fun <reified T : Any> write(
        param: String, value: T
    ) = write(param, value, T::class)

    protected fun <T : Any> readNullableSync(param: String, returnType: KClass<T>): T? {
        val value = when (returnType) {
            String::class -> preferenceApi.getStringSync(param)
            Boolean::class -> preferenceApi.getBooleanSync(param)
            Long::class -> preferenceApi.getLongSync(param)
            Int::class -> preferenceApi.getIntSync(param)
            Float::class -> preferenceApi.getFloatSync(param)
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
        @Suppress("UNCHECKED_CAST") return (value as T?)
    }

    protected fun <T : Any> readNonNullableSync(
        param: String,
        defaultValue: T,
        returnType: KClass<T>
    ): T {
        val value = when (returnType) {
            String::class -> preferenceApi.getStringSync(param, defaultValue as String)
            Boolean::class -> preferenceApi.getBooleanSync(param, defaultValue as Boolean)
            Long::class -> preferenceApi.getLongSync(param, defaultValue as Long)
            Int::class -> preferenceApi.getIntSync(param, defaultValue as Int)
            Float::class -> preferenceApi.getFloatSync(param, defaultValue as Float)
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
        @Suppress("UNCHECKED_CAST") return (value as T)
    }

    protected fun <T : Any> writeSync(
        param: String, value: T, returnType: KClass<T>
    ) {
        when (returnType) {
            String::class -> preferenceApi.putStringSync(param, value as String)
            Boolean::class -> preferenceApi.putBooleanSync(param, value as Boolean)
            Long::class -> preferenceApi.putLongSync(param, value as Long)
            Int::class -> preferenceApi.putIntSync(param, value as Int)
            Float::class -> preferenceApi.putFloatSync(param, value as Float)
            else -> {
                throw IllegalArgumentException("Unsupported cast")
            }
        }
    }

    protected inline fun <reified T : Any> readNullableSync(param: String): T? =
        readNullableSync(param, T::class)

    protected inline fun <reified T : Any> readNonNullableSync(
        param: String,
        defaultValue: T
    ): T = readNonNullableSync(param, defaultValue, T::class)

    protected inline fun <reified T : Any> writeSync(
        param: String, value: T
    ) = writeSync(param, value, T::class)

    protected fun clearAllSync(){
        preferenceApi.clearAllSync()
    }

}