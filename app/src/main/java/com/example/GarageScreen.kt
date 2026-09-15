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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.RacingRed
import com.example.ui.theme.SpeedGold

@Composable
fun GarageScreen(
    currentCarId: String,
    totalCoins: Int,
    onSelectCar: (CarProfile) -> Unit,
    onCoinsUpdated: (Int) -> Unit,
    onStartRace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val carsList = remember { mutableStateListOf(*CarCatalog.ALL_CARS.toTypedArray()) }
    var selectedCarId by remember { mutableStateOf(currentCarId) }
    val activeCar = carsList.find { it.id == selectedCarId } ?: carsList.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏎️ SPEED GARAGE",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = SpeedGold
                    )
                    Text(
                        text = "Upgrade your supercars & tune engine speed",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                // Coins Bank Card
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpeedGold),
                    modifier = Modifier.clickable {
                        onCoinsUpdated(totalCoins + 100)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = SpeedGold, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("$totalCoins G", fontWeight = FontWeight.Bold, color = GoldLight, fontSize = 13.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("+100", fontSize = 10.sp, color = NeonGreen)
                    }
                }
            }
        }

        // 1. Garage Banner Image
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_car_banner),
                        contentDescription = "Racing Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // 2. Car Showcase Viewer Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.5f.dp, NitroCyan),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeCar.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = activeCar.typeName,
                                fontSize = 11.sp,
                                color = NitroGlow
                            )
                        }

                        if (activeCar.id == currentCarId) {
                            Surface(
                                color = NeonGreen,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "SELECTED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Center High-Detail Car Canvas Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F131C))
                            .border(1.dp, Color(0xFF26354D), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CarCanvas(
                            bodyColor = activeCar.bodyColor,
                            accentColor = activeCar.accentColor,
                            isBraking = false,
                            isNitroActive = true,
                            steerTilt = 0f,
                            modifier = Modifier.size(75.dp, 130.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // Car Specs (Top Speed, Acceleration, Handling)
                    val effectiveSpeed = activeCar.topSpeedKmh + (activeCar.engineUpgrade - 1) * 15
                    val effectiveAccel = (activeCar.acceleration0To100 - (activeCar.turboUpgrade - 1) * 0.2f).coerceAtLeast(1.5f)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Top Speed Spec
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Top Speed", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = { (effectiveSpeed / 360f).coerceIn(0f, 1f) },
                                color = SpeedGold,
                                trackColor = Color(0xFF232A38),
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("$effectiveSpeed km/h", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Acceleration Spec
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("0-100 km/h", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = { ((4f - effectiveAccel) / 2.5f).coerceIn(0f, 1f) },
                                color = NitroCyan,
                                trackColor = Color(0xFF232A38),
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("${"%.1f".format(effectiveAccel)}s", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Handling Spec
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Handling", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = { activeCar.handling },
                                color = NeonGreen,
                                trackColor = Color(0xFF232A38),
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("${(activeCar.handling * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Select or Unlock Car Button
                    if (activeCar.isUnlocked) {
                        Button(
                            onClick = {
                                onSelectCar(activeCar)
                                onStartRace()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NitroCyan),
                            modifier = Modifier.fillMaxWidth().testTag("select_car_race_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Race", tint = Color.Black)
                            Spacer(Modifier.width(6.dp))
                            Text("Race With ${activeCar.name}", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (totalCoins >= activeCar.costCoins) {
                                    onCoinsUpdated(totalCoins - activeCar.costCoins)
                                    activeCar.isUnlocked = true
                                    onSelectCar(activeCar)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (totalCoins >= activeCar.costCoins) SpeedGold else Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("unlock_car_btn")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Text("Unlock for ${activeCar.costCoins} Coins", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // 3. Performance Upgrades Section
        if (activeCar.isUnlocked) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = "Upgrades", tint = SpeedGold, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("PERFORMANCE TUNING", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SpeedGold)
                        }

                        Spacer(Modifier.height(10.dp))

                        // Engine Upgrade Item
                        val engineUpgradeCost = activeCar.engineUpgrade * 50
                        UpgradeRow(
                            title = "V8 Engine Tune (Lv.${activeCar.engineUpgrade})",
                            bonusText = "+15 km/h Top Speed",
                            cost = engineUpgradeCost,
                            canAfford = totalCoins >= engineUpgradeCost,
                            onUpgrade = {
                                if (totalCoins >= engineUpgradeCost) {
                                    onCoinsUpdated(totalCoins - engineUpgradeCost)
                                    activeCar.engineUpgrade++
                                    onSelectCar(activeCar)
                                }
                            }
                        )

                        Spacer(Modifier.height(8.dp))

                        // Turbo Upgrade Item
                        val turboUpgradeCost = activeCar.turboUpgrade * 60
                        UpgradeRow(
                            title = "Twin Turbocharger (Lv.${activeCar.turboUpgrade})",
                            bonusText = "-0.2s 0-100 Accel",
                            cost = turboUpgradeCost,
                            canAfford = totalCoins >= turboUpgradeCost,
                            onUpgrade = {
                                if (totalCoins >= turboUpgradeCost) {
                                    onCoinsUpdated(totalCoins - turboUpgradeCost)
                                    activeCar.turboUpgrade++
                                    onSelectCar(activeCar)
                                }
                            }
                        )

                        Spacer(Modifier.height(8.dp))

                        // NOS Upgrade Item
                        val nosUpgradeCost = activeCar.nosUpgrade * 40
                        UpgradeRow(
                            title = "Nitro NOS Injector (Lv.${activeCar.nosUpgrade})",
                            bonusText = "+25% Nitro Duration",
                            cost = nosUpgradeCost,
                            canAfford = totalCoins >= nosUpgradeCost,
                            onUpgrade = {
                                if (totalCoins >= nosUpgradeCost) {
                                    onCoinsUpdated(totalCoins - nosUpgradeCost)
                                    activeCar.nosUpgrade++
                                    onSelectCar(activeCar)
                                }
                            }
                        )
                    }
                }
            }
        }

        // 4. Car Select Grid (All Available Cars)
        item {
            Text("SELECT VEHICLE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Gray, letterSpacing = 1.sp)
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                carsList.forEach { car ->
                    val isSelected = car.id == selectedCarId
                    Surface(
                        color = if (isSelected) Color(0xFF1E283D) else CardSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) NitroCyan else CardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCarId = car.id }
                            .testTag("car_card_${car.id}")
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
                                        .background(car.bodyColor, RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏎️", fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(car.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text("${car.topSpeedKmh} km/h • ${car.typeName}", fontSize = 10.sp, color = Color.LightGray)
                                }
                            }

                            if (!car.isUnlocked) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = SpeedGold, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("${car.costCoins} G", fontSize = 12.sp, color = SpeedGold, fontWeight = FontWeight.Bold)
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
fun UpgradeRow(
    title: String,
    bonusText: String,
    cost: Int,
    canAfford: Boolean,
    onUpgrade: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F131C), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(bonusText, fontSize = 10.sp, color = NeonGreen)
        }

        Button(
            onClick = onUpgrade,
            enabled = canAfford,
            colors = ButtonDefaults.buttonColors(
                containerColor = SpeedGold,
                disabledContainerColor = Color(0xFF242E40)
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("$cost G", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (canAfford) Color.Black else Color.Gray)
        }
    }
}
