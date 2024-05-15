package com.jar.app.feature_lending.impl.ui.mandate.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.jar.app.feature_lending.databinding.FeatureLendingLinkedBankCardLayoutBinding
import com.jar.app.feature_lending.databinding.FeatureLendingMandatePaymentModeButtonLayoutBinding

internal class LinkedBankDetailView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding:FeatureLendingLinkedBankCardLayoutBinding
    init {
        removeAllViews()
       binding = FeatureLendingLinkedBankCardLayoutBinding.inflate(LayoutInflater.from(context),this, true)
    }

    fun setBankIcon(url:String){
        Glide.with(context).load(url).into(binding.ivBankLogo)
    }

    fun setBankName(name:String){
        binding.tvBankName.text = name
    }

    fun setBankAccountNumber(accountNumber:String){
        binding.tvAccountNumber.text = accountNumber
    }
}