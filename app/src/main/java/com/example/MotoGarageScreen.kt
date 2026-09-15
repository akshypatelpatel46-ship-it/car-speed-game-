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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun MotoGarageScreen(
    currentBikeId: String,
    totalCoins: Int,
    onSelectBike: (BikeProfile) -> Unit,
    onCoinsUpdated: (Int) -> Unit,
    onStartRide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bikes = remember { mutableStateListOf(*SuperbikeCatalog.ALL_BIKES.toTypedArray()) }
    var selectedBikeId by remember { mutableStateOf(currentBikeId) }
    val activeBike = bikes.find { it.id == selectedBikeId } ?: bikes.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏍️ SUPERBIKE GARAGE",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NitroCyan
                    )
                    Text(
                        text = "PC & Mobile high-speed motorcycle tuning",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                // Coins Bank
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpeedGold),
                    modifier = Modifier.clickable { onCoinsUpdated(totalCoins + 100) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = SpeedGold, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("$totalCoins G", fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 13.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("+100", fontSize = 10.sp, color = NeonGreen)
                    }
                }
            }
        }

        // 1. High-Level Concept Art Scenery Banner
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
                        Text(
                            text = "HIGH-LEVEL SCENERY: CYBERPUNK METROPOLIS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = NitroGlow,
                            letterSpacing = 1.sp
                        )
                        Icon(Icons.Default.Panorama, contentDescription = "Scenery", tint = NitroGlow, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_highlevel_bg),
                            contentDescription = "High-Level Scenery Background",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // 2. PC Keyboard Controls Quick Reference
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Keyboard, contentDescription = "PC Keyboard", tint = SpeedGold, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PC KEYBOARD CONTROLS (DESKTOP MODE)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SpeedGold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PCKeyInfo("W / ↑", "Full Gas Accelerate")
                        PCKeyInfo("S / ↓", "Rear Brake Decel")
                        PCKeyInfo("A / D", "Knee-Down Lean")
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PCKeyInfo("SPACE", "Dual Nitro Boost")
                        PCKeyInfo("K / CTRL", "Wheelie Stunt!")
                        PCKeyInfo("E", "Switch Background")
                    }
                }
            }
        }

        // 3. Active Superbike Showcase & Stats
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5f.dp, activeBike.bodyColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(activeBike.name, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(activeBike.brandStyle, fontSize = 11.sp, color = NitroGlow)
                        }

                        if (activeBike.id == currentBikeId) {
                            Surface(color = NeonGreen, shape = RoundedCornerShape(6.dp)) {
                                Text("SELECTED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Center Superbike Canvas Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D111A))
                            .border(1.dp, Color(0xFF26354D), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        SuperbikeCanvas(
                            bodyColor = activeBike.bodyColor,
                            accentColor = activeBike.accentColor,
                            suitColor = activeBike.suitColor,
                            isBraking = false,
                            isNitroActive = true,
                            isWheelie = false,
                            leanAngle = 0f,
                            modifier = Modifier.size(65.dp, 120.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    val effectiveSpeed = activeBike.topSpeedKmh + (activeBike.engineUpgrade - 1) * 18
                    val effectiveAccel = (activeBike.acceleration0To100 - (activeBike.nitroUpgrade - 1) * 0.15f).coerceAtLeast(1.4f)

                    // Specs
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Top Speed", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = { (effectiveSpeed / 420f).coerceIn(0f, 1f) },
                                color = NitroCyan,
                                trackColor = Color(0xFF1E2636),
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("$effectiveSpeed km/h", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("0-100 km/h", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = { ((3.5f - effectiveAccel) / 2.2f).coerceIn(0f, 1f) },
                                color = SpeedGold,
                                trackColor = Color(0xFF1E2636),
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("${"%.1f".format(effectiveAccel)}s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Wheelie Stunt", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = { activeBike.wheelieStability },
                                color = NeonGreen,
                                trackColor = Color(0xFF1E2636),
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("${(activeBike.wheelieStability * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (activeBike.isUnlocked) {
                        Button(
                            onClick = {
                                onSelectBike(activeBike)
                                onStartRide()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NitroCyan),
                            modifier = Modifier.fillMaxWidth().testTag("select_bike_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Ride", tint = Color.Black)
                            Spacer(Modifier.width(6.dp))
                            Text("Ride ${activeBike.name}", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (totalCoins >= activeBike.costCoins) {
                                    onCoinsUpdated(totalCoins - activeBike.costCoins)
                                    activeBike.isUnlocked = true
                                    onSelectBike(activeBike)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (totalCoins >= activeBike.costCoins) SpeedGold else Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("unlock_bike_btn")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Text("Unlock for ${activeBike.costCoins} Coins", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // 4. Performance Tuning
        if (activeBike.isUnlocked) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = "Tune", tint = SpeedGold, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("SUPERBIKE PERFORMANCE TUNING", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SpeedGold)
                        }
                        Spacer(Modifier.height(10.dp))

                        val engCost = activeBike.engineUpgrade * 50
                        UpgradeRow(
                            title = "Titanium Exhaust & ECU (Lv.${activeBike.engineUpgrade})",
                            bonusText = "+18 km/h Top Speed",
                            cost = engCost,
                            canAfford = totalCoins >= engCost,
                            onUpgrade = {
                                if (totalCoins >= engCost) {
                                    onCoinsUpdated(totalCoins - engCost)
                                    activeBike.engineUpgrade++
                                    onSelectBike(activeBike)
                                }
                            }
                        )

                        Spacer(Modifier.height(8.dp))

                        val nosCost = activeBike.nitroUpgrade * 50
                        UpgradeRow(
                            title = "High-Pressure NOS Injector (Lv.${activeBike.nitroUpgrade})",
                            bonusText = "+30% Nitro Duration",
                            cost = nosCost,
                            canAfford = totalCoins >= nosCost,
                            onUpgrade = {
                                if (totalCoins >= nosCost) {
                                    onCoinsUpdated(totalCoins - nosCost)
                                    activeBike.nitroUpgrade++
                                    onSelectBike(activeBike)
                                }
                            }
                        )
                    }
                }
            }
        }

        // 5. Bike Catalog
        item {
            Text("SUPERBIKE FLEET", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray)
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                bikes.forEach { bike ->
                    val isSelected = bike.id == selectedBikeId
                    Surface(
                        color = if (isSelected) Color(0xFF1E283D) else CardSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) NitroCyan else CardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBikeId = bike.id }
                            .testTag("bike_card_${bike.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(bike.bodyColor, RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏍️", fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(bike.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("${bike.topSpeedKmh} km/h • ${bike.brandStyle}", fontSize = 10.sp, color = Color.LightGray)
                                }
                            }

                            if (!bike.isUnlocked) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = SpeedGold, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("${bike.costCoins} G", fontSize = 12.sp, color = SpeedGold, fontWeight = FontWeight.Bold)
                                }
                            } else if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = NitroGlow, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PCKeyInfo(key: String, label: String) {
    Column(
        modifier = Modifier
            .background(Color(0xFF0F131C), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(key, fontSize = 11.sp, fontWeight = FontWeight.Black, color = NitroGlow)
        Text(label, fontSize = 9.sp, color = Color.LightGray)
    }
}
