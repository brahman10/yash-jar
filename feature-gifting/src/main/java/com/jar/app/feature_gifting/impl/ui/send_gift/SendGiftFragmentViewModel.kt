package com.jar.app.feature_gifting.impl.ui.send_gift

import android.app.Application
import android.net.Uri
import android.provider.ContactsContract
import androidx.lifecycle.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_gifting.R
import com.jar.app.feature_gifting.shared.domain.model.*
import com.jar.app.feature_gifting.shared.domain.model.AmountAndMessageDetail
import com.jar.app.feature_gifting.shared.domain.model.GiftView
import com.jar.app.feature_gifting.shared.domain.model.Question
import com.jar.app.feature_gifting.shared.domain.model.ReceiverDetail
import com.jar.app.feature_gifting.shared.domain.use_case.FetchGiftGoldOptionsUseCase
import com.jar.app.feature_gifting.shared.util.Constants
import com.jar.app.feature_gold_price.shared.data.model.FetchCurrentGoldPriceResponse
import com.jar.app.feature_gold_price.shared.data.model.GoldPriceType
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult

@HiltViewModel
internal class SendGiftFragmentViewModel @Inject constructor(
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchGiftGoldOptionsUseCase: FetchGiftGoldOptionsUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val mApplication: Application,
    private val phoneNumberUtils: PhoneNumberUtil,
    private val dispatcherProvider: DispatcherProvider
) : AndroidViewModel(mApplication) {

    private val _listLiveData = MutableLiveData<List<GiftView>>()
    val listLiveData: LiveData<List<GiftView>>
        get() = _listLiveData

    private val _errorLiveData = SingleLiveEvent<Int>()
    val errorLiveData: LiveData<Int>
        get() = _errorLiveData

    private val _suggestedAmountLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<GiftGoldOptions>>>()
    val suggestedAmountLiveData: LiveData<RestClientResult<ApiResponseWrapper<GiftGoldOptions>>>
        get() = _suggestedAmountLiveData

    private val _currentGoldBuyPriceLiveData =
        SingleLiveEvent<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>()
    val currentGoldBuyPriceLiveData: LiveData<RestClientResult<ApiResponseWrapper<FetchCurrentGoldPriceResponse>>>
        get() = _currentGoldBuyPriceLiveData

    private val _volumeFromAmountLiveData =
        MutableLiveData<RestClientResult<Float>>()
    val volumeFromAmountLiveData: LiveData<RestClientResult<Float>>
        get() = _volumeFromAmountLiveData

    private val _amountFromVolumeLiveData =
        MutableLiveData<RestClientResult<Float>>()
    val amountFromVolumeLiveData: LiveData<RestClientResult<Float>>
        get() = _amountFromVolumeLiveData

    var giftingState: GiftingState? = null

    private var fetchCurrentBuyPriceResponse: FetchCurrentGoldPriceResponse? = null

    private var fetchBuyPriceJob: Job? = null

    private var fetchVolumeFromAmountJob: Job? = null

    private var fetchAmountFromVolumeJob: Job? = null

    var sendGiftGoldRequest: SendGiftGoldRequest = SendGiftGoldRequest()

    fun getData() {
        getInitialGiftList()
        fetchSuggestedAmount()
        fetchCurrentGoldBuyPrice()
    }

    private fun getInitialGiftList() {
        viewModelScope.launch(dispatcherProvider.default) {
            val list = mutableListOf<GiftView>()
            list.add(
                Question(
                    "Whom do you want to gift gold to?",
                    Constants.GiftCardOrder.WHOM_TO_SEND
                )
            )
            giftingState = GiftingState.SHOW_CONTACT_SELECTION
            _listLiveData.postValue(list)
        }
    }

    fun setReceiverDetailFromUri(currentList: List<GiftView>, uri: Uri) {
        val cursor = mApplication.applicationContext.contentResolver.query(
            uri, null, null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val numberIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            val numberData = phoneNumberUtils.parse(
                cursor.getString(numberIndex),
                BaseConstants.REGION_CODE
            )
            val receiverDetail = ReceiverDetail(
                name = cursor.getString(nameIndex).orEmpty(),
                number = "+${numberData.countryCode}${numberData.nationalNumber}"
            )
            addReceiverDetail(currentList, receiverDetail)
        } else
            _errorLiveData.postValue(R.string.feature_gifting_failed_to_get_contact)

        cursor?.close()
    }

    fun addReceiverDetail(
        currentList: List<GiftView>,
        receiverDetail: ReceiverDetail,
    ) {
        viewModelScope.launch(dispatcherProvider.default) {
            sendGiftGoldRequest.receiverName = receiverDetail.name
            sendGiftGoldRequest.receiverPhoneNo = receiverDetail.number
            val list = currentList
                .filter {
                    (it is ReceiverDetail).not()
                }
                .filter {
                    if (it is Question)
                        it.questionOrder != Constants.GiftCardOrder.HOW_MUCH_TO_SEND
                    else
                        true
                }
                .map { it }.toMutableList()
            list.add(receiverDetail)

            val nextQuestion = Question(
                "How much gold do you want to gift?",
                Constants.GiftCardOrder.HOW_MUCH_TO_SEND
            )
            list.add(nextQuestion)

            giftingState = GiftingState.SHOW_ENTER_AMOUNT

            list.sortBy { it.getOrder() }
            _listLiveData.postValue(list)
        }
    }

    fun addAmountAndMessageDetail(
        currentList: List<GiftView>,
        amountAndMessageDetail: AmountAndMessageDetail,
    ) {
        viewModelScope.launch(dispatcherProvider.default) {
            sendGiftGoldRequest.amount = amountAndMessageDetail.amountInRupees
            sendGiftGoldRequest.volume = amountAndMessageDetail.volumeInGm
            sendGiftGoldRequest.messageForReceiver = amountAndMessageDetail.message
            val list = currentList
                .filter { (it is AmountAndMessageDetail).not() }
                .map { it }.toMutableList()
            list.add(amountAndMessageDetail)

            giftingState = GiftingState.ALL_DETAILS_ENTERED

            list.sortBy { it.getOrder() }
            _listLiveData.postValue(list)
        }
    }

    private fun fetchSuggestedAmount() {
        viewModelScope.launch {
            fetchGiftGoldOptionsUseCase.fetchGiftGoldOptions()
                .collect {
                    _suggestedAmountLiveData.postValue(it)
                }
        }
    }

    fun fetchCurrentGoldBuyPrice() {
        fetchBuyPriceJob?.cancel()
        fetchBuyPriceJob = viewModelScope.launch {
            fetchCurrentGoldPriceUseCase.fetchCurrentGoldPrice(GoldPriceType.BUY)
                .collectUnwrapped(
                    onLoading = {
                        _currentGoldBuyPriceLiveData.postValue(RestClientResult.loading())
                    },
                    onSuccess = {
                        fetchCurrentBuyPriceResponse = it.data
                        _currentGoldBuyPriceLiveData.postValue(RestClientResult.success(it))
                    },
                    onError = { errorMessage, errorCode ->
                        _currentGoldBuyPriceLiveData.postValue(
                            RestClientResult.error(
                                errorMessage
                            )
                        )
                    }
                )
        }
    }


    fun calculateVolumeFromAmount(amount: Float) {
        fetchVolumeFromAmountJob?.cancel()
        fetchVolumeFromAmountJob = viewModelScope.launch {
            buyGoldUseCase.calculateVolumeFromAmount(amount, fetchCurrentBuyPriceResponse).collect {
                _volumeFromAmountLiveData.postValue(it)
            }
        }
    }

    fun calculateAmountFromVolume(volume: Float) {
        fetchAmountFromVolumeJob?.cancel()
        fetchAmountFromVolumeJob = viewModelScope.launch {
            buyGoldUseCase.calculateAmountFromVolume(volume, fetchCurrentBuyPriceResponse).collect {
                _amountFromVolumeLiveData.postValue(it)
            }
        }
    }

    fun prefillReceiverDetails(
        currentList: List<GiftView>,
        sendGiftGoldRequest: SendGiftGoldRequest,

        ) {
        if (
            sendGiftGoldRequest.receiverName.isNullOrBlank().not() &&
            sendGiftGoldRequest.receiverPhoneNo.isNullOrBlank().not()
        ) {
            val numberData = phoneNumberUtils.parse(
                sendGiftGoldRequest.receiverPhoneNo,
                BaseConstants.REGION_CODE
            )
            addReceiverDetail(
                currentList,
                ReceiverDetail(
                    name = sendGiftGoldRequest.receiverName!!,
                    number = "+${numberData.countryCode}${numberData.nationalNumber}"
                )
            )
        }
    }
}