package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.use_case.FetchReminderUseCase

internal class FetchReminderUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): FetchReminderUseCase {

    override suspend fun fetchReminder(loanId: String, medium: String) =
        vasooliRepository.fetchReminder(loanId, medium)

}