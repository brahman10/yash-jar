package com.jar.app.feature_settings.impl.ui.payment_methods.add_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.widget.credit_card_view.CreditCardView
import com.jar.app.feature_settings.R
import com.jar.app.feature_settings.databinding.FragmentAddNewCardBinding
import com.jar.app.feature_settings.domain.event.PaymentMethodsAlteredEvent
import com.jar.app.feature_settings.domain.model.CardDetail
import com.jar.app.feature_settings.shared.SettingsMR
import com.jar.app.feature_settings.util.SettingsConstants
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class AddNewCardFragment : BaseFragment<FragmentAddNewCardBinding>(),
    CreditCardView.OnCardDetailsChangedListener, CreditCardView.OnCardItemFocusListener {

    private var cardInfo: CardInfo? = null

    private var cardBin: String? = null

    private val viewModel: AddNewCardViewModel by viewModels()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddNewCardBinding
        get() = FragmentAddNewCardBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
        observeLiveData()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            popBackStack()
        }

        binding.btnNext.setDebounceClickListener {
            val isValid = validateCardDetails()
            if (isValid) {

                binding.creditCardView.flipCard(CreditCardView.CardSide.FRONT)
                binding.creditCardView.hideKeyboard()
                //TODO: Store Card Details
                viewModel.addNewCard(
                    CardDetail(
                        cardNumber = binding.creditCardView.getRawCardNumber()!!,
                        nameOnCard = binding.creditCardView.getCardName()!!,
                        cardExpMonth = binding.creditCardView.getCardExpiryMonth().toString(),
                        cardExpYear = binding.creditCardView.getCardExpiryYear().toString(),
                    )
                )
            }
        }

        binding.creditCardView.setOnCardDetailsChangedListener(this)

        binding.creditCardView.setOnCardItemFocusListener(this)
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.cardBinInfoLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                if (it.cardType != null) {
                    cardInfo = CardInfo(it.cardType, it.cardBrand, it.cardBank)
                    updateCardBinDetails()
                }
            }
        )
        viewModel.addNewCardLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                EventBus.getDefault()
                    .post(PaymentMethodsAlteredEvent(SettingsConstants.PaymentMethodsPosition.CARDS))
                popBackStack()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                EventBus.getDefault()
                    .post(PaymentMethodsAlteredEvent(SettingsConstants.PaymentMethodsPosition.CARDS))
                popBackStack()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun validateCardDetails(): Boolean {
        if (!binding.creditCardView.isCardNumberValid() || !isCardValidFromApi()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.NUMBER)
            return false
        }
        if (!binding.creditCardView.isCardNameValid()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.NAME)
            return false
        }
        if (!binding.creditCardView.isCardExpiryValid()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.EXPIRY)
            return false
        }
        if (!binding.creditCardView.isCvvValid()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.CVV)
            return false
        }
        return true
    }

    override fun onDestroyView() {
        binding.creditCardView.onDestroy()
        super.onDestroyView()
    }

    override fun onCardNumberChangedListener(number: CharSequence?) {
        if (binding.creditCardView.isCardNumberValid() && isCardValidFromApi()) {
            binding.btnNext.alpha = 1f
        } else {
            binding.btnNext.alpha = 0.5f
        }

        val bin = if ((number?.length ?: 0) >= 6) number?.substring(0, 6) else null

        when {
            cardBin != bin -> {
                cardBin = bin
                fetchCardInfo(cardBin)
            }
            (number?.length ?: 0) < 6 -> {
                binding.creditCardView.setCardBrand(CreditCardView.CardBrand.OTHER)
                binding.creditCardView.setBankName(null)
            }
            number.isNullOrBlank() -> {
                updateCardState(true)
            }
        }
    }

    override fun onCardNameChangedListener(number: CharSequence?) {
        binding.btnNext.alpha = if (binding.creditCardView.isCardNameValid()) 1f else 0.5f
    }

    override fun onCardExpiryChangedListener(number: CharSequence?) {
        binding.btnNext.alpha = if (binding.creditCardView.isCardExpiryValid()) 1f else 0.5f
        updateCardState(
            binding.creditCardView.isCardExpiryValid(),
            getCustomString(SettingsMR.strings.feature_settings_invalid_expiry_date_please_try_agin)
        )
    }

    override fun onCardCvvChangedListener(cvv: CharSequence?) {
        binding.btnNext.alpha = if (binding.creditCardView.isCvvValid()) 1f else 0.5f
    }

    override fun onCardFlipListener(cardSide: CreditCardView.CardSide) {
        when (cardSide) {
            CreditCardView.CardSide.FRONT -> {
                binding.creditCardView.focusView(CreditCardView.CardViewElements.NUMBER)
            }
            CreditCardView.CardSide.BACK -> {
            }
        }
    }

    private fun updateCardBinDetails() {
        val isValid = cardInfo != null && cardInfo!!.bank.isNullOrBlank().not()
        binding.creditCardView.setCardValidFromApi(isValid)
        if (isValid && (binding.creditCardView.getCardNumber()?.length ?: 0) >= 6) {
            val cardBrand = CreditCardView.CardBrand.values().find { it.name == cardInfo?.brand }
            binding.creditCardView.setCardBrand(cardBrand ?: CreditCardView.CardBrand.OTHER)
            binding.creditCardView.setBankName(cardInfo!!.bank)
        }

        if ((binding.creditCardView.getCardNumber()?.length ?: 0) >= 6) {
            updateCardState(
                isValid,
                getCustomString(SettingsMR.strings.feature_settings_invalid_card_number_please_try_agin)
            )
        }
    }

    private fun updateCardState(isValid: Boolean, errorMessage: String? = null) {
        if (isValid) {
            binding.clCard.setBackgroundResource(0)
            binding.tvError.isVisible = false
        } else {
            binding.clCard.setBackgroundResource(R.drawable.feature_settings_bg_rounded_outline_eb6a6e_22dp)
            binding.tvError.text = errorMessage
            binding.tvError.isVisible = true
        }
    }

    private fun fetchCardInfo(number: CharSequence?) {
        if (number.isNullOrBlank())
            return
        viewModel.fetchCardBinInfo(number.toString())
    }

    private fun isCardValidFromApi() = cardInfo?.bank.isNullOrBlank().not()

    override fun onCardNumberFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCardNumberValid()) 1f else 0.5f
    }

    override fun onCardNameFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCardNameValid()) 1f else 0.5f
    }

    override fun onCardExpiryFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCardExpiryValid()) 1f else 0.5f
    }

    override fun onCardCvvFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCvvValid()) 1f else 0.5f
    }

    data class CardInfo(
        val type: String?,
        val brand: String?,
        val bank: String?
    )
}