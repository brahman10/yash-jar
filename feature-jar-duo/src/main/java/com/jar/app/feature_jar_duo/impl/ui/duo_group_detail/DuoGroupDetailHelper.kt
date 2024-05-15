package com.jar.app.feature_jar_duo.impl.ui.duo_group_detail

import com.jar.app.feature_jar_duo.shared.util.DuoEventKey

object DuoGroupDetailHelper {

    internal fun constructHashMapForAnalytics(
        firstKey: String,
        status: Boolean? = null
    ): HashMap<String, String> {
        return hashMapOf(
            DuoEventKey.Button to firstKey
        ).apply {
            if (status != null) this.put(
                DuoEventKey.InitialStatus,
                status.toString()
            )
        }
    }
}