package com.jar.app.core_ui.dynamic_cards.base

import android.graphics.Color
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyViewHolder
import com.jar.app.base.util.forEachVisibleHolder
import com.jar.app.core_base.util.orZero

class EpoxyBaseEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {

    companion object {
        /** The magnitude of translation distance while the list is over-scrolled. */
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.15f

        /** The magnitude of translation distance when the list reaches the edge on fling. */
        private const val FLING_TRANSLATION_MAGNITUDE = 0.20f

    }

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect {

        val edgeEffectFactory = object : EdgeEffect(recyclerView.context) {

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                // This is called on every touch event while the list is scrolled with a finger.
                // We simply update the view properties without animation.
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationYDelta =
                    sign * recyclerView.width * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                recyclerView.forEachVisibleHolder { holder: RecyclerView.ViewHolder ->
                    val model = ((holder as EpoxyViewHolder).model)
                    if (model is ViewBindingKotlinModel<*>) {
                        model.translationY?.cancel()
                        model.view?.translationY =
                            model.view?.translationY.orZero() + translationYDelta
                    } else if (model is CustomLinearCarousalModel) {

                        model.translationY?.cancel()
                        model.view?.translationY =
                            model.view?.translationY.orZero() + translationYDelta
                    }
                }
            }

            override fun onRelease() {
                super.onRelease()
                // The finger is lifted. This is when we should start the animations to bring
                // the view property values back to their resting states.
                recyclerView.forEachVisibleHolder { holder: RecyclerView.ViewHolder ->
                    val model = ((holder as EpoxyViewHolder).model)
                    if (model is ViewBindingKotlinModel<*>) {
                        model.translationY?.start()
                    } else if (model is CustomLinearCarousalModel) {
                        model.translationY?.start()
                    }
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                // The list has reached the edge on fling.
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                recyclerView.forEachVisibleHolder { holder: RecyclerView.ViewHolder ->
                    val model = ((holder as EpoxyViewHolder).model)
                    if (model is ViewBindingKotlinModel<*>) {
                        model.translationY
                            ?.setStartVelocity(translationVelocity)
                            ?.start()
                    } else if (model is CustomLinearCarousalModel) {
                        model.translationY
                            ?.setStartVelocity(translationVelocity)
                            ?.start()
                    }
                }
            }
        }
        edgeEffectFactory.color = Color.WHITE
        return edgeEffectFactory
    }
}