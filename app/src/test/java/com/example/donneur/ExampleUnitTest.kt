package com.example.donneur

import org.junit.Test
import org.junit.Assert.*

// Unit tests for PostData and time formatting function
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun postData_defaults_are_correct() {
        val post = PostData()
        assertEquals("", post.id)
        assertEquals("", post.content)
        assertEquals("Mohamed Achek", post.author)
        assertEquals("@med_6", post.authorTag)
        assertTrue(post.timestamp > 0)
    }

    @Test
    fun toTimeAgo_just_now() {
        val now = System.currentTimeMillis()
        val result = now.toTimeAgo()
        assertEquals("Just now", result)
    }

    @Test
    fun toTimeAgo_minutes() {
        val now = System.currentTimeMillis()
        val fiveMinutesAgo = now - 5 * 60 * 1000
        val result = fiveMinutesAgo.toTimeAgo()
        assertTrue(result.contains("min"))
    }

    @Test
    fun toTimeAgo_hours() {
        val now = System.currentTimeMillis()
        val twoHoursAgo = now - 2 * 60 * 60 * 1000
        val result = twoHoursAgo.toTimeAgo()
        assertTrue(result.contains("hour"))
    }
}
