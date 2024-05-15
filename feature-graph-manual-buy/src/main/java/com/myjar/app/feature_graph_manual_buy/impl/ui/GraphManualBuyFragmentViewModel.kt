package com.myjar.app.feature_graph_manual_buy.impl.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.android.feature_post_setup.CalendarUtil
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.calendarView.model.CalendarInfo
import com.jar.app.core_ui.calendarView.model.CalendarSavingOperations
import com.jar.app.core_ui.calendarView.model.SavingOperations
import com.jar.app.core_ui.calendarView.viewholder.CalenderViewPageItem
import com.jar.app.core_ui.calendarView.viewholder.PostSetupPageItem
import com.jar.app.feature_post_setup.domain.model.calendar.FeaturePostSetUpCalendarInfo
import com.jar.internal.library.jar_core_network.api.util.collect
import com.myjar.app.feature_graph_manual_buy.data.model.CalanderModel
import com.myjar.app.feature_graph_manual_buy.data.model.FaqsResponse
import com.myjar.app.feature_graph_manual_buy.data.model.GraphManualBuyPriceGraphModel
import com.myjar.app.feature_graph_manual_buy.data.model.QuickActionResponse
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchCalenderUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchGraphDataUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchManualBuyGraphFaqUseCase
import com.myjar.app.feature_graph_manual_buy.domain.userCases.FetchQuickActionUseCase
import com.myjar.app.feature_graph_manual_buy.impl.model.BottomImageItem
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphFaqsItem
import com.myjar.app.feature_graph_manual_buy.impl.model.ManualBuyGraphItem
import com.myjar.app.feature_graph_manual_buy.impl.model.NeedHelpManualBuyGraphItem
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.ManualSaving_TrackerClicked
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.ManualSaving_TrackerShown
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.Total_Manual_Saving
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.back
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.clickaction
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.day
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.faqs
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.fromscreen
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.homescreen
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.info
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.month
import com.myjar.app.feature_graph_manual_buy.impl.ui.FeatureGraphManualBuyConstants.saveMore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GraphManualBuyFragmentViewModel @Inject constructor(
    private val fetchQuickActionUseCase: FetchQuickActionUseCase,
    private val fetchManualBuyGraphFaqUseCase: FetchManualBuyGraphFaqUseCase,
    private val fetchGraphDataUseCase: FetchGraphDataUseCase,
    private val fetchCalenderUseCase: FetchCalenderUseCase,
    private val calendarUtil: CalendarUtil,
    private val remoteConfigManager: RemoteConfigApi,
    private val analyticsApi: com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi,
    ): ViewModel() {

    private var job: Job? = null
    private val _manualBuyGraphStateFlow = MutableStateFlow<List<PostSetupPageItem>>(emptyList())
    val manualBuyGraphStateFlow: StateFlow<List<PostSetupPageItem>> = _manualBuyGraphStateFlow

    private val _manualBuyGraphData: MutableStateFlow<GraphManualBuyPriceGraphModel?> = MutableStateFlow(null)
    val manualBuyGraphData: StateFlow<GraphManualBuyPriceGraphModel?> = _manualBuyGraphData

    private var _manualCalenderData: CalanderModel? = null

    private var _faqsData: FaqsResponse? = null

    private var _quickActionData: QuickActionResponse? = null

    private val _openWhatsApp = MutableSharedFlow<String?>()
    val openWhatsApp: SharedFlow<String?> = _openWhatsApp

    var monthIndex = calendarUtil.getCurrentMonthIndex()

    fun handleAction(action: GraphManualBuyFragmentAction) {
        viewModelScope.launch(Dispatchers.IO) {
            when(action) {
                GraphManualBuyFragmentAction.Init -> {
                    fetchManualByGraphData()
                    fetchCalenderData()
                    fetchFaq()
                    fetchQuickAction()
                }

                GraphManualBuyFragmentAction.ClickOnNeedHelp -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to FeatureGraphManualBuyConstants.help,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )
                    _openWhatsApp.emit(remoteConfigManager.getWhatsappNumber())
                }

                GraphManualBuyFragmentAction.OnClickOnNextOnCalender -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to month,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )
                    monthIndex++
                    if (monthIndex == -1) {
                        monthIndex = CalendarUtil.LAST_MONTH_IN_YEAR_INDEX
                        calendarUtil.updateCurrentCalenderYear(shouldIncreaseYear = false)
                    } else if (monthIndex == 12) {
                        monthIndex = CalendarUtil.FIRST_MONTH_IN_YEAR_INDEX
                        calendarUtil.updateCurrentCalenderYear(shouldIncreaseYear = true)
                    }
                    fetchCalenderData()

                }
                GraphManualBuyFragmentAction.OnClickOnPreviousOnCalender -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to month,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )
                    monthIndex--
                    if (monthIndex == -1) {
                        monthIndex = CalendarUtil.LAST_MONTH_IN_YEAR_INDEX
                        calendarUtil.updateCurrentCalenderYear(shouldIncreaseYear = false)
                    } else if (monthIndex == 12) {
                        monthIndex = CalendarUtil.FIRST_MONTH_IN_YEAR_INDEX
                        calendarUtil.updateCurrentCalenderYear(shouldIncreaseYear = true)
                    }
                    fetchCalenderData()
                }

                GraphManualBuyFragmentAction.OnClickOnInfoIcon -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to info,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )
                }
                GraphManualBuyFragmentAction.OnClickOnCalenderCta -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to saveMore,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )

                }
                GraphManualBuyFragmentAction.OnClickOnFaqs -> {

                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to faqs,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )


                }
                GraphManualBuyFragmentAction.OnClickOnBack -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            homescreen to homescreen,
                            clickaction to back,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )
                }

                GraphManualBuyFragmentAction.OnDayClick -> {
                    analyticsApi.postEvent(
                        ManualSaving_TrackerClicked,
                        mapOf(
                            fromscreen to homescreen,
                            clickaction to day,
                            Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                        )
                    )
                }
            }
        }
    }

    private suspend fun fetchManualByGraphData() {
        fetchGraphDataUseCase.fetchGraphData().collect(
            onLoading = {

            },
            onSuccess = {
                _manualBuyGraphData.value = it
                mergeAllTheResponses(graphManualBuyPriceGraphModel = it)
                analyticsApi.postEvent(
                    ManualSaving_TrackerShown,
                    mapOf(
                        fromscreen to homescreen,
                        Total_Manual_Saving to (getNumberString(_manualBuyGraphData.value?.totalManualSavings?.value!!) ?: 0)
                    )
                )
            },
            onError = {_, _ -> }
        )
    }

    private suspend fun fetchCalenderData() {
        fetchCalenderUseCase.fetchCalender(
            startDate = calendarUtil.getMonthFirstDay(monthIndex),
            endDate = calendarUtil.getMonthLastDay(monthIndex)
        ).collect(
            onLoading = {},
            onSuccess = {
                _manualCalenderData = it
                mergeAllTheResponses(calenderData = it)
            },
            onError = {er, em -> }
        )
    }

    private suspend fun fetchFaq() {
        fetchManualBuyGraphFaqUseCase.fetchFaqs().collect(
            onLoading = {},
            onSuccess = {
                _faqsData = it
                mergeAllTheResponses(it)
            },
            onError = {_, _ -> }
        )
    }

    private suspend fun fetchQuickAction() {
        fetchQuickActionUseCase.fetchQuickAction().collect(
            onLoading = {},
            onSuccess = {
                _quickActionData = it
                mergeAllTheResponses(quickActionResponse = it)
            },
            onError = {_, _ -> }
        )
    }

    private var calenderViewPageItem: CalenderViewPageItem? = null

    private fun mergeAllTheResponses(
        faqsResponse: FaqsResponse? = _faqsData,
        quickActionResponse: QuickActionResponse? = _quickActionData,
        graphManualBuyPriceGraphModel: GraphManualBuyPriceGraphModel? = _manualBuyGraphData.value,
        calenderData: CalanderModel? = _manualCalenderData,
    ) {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            val list = mutableListOf<PostSetupPageItem>()

            graphManualBuyPriceGraphModel?.let {
                list.add(
                    ManualBuyGraphItem(
                        order = 0,
                        graphItem = it
                    )
                )
            }

            calenderData?.let {
                val listOfCalenderInfo = it.dayResponses?.map {
                    CalendarInfo(
                        day = it.day,
                        status = it.status,
                        amount = it.amount,
                        isSelected = false
                    )
                } ?: emptyList()
                calenderViewPageItem = CalenderViewPageItem(
                    order = 1,
                    calendarInfo =  calendarUtil.createMonthCalendar(monthIndex, listOfCalenderInfo.map {
                        FeaturePostSetUpCalendarInfo(
                            id = it.id,
                            day = it.day,
                            status = it.status,
                            amount = it.amount,
                            isSelected = false
                        )
                    }).map {
                        CalendarInfo(
                            day = it.day,
                            status = it.status,
                            amount = it.amount,
                            isSelected = false
                        )
                    } ,
                    calendarSavingOperations = CalendarSavingOperations(
                        listOf(
                            SavingOperations(
                                icon = "",
                                title = graphManualBuyPriceGraphModel?.buyGoldCta?.text ?:"",
                                isPrimary = true,
                                deeplink = graphManualBuyPriceGraphModel?.buyGoldCta?.deeplink ?:""
                            )
                        )
                    ),
                    yearAndMonthText = calendarUtil.getMonthAndYearString(monthIndex = monthIndex),
                    isPreviousClickEnabled = it.leftSwipeEnable ?: false,
                    isNextClickEnabled = it.rightSwipeEnable ?: false
                )
                list.add(calenderViewPageItem!!.copy())
            }

            faqsResponse?.let {
                list.add(
                    ManualBuyGraphFaqsItem(
                        order = 2,
                        title = it.faqsList?.getOrNull(0)?.type ?: "faqs",
                        faq = faqsResponse
                    )
                )
            }

            quickActionResponse?.let {
                list.add(
                    NeedHelpManualBuyGraphItem(
                        order = 3,
                        title = it.quickActionList?.getOrNull(0)?.title ?: "Need Help",
                        item = it
                    )
                )
            }

            list.add(
                BottomImageItem(
                    order = 4,
                    ""
                )
            )

            val newList = list.map {
                when (it) {
                    is CalenderViewPageItem -> it.copy()
                    is ManualBuyGraphFaqsItem -> it.copy()
                    is ManualBuyGraphItem -> it.copy()
                    is NeedHelpManualBuyGraphItem -> it.copy()
                    is BottomImageItem -> it.copy()
                    else -> {
                        it
                    }
                }
            }
            _manualBuyGraphStateFlow.emit(newList)

        }
    }

    private fun getNumberString(input: String): Int? {
        return input.subSequence(1, input.length).toString().replace(",", "").toIntOrNull()
    }
}