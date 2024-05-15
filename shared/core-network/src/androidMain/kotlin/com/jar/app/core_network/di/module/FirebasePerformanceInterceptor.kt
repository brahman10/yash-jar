package com.jar.app.core_network.di.module


import okhttp3.Interceptor
import okhttp3.Response
import com.google.firebase.perf.FirebasePerformance
import okhttp3.Protocol

class FirebasePerformanceInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Start a new HttpMetric for the request URL and method
        val metric = FirebasePerformance.getInstance().newHttpMetric(
            request.url.toString(),
            request.method
        )

        // Record request payload size (if available)
        request.body?.let { metric.setRequestPayloadSize(it.contentLength()) }

        metric.start()

        // Proceed with the actual request
        var response: Response? = null

        try {
            response = chain.proceed(request)
        } finally {
            metric.setHttpResponseCode(response?.code ?: 404)
            metric.stop()
        }

        // Set the response details on the metric and if the response is null then we're assuming the url not found (404 error)
        return response ?: Response.Builder()
            .code(404)
            .message("Not Found")
            .protocol(Protocol.HTTP_1_1)
            .request(request)
            .build()
    }
}