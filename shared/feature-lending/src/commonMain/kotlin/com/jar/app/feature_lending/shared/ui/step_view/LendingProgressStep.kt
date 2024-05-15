package com.jar.app.feature_lending.shared.ui.step_view

import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.StringResource


data class LendingProgressStep(
    val status: LendingStepStatus,
    val titleResId: StringResource,
    val position: Int = 0
)

enum class LendingStepStatus(val statusText: StringResource, val statusTextColor: ColorResource) {
    FAILURE(
        com.jar.app.feature_lending.shared.MR.strings.feature_lending_empty,
        com.jar.app.core_base.shared.CoreBaseMR.colors.transparent
    ),
    PENDING(
        com.jar.app.feature_lending.shared.MR.strings.feature_lending_empty,
        com.jar.app.core_base.shared.CoreBaseMR.colors.transparent
    ),
    IN_PROGRESS(
        com.jar.app.feature_lending.shared.MR.strings.feature_lending_complete_now,
        com.jar.app.core_base.shared.CoreBaseMR.colors.color_EBB46A
    ),
    COMPLETED(
        com.jar.app.feature_lending.shared.MR.strings.feature_lending_completed,
        com.jar.app.core_base.shared.CoreBaseMR.colors.commonTxtColor
    )
}

enum class LendingStep(val titleRes: StringResource) {
    CHOOSE_AMOUNT(com.jar.app.feature_lending.shared.MR.strings.feature_lending_choose_n_amount),
    KYC(com.jar.app.feature_lending.shared.MR.strings.feature_lending_verify_n_kyc),
    BANK_DETAILS(com.jar.app.feature_lending.shared.MR.strings.feature_lending_bank_n_details),
    LOAN_AGREEMENT(com.jar.app.feature_lending.shared.MR.strings.feature_lending_loan_n_agreement)
}

