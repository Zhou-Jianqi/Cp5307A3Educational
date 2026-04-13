package com.edu.vocabularyfield.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.edu.vocabularyfield.data.SavedMcqProgress
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.data.VocabularyRepository
import com.edu.vocabularyfield.data.VocabularyWord
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

data class McqQuestion(
    val definition: String,
    val correctWord: String,
    val options: List<String>
)

data class McqGameState(
    val bookId: String = "",
    val bookName: String = "",
    val questions: List<McqQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val currentLevel: Int = 1,
    val score: Int = 0,
    val lives: Int = 3,
    val isGameOver: Boolean = false,
    val isAllLevelsCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val shuffledWordIndices: List<Int> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
class VocabularyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VocabularyRepository(application)

    private val _onlineBooks = MutableStateFlow<List<VocabularyBook>>(emptyList())
    val onlineBooks: StateFlow<List<VocabularyBook>> = _onlineBooks

    private val _downloadedBooks = MutableStateFlow<List<VocabularyBook>>(emptyList())
    val downloadedBooks: StateFlow<List<VocabularyBook>> = _downloadedBooks

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _downloadingIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingIds: StateFlow<Set<String>> = _downloadingIds

    private val _bookProgressMap = MutableStateFlow<Map<String, SavedMcqProgress>>(emptyMap())
    val bookProgressMap: StateFlow<Map<String, SavedMcqProgress>> = _bookProgressMap

    private val _checkInDates = MutableStateFlow<Set<String>>(emptySet())
    val checkInDates: StateFlow<Set<String>> = _checkInDates

    private val _dailyScores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val dailyScores: StateFlow<Map<String, Int>> = _dailyScores

    init {
        loadDownloadedBooks()
        loadAllMcqProgress()
        recordTodayCheckIn()
        loadDailyScores()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun recordTodayCheckIn() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        repository.recordCheckIn(today)
        _checkInDates.value = repository.getCheckInDates()
    }

    private fun loadDailyScores() {
        _dailyScores.value = repository.getDailyScores()
    }

    fun searchOnlineBooks() {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                _onlineBooks.value = repository.searchOnlineVocabularySets()
            } catch (_: Exception) {
                _onlineBooks.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun downloadBook(book: VocabularyBook): Boolean {
        if (!canAddMore()) return false
        if (isBookDownloaded(book.id)) return true

        viewModelScope.launch {
            _downloadingIds.value += book.id
            try {
                delay(1500.milliseconds)
                repository.saveBook(book)
                loadDownloadedBooks()
            } finally {
                _downloadingIds.value -= book.id
            }
        }
        return true
    }

    fun loadDownloadedBooks() {
        _downloadedBooks.value = repository.getDownloadedBooks()
    }

    fun isBookDownloaded(bookId: String): Boolean {
        return _downloadedBooks.value.any { it.id == bookId }
    }

    fun deleteBook(bookId: String) {
        repository.deleteBook(bookId)
        repository.deleteMcqProgress(bookId)
        loadDownloadedBooks()
        loadAllMcqProgress()
    }

    fun canAddMore(): Boolean = _downloadedBooks.value.size < 5

    companion object {
        val FIXED_WORDS = listOf(
            VocabularyWord("advocate", "To publicly recommend or support a particular cause or policy"),
            VocabularyWord("beneficial", "Resulting in good; favorable or advantageous"),
            VocabularyWord("confront", "To face up to and deal with a problem or difficult situation"),
            VocabularyWord("detrimental", "Tending to cause harm or damage"),
            VocabularyWord("eliminate", "To completely remove or get rid of something"),
            VocabularyWord("fluctuate", "To rise and fall irregularly in number or amount"),
            VocabularyWord("guarantee", "A formal promise or assurance that certain conditions will be fulfilled"),
            VocabularyWord("hierarchy", "A system in which members are ranked according to relative status or authority"),
            VocabularyWord("inevitable", "Certain to happen; unavoidable"),
            VocabularyWord("justify", "To show or prove to be right or reasonable"),
            VocabularyWord("keynote", "The prevailing tone or central theme, typically one set or introduced at the start"),
            VocabularyWord("legitimate", "Conforming to the law or to rules; justifiable"),
            VocabularyWord("mitigate", "To make less severe, serious, or painful"),
            VocabularyWord("notion", "A conception of or belief about something"),
            VocabularyWord("optimize", "To make the best or most effective use of a situation or resource"),
            VocabularyWord("prevalent", "Widespread in a particular area or at a particular time"),
            VocabularyWord("quota", "A limited or fixed number or amount of people or things"),
            VocabularyWord("reinforce", "To strengthen or support, especially with additional material"),
            VocabularyWord("substantial", "Of considerable importance, size, or worth"),
            VocabularyWord("threshold", "The magnitude or intensity that must be exceeded for a certain reaction or result"),
            VocabularyWord("utilize", "To make practical and effective use of something"),
            VocabularyWord("vulnerable", "Susceptible to physical or emotional attack or harm"),
            VocabularyWord("warrant", "To justify or necessitate a certain course of action"),
            VocabularyWord("xenophobia", "Dislike of or prejudice against people from other countries"),
            VocabularyWord("yield", "To produce or provide a natural, agricultural, or industrial product"),
            VocabularyWord("zeal", "Great energy or enthusiasm in pursuit of a cause or an objective"),
        )
    }

    private val _mcqGameState = MutableStateFlow(McqGameState())
    val mcqGameState: StateFlow<McqGameState> = _mcqGameState

    private val _selectedBackground = MutableStateFlow(repository.getSelectedBackground())
    val selectedBackground: StateFlow<String> = _selectedBackground

    fun setSelectedBackground(background: String) {
        _selectedBackground.value = background
        repository.saveSelectedBackground(background)
    }

    private fun loadAllMcqProgress() {
        _bookProgressMap.value = repository.getAllMcqProgress()
    }

    fun startMcqGame(book: VocabularyBook) {
        val saved = repository.getMcqProgress(book.id)
        if (saved != null) {
            val questions = generateQuestionsFromIndices(saved.shuffledWordIndices)
            _mcqGameState.value = McqGameState(
                bookId = book.id,
                bookName = book.name,
                questions = questions,
                currentIndex = saved.currentLevel - 1,
                currentLevel = saved.currentLevel,
                score = saved.score,
                lives = saved.lives,
                shuffledWordIndices = saved.shuffledWordIndices
            )
        } else {
            val indices = FIXED_WORDS.indices.shuffled()
            val questions = generateQuestionsFromIndices(indices)
            _mcqGameState.value = McqGameState(
                bookId = book.id,
                bookName = book.name,
                questions = questions,
                currentLevel = 1,
                score = 0,
                lives = 3,
                shuffledWordIndices = indices
            )
        }
    }

    private fun generateQuestionsFromIndices(indices: List<Int>): List<McqQuestion> {
        val allWords = FIXED_WORDS.map { it.word }
        return indices.map { idx ->
            val word = FIXED_WORDS[idx]
            val wrongOptions = (allWords - word.word).shuffled().take(3)
            val options = (wrongOptions + word.word).shuffled()
            McqQuestion(
                definition = word.definition,
                correctWord = word.word,
                options = options
            )
        }
    }

    fun saveAndExitMcqGame() {
        val state = _mcqGameState.value
        if (state.bookId.isEmpty()) return
        repository.saveMcqProgress(
            state.bookId,
            SavedMcqProgress(
                currentLevel = state.currentLevel,
                score = state.score,
                lives = state.lives,
                shuffledWordIndices = state.shuffledWordIndices
            )
        )
        loadAllMcqProgress()
    }

    fun answerMcqQuestion(selectedWord: String) {
        val state = _mcqGameState.value
        if (state.isGameOver || state.isAllLevelsCompleted || state.currentIndex >= state.questions.size) return

        val question = state.questions[state.currentIndex]
        val isCorrect = selectedWord == question.correctWord
        val newScore = if (isCorrect) state.score + 1 else state.score
        val newLives = if (isCorrect) state.lives else state.lives - 1

        if (isCorrect) {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            repository.addDailyScore(today, 1)
            loadDailyScores()
        }
        val nextIndex = state.currentIndex + 1
        val allCompleted = nextIndex >= state.questions.size && newLives > 0
        val gameOver = newLives <= 0

        _mcqGameState.value = state.copy(
            score = newScore,
            lives = newLives,
            currentIndex = if (gameOver || allCompleted) state.currentIndex else nextIndex,
            currentLevel = if (gameOver || allCompleted) state.currentLevel else state.currentLevel + 1,
            isGameOver = gameOver,
            isAllLevelsCompleted = allCompleted
        )

        if (gameOver) {
            repository.deleteMcqProgress(state.bookId)
            loadAllMcqProgress()
        }
    }
}
