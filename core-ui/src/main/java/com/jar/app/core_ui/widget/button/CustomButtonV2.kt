package com.jar.app.core_ui.widget.button

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.text.Spannable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewPropertyAnimator
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.use
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import com.jar.app.base.util.dp
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.LayoutCustomButtonV2Binding

class CustomButtonV2 @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attributeSet, defStyleAttr) {

    companion object {
        private const val DEFAULT_ELEVATION = 0
        private const val DEFAULT_DRAWABLE_PADDING = 4f
        private const val DEFAULT_PRIMARY_BORDER_WIDTH = 1
        private const val DEFAULT_SECONDARY_HOLLOW_BORDER_WIDTH = 2
        private const val DEFAULT_SECONDARY_BORDER_WIDTH = 2
        private const val DEFAULT_PRIMARY_BACKGROUND_COLOR = "#6038CE"
        private const val DEFAULT_SECONDARY_BACKGROUND_COLOR = "#383250"
        private const val DEFAULT_SECONDARY_HOLLOW_BACKGROUND_COLOR = "#272239"
        private const val DEFAULT_SECONDARY_STROKE_COLOR = "#00000000"
        private const val DEFAULT_PRIMARY_STROKE_GRADIENT_START_COLOR = "#845fe9"
        private const val DEFAULT_PRIMARY_STROKE_GRADIENT_CENTER_COLOR = "#7349e6"
        private const val DEFAULT_SECONDARY_HOLLOW_STROKE_COLOR = "#846FC0"
        private const val DEFAULT_DIMMING_FACTOR = 0.3f
        private const val DEFAULT_PRIMARY_RADIUS = 12
        private const val DEFAULT_SECONDARY_RADIUS = 12
        private const val DEFAULT_SECONDARY_HOLLOW_RADIUS = 12
    }

    private val binding: LayoutCustomButtonV2Binding

    private var customAnimation: ViewPropertyAnimator? = null
    private var isDisabled = false
    private var isButtonTextBold = false
    private var isButtonDisabled = false
    private var buttonType: ButtonType? = null

    private var buttonText:String?=null
    private var buttonTextAllCaps:Boolean = false
    private var buttonTextColor:Int = Color.WHITE
    private var textViewMarginStart:Float = 0f
    private var textViewMarginEnd:Float = 0f
    private var textViewMarginTop:Float = 0f
    private var textViewMarginBottom:Float = 0f

    private var drawableStart:Drawable? = null
    private var drawableEnd:Drawable? = null
    private var buttonDrawablePadding:Float = DEFAULT_DRAWABLE_PADDING
    private var drawableTintColor:Int = 0
    private var buttonBackgroundColor:Int = 0
    private var buttonBorderWidth:Float = 0f
    private var buttonCornerRadius:Float = 0f
    private var buttonElevation:Float = 0f
    private var buttonTextSize:Int = 0
    private var buttonStrokeColor:Int = 0

