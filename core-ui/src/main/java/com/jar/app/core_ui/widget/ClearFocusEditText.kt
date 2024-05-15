package com.jar.app.core_ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText

class ClearFocusEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : AppCompatEditText(context, attrs, defStyleAttr) {

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }
}