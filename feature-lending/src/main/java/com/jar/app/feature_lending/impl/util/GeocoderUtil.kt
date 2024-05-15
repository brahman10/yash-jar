package com.jar.app.feature_lending.impl.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.impl.domain.model.GeocoderAddressWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GeocoderUtil @Inject constructor(@ApplicationContext private val context: Context){

    fun getAddress(lat: Double, lon: Double): GeocoderAddressWrapper {
        var addressList = mutableListOf<Address>()
        var errorMessage = ""
        var addressItem: Address? = null
        try {
            addressList = Geocoder(context)
                .getFromLocation(lat, lon, 1) ?: mutableListOf()
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = context.getString(com.jar.app.core_ui.R.string.something_went_wrong)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = context.getString(com.jar.app.feature_lending.shared.MR.strings.feature_lending_unable_to_detect_address.resourceId)
        } catch (exception: Exception) {
            errorMessage = context.getString(com.jar.app.core_ui.R.string.something_went_wrong)
        }

        if(addressList.isEmpty()) {
            if(errorMessage.isEmpty()) {
                errorMessage = context.getString(com.jar.app.feature_lending.shared.MR.strings.feature_lending_unable_to_detect_address.resourceId)
            }
        } else {
            addressItem = addressList.first()
        }

        return GeocoderAddressWrapper(
            address = addressItem,
            errorMessage = errorMessage
        )
    }
}