package com.edu.vocabularyfield

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edu.vocabularyfield.data.SavedMcqProgress
import com.edu.vocabularyfield.data.VocabularyBook
import com.edu.vocabularyfield.screen.AddBookScreen
import com.edu.vocabularyfield.screen.McqScreen
import com.edu.vocabularyfield.screen.SettingScreen
import com.edu.vocabularyfield.screen.StatisticsScreen
import com.edu.vocabularyfield.ui.theme.Cp5307A3EducationalAppTheme
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Cp5307A3EducationalAppPreview() {
    Cp5307A3EducationalAppTheme {
        Cp5307A3EducationalAppApp()
    }
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cp5307A3EducationalAppTheme {
                Cp5307A3EducationalAppApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Cp5307A3EducationalAppApp() {
    val viewModel: VocabularyViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToAddBook = { navController.navigate("add_book") },
                onNavigateToMcq = { navController.navigate("mcq") }
            )
        }
        composable("add_book") {
            AddBookScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("mcq") {
            McqScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    viewModel: VocabularyViewModel,
    onNavigateToAddBook: () -> Unit,
    onNavigateToMcq: () -> Unit
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    LaunchedEffect(Unit) {
        viewModel.loadDownloadedBooks()
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen(
                viewModel = viewModel,
                onNavigateToAddBook = onNavigateToAddBook
            )
            AppDestinations.LEARNING -> LearningScreen(
                viewModel = viewModel,
                onNavigateToAddBook = onNavigateToAddBook,
                onNavigateToMcq = onNavigateToMcq
            )
            AppDestinations.SETTING -> SettingScreen(viewModel = viewModel)
            AppDestinations.STATISTICS -> StatisticsScreen(viewModel = viewModel)
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    LEARNING("Learning", R.drawable.ic_learning),
    SETTING("Setting", R.drawable.ic_setting),
    STATISTICS("Statistics", R.drawable.ic_statistics),
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: VocabularyViewModel,
    onNavigateToAddBook: () -> Unit
) {
    val downloadedBooks by viewModel.downloadedBooks.collectAsState()
    var showLimitDialog by remember { mutableStateOf(false) }

    if (showLimitDialog) {
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            title = { Text("Limit Reached") },
            text = { Text("You have reached the maximum of 5 vocabulary books. Cannot add more.") },
            confirmButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Welcome to VocabField!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (downloadedBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.bookshelf_bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Start learning by adding a vocabulary book~",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToAddBook,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF26C6DA)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Start adding", color = Color.White)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(downloadedBooks, key = { it.id }) { book ->
                    SwipeToDeleteBookCard(
                        book = book,
                        onDelete = { viewModel.deleteBook(book.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (viewModel.canAddMore()) {
                                onNavigateToAddBook()
                            } else {
                                showLimitDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF26C6DA)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add Vocabulary Book", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteBookCard(
    book: VocabularyBook,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
        LaunchedEffect(Unit) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE53935), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Delete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        DownloadedBookCard(book)
    }
}

@Composable
fun DownloadedBookCard(book: VocabularyBook) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = book.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.description,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${book.wordCount} words \u00b7 ${book.category}",
                fontSize = 13.sp,
                color = Color(0xFF26C6DA),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LearningBookCard(book: VocabularyBook, progress: SavedMcqProgress?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = book.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = book.description,
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (progress != null) {
                Text(
                    text = "Level ${progress.currentLevel} \u00b7 Score ${progress.score} \u00b7 \u2764\uFE0F ${progress.lives}",
                    fontSize = 13.sp,
                    color = Color(0xFF26C6DA),
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Not started",
                    fontSize = 13.sp,
                    color = Color(0xFF26C6DA),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LearningScreen(
    viewModel: VocabularyViewModel,
    onNavigateToAddBook: () -> Unit,
    onNavigateToMcq: () -> Unit
) {
    val downloadedBooks by viewModel.downloadedBooks.collectAsState()
    val progressMap by viewModel.bookProgressMap.collectAsState()
    var showLimitDialog by remember { mutableStateOf(false) }

    if (showLimitDialog) {
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            title = { Text("Limit Reached") },
            text = { Text("You have reached the maximum of 5 vocabulary books. Cannot add more.") },
            confirmButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Select a vocabulary book from the list below to play\uFF1A",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (downloadedBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.common_background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Start learning by adding a vocabulary book~",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToAddBook,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF26C6DA)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Start adding", color = Color.White)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(downloadedBooks, key = { it.id }) { book ->
                    Box(
                        modifier = Modifier.clickable {
                            viewModel.startMcqGame(book)
                            onNavigateToMcq()
                        }
                    ) {
                        LearningBookCard(book, progressMap[book.id])
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (viewModel.canAddMore()) {
                                onNavigateToAddBook()
                            } else {
                                showLimitDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF26C6DA)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add Vocabulary Book", color = Color.White)
                    }
                }
            }
        }
    }
}
