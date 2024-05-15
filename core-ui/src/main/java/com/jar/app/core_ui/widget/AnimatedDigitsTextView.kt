package com.jar.app.core_ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.jar.app.base.util.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.digits
import com.jar.app.core_ui.extension.setDebounceClickListener
import java.lang.ref.WeakReference

class AnimatedDigitsTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val rupee_symbol="₹"
    private val animationOffset = 14.dp

    private var initialValueAsString = ""
    private var finalValue = 0
    private var finalValueArray = emptyList<Int>()
    private val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
    val typeface = ResourcesCompat.getFont(context, R.font.inter_bold)

    //private val valueAnimator:ValueAnimator
    private val views = ArrayList<WeakReference<AppCompatTextView>>()

    init {
        orientation = HORIZONTAL
        if (isInEditMode)
            valuateInitialValue()
        //valueAnimator =  ValueAnimator.ofInt(initialValue, finalValue)
        this.setDebounceClickListener {
            animateText()
        }
    }

    fun setFinalValue(value:Int){
        this.finalValue = value
        this.finalValueArray = value.digits()
        valuateInitialValue()
    }

    fun valuateInitialValue() {
        initialValueAsString = finalValue.digits().map { 0 }.joinToString("")
        createTextViews()
    }

    fun createTextViews() {
        views.clear()
        removeAllViews()
        views.add(WeakReference(AppCompatTextView(context).apply {
            text = rupee_symbol
            typeface = typeface
            setTextColor(Color.WHITE)
            textSize = 28f

        }))
        finalValue.getFormattedAmount().toCharArray().forEach {
            val textView = AppCompatTextView(context).apply {
                text = it.toString()
                typeface = typeface
                setTextColor(Color.WHITE)
                textSize = 28f

            }
            views.add(WeakReference(textView))
        }
        views.forEach {
            addView(it.get())
        }
    }

    fun animateText() {
        views.forEach {
            val view = it.get()
            val set = AnimatorSet()
            if (view?.text != rupee_symbol &&  view?.text != ","){
                view?.let {
                    verticalAnimation(it,it.text.toString(),0)
                }
            }

        }
    }

    fun verticalAnimation(textView:AppCompatTextView,text:String,index:Int=0){
        val number = numbers[index].toString()
        val isNumberReached = numbers[index] == text.toInt()
        textView.text = number
        ObjectAnimator.ofFloat(
            textView,
            View.TRANSLATION_Y,
            animationOffset.toFloat(),
            if (isNumberReached) 0f else -animationOffset.toFloat()
        ).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object :AnimatorListenerAdapter(){
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    textView.text = number
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (!isNumberReached){
                        verticalAnimation(textView,text, index+1)
                    }else{
                        textView.text = text
                    }
                }
            })
        }
    }


}