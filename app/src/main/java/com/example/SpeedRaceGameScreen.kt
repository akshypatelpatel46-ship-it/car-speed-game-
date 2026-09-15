package com.example

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AsphaltGray
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NitroCyan
import com.example.ui.theme.NitroGlow
import com.example.ui.theme.RacingRed
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SpeedGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

data class SpeedPopEffect(
    val id: Long,
    val text: String,
    val color: Color,
    val x: Float,
    val y: Float,
    val createdAt: Long = System.currentTimeMillis()
)

@Composable
fun SpeedRaceGameScreen(
    currentCar: CarProfile,
    totalCoinsBank: Int,
    onCoinsUpdated: (Int) -> Unit,
    onGoToGarage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Game Running States
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Driving Mechanics
    var speedKmh by remember { mutableFloatStateOf(80f) }
    var topSpeedReached by remember { mutableFloatStateOf(80f) }
    var distanceMeters by remember { mutableFloatStateOf(0f) }
    var score by remember { mutableIntStateOf(0) }
    var coinsInRun by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var hasShield by remember { mutableStateOf(false) }

    // Nitro Tank (0 to 100%)
    var nitroTank by remember { mutableFloatStateOf(85f) }
    var isNitroPressed by remember { mutableStateOf(false) }
    var isGasPressed by remember { mutableStateOf(true) }
    var isBrakePressed by remember { mutableStateOf(false) }

    // Car position on highway: normalized X (0.0 to 1.0 across 4 lanes)
    // Lane centers: 0.125, 0.375, 0.625, 0.875
    var carLaneX by remember { mutableFloatStateOf(0.375f) }
    var targetLaneX by remember { mutableFloatStateOf(0.375f) }
    var steerTilt by remember { mutableFloatStateOf(0f) }

    // Highway Road Scrolling progress
    var roadStripeOffset by remember { mutableFloatStateOf(0f) }

    // Traffic Cars
    val trafficCars = remember { mutableStateListOf<TrafficCar>() }

    // Pickups
    val roadPickups = remember { mutableStateListOf<RoadPickup>() }

    // Floating text feedback (e.g. "+100 NEAR MISS!", "NITRO BOOST!")
    val popEffects = remember { mutableStateListOf<SpeedPopEffect>() }

    // Near miss count
    var nearMissCount by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(CarAudioSystem.isMuted()) }

    val maxEngineSpeed = (currentCar.topSpeedKmh + (currentCar.engineUpgrade - 1) * 15).toFloat()

    // Smooth lane steering interpolation
    LaunchedEffect(targetLaneX) {
        while (abs(carLaneX - targetLaneX) > 0.01f) {
            val delta = (targetLaneX - carLaneX) * 0.28f
            carLaneX += delta
            steerTilt = (delta * 8f).coerceIn(-1f, 1f)
            delay(16)
        }
        carLaneX = targetLaneX
        steerTilt = 0f
    }

    // Main Game Loop (60 FPS tick)
    LaunchedEffect(isGameOver, isPaused) {
        var frameCounter = 0
        while (!isGameOver && !isPaused) {
            delay(16) // ~60 fps
            frameCounter++

            // Calculate Target Speed based on Gas / Brake / Nitro
            val isNitroActive = isNitroPressed && nitroTank > 2f
            if (isNitroActive) {
                nitroTank = (nitroTank - 0.35f).coerceAtLeast(0f)
                val targetSpeed = maxEngineSpeed + 50f
                speedKmh += (targetSpeed - speedKmh) * 0.08f
            } else if (isBrakePressed) {
                speedKmh = (speedKmh - 3.5f).coerceAtLeast(35f)
            } else if (isGasPressed) {
                speedKmh += (maxEngineSpeed - speedKmh) * 0.035f
            } else {
                // Coasting
                speedKmh = (speedKmh - 0.5f).coerceAtLeast(60f)
            }

            if (speedKmh > topSpeedReached) {
                topSpeedReached = speedKmh
            }

            // Engine rev sound occasionally
            if (frameCounter % 18 == 0) {
                CarAudioSystem.playEngineRev(speedKmh)
            }

            // Distance & Score increment
            val deltaMeters = (speedKmh * 1000f / 3600f) * 0.016f
            distanceMeters += deltaMeters
            score += (speedKmh * 0.04f).toInt().coerceAtLeast(1)

            // Scroll highway lines
            roadStripeOffset = (roadStripeOffset + (speedKmh * 0.12f)) % 100f

            // 1. Update Traffic Cars
            val trafficIter = trafficCars.iterator()
            while (trafficIter.hasNext()) {
                val traffic = trafficIter.next()
                // Move down relative to player speed
                val relSpeed = (speedKmh - traffic.speed) * 0.00018f
                traffic.yProgress += relSpeed.coerceAtLeast(0.003f)

                // Check collision with player (player is around y = 0.78f)
                val playerY = 0.78f
                val playerLaneIndex = when {
                    carLaneX < 0.25f -> 0
                    carLaneX < 0.50f -> 1
                    carLaneX < 0.75f -> 2
                    else -> 3
                }

                if (abs(traffic.yProgress - playerY) < 0.09f && traffic.lane == playerLaneIndex) {
                    // CRASH!
                    CarAudioSystem.playCrash(context)
                    if (hasShield) {
                        hasShield = false
                        popEffects.add(
                            SpeedPopEffect(
                                System.currentTimeMillis(),
                                "SHIELD BROKEN!",
                                SpeedGold,
                                carLaneX,
                                0.7f
                            )
                        )
                        trafficIter.remove()
                    } else {
                        lives--
                        speedKmh = 40f
                        popEffects.add(
                            SpeedPopEffect(
                                System.currentTimeMillis(),
                                "CRASH! -1 LIFE",
                                RacingRed,
                                carLaneX,
                                0.7f
                            )
                        )
                        trafficIter.remove()
                        if (lives <= 0) {
                            isGameOver = true
                            val newTotal = totalCoinsBank + coinsInRun
                            onCoinsUpdated(newTotal)
                        }
                    }
                    continue
                }

                // Check Near Miss
                if (!traffic.isPassed && traffic.yProgress > playerY && traffic.yProgress < playerY + 0.15f) {
                    val laneDiff = abs(traffic.lane - playerLaneIndex)
                    if (laneDiff == 1) {
                        traffic.isPassed = true
                        nearMissCount++
                        score += 150
                        CarAudioSystem.playNearMiss(context)
                        popEffects.add(
                            SpeedPopEffect(
                                System.currentTimeMillis(),
                                "CLOSE CALL! +150",
                                NitroGlow,
                                carLaneX,
                                0.65f
                            )
                        )
                    }
                }

                // Remove traffic off bottom
                if (traffic.yProgress > 1.2f) {
                    trafficIter.remove()
                }
            }

            // 2. Spawn Traffic Cars
            if (trafficCars.size < 4 && Random.nextInt(100) < 5) {
                val spawnLane = Random.nextInt(4)
                val isLaneOccupied = trafficCars.any { it.lane == spawnLane && it.yProgress < 0.25f }
                if (!isLaneOccupied) {
                    val colors = listOf(Color(0xFF3B82F6), Color(0xFFE2E8F0), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF8B5CF6))
                    val trafficSpeed = Random.nextFloat() * 60f + 50f
                    trafficCars.add(
                        TrafficCar(
                            id = System.currentTimeMillis() + Random.nextInt(1000),
                            lane = spawnLane,
                            yProgress = -0.1f,
                            speed = trafficSpeed,
                            color = colors.random(),
                            carType = if (Random.nextBoolean()) CarType.SEDAN else CarType.SUPERCAR
                        )
                    )
                }
            }

            // 3. Update & Spawn Road Pickups
            val pickupIter = roadPickups.iterator()
            while (pickupIter.hasNext()) {
                val pickup = pickupIter.next()
                pickup.yProgress += (speedKmh * 0.00015f).coerceAtLeast(0.004f)

                // Collection collision
                val playerY = 0.78f
                val playerLane = when {
                    carLaneX < 0.25f -> 0
                    carLaneX < 0.50f -> 1
                    carLaneX < 0.75f -> 2
                    else -> 3
                }
                if (abs(pickup.yProgress - playerY) < 0.07f && pickup.lane == playerLane) {
                    CarAudioSystem.playCoin(context)
                    when (pickup.type) {
                        PickupType.COIN -> {
                            coinsInRun += 5
                            score += 50
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "+5 COINS", GoldLight, carLaneX, 0.7f))
                        }
                        PickupType.NITRO -> {
                            nitroTank = (nitroTank + 35f).coerceAtMost(100f)
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "+35% NITRO!", NitroGlow, carLaneX, 0.7f))
                        }
                        PickupType.SHIELD -> {
                            hasShield = true
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "ENERGY SHIELD ON!", SkyBlue, carLaneX, 0.7f))
                        }
                        PickupType.MULTIPLIER -> {
                            score += 300
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "SPEED BONUS +300!", NeonGreen, carLaneX, 0.7f))
                        }
                    }
                    pickupIter.remove()
                    continue
                }

                if (pickup.yProgress > 1.2f) {
                    pickupIter.remove()
                }
            }

            // Spawn Pickups
            if (roadPickups.size < 2 && Random.nextInt(100) < 3) {
                val pLane = Random.nextInt(4)
                val type = when (Random.nextInt(10)) {
                    in 0..5 -> PickupType.COIN
                    in 6..7 -> PickupType.NITRO
                    8 -> PickupType.SHIELD
                    else -> PickupType.MULTIPLIER
                }
                roadPickups.add(
                    RoadPickup(
                        id = System.currentTimeMillis() + Random.nextInt(1000),
                        lane = pLane,
                        yProgress = -0.1f,
                        type = type
                    )
                )
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(CarbonDark)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // 1. HIGHWAY CANVAS (Road, Asphalt, Lane Lines, Guardrails)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Grass / Shoulder background
            drawRect(Color(0xFF0F2317), Offset(0f, 0f), Size(w, h))

            // Road Bounds (centered 4-lane highway)
            val roadW = w * 0.86f
            val roadLeft = (w - roadW) / 2f
            val roadRight = roadLeft + roadW
            val laneW = roadW / 4f

            // Asphalt Surface
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF141720), Color(0xFF1C202C))
                ),
                topLeft = Offset(roadLeft, 0f),
                size = Size(roadW, h)
            )

            // Outer Guardrails / Red-White Curbs
            val curbW = 8.dp.toPx()
            val numCurbSegments = 24
            val segmentH = h / numCurbSegments
            for (i in 0..numCurbSegments) {
                val curbColor = if ((i + (roadStripeOffset / 8).toInt()) % 2 == 0) RacingRed else Color.White
                // Left curb
                drawRect(curbColor, Offset(roadLeft - curbW, i * segmentH), Size(curbW, segmentH))
                // Right curb
                drawRect(curbColor, Offset(roadRight, i * segmentH), Size(curbW, segmentH))
            }

            // Lane Divider Dashed Lines (3 divider lines for 4 lanes)
            val dashH = 34.dp.toPx()
            val dashGap = 24.dp.toPx()
            val totalDashUnit = dashH + dashGap

            for (laneIndex in 1..3) {
                val lx = roadLeft + laneIndex * laneW
                var yPos = -(roadStripeOffset * 2f) % totalDashUnit
                while (yPos < h) {
                    if (yPos + dashH > 0) {
                        drawLine(
                            color = Color(0xCCFFFFFF),
                            start = Offset(lx, yPos),
                            end = Offset(lx, yPos + dashH),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    yPos += totalDashUnit
                }
            }

            // Speed lines during Nitro
            if (isNitroPressed && nitroTank > 2f) {
                for (s in 0..12) {
                    val lineX = roadLeft + (s * (roadW / 12f))
                    val lineY = (Random.nextFloat() * h)
                    drawLine(
                        color = Color(0x7722D3EE),
                        start = Offset(lineX, lineY),
                        end = Offset(lineX, lineY + 60.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        val screenWVal = screenW.value
        val screenHVal = screenH.value
        val roadWVal = screenWVal * 0.86f
        val roadLeftVal = (screenWVal - roadWVal) / 2f
        val laneWVal = roadWVal / 4f

        // 2. ROAD PICKUPS (Coins, Nitro Tanks, Shields)
        roadPickups.forEach { pickup ->
            val px = (roadLeftVal + (pickup.lane + 0.5f) * laneWVal).dp
            val py = (screenHVal * pickup.yProgress).dp

            Box(
                modifier = Modifier
                    .offset(x = px - 18.dp, y = py - 18.dp)
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                when (pickup.type) {
                    PickupType.COIN -> {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .shadow(8.dp, CircleShape)
                                .background(GoldLight, CircleShape)
                                .border(2.dp, SpeedGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪙", fontSize = 16.sp)
                        }
                    }
                    PickupType.NITRO -> {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .shadow(8.dp, CircleShape)
                                .background(NitroCyan, CircleShape)
                                .border(2.dp, NitroGlow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 18.sp)
                        }
                    }
                    PickupType.SHIELD -> {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .shadow(8.dp, CircleShape)
                                .background(SkyBlue, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛡️", fontSize = 18.sp)
                        }
                    }
                    PickupType.MULTIPLIER -> {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .shadow(8.dp, CircleShape)
                                .background(NeonGreen, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2X", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }
            }
        }

        // 3. TRAFFIC CARS
        trafficCars.forEach { traffic ->
            val tx = (roadLeftVal + (traffic.lane + 0.5f) * laneWVal).dp
            val ty = (screenHVal * traffic.yProgress).dp

            Box(
                modifier = Modifier
                    .offset(x = tx - 25.dp, y = ty - 45.dp)
                    .size(50.dp, 90.dp)
            ) {
                CarCanvas(
                    bodyColor = traffic.color,
                    accentColor = Color(0xFF1E293B),
                    isBraking = false,
                    isNitroActive = false,
                    steerTilt = 0f,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 4. PLAYER'S HIGH-SPEED CAR
        val playerPx = (roadLeftVal + (carLaneX * roadWVal)).dp
        val playerPy = (screenHVal * 0.78f).dp

        Box(
            modifier = Modifier
                .offset(x = playerPx - 30.dp, y = playerPy - 55.dp)
                .size(60.dp, 105.dp)
        ) {
            CarCanvas(
                bodyColor = currentCar.bodyColor,
                accentColor = currentCar.accentColor,
                isBraking = isBrakePressed,
                isNitroActive = isNitroPressed && nitroTank > 2f,
                steerTilt = steerTilt,
                modifier = Modifier.fillMaxSize()
            )

            // Energy Shield Overlay (if active)
            if (hasShield) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, NitroGlow, RoundedCornerShape(16.dp))
                        .background(NitroCyan.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                )
            }
        }

        // 5. POPUP EFFECTS (Near Miss, Coins, Nitro)
        popEffects.forEach { pop ->
            val age = System.currentTimeMillis() - pop.createdAt
            val yOffset = -(age / 20f).dp
            val popX = (screenWVal * pop.x).dp
            val popY = (screenHVal * pop.y).dp + yOffset
            Box(
                modifier = Modifier
                    .offset(x = popX, y = popY)
            ) {
                Text(
                    text = pop.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = pop.color,
                    modifier = Modifier
                        .background(Color(0xDD000000), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // 6. TOP RACING HUD (Speedometer, Score, Lives, Coins, Nitro Gauge)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Digital Speedometer Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface.copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5f.dp, if (isNitroPressed && nitroTank > 2f) NitroCyan else CardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${speedKmh.roundToInt()}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isNitroPressed && nitroTank > 2f) NitroGlow else Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "KM/H",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SpeedGold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Text(
                                text = "TOP: ${topSpeedReached.roundToInt()} km/h",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Middle Stats (Score & Distance & Coins)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SCORE: $score",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = SpeedGold, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = "+$coinsInRun G",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${(distanceMeters / 1000f).format(1)} km",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }

                // Right: Lives & Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Lives Hearts
                    Row(
                        modifier = Modifier
                            .background(CardSurface.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        repeat(3) { i ->
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Life",
                                tint = if (i < lives) RacingRed else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    IconButton(
                        onClick = { isMuted = CarAudioSystem.toggleMute() },
                        modifier = Modifier.size(34.dp).testTag("toggle_audio")
                    ) {
                        Icon(
                            if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Mute",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            // Nitro (NOS) Progress Gauge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Bolt, contentDescription = "NOS", tint = NitroCyan, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("NITRO (NOS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NitroGlow)
                Spacer(Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { (nitroTank / 100f).coerceIn(0f, 1f) },
                    color = if (nitroTank > 20f) NitroCyan else RacingRed,
                    trackColor = Color(0xFF0F2633),
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text("${nitroTank.toInt()}%", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // 7. BOTTOM STEERING & PEDAL CONTROLS
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Steering Left / Right Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                // Steer Left
                SteerButton(
                    text = "◀ LEFT",
                    tag = "steer_left",
                    onClick = {
                        targetLaneX = when {
                            targetLaneX > 0.7f -> 0.625f
                            targetLaneX > 0.45f -> 0.375f
                            else -> 0.125f
                        }
                    }
                )

                // Steer Right
                SteerButton(
                    text = "RIGHT ▶",
                    tag = "steer_right",
                    onClick = {
                        targetLaneX = when {
                            targetLaneX < 0.25f -> 0.375f
                            targetLaneX < 0.50f -> 0.625f
                            else -> 0.875f
                        }
                    }
                )
            }

            // Pedals & Nitro
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                // Brake Pedal
                PedalButton(
                    label = "BRAKE",
                    color = RacingRed,
                    size = 54.dp,
                    tag = "brake_pedal",
                    onPressedChange = { isBrakePressed = it }
                )

                // Nitro NOS Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .shadow(10.dp, CircleShape)
                        .background(
                            brush = Brush.radialGradient(listOf(NitroGlow, NitroCyan, Color(0xFF0E7490))),
                            shape = CircleShape
                        )
                        .border(2.dp, Color.White, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    if (nitroTank > 5f) {
                                        isNitroPressed = true
                                        CarAudioSystem.playNitro(context)
                                    }
                                    tryAwaitRelease()
                                    isNitroPressed = false
                                }
                            )
                        }
                        .testTag("nitro_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 18.sp)
                        Text("NITRO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                // Gas / Accelerator Pedal
                PedalButton(
                    label = "GAS",
                    color = NeonGreen,
                    size = 64.dp,
                    tag = "gas_pedal",
                    onPressedChange = { isGasPressed = it }
                )
            }
        }

        // 8. GAME OVER MODAL
        AnimatedVisibility(
            visible = isGameOver,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center).padding(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, RacingRed),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💥 CRASHED!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = RacingRed
                    )
                    Text(
                        text = "Your high-speed run has ended",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(Modifier.height(16.dp))

                    // Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("FINAL SCORE", fontSize = 10.sp, color = Color.Gray)
                            Text("$score", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOP SPEED", fontSize = 10.sp, color = Color.Gray)
                            Text("${topSpeedReached.roundToInt()} km/h", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SpeedGold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("COINS", fontSize = 10.sp, color = Color.Gray)
                            Text("+$coinsInRun G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Near Misses: $nearMissCount  •  Distance: ${(distanceMeters / 1000f).format(1)} km", fontSize = 11.sp, color = Color.LightGray)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onGoToGarage,
                            modifier = Modifier.weight(1f).testTag("garage_btn")
                        ) {
                            Icon(Icons.Default.ElectricCar, contentDescription = "Garage", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Garage", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                // Reset Run
                                lives = 3
                                speedKmh = 80f
                                topSpeedReached = 80f
                                score = 0
                                coinsInRun = 0
                                distanceMeters = 0f
                                nitroTank = 85f
                                trafficCars.clear()
                                roadPickups.clear()
                                isGameOver = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            modifier = Modifier.weight(1f).testTag("restart_btn")
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Restart", modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Race Again", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SteerButton(
    text: String,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 66.dp, height = 54.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

@Composable
fun PedalButton(
    label: String,
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    tag: String,
    onPressedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(color, RoundedCornerShape(12.dp))
            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPressedChange(true)
                        tryAwaitRelease()
                        onPressedChange(false)
                    }
                )
            }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
        )
    }
}

fun Float.format(decimals: Int): String = "%.${decimals}f".format(this)
