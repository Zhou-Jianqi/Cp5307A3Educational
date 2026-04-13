package com.edu.vocabularyfield

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.data.VocabularyRepository
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var app: Application

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("vocab_books", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun displaysWelcomeText() {
        val viewModel = VocabularyViewModel(app)
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("Welcome to VocabField!").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsAddPromptMessage() {
        val viewModel = VocabularyViewModel(app)
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("Start learning by adding a vocabulary book~")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_showsStartAddingButton() {
        val viewModel = VocabularyViewModel(app)
        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("Start adding").assertIsDisplayed()
    }

    @Test
    fun withBooks_displaysBookName() {
        val repo = VocabularyRepository(app)
        repo.saveBook(VocabularyBook("1", "IELTS Academic Word List", "Core vocab", 570, "Academic"))
        val viewModel = VocabularyViewModel(app)

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("IELTS Academic Word List").assertIsDisplayed()
    }

    @Test
    fun withBooks_displaysBookDescription() {
        val repo = VocabularyRepository(app)
        repo.saveBook(VocabularyBook("1", "Test Book", "A test description", 100, "General"))
        val viewModel = VocabularyViewModel(app)

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("A test description").assertIsDisplayed()
    }

    @Test
    fun withBooks_displaysWordCountAndCategory() {
        val repo = VocabularyRepository(app)
        repo.saveBook(VocabularyBook("1", "Test Book", "Desc", 250, "Advanced"))
        val viewModel = VocabularyViewModel(app)

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("250 words \u00b7 Advanced").assertIsDisplayed()
    }

    @Test
    fun withBooks_showsAddVocabularyBookButton() {
        val repo = VocabularyRepository(app)
        repo.saveBook(VocabularyBook("1", "Test Book", "Desc", 10, "Cat"))
        val viewModel = VocabularyViewModel(app)

        composeTestRule.setContent {
            HomeScreen(viewModel = viewModel, onNavigateToAddBook = {})
        }
        composeTestRule.onNodeWithText("Add Vocabulary Book").assertIsDisplayed()
    }
}
