package com.jar.app.core_ui.widget.credit_card_view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.dp
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.*
import com.jar.app.core_utils.data.*
import com.wajahatkarim3.easyflipview.EasyFlipView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class CreditCardView : ConstraintLayout {

    private var onCardItemFocusListener: OnCardItemFocusListener? = null

    private var onCardDetailsChangedListener: OnCardDetailsChangedListener? = null

    private var etCardNumber: AppCompatEditText? = null

    private var etCardName: AppCompatEditText? = null

    private var etCardExpiry: AppCompatEditText? = null

    private var etCvv: AppCompatEditText? = null

    private var ivCardTypeFront: AppCompatImageView? = null

    private var ivCardTypeBack: AppCompatImageView? = null

    private var tvBankNameFront: AppCompatTextView? = null

    private var tvBankNameBack: AppCompatTextView? = null

    private var flipView: EasyFlipView? = null

    private var outline: View? = null

    private var widthAnimator: ValueAnimator? = null

    private var heightAnimator: ValueAnimator? = null

    private var translateAnimator: ViewPropertyAnimator? = null

    private var separator = ""

    private val uiScope = CoroutineScope(Dispatchers.Main)

    private var isCardEditMode = true

    private var isCardValidFromApi = false

    companion object {
        private const val IMAGE_BASE_URL = "${BaseConstants.CDN_BASE_URL}/CardTypes/"
    }

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
        val layout =
            LayoutInflater.from(context).inflate(R.layout.layout_credit_card_view, this, false)
        this.addView(layout)
    }

    private fun init(attributeSet: AttributeSet) {
        init()

        val typedArray =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.CreditCardView, 0, 0)

        /**
         ** Outline View
         **/
        val outlineBaseColor =
            typedArray.getColor(R.styleable.CreditCardView_card_outline_base_color, Color.WHITE)

        val outlineErrorColor =
            typedArray.getColor(R.styleable.CreditCardView_card_outline_error_color, Color.RED)

        outline = findViewById(R.id.outline)

        flipView = findViewById(R.id.flipView)

        ivCardTypeFront = findViewById(R.id.ivCardTypeFront)

        ivCardTypeBack = findViewById(R.id.ivCardTypeBack)

        tvBankNameFront = findViewById(R.id.front_tvBankName)

        tvBankNameBack = findViewById(R.id.back_tvBankName)

        isCardEditMode = typedArray.getBoolean(R.styleable.CreditCardView_card_isInEditMode, true)

        val tooltip = findViewById<AppCompatTextView>(R.id.tooltipNumber)

        tooltip.text = typedArray.getString(R.styleable.CreditCardView_card_tooltip_text)

        /**
         ** Card Front View
         **/
        val frontGradientStart =
            typedArray.getColor(R.styleable.CreditCardView_card_frontGradientStart, Color.BLACK)

        val frontGradientEnd =
            typedArray.getColor(R.styleable.CreditCardView_card_frontGradientEnd, Color.BLACK)

        val view = findViewById<View>(R.id.clTopView)

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(frontGradientStart, frontGradientEnd)
        )

        view.background = gradient

        /**
         ** Card Background
         **/
        val backGradientStart =
            typedArray.getColor(R.styleable.CreditCardView_card_backGradientStart, Color.BLACK)

        val backGradientEnd =
            typedArray.getColor(R.styleable.CreditCardView_card_backGradientEnd, Color.BLACK)

        val backStripColor =
            typedArray.getColor(R.styleable.CreditCardView_card_backStripColor, Color.BLACK)

        val backStrip2Color =
            typedArray.getColor(R.styleable.CreditCardView_card_backStrip2Color, Color.BLACK)

        findViewById<View>(R.id.strip2).backgroundTintList = ColorStateList.valueOf(backStrip2Color)

        val back = findViewById<View>(R.id.clBackView)

        val gradientBack = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(backGradientStart, backGradientStart, backGradientEnd)
        )

        back.background = gradientBack

        setColor(R.id.strip1, backStripColor)

        /**
         ** Card CVV
         **/
        val cvvBackgroundColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cvvBackgroundColor, Color.WHITE)

        val cvvHintColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cvvHintColor, Color.WHITE)

        val cvvTextColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cvvTextColor, Color.WHITE)

        etCvv = findViewById(R.id.etCvv)
        etCvv?.backgroundTintList = ColorStateList.valueOf(cvvBackgroundColor)
        etCvv?.setTextColor(cvvTextColor)
        etCvv?.setHintTextColor(cvvHintColor)

        etCvv?.doOnTextChanged { text, _, _, _ ->
            onCardDetailsChangedListener?.onCardCvvChangedListener(text)
        }

        etCvv?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                onCardItemFocusListener?.onCardCvvFocus()
        }

        /**
         ** Card Number
         **/
        val cardNumberTextColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardNumberTextColor, Color.WHITE)

        val cardNumberHintColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardNumberHintColor, Color.WHITE)

        etCardNumber = findViewById(R.id.etCardNumber)

        etCardNumber?.setHintTextColor(cardNumberHintColor)
        etCardNumber?.setTextColor(cardNumberTextColor)

        separator = typedArray.getString(R.styleable.CreditCardView_card_separator) ?: " "

        if (isCardEditMode) {
            etCardNumber?.keyboardVisibilityChanges()
                ?.debounce(1000)
                ?.onEach { visible ->
                    tooltip.isVisible = visible.not() && etCardNumber?.text.isNullOrBlank()
                }
                ?.launchIn(uiScope)
        }

        if (isCardEditMode) {
            etCardNumber?.addTextChangedListener(
                CreditCardTextFormatter(
                    separator = separator,
                    textColor = cardNumberTextColor
                )
            )
        }

        etCardNumber?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                animateOutline(view)
                onCardItemFocusListener?.onCardNumberFocus()
            }
        }

        etCardNumber?.textChanges()
            ?.debounce(500)
            ?.onEach {
                when {
                    it.isNullOrBlank() -> updateOutlineState(outlineBaseColor)
                    isCardNumberValid() -> {
                        updateOutlineState(outlineBaseColor)
                    }
                    else -> updateOutlineState(outlineErrorColor)
                }
            }
            ?.launchIn(uiScope)

        etCardNumber?.doOnTextChanged { text, _, _, _ ->
            val valid = isCardNumberValid()
            updateEditTextState(etCardName, enabled = valid)
            updateEditTextState(etCardExpiry, enabled = valid)
            onCardDetailsChangedListener?.onCardNumberChangedListener(getRawCardNumber())
        }

        /**
         ** Card Name Header
         **/
        val cardNameHeaderColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardNameHeaderColor, Color.WHITE)
        setTVTextColor(R.id.tvHeaderName, cardNameHeaderColor)

        /**
         ** Card Name
         **/
        val cardNameTextColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardNameTextColor, Color.WHITE)

        val cardNameHintColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardNameHintColor, Color.WHITE)

        etCardName = findViewById(R.id.tvName)
        etCardName?.setHintTextColor(cardNameHintColor)
        etCardName?.setTextColor(cardNameTextColor)
        if (isCardEditMode) {
            etCardName?.filters = arrayOf(
                AlphabetOnlyInputFilter(),
                InputFilter.AllCaps()
            )
        }

        etCardName?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                animateOutline(view)
                onCardItemFocusListener?.onCardNameFocus()
            }
        }

        etCardName?.doOnTextChanged { text, _, _, _ ->
            outline?.layoutParams?.width = etCardName?.width.orZero()
            outline?.requestLayout()
            onCardDetailsChangedListener?.onCardNameChangedListener(text)
            updateEditTextState(etCardExpiry, enabled = isCardNameValid())
        }

        etCardName?.textChanges()
            ?.debounce(500)
            ?.onEach {
                when {
                    it.isNullOrBlank() -> updateOutlineState(outlineBaseColor)
                    isCardNameValid() -> {
                        updateOutlineState(outlineBaseColor)
                    }
                    else -> updateOutlineState(outlineErrorColor)
                }
            }
            ?.launchIn(uiScope)

        /**
         ** Card Expiry Date
         **/
        val cardExpiryHeaderColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardExpiryHeaderColor, Color.WHITE)

        val cardExpiryTextColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardExpiryTextColor, Color.WHITE)

        val cardExpiryHintColor =
            typedArray.getColor(R.styleable.CreditCardView_card_cardExpiryHintColor, Color.WHITE)

        setTVTextColor(R.id.tvHeaderExpDate, cardExpiryHeaderColor)

        etCardExpiry = findViewById(R.id.tvExpiry)
        etCardExpiry?.setHintTextColor(cardExpiryHintColor)
        etCardExpiry?.setTextColor(cardExpiryTextColor)

        if (isCardEditMode) {
            etCardExpiry?.filters = arrayOf(
                NumberOnlyInputFilter(),
                MMYYDateFilter()
            )
        }

        etCardExpiry?.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                animateOutline(view)
                onCardItemFocusListener?.onCardExpiryFocus()
            }
        }

        etCardExpiry?.setOnImeActionNextListener {
            if (isCardExpiryValid()) {
                outline?.isVisible = false
                flipView?.flipTheView(true)
            }
        }

        etCardExpiry?.textChanges()
            ?.debounce(500)
            ?.onEach {
                when {
                    it.isNullOrBlank() -> updateOutlineState(outlineBaseColor)
                    isCardExpiryValid() -> updateOutlineState(outlineBaseColor)
                    else -> updateOutlineState(outlineErrorColor)
                }
            }
            ?.launchIn(uiScope)

        etCardExpiry?.doOnTextChanged { text, _, _, _ ->
            onCardDetailsChangedListener?.onCardExpiryChangedListener(text)
        }

        val frontView = findViewById<CardView>(R.id.frontView)
        val backView = findViewById<CardView>(R.id.backView)

        frontView.post {
            val params: ViewGroup.LayoutParams = backView.layoutParams
            params.width = frontView.width
            params.height = frontView.height
            backView.layoutParams = params
        }

        flipView?.setOnFlipListener { _, newCurrentSide ->
            when (newCurrentSide) {
                EasyFlipView.FlipState.BACK_SIDE -> {
                    onCardDetailsChangedListener?.onCardFlipListener(CardSide.BACK)
                    if (isCardEditMode)
                        focusView(CardViewElements.CVV)
                }
                EasyFlipView.FlipState.FRONT_SIDE -> {
                    onCardDetailsChangedListener?.onCardFlipListener(CardSide.FRONT)
                }
            }
        }

        this.setOnClickListener {
            flipView?.flipTheView(true)
        }

        etCvv?.setOnImeActionDoneListener {
            if (isCvvValid()) {
                flipView?.flipTheView(true)
                etCvv?.hideKeyboard()
            }
        }

        val shouldAutoFocusOnCardNumber =
            typedArray.getBoolean(R.styleable.CreditCardView_card_autoFocusCardNumber, false)

        if (shouldAutoFocusOnCardNumber && isCardEditMode) {
            etCardNumber?.postDelayed({
                etCardNumber?.requestFocus()
                etCardNumber?.showKeyboard()
            }, 500)
        }

        if (!isCardEditMode) {
            updateEditTextState(etCardNumber, false)
            updateEditTextState(etCardName, false)
            updateEditTextState(etCardExpiry, false)
        }

        if (isCardEditMode) {
            updateEditTextState(etCardName, false)
            updateEditTextState(etCardExpiry, false)
        }

        typedArray.recycle()

    }


    /***
     * private util functions
     * ***/
    private fun updateEditTextState(editText: AppCompatEditText?, enabled: Boolean) {
        editText?.isEnabled = enabled
        editText?.isClickable = enabled
    }

    private fun setColor(@IdRes id: Int, color: Int) {
        findViewById<View>(id).setBackgroundColor(color)
    }

    private fun setTVHintColor(@IdRes id: Int, color: Int) {
        findViewById<AppCompatTextView>(id).setHintTextColor(color)
    }

    private fun setTVTextColor(@IdRes id: Int, color: Int) {
        findViewById<AppCompatTextView>(id).setTextColor(color)
    }

    fun isCardFrontDetailsFilled(): Boolean {
        return isCardNumberValid() && isCardNameValid() && isCardExpiryValid()
    }

    fun isCardNumberValid(): Boolean {
        return etCardNumber?.text?.length == 19 && isCardValidFromApi
    }

    fun isCardNameValid(): Boolean {
        return !etCardName?.text.isNullOrBlank()
    }

    fun flipCard(cardSide: CardSide) {
        when (cardSide) {
            CardSide.BACK -> {
                if (flipView?.isBackSide == false)
                    flipView?.flipTheView(true)
            }
            CardSide.FRONT -> {
                if (flipView?.isFrontSide == false)
                    flipView?.flipTheView(true)
            }
        }
    }

    fun focusView(cardViewElements: CardViewElements) {
        when (cardViewElements) {
            CardViewElements.NUMBER -> {
                setFocusToView(etCardNumber, EasyFlipView.FlipState.FRONT_SIDE)
            }
            CardViewElements.NAME -> {
                setFocusToView(etCardName, EasyFlipView.FlipState.FRONT_SIDE)
            }
            CardViewElements.EXPIRY -> {
                setFocusToView(etCardExpiry, EasyFlipView.FlipState.FRONT_SIDE)
            }
            CardViewElements.CVV -> {
                setFocusToView(etCvv, EasyFlipView.FlipState.BACK_SIDE)
            }
        }
    }

    private fun setFocusToView(
        view: View?,
        flipState: EasyFlipView.FlipState
    ) {
        view?.let {
            it.requestFocus()
            it.showKeyboard()
        }
        if (flipView?.currentFlipState != flipState)
            flipView?.flipTheView(true)
    }

    fun isCardExpiryValid(): Boolean {
        val mmYY = etCardExpiry?.text
        val temp = mmYY?.split('/')
        val mm = temp?.getOrNull(0)?.toIntOrNull().orZero()
        val yy = temp?.getOrNull(1)?.toIntOrNull().orZero()
        val calendar = Calendar.getInstance()
        val currentYear =
            calendar.get(Calendar.YEAR) % 100 //For last two digits i.e. 2022 % 100 = 22
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return if (yy > currentYear)
            (mm in 1..12) && (yy >= currentYear)
        else
            (mm in currentMonth..12) && (yy >= currentYear)
    }

    fun isCvvValid(): Boolean {
        return etCvv?.text?.length == 3 || etCvv?.text?.length == 4
    }

    private fun animateOutline(toView: View) {
        outline?.isVisible = true
        widthAnimator?.cancel()
        heightAnimator?.cancel()
        translateAnimator?.cancel()

        val duration = 200L
        val newWidth = toView.width
        val newHeight = toView.height
        widthAnimator = ValueAnimator.ofInt(outline?.width.orZero(), newWidth)
        widthAnimator?.duration = duration
        widthAnimator?.interpolator = LinearInterpolator()
        widthAnimator?.addUpdateListener {
            outline?.layoutParams?.width = it.animatedValue as Int
            outline?.requestLayout()
        }
        widthAnimator?.start()

        heightAnimator = ValueAnimator.ofInt(outline?.height.orZero(), newHeight)
        heightAnimator?.duration = duration
        heightAnimator?.interpolator = LinearInterpolator()
        heightAnimator?.addUpdateListener {
            outline?.layoutParams?.height = it.animatedValue as Int
            outline?.requestLayout()
        }
        heightAnimator?.start()

        translateAnimator = outline?.animate()
            ?.x(toView.x)
            ?.y(toView.y)
            ?.setDuration(duration)
            ?.setInterpolator(LinearInterpolator())
        translateAnimator?.start()
    }

    private fun updateOutlineState(color: Int) {
        val drawable = GradientDrawable()
        drawable.setStroke(1.dp, color)
        drawable.cornerRadius = 5.dp.toFloat()
        outline?.background = drawable
    }

    /*** ***/


    /***
     * Input Field Getters & Setters
     * ***/
    fun setCardNumber(number: String) = etCardNumber?.setText(number)

    fun getCardNumber() = etCardNumber?.text?.toString()

    fun getRawCardNumber() = etCardNumber?.text?.toString()?.replace(separator, "")

    fun setCardName(name: String) = etCardName?.setText(name)

    fun getCardName() = etCardName?.text?.toString()

    fun setCardExpiry(expiry: String) = etCardExpiry?.setText(expiry)

    fun getCardExpiry() = etCardExpiry?.text?.toString()

    fun setCvv(cvv: String) = etCvv?.setText(cvv)

    fun getCardExpiryMonth(): Int {
        val mmYY = etCardExpiry?.text
        val temp = mmYY?.split('/')
        return temp?.getOrNull(0)?.toIntOrNull().orZero()
    }

    fun getCardExpiryYear(): Int {
        val mmYY = etCardExpiry?.text
        val temp = mmYY?.split('/')
        return temp?.getOrNull(1)?.toIntOrNull().orZero()
    }

    fun getCvv(): String? {
        return etCvv?.text?.toString()
    }

    fun setCardBrand(cardBrand: CardBrand) {
        if (cardBrand != CardBrand.OTHER) {
            if (ivCardTypeFront != null) {
                Glide.with(this)
                    .load("$IMAGE_BASE_URL${cardBrand.name}.png")
                    .into(ivCardTypeFront!!)
            }
            if (ivCardTypeBack != null) {
                Glide.with(this)
                    .load("$IMAGE_BASE_URL${cardBrand.name}.png")
                    .into(ivCardTypeBack!!)
            }
        } else {
            ivCardTypeFront?.setImageResource(0)
            ivCardTypeBack?.setImageResource(0)
        }
    }

    fun setBankName(bankName: String?) {
        tvBankNameFront?.text = bankName
        tvBankNameBack?.text = bankName
    }

    fun setCardValidFromApi(valid: Boolean) {
        isCardValidFromApi = valid
    }

    /*** ***/


    /***
     * Click Listeners on input fields
     * **/
    interface OnCardItemFocusListener {
        fun onCardNumberFocus()
        fun onCardNameFocus()
        fun onCardExpiryFocus()
        fun onCardCvvFocus()
    }

    fun setOnCardItemFocusListener(onCardItemFocusListener: OnCardItemFocusListener) {
        this.onCardItemFocusListener = onCardItemFocusListener
    }

    interface OnCardDetailsChangedListener {
        fun onCardNumberChangedListener(number: CharSequence?)
        fun onCardNameChangedListener(number: CharSequence?)
        fun onCardExpiryChangedListener(number: CharSequence?)
        fun onCardCvvChangedListener(cvv: CharSequence?)
        fun onCardFlipListener(cardSide: CardSide)
    }

    fun setOnCardDetailsChangedListener(onCardDetailsChangedListener: OnCardDetailsChangedListener) {
        this.onCardDetailsChangedListener = onCardDetailsChangedListener
    }

    fun onDestroy() {
        this.onCardItemFocusListener = null
        this.onCardDetailsChangedListener = null
        widthAnimator?.cancel()
        heightAnimator?.cancel()
        translateAnimator?.cancel()
        uiScope.cancel()
    }

    /*** ***/

    enum class CardViewElements {
        NUMBER, NAME, EXPIRY, CVV
    }

    enum class CardSide {
        FRONT, BACK
    }

    enum class CardBrand {
        AMEX, VISA, MASTERCARD, RUPAY, DINERS, JCB, OTHER
    }
}