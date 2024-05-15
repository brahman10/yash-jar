package com.jar.app.core_base.util

import com.jar.app.core_base.shared.CoreBaseBuildKonfig
import com.jar.app.core_base.shared.CoreBaseMR
import dev.icerock.moko.resources.FileResource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject

class RemoteConfigDefaultsHelper constructor(
    private val json: Json
) {
    fun getRemoteConfigDefaultsFile(): FileResource {
        return when (CoreBaseBuildKonfig.ENV) {
            "staging" -> CoreBaseMR.files.remote_config_defaults_staging
            "prodReplica" -> CoreBaseMR.files.remote_config_defaults_prodReplica
            "prod" -> CoreBaseMR.files.remote_config_defaults_prod
            else -> CoreBaseMR.files.remote_config_defaults_prod
        }
    }

    fun convertToList(jsonString: String): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        (json.parseToJsonElement(jsonString) as? JsonArray)
            ?.forEach {
                val key = it.jsonObject["key"]?.toString()?.replace("^\"|\"$".toRegex(), "")!!
                val value = it.jsonObject["value"]?.toString()?.replace("^\"|\"$".toRegex(), "")!!
                list.add(key to value)
            }
        return list
    }
}