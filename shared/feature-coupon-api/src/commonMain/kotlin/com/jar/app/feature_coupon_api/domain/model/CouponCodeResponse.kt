package com.jar.app.feature_coupon_api.domain.model

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.util.roundDown
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import com.jar.app.feature_coupon_api.util.CouponConstants
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class CouponCodeResponse(
    @SerialName("couponCodes")
    val couponCodes: List<CouponCode>? = null,

    @SerialName("headerCouponCode")
    val headerCouponCode: CouponCode? = null
)

enum class CouponCodeVariant{
    COUPON_VARIANT_ONE,
    COUPON_VARIANT_TWO
}

@Serializable
data class CouponCode(
    @SerialName("couponCodeId")
    val couponCodeId: String? = null,

    @SerialName("couponCode")
    val couponCode: String,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    var description: String? = null,

    @SerialName("iconLink")
    val iconLink: String? = null,

    @SerialName("expiry")
    var validityInMillis: Long? = null,

    @SerialName("currentTimestamp")
    var currentTimestamp: Long? = null,

    @SerialName("minimumAmount")
    val minimumAmount: Float,

    @SerialName("preApply")
    val preApply: Boolean? = null,

    @SerialName("startColor")
    val startColor: String? = null,

    @SerialName("endColor")
    val endColor: String? = null,

    @SerialName("overlayType")
    private val overlayType: String? = null,

    @SerialName("fixedDiscount")
    val fixedDiscount: Float? = null,

    @SerialName("showOnHomeScreen")
    val showOnHomeScreen: Boolean? = null,

    @SerialName("couponType")
    val couponType: String,

    @SerialName("couponState")
    var couponState: String,

    @SerialName("couponsLeftText")
    var couponsLeftText: String? = null,

    @SerialName("maxRewardAmount")
    val maxAmount: Float? = null,

    @SerialName("rewardPercentage")
    val rewardPercentage: Float? = null,

    @SerialName("couponCodeDetails")
    val couponCodeDetails: CouponCodeDetails? = null,

    @SerialName("couponCodeVariant")
    val couponCodeVariant:String = CouponCodeVariant.COUPON_VARIANT_ONE.name,

    //Fields for UI
    var isSelected: Boolean = false,
    var isBestCoupon: Boolean = false,
    var couponAppliedDescription: String? = null,
    var couponNotEligibleDescription: String? = null,
    var isCouponAmountEligible: Boolean = false,
    private var formattedDescription: String? = null
) {
    fun getOverlayType(): String {
        return if (overlayType.isNullOrBlank())
            OverlayType.CIRCLE.name
        else OverlayType.valueOf(overlayType).name
    }

    fun getCouponType() = CouponType.values().find { it.name == couponType } ?: CouponType.COUPON

    fun getCouponState() = CouponState.values().find { it.name == couponState } ?: CouponState.INACTIVE

    fun getMaxRewardThatCanBeAvailed(buyAmount: Float) = min(((rewardPercentage.orZero()/100f)*buyAmount), maxAmount.orZero()).roundDown(2)

    fun setMaxRewardInCouponDescription(buyAmount: Float? = null) {
        val amount = buyAmount.takeIf { buyAmount.orZero() != 0.0f } ?: minimumAmount.toFloat()
        formattedDescription = description.orEmpty().replace(CouponConstants.MAX_REWARD_TEMPLATE_FOR_COUPON_DESCRIPTION, "₹${getMaxRewardThatCanBeAvailed(amount)}")
    }

    fun getCouponDescription() = formattedDescription ?: description.orEmpty()

    fun getCardBackgroundGradient(): List<String> {
        val gradientStartColor = startColor ?: "color_352F4F"
        val gradientEndColor = endColor ?: "color_352F4F"
        return listOf(gradientStartColor, gradientEndColor)
    }

    fun getBuyGoldPreApplyCouponDeeplink(buyGoldFlowContext: String) =
        BaseConstants.BASE_EXTERNAL_DEEPLINK + BaseConstants.ExternalDeepLinks.BUY_GOLD +
                "/$buyGoldFlowContext/${couponCode}/${couponType}"
}

@Parcelize
@Serializable
data class CouponCodeDetails(
    @SerialName("title")
    val title: String,

    @SerialName("pointsList")
    val pointsList: List<String>,
):Parcelable

@Parcelize
@Serializable
data class ApplyCouponCodeResponse(
    @SerialName("isValid")
    val isValid: Boolean,

    @SerialName("couponCode")
    val couponCode: String,

    @SerialName("couponCodeId")
    val couponCodeId: String? = null,

    @SerialName("title")
    val title: String,

    @SerialName("amount")
    val amount: Float,

    @SerialName("expiry")
    val validity: Long? = null,

    @SerialName("currentTimestamp")
    var currentTimestamp: Long? = null,

    @SerialName("offerAmount")
    val offerAmount: Float,

    @SerialName("offerVolume")
    val offerVolume: Float? = null,

    @SerialName("rewardType")
    val rewardType: String? = null,

    @SerialName("payableAmount")
    val payableAmount: Float? = null,

    @SerialName("couponCodeDesc")
    val couponCodeDesc: String? = null,

    @SerialName("couponType")
    val couponType: String,

    @SerialName("couponState")
    val couponState: String,

    @SerialName("rewardPercentage")
    val rewardPercentage: Float? = null,

    @SerialName("maxRewardAmount")
    val maxAmount: Float? = null,

    //for events
    @SerialName("isManuallyEntered")
    val isManuallyEntered:Boolean = false,

    @SerialName("screenName")
    val screenName: String? = null
) : Parcelable

enum class OverlayType {
    CIRCLE, BACKWARD_INCLINE
}

enum class CouponType {
    WINNINGS,
    COUPON,
    JACKPOT,
    REFERRAL;
    companion object {
        fun getCouponType(typeString: String): CouponType {
            return when(typeString) {
                "WINNINGS" -> CouponType.WINNINGS
                "COUPON" -> CouponType.COUPON
                "JACKPOT"-> CouponType.JACKPOT
                "REFERRAL"-> CouponType.REFERRAL
                else -> CouponType.COUPON
            }
        }

        fun getCouponBtnName(typeString: String): String {
            return when(typeString) {
                "WINNINGS" -> "USE"
                else -> "APPLY"
            }
        }
    }
}

// removed coupon state  CouponState