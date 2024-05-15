package com.jar.app.feature_gold_delivery.impl.ui.store_item.detail

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.jar.app.feature_gold_delivery.R
import java.util.concurrent.atomic.AtomicInteger

internal interface CartItemQuantityViewListener {
    fun counterAdded()
    fun counterSubtracted(quantity: Int)
}

class CartItemQuantityView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var minusSign: View
    private var plusSign: View
    private var counterTv: TextView
    private var listener: CartItemQuantityViewListener? = null
    private val currentCount: AtomicInteger = AtomicInteger(1)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val inflate = inflater.inflate(R.layout.layout_cart_item_quantity, this, true)
        minusSign = inflate.findViewById<TextView>(R.id.minusSign)
        plusSign = inflate.findViewById<ImageView>(R.id.plusSign)
        counterTv = inflate.findViewById<TextView>(R.id.counter)

        minusSign.setOnClickListener {
            listener?.counterSubtracted(currentCount.get() - 1)
        }
        plusSign.setOnClickListener {
            listener?.counterAdded()
        }
    }

    internal fun setListener(listener: CartItemQuantityViewListener) {
        this.listener = listener
    }

    fun setCount(count: Int) {
        currentCount.set(count)
        counterTv.text = count.toString()
    }
}