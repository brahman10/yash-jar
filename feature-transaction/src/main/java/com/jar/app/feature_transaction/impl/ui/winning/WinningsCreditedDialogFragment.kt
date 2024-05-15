package com.jar.app.feature_transaction.impl.ui.winning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentWinningsCreditedBinding
import com.jar.app.feature_transaction.shared.domain.model.WinningData
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WinningsCreditedDialogFragment :
    BaseDialogFragment<FeatureTransactionFragmentWinningsCreditedBinding>() {
    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<WinningsCreditedDialogFragmentArgs>()

    private val winningData by lazy {
        val encoded = args.winningData
        serializer.decodeFromString<com.jar.app.feature_transaction.shared.domain.model.WinningData>(decodeUrl(encoded))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentWinningsCreditedBinding
        get() = FeatureTransactionFragmentWinningsCreditedBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        /*  Glide.with(requireActivity())
              .load(winningData.iconLink)
              .into((binding.ivTransactionType))*/

        binding.tvTitle.text = winningData.title
        binding.tvSubTitle.text = winningData.nestedCardName
        binding.tvAmount.text = getString(R.string.feature_transaction_rs_value, winningData.amount)
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }
    }
}