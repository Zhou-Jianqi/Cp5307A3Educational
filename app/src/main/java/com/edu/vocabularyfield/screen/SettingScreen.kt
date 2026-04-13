package com.edu.vocabularyfield.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.vocabularyfield.R
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel

private data class SeasonOption(val name: String, val drawable: Int)

private val seasons = listOf(
    SeasonOption("Spring", R.drawable.spring_theme_background),
    SeasonOption("Summer", R.drawable.ocean_theme_background),
    SeasonOption("Autumn", R.drawable.autumn_theme_background),
    SeasonOption("Winter", R.drawable.winter_theme_background),
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(viewModel: VocabularyViewModel) {
    val selectedBackground by viewModel.selectedBackground.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Select a level background that you favorite:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(seasons) { season ->
                val isSelected = selectedBackground == season.name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.setSelectedBackground(season.name) }
                ) {
                    Image(
                        painter = painterResource(season.drawable),
                        contentDescription = season.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isSelected)
                                    Modifier.border(3.dp, Color(0xFF2196F3), RoundedCornerShape(12.dp))
                                else
                                    Modifier
                            )
                    )
                    Text(
                        text = season.name,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
