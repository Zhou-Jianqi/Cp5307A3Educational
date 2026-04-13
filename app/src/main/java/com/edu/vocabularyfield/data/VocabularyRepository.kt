package com.edu.vocabularyfield.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class VocabularyRepository(context: Context) {

    private val api = VocabularyApiService.create()
    private val prefs = context.getSharedPreferences("vocab_books", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun searchOnlineVocabularySets(): List<VocabularyBook> {
        return api.searchIeltsVocabularySets()
    }

    suspend fun getWordsForBook(bookId: String): List<VocabularyWord> {
        return api.getWordsForBook(bookId)
    }

    fun getDownloadedBooks(): List<VocabularyBook> {
        val json = prefs.getString("downloaded_books", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<VocabularyBook>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveBook(book: VocabularyBook) {
        val books = getDownloadedBooks().toMutableList()
        if (books.none { it.id == book.id }) {
            books.add(book)
            prefs.edit { putString("downloaded_books", gson.toJson(books)) }
        }
    }

    fun deleteBook(bookId: String) {
        val books = getDownloadedBooks().toMutableList()
        books.removeAll { it.id == bookId }
        prefs.edit { putString("downloaded_books", gson.toJson(books)) }
    }

    fun getDownloadedBookCount(): Int = getDownloadedBooks().size

    // MCQ progress persistence

    fun saveMcqProgress(bookId: String, progress: SavedMcqProgress) {
        val all = getAllMcqProgress().toMutableMap()
        all[bookId] = progress
        prefs.edit { putString("mcq_progress", gson.toJson(all)) }
    }

    fun getMcqProgress(bookId: String): SavedMcqProgress? {
        return getAllMcqProgress()[bookId]
    }

    fun deleteMcqProgress(bookId: String) {
        val all = getAllMcqProgress().toMutableMap()
        all.remove(bookId)
        prefs.edit { putString("mcq_progress", gson.toJson(all)) }
    }

    fun getAllMcqProgress(): Map<String, SavedMcqProgress> {
        val json = prefs.getString("mcq_progress", null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, SavedMcqProgress>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun saveSelectedBackground(background: String) {
        prefs.edit { putString("selected_background", background) }
    }

    fun getSelectedBackground(): String {
        return prefs.getString("selected_background", "Summer") ?: "Summer"
    }

    // Check-in date tracking

    fun recordCheckIn(dateString: String) {
        val dates = getCheckInDates().toMutableSet()
        dates.add(dateString)
        prefs.edit { putString("check_in_dates", gson.toJson(dates)) }
    }

    fun getCheckInDates(): Set<String> {
        val json = prefs.getString("check_in_dates", null) ?: return emptySet()
        return try {
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptySet()
        }
    }

    // Daily score tracking

    fun addDailyScore(dateString: String, points: Int) {
        val scores = getDailyScores().toMutableMap()
        scores[dateString] = (scores[dateString] ?: 0) + points
        prefs.edit { putString("daily_scores", gson.toJson(scores)) }
    }

    fun getDailyScores(): Map<String, Int> {
        val json = prefs.getString("daily_scores", null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, Int>>() {}.type
            gson.fromJson(json, type)
        } catch (_: Exception) {
            emptyMap()
        }
    }
}

data class SavedMcqProgress(
    val currentLevel: Int,
    val score: Int,
    val lives: Int,
    val shuffledWordIndices: List<Int>
)
