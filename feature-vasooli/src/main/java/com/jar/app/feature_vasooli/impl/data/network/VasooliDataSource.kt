package com.jar.app.feature_vasooli.impl.data.network

import com.jar.app.feature_vasooli.impl.domain.model.*
import com.jar.internal.library.jar_core_network.api.data.BaseDataSource
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import com.jar.app.feature_vasooli.impl.util.VasooliConstants.Endpoints

internal class VasooliDataSource constructor(
    private val client: HttpClient
) : BaseDataSource() {

    suspend fun fetchVasooliOverview() = getResult<ApiResponseWrapper<VasooliOverview>> {
        client.get {
            url(Endpoints.FETCH_VASOOLI_OVERVIEW)
        }
    }

    suspend fun fetchLoansList() = getResult<ApiResponseWrapper<List<Borrower>>> {
        client.get {
            url(Endpoints.FETCH_VASOOLI_LENT_LIST)
        }
    }

    suspend fun postVasooliRequest(vasooliEntryRequest: VasooliEntryRequest) =
        getResult<ApiResponseWrapper<VasooliEntryResponse>> {
            client.post {
                url(Endpoints.POST_VASOOLI_REQUEST)
                setBody(vasooliEntryRequest)
            }
        }

    suspend fun fetchRepaymentHistory(loanId: String) = getResult<ApiResponseWrapper<List<Repayment>>> {
        client.get {
            url(Endpoints.FETCH_VASOOLI_REPAYMENT_HISTORY)
            parameter("loanId", loanId)
        }
    }

    suspend fun postRepaymentEntryRequest(repaymentEntryRequest: RepaymentEntryRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.POST_REPAYMENT_ENTRY_REQUEST)
                setBody(repaymentEntryRequest)
            }
        }

    suspend fun updateVasooliStatus(updateStatusRequest: UpdateStatusRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.UPDATE_VASOOLI_STATUS)
                setBody(updateStatusRequest)
            }
        }

    suspend fun deleteVasooliEntry(loanId: String) = getResult<ApiResponseWrapper<Unit?>> {
        client.delete {
            url(Endpoints.DELETE_VASOOLI_ENTRY)
            parameter("loanId", loanId)
        }
    }

    suspend fun fetchLoanDetails(loanId: String) = getResult<ApiResponseWrapper<Borrower>> {
        client.get {
            url(Endpoints.FETCH_LOAD_DETAILS)
            parameter("loanId", loanId)
        }
    }

    suspend fun updateVasooliEntry(updateEntryRequest: UpdateEntryRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.put {
                url(Endpoints.UPDATE_VASOOLI_ENTRY)
                setBody(updateEntryRequest)
            }
        }

    suspend fun fetchReminder(loanId: String, medium: String) =
        getResult<ApiResponseWrapper<Reminder>> {
            client.get {
                url(Endpoints.FETCH_VASOOLI_REMINDER)
                parameter("loanId", loanId)
                parameter("medium", medium)
            }
        }

    suspend fun fetchNewImage(ignoreIndex: String) = getResult<ApiResponseWrapper<Reminder>> {
        client.get {
            url(Endpoints.FETCH_VASOOLI_REMINDER_IMAGE)
            parameter("ignoreIndex", ignoreIndex)
        }
    }

    suspend fun sendReminder(sendReminderRequest: SendReminderRequest) =
        getResult<ApiResponseWrapper<Unit?>> {
            client.post {
                url(Endpoints.SEND_VASOOLI_REMINDER)
                setBody(sendReminderRequest)
            }
        }
}