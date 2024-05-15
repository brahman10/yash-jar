package com.jar.app.feature_lending.shared.domain.model.camps_flow

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CamsSdkRedirectionData(
	@SerialName("sessionId")
	val sessionId: String? = null,
	@SerialName("fipId")
	val fipId: String? = null,
	@SerialName("aaCustomerMobile")
	val aaCustomerMobile: String? = null,
	@SerialName("clienttrnxid")
	val clienttrnxid: String? = null,
	@SerialName("aaCustomerHandleId")
	val aaCustomerHandleId: String? = null,
	@SerialName("useCaseId")
	val useCaseId: String? = null,
	@SerialName("userId")
	val userId: String? = null,
	@SerialName("fiuId")
	val fiuId: String? = null
)

