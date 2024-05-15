package com.jar.app.core_ui

import com.jar.app.core_ui.extension.digits
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
        val res = 12345.digits().map{ 0 }.joinToString("").toInt()
        println(res.toString())
    }
}