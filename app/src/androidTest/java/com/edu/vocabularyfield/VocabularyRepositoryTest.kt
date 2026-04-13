package com.edu.vocabularyfield

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.edu.vocabularyfield.data.SavedMcqProgress
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.data.VocabularyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VocabularyRepositoryTest {

    private lateinit var repository: VocabularyRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("vocab_books", Context.MODE_PRIVATE).edit().clear().commit()
        repository = VocabularyRepository(context)
    }

    // --- Book CRUD ---

    @Test
    fun getDownloadedBooks_initiallyEmpty() {
        assertTrue(repository.getDownloadedBooks().isEmpty())
    }

    @Test
    fun saveBook_thenRetrieve() {
        val book = VocabularyBook("1", "Test Book", "Description", 100, "Academic")
        repository.saveBook(book)
        val books = repository.getDownloadedBooks()
        assertEquals(1, books.size)
        assertEquals(book, books[0])
    }

    @Test
    fun saveMultipleBooks_allRetrieved() {
        val book1 = VocabularyBook("1", "Book 1", "Desc 1", 100, "Cat1")
        val book2 = VocabularyBook("2", "Book 2", "Desc 2", 200, "Cat2")
        val book3 = VocabularyBook("3", "Book 3", "Desc 3", 300, "Cat3")
        repository.saveBook(book1)
        repository.saveBook(book2)
        repository.saveBook(book3)
        assertEquals(3, repository.getDownloadedBooks().size)
    }

    @Test
    fun saveBook_duplicatePrevented() {
        val book = VocabularyBook("1", "Book", "Desc", 100, "Cat")
        repository.saveBook(book)
        repository.saveBook(book)
        assertEquals(1, repository.getDownloadedBooks().size)
    }

    @Test
    fun deleteBook_removesTargetBook() {
        val book1 = VocabularyBook("1", "Book 1", "D", 10, "C")
        val book2 = VocabularyBook("2", "Book 2", "D", 10, "C")
        repository.saveBook(book1)
        repository.saveBook(book2)
        repository.deleteBook("1")
        val books = repository.getDownloadedBooks()
        assertEquals(1, books.size)
        assertEquals("2", books[0].id)
    }

    @Test
    fun deleteBook_otherBooksUnaffected() {
        val book1 = VocabularyBook("1", "Book 1", "D", 10, "C")
        val book2 = VocabularyBook("2", "Book 2", "D", 10, "C")
        val book3 = VocabularyBook("3", "Book 3", "D", 10, "C")
        repository.saveBook(book1)
        repository.saveBook(book2)
        repository.saveBook(book3)
        repository.deleteBook("2")
        val books = repository.getDownloadedBooks()
        assertEquals(2, books.size)
        assertTrue(books.any { it.id == "1" })
        assertTrue(books.any { it.id == "3" })
    }

    @Test
    fun getDownloadedBookCount_returnsCorrectCount() {
        assertEquals(0, repository.getDownloadedBookCount())
        repository.saveBook(VocabularyBook("1", "B", "D", 10, "C"))
        assertEquals(1, repository.getDownloadedBookCount())
        repository.saveBook(VocabularyBook("2", "B", "D", 10, "C"))
        assertEquals(2, repository.getDownloadedBookCount())
    }

    // --- MCQ Progress ---

    @Test
    fun saveMcqProgress_thenRetrieve() {
        val progress = SavedMcqProgress(5, 10, 2, listOf(3, 1, 4))
        repository.saveMcqProgress("book1", progress)
        val retrieved = repository.getMcqProgress("book1")
        assertNotNull(retrieved)
        assertEquals(progress, retrieved)
    }

    @Test
    fun getMcqProgress_returnsNullForUnknownBook() {
        assertNull(repository.getMcqProgress("nonexistent"))
    }

    @Test
    fun deleteMcqProgress_removesProgress() {
        repository.saveMcqProgress("book1", SavedMcqProgress(1, 0, 3, listOf(0)))
        repository.deleteMcqProgress("book1")
        assertNull(repository.getMcqProgress("book1"))
    }

    @Test
    fun getAllMcqProgress_returnsAllBookProgress() {
        repository.saveMcqProgress("book1", SavedMcqProgress(1, 0, 3, listOf(0)))
        repository.saveMcqProgress("book2", SavedMcqProgress(5, 10, 2, listOf(1, 2)))
        val allProgress = repository.getAllMcqProgress()
        assertEquals(2, allProgress.size)
        assertNotNull(allProgress["book1"])
        assertNotNull(allProgress["book2"])
    }

    @Test
    fun getAllMcqProgress_initiallyEmpty() {
        assertTrue(repository.getAllMcqProgress().isEmpty())
    }

    @Test
    fun saveMcqProgress_overwritesExistingProgress() {
        repository.saveMcqProgress("book1", SavedMcqProgress(1, 0, 3, listOf(0)))
        repository.saveMcqProgress("book1", SavedMcqProgress(5, 10, 2, listOf(1, 2)))
        val retrieved = repository.getMcqProgress("book1")
        assertEquals(5, retrieved?.currentLevel)
        assertEquals(10, retrieved?.score)
    }

    // --- Background ---

    @Test
    fun getSelectedBackground_defaultIsSummer() {
        assertEquals("Summer", repository.getSelectedBackground())
    }

    @Test
    fun saveSelectedBackground_thenRetrieve() {
        repository.saveSelectedBackground("Spring")
        assertEquals("Spring", repository.getSelectedBackground())
    }

    @Test
    fun saveSelectedBackground_overwritesPrevious() {
        repository.saveSelectedBackground("Spring")
        repository.saveSelectedBackground("Winter")
        assertEquals("Winter", repository.getSelectedBackground())
    }

    // --- Check-in Dates ---

    @Test
    fun getCheckInDates_initiallyEmpty() {
        assertTrue(repository.getCheckInDates().isEmpty())
    }

    @Test
    fun recordCheckIn_addsDate() {
        repository.recordCheckIn("2026-04-13")
        val dates = repository.getCheckInDates()
        assertEquals(1, dates.size)
        assertTrue(dates.contains("2026-04-13"))
    }

    @Test
    fun recordCheckIn_multipleDates() {
        repository.recordCheckIn("2026-04-13")
        repository.recordCheckIn("2026-04-14")
        repository.recordCheckIn("2026-04-15")
        assertEquals(3, repository.getCheckInDates().size)
    }

    @Test
    fun recordCheckIn_duplicateDateNotAdded() {
        repository.recordCheckIn("2026-04-13")
        repository.recordCheckIn("2026-04-13")
        assertEquals(1, repository.getCheckInDates().size)
    }

    // --- Daily Scores ---

    @Test
    fun getDailyScores_initiallyEmpty() {
        assertTrue(repository.getDailyScores().isEmpty())
    }

    @Test
    fun addDailyScore_singleDay() {
        repository.addDailyScore("2026-04-13", 5)
        val scores = repository.getDailyScores()
        assertEquals(1, scores.size)
        assertEquals(5, scores["2026-04-13"])
    }

    @Test
    fun addDailyScore_accumulatesForSameDay() {
        repository.addDailyScore("2026-04-13", 3)
        repository.addDailyScore("2026-04-13", 2)
        repository.addDailyScore("2026-04-13", 1)
        assertEquals(6, repository.getDailyScores()["2026-04-13"])
    }

    @Test
    fun addDailyScore_separateDaysIndependent() {
        repository.addDailyScore("2026-04-13", 5)
        repository.addDailyScore("2026-04-14", 3)
        val scores = repository.getDailyScores()
        assertEquals(2, scores.size)
        assertEquals(5, scores["2026-04-13"])
        assertEquals(3, scores["2026-04-14"])
    }
}
