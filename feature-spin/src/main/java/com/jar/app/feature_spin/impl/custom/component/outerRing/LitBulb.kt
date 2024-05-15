package com.jar.app.feature_spin.impl.custom.component.outerRing

import android.content.Context
import android.util.AttributeSet
import com.jar.app.feature_spin.R

internal class LitBulb @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyleAttr: Int = 0):
    androidx.appcompat.widget.AppCompatImageView(context, attr, defStyleAttr) {

    private var isOn = true

    fun turnOffBulb() {
        isOn = false
        setImageResource(R.drawable.ic_off_buld)
    }

    fun turnOnBulb() {
        isOn = true
        setImageResource(R.drawable.lit_bulb)
    }

    fun toggleState() {
        isOn = !isOn
        if (isOn) {
            setImageResource(R.drawable.lit_bulb)
        } else {
            setImageResource(R.drawable.ic_off_buld)
        }
    }
}