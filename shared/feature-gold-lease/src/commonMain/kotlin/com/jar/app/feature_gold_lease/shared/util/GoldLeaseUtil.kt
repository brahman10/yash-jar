package com.jar.app.feature_gold_lease.shared.util

import com.jar.app.core_base.util.roundDown

class GoldLeaseUtil {

    private fun getGoldYield(leasePercent: Float, goldVolume: Float, leaseNoOfDays: Int): Float {
        var totalYield = 0f
        for (i in 1..leaseNoOfDays) {
            //Yield for 1mg gold pledged for 1 day in mg
            val yieldForOneDayPerMg = (leasePercent/(360*100))

            //Yield for goldVolume (gms) for one day in mg
            val yieldInMg = goldVolume * 1000 * yieldForOneDayPerMg
            val yieldInGms = (yieldInMg/1000)

            totalYield += yieldInGms
        }
        return totalYield.roundDown(4)
    }

    fun getGoldYieldWithoutCommittedGold(leasePercent: Float, goldVolume: Float, leaseNoOfDays: Int): Float {
        return getGoldYield(
            leasePercent = leasePercent,
            goldVolume = goldVolume,
            leaseNoOfDays = leaseNoOfDays
        )
    }

    fun getGoldYieldWithCommittedGold(leasePercent: Float, goldVolume: Float, leaseNoOfDays: Int): Float {
        return goldVolume + getGoldYield(
            leasePercent = leasePercent,
            goldVolume = goldVolume,
            leaseNoOfDays = leaseNoOfDays
        )
    }

}