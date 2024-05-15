package com.jar.app.core_network.util

import com.jar.internal.library.jar_core_network.api.util.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import org.json.JSONArray
import org.json.JSONObject

fun JSONObject.toJsonObject() = json.parseToJsonElement(this.toString())

fun JSONArray.toJsonArray() = json.parseToJsonElement(this.toString()).jsonArray

fun JsonObject.toJSONObject() = JSONObject(json.encodeToString(this))