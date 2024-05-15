package com.jar.app.core_ui.widget.button

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewPropertyAnimator
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.jar.app.base.util.dp
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.LayoutCustomButtonBinding

class CustomButton : CardView {

    companion object {
        private const val DEFAULT_RADIUS = 45
        private const val DEFAULT_ELEVATION = 8
        private const val DEFAULT_START_COLOR = "#A841FF"
        private const val DEFAULT_END_COLOR = "#36A2FF"
        private const val DEFAULT_STROKE_COLOR = "#00FFFFFF"
        private const val SECONDARY_START_COLOR = "#272239"
        private const val SECONDARY_END_COLOR = "#272239"
        private const val SECONDARY_STROKE_COLOR = "#40FFFFFF"
        private const val DEFAULT_SCALE_DOWN_FACTOR = 0.97f
        private const val DEFAULT_DIMMING_FACTOR = 0.5f
    }

    private lateinit var binding: LayoutCustomButtonBinding

    private var customAnimation: ViewPropertyAnimator? = null
    private var isDisabled = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init(attributeSet)
    }

    private fun init() {
        binding =
            LayoutCustomButtonBinding.inflate(LayoutInflater.from(context), this, false)
        this.addView(binding.root)
    }

    private fun init(attributeSet: AttributeSet) {
        init()
        setupListener()

        val typedArray =
            context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.CustomButton,
                0,
                0
            )
        setCustomButtonStyle(typedArray)
        setButtonText(typedArray)
        typedArray.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        this.setOnTouchListener { view, motionEvent ->
            if (isDisabled) return@setOnTouchListener false
            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    customAnimation?.cancel()
                    customAnimation = view.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100)
                    customAnimation?.start()
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    customAnimation?.cancel()
                    customAnimation = view.animate().scaleX(1f).scaleY(1f).setDuration(100)
                    customAnimation?.start()
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun setCustomButtonStyle(typedArray: TypedArray) {
        this.elevation = DEFAULT_ELEVATION.dp.toFloat()
        this.cardElevation = DEFAULT_ELEVATION.dp.toFloat()
        this.setCardBackgroundColor(Color.TRANSPARENT)

        val buttonType = ButtonType.fromParams(
            typedArray.getInt(
                R.styleable.CustomButton_buttonType,
                ButtonType.primaryButton.ordinal
            )
        )
        setButtonType(typedArray, buttonType)
    }

    fun setCustomButtonStyle(type: Int) {
        val typedArray = context.obtainStyledAttributes(
            null,
            R.styleable.CustomButton,
            0,
            0
        )

        val buttonType = ButtonType.fromParams(type)
        setButtonType(typedArray,buttonType)
    }
    private fun setButtonType(typedArray: TypedArray, buttonType: ButtonType){
        this.elevation = DEFAULT_ELEVATION.dp.toFloat()
        this.cardElevation = DEFAULT_ELEVATION.dp.toFloat()
        this.setCardBackgroundColor(Color.TRANSPARENT)

        var startColor = 0
        var endColor = 0
        var strokeColor = 0
        var borderWidth = 0f

        /**SET DEFAULT VALUES BASED ON BUTTON TYPE**/
        when (buttonType) {
            ButtonType.primaryButton -> {
                startColor =
                    typedArray.getColor(
                        R.styleable.CustomButton_buttonStartColor,
                        Color.parseColor(DEFAULT_START_COLOR)
                    )

                endColor =
                    typedArray.getColor(
                        R.styleable.CustomButton_buttonEndColor,
                        Color.parseColor(DEFAULT_END_COLOR)
                    )
                strokeColor =
                    typedArray.getColor(
                        R.styleable.CustomButton_buttonStrokeColor,
                        Color.parseColor(DEFAULT_STROKE_COLOR)
                    )
                borderWidth =
                    typedArray.getDimension(R.styleable.CustomButton_buttonBorderWidth, 0f)
            }
            ButtonType.secondaryButton -> {
                startColor =
                    typedArray.getColor(
                        R.styleable.CustomButton_buttonStartColor,
                        Color.parseColor(SECONDARY_START_COLOR)
                    )

                endColor =
                    typedArray.getColor(
                        R.styleable.CustomButton_buttonEndColor,
                        Color.parseColor(SECONDARY_END_COLOR)
                    )

                strokeColor =
                    typedArray.getColor(
                        R.styleable.CustomButton_buttonStrokeColor,
                        Color.parseColor(SECONDARY_STROKE_COLOR)
                    )
                borderWidth =
                    typedArray.getDimension(R.styleable.CustomButton_buttonBorderWidth, 1f)
            }

            ButtonType.secondaryHollowButton -> {
                //Old button does not support secondary hollow style. @Refer: CustomButtonV2.kt
            }
        }

        val radius =
            typedArray.getDimension(R.styleable.CustomButton_buttonRadius, DEFAULT_RADIUS.toFloat())
                .toInt()


        setButtonStyle(startColor, endColor, radius, strokeColor, borderWidth.toInt())

        val isButtonDimmed = typedArray.getBoolean(R.styleable.CustomButton_buttonDimmed, false)

        setDimmed(isButtonDimmed)

        val isButtonDisabled = typedArray.getBoolean(R.styleable.CustomButton_buttonDisabled, false)

        setDisabled(isButtonDisabled)
    }

    private fun setButtonText(typedArray: TypedArray) {
        val buttonText = typedArray.getString(R.styleable.CustomButton_buttonText)
        val buttonTextAllCaps =
            typedArray.getBoolean(R.styleable.CustomButton_buttonTextAllCaps, false)
        val buttonTextColor =
            typedArray.getColor(R.styleable.CustomButton_buttonTextColor, Color.WHITE)

        binding.textView.setTextColor(buttonTextColor)
        binding.textView.isAllCaps = buttonTextAllCaps
        binding.textView.text = buttonText

        val drawableStart = typedArray.getDrawable(R.styleable.CustomButton_buttonDrawableStart)
        val drawableEnd = typedArray.getDrawable(R.styleable.CustomButton_buttonDrawableEnd)
        val buttonDrawablePadding =
            typedArray.getDimension(R.styleable.CustomButton_buttonDrawablePadding, 4f)

        binding.textView.compoundDrawablePadding = buttonDrawablePadding.toInt()
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawableStart,
            null,
            drawableEnd,
            null
        )
        val drawableTintColor =  typedArray.getColor(
            R.styleable.CustomButtonV2_buttonDrawableTint,
            0
        )
        if (drawableTintColor!=0){
            TextViewCompat.setCompoundDrawableTintList(binding.textView, ColorStateList.valueOf(drawableTintColor))
        }
    }

    fun setButtonStyle(
        startColorHex: String? = DEFAULT_START_COLOR,
        endColorHex: String ? = DEFAULT_END_COLOR,
        radius: Int,
        strokeColor: Int? = null
    ) {
        val ctaBackground = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor(startColorHex),
                Color.parseColor(endColorHex),
            )
        )
        if (strokeColor != null)
            ctaBackground.setStroke(1.dp, strokeColor)
        ctaBackground.cornerRadius = radius.dp.toFloat()
        this.radius = radius.dp.toFloat()
        binding.root.background = ctaBackground
    }

    private fun setButtonStyle(
        startColor: Int,
        endColor: Int,
        radius: Int,
        borderColor: Int,
        borderWidth: Int,
    ) {
        val ctaBackground = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(startColor, endColor)
        )
        ctaBackground.cornerRadius = radius.dp.toFloat()

        ctaBackground.setStroke(borderWidth.dp, borderColor)
        this.radius = radius.dp.toFloat()
        binding.root.background = ctaBackground
    }

    fun setText(text: Spannable?) {
        binding.textView.text = text
    }

    fun setText(text: String?) {
        binding.textView.text = text
    }

    fun getText(): String = binding.textView.text.toString()

    fun setIcon(icon: Drawable) {
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null)
    }

    fun clearIcon() {
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
    }

    fun setDimmed(isDimmed: Boolean) {
        binding.root.alpha = if (isDimmed) DEFAULT_DIMMING_FACTOR else 1f
    }

    fun setDrawableStart(@DrawableRes drawableRes: Int) {
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawableRes,
            0,
            0,
            0
        )
    }

    fun setDrawableEnd(@DrawableRes drawableRes: Int) {
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            drawableRes,
            0
        )
    }

    fun setDisabled(isDisabled: Boolean) {
        this.isDisabled = isDisabled
        isClickable = !isDisabled
        isEnabled = !isDisabled
        setDimmed(isDisabled)
        invalidate()
    }

    fun setFont(inter: Int) {
        val font = ResourcesCompat.getFont(this.context, inter)
        binding.textView.setTypeface(font)
    }
}