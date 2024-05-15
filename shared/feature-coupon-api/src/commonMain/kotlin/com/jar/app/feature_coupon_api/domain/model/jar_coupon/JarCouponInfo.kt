package com.jar.app.feature_coupon_api.domain.model.jar_coupon


import com.jar.app.feature_coupon_api.domain.model.CouponCodeVariant
import com.jar.app.feature_coupon_api.domain.model.CouponType
import com.jar.app.feature_coupon_api.domain.model.OverlayType
import com.jar.app.feature_coupon_api.domain.model.brand_coupon.CouponState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JarCouponInfo(
    @SerialName("couponCode")
    val couponCode: String?= null,
    @SerialName("couponCodeId")
    val couponCodeId: String?= null,
    @SerialName("couponState")
    val couponState: String?= null,
    @SerialName("couponType")
    val couponType: String?= null,
    @SerialName("currentTimestamp")
    val currentTimestamp: Long?= null,
    @SerialName("description")
    val description: String?= null,
    @SerialName("endColor")
    val endColor: String?= null,
    @SerialName("expiry")
    val expiry: Long?= null,
    @SerialName("iconLink")
    val iconLink: String?= null,
    @SerialName("maxRewardAmount")
    val maxRewardAmount: Float,
    @SerialName("minimumAmount")
    val minimumAmount: Float?= null,
    @SerialName("overlayType")
    private val overlayType: String?= null,
    @SerialName("preApply")
    val preApply: Boolean?= null,
    @SerialName("rewardPercentage")
    val rewardPercentage: Float?= null,
    @SerialName("showOnHomeScreen")
    val showOnHomeScreen: Boolean?= null,
    @SerialName("startColor")
    val startColor: String?= null,
    @SerialName("title")
    val title: String?= null,
    @SerialName("trayOrder")
    val trayOrder: Int?= null,
    @SerialName("validity")
    val validity: Int?= null,
    private var formattedDescription: String?= null,
    @SerialName("couponCodeVariant")
    val couponCodeVariant:String = CouponCodeVariant.COUPON_VARIANT_TWO.name,
){

    fun getOverlayType(): String {
        return if (overlayType.isNullOrBlank())
            OverlayType.CIRCLE.name
        else OverlayType.valueOf(overlayType).name
    }

    fun getCouponType() = CouponType.values().find { it.name == couponType } ?: CouponType.COUPON

    fun getCouponState() = CouponState.values().find { it.name == couponState } ?: CouponState.INACTIVE

    fun getCouponDescription() = formattedDescription ?: description.orEmpty()
}