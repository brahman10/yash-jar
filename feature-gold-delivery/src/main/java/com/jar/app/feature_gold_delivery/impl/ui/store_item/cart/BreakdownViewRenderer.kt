package com.jar.app.feature_gold_delivery.impl.ui.store_item.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_gold_delivery.R
import com.jar.app.feature_gold_delivery.databinding.LayoutBreakdownCardSingledataBinding
import com.jar.app.feature_gold_delivery.databinding.LayoutBreakdownImagelinedataBinding
import com.jar.app.feature_gold_delivery.databinding.LayoutBreakdownMultilinedataBinding
import com.jar.app.feature_gold_delivery.databinding.LayoutBreakdownSingledataBinding
import com.jar.app.feature_gold_delivery.shared.domain.model.CartAPIBreakdownData
import java.lang.ref.WeakReference

object BreakdownViewRenderer {

    fun getFormattedAmount(context: WeakReference<Context>, amount: String): String {
        return context.get()?.getString(
            R.string.feature_buy_gold_currency_sign_x_string,
            amount
        ) ?: ""
    }

    private fun getFormatterQuantity(context: WeakReference<Context>, quantity: Double): String {
        return context.get()?.getString(
            R.string.feature_gold_delivery_n_gm_v2,
            quantity
        ).orEmpty()
    }

    internal fun getBreakdownListFromCart(
        context: WeakReference<Context>,
        cartAPIBreakdownData: CartAPIBreakdownData,
        jarSavingsUsed: Boolean
    ): MutableList<BreakdownViewData> {
        val list = mutableListOf<BreakdownViewData>()
        cartAPIBreakdownData.cart?.forEach {
            it?.let {
                list.add(
                    BreakdownSingleLineData(
                        it.label + " (${it.quantity} item)",
                        getFormatterQuantity(context, it.volume.orZero())
                    )
                )
            }
        }
        if (jarSavingsUsed) {
            list.add(
                BreakdownCardSingleLineData(
                    context.get()?.getString(R.string.jar_savings) ?: "",
                    "-" + getFormatterQuantity(context, cartAPIBreakdownData.jarSavingsInGm.orZero())
                )
            )
            list.add(BreakdownGradientDividerData())
            cartAPIBreakdownData.balanceAmount?.let {
                list.add(
                    BreakdownSingleLineData(
                        context.get()?.getString(R.string.balance) ?: "",
                    "${getFormatterQuantity(context, (cartAPIBreakdownData.totalVolume.orZero() - cartAPIBreakdownData.jarSavingsInGm.orZero()))} | ${getFormattedAmount(context, it.getFormattedAmount())}"
                    )
                )
            }
        } else {
            list.add(BreakdownGradientDividerData())
        }
        cartAPIBreakdownData.cart?.forEach {
            it?.let {
                list.add(
                    BreakdownDoubleLineData(
                        context.get()?.getString(R.string.delivery_and_making_charges) ?: "",
                        getFormattedAmount(
                            context,
                            it.deliveryMakingCharge.orZero().getFormattedAmount()
                        ),
                        it.label ?: ""
                    )
                )
            }
        }
        list.add(BreakdownGradientDividerData())
        val savings =
            if (!jarSavingsUsed) cartAPIBreakdownData.netAmount else cartAPIBreakdownData.netAmountWithJarSavingsUsed
        savings?.let {
            list.add(
                BreakdownSingleLineData(
                    context.get()?.getString(R.string.total_payable_amount) ?: "",
                    getFormattedAmount(context, it.getFormattedAmount()),
                    styleId = com.jar.app.core_ui.R.style.CommonBoldTextViewStyle
                )
            )
        }
        return list
    }

    internal fun renderBreakdownView(linearLayout: LinearLayout, data: List<BreakdownViewData>) {
        if (data.isEmpty()) return
        linearLayout.removeAllViews()

        data.forEach {
            when (it) {
                is BreakdownSingleLineData -> {
                    val inflate = LayoutBreakdownSingledataBinding.inflate(
                        LayoutInflater.from(linearLayout.context), linearLayout, false
                    )
                    inflate.root.id = ViewCompat.generateViewId()
                    inflate.title.text = it.left
                    inflate.subtitle.text = it.right
                    linearLayout.addView(inflate.root)
                }

                is BreakdownCardSingleLineData -> {
                    val inflate = LayoutBreakdownCardSingledataBinding.inflate(
                        LayoutInflater.from(linearLayout.context), linearLayout, false
                    )
                    inflate.root.id = ViewCompat.generateViewId()
                    inflate.title.text = it.left
                    inflate.subtitle.text = it.right
                    linearLayout.addView(inflate.root)
                }

                is BreakdownGradientDividerData -> {
                    val view = View(linearLayout.context)
                    val params =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3)
                    params.topMargin = 20
                    params.bottomMargin = 20
                    view.layoutParams = params
                    view.background =
                        ContextCompat.getDrawable(
                            linearLayout.context,
                            com.jar.app.core_ui.R.drawable.core_ui_separator
                        )
                    linearLayout.addView(view)
                }

                is BreakdownNormalDividerData -> {
                    val view = View(linearLayout.context)
                    val params =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3)
                    params.topMargin = 20
                    params.bottomMargin = 20
                    view.layoutParams = params
                    view.setBackgroundColor(
                        ContextCompat.getColor(
                            linearLayout.context,
                            com.jar.app.core_ui.R.color.color_3c3357
                        )
                    )
                    linearLayout.addView(view)
                }

                is BreakdownImageLineData -> {
                    val inflate = LayoutBreakdownImagelinedataBinding.inflate(
                        LayoutInflater.from(linearLayout.context), linearLayout, false
                    )

                    inflate.title2.text = it.left
                    inflate.subtitle.text = it.right


                    Glide.with(linearLayout.context).load(it.startImage ?: it.imageUrl).into(inflate.leftImage)

                    inflate.root.id = ViewCompat.generateViewId()
                    linearLayout.addView(inflate.root)
                }

                is BreakdownDoubleLineData -> {
                    val inflate = LayoutBreakdownMultilinedataBinding.inflate(
                        LayoutInflater.from(linearLayout.context), linearLayout, false
                    )
                    inflate.title.text = it.left
                    inflate.subtitle.text = it.right
                    inflate.title2.text = it.secondLine
                    inflate.root.id = ViewCompat.generateViewId()
                    linearLayout.addView(inflate.root)
                }
            }
        }
    }
}
internal interface BreakdownViewData

internal data class BreakdownSingleLineData(
    val left: String,
    val right: String,
    @StyleRes val styleId: Int = com.jar.app.core_ui.R.style.CommonTextViewStyle
) : BreakdownViewData

internal data class BreakdownImageLineData(
    @DrawableRes val startImage: Int? = null,
    val left: String,
    val right: String,
    val imageUrl: String? = null
) : BreakdownViewData

internal data class BreakdownCardSingleLineData(
    val left: String,
    val right: String
) : BreakdownViewData

class BreakdownGradientDividerData : BreakdownViewData
class BreakdownNormalDividerData : BreakdownViewData

internal data class BreakdownDoubleLineData(
    val left: String,
    val right: String,
    val secondLine: String
) : BreakdownViewData