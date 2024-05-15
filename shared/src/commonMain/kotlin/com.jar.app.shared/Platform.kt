package com.jar.app.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform