package com.jar.app.core_base.util

import kotlinx.serialization.json.JsonPrimitive

fun String.toJsonElement() = JsonPrimitive(this)
fun Int.toJsonElement() = JsonPrimitive(this)
fun Float.toJsonElement() = JsonPrimitive(this)
fun Boolean.toJsonElement() = JsonPrimitive(this)