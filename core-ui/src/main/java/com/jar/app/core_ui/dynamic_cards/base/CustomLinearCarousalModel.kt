package com.jar.app.core_ui.dynamic_cards.base

import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.updatePadding
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.CarouselModel_
import com.jar.app.base.util.findViewWithTag
import com.jar.app.core_ui.util.dp

class CustomLinearCarousalModel : CarouselModel_() {

    internal var translationY: SpringAnimation? = null

    internal var view: View? = null
    internal var carousel: Carousel? = null

    override fun bind(carousel: Carousel) {
        super.bind(carousel)
        this.carousel = carousel
        view = carousel.rootView
        view?.updatePadding(16.dp, 0, 16.dp, 0)
        view?.overScrollMode = View.OVER_SCROLL_NEVER
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

    fun scrollToDx(dx: Int) {
        carousel?.smoothScrollBy(dx, 0, DecelerateInterpolator())
    }

    fun findChildViewForTag(tag: String): View? {
        return carousel?.layoutManager?.findViewWithTag(tag)
    }

}