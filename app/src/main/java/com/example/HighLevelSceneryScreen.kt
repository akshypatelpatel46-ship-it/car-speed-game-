package com.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
import com.example.ui.theme.SpeedGold

@Composable
fun HighLevelSceneryScreen(
    onSelectAndRide: (HighLevelEnvironment) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedEnv by remember { mutableStateOf(HighLevelEnvironment.CYBERPUNK_NIGHT) }

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
                    text = "🌆 HIGH-LEVEL BACKGROUND SCENERY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NitroGlow
                )
                Text(
                    text = "Cinematic AAA racing backgrounds, lighting atmospheres & parallax environments",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }

        // 1. Full High-Resolution Concept Background Viewer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5f.dp, NitroCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(selectedEnv.title, fontWeight = FontWeight.Black, color = Color.White, fontSize = 16.sp)
                            Text(selectedEnv.description, fontSize = 10.sp, color = NitroGlow)
                        }
                        Icon(Icons.Default.Panorama, contentDescription = "Scenery", tint = NitroCyan)
                    }

                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_highlevel_bg),
                            contentDescription = "Selected Environment",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )

                        // Dynamic Atmosphere Gradient Tint
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            selectedEnv.skyTop.copy(alpha = 0.35f),
                                            selectedEnv.skyBottom.copy(alpha = 0.45f)
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { onSelectAndRide(selectedEnv) },
                        colors = ButtonDefaults.buttonColors(containerColor = NitroCyan),
                        modifier = Modifier.fillMaxWidth().testTag("ride_in_scenery_btn")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black)
                        Spacer(Modifier.width(6.dp))
                        Text("Ride in ${selectedEnv.title}", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // 2. Environment Selection Cards
        item {
            Text("SELECT HIGH-LEVEL TRACK", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HighLevelEnvironment.values().forEach { env ->
                    val isSelected = env == selectedEnv
                    Surface(
                        color = if (isSelected) Color(0xFF1E283D) else CardSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) NitroCyan else CardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedEnv = env }
                            .testTag("env_card_${env.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(env.skyBottom, RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0x55FFFFFF), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌆", fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(env.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text(env.description, fontSize = 10.sp, color = Color.LightGray)
                                }
                            }

                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = NitroGlow, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // 3. PC Desktop Keyboard & Wide Screen Compatibility Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Keyboard, contentDescription = "PC Support", tint = SpeedGold, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PC / DESKTOP OPTIMIZATIONS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SpeedGold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "• Full hardware keyboard support (WASD and Arrow keys)\n" +
                               "• Spacebar / Shift for Dual Nitro Boost\n" +
                               "• [K] or [Ctrl] for Wheelie Stunt physics\n" +
                               "• Press [E] anytime in race to cycle backgrounds\n" +
                               "• Ultra-wide landscape responsive scaling",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
