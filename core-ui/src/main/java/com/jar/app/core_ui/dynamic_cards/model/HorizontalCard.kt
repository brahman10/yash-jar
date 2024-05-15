package com.jar.app.core_ui.dynamic_cards.model

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.util.dp
import com.jar.app.core_base.domain.model.card_library.Label
import com.jar.app.core_base.domain.model.card_library.LabelAlignment
import com.jar.app.core_ui.util.convertToString
import java.lang.ref.WeakReference

interface HorizontalCard: HomeFeedCard {

    fun updateLabelState(
        label: Label?,
        shouldShowLabel: Boolean,
        tvLabel: AppCompatTextView,
        card: View,
        shouldTranslateCard: Boolean,
        marginTop: Int,
        rootView: View,
        contextRef: WeakReference<Context>
    ) {
        if (shouldShowLabel) {
            if (label != null)
                tvLabel.visibility = View.VISIBLE
            else
                tvLabel.visibility = View.INVISIBLE

            if (shouldTranslateCard) {
                card.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    this.topMargin = marginTop
                }
            } else {
                card.translationY = 0f
            }
        } else {
            tvLabel.visibility = View.GONE
        }

        tvLabel.text = label?.text?.convertToString(contextRef)

        Glide.with(rootView)
            .asDrawable()
            .load(label?.icon)
            .override(11.dp, 11.dp)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    tvLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        resource, null, null, null
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                }

            })

        if (label != null) {
            tvLabel.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(label.getLabelBackgroundColor()))
            tvLabel.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToStart = ConstraintLayout.LayoutParams.UNSET
                endToEnd = ConstraintLayout.LayoutParams.UNSET
                when (label.getAlignment()) {
                    LabelAlignment.START -> {
                        startToStart = ConstraintSet.PARENT_ID
                    }
                    LabelAlignment.CENTER -> {
                        startToStart = ConstraintSet.PARENT_ID
                        endToEnd = ConstraintSet.PARENT_ID
                    }
                    LabelAlignment.END -> {
                        endToEnd = ConstraintSet.PARENT_ID
                    }
                }
            }
        }
    }
}