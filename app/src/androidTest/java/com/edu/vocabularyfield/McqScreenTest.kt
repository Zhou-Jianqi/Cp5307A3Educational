package com.edu.vocabularyfield

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.screen.McqScreen
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class McqScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: VocabularyViewModel
    private val testBook = VocabularyBook("test-1", "Test Book", "Desc", 26, "Test")

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("vocab_books", Context.MODE_PRIVATE).edit().clear().commit()
        viewModel = VocabularyViewModel(app)
        viewModel.startMcqGame(testBook)
    }

    @Test
    fun displaysLevelIndicator() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        composeTestRule.onNodeWithText("Level 1").assertIsDisplayed()
    }

    @Test
    fun displaysScoreValue() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        // Initial score is 0
        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun displaysLivesValue() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        // Initial lives is 3
        composeTestRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun displaysQuestionDefinition() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        val definition = viewModel.mcqGameState.value.questions[0].definition
        composeTestRule.onNodeWithText(definition).assertIsDisplayed()
    }

    @Test
    fun displaysFourAnswerOptions() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        val options = viewModel.mcqGameState.value.questions[0].options
        options.forEach { option ->
            composeTestRule.onNodeWithText(option).assertIsDisplayed()
        }
    }

    @Test
    fun correctAnswer_advancesToLevel2() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        val correctWord = viewModel.mcqGameState.value.questions[0].correctWord
        composeTestRule.onNodeWithText(correctWord).performClick()
        composeTestRule.onNodeWithText("Level 2").assertIsDisplayed()
    }

    @Test
    fun wrongAnswer_reducesLivesTo2() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }
        val question = viewModel.mcqGameState.value.questions[0]
        val wrongWord = question.options.first { it != question.correctWord }
        composeTestRule.onNodeWithText(wrongWord).performClick()
        composeTestRule.onNodeWithText("2").assertExists()
    }

    @Test
    fun gameOver_showsGameOverContent() {
        composeTestRule.setContent {
            McqScreen(viewModel = viewModel, onBack = {})
        }

        // Answer 3 questions wrong to trigger game over
        repeat(3) {
            val state = viewModel.mcqGameState.value
            if (!state.isGameOver) {
                val question = state.questions[state.currentIndex]
                val wrongWord = question.options.first { it != question.correctWord }
                composeTestRule.onNodeWithText(wrongWord).performClick()
            }
        }

        composeTestRule.onNodeWithText("Keep your determination!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back to Learning").assertIsDisplayed()
    }
}
