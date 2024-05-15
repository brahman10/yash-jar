package com.jar.app.feature_lending

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkLendingParser_isCorrect(){
        val response =  DummyDataProvider.getDummyLendingJourneyResponse()
        val value =  response.data.screenData[response.data.currentScreen]
        println(value.toString())
    }
}