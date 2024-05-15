package com.jar.app.core_ui.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.jar.app.base.util.getNameInitials
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var clipPath = Path()

    /** To store custom drawable created using name initials **/
    private var customDrawable: Drawable? = null

    /** To store user name initials **/
    private var userInitials: String? = null

    /** To set text paint attributes **/
    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    /** To set background paint attributes **/
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** To set border paint attributes **/
    private var borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var rectF = RectF()

    /** To store border width **/
    private var borderWidth = 8f * resources.displayMetrics.density

    init {
        textPaint.textSize = 28f * resources.displayMetrics.scaledDensity
        textPaint.color = Color.WHITE
        borderPaint.style = Paint.Style.STROKE
        borderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.color = ContextCompat.getColor(
            context, R.color.lightBgColor
        )
    }

    /***
     * Need to explicitly call this method to set User Image.
     * It will check for profile pic url first,
     * and if not present will set the name initials drawable in the image view otherwise will set
     * user image in the image view
     ***/
    fun setUserImage(
        user: User? = null,
        borderWidth: Float = 8f * resources.displayMetrics.density,
    ) {
        this.borderWidth = borderWidth
        userInitials = getUserNameInitials(user)
        setDrawable()
        if (!user?.profilePicUrl.isNullOrEmpty()) {
            borderPaint.strokeWidth = 0f
            borderPaint.color = ContextCompat.getColor(
                context, R.color.transparent
            )
            Glide.with(context)
                .load(user?.profilePicUrl)
                .placeholder(customDrawable)
                .circleCrop()
                .into(this)
        } else {
            borderPaint.strokeWidth = borderWidth
            borderPaint.color = ContextCompat.getColor(
                context, R.color.color_3c3357
            )
            setImageDrawable(customDrawable)
            invalidate()
        }
    }


    private fun setDrawable() {
        customDrawable = object : Drawable() {
            override fun draw(canvas: Canvas) {
                val centerX = (bounds.width() * 0.5f)
                val centerY = (bounds.height() * 0.5f)
                if (userInitials != null) {
                    val textWidth = textPaint.measureText(userInitials) * 0.5f
                    val textBaseLineHeight = textPaint.fontMetrics.ascent * -0.4f
                    canvas.drawText(userInitials.orEmpty(), centerX - textWidth, centerY + textBaseLineHeight, textPaint)
                }
            }

            override fun setAlpha(alpha: Int) {}
            override fun setColorFilter(colorFilter: ColorFilter?) {}
            override fun getOpacity(): Int {
                return PixelFormat.UNKNOWN
            }
        }
    }

    fun setDrawableFromName(
        name: String,
        textSize: Float = 18f,
        textColor: Int = Color.WHITE,
        borderWidth: Float = 0f,
        borderColor: Int = R.color.transparent,
        backgroundColor: Int = R.color.lightBgColor
    ) {
        textPaint.textSize = textSize * resources.displayMetrics.scaledDensity
        textPaint.color = textColor
        borderPaint.strokeWidth = borderWidth
        borderPaint.color = ContextCompat.getColor(
            context, borderColor
        )
        paint.color = ContextCompat.getColor(
            context, backgroundColor
        )
        val initials = getNameInitials(name)
        customDrawable = object : Drawable() {
            override fun draw(canvas: Canvas) {
                val centerX = (bounds.width() * 0.5f)
                val centerY = (bounds.height() * 0.5f)
                val textWidth = textPaint.measureText(initials) * 0.5f
                val textBaseLineHeight = textPaint.fontMetrics.ascent * -0.4f
                canvas.drawCircle(rectF.centerX(), rectF.centerY(), bounds.height() / 2f - borderWidth, paint)
                canvas.drawText(initials, centerX - textWidth, centerY + textBaseLineHeight, textPaint)
            }

            override fun setAlpha(alpha: Int) {}
            override fun setColorFilter(colorFilter: ColorFilter?) {}
            override fun getOpacity(): Int {
                return PixelFormat.UNKNOWN
            }
        }
        setImageDrawable(customDrawable)
        invalidate()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val screenWidth = MeasureSpec.getSize(widthMeasureSpec)
        val screenHeight = MeasureSpec.getSize(heightMeasureSpec)
        rectF[0f, 0f, screenWidth.toFloat()] = screenHeight.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(
            rectF.centerX(),
            rectF.centerY(),
            rectF.height() / 2 - borderWidth,
            borderPaint,
        )
        clipPath.addCircle(
            rectF.centerX(),
            rectF.centerY(),
            rectF.height() / 2,
            Path.Direction.CW
        )
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }

    private fun getUserNameInitials(user: User?): String {
        var nameInitials = ""
        user?.firstName?.getOrNull(0).let {
            nameInitials = it?.toString().orEmpty()
        }
        var lastWord = user?.lastName?.split("\\s".toRegex())?.size.orZero()
        if (lastWord != 0)
            lastWord--
        user?.lastName?.split("\\s".toRegex())?.getOrNull(lastWord)?.getOrNull(0).let {
            nameInitials += it?.toString().orEmpty()
        }
        return nameInitials.uppercase()
    }

}