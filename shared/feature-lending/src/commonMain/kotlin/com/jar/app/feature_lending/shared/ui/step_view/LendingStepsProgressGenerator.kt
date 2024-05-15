package com.jar.app.feature_lending.shared.ui.step_view

import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import kotlinx.coroutines.withContext
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashProgressBar
import kotlinx.coroutines.Dispatchers

class LendingStepsProgressGenerator {
    suspend fun getLendingProgress(
        progressBar: List<ReadyCashProgressBar>
    ): List<LendingProgressStep> = withContext(Dispatchers.Default) {
        val list = ArrayList<LendingProgressStep>()
        progressBar.forEachIndexed { index, readyCashProgressBar ->
            list.add(
                getLendingStep(
                    readyCashProgressBar.status,
                    getStep(readyCashProgressBar.step),
                    index + 1
                )
            )
        }
        return@withContext list
    }

    private fun getStep(step: String): LendingStep {
        return when (step) {
            LendingStep.CHOOSE_AMOUNT.name -> LendingStep.CHOOSE_AMOUNT
            LendingStep.KYC.name -> LendingStep.KYC
            LendingStep.BANK_DETAILS.name -> LendingStep.BANK_DETAILS
            LendingStep.LOAN_AGREEMENT.name -> LendingStep.LOAN_AGREEMENT
            else -> LendingStep.CHOOSE_AMOUNT
        }
    }

    private fun getLendingStep(
        status: String,
        step: LendingStep,
        position: Int
    ): LendingProgressStep {
        return when (status) {
            LoanStatus.VERIFIED.name ->
                LendingProgressStep(
                    LendingStepStatus.COMPLETED,
                    step.titleRes,
                    position
                )
            LoanStatus.IN_PROGRESS.name ->
                LendingProgressStep(
                    LendingStepStatus.IN_PROGRESS,
                    step.titleRes,
                    position
                )
            LoanStatus.FAILED.name ->
                LendingProgressStep(
                    LendingStepStatus.FAILURE,
                    step.titleRes,
                    position
                )
            else -> LendingProgressStep(
                LendingStepStatus.PENDING,
                step.titleRes,
                position
            )
        }

    }
}