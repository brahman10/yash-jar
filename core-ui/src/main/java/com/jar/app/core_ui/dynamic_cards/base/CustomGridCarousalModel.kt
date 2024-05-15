package com.jar.app.core_ui.dynamic_cards.base

import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

class CustomGridCarousalModel : VerticalGridCarouselModel_() {

    internal var translationY: SpringAnimation? = null

    internal var view: View? = null

    override fun bind(carousel: VerticalGridCarousel) {
        super.bind(carousel)
        view = carousel.rootView

        /**
         * A [SpringAnimation] for this RecyclerView item. This animation is used to bring the item back
         * after the over-scroll effect.
         */
        translationY = SpringAnimation(view, SpringAnimation.TRANSLATION_Y)
            .setSpring(
                SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW)
            )
    }
}