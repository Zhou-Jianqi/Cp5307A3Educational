package com.edu.vocabularyfield

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.edu.vocabularyfield.screen.StatisticsScreen
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class StatisticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: VocabularyViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        app.getSharedPreferences("vocab_books", Context.MODE_PRIVATE).edit().clear().commit()
        viewModel = VocabularyViewModel(app)
    }

    @Test
    fun displaysCalendarTitle() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText("Your consecutive days of\nmemorizing words:")
            .assertIsDisplayed()
    }

    @Test
    fun displaysStarsSection() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText("Your stars:").assertIsDisplayed()
    }

    @Test
    fun displaysCurrentMonthAndYear() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        val now = YearMonth.now()
        val monthName = now.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        composeTestRule.onNodeWithText("$monthName ${now.year}").assertIsDisplayed()
    }

    @Test
    fun displaysDayOfWeekHeaders() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
            composeTestRule.onNodeWithText(day).assertIsDisplayed()
        }
    }

    @Test
    fun previousMonthNavigation_showsPreviousMonth() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText("<").performClick()

        val prevMonth = YearMonth.now().minusMonths(1)
        val monthName = prevMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        composeTestRule.onNodeWithText("$monthName ${prevMonth.year}").assertIsDisplayed()
    }

    @Test
    fun nextMonthNavigation_showsNextMonth() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText(">").performClick()

        val nextMonth = YearMonth.now().plusMonths(1)
        val monthName = nextMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        composeTestRule.onNodeWithText("$monthName ${nextMonth.year}").assertIsDisplayed()
    }

    @Test
    fun displaysTodayDate() {
        composeTestRule.setContent {
            StatisticsScreen(viewModel = viewModel)
        }
        val today = java.time.LocalDate.now().dayOfMonth.toString()
        composeTestRule.onNodeWithText(today).assertExists()
    }
}
