package com.jar.app.core_image_picker.impl.ui.crop.custom

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.ViewConfiguration
import androidx.core.view.MotionEventCompat
import com.jar.app.base.util.dp

class CropGestureDetector(context: Context) {

    private val mDetector: ScaleGestureDetector
    private var mListener: CropGestureListener? = null
    internal var mLastTouchX: Float = 0.toFloat()
    internal var mLastTouchY: Float = 0.toFloat()
    internal val mTouchSlop: Float
    internal val mMinimumVelocity: Float
    private var mVelocityTracker: VelocityTracker? = null
    var isDragging: Boolean = false
        private set
    private var mActivePointerId: Int = 0
    private var mActivePointerIndex: Int = 0

    private var started: Boolean = false

    val isScaling: Boolean
        get() = mDetector.isInProgress

    interface CropGestureListener {
        fun onDrag(dx: Float, dy: Float)
        fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float)
        fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)
    }

    init {
        val configuration = ViewConfiguration
            .get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = 1.dp.toFloat()

        this.mActivePointerId = INVALID_POINTER_ID
        this.mActivePointerIndex = 0

        val mScaleListener = object : ScaleGestureDetector.OnScaleGestureListener {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor

                if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor))
                    return false

                mListener!!.onScale(
                    scaleFactor,
                    detector.focusX, detector.focusY
                )
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                // NO-OP
            }
        }
        mDetector = ScaleGestureDetector(context, mScaleListener)
    }

    internal fun getActiveX(ev: MotionEvent): Float {
        try {
            return ev.getX(this.mActivePointerIndex)
        } catch (e: Exception) {
            return ev.x
        }

    }

    internal fun getActiveY(ev: MotionEvent): Float {
        try {
            return ev.getY(this.mActivePointerIndex)
        } catch (e: Exception) {
            return ev.y
        }

    }

    fun setOnGestureListener(listener: CropGestureListener) {
        this.mListener = listener
    }


    fun onTouchEvent(ev: MotionEvent): Boolean {
        mDetector.onTouchEvent(ev)

        var i = 0
        when (ev.action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> this.mActivePointerId = ev.getPointerId(0)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> this.mActivePointerId =
                INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = MotionEventCompat.ACTION_POINTER_INDEX_MASK and ev.action shr 8
                if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
                    val newPointerIndex: Int
                    if (pointerIndex == 0) {
                        newPointerIndex = 1
                    } else {
                        newPointerIndex = 0
                    }
                    this.mActivePointerId = ev.getPointerId(newPointerIndex)
                    this.mLastTouchX = ev.getX(newPointerIndex)
                    this.mLastTouchY = ev.getY(newPointerIndex)
                }
            }
        }
        if (this.mActivePointerId != INVALID_POINTER_ID) {
            i = this.mActivePointerId
        }
        this.mActivePointerIndex = ev.findPointerIndex(i)

        when (ev.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (!started) {
                    mVelocityTracker = VelocityTracker.obtain()
                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    }

                    mLastTouchX = getActiveX(ev)
                    mLastTouchY = getActiveY(ev)
                    isDragging = false

                    started = true
                    return true
                }

                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY

                if (!isDragging) {
                    // Use Pythagoras to see if drag length is larger than
                    // touch slop
                    isDragging = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() >= mTouchSlop
                }

                if (isDragging) {
                    mListener!!.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y

                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
                started = false
                isDragging = false
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        // Compute velocity within the last 1000ms
                        mVelocityTracker!!.addMovement(ev)
                        mVelocityTracker!!.computeCurrentVelocity(1000)

                        val vX = mVelocityTracker!!.xVelocity
                        val vY = mVelocityTracker!!
                            .yVelocity

                        // If the velocity is greater than minVelocity, call
                        // listener
                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener!!.onFling(
                                mLastTouchX, mLastTouchY, -vX,
                                -vY
                            )
                        }
                    }

                    isDragging = false
                }

                // Recycle Velocity Tracker
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }

                started = false
            }
        }

        return true
    }

    companion object {

        private val INVALID_POINTER_ID = -1
    }

}