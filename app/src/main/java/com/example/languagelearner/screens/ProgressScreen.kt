package com.example.languagelearner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.languagelearner.data.AppState
import com.example.languagelearner.data.PracticeRecord
import com.example.languagelearner.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DayActivity(
    val dayName: String,
    val count: Int,
    val isFuture: Boolean,
    val isToday: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "guest_user"

    var records by remember { mutableStateOf<List<PracticeRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch saved sessions from Firestore
    LaunchedEffect(userId) {
        isLoading = true
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("practice_records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    PracticeRecord(
                        id = doc.id,
                        languageName = doc.getString("languageName") ?: "",
                        situationTitle = doc.getString("situationTitle") ?: "",
                        levelTitle = doc.getString("levelTitle") ?: "",
                        fluencyScore = doc.getLong("fluencyScore")?.toInt() ?: 0,
                        spokenText = doc.getString("spokenText") ?: "",
                        grammarFeedback = doc.getString("grammarFeedback") ?: "",
                        nativeSuggestion = doc.getString("nativeSuggestion") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                records = list
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    // Dynamic stats derived from real Firestore records
    val avgScore = if (records.isNotEmpty()) {
        (records.map { it.fluencyScore }.average()).toInt()
    } else {
        AppState.lastAccuracyScore
    }
    val totalPractices = records.size
    val activeLanguages = records.map { it.languageName }.distinct()
    val activeLanguagesCount = if (activeLanguages.isNotEmpty()) activeLanguages.size else 1
    val estimatedMinutes = (records.size * 2).coerceAtLeast(if (records.isNotEmpty()) 2 else 0)

    // Calculate real weekly activity based on calendar dates
    val weeklyActivity = remember(records) { calculateWeeklyActivity(records) }
    val thisWeekCount = weeklyActivity.filter { !it.isFuture }.sumOf { it.count }

    // Fluency tier calculation
    val (tierEmoji, tierTitle, tierSubtitle) = when {
        avgScore >= 80 -> Triple("🥇", "Gold Fluent Speaker", "Outstanding conversational naturalness!")
        avgScore >= 50 -> Triple("🥈", "Silver Conversationalist", "Good flow! Practice daily to reach Gold tier.")
        else -> Triple("🥉", "Bronze Starter", "Keep speaking out loud to build fluency & confidence!")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Progress",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                // 1. Fluency Mastery Rank Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = tierEmoji, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = tierTitle,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = tierSubtitle,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar toward 100%
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fluency Level",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$avgScore% / 100%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { (avgScore / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. 4 Stat Metric Cards (2x2 grid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Avg Fluency",
                        value = "$avgScore%",
                        emoji = "🎯",
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Sessions Done",
                        value = "$totalPractices",
                        emoji = "🎙️",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Languages",
                        value = "$activeLanguagesCount Active",
                        emoji = "🌐",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Practice Time",
                        value = "$estimatedMinutes mins",
                        emoji = "⏱️",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Weekly Activity Section (Calculated with real dates)
            Text(
                text = "Weekly Activity",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (thisWeekCount > 0) "$thisWeekCount practice session(s) logged this week" else "No practice sessions logged yet this week",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyActivity.forEach { activity ->
                            DayBar(
                                day = activity.dayName,
                                count = activity.count,
                                isFuture = activity.isFuture,
                                isToday = activity.isToday
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Language Fluency Breakdown
            if (records.isNotEmpty()) {
                Text(
                    text = "Language Breakdown",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                records.groupBy { it.languageName }.forEach { (langName, langRecords) ->
                    val langAvg = langRecords.map { it.fluencyScore }.average().toInt()
                    val langFlag = getLanguageFlag(langName)
                    LanguageBreakdownCard(
                        language = "$langFlag $langName",
                        sessionsCount = langRecords.size,
                        avgScore = langAvg
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // 5. Recent Practice History Section
            Text(
                text = "Recent Practice History",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (records.isEmpty() && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No saved practice sessions yet.\nComplete a speaking practice to record progress!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                records.take(10).forEach { record ->
                    EnhancedPracticeRecordCard(
                        record = record,
                        formattedDate = if (record.timestamp > 0) dateFormat.format(Date(record.timestamp)) else "Recent"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun calculateWeeklyActivity(records: List<PracticeRecord>): List<DayActivity> {
    val cal = Calendar.getInstance()
    val todayYear = cal.get(Calendar.YEAR)
    val todayDayOfYear = cal.get(Calendar.DAY_OF_YEAR)

    // Calculate days since Monday (0 for Mon, 1 for Tue, ..., 6 for Sun)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = (dayOfWeek + 5) % 7

    // Set to Monday 00:00:00 of this current week
    cal.add(Calendar.DAY_OF_MONTH, -daysFromMonday)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val result = mutableListOf<DayActivity>()

    for (i in 0 until 7) {
        val dayStart = cal.timeInMillis
        val dayYear = cal.get(Calendar.YEAR)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        cal.add(Calendar.DAY_OF_MONTH, 1)
        val dayEnd = cal.timeInMillis

        val count = records.count { it.timestamp in dayStart until dayEnd }
        val isToday = (dayYear == todayYear && dayOfYear == todayDayOfYear)
        val isFuture = (dayYear > todayYear || (dayYear == todayYear && dayOfYear > todayDayOfYear))

        result.add(DayActivity(dayNames[i], count, isFuture, isToday))
    }
    return result
}

@Composable
fun DayBar(day: String, count: Int, isFuture: Boolean, isToday: Boolean) {
    val progress = when {
        isFuture -> 0f
        count == 0 -> 0f
        count == 1 -> 0.45f
        count == 2 -> 0.75f
        else -> 1f
    }
    val isDone = count > 0 && !isFuture

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isDone) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height((60 * progress).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day,
            fontSize = 11.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isToday -> MaterialTheme.colorScheme.primary
                isFuture -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                isDone -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun LanguageBreakdownCard(language: String, sessionsCount: Int, avgScore: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$avgScore% • $sessionsCount session(s)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (avgScore / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun EnhancedPracticeRecordCard(record: PracticeRecord, formattedDate: String) {
    val (badgeBg, badgeText) = when {
        record.fluencyScore >= 75 -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32)) // Green
        record.fluencyScore >= 50 -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100)) // Amber / Orange
        else -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))                      // Blue
    }

    val flag = getLanguageFlag(record.languageName)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$flag ${record.languageName} • ${record.levelTitle}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${record.fluencyScore}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeText
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = record.situationTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (record.spokenText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🗣️ “${record.spokenText}”",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            if (record.nativeSuggestion.isNotBlank() && record.nativeSuggestion != "Error") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✨ Native: “${record.nativeSuggestion}”",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formattedDate,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

fun getLanguageFlag(language: String): String {
    return when {
        language.contains("Hindi", ignoreCase = true) || language.contains("Punjabi", ignoreCase = true) -> "🇮🇳"
        language.contains("Spanish", ignoreCase = true) -> "🇪🇸"
        language.contains("French", ignoreCase = true) -> "🇫🇷"
        language.contains("German", ignoreCase = true) -> "🇩🇪"
        language.contains("Italian", ignoreCase = true) -> "🇮🇹"
        language.contains("Japanese", ignoreCase = true) -> "🇯🇵"
        language.contains("Korean", ignoreCase = true) -> "🇰🇷"
        language.contains("Chinese", ignoreCase = true) || language.contains("Mandarin", ignoreCase = true) -> "🇨🇳"
        language.contains("Portuguese", ignoreCase = true) -> "🇵🇹"
        language.contains("Russian", ignoreCase = true) -> "🇷🇺"
        else -> "🇬🇧"
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    emoji: String,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}