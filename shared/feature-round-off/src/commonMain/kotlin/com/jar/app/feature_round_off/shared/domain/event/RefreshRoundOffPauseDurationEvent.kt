package com.jar.app.feature_round_off.shared.domain.event

import com.jar.app.feature_user_api.domain.model.PauseSavingResponse

data class RefreshRoundOffPauseDurationEvent (val pauseSavingResponse: PauseSavingResponse)