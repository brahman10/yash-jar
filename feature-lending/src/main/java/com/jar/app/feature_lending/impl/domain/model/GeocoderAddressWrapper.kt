package com.jar.app.feature_lending.impl.domain.model

import android.location.Address

data class GeocoderAddressWrapper(
    val address: Address? = null,
    val errorMessage: String? = null
)