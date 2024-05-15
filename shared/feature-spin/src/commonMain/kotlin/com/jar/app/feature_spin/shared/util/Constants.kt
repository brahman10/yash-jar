package com.jar.app.feature_spin.shared.util

object Constants {

    internal object Endpoints {
        const val FETCH_SPIN_META_DATA = "v1/api/dashboard/games"
        const val FETCH_INTRO_PAGE = "v1/api/games/introPage"
        const val FETCH_SPIN_GANE = "v1/api/games/fetch"
        const val FETCH_GAME_RESULT = "v1/api/games/spin"
        const val FETCH_FLAT_OUTCOME = "v1/api/games/spin/outcome/flat"
        const val FETCH_JACKPOT_OUTCOME = "v1/api/games/spin/outcome/coupon/v2"
        const val FETCH_WINNINGS_POPUP_DATA = "v1/api/games/useWinnings/popup"
        const val RESET_SPIN = "v1/api/games/resetSpin"
    }

    internal object QuestsEndpointsV2 {
        const val FETCH_SPIN_GANE = "v1/api/quest/spinGame/fetch"
        const val FETCH_GAME_RESULT = "v1/api/quest/spinGame/spin"
        const val FETCH_FLAT_OUTCOME = "v1/api/quest/spinGame/spin/outcome/flat"
        const val FETCH_JACKPOT_OUTCOME = "v1/api/quest/spinGame/spin/outcome/coupon"
    }
}