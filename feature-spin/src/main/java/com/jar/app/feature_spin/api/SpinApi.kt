package com.jar.app.feature_spin.api

import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType

/**
 * Spin Api (to be used by other modules)
 * **/
interface SpinApi {

    /**
     * Method to start Spin Fragment
     * **/
    fun openSpinFragmentV2(context: SpinsContextFlowType, backstackId: String? = null)
}