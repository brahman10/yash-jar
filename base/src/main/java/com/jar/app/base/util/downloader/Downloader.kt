package com.jar.app.base.util.downloader


interface Downloader {
    fun downloadVideoFile(url: String): Long?
}