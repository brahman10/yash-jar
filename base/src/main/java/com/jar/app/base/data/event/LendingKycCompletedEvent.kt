package com.jar.app.base.data.event

data class LendingKycCompletedEvent(val microLoanDetailsUrl: String, val type: LendingRedirectionType = LendingRedirectionType.TYPE_REDIRECTION_LENDING_WEBVIEW)

//To decide where to redirect user after KYC is completed for Lending flow
enum class LendingRedirectionType {
    TYPE_REDIRECTION_LENDING_WEBVIEW,
    TYPE_REDIRECTION_LENDING_INAPP,
}