package com.edu.vocabularyfield

import com.edu.vocabularyfield.data.SavedMcqProgress
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.data.VocabularyWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class DataModelTest {

    // --- VocabularyWord ---

    @Test
    fun vocabularyWord_storesWordAndDefinition() {
        val word = VocabularyWord("advocate", "To publicly recommend or support")
        assertEquals("advocate", word.word)
        assertEquals("To publicly recommend or support", word.definition)
    }

    @Test
    fun vocabularyWord_equalityForSameData() {
        val word1 = VocabularyWord("test", "definition")
        val word2 = VocabularyWord("test", "definition")
        assertEquals(word1, word2)
    }

    @Test
    fun vocabularyWord_inequalityForDifferentDefinition() {
        val word1 = VocabularyWord("test", "definition 1")
        val word2 = VocabularyWord("test", "definition 2")
        assertNotEquals(word1, word2)
    }

    @Test
    fun vocabularyWord_inequalityForDifferentWord() {
        val word1 = VocabularyWord("word1", "same definition")
        val word2 = VocabularyWord("word2", "same definition")
        assertNotEquals(word1, word2)
    }

    @Test
    fun vocabularyWord_copyWithModifiedField() {
        val original = VocabularyWord("test", "original")
        val modified = original.copy(definition = "updated")
        assertEquals("test", modified.word)
        assertEquals("updated", modified.definition)
    }

    // --- VocabularyBook ---

    @Test
    fun vocabularyBook_storesAllProperties() {
        val book = VocabularyBook("1", "IELTS Academic", "Core vocab", 570, "Academic")
        assertEquals("1", book.id)
        assertEquals("IELTS Academic", book.name)
        assertEquals("Core vocab", book.description)
        assertEquals(570, book.wordCount)
        assertEquals("Academic", book.category)
    }

    @Test
    fun vocabularyBook_equalityForSameData() {
        val book1 = VocabularyBook("1", "Name", "Desc", 10, "Cat")
        val book2 = VocabularyBook("1", "Name", "Desc", 10, "Cat")
        assertEquals(book1, book2)
    }

    @Test
    fun vocabularyBook_inequalityForDifferentId() {
        val book1 = VocabularyBook("1", "Name", "Desc", 10, "Cat")
        val book2 = VocabularyBook("2", "Name", "Desc", 10, "Cat")
        assertNotEquals(book1, book2)
    }

    // --- SavedMcqProgress ---

    @Test
    fun savedMcqProgress_storesAllProperties() {
        val indices = listOf(3, 1, 4, 1, 5)
        val progress = SavedMcqProgress(
            currentLevel = 5,
            score = 10,
            lives = 2,
            shuffledWordIndices = indices
        )
        assertEquals(5, progress.currentLevel)
        assertEquals(10, progress.score)
        assertEquals(2, progress.lives)
        assertEquals(indices, progress.shuffledWordIndices)
    }

    @Test
    fun savedMcqProgress_equalityForSameData() {
        val p1 = SavedMcqProgress(1, 0, 3, listOf(0, 1, 2))
        val p2 = SavedMcqProgress(1, 0, 3, listOf(0, 1, 2))
        assertEquals(p1, p2)
    }

    @Test
    fun savedMcqProgress_inequalityForDifferentScore() {
        val p1 = SavedMcqProgress(1, 5, 3, listOf(0, 1, 2))
        val p2 = SavedMcqProgress(1, 10, 3, listOf(0, 1, 2))
        assertNotEquals(p1, p2)
    }

    @Test
    fun savedMcqProgress_inequalityForDifferentIndices() {
        val p1 = SavedMcqProgress(1, 0, 3, listOf(0, 1, 2))
        val p2 = SavedMcqProgress(1, 0, 3, listOf(2, 1, 0))
        assertNotEquals(p1, p2)
    }

    @Test
    fun savedMcqProgress_copyWithModifiedField() {
        val original = SavedMcqProgress(1, 0, 3, listOf(0, 1, 2))
        val modified = original.copy(score = 5, lives = 2)
        assertEquals(5, modified.score)
        assertEquals(2, modified.lives)
        assertEquals(1, modified.currentLevel)
        assertEquals(listOf(0, 1, 2), modified.shuffledWordIndices)
    }
}
