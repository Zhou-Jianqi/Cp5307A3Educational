package com.edu.vocabularyfield

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.edu.vocabularyfield.ui.theme.Cp5307A3EducationalAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("vocab_books", Context.MODE_PRIVATE).edit().clear().commit()
    }

    private fun launchApp() {
        composeTestRule.setContent {
            Cp5307A3EducationalAppTheme {
                Cp5307A3EducationalAppApp()
            }
        }
    }

    @Test
    fun allTabLabelsDisplayed() {
        launchApp()
        composeTestRule.onNodeWithText("Home").assertExists()
        composeTestRule.onNodeWithText("Learning").assertExists()
        composeTestRule.onNodeWithText("Setting").assertExists()
        composeTestRule.onNodeWithText("Statistics").assertExists()
    }

    @Test
    fun defaultTab_showsHomeContent() {
        launchApp()
        composeTestRule.onNodeWithText("Welcome to VocabField!").assertIsDisplayed()
    }

    @Test
    fun navigateToStatistics_showsStatisticsContent() {
        launchApp()
        composeTestRule.onNodeWithText("Statistics").performClick()
        composeTestRule.onNodeWithText("Your consecutive days of\nmemorizing words:")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Your stars:").assertIsDisplayed()
    }

    @Test
    fun navigateToSetting_showsSettingContent() {
        launchApp()
        composeTestRule.onNodeWithText("Setting").performClick()
        composeTestRule.onNodeWithText("Select a level background that you favorite:")
            .assertIsDisplayed()
    }

    @Test
    fun navigateToLearning_showsLearningContent() {
        launchApp()
        composeTestRule.onNodeWithText("Learning").performClick()
        composeTestRule.onNodeWithText(
            "Select a vocabulary book from the list below to play\uFF1A"
        ).assertIsDisplayed()
    }

    @Test
    fun navigateBackToHome_showsHomeContent() {
        launchApp()
        composeTestRule.onNodeWithText("Statistics").performClick()
        composeTestRule.onNodeWithText("Your stars:").assertIsDisplayed()

        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Welcome to VocabField!").assertIsDisplayed()
    }

    @Test
    fun navigateBetweenAllTabs() {
        launchApp()

        // Home (default)
        composeTestRule.onNodeWithText("Welcome to VocabField!").assertIsDisplayed()

        // Learning
        composeTestRule.onNodeWithText("Learning").performClick()
        composeTestRule.onNodeWithText(
            "Select a vocabulary book from the list below to play\uFF1A"
        ).assertIsDisplayed()

        // Setting
        composeTestRule.onNodeWithText("Setting").performClick()
        composeTestRule.onNodeWithText("Select a level background that you favorite:")
            .assertIsDisplayed()

        // Statistics
        composeTestRule.onNodeWithText("Statistics").performClick()
        composeTestRule.onNodeWithText("Your stars:").assertIsDisplayed()

        // Back to Home
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNodeWithText("Welcome to VocabField!").assertIsDisplayed()
    }
}
