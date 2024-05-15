package com.jar.app.feature_lending_kyc.impl.ui.steps

import androidx.fragment.app.FragmentActivity
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.domain.model.KycEmailAndAadhaarProgressStatus
import com.jar.app.core_base.domain.model.KycPANProgressStatus
import com.jar.app.feature_lending_kyc.impl.data.KycStep
import com.jar.app.feature_lending_kyc.impl.data.KycStepStatus
import com.jar.app.feature_lending_kyc.impl.data.Step
import com.jar.app.core_base.domain.model.KycProgressResponse
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LendingKycStepsProgressGenerator @Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) {

    //We need activity content to fetch multi-langual strings.. application context won't work..
    suspend fun getKycProgressList(
        contextRef: WeakReference<FragmentActivity>,
        kycProgressResponse: KycProgressResponse
    ): List<KycStep> = withContext(dispatcherProvider.default) {
        val context = contextRef.get()!!
        val list = ArrayList<KycStep>()

        kycProgressResponse.kycProgress?.EMAIL?.let {
            list.add(
                getKycStep(
                    context,
                    it.status,
                    Step.EMAIL
                )
            )
        } ?: kotlin.run {
            list.add(
                KycStep(
                    KycStepStatus.IN_PROGRESS,
                    context.getString(Step.EMAIL.titleRes),
                    Step.EMAIL.stepNumber
                )
            )
        }
        kycProgressResponse.kycProgress?.PAN?.let {
            list.add(
                getKycStep(
                    context,
                    it.status,
                    Step.PAN
                )
            )
        } ?: kotlin.run {
            val status =
                if (kycProgressResponse.kycProgress?.EMAIL?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name)
                    KycStepStatus.IN_PROGRESS
                else KycStepStatus.NOT_YET_VISITED
            list.add(
                KycStep(
                    status,
                    context.getString(Step.PAN.titleRes),
                    Step.PAN.stepNumber
                )
            )
        }

        kycProgressResponse.kycProgress?.AADHAAR?.let {
            if (it.isEmpty()) {
                list.add(
                    KycStep(
                        KycStepStatus.IN_PROGRESS,
                        context.getString(Step.AADHAAR.titleRes),
                        Step.AADHAAR.stepNumber
                    )
                )
            } else {
                list.add(
                    getKycStep(
                        context,
                        it.status,
                        Step.AADHAAR
                    )
                )
            }

        } ?: kotlin.run {
            val status =
                if (kycProgressResponse.kycProgress?.PAN?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name)
                    KycStepStatus.IN_PROGRESS
                else KycStepStatus.NOT_YET_VISITED
            list.add(
                KycStep(
                    status,
                    context.getString(Step.AADHAAR.titleRes),
                    Step.AADHAAR.stepNumber
                )
            )
        }

        kycProgressResponse.kycProgress?.SELFIE?.let {
            list.add(
                getKycStep(
                    context,
                    it.status,
                    Step.SELFIE
                )
            )
        } ?: kotlin.run {
            val status =
                if (kycProgressResponse.kycProgress?.AADHAAR?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name)
                    KycStepStatus.IN_PROGRESS
                else KycStepStatus.NOT_YET_VISITED
            list.add(
                KycStep(
                    status,
                    context.getString(Step.SELFIE.titleRes),
                    Step.SELFIE.stepNumber
                )
            )
        }
        return@withContext list
    }

    private fun getKycStep(context: FragmentActivity, status: String, step: Step): KycStep {
        return when (status) {
            KycEmailAndAadhaarProgressStatus.VERIFIED.name ->
                KycStep(
                    KycStepStatus.COMPLETED,
                    context.getString(step.titleRes),
                    step.stepNumber
                )
            KycEmailAndAadhaarProgressStatus.OTP_SENT.name, KycEmailAndAadhaarProgressStatus.EXPIRED.name, KycPANProgressStatus.OTP_VERIFIED.name, KycEmailAndAadhaarProgressStatus.INPROGRESS.name, KycPANProgressStatus.RETRY_LIMIT_EXCEEDED.name, KycEmailAndAadhaarProgressStatus.FAILED.name, KycPANProgressStatus.RETRY_LIMIT_EXHAUSTED.name ->
                KycStep(
                    KycStepStatus.IN_PROGRESS,
                    context.getString(step.titleRes),
                    step.stepNumber
                )
            else -> KycStep(
                KycStepStatus.NOT_YET_VISITED,
                context.getString(step.titleRes),
                step.stepNumber
            )
        }
    }
}