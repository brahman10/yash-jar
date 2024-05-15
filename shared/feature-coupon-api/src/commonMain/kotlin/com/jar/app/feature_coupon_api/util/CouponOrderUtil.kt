package com.jar.app.feature_coupon_api.util

import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_coupon_api.domain.model.CouponCode
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState

class CouponOrderUtil {

    private var previousBestCoupon: CouponCode? = null

    fun sortCouponListOrder(
        couponCodeList: MutableList<CouponCode>,
        amount: Float,
        shouldInactiveAllCoupons: Boolean = false
    ): List<CouponCode> {
        if (shouldInactiveAllCoupons) {
            val newInactiveList = couponCodeList.map {
                it.copy(
                    isBestCoupon = false,
                    isCouponAmountEligible = false
                )
            }
            return newInactiveList
        }
        previousBestCoupon = couponCodeList.find { it.isBestCoupon }
        val couponCodeListWithMaxReward = sortMainListAnCalculateMaxReward(couponCodeList, amount).toMutableList()

        if (amount == 0.0f) {
            return couponCodeListWithMaxReward
        }

        sortAllCouponsWithBestCoupon(couponCodeListWithMaxReward, amount)?.let {
            return it
        } ?: kotlin.run {
            return couponCodeListWithMaxReward
        }
    }

    fun shouldUpdateBestCoupon(
        couponCodeList: MutableList<CouponCode>,
        amount: Float
    ): Boolean {
        val sortedList = sortCouponListOrder(couponCodeList, amount)
        val bestCouponInSortedList = sortedList.find { it.isBestCoupon }
        val bestCouponInOldList = couponCodeList.find { it.isBestCoupon }

        bestCouponInSortedList?.let { bestInSorted ->
            bestCouponInOldList?.let { bestInOld ->
                return bestInSorted.couponCode != bestInOld.couponCode || amount < bestInSorted.minimumAmount
            } ?: kotlin.run {
                return bestInSorted.couponCode != previousBestCoupon?.couponCode.orEmpty() || amount < bestInSorted.minimumAmount
            }
        } ?: kotlin.run {
            return true
        }
    }

    private fun sortMainListAnCalculateMaxReward(couponCodeList: MutableList<CouponCode>, amount: Float): List<CouponCode> {
        val newList = couponCodeList.map {
            //Set isBestCoupon to false as this object may be used to compare with the mainList where isBestCoupon is set to false by default
            if (it.couponCode == previousBestCoupon?.couponCode) {
                previousBestCoupon?.isBestCoupon = false
                previousBestCoupon?.isCouponAmountEligible = amount >= it.minimumAmount
                previousBestCoupon?.setMaxRewardInCouponDescription(amount)
            }
            it.setMaxRewardInCouponDescription(amount)
            it.copy(
                isBestCoupon = false,
                isCouponAmountEligible = amount >= it.minimumAmount
            )
        }

        newList.sortedBy { it.minimumAmount }
        //Send all inactive coupons to end
        val (activeList, inActiveList) = newList.partition { it.getCouponState() == CouponState.ACTIVE && it.isCouponAmountEligible }
        newList.toMutableList().clear()
        newList.toMutableList().addAll(activeList)
        newList.toMutableList().addAll(inActiveList)

        return newList
    }

    private fun sortAllCouponsWithBestCoupon(
        couponCodeList: MutableList<CouponCode>,
        amount: Float
    ): List<CouponCode>? {
        // Check for all coupons eligible for input amount, check for coupon with best returns, swap to 1st position and return the list
        val listOfEligibleCoupons: MutableList<CouponCode> = ArrayList()
        couponCodeList.forEach {
            if (it.getCouponState() == CouponState.ACTIVE && it.isCouponAmountEligible) {
                listOfEligibleCoupons.add(it)
            }
        }
        return if (listOfEligibleCoupons.isEmpty().not()) {
            listOfEligibleCoupons.sortByDescending { it.getMaxRewardThatCanBeAvailed(buyAmount = amount) }
            val sortedList: List<CouponCode> = listOfEligibleCoupons + couponCodeList.filter { !listOfEligibleCoupons.contains(it) }
            if (sortedList.isEmpty().not()) {
                swapBestCouponToFirstPosition(sortedList.toMutableList(), amount)
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun swapBestCouponToFirstPosition(
        couponCodeList: MutableList<CouponCode>,
        amount: Float
    ): List<CouponCode> {
        //list[0] is best coupon after sorting
        //Use previous coupon for best coupon if max reward is same for the new coupon
        var bestCoupon = if (couponCodeList[0].getMaxRewardThatCanBeAvailed(amount) == previousBestCoupon?.getMaxRewardThatCanBeAvailed(amount) &&  previousBestCoupon?.isCouponAmountEligible.orFalse()) previousBestCoupon else couponCodeList[0]

        //If best coupon is not winning check if winnings have same reward as best coupon
        //If yes using winnings as best coupon
        if (bestCoupon?.getCouponType() != CouponType.WINNINGS) {
            bestCoupon = couponCodeList.find {
                it.getCouponType() == CouponType.WINNINGS
                        && bestCoupon?.getMaxRewardThatCanBeAvailed(amount) == it.getMaxRewardThatCanBeAvailed(amount)
                        && it.getCouponState() == CouponState.ACTIVE
            } ?: bestCoupon
        }

        val indexOfBestCoupon = couponCodeList.indexOf(bestCoupon)
        if (indexOfBestCoupon != -1) {
            bestCoupon?.let {
                it.isBestCoupon = it.isCouponAmountEligible
                couponCodeList.removeAt(indexOfBestCoupon)
                couponCodeList.add(0, it)
            }
        }

        return couponCodeList
    }
}