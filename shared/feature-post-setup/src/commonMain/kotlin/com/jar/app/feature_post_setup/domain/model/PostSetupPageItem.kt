package com.jar.app.feature_post_setup.domain.model

sealed interface PostSetupPageItem {

    fun getSortKey(): Int

}