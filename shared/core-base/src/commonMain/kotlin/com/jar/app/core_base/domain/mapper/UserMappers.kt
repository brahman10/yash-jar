package com.jar.app.core_base.domain.mapper

import com.jar.app.core_base.data.dto.UserDTO
import com.jar.app.core_base.data.dto.UserResponseDTO
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.domain.model.UserResponseData

fun UserResponseDTO.toUserResponseData(): UserResponseData {
    return UserResponseData(
        accessToken = accessToken,
        refreshToken = refreshToken,
        user = user.toUser(),
        hasOtherActiveSessions = hasOtherActiveSessions,
        authType = authType,
        numberOfDaysOfSms = numberOfDaysOfSms
    )
}

fun UserDTO.toUser(): User {
    return User(
        userId = userId,
        profilePicUrl = profilePicUrl,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        age = age,
        gender = gender,
        email = email,
        onboarded = onboarded,
        createdAtInUtc = createdAtInUtc,
        userGoalSetup = userGoalSetup
    )
}

