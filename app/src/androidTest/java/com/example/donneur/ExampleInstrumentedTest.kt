package com.example.donneur

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.donneur", appContext.packageName)
    }

    // Example Compose UI test placeholder
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun postComposable_displaysPostButton() {
        composeTestRule.setContent {
            Post()
        }
        // This checks if the "Post" button exists in the UI
        composeTestRule.onNodeWithText("Post").assertExists()
    }
}
