package com.jar.app.feature_jar_duo.shared.util

object DuoConstants {

    const val SOURCE_HOME = "SOURCE_HOME"
    const val SOURCE_PENDING_INVITE = "SOURCE_PENDING_INVITE"
    const val SOURCE_DUO_LIST = "SOURCE_DUO_LIST"
    const val SOURCE_DUO_PENDING_INVITE = "SOURCE_DUO_PENDING_INVITE"
    const val SOURCE_HOME_CARD_INFO_BUTTON = "SOURCE_HOME_CARD_INFO_BUTTON"

    object ImageEndpoints {
        const val FACE_SINGLE = "/Images/Jar-Duo/face_single_new.webp"
        const val FACE_MULTIPLE = "/Images/Jar-Duo/face_multiple_new.webp"
        const val FACE_PLUS_SIGN = "/Images/Jar-Duo/face_plus_sign.webp"

        const val HOME_CARD_ONE = "/Images/Jar-Duo/home_card_one.webp"
        const val HOME_CARD_TWO = "/Images/Jar-Duo/home_card_two.webp"
        const val HOME_CARD_THREE = "/Images/Jar-Duo/home_card_three.webp"
        const val HOME_CARD_FOUR = "/Images/Jar-Duo/home_card_four.webp"
    }

    internal object Endpoints {
        const val FETCH_GROUP_LIST = "v1/api/duo/groups/list"
        const val FETCH_GROUP_INFO = "v1/api/duo/group/info"
        const val RENAME_GROUP = "v1/api/duo/groups/rename"
        const val DELETE_GROUP = "v1/api/duo/groups/delete"
        const val FETCH_GROUP_INFO_V2 = "v1/api/duo/group/duo-info-v2"
        const val FETCH_DUO_STORY_INTRO = "v1/api/duo/introPage"
    }

}