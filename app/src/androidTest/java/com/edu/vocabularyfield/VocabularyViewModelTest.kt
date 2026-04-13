package com.edu.vocabularyfield

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.edu.vocabularyfield.data.SavedMcqProgress
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.data.VocabularyRepository
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4::class)
class VocabularyViewModelTest {

    private lateinit var app: Application
    private lateinit var viewModel: VocabularyViewModel
    private lateinit var repository: VocabularyRepository

    private val testBook = VocabularyBook("test-1", "Test Book", "Description", 26, "Test")

    @Before
    fun setup() {
        app = ApplicationProvider.getApplicationContext()
        app.getSharedPreferences("vocab_books", Context.MODE_PRIVATE).edit().clear().commit()
        repository = VocabularyRepository(app)
        viewModel = VocabularyViewModel(app)
    }

    // --- Init behavior ---

    @Test
    fun init_recordsTodayCheckIn() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        assertTrue(viewModel.checkInDates.value.contains(today))
    }

    @Test
    fun init_downloadsEmptyByDefault() {
        assertTrue(viewModel.downloadedBooks.value.isEmpty())
    }

    @Test
    fun init_progressMapEmptyByDefault() {
        assertTrue(viewModel.bookProgressMap.value.isEmpty())
    }

    // --- FIXED_WORDS ---

    @Test
    fun fixedWords_has26Words() {
        assertEquals(26, VocabularyViewModel.FIXED_WORDS.size)
    }

    @Test
    fun fixedWords_allHaveNonEmptyFields() {
        VocabularyViewModel.FIXED_WORDS.forEach { word ->
            assertTrue("Word should not be empty", word.word.isNotEmpty())
            assertTrue("Definition should not be empty for ${word.word}", word.definition.isNotEmpty())
        }
    }

    @Test
    fun fixedWords_allWordsUnique() {
        val words = VocabularyViewModel.FIXED_WORDS.map { it.word }
        assertEquals(words.size, words.toSet().size)
    }

    // --- MCQ Game: Start ---

    @Test
    fun startMcqGame_creates26Questions() {
        viewModel.startMcqGame(testBook)
        assertEquals(26, viewModel.mcqGameState.value.questions.size)
    }

    @Test
    fun startMcqGame_setsCorrectInitialState() {
        viewModel.startMcqGame(testBook)
        val state = viewModel.mcqGameState.value
        assertEquals("test-1", state.bookId)
        assertEquals("Test Book", state.bookName)
        assertEquals(0, state.currentIndex)
        assertEquals(1, state.currentLevel)
        assertEquals(0, state.score)
        assertEquals(3, state.lives)
        assertFalse(state.isGameOver)
        assertFalse(state.isAllLevelsCompleted)
    }

    @Test
    fun startMcqGame_eachQuestionHas4Options() {
        viewModel.startMcqGame(testBook)
        viewModel.mcqGameState.value.questions.forEach { question ->
            assertEquals(4, question.options.size)
        }
    }

    @Test
    fun startMcqGame_correctWordAlwaysInOptions() {
        viewModel.startMcqGame(testBook)
        viewModel.mcqGameState.value.questions.forEach { question ->
            assertTrue(
                "Options should contain correct word '${question.correctWord}'",
                question.options.contains(question.correctWord)
            )
        }
    }

    @Test
    fun startMcqGame_optionsHaveUniqueWords() {
        viewModel.startMcqGame(testBook)
        viewModel.mcqGameState.value.questions.forEach { question ->
            assertEquals(
                "Options should all be unique for question '${question.definition}'",
                4,
                question.options.toSet().size
            )
        }
    }

    // --- MCQ Game: Answering ---

    @Test
    fun answerCorrect_incrementsScore() {
        viewModel.startMcqGame(testBook)
        val correctWord = viewModel.mcqGameState.value.questions[0].correctWord
        viewModel.answerMcqQuestion(correctWord)
        assertEquals(1, viewModel.mcqGameState.value.score)
    }

    @Test
    fun answerCorrect_advancesLevel() {
        viewModel.startMcqGame(testBook)
        val correctWord = viewModel.mcqGameState.value.questions[0].correctWord
        viewModel.answerMcqQuestion(correctWord)
        assertEquals(2, viewModel.mcqGameState.value.currentLevel)
        assertEquals(1, viewModel.mcqGameState.value.currentIndex)
    }

    @Test
    fun answerCorrect_doesNotChangeLives() {
        viewModel.startMcqGame(testBook)
        val correctWord = viewModel.mcqGameState.value.questions[0].correctWord
        viewModel.answerMcqQuestion(correctWord)
        assertEquals(3, viewModel.mcqGameState.value.lives)
    }

    @Test
    fun answerWrong_decrementsLives() {
        viewModel.startMcqGame(testBook)
        val question = viewModel.mcqGameState.value.questions[0]
        val wrongWord = question.options.first { it != question.correctWord }
        viewModel.answerMcqQuestion(wrongWord)
        assertEquals(2, viewModel.mcqGameState.value.lives)
    }

    @Test
    fun answerWrong_doesNotChangeScore() {
        viewModel.startMcqGame(testBook)
        val question = viewModel.mcqGameState.value.questions[0]
        val wrongWord = question.options.first { it != question.correctWord }
        viewModel.answerMcqQuestion(wrongWord)
        assertEquals(0, viewModel.mcqGameState.value.score)
    }

    @Test
    fun gameOver_whenLivesReachZero() {
        viewModel.startMcqGame(testBook)

        // Answer 3 questions wrong to lose all lives
        repeat(3) {
            val state = viewModel.mcqGameState.value
            val question = state.questions[state.currentIndex]
            val wrongWord = question.options.first { it != question.correctWord }
            viewModel.answerMcqQuestion(wrongWord)
        }

        assertTrue(viewModel.mcqGameState.value.isGameOver)
        assertEquals(0, viewModel.mcqGameState.value.lives)
    }

    @Test
    fun cannotAnswer_afterGameOver() {
        viewModel.startMcqGame(testBook)

        repeat(3) {
            val state = viewModel.mcqGameState.value
            val question = state.questions[state.currentIndex]
            val wrongWord = question.options.first { it != question.correctWord }
            viewModel.answerMcqQuestion(wrongWord)
        }

        val stateAfterGameOver = viewModel.mcqGameState.value
        assertTrue(stateAfterGameOver.isGameOver)

        // Try to answer again
        val question = stateAfterGameOver.questions[stateAfterGameOver.currentIndex]
        viewModel.answerMcqQuestion(question.correctWord)

        // State should not change
        assertEquals(stateAfterGameOver, viewModel.mcqGameState.value)
    }

    @Test
    fun allLevelsCompleted_afterAllCorrectAnswers() {
        viewModel.startMcqGame(testBook)

        repeat(26) {
            val state = viewModel.mcqGameState.value
            val question = state.questions[state.currentIndex]
            viewModel.answerMcqQuestion(question.correctWord)
        }

        val finalState = viewModel.mcqGameState.value
        assertTrue(finalState.isAllLevelsCompleted)
        assertFalse(finalState.isGameOver)
        assertEquals(26, finalState.score)
        assertTrue(finalState.lives > 0)
    }

    @Test
    fun cannotAnswer_afterAllLevelsCompleted() {
        viewModel.startMcqGame(testBook)

        repeat(26) {
            val state = viewModel.mcqGameState.value
            val question = state.questions[state.currentIndex]
            viewModel.answerMcqQuestion(question.correctWord)
        }

        val completedState = viewModel.mcqGameState.value
        assertTrue(completedState.isAllLevelsCompleted)

        viewModel.answerMcqQuestion("anything")
        assertEquals(completedState, viewModel.mcqGameState.value)
    }

    // --- MCQ Game: Save/Resume ---

    @Test
    fun saveAndExit_preservesProgressInRepository() {
        viewModel.startMcqGame(testBook)

        // Answer 2 questions correctly
        repeat(2) {
            val state = viewModel.mcqGameState.value
            viewModel.answerMcqQuestion(state.questions[state.currentIndex].correctWord)
        }

        viewModel.saveAndExitMcqGame()

        val saved = repository.getMcqProgress("test-1")
        assertNotNull(saved)
        assertEquals(3, saved!!.currentLevel)
        assertEquals(2, saved.score)
        assertEquals(3, saved.lives)
    }

    @Test
    fun resumeGame_restoresSavedProgress() {
        // Seed saved progress
        val indices = VocabularyViewModel.FIXED_WORDS.indices.toList()
        repository.saveMcqProgress(
            "test-1",
            SavedMcqProgress(
                currentLevel = 5,
                score = 4,
                lives = 2,
                shuffledWordIndices = indices
            )
        )

        // Create fresh ViewModel that loads the progress
        val freshViewModel = VocabularyViewModel(app)
        freshViewModel.startMcqGame(testBook)

        val state = freshViewModel.mcqGameState.value
        assertEquals(5, state.currentLevel)
        assertEquals(4, state.currentIndex)
        assertEquals(4, state.score)
        assertEquals(2, state.lives)
    }

    @Test
    fun gameOver_deletesProgress() {
        viewModel.startMcqGame(testBook)

        // Save progress first
        viewModel.saveAndExitMcqGame()
        assertNotNull(repository.getMcqProgress("test-1"))

        // Start again and lose
        viewModel.startMcqGame(testBook)
        repeat(3) {
            val state = viewModel.mcqGameState.value
            val question = state.questions[state.currentIndex]
            val wrongWord = question.options.first { it != question.correctWord }
            viewModel.answerMcqQuestion(wrongWord)
        }

        assertNull(repository.getMcqProgress("test-1"))
    }

    // --- Book Management ---

    @Test
    fun canAddMore_trueWhenUnderLimit() {
        assertTrue(viewModel.canAddMore())
    }

    @Test
    fun canAddMore_falseWhenAtLimit() {
        repeat(5) { i ->
            repository.saveBook(VocabularyBook("$i", "Book $i", "D", 10, "C"))
        }
        val vm = VocabularyViewModel(app)
        assertFalse(vm.canAddMore())
    }

    @Test
    fun isBookDownloaded_falseForNewBook() {
        assertFalse(viewModel.isBookDownloaded("nonexistent"))
    }

    @Test
    fun isBookDownloaded_trueForSavedBook() {
        repository.saveBook(testBook)
        val vm = VocabularyViewModel(app)
        assertTrue(vm.isBookDownloaded("test-1"))
    }

    @Test
    fun deleteBook_removesBookAndProgress() {
        repository.saveBook(testBook)
        repository.saveMcqProgress("test-1", SavedMcqProgress(1, 0, 3, listOf(0)))
        val vm = VocabularyViewModel(app)

        vm.deleteBook("test-1")

        assertFalse(vm.isBookDownloaded("test-1"))
        assertNull(repository.getMcqProgress("test-1"))
    }

    // --- Daily Scores ---

    @Test
    fun answerCorrect_updatesDailyScore() {
        viewModel.startMcqGame(testBook)
        val correctWord = viewModel.mcqGameState.value.questions[0].correctWord
        viewModel.answerMcqQuestion(correctWord)

        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        assertEquals(1, viewModel.dailyScores.value[today])
    }

    @Test
    fun multipleCorrectAnswers_accumulateDailyScore() {
        viewModel.startMcqGame(testBook)

        repeat(3) {
            val state = viewModel.mcqGameState.value
            viewModel.answerMcqQuestion(state.questions[state.currentIndex].correctWord)
        }

        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        assertEquals(3, viewModel.dailyScores.value[today])
    }

    @Test
    fun wrongAnswer_doesNotUpdateDailyScore() {
        viewModel.startMcqGame(testBook)
        val question = viewModel.mcqGameState.value.questions[0]
        val wrongWord = question.options.first { it != question.correctWord }
        viewModel.answerMcqQuestion(wrongWord)

        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val todayScore = viewModel.dailyScores.value[today]
        assertTrue(todayScore == null || todayScore == 0)
    }

    // --- Background ---

    @Test
    fun selectedBackground_defaultIsSummer() {
        assertEquals("Summer", viewModel.selectedBackground.value)
    }

    @Test
    fun setSelectedBackground_updatesStateAndPersists() {
        viewModel.setSelectedBackground("Winter")
        assertEquals("Winter", viewModel.selectedBackground.value)
        assertEquals("Winter", repository.getSelectedBackground())
    }
}
