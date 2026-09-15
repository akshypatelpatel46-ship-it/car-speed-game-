package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NitroCyan
import com.example.ui.theme.NitroGlow
import com.example.ui.theme.RacingRed
import com.example.ui.theme.SpeedGold

data class SpeedChallenge(
    val title: String,
    val description: String,
    val target: String,
    val rewardCoins: Int,
    val progress: Float,
    val isCompleted: Boolean
)

@Composable
fun SpeedRecordsScreen(
    modifier: Modifier = Modifier
) {
    val challenges = listOf(
        SpeedChallenge(
            title = "Speed Demon",
            description = "Reach 220 km/h in an endless highway run",
            target = "220 km/h",
            rewardCoins = 100,
            progress = 1.0f,
            isCompleted = true
        ),
        SpeedChallenge(
            title = "Sonic Hyperdrive",
            description = "Exceed 300 km/h using Nitro Boost",
            target = "300 km/h",
            rewardCoins = 250,
            progress = 0.85f,
            isCompleted = false
        ),
        SpeedChallenge(
            title = "Close Shave Master",
            description = "Perform 10 Near-Miss overtakes in traffic",
            target = "10 Overtakes",
            rewardCoins = 150,
            progress = 0.6f,
            isCompleted = false
        ),
        SpeedChallenge(
            title = "Highway Millionaire",
            description = "Collect 100 Gold Coins on the road",
            target = "100 Coins",
            rewardCoins = 300,
            progress = 0.45f,
            isCompleted = false
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "🏆 SPEED RECORDS & CHALLENGES",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SpeedGold
                )
                Text(
                    text = "High-speed records, milestones, and racing challenges",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }

        // 1. High Speed Hall of Fame Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5f.dp, SpeedGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = "Trophy", tint = SpeedGold, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PERSONAL BEST RECORDS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SpeedGold)
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RecordBox(label = "MAX SPEED", value = "312 km/h", color = NitroGlow)
                        RecordBox(label = "MAX RUN", value = "14.8 km", color = NeonGreen)
                        RecordBox(label = "OVERTAKES", value = "28 Cars", color = SpeedGold)
                    }
                }
            }
        }

        // 2. Speed Challenges List
        item {
            Text("ACTIVE RACING CHALLENGES", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                challenges.forEach { ch ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (ch.isCompleted) NeonGreen.copy(alpha = 0.6f) else CardBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (ch.isCompleted) Icons.Default.EmojiEvents else Icons.Default.LocalFireDepartment,
                                        contentDescription = "Challenge",
                                        tint = if (ch.isCompleted) NeonGreen else RacingRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(ch.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                }

                                Surface(
                                    color = if (ch.isCompleted) NeonGreen.copy(alpha = 0.2f) else Color(0xFF1E283D),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "+${ch.rewardCoins} G",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ch.isCompleted) NeonGreen else GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))
                            Text(ch.description, fontSize = 11.sp, color = Color.LightGray)
                            Spacer(Modifier.height(8.dp))

                            // Progress Bar
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LinearProgressIndicator(
                                    progress = { ch.progress },
                                    color = if (ch.isCompleted) NeonGreen else NitroCyan,
                                    trackColor = Color(0xFF1E2636),
                                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    if (ch.isCompleted) "DONE!" else "${(ch.progress * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ch.isCompleted) NeonGreen else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .background(Color(0xFF0F131C), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
    }
}
