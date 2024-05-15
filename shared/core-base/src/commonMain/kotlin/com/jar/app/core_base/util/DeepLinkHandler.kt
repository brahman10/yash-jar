package com.jar.app.core_base.util

import com.jar.app.core_base.util.BaseConstants.ExternalDeepLinks

class DeepLinkHandler(
    private val onDeepLinkNavigation: OnDeepLinkNavigation
) {

    fun handleDeepLink(
        deepLink: String?,
        fromScreen: String? = null,
        fromSection: String? = null,
        fromCard: String? = null
    ) {

        val fromScreenValue = fromScreen ?: "deeplink"

        val deepLinkData = deepLink?.split("/")
        if (deepLinkData.isNullOrEmpty().not()) {
            when (deepLinkData?.getOrNull(0)) {
                ExternalDeepLinks.SINGLE_HOME_FEED_CTA -> {
                    onDeepLinkNavigation.openSingleHomeFeedCTA(deepLink.substring(ExternalDeepLinks.SINGLE_HOME_FEED_CTA.length))
                }
            }
            val deepLinkEndpoint = deepLinkData?.get(1)!! // Endpoint

            return when (deepLinkEndpoint) {

                ExternalDeepLinks.VIBA_WEB_VIEW -> {
                    if (deepLinkData.size > 3) {
                        val url =
                            deepLinkData.subList(2, deepLinkData.size).joinToString(separator = "/")
                        onDeepLinkNavigation.openVibaWebView(
                            shouldPostAnalyticsFromUrl = true,
                            url = url,
                            title = BaseConstants.Viba,
                            showToolbar = true
                        )
                    } else {

                    }
                }

                /**
                 * Buy Gold
                 * **/
                ExternalDeepLinks.BUY_GOLD -> {
                    if (deepLinkData.size > 4) {
                        val buyGoldFlowContext =
                            deepLinkData[2] //deepLinkData[2] param will provide the buyGoldFlowContext
                        val couponCode =
                            deepLinkData[3]  //deepLinkData[3] will be the couponCode
                        val couponType =
                            deepLinkData[4] //deepLinkData[4] param will provide couponType

                        onDeepLinkNavigation.openBuyGoldFlowWithCoupon(
                            couponCode = couponCode,
                            couponType = couponType,
                            isFromJackpotScreen = false,
                            buyGoldFlowContext = buyGoldFlowContext
                        )
                    } else if (deepLinkData.size > 3) {
                        val buyGoldFlowContext =
                            deepLinkData[2] //deepLinkData[2] will be the buyGoldFlowContext
                        val prefillAmount = deepLinkData[3].toFloatOrNull()
                            .orZero()//deepLinkData[3] will be the amount which needs to be prefilled

                        onDeepLinkNavigation.openBuyGoldFlowWithPrefillAmount(
                            prefillAmount = prefillAmount, buyGoldFlowContext = buyGoldFlowContext
                        )
                    } else if (deepLinkData.size > 2) {
                        //deepLinkData[2] will be the buyGoldFlowContext
                        onDeepLinkNavigation.openBuyGoldFlowWithWeeklyChallengeAmount(
                            amount = 0f, buyGoldFlowContext = deepLinkData[2]
                        )
                    } else {
                        onDeepLinkNavigation.openBuyGoldFlowWithWeeklyChallengeAmount(
                            amount = 0f,
                            buyGoldFlowContext = BaseConstants.BuyGoldFlowContext.BUY_GOLD
                        )
                    }
                }


                /**
                 * Transaction Listing
                 * **/
                ExternalDeepLinks.TRANSACTIONS -> {
                    if (deepLinkData.size > 2) {
                        val transactionType =
                            deepLinkData[2] //deepLinkData[2] param will provide the actual transactionType
                        onDeepLinkNavigation.openTransactionScreen(transactionType = transactionType)
                    } else {
                        // Redirect to INVESTMENTS tab by default
                        onDeepLinkNavigation.openTransactionScreen(transactionType = "INVESTMENTS")
                    }
                }

                ExternalDeepLinks.REWARDS -> {
                    onDeepLinkNavigation.openTransactionScreen(transactionType = "WINNINGS")
                }

                /**
                 * Refer & Earn
                 * **/
                ExternalDeepLinks.REFER_EARN_V2, ExternalDeepLinks.REFER_AND_EARN -> {
                    onDeepLinkNavigation.openReferAndEarn()
                }

                ExternalDeepLinks.REFERRAL_FAQ -> {
                    onDeepLinkNavigation.openReferralFaqScreen()
                }

                ExternalDeepLinks.REFERRAL_INVITE_SHARE -> {
                    onDeepLinkNavigation.shareReferralInvite()
                }

                /**
                 * Jewellery Voucher
                 * **/
                ExternalDeepLinks.JEWELLERY_VOUCHERS -> {
//                      dl.myjar.app/jewelleryVoucher/voucherStatus/{id}/{enumType}
//                      dl.myjar.app/jewelleryVoucher/voucherStatus/{id}
//                      dl.myjar.app/jewelleryVoucher/myOrders/active
//                      dl.myjar.app/jewelleryVoucher/myOrders/all
//                      dl.myjar.app/jewelleryVoucher/brandCatalouge

                    val toScreen = deepLinkData.getOrNull(2)

                    when (toScreen) {
                        ExternalDeepLinks.GOLD_REDEMPTION_BRAND_CATALOGUE -> {
                            onDeepLinkNavigation.openJewelleryVoucherBrandCatalogueScreen()
                        }

                        ExternalDeepLinks.GOLD_REDEMPTION_MY_ORDERS -> {
                            val tabType = deepLinkData.getOrNull(3)
                            onDeepLinkNavigation.openJewelleryVoucherOpenMyOrdersScreen(tabType = tabType)
                        }

                        ExternalDeepLinks.GOLD_REDEMPTION_VOUCHER_STATUS -> {
                            val voucherId = deepLinkData.getOrNull(3)
                            val orderType = deepLinkData.getOrNull(4)
                            onDeepLinkNavigation.openJewelleryVoucherStatusScreen(
                                voucherId = voucherId, orderType = orderType
                            )
                        }

                        else -> {
                            // Unsupported case... Ignore it..
                        }
                    }
                }

                ExternalDeepLinks.GOLD_REDEMPTION -> {

                    onDeepLinkNavigation.openJewelleryVoucherIntroScreen()
                }

                ExternalDeepLinks.GOLD_REDEMPTION_VOUCHER_PURCHASE -> {
                    if (deepLinkData.size > 2) {
                        val voucherId = deepLinkData[2]
                        onDeepLinkNavigation.openJewelleryVoucherPurchaseScreen(voucherId)
                    } else {
                        onDeepLinkNavigation.openJewelleryVoucherIntroScreen()
                    }
                }

                /**
                 * Transaction Detail
                 * **/
                ExternalDeepLinks.TRANSACTION_DETAIL -> {
                    // dl.myjar.app/transactionDetail/{orderId}/{txnId}/{sourceType}
                    if (deepLinkData.size > 4) {
                        val orderId = deepLinkData[2]
                        val txnId = deepLinkData[3]
                        val sourceType = deepLinkData[4]

                        onDeepLinkNavigation.openOldTransactionDetailScreen(
                            orderId = orderId, txnId = txnId, sourceType = sourceType
                        )
                    } else {
                        // Missing Data In Deeplink. Ignore it..
                    }
                }

                ExternalDeepLinks.NEW_TRANSACTION_DETAIL -> {
                    // dl.myjar.app/newTransactionDetail/{orderId}/{txnId}/{sourceType}
                    if (deepLinkData.size > 4) {
                        val orderId = deepLinkData[2]
                        val txnId = deepLinkData[3]
                        val sourceType = deepLinkData[4]
                        onDeepLinkNavigation.openNewTransactionDetailScreen(
                            orderId = orderId, txnId = txnId, sourceType = sourceType
                        )
                    } else {
                        // Missing Data In Deeplink. Ignore it..
                    }
                }

                ExternalDeepLinks.TRANSACTION_DETAIL_BOTTOM_SHEET -> {
                    // dl.myjar.app/transactionDetailBottomSheet/{id}
                    if (deepLinkData.size > 2) {
                        val txnId = deepLinkData[2]
                        onDeepLinkNavigation.openTransactionDetailBottomSheet(txnId = txnId)
                    } else {
                        // Missing Data In Deeplink. Ignore it..
                    }
                }

                /**
                 * Gold Delivery
                 * **/
                ExternalDeepLinks.GOLD_DELIVERY -> {
                    val productId = deepLinkData.getOrNull(2)
                    onDeepLinkNavigation.openGoldDeliveryEntryScreen(productId = productId)
                }

                ExternalDeepLinks.GOLD_DELIVERY_CART -> {
                    onDeepLinkNavigation.openGoldDeliveryCartScreen()
                }

                /**
                 * Daily Saving Cancellation
                 * **/
                ExternalDeepLinks.DAILY_SAVINGS_CANCELLATION -> {
                    onDeepLinkNavigation.openDailySavingCancellationEntryScreen()
                }

                /**
                 * Spins Screen
                 * **/
                ExternalDeepLinks.SPIN_GAME -> {
                    onDeepLinkNavigation.openSpinsScreen()
                }

                /**
                 * Promo Code
                 * **/
                ExternalDeepLinks.PROMO_CODE -> {
                    onDeepLinkNavigation.openPromoCodeScreen()
                }

                ExternalDeepLinks.MANUAL_SAVING_TRACKER -> {
                    onDeepLinkNavigation.openManualBuyGraph()
                }

                /**
                 * Gold Gifting
                 * **/
                ExternalDeepLinks.GOLD_GIFTING -> {
                    onDeepLinkNavigation.openGoldGiftingScreen()
                }

                /**
                 * Change Language
                 * **/
                ExternalDeepLinks.CHANGE_LANGUAGE -> {
                    onDeepLinkNavigation.openChangeLanguageScreen()
                }

                /**
                 * Settings
                 * **/
                ExternalDeepLinks.SETTINGS -> {
                    onDeepLinkNavigation.openSettingsPage()
                }

                /**
                 * Profile
                 * **/
                ExternalDeepLinks.PROFILE -> {
                    onDeepLinkNavigation.openProfilePage()
                }

                /**
                 * Help And Support
                 * **/
                ExternalDeepLinks.HELP_SUPPORT -> {
                    onDeepLinkNavigation.openAppHelpAndSupportPage()
                }

                /**
                 * Help And Support
                 * **/
                ExternalDeepLinks.HELP_SUPPORT_HEALTH_INSURANCE -> {
                    onDeepLinkNavigation.openInsuranceHelpAndSupportPage()
                }

                /**
                 * Daily Saving Settings
                 * **/
                ExternalDeepLinks.DAILY_SAVINGS_SETTINGS -> {
                    onDeepLinkNavigation.openDailySavingSettings(
                        fromScreen = fromScreenValue,
                        fromSection = fromSection,
                        fromCard = fromCard
                    )
                }

                ExternalDeepLinks.EXIT_SURVEY -> {
                    val surveyFor = deepLinkData.getOrNull(2)

                    onDeepLinkNavigation.openExitSurvey(surveyFor.orEmpty())
                }

                /**
                 * Gold Price Detail
                 * **/
                ExternalDeepLinks.GOLD_PRICE_DETAIL -> {
                    onDeepLinkNavigation.openGoldPriceDetailScreen()
                }

                /**
                 * RoundOff
                 * **/
                ExternalDeepLinks.ROUND_OFF, ExternalDeepLinks.ROUND_OFF_DETAILS -> {
                    onDeepLinkNavigation.openRoundOffScreen(fromScreen = fromScreenValue)
                }

                ExternalDeepLinks.ROUND_OFF_EXPLANATION -> {
                    onDeepLinkNavigation.openRoundOffExplanation(fromScreen = fromScreenValue)
                }

                /**
                 * Setup AutoPay For RoundOff And Daily Saving
                 * **/
                ExternalDeepLinks.PRE_ROUND_OFF_AUTOPAY_SETUP -> {
                    onDeepLinkNavigation.openSetupAutoPayScreenForRoundOffAndDailySaving()
                }

                /**
                 * Jar Duo
                 * **/
                ExternalDeepLinks.JAR_DUO -> {
                    onDeepLinkNavigation.openJarDuoScreen()
                }

                ExternalDeepLinks.JAR_DUO_ONBOARDING -> {
                    onDeepLinkNavigation.openJarDuoOnboardingScreen()
                }

                /**
                 * Offer List
                 * **/
                ExternalDeepLinks.OFFER_LIST_FRAGMENT -> {
                    onDeepLinkNavigation.openOfferListScreen()
                }

                /**
                 * Lending KYC
                 * **/
                ExternalDeepLinks.LENDING_KYC_ONBOARDING, ExternalDeepLinks.LENDING_KYC_RESUME -> {
                    onDeepLinkNavigation.openLendingKyc()
                }

                /**
                 * Gold SIP (Weekly/Monthly)
                 * **/
                ExternalDeepLinks.SETUP_GOLD_SIP -> {
                    onDeepLinkNavigation.openGoldSipScreen()
                }

                ExternalDeepLinks.GOLD_SIP_INTRO -> {
                    onDeepLinkNavigation.openGoldSipIntroScreen()
                }

                ExternalDeepLinks.GOLD_SIP_TYPE_SELECTION -> {
                    if (deepLinkData.size > 2) {
                        val sipType = deepLinkData[2]
                        onDeepLinkNavigation.openGoldSipTypeSelectionScreen(sipType)
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                ExternalDeepLinks.GOLD_SIP_DETAILS -> {
                    onDeepLinkNavigation.openGoldSipDetailScreen()
                }


                /**
                 * Help Videos
                 * **/
                ExternalDeepLinks.HELP_VIDEOS_LISTING -> {
                    onDeepLinkNavigation.openHelpVideosListingScreen()
                }

                /**
                 * Weekly Magic
                 * **/
                ExternalDeepLinks.WEEKLY_MAGIC -> {
                    onDeepLinkNavigation.openWeeklyMagicFlow(
                        fromScreen = deepLinkData.getOrNull(3) ?: "DeepLink",
                        checkMysteryCardOrChallengeWin = deepLinkData.getOrNull(2)
                            ?.toBooleanStrictOrNull().orFalse()
                    )
                }

                /**
                 * Lending
                 * **/
                ExternalDeepLinks.LENDING_ONBOARDING -> {
                    onDeepLinkNavigation.openLendingOnboardingFlow(fromScreen = fromScreenValue)
                }

                ExternalDeepLinks.REAL_TIME_READY_CASH -> {
                    onDeepLinkNavigation.openRealTimeLendingFlow(fromScreen = fromScreenValue)
                }

                ExternalDeepLinks.CHECK_CREDIT_SCORE -> {
                    onDeepLinkNavigation.openCheckCreditScoreFlow(fromScreen = fromScreenValue)
                }

                /**
                 * Withdrawal Bottom Sheet
                 * **/
                ExternalDeepLinks.WITHDRAWAL_HELP_BOTTOM_SHEET -> {
                    onDeepLinkNavigation.openWithdrawalBottomSheet()
                }

                /**
                 * Gold Lease
                 * **/
                ExternalDeepLinks.GOLD_LEASE -> {
                    if (deepLinkData.size > 2) {
                        val flowType = deepLinkData[2]
                        onDeepLinkNavigation.openGoldLeaseFlow(flowType = flowType)
                    } else {
                        onDeepLinkNavigation.openGoldLeaseFlow(flowType = BaseConstants.GoldLeaseFlowType.HOME_CARD)
                    }
                }

                ExternalDeepLinks.GOLD_LEASE_NEW_LEASE -> {
                    if (deepLinkData.size > 2) {
                        val flowType = deepLinkData[2]
                        onDeepLinkNavigation.openGoldLeaseFlowForNewUser(
                            flowType = flowType, isNewUserLease = true
                        )
                    } else {
                        onDeepLinkNavigation.openGoldLeaseFlowForNewUser(
                            flowType = BaseConstants.GoldLeaseFlowType.HOME_CARD,
                            isNewUserLease = true
                        )
                    }
                }

                ExternalDeepLinks.GOLD_LEASE_MY_ORDERS -> {
                    if (deepLinkData.size > 2) {
                        val flowType = deepLinkData[2]
                        onDeepLinkNavigation.openGoldLeaseMyOrdersScreen(
                            flowType = flowType,
                            tabPosition = BaseConstants.GoldLeaseTabPosition.TAB_MY_ORDERS
                        )
                    } else {
                        onDeepLinkNavigation.openGoldLeaseMyOrdersScreen(
                            flowType = BaseConstants.GoldLeaseFlowType.HOME_CARD,
                            tabPosition = BaseConstants.GoldLeaseTabPosition.TAB_MY_ORDERS
                        )
                    }
                }

                ExternalDeepLinks.GOLD_LEASE_PLANS -> {
                    if (deepLinkData.size > 3) {
                        //dl.myjar.app/goldLeasePlans/{flowType}/{isNewLeaseUser}
                        val flowType = deepLinkData[2]
                        val isNewUserLease = deepLinkData[3].toBooleanStrictOrNull().orFalse()

                        onDeepLinkNavigation.openGoldLeasePlansScreen(
                            flowType = flowType, isNewUserLease = isNewUserLease
                        )
                    } else {
                        onDeepLinkNavigation.openGoldLeasePlansScreen(
                            flowType = BaseConstants.GoldLeaseFlowType.HOME_CARD,
                            isNewUserLease = false
                        )
                    }
                }

                ExternalDeepLinks.GOLD_LEASE_USER_LEASE_DETAILS -> {
                    if (deepLinkData.size > 3) {
                        val flowType = deepLinkData[2]
                        val leaseId = deepLinkData[3]
                        onDeepLinkNavigation.openGoldLeaseUserLeaseDetailsScreen(
                            flowType = flowType, leaseId = leaseId
                        )
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                ExternalDeepLinks.GOLD_LEASE_SUMMARY_RETRY_FLOW -> {
                    if (deepLinkData.size > 3) {
                        // dl.myjar.app/goldLeaseOrderSummary/{flowType}/{goldLeaseV2OrderSummaryScreenDataString}/{leaseId}/{isNewLeaseUser}
                        // But BE will not send goldLeaseV2OrderSummaryScreenDataString in case of retry flow
                        // isNewLeaseUser will always be false for retry flow
                        // Deeplink expected from BE - dl.myjar.app/goldLeaseOrderSummary/{flowType}/{leaseId}

                        val flowType = deepLinkData[2]
                        val leaseId = deepLinkData[3]
                        val isNewUserLease = false
                        onDeepLinkNavigation.openGoldLeaseSummaryRetryFlow(
                            flowType = flowType, leaseId = leaseId, isNewUserLease = isNewUserLease
                        )
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                /**
                 * Custom Web View
                 * **/
                ExternalDeepLinks.WEB_VIEW -> {
                    // dl.myjar.app/webView/{flowType}/{title}/{showToolbar}/{url}
                    if (deepLinkData.size > 5) {
                        val shouldPostAnalyticsFromUrl = true
                        val flowType = deepLinkData[2].ifEmpty { BaseConstants.WebViewFlowType.DEEPLINK }
                        val title = deepLinkData[3].ifEmpty { "Jar" }
                        val showToolbar = deepLinkData.getOrNull(4)?.toBooleanStrictOrNull() ?: true
                        val url =
                            deepLinkData.subList(5, deepLinkData.size).joinToString(separator = "/")
                        onDeepLinkNavigation.openCustomWebView(
                            shouldPostAnalyticsFromUrl = shouldPostAnalyticsFromUrl,
                            url = url,
                            title = title,
                            showToolbar = showToolbar,
                            flowType = flowType
                        )
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                /**
                 * First Coin
                 * **/
                ExternalDeepLinks.FIRST_COIN -> {
                    if (deepLinkData.size > 1) {
                        val toScreen = deepLinkData[1]
                        when (toScreen) {
                            ExternalDeepLinks.FIRST_COIN_TRANSITION -> {
                                onDeepLinkNavigation.openFirstCoinTransitionScreen()
                            }

                            ExternalDeepLinks.FIRST_COIN_PROGRESS -> {
                                onDeepLinkNavigation.openFirstCoinProgressScreen()
                            }

                            ExternalDeepLinks.FIRST_COIN_DELIVERY -> {
                                if (deepLinkData.size > 2) {
                                    val orderId = deepLinkData[2]
                                    onDeepLinkNavigation.openFirstCoinDeliveryScreen(orderId = orderId)
                                } else {
                                    // Missing Data In Deeplink.. Ignore It..
                                }
                            }

                            else -> {
                                onDeepLinkNavigation.openFirstCoinProgressScreen()
                            }
                        }
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                /**
                 * Post Setup
                 * **/
                ExternalDeepLinks.POST_SETUP_DETAILS -> {
                    onDeepLinkNavigation.openPostSetupDetailScreen()
                }


                /**
                 * Daily Saving
                 * **/
                ExternalDeepLinks.UPDATE_DAILY_SAVING_V2 -> {
                    val flow =
                        if (deepLinkData.size > 2) deepLink[2].toString() else BaseConstants.DSPreAutoPayFlowType.SETUP_DS
                    onDeepLinkNavigation.openUpdateDailySavingScreen(flow)
                }

                ExternalDeepLinks.PRE_DAILY_SAVING_AUTOPAY -> {
                    if (deepLinkData.size > 3) {
                        val flowType = deepLinkData[2]
                        val dsAmount = deepLinkData[3].toIntOrNull().orZero()
                        onDeepLinkNavigation.openPreDailySavingAutopay(
                            flowType = flowType,
                            dsAmount = dsAmount
                        )
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                ExternalDeepLinks.UPDATE_AUTOPAY_BANK -> {
                    onDeepLinkNavigation.openUpdateBankForDailySavingAutopay()
                }

                ExternalDeepLinks.DAILY_SAVINGS,
                ExternalDeepLinks.SETUP_DAILY_INVESTMENT -> {
                    val shouldOpenDSIntroBS =
                        deepLinkData.getOrNull(2)?.toBooleanStrictOrNull().orFalse()
                    val fromAbandonFlow =
                        deepLinkData.getOrNull(3)?.toBooleanStrictOrNull().orFalse()
                    onDeepLinkNavigation.openDailySavingScreen(
                        shouldOpenDSIntroBottomSheet = shouldOpenDSIntroBS,
                        fromAbandonFlow = fromAbandonFlow,
                        fromScreen = fromScreenValue,
                        fromSection = fromSection,
                        fromCard = fromCard
                    )
                }

                ExternalDeepLinks.DAILY_SAVINGS_ONBOARDING -> {
                    onDeepLinkNavigation.openDailySavingOnboardingScreen()
                }

                ExternalDeepLinks.DAILY_SAVING_EDUCATION -> {
                    if (deepLinkData.size > 2) {
                        onDeepLinkNavigation.openDailySavingEducationScreen(
                            deepLinkData[2].toBooleanStrictOrNull().orFalse()
                        )
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                ExternalDeepLinks.UPDATE_DAILY_SAVING_MANDATE_SETUP -> {
                    if (deepLinkData.size > 2) {
                        val newDailySavingAmount = deepLinkData[2].toFloat().orZero()
                        val currentDailySavingAmount = deepLinkData.getOrNull(3)
                        val flowSource = deepLinkData.getOrNull(4)
                        onDeepLinkNavigation.updateDailySavingMandateSetup(
                            newDailySavingAmount,
                            currentDailySavingAmount,
                            flowSource
                        )
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                /**
                 * Spends Tracker
                 * **/
                ExternalDeepLinks.SPENDS_TRACKER -> {
                    onDeepLinkNavigation.openSpendsTracker()
                }

                /**
                 * Health Insurance
                 * **/
                ExternalDeepLinks.JAR_HEALTH_INSURANCE -> {
                    onDeepLinkNavigation.openHealthInsuranceLandingPage(fromScreen = fromScreenValue)
                }

                ExternalDeepLinks.JAR_HEALTH_INSURANCE_POST_PURCHASE -> {
                    if (deepLinkData.size > 2) {
                        val insuranceId = deepLinkData[2]
                        onDeepLinkNavigation.openHealthInsurancePostPurchasePage(insuranceId)
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                ExternalDeepLinks.JAR_HEALTH_INSURANCE_ADD_DETAILS -> {
                    onDeepLinkNavigation.openHealthInsuranceAddDetailsPage()
                }

                ExternalDeepLinks.JAR_HEALTH_INSURANCE_SELECT_PLAN -> {
                    if (deepLinkData.size > 2) {
                        val orderId = deepLinkData[2]
                        onDeepLinkNavigation.openHealthInsuranceSelectPlanScreen(orderId)

                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                ExternalDeepLinks.JAR_HEALTH_INSURANCE_MANAGE_SCREEN -> {
                    if (deepLinkData.size > 2) {
                        val insuranceId = deepLinkData[2]
                        onDeepLinkNavigation.openHealthInsuranceManageScreen(insuranceId)
                    } else {
                        // Missing Data In Deeplink.. Ignore It..
                    }
                }

                /**
                 * Sell Gold
                 * **/
                ExternalDeepLinks.SELL_GOLD -> {
                    onDeepLinkNavigation.openSellGoldScreen()
                }

                /**
                 * Survey
                 * **/
                ExternalDeepLinks.SURVEY -> {
                    onDeepLinkNavigation.openSurveyScreen()
                }

                /**
                 * KYC (Id Verification)
                 * **/
                ExternalDeepLinks.KYC_VERIFICATION,
                ExternalDeepLinks.KYC,
                ExternalDeepLinks.KYC_DETAILS -> {
                    onDeepLinkNavigation.openKycScreen()
                }

                /**
                 * Savings Goal
                 * **/
                ExternalDeepLinks.SAVING_GOAL -> {
                    onDeepLinkNavigation.openGoalBasedSaving()
                }

                ExternalDeepLinks.SAVINGS_GOALS_SETTINGS -> {
                    onDeepLinkNavigation.openGoalBasedSavingSettings()
                }

                ExternalDeepLinks.EXTERNAL_PROMO_CODE -> {
                    onDeepLinkNavigation.openPromoCodeDialog()
                }
                /**
                 * Quests
                 * **/
                ExternalDeepLinks.QUEST -> {
                    val toScreen = deepLinkData.getOrNull(2)
                    when (toScreen) {
                        ExternalDeepLinks.QUEST_DASHBOARD -> {
                            onDeepLinkNavigation.openQuestDashboard(fromScreenValue)
                        }

                        ExternalDeepLinks.QUEST_COUPON_DETAILS -> {
                            val brandCouponId = deepLinkData.getOrNull(3)
                            brandCouponId?.let {
                                onDeepLinkNavigation.openQuestCouponDetails(
                                    fromScreenValue,
                                    brandCouponId
                                )
                            } ?: kotlin.run {
                                // Unsupported case... Ignore it..
                            }
                        }

                        ExternalDeepLinks.QUEST_SPLASH -> {
                            onDeepLinkNavigation.openQuestSplash(fromScreenValue)
                        }

                        ExternalDeepLinks.QUEST_ALL_REWARDS -> {
                            onDeepLinkNavigation.openQuestAllRewards(fromScreenValue)
                        }

                        else -> {
                            // Unsupported case... Ignore it..
                        }
                    }
                }
                /**
                 * Initiate One Time Payment for buying gold
                 * **/
                ExternalDeepLinks.INITIATE_PAYMENT -> {
                    if (deepLinkData.size > 3) {
                        onDeepLinkNavigation.initiateOneTimePayment(
                            deepLinkData[2].toFloatOrNull().orZero(),
                            deepLinkData[3]
                        )
                    } else {
                        // no - op
                    }
                }

                ExternalDeepLinks.DAILY_INVESTMENT_UPDATE_FRAGMENT ->{
                    onDeepLinkNavigation.openDailyInvestmentUpdateFragment()
                }

                ExternalDeepLinks.CALCULATOR -> {
                    onDeepLinkNavigation.openCalculator(fromScreen, fromSection)
                }

                ExternalDeepLinks.SAVINGS_CALCULATOR -> {
                    onDeepLinkNavigation.openGoldCalculator(fromScreen, fromSection)
                }

                ExternalDeepLinks.STORIES -> {
                    if (deepLinkData.size > 2) {
                        onDeepLinkNavigation.openStoryByPageId(deepLinkData[2])
                    } else {

                    }
                }

                else -> {
                    // Unsupported deeplink.. Ignore this case
                }
            }
        }
    }
}

interface OnDeepLinkNavigation {

    // Buy Gold
    fun openBuyGoldFlowWithCoupon(
        couponCode: String,
        couponType: String,
        isFromJackpotScreen: Boolean,
        buyGoldFlowContext: String
    )

    fun openSingleHomeFeedCTA(flow: String)

    fun openBuyGoldFlowWithPrefillAmount(
        prefillAmount: Float, buyGoldFlowContext: String
    )

    fun openBuyGoldFlowWithWeeklyChallengeAmount(
        amount: Float, buyGoldFlowContext: String
    )

    // Transaction Screen
    fun openTransactionScreen(transactionType: String)

    // Viba Card
    fun openVibaWebView(
        shouldPostAnalyticsFromUrl: Boolean,
        url: String,
        title: String,
        showToolbar: Boolean
    )

    // Refer And Earn
    fun openReferAndEarn()

    fun openReferralFaqScreen()

    fun shareReferralInvite()

    // Jewellery voucher
    fun openJewelleryVoucherBrandCatalogueScreen()

    fun openJewelleryVoucherOpenMyOrdersScreen(tabType: String?)

    fun openJewelleryVoucherStatusScreen(voucherId: String?, orderType: String?)

    fun openJewelleryVoucherIntroScreen()

    fun openJewelleryVoucherPurchaseScreen(voucherId: String?)

    // Transaction Detail
    fun openOldTransactionDetailScreen(orderId: String, txnId: String, sourceType: String)

    fun openNewTransactionDetailScreen(orderId: String, txnId: String, sourceType: String)

    fun openTransactionDetailBottomSheet(txnId: String)

    // Gold Delivery
    fun openGoldDeliveryEntryScreen(productId: String?)

    fun openGoldDeliveryCartScreen()

    // Daily Saving Cancellation
    fun openDailySavingCancellationEntryScreen()

    // Daily Saving
    fun updateDailySavingMandateSetup(
        newDailySavingAmount: Float,
        currentDailySavingAmount: String?,
        flowSource: String?
    )

    fun openUpdateBankForDailySavingAutopay()

    fun openDailySavingScreen(
        shouldOpenDSIntroBottomSheet: Boolean,
        fromAbandonFlow: Boolean,
        fromScreen: String,
        fromSection: String?,
        fromCard: String?
    )

    fun openDailySavingOnboardingScreen()

    fun openDailySavingEducationScreen(isSetupFlow: Boolean)

    fun openPreDailySavingAutopay(flowType: String, dsAmount: Int)

    // Spins
    fun openSpinsScreen()

    // Promo Code
    fun openPromoCodeScreen()

    // Gold Gifting
    fun openGoldGiftingScreen()

    // Change Language
    fun openChangeLanguageScreen()

    // Settings Page
    fun openSettingsPage()

    // Profile Page
    fun openProfilePage()

    // Help & Support
    fun openAppHelpAndSupportPage()

    // Insurance Help & Support
    fun openInsuranceHelpAndSupportPage()

    // Daily Saving Settings
    fun openDailySavingSettings(
        fromScreen: String,
        fromSection: String? = null,
        fromCard: String? = null
    )

    // Gold Price Detail
    fun openGoldPriceDetailScreen()

    //Round Off
    fun openRoundOffScreen(fromScreen: String? = null)

    fun openSetupAutoPayScreenForRoundOffAndDailySaving()

    fun openRoundOffExplanation(fromScreen: String)

    // Jar Duo
    fun openJarDuoScreen()

    fun openJarDuoOnboardingScreen()

    // Offer List
    fun openOfferListScreen()

    // Lending KYC
    fun openLendingKyc()

    // KYC (Id Verification)
    fun openKycScreen()

    // Gold Sip
    fun openGoldSipScreen()

    fun openGoldSipIntroScreen()

    fun openGoldSipTypeSelectionScreen(subscriptionType: String)

    fun openGoldSipDetailScreen()

    // Help Videos
    fun openHelpVideosListingScreen()

    // Weekly Magic
    fun openWeeklyMagicFlow(
        fromScreen: String,
        checkMysteryCardOrChallengeWin: Boolean
    )

    // Lending
    fun openLendingOnboardingFlow(fromScreen: String)

    fun openRealTimeLendingFlow(fromScreen: String)
    fun openCheckCreditScoreFlow(fromScreen: String)

    // Sell Gold (Withdrawal)
    fun openWithdrawalBottomSheet()

    // Gold Lease
    fun openGoldLeaseFlow(flowType: String)

    fun openGoldLeaseFlowForNewUser(
        flowType: String,
        isNewUserLease: Boolean
    )

    fun openGoldLeaseMyOrdersScreen(
        flowType: String,
        tabPosition: Int
    )

    fun openGoldLeasePlansScreen(
        flowType: String,
        isNewUserLease: Boolean
    )

    fun openGoldLeaseUserLeaseDetailsScreen(
        flowType: String,
        leaseId: String
    )

    fun openGoldLeaseSummaryRetryFlow(
        flowType: String,
        leaseId: String,
        isNewUserLease: Boolean
    )

    // Custom WebView
    fun openCustomWebView(
        shouldPostAnalyticsFromUrl: Boolean,
        url: String,
        title: String,
        showToolbar: Boolean,
        flowType: String
    )

    // First Coin
    fun openFirstCoinTransitionScreen()

    fun openFirstCoinProgressScreen()

    fun openFirstCoinDeliveryScreen(orderId: String)

    // Post Setup
    fun openPostSetupDetailScreen()

    // Update Daily Saving
    fun openUpdateDailySavingScreen(flow: String)

    // Health Insurance
    fun openHealthInsuranceLandingPage(fromScreen: String)

    fun openHealthInsurancePostPurchasePage(insuranceId: String)

    fun openHealthInsuranceAddDetailsPage()

    fun openHealthInsuranceSelectPlanScreen(orderId: String)

    fun openHealthInsuranceManageScreen(orderId: String)

    // Spends Tracker
    fun openSpendsTracker()

    // Sell Gold
    fun openSellGoldScreen()

    // Survey
    fun openSurveyScreen()

    // Goal Based Savings
    fun openGoalBasedSaving()

    fun openGoalBasedSavingSettings()

    // Quests
    fun openQuestCouponDetails(fromScreen: String, brandCouponId: String)

    fun openQuestSplash(fromScreen: String)

    fun openQuestDashboard(fromScreen: String)

    fun openQuestAllRewards(fromScreen: String)
    fun openPromoCodeDialog()

    //Direct Initiate One Time Payment for gold buy
    fun initiateOneTimePayment(amount: Float, paymentSource: String)

    fun openDailyInvestmentUpdateFragment()

    fun openManualBuyGraph()

    //EMI calculator
    fun openCalculator(fromScreen: String?, fromSection: String?)
    fun openGoldCalculator(fromScreen: String?, fromSection: String?)

    fun openExitSurvey(surveyFor: String)

    fun openStoryByPageId(pageId: String)
}