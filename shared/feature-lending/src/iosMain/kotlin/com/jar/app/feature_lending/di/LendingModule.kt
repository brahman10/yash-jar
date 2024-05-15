package com.jar.app.feature_lending.di

import com.jar.app.feature_lending.shared.api.impl.use_case.FetchLendingV2PreApprovedDataUseCaseImpl
import com.jar.app.feature_lending.shared.api.impl.use_case.FetchLoanApplicationListUseCaseImpl
import com.jar.app.feature_lending.shared.api.impl.use_case.FetchLoanProgressStatusV2UseCaseImpl
import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanProgressStatusV2UseCase
import com.jar.app.feature_lending.shared.data.network.LendingDataSource
import com.jar.app.feature_lending.shared.data.repository.LendingRepository
import com.jar.app.feature_lending.shared.domain.repository.LendingRepositoryImpl
import com.jar.app.feature_lending.shared.domain.use_case.AcknowledgeOneTimeCardUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsBanksUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsDataStatusUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCamsSdkRedirectDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditDetailedReportUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchCreditReportSummaryDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiPlansUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchEmiTxnHistoryUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchExperianReportUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLendingAgreementUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLendingFaqUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanApplicationsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchPANStatusUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashLandingScreenContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeCreditDetailsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchRealTimeLeadStatusUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchRepaymentDetailsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchTransactionDetailsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchUploadedBankStatementsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.InitiateForeclosurePaymentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.RefreshCreditReportSummaryDataUseCase
import com.jar.app.feature_lending.shared.domain.use_case.RequestLendingOtpUseCase
import com.jar.app.feature_lending.shared.domain.use_case.ScheduleBankUptimeNotificationUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateAddressDetailsUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankDetailUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateBankStatementPasswordUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateDrawdownUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateNotifyUserUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UploadBankStatementUseCase
import com.jar.app.feature_lending.shared.domain.use_case.ValidateIfscCodeUseCase
import com.jar.app.feature_lending.shared.domain.use_case.VerifyLendingOtpUseCase
import com.jar.app.feature_lending.shared.domain.use_case.impl.AcknowledgeOneTimeCardUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchCamsBanksUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchCamsDataStatusUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchCamsSdkRedirectDataUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchCreditReportSummaryDataUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchEmiPlansUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchEmiTxnHistoryUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchExperianReportUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchLendingAgreementUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchLendingFaqUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchLoanApplicationsUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchLoanDetailsV2UseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchPANStatusUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchReadyCashJourneyUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchReadyCashLandingScreenContentUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchRealTimeCreditDetailsUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchRealTimeLeadStatusUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchRepaymentDetailsUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchStaticContentUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchTransactionDetailsUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.FetchUploadedBankStatementsUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.InitiateForeclosurePaymentUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.RefreshCreditReportSummaryDataUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.RequestLendingOtpUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.ScheduleBankUptimeNotificationUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UpdateAddressDetailsUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UpdateBankDetailUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UpdateBankStatementPasswordUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UpdateDrawdownUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UpdateLoanDetailsV2CaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UpdateNotifyUserUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.UploadBankStatementUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.ValidateIfscCodeUseCaseImpl
import com.jar.app.feature_lending.shared.domain.use_case.impl.VerifyLendingOtpUseCaseImpl
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
import io.ktor.client.HttpClient


