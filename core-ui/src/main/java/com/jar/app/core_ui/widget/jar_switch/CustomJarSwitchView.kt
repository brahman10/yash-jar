package com.jar.app.core_ui.widget.jar_switch

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import com.jar.app.core_ui.R

class CustomJarSwitchView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SwitchCompat(context, attrs) {
    init {
        this.textOff = context.getString(R.string.feature_core_ui_switch_compat_off)
        this.textOn = context.getString(R.string.feature_core_ui_switch_compat_on)
        this.thumbDrawable = AppCompatResources.getDrawable(context, R.drawable.core_ui_jar_thumb)
        this.trackDrawable = AppCompatResources.getDrawable(context, R.drawable.custom_track)
        this.setSwitchTextAppearance(context, R.style.JarSwitch)
        this.showText = true
    }
}