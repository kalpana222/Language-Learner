package com.example.languagelearner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.languagelearner.navigation.Screen
import com.example.languagelearner.data.AppState

data class SituationOption(
    val id: String,
    val title: String,
    val description: String,
    val exerciseCount: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SituationSelectionScreen(navController: NavController) {
    val situations = remember {
        listOf(
            SituationOption(
                id = "cafe",
                title = "At a Café & Restaurant",
                description = "Order food, ask about menu items, and request the bill.",
                exerciseCount = "AI Roleplay",
                emoji = "☕"
            ),
            SituationOption(
                id = "travel",
                title = "Travel & Airport",
                description = "Check in for flights, ask for directions, and navigate terminals.",
                exerciseCount = "AI Roleplay",
                emoji = "✈️"
            ),
            SituationOption(
                id = "shopping",
                title = "Shopping & Market",
                description = "Ask prices, request different sizes, and make payments.",
                exerciseCount = "AI Roleplay",
                emoji = "🛍️"
            ),
            SituationOption(
                id = "greetings",
                title = "Meeting People & Small Talk",
                description = "Introduce yourself, talk about hobbies, and daily life.",
                exerciseCount = "AI Roleplay",
                emoji = "🤝"
            ),
            SituationOption(
                id = "hotel",
                title = "Hotel & Lodging",
                description = "Check in, ask for room amenities, and request room service.",
                exerciseCount = "AI Roleplay",
                emoji = "🏨"
            ),
            SituationOption(
                id = "transit",
                title = "Taxi & Public Transit",
                description = "Give directions to a driver, take a taxi, and buy transit tickets.",
                exerciseCount = "AI Roleplay",
                emoji = "🚕"
            ),
            SituationOption(
                id = "phone",
                title = "Phone Calls & Reservations",
                description = "Book restaurant tables, schedule appointments, and ask inquiries.",
                exerciseCount = "AI Roleplay",
                emoji = "📱"
            ),
            SituationOption(
                id = "hobbies",
                title = "Hobbies & Weekend Fun",
                description = "Discuss favorite movies, sports, music, and weekend plans.",
                exerciseCount = "AI Roleplay",
                emoji = "🎬"
            ),
            SituationOption(
                id = "health",
                title = "Pharmacy & Emergencies",
                description = "Explain common symptoms and ask for assistance or medicine.",
                exerciseCount = "AI Roleplay",
                emoji = "🏥"
            ),
            SituationOption(
                id = "work",
                title = "Work & Business",
                description = "Polite workplace requests, scheduling, and meetings.",
                exerciseCount = "AI Roleplay",
                emoji = "💼"
            ),
            SituationOption(
                id = "bank",
                title = "Bank & Currency Exchange",
                description = "Inquire about foreign currency exchange, ATM help, and cards.",
                exerciseCount = "AI Roleplay",
                emoji = "🏦"
            ),
            SituationOption(
                id = "school",
                title = "School & Campus Life",
                description = "Chat with classmates, ask study questions, and campus talk.",
                exerciseCount = "AI Roleplay",
                emoji = "🎓"
            )
        )
    }

    var selectedSituationId by remember { mutableStateOf("cafe") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Choose Situation",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp)
            ) {
                Button(
                    onClick = {
                        val selected = situations.firstOrNull { it.id == selectedSituationId }
                        if (selected != null) {
                            AppState.selectedSituationTitle = selected.title
                            AppState.selectedSituationEmoji = selected.emoji
                        }
                        navController.navigate(Screen.SpeakingPractice.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Start Speaking Practice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Select a Practice Scenario",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Practice speaking phrases you will use in real daily conversations.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(situations) { situation ->
                    val isSelected = situation.id == selectedSituationId

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedSituationId = situation.id }
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                } else Modifier
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = situation.emoji,
                                    fontSize = 32.sp
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = situation.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = situation.description,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 17.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "⚡ ${situation.exerciseCount}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedSituationId = situation.id }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}