class LendingModule(
    client: HttpClient
) {

    private val lendingDataSource: LendingDataSource by lazy {
        LendingDataSource(client)
    }

    private val lendingRepository: LendingRepository by lazy {
        LendingRepositoryImpl(lendingDataSource)
    }

    val provideFetchLoanApplicationsUseCase: FetchLoanApplicationsUseCase by lazy {
        FetchLoanApplicationsUseCaseImpl(lendingRepository)
    }


    val provideFetchLendingFaqUseCase: FetchLendingFaqUseCase by lazy {
        FetchLendingFaqUseCaseImpl(lendingRepository)
    }

    val provideValidateIfscCodeUseCase: ValidateIfscCodeUseCase by lazy {
        ValidateIfscCodeUseCaseImpl(lendingRepository)
    }


    val provideUpdateAddressDetailsUseCase: UpdateAddressDetailsUseCase by lazy {
        UpdateAddressDetailsUseCaseImpl(lendingRepository)
    }


    val provideUpdateDrawdownUseCase: UpdateDrawdownUseCase by lazy {
        UpdateDrawdownUseCaseImpl(lendingRepository)
    }

    val provideFetchLendingAgreementUseCase: FetchLendingAgreementUseCase by lazy {
        FetchLendingAgreementUseCaseImpl(lendingRepository)
    }

    val provideRequestLendingOtpUseCase: RequestLendingOtpUseCase by lazy {
        RequestLendingOtpUseCaseImpl(lendingRepository)
    }

    val provideVerifyLendingOtpUseCase: VerifyLendingOtpUseCase by lazy {
        VerifyLendingOtpUseCaseImpl(lendingRepository)
    }

    val provideFetchEmiPlansUseCase: FetchEmiPlansUseCase by lazy {
        FetchEmiPlansUseCaseImpl(lendingRepository)
    }

    val provideStaticContentUseCase: FetchStaticContentUseCase by lazy {
        FetchStaticContentUseCaseImpl(lendingRepository)
    }

    val provideFetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase by lazy {
        FetchLoanDetailsV2UseCaseImpl(lendingRepository)
    }

    val provideUpdateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase by lazy {
        UpdateLoanDetailsV2CaseImpl(lendingRepository)
    }

    val provideInitiateForeclosurePaymentUseCase: InitiateForeclosurePaymentUseCase by lazy {
        InitiateForeclosurePaymentUseCaseImpl(lendingRepository)
    }

    val provideFetchRepaymentDetailsUseCase: FetchRepaymentDetailsUseCase by lazy {
        FetchRepaymentDetailsUseCaseImpl(lendingRepository)
    }

    val provideEmiTxnHistoryUseCase: FetchEmiTxnHistoryUseCase by lazy {
        FetchEmiTxnHistoryUseCaseImpl(lendingRepository)
    }

    val provideTxnDetailUseCase: FetchTransactionDetailsUseCase by lazy {
        FetchTransactionDetailsUseCaseImpl(lendingRepository)
    }

    val provideFetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase by lazy {
        FetchReadyCashJourneyUseCaseImpl(lendingRepository)
    }

    val provideFetchLandingContentUseCase: FetchReadyCashLandingScreenContentUseCase by lazy {
        FetchReadyCashLandingScreenContentUseCaseImpl(lendingRepository)
    }

    val provideUpdateNotifyUserUseCase: UpdateNotifyUserUseCase by lazy {
        UpdateNotifyUserUseCaseImpl(lendingRepository)
    }

    val provideAcknowledgeOneTimeCardUseCase: AcknowledgeOneTimeCardUseCase by lazy {
        AcknowledgeOneTimeCardUseCaseImpl(lendingRepository)
    }

    val provideUploadBankStatementUseCase: UploadBankStatementUseCase by lazy {
        UploadBankStatementUseCaseImpl(lendingRepository = lendingRepository)
    }

    val provideFetchCamsBanksUseCase: FetchCamsBanksUseCase by lazy {
        FetchCamsBanksUseCaseImpl(lendingRepository)
    }

    val provideFetchCamsDataStatusUseCase: FetchCamsDataStatusUseCase by lazy {
        FetchCamsDataStatusUseCaseImpl(lendingRepository)
    }

    val provideFetchCamsSdkRedirectDataUseCase: FetchCamsSdkRedirectDataUseCase by lazy {
        FetchCamsSdkRedirectDataUseCaseImpl(lendingRepository)
    }

    val provideFetchPANStatusUseCase: FetchPANStatusUseCase by lazy {
        FetchPANStatusUseCaseImpl(lendingRepository)
    }

    val provideUpdateCamsDowntimeUseCase: ScheduleBankUptimeNotificationUseCase by lazy {
        ScheduleBankUptimeNotificationUseCaseImpl(lendingRepository)
    }

    val provideFetchRealTimeCreditDetailsUseCase: FetchRealTimeCreditDetailsUseCase by lazy {
        FetchRealTimeCreditDetailsUseCaseImpl(lendingRepository)
    }

    val provideFetchExperianUseCase: FetchExperianReportUseCase by lazy {
        FetchExperianReportUseCaseImpl(lendingRepository)
    }

    val provideUpdateBankDetailUseCase: UpdateBankDetailUseCase by lazy {
        UpdateBankDetailUseCaseImpl(lendingRepository)
    }

    val provideFetchUploadedBankStatementsUseCase: FetchUploadedBankStatementsUseCase by lazy {
        FetchUploadedBankStatementsUseCaseImpl(lendingRepository)
    }

    val provideFetchRealTimeLeadStatusUseCase: FetchRealTimeLeadStatusUseCase by lazy {
        FetchRealTimeLeadStatusUseCaseImpl(lendingRepository)
    }

    val provideUpdateBankStatementPasswordUseCase: UpdateBankStatementPasswordUseCase by lazy {
        UpdateBankStatementPasswordUseCaseImpl(lendingRepository)
    }

    val provideFetchPreApprovedUseCase: FetchLendingV2PreApprovedDataUseCase by lazy {
        FetchLendingV2PreApprovedDataUseCaseImpl(lendingRepository)
    }

    val provideFetchLoanListUseCase: FetchLoanApplicationListUseCase by lazy {
        FetchLoanApplicationListUseCaseImpl(lendingRepository)
    }

    val provideFetchLoanProgressStatusV2UseCase: FetchLoanProgressStatusV2UseCase by lazy {
        FetchLoanProgressStatusV2UseCaseImpl(lendingRepository)
    }

    val provideLendingStepsProgressGenerator: LendingStepsProgressGenerator by lazy{
        LendingStepsProgressGenerator()
    }

    val fetchCreditReportSummaryDataUseCase: FetchCreditReportSummaryDataUseCase by lazy {
        FetchCreditReportSummaryDataUseCaseImpl(lendingRepository)
    }

    val refreshCreditReportSummaryDataUseCase: RefreshCreditReportSummaryDataUseCase by lazy {
        RefreshCreditReportSummaryDataUseCaseImpl(lendingRepository)
    }
}