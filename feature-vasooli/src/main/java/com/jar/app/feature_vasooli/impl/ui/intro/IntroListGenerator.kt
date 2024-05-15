package com.jar.app.feature_vasooli.impl.ui.intro

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.impl.domain.model.Intro
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class IntroListGenerator @Inject constructor() {

    fun getIntroList(): List<Intro> {
        val list = ArrayList<Intro>()

        list.add(
            Intro(
                title = null,
                description = null,
                imageLink = "${BaseConstants.CDN_BASE_URL}/Vasooli-Onboarding/Splash.png"
            )
        )

        list.add(
            Intro(
                title = R.string.feature_vasooli_intro_title_1,
                description = R.string.feature_vasooli_intro_desc_1,
                imageLink = "${BaseConstants.CDN_BASE_URL}/Vasooli-Onboarding/vasooli_onboarding_1.png"
            )
        )

        list.add(
            Intro(
                title = R.string.feature_vasooli_intro_title_2_new,
                description = null,
                imageLink = "${BaseConstants.CDN_BASE_URL}/Vasooli-Onboarding/vasooli_onboarding_2.webp"
            )
        )

        list.add(
            Intro(
                title = R.string.feature_vasooli_intro_title_3,
                description = R.string.feature_vasooli_intro_desc_3,
                imageLink = "${BaseConstants.CDN_BASE_URL}/Vasooli-Onboarding/vasooli_onboarding_3.webp"
            )
        )

        return list
    }

}