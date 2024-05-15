package com.jar.app.core_ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jar.app.base.util.isValidEmail
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.isSpecialCharacters
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.CustomEdittextWithErrorHandlingBinding
import com.jar.app.core_ui.extension.isLetters
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setOnImeActionDoneListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CustomEditTextWithErrorHandling @JvmOverloads constructor(
    private val ctx: Context,
    private val attributeSet: AttributeSet? = null,
    private val defStyleAttr: Int = 0
) : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    companion object {
        const val PAN_LENGTH = 10
        const val AADHAAR_LENGTH = 12
    }

    private var binding: CustomEdittextWithErrorHandlingBinding

    private var uiScope: CoroutineScope? = null
    private var editTextType = EditTextType.DEFAULT

    private var isValidationCompleted: ((Boolean, String?) -> Unit)? = null
    private var onTextClearClicked: (() -> Unit)? = null
    private var customTextValidation: ((String) -> Unit)? = null
    private var onImeActionDoneListener: (() -> Unit)? = null
    private var onEditTextContainerClicked: (() -> Unit)? = null

    init {
        binding =
            CustomEdittextWithErrorHandlingBinding.inflate(LayoutInflater.from(ctx), this, true)
    }

    fun setEditTextEnumType(editTextType: EditTextType, uiScope: CoroutineScope) {
        this.editTextType = editTextType
        this.uiScope = uiScope
        setupUI()
        setClickListener()
    }

    private fun setInitialCharacterCount(lengthLimit: Int) {
        binding.tvCharacterCount.isVisible = true
        binding.tvCharacterCount.text = ctx.getString(
            R.string.core_ui_x_char_out_of_x, 0,
            lengthLimit
        )
    }

    fun setCharacterCount(textLength: Int, lengthLimit: Int) {
        binding.tvCharacterCount.isVisible = true
        binding.tvCharacterCount.text = ctx.getString(
            R.string.core_ui_x_char_out_of_x, textLength,
            lengthLimit
        )
    }

    private fun setupUI() {
        when (editTextType) {
            EditTextType.EMAIL -> {
                setEditTextHint(R.string.core_ui_email_hint)
            }

            EditTextType.PAN -> {
                binding.editText.filters = arrayOf(AllCaps(), LengthFilter(PAN_LENGTH + 2))
                setEditTextHint(R.string.core_ui_pan_hint)
                setInitialCharacterCount(PAN_LENGTH)
            }

            EditTextType.AADHAAR -> {
                setEditTextHint(R.string.core_ui_aadhaar_hint)
                binding.editText.filters = arrayOf(LengthFilter(AADHAAR_LENGTH + 2))
                binding.editText.inputType = InputType.TYPE_CLASS_NUMBER
                binding.editText.keyListener = DigitsKeyListener.getInstance("0123456789 ")
                setInitialCharacterCount(AADHAAR_LENGTH)
            }

            EditTextType.AGE, EditTextType.NUMBER -> {
                binding.clEndContainer.isVisible = false
                binding.editText.inputType = InputType.TYPE_CLASS_NUMBER
                binding.editText.filters = arrayOf(LengthFilter(6))
                binding.editText.keyListener = DigitsKeyListener.getInstance("0123456789")
            }

            EditTextType.NAME -> {

            }

            else -> {}
        }
    }

    fun setEditTextHint(hintRes: Int) {
        binding.editText.hint = " ${resources.getString(hintRes)}"
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListener() {
        binding.editText.textChanges()
            .debounce(100)
            .onEach {
                if (it?.isEmpty().orFalse())
                    isValidationCompleted?.invoke(false, "")
                validateText(it.toString())
            }
            .launchIn(uiScope!!)

        binding.ivClear.setDebounceClickListener {
            onTextClearClicked?.invoke()
            binding.editText.setText(ctx.getString(R.string.core_ui_empty))
            resetDefault()
        }

        binding.editText.setOnImeActionDoneListener {
            onImeActionDoneListener?.invoke()
        }

        binding.root.setDebounceClickListener {
            onEditTextContainerClicked?.invoke()
        }
    }

    private fun validateText(value: String) {
        when (editTextType) {
            EditTextType.EMAIL -> {
                if (value.length > 4)
                    emailValidation(value)
            }

            EditTextType.PAN -> {
                binding.tvCharacterCount.text = ctx.getString(
                    R.string.core_ui_x_char_out_of_x, getRawText().length,
                    PAN_LENGTH
                )
                if (value.length > 4)
                    panValidation(value)
            }

            EditTextType.AADHAAR -> {
                binding.tvCharacterCount.text = ctx.getString(
                    R.string.core_ui_x_char_out_of_x, getRawText().length,
                    AADHAAR_LENGTH
                )
                if (value.length > 4)
                    aadhaarValidation(value)
            }

            EditTextType.NAME -> {
                nameValidation(value)
            }

            EditTextType.AGE -> {
                ageValidation(value)
            }

            else -> {
                customTextValidation?.invoke(value)
            }
        }
    }

    private fun emailValidation(value: String) {
        if (value.last() == ' ') {
            binding.editText.setText(value.trim())
            return
        }
        binding.ivClear.isVisible = true
        binding.tvCharacterCount.isVisible = false
        if (value.trim().isValidEmail().not())
            showError(ctx.getString(R.string.core_ui_invalid_email_address))
        else
            resetDefault()
    }

    private fun panValidation(value: String) {
        binding.ivClear.isVisible = false
        binding.tvCharacterCount.isVisible = true

        if (value.isSpecialCharacters())
            showError(ctx.getString(R.string.core_ui_pan_cannot_have_special_character))
        else if (
            value.length >= 4 && value[3].equals(Char(80/*ASCII for Char P*/), true).not()
        )
            showError(ctx.getString(R.string.core_ui_incorrect_pan_format))
        else if (getRawText().length != PAN_LENGTH)
            showError(ctx.getString(R.string.core_ui_pan_number_should_be_10_char))
        else
            resetDefault()
    }

    private fun aadhaarValidation(value: String) {
        binding.ivClear.isVisible = false
        binding.tvCharacterCount.isVisible = true
        if (value.isLetters())
            showError(ctx.getString(R.string.core_ui_aadhar_cannot_have_char))
        else if (value.isSpecialCharacters())
            showError(ctx.getString(R.string.core_ui_incorrect_aadhaar_format))
        else if (getRawText().length != AADHAAR_LENGTH)
            showError(ctx.getString(R.string.core_ui_aadhar_number_should_be_12_char))
        else
            resetDefault()
    }

    private fun ageValidation(value: String) {
        binding.ivClear.isVisible = false
        binding.tvCharacterCount.isVisible = false
        val ageIntegerValue = value.toInt()
        when {
            ageIntegerValue <= 17 -> {
                binding.tvError.text = ctx.getString(R.string.core_ui_you_must_be_above_18)
            }

            ageIntegerValue > 100 -> {
                binding.tvError.text = ctx.getString(R.string.core_ui_maximum_age_limit_reached)
            }

            else -> resetDefault()
        }
    }

    private fun nameValidation(value: String) {
        binding.ivClear.isVisible = false
        binding.tvCharacterCount.isVisible = false
        if (value.length < 2 || value.isEmpty())
            binding.tvError.text = ctx.getString(R.string.core_ui_empty)
        else
            resetDefault()
    }

    fun showError(errorMessage: String) {
        binding.tvError.isVisible = true
        binding.tvError.text = errorMessage
        binding.clEditTextContainer.background =
            ContextCompat.getDrawable(ctx, R.drawable.core_ui_bg_rounded_2e2942_outline_eb6a6e_10dp)
        isValidationCompleted?.invoke(false, getEditTextValue())
    }

    fun getRawText() = binding.editText.text.toString().replace(" ", "")

    fun resetDefault() {
        binding.tvError.isVisible = false
        binding.clEditTextContainer.background =
            ContextCompat.getDrawable(ctx, R.drawable.core_ui_bg_rounded_2e2942_10dp)
        isValidationCompleted?.invoke(true, getEditTextValue())
    }

    fun setIsValidatedListener(isValidationCompleted: (Boolean, String?) -> Unit) {
        this.isValidationCompleted = isValidationCompleted
    }

    fun setCustomTextValidationListener(customTextValidation: (String?) -> Unit) {
        this.customTextValidation = customTextValidation
    }

    fun setOnClearTextClickedListener(onTextClearClicked: (() -> Unit)) {
        this.onTextClearClicked = onTextClearClicked
    }

    fun setOnImeActionDoneListener(onImeActionDoneListener: (() -> Unit)) {
        this.onImeActionDoneListener = onImeActionDoneListener
    }

    fun setOnEditTextContainerClicked(onEditTextContainerClicked: (() -> Unit)) {
        this.onEditTextContainerClicked = onEditTextContainerClicked
    }

    fun getEditTextValue() = binding.editText.text.toString()

    fun setEditTextValue(value: String) = binding.editText.setText(value)

    fun setEditTextSize(textSize: Float) {
        binding.editText.textSize = textSize
    }

    fun setTextWatcher(textWatcher: TextWatcher) {
        binding.editText.addTextChangedListener(textWatcher)
    }

    fun setStartIcon(@DrawableRes drawableRes: Int) {
        binding.ivStartIcon.isVisible = true
        binding.ivStartIcon.setImageResource(drawableRes)
    }

    fun setStartIconTint(@ColorRes colorRes: Int) {
        binding.ivStartIcon.backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
    }

    enum class EditTextType {
        AADHAAR,
        PAN,
        EMAIL,
        NAME,
        AGE,
        NUMBER,
        DEFAULT
    }
}