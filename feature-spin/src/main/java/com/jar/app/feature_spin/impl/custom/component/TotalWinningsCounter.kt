package com.jar.app.feature_spin.impl.custom.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.jar.app.feature_spin.R
import com.jar.app.feature_spin.impl.custom.component.models.TextAnimationFields
import kotlin.math.max

@Suppress("DEPRECATION")
internal class TotalWinningsCounter(context: Context, attributeSets: AttributeSet?): ConstraintLayout(
    context, attributeSets
) {
    private val textStyle = arrayOf("Bold", "Normal")

    private val minLength = 4

    // starting number, we can programmatically set this
    private var animatingNumbers: IntArray? = intArrayOf(minLength)
    private var previousNumber = intArrayOf(minLength)


    private val textAnimationFields: TextAnimationFields by lazy {
        TextAnimationFields()
    }

    var number: String = ""

    var fontStyle = "bold"

    private val badgeImageView by lazy {
        ZoomableImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
                setPadding(5, 8, 5, 10)
            }
            setImageResource(R.drawable.badge)
        }
    }

    private val displayCounterLinearLayout: LinearLayout by lazy {
        LinearLayout(this.context).apply {
            val newLayoutParam = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
                gravity = Gravity.CENTER
                setPadding(10, 10, 15, 10)
                setMargins(0, 0, 0, 10)
            }
            this.layoutParams = newLayoutParam
            background = ContextCompat.getDrawable(this.context, R.drawable.border_total_winnings_count)
            this.addView(badgeImageView)
        }
    }

    fun updateTextSize(size: Int) {
        textAnimationFields.textSize = size
    }

    private var myTextViews: Array<AutoScalingTextView2?>? = null
    private var myTextViewsOut: Array<AutoScalingTextView2?>? = null
    private var childLayout: RelativeLayout? = null
    private var layoutParams: LinearLayout.LayoutParams? = null
    private var rowTextViewOut: AutoScalingTextView2? = null
    private var rowTextView: AutoScalingTextView2? = null

    companion object {
        const val TAG = "TotalWinningsCounter"
    }

    init {
        addView(displayCounterLinearLayout)
    }

    private fun createNumberOfTextFields() {
        myTextViews =
            arrayOfNulls(textAnimationFields.maxNumbers) // create an empty array;

        myTextViewsOut =
            arrayOfNulls(textAnimationFields.maxNumbers) // create an empty array;

        for (i in 0 until textAnimationFields.maxNumbers) {
            childLayout = RelativeLayout(this.context)
            layoutParams = LinearLayout.LayoutParams(
                (textAnimationFields.textSize * 3),
                (textAnimationFields.textSize * 4),
            )
            childLayout?.background = (ContextCompat.getDrawable(context, R.drawable.border_with_radius))
            layoutParams?.setMargins(6, 0, 0, 0)
            childLayout?.layoutParams = layoutParams

            // create a new textview
            rowTextView = AutoScalingTextView2(this.context)
            rowTextViewOut = AutoScalingTextView2(this.context)

            rowTextView?.layoutParams = TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            rowTextViewOut?.layoutParams = TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )

            rowTextView?.gravity = Gravity.CENTER
            rowTextViewOut?.gravity = Gravity.CENTER

            childLayout?.addView(rowTextViewOut, 0)
            childLayout?.addView(rowTextView, 0)

            displayCounterLinearLayout.addView(childLayout)
            rowTextView?.setTextColor(Color.WHITE)
            rowTextViewOut?.setTextColor(Color.WHITE)

            // setting the style of fonts
            if (textAnimationFields.textStyleString == textStyle[0]) {
                rowTextView?.typeface = Typeface.DEFAULT_BOLD
                rowTextViewOut?.typeface = Typeface.DEFAULT_BOLD
            } else {
                rowTextView?.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                rowTextViewOut?.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            myTextViews!![i] = rowTextView
            myTextViewsOut!![i] = rowTextViewOut

            animateTexts(animatingNumbers?.get(i),
                previousNumber[i], myTextViews?.get(i), myTextViewsOut?.get(i))
        }
    }

    private fun animateTexts(actualNo : Int?, loopNo: Int, textView: TextView?, textViewOut: TextView?) {
        textViewOut?.text = " $loopNo "
        textView?.visibility = GONE

        if (actualNo == loopNo) {
            textViewOut?.text = " $actualNo "
            textView?.text = " $actualNo "
            textView?.visibility = VISIBLE
        } else {
            val animatorSet2 = AnimatorSet()
            animatorSet2.interpolator = LinearInterpolator()
            animatorSet2.playTogether(
                ObjectAnimator.ofFloat(
                    textViewOut,
                    "translationY",
                    0f,
                    textAnimationFields.textSize * 3f,
                )
            )
            animatorSet2.duration = textAnimationFields.animationDuration
            animatorSet2.addListener(object : AnimatorListenerAdapter() {})
            animatorSet2.start()

            val handler = Handler()
            handler.postDelayed({
                textView?.visibility = VISIBLE
                val animatorSet2 = AnimatorSet()
                animatorSet2.playTogether(
                    ObjectAnimator.ofFloat(
                        textView,
                        "translationY",
                        -(textAnimationFields.textSize * 3f),
                        0f
                    )
                )
                animatorSet2.duration = textAnimationFields.animationDuration
                animatorSet2.interpolator = LinearInterpolator()
                animatorSet2.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        if (actualNo!! < loopNo) textView?.text =
                            " " + (loopNo - 1) + " " else textView?.text =
                            " " + (loopNo + 1) + " "
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (actualNo!! < loopNo) animateTexts(
                            actualNo,
                            loopNo - 1,
                            textView,
                            textViewOut
                        ) else animateTexts(actualNo, loopNo + 1, textView, textViewOut)
                    }
                })
                animatorSet2.start()
            }, textAnimationFields.gapBetweenTwoNumbersDuration)
        }
    }

    private fun setTextField(newValue: String) {
        var newTrimmedValue = newValue.trim()
        if (newValue.length != minLength)
            newTrimmedValue = newTrimmedValue.padStart(minLength, '0')
        if(newTrimmedValue.isNotBlank()) {
            val nArray: CharArray = newTrimmedValue.toCharArray()
            textAnimationFields.maxNumbers = nArray.size
            previousNumber = IntArray(max(textAnimationFields.maxNumbers, previousNumber.size))
            animatingNumbers?.let {
                System.arraycopy(it, 0, previousNumber, 0, animatingNumbers!!.size)
            }
            animatingNumbers = IntArray(nArray.size)
            if (nArray[0] == '-') {
                animatingNumbers?.let {
                    for (i in newTrimmedValue.indices) {
                        it[i] = 0
                    }
                }
            } else {
                for (i in newTrimmedValue.indices) {
                    animatingNumbers!![i] = try {
                        ("" + nArray[i]).toInt()
                    } catch (e: Exception) {
                        0
                    }
                }
            }
            val icon = displayCounterLinearLayout.getChildAt(0)
            displayCounterLinearLayout.removeAllViews()
            displayCounterLinearLayout.addView(icon)
        }
    }

    fun setInitialData(initialNumber: String) {
        val newInitialNumber = initialNumber.trim()
        if(newInitialNumber.isNotBlank()) {
            val nArray: CharArray = newInitialNumber.toCharArray()
            textAnimationFields.maxNumbers = nArray.size
            previousNumber = IntArray(textAnimationFields.maxNumbers)
            animatingNumbers?.let { System.arraycopy(it, 0, previousNumber, 0, 4) }
            animatingNumbers = IntArray(7)
            for (i in newInitialNumber.indices) {
                animatingNumbers!![i] = ("" + nArray[i]).toInt()
            }
        }
        createNumberOfTextFields()
    }

     fun updateValue(newValue: String) {
         setTextField(newValue)
         createNumberOfTextFields()
    }

    fun zoomInWinning() {
        badgeImageView.zoomInZoomOut()
    }
}
