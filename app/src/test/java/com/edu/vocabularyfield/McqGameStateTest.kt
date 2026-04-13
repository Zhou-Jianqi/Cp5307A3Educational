package com.edu.vocabularyfield

import com.edu.vocabularyfield.viewmodel.McqGameState
import com.edu.vocabularyfield.viewmodel.McqQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class McqGameStateTest {

    @Test
    fun defaultState_hasCorrectInitialValues() {
        val state = McqGameState()
        assertEquals("", state.bookId)
        assertEquals("", state.bookName)
        assertTrue(state.questions.isEmpty())
        assertEquals(0, state.currentIndex)
        assertEquals(1, state.currentLevel)
        assertEquals(0, state.score)
        assertEquals(3, state.lives)
        assertFalse(state.isGameOver)
        assertFalse(state.isAllLevelsCompleted)
        assertFalse(state.isLoading)
        assertTrue(state.shuffledWordIndices.isEmpty())
    }

    @Test
    fun copyWithCorrectAnswer_incrementsScoreAndAdvancesLevel() {
        val state = McqGameState(score = 5, currentIndex = 2, currentLevel = 3, lives = 3)
        val updated = state.copy(
            score = state.score + 1,
            currentIndex = state.currentIndex + 1,
            currentLevel = state.currentLevel + 1
        )
        assertEquals(6, updated.score)
        assertEquals(3, updated.currentIndex)
        assertEquals(4, updated.currentLevel)
        assertEquals(3, updated.lives)
    }

    @Test
    fun copyWithWrongAnswer_decrementsLivesOnly() {
        val state = McqGameState(lives = 3, score = 5, currentIndex = 2, currentLevel = 3)
        val updated = state.copy(lives = state.lives - 1)
        assertEquals(2, updated.lives)
        assertEquals(5, updated.score)
        assertEquals(2, updated.currentIndex)
        assertEquals(3, updated.currentLevel)
    }

    @Test
    fun gameOverState_hasZeroLivesAndGameOverTrue() {
        val state = McqGameState(lives = 0, isGameOver = true, score = 10)
        assertTrue(state.isGameOver)
        assertEquals(0, state.lives)
        assertFalse(state.isAllLevelsCompleted)
        assertEquals(10, state.score)
    }

    @Test
    fun allLevelsCompletedState_hasPositiveLivesAndNotGameOver() {
        val state = McqGameState(
            score = 26,
            lives = 2,
            isAllLevelsCompleted = true,
            isGameOver = false
        )
        assertTrue(state.isAllLevelsCompleted)
        assertFalse(state.isGameOver)
        assertTrue(state.lives > 0)
        assertEquals(26, state.score)
    }

    @Test
    fun mcqQuestion_containsCorrectWordInOptions() {
        val question = McqQuestion(
            definition = "Test definition",
            correctWord = "correct",
            options = listOf("wrong1", "correct", "wrong2", "wrong3")
        )
        assertTrue(question.options.contains(question.correctWord))
        assertEquals(4, question.options.size)
    }

    @Test
    fun mcqQuestion_storesAllProperties() {
        val options = listOf("a", "b", "c", "d")
        val question = McqQuestion("Some definition", "b", options)
        assertEquals("Some definition", question.definition)
        assertEquals("b", question.correctWord)
        assertEquals(options, question.options)
    }

    @Test
    fun mcqGameState_equality() {
        val state1 = McqGameState(bookId = "1", score = 5, lives = 2)
        val state2 = McqGameState(bookId = "1", score = 5, lives = 2)
        assertEquals(state1, state2)
    }

    @Test
    fun mcqGameState_transitionFromActiveToGameOver() {
        val activeState = McqGameState(
            bookId = "book1",
            lives = 1,
            score = 3,
            currentIndex = 5,
            isGameOver = false
        )
        val gameOverState = activeState.copy(lives = 0, isGameOver = true)
        assertFalse(activeState.isGameOver)
        assertTrue(gameOverState.isGameOver)
        assertEquals(0, gameOverState.lives)
        assertEquals(3, gameOverState.score)
    }
}