    init {
        removeAllViews()
        binding = LayoutCustomButtonV2Binding.inflate(LayoutInflater.from(context), this, true)
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CustomButtonV2,
            defStyleAttr,
            0
        ).use {
            extractValues(it)
            setValues()
        }
        setupListener()
    }

    private fun extractValues(typedArray: TypedArray) {
        buttonText = typedArray.getString(R.styleable.CustomButtonV2_buttonText)
        buttonTextAllCaps = typedArray.getBoolean(R.styleable.CustomButtonV2_buttonTextAllCaps, false)
        buttonTextColor = typedArray.getColor(R.styleable.CustomButtonV2_buttonTextColor, Color.WHITE)
        val layoutParams = binding.textView.layoutParams as MarginLayoutParams
        textViewMarginEnd = typedArray.getDimension(
            R.styleable.CustomButtonV2_textMarginEnd,
            layoutParams.marginEnd.toFloat()
        )
        textViewMarginTop = typedArray.getDimension(
            R.styleable.CustomButtonV2_textMarginTop,
            layoutParams.topMargin.toFloat()
        )
        textViewMarginBottom = typedArray.getDimension(
                R.styleable.CustomButtonV2_textMarginBottom,
                layoutParams.bottomMargin.toFloat()
            )
        textViewMarginStart = typedArray.getDimension(
            R.styleable.CustomButtonV2_textMarginStart,
            layoutParams.marginStart.toFloat()
        )
        drawableStart = typedArray.getDrawable(R.styleable.CustomButtonV2_buttonDrawableStart)
       drawableEnd = typedArray.getDrawable(R.styleable.CustomButtonV2_buttonDrawableEnd)
        buttonDrawablePadding =
            typedArray.getDimension(R.styleable.CustomButtonV2_buttonDrawablePadding, DEFAULT_DRAWABLE_PADDING)
        drawableTintColor = typedArray.getColor(
            R.styleable.CustomButtonV2_buttonDrawableTint,
            0
        )
        isButtonTextBold = typedArray.getBoolean(R.styleable.CustomButtonV2_buttonTextBold, false)
       buttonTextSize =
            typedArray.getDimensionPixelSize(R.styleable.CustomButtonV2_buttonTextSize, 0)
        isButtonDisabled =
            typedArray.getBoolean(R.styleable.CustomButtonV2_buttonDisabled, false)
        buttonType = ButtonType.fromParams(
            typedArray.getInt(
                R.styleable.CustomButtonV2_buttonType,
                ButtonType.primaryButton.ordinal
            )
        )

        buttonElevation = typedArray.getDimension(
            R.styleable.CustomButtonV2_buttonElevation,
            DEFAULT_ELEVATION.dp.toFloat()
        )
        buttonType?.let {
            buttonBackgroundColor = typedArray.getColor(
                R.styleable.CustomButtonV2_buttonBackgroundColor,
                getDefaultButtonBackgroundColor(it)
            )
            buttonBorderWidth = typedArray.getDimension(
                R.styleable.CustomButtonV2_buttonBorderWidth,
                getDefaultButtonBorderWidth(it).toFloat()
            )
            buttonCornerRadius = typedArray.getDimension(
                R.styleable.CustomButtonV2_buttonRadius,
                getDefaultButtonRadius(it).toFloat()
            )
            buttonStrokeColor = typedArray.getColor(
                R.styleable.CustomButtonV2_buttonStrokeColor,
                getDefaultButtonStrokeColor(it)
            )
        }
    }

    private fun reInitButtonBackgroundDefaults(){
        buttonType?.let {
            buttonBackgroundColor = getDefaultButtonBackgroundColor(it)
            buttonBorderWidth = getDefaultButtonBorderWidth(it).toFloat()
            buttonCornerRadius = getDefaultButtonRadius(it).toFloat()
            buttonStrokeColor = getDefaultButtonStrokeColor(it)
        }
    }
    private fun setValues(){
        buttonText?.let {
            binding.textView.text = it
        }
        binding.textView.setTextColor(buttonTextColor)
        binding.textView.isAllCaps = buttonTextAllCaps
        binding.textView.updateLayoutParams<MarginLayoutParams> {
            marginStart = textViewMarginStart.toInt()
            marginEnd = textViewMarginEnd.toInt()
            topMargin = textViewMarginTop.toInt()
            bottomMargin = textViewMarginBottom.toInt()
        }
        binding.textView.compoundDrawablePadding = buttonDrawablePadding.toInt()
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            drawableStart,
            null,
            drawableEnd,
            null
        )
        if (drawableTintColor != 0) {
            TextViewCompat.setCompoundDrawableTintList(
                binding.textView,
                ColorStateList.valueOf(drawableTintColor)
            )
        }
        this.elevation = buttonElevation
        this.cardElevation = buttonElevation

        if (isButtonTextBold) {
            setTypeface(R.font.inter_bold)
        }
        if (buttonTextSize > 0f) {
            binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize.toFloat())
        }
        buttonType?.let {
            renderForButtonType(it)
        }
        setDisabled(isButtonDisabled)
    }

    private fun renderForButtonType(buttonType: ButtonType){
      when(buttonType){
          ButtonType.primaryButton -> {
              setButtonStyleWithDrawableStroke(
                  buttonBackgroundColor,
                  buttonCornerRadius,
                  buttonBorderWidth.toInt()
              )
          }
          ButtonType.secondaryButton -> {
              setButtonStyle(
                  buttonBackgroundColor,
                  buttonCornerRadius,
                  buttonStrokeColor,
                  buttonBorderWidth.toInt()
              )
          }
          ButtonType.secondaryHollowButton -> {
              setButtonStyle(
                  buttonBackgroundColor,
                  buttonCornerRadius,
                  buttonStrokeColor,
                  buttonBorderWidth.toInt()
              )
          }
      }
    }

    fun setTypeface(@FontRes fontIdRes:Int) {
        try {
            val typeface = ResourcesCompat.getFont(context, fontIdRes)
            binding.textView.typeface = typeface
        } catch (_: Exception) {
            binding.textView.typeface = Typeface.DEFAULT
        }

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

    fun setCustomButtonStyle(type: ButtonType) {
        this.buttonType = type
        reInitButtonBackgroundDefaults()
        renderForButtonType(type)
    }

    fun setCompoundDrawablesRelativeWithIntrinsicBounds(icon: Drawable, top: Drawable? = null, end: Drawable? = null, bottom: Drawable? = null) {
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, top, end, bottom)
    }

    fun clearCompoundDrawablesRelativeWithIntrinsicBounds() {
        binding.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
    }


    private fun getDefaultButtonBackgroundColor(type: ButtonType):Int{
        return when(type){
            ButtonType.primaryButton -> Color.parseColor(DEFAULT_PRIMARY_BACKGROUND_COLOR)
            ButtonType.secondaryButton -> Color.parseColor(DEFAULT_SECONDARY_BACKGROUND_COLOR)
            ButtonType.secondaryHollowButton -> Color.parseColor(DEFAULT_SECONDARY_HOLLOW_BACKGROUND_COLOR)
        }
    }
    private fun getDefaultButtonRadius(type: ButtonType):Int{
        return when(type){
            ButtonType.primaryButton -> DEFAULT_PRIMARY_RADIUS
            ButtonType.secondaryButton -> DEFAULT_SECONDARY_RADIUS
            ButtonType.secondaryHollowButton -> DEFAULT_SECONDARY_HOLLOW_RADIUS
        }
    }

    private fun getDefaultButtonBorderWidth(type: ButtonType):Int{
        return when(type){
            ButtonType.primaryButton -> DEFAULT_PRIMARY_BORDER_WIDTH
            ButtonType.secondaryButton -> DEFAULT_SECONDARY_BORDER_WIDTH
            ButtonType.secondaryHollowButton -> DEFAULT_SECONDARY_HOLLOW_BORDER_WIDTH
        }
    }
    private fun getDefaultButtonStrokeColor(type: ButtonType):Int{
        return when(type){
            ButtonType.primaryButton -> 0 //in case of primary button it's gradient Colors
            ButtonType.secondaryButton -> Color.parseColor(DEFAULT_SECONDARY_STROKE_COLOR)
            ButtonType.secondaryHollowButton -> Color.parseColor(DEFAULT_SECONDARY_HOLLOW_STROKE_COLOR)
        }
    }

    private fun setButtonStyleWithDrawableStroke(
        backgroundColor: Int,
        radius: Float,
        strokeWidth: Int
    ) {
        this.radius = radius.dp
        val layerDrawable = LayerDrawable(arrayOf(
            createPrimaryButtonGradientStrokeDrawable(backgroundColor,radius),
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = radius.dp
                setColor(backgroundColor)
            })
        )
        layerDrawable.setLayerInset(1, strokeWidth.dp, strokeWidth.dp, strokeWidth.dp, strokeWidth.dp)
        this.setCardBackgroundColor(backgroundColor)
        binding.root.background = layerDrawable
    }

    private fun setButtonStyle(
        backgroundColor: Int,
        radius: Float,
        strokeColor: Int,
        strokeWidth: Int
    ) {
        val ctaBackground = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(backgroundColor, backgroundColor)
        ).apply {
            cornerRadius = radius.dp
            setStroke(strokeWidth.dp, strokeColor)
        }
        this.radius = radius.dp
        this.setCardBackgroundColor(backgroundColor)
        binding.root.background = ctaBackground
    }

    private fun createPrimaryButtonGradientStrokeDrawable(backgroundColor: Int, radius: Float): GradientDrawable {
        val gradientColors = intArrayOf(
            Color.parseColor(DEFAULT_PRIMARY_STROKE_GRADIENT_START_COLOR),
            Color.parseColor(DEFAULT_PRIMARY_STROKE_GRADIENT_CENTER_COLOR),
            backgroundColor
        )
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius.dp
            colors = gradientColors
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    }

    fun setText(text: Spannable) {
        binding.textView.text = text
    }

    fun setText(text: String) {
        this.buttonText = text
        binding.textView.text = text
    }

    fun setTextMargin(start:Float?,end:Float?,top:Float?,bottom:Float?){

        binding.textView.updateLayoutParams<MarginLayoutParams> {
            start?.let{
                marginStart = it.toInt()
            }
            end?.let{
                marginEnd = it.toInt()
            }
           top?.let{
               topMargin = it.toInt()
           }
            bottom?.let{
                bottomMargin = it.toInt()
            }
        }
        postInvalidate()
    }

    fun getText(): String = binding.textView.text.toString()

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

    fun setButtonTextAllCaps(shouldMakeCaps: Boolean) {
        binding.textView.isAllCaps = shouldMakeCaps
        postInvalidate()
    }

    fun setDisabled(isDisabled: Boolean) {
        this.isDisabled = isDisabled
        isClickable = !isDisabled
        isEnabled = !isDisabled
        setDimmed(isDisabled)
        invalidate()
    }

    fun setBackGroundColor(color: Int) {
        buttonBackgroundColor = color
        invalidate()
    }
}