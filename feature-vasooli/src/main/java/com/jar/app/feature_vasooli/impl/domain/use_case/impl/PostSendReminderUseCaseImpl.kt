package com.jar.app.feature_vasooli.impl.domain.use_case.impl

import com.jar.app.feature_vasooli.impl.data.repository.VasooliRepository
import com.jar.app.feature_vasooli.impl.domain.model.SendReminderRequest
import com.jar.app.feature_vasooli.impl.domain.use_case.PostSendReminderUseCase

internal class PostSendReminderUseCaseImpl constructor(
    private val vasooliRepository: VasooliRepository
): PostSendReminderUseCase {

    override suspend fun sendReminder(sendReminderRequest: SendReminderRequest) =
        vasooliRepository.sendReminder(sendReminderRequest)
}