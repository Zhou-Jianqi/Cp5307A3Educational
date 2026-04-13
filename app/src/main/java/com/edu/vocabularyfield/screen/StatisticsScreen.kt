package com.edu.vocabularyfield.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.vocabularyfield.viewmodel.VocabularyViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(viewModel: VocabularyViewModel) {
    val checkInDates by viewModel.checkInDates.collectAsState()
    val dailyScores by viewModel.dailyScores.collectAsState()
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Your consecutive days of\nmemorizing words:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        CalendarView(
            yearMonth = displayedMonth,
            checkInDates = checkInDates,
            onPreviousMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your stars:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScoreLineChart(
            dailyScores = dailyScores,
            yearMonth = displayedMonth
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarView(
    yearMonth: YearMonth,
    checkInDates: Set<String>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek
    // Sunday = 0, Monday = 1, ..., Saturday = 6
    val startOffset = if (firstDayOfWeek == DayOfWeek.SUNDAY) 0 else firstDayOfWeek.value
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    val today = LocalDate.now()
    val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Month/Year header with navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Text(text = "<", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Text(
                text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${yearMonth.year}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            IconButton(onClick = onNextMonth) {
                Text(text = ">", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startOffset + 1

                    if (day in 1..daysInMonth) {
                        val date = yearMonth.atDay(day)
                        val dateStr = date.format(formatter)
                        val isCheckedIn = checkInDates.contains(dateStr)
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCheckedIn) {
                                PencilCircle(
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Text(
                                text = day.toString(),
                                fontSize = 16.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) Color(0xFF26C6DA) else Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ScoreLineChart(
    dailyScores: Map<String, Int>,
    yearMonth: YearMonth
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    // Collect scores for each day of the displayed month
    val scores = (1..daysInMonth).map { day ->
        val dateStr = yearMonth.atDay(day).format(formatter)
        dailyScores[dateStr] ?: 0
    }

    val maxScore = (scores.maxOrNull() ?: 0).coerceAtLeast(1)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val leftPadding = 40.dp.toPx()
        val bottomPadding = 32.dp.toPx()
        val topPadding = 12.dp.toPx()
        val rightPadding = 12.dp.toPx()

        val chartWidth = size.width - leftPadding - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding

        val axisColor = Color.Gray
        val lineColor = Color(0xFF26C6DA)
        val dotColor = Color(0xFF26C6DA)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        val yTextPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 10.sp.toPx()
            textAlign = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }

        // Draw Y axis
        drawLine(
            color = axisColor,
            start = Offset(leftPadding, topPadding),
            end = Offset(leftPadding, topPadding + chartHeight),
            strokeWidth = 1.dp.toPx()
        )

        // Draw X axis
        drawLine(
            color = axisColor,
            start = Offset(leftPadding, topPadding + chartHeight),
            end = Offset(leftPadding + chartWidth, topPadding + chartHeight),
            strokeWidth = 1.dp.toPx()
        )

        // Draw Y axis labels and grid lines
        val ySteps = 4
        for (i in 0..ySteps) {
            val value = (maxScore.toFloat() / ySteps * i).toInt()
            val y = topPadding + chartHeight - (chartHeight * i / ySteps)

            // Grid line
            if (i > 0) {
                drawLine(
                    color = Color(0xFFEEEEEE),
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Y label
            drawContext.canvas.nativeCanvas.drawText(
                value.toString(),
                leftPadding - 8.dp.toPx(),
                y + 4.dp.toPx(),
                yTextPaint
            )
        }

        // Draw X axis labels (show subset to avoid crowding)
        val labelInterval = when {
            daysInMonth <= 10 -> 1
            daysInMonth <= 20 -> 2
            else -> 5
        }
        for (day in 1..daysInMonth) {
            if (day == 1 || day == daysInMonth || day % labelInterval == 0) {
                val x = leftPadding + chartWidth * (day - 1).toFloat() / (daysInMonth - 1).coerceAtLeast(1)
                drawContext.canvas.nativeCanvas.drawText(
                    day.toString(),
                    x,
                    topPadding + chartHeight + 20.dp.toPx(),
                    textPaint
                )
            }
        }

        // Draw line chart
        if (daysInMonth >= 2) {
            val dataPoints = scores.mapIndexed { index, score ->
                val x = leftPadding + chartWidth * index.toFloat() / (daysInMonth - 1)
                val y = topPadding + chartHeight - (chartHeight * score.toFloat() / maxScore)
                Offset(x, y)
            }

            // Draw the line path
            val linePath = Path()
            dataPoints.forEachIndexed { index, point ->
                if (index == 0) {
                    linePath.moveTo(point.x, point.y)
                } else {
                    linePath.lineTo(point.x, point.y)
                }
            }
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw dots on days that have scores > 0
            dataPoints.forEachIndexed { index, point ->
                if (scores[index] > 0) {
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = dotColor,
                        radius = 4.dp.toPx(),
                        center = point,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
private fun PencilCircle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = size.minDimension / 2f - 4.dp.toPx()

        // Draw a hand-drawn style red pencil circle using a slightly wobbly path
        val path = Path()
        val points = 60
        for (i in 0..points) {
            val angle = (i.toFloat() / points) * 2f * Math.PI.toFloat()
            // Add slight wobble for pencil-drawn effect
            val wobble = if (i % 3 == 0) 1.5f else if (i % 3 == 1) -1f else 0.5f
            val r = radius + wobble
            val x = centerX + r * cos(angle)
            val y = centerY + r * sin(angle)
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        drawPath(
            path = path,
            color = Color(0xFFE53935),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
