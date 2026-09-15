package com.example

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Panorama
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
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
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SpeedGold
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

data class BikeTraffic(
    val id: Long,
    val lane: Int,
    var yProgress: Float,
    val speed: Float,
    val color: Color,
    var isPassed: Boolean = false
)

data class BikePickup(
    val id: Long,
    val lane: Int,
    var yProgress: Float,
    val type: PickupType,
    var isCollected: Boolean = false
)

@Composable
fun MotoSpeedGameScreen(
    currentBike: BikeProfile,
    totalCoinsBank: Int,
    onCoinsUpdated: (Int) -> Unit,
    onGoToGarage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    // High Level Environment Selector
    var activeEnv by remember { mutableStateOf(HighLevelEnvironment.CYBERPUNK_NIGHT) }

    // Game Running States
    var isGameOver by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }

    // Driving Mechanics
    var speedKmh by remember { mutableFloatStateOf(110f) }
    var topSpeedReached by remember { mutableFloatStateOf(110f) }
    var rpmValue by remember { mutableFloatStateOf(6500f) }
    var distanceMeters by remember { mutableFloatStateOf(0f) }
    var score by remember { mutableIntStateOf(0) }
    var coinsInRun by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var hasShield by remember { mutableStateOf(false) }

    // Wheelie Stunt Mechanic
    var isWheelieActive by remember { mutableStateOf(false) }
    var wheelieDurationFrames by remember { mutableIntStateOf(0) }

    // Nitro Tank
    var nitroTank by remember { mutableFloatStateOf(90f) }
    var isNitroPressed by remember { mutableStateOf(false) }
    var isGasPressed by remember { mutableStateOf(true) }
    var isBrakePressed by remember { mutableStateOf(false) }

    // Keyboard Pressed State tracking for PC HUD
    var keyWPressed by remember { mutableStateOf(false) }
    var keySPressed by remember { mutableStateOf(false) }
    var keyAPressed by remember { mutableStateOf(false) }
    var keyDPressed by remember { mutableStateOf(false) }
    var keySpacePressed by remember { mutableStateOf(false) }
    var keyKPressed by remember { mutableStateOf(false) }

    // Bike Highway position (0.0 to 1.0)
    var bikeLaneX by remember { mutableFloatStateOf(0.375f) }
    var targetLaneX by remember { mutableFloatStateOf(0.375f) }
    var leanAngle by remember { mutableFloatStateOf(0f) }

    // Road animation offset
    var roadStripeOffset by remember { mutableFloatStateOf(0f) }

    // Traffic and Pickups
    val trafficCars = remember { mutableStateListOf<BikeTraffic>() }
    val roadPickups = remember { mutableStateListOf<BikePickup>() }
    val popEffects = remember { mutableStateListOf<SpeedPopEffect>() }

    var nearMissCount by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(BikeAudioSystem.isMuted()) }

    val maxEngineSpeed = (currentBike.topSpeedKmh + (currentBike.engineUpgrade - 1) * 18).toFloat()

    // Focus for PC Keyboard Input
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Smooth Leaning Physics
    LaunchedEffect(targetLaneX) {
        while (abs(bikeLaneX - targetLaneX) > 0.01f) {
            val delta = (targetLaneX - bikeLaneX) * 0.32f
            bikeLaneX += delta
            leanAngle = (delta * 12f).coerceIn(-1f, 1f)
            delay(16)
        }
        bikeLaneX = targetLaneX
        leanAngle = 0f
    }

    // Main 60 FPS Game Loop
    LaunchedEffect(isGameOver, isPaused) {
        var frame = 0
        while (!isGameOver && !isPaused) {
            delay(16)
            frame++

            val isNitro = isNitroPressed && nitroTank > 2f
            if (isNitro) {
                nitroTank = (nitroTank - 0.45f).coerceAtLeast(0f)
                val targetSpeed = maxEngineSpeed + 55f
                speedKmh += (targetSpeed - speedKmh) * 0.09f
                rpmValue = (13500f + Random.nextFloat() * 800f).coerceAtMost(14800f)
            } else if (isBrakePressed) {
                speedKmh = (speedKmh - 4.5f).coerceAtLeast(40f)
                rpmValue = (rpmValue - 250f).coerceAtLeast(3000f)
            } else if (isGasPressed) {
                speedKmh += (maxEngineSpeed - speedKmh) * 0.045f
                val targetRpm = 4000f + (speedKmh / maxEngineSpeed) * 9500f
                rpmValue += (targetRpm - rpmValue) * 0.1f
            } else {
                speedKmh = (speedKmh - 0.8f).coerceAtLeast(60f)
                rpmValue = (rpmValue - 100f).coerceAtLeast(3000f)
            }

            if (speedKmh > topSpeedReached) {
                topSpeedReached = speedKmh
            }

            // Wheelie Stunt Scoring
            if (isWheelieActive && speedKmh > 100f) {
                wheelieDurationFrames++
                score += 8
                if (wheelieDurationFrames % 30 == 0) {
                    popEffects.add(
                        SpeedPopEffect(
                            System.currentTimeMillis(),
                            "WHEELIE COMBO! +250",
                            GoldLight,
                            bikeLaneX,
                            0.62f
                        )
                    )
                }
            } else {
                wheelieDurationFrames = 0
            }

            // Superbike Engine screaming sound
            if (frame % 15 == 0) {
                BikeAudioSystem.playBikeRev(speedKmh)
            }

            val deltaM = (speedKmh * 1000f / 3600f) * 0.016f
            distanceMeters += deltaM
            score += (speedKmh * 0.05f).toInt().coerceAtLeast(1)

            roadStripeOffset = (roadStripeOffset + (speedKmh * 0.15f)) % 100f

            // 1. Update Traffic
            val trafficIter = trafficCars.iterator()
            while (trafficIter.hasNext()) {
                val traffic = trafficIter.next()
                val relSpeed = (speedKmh - traffic.speed) * 0.00022f
                traffic.yProgress += relSpeed.coerceAtLeast(0.004f)

                val playerY = 0.78f
                val playerLaneIndex = when {
                    bikeLaneX < 0.25f -> 0
                    bikeLaneX < 0.50f -> 1
                    bikeLaneX < 0.75f -> 2
                    else -> 3
                }

                // Crash Check
                if (abs(traffic.yProgress - playerY) < 0.08f && traffic.lane == playerLaneIndex) {
                    BikeAudioSystem.playCrash(context)
                    if (hasShield) {
                        hasShield = false
                        popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "SHIELD DEFLECTED!", SpeedGold, bikeLaneX, 0.7f))
                        trafficIter.remove()
                    } else {
                        lives--
                        speedKmh = 45f
                        isWheelieActive = false
                        popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "WIPEOUT! -1 LIFE", RacingRed, bikeLaneX, 0.7f))
                        trafficIter.remove()
                        if (lives <= 0) {
                            isGameOver = true
                            onCoinsUpdated(totalCoinsBank + coinsInRun)
                        }
                    }
                    continue
                }

                // Near Miss Overtake
                if (!traffic.isPassed && traffic.yProgress > playerY && traffic.yProgress < playerY + 0.14f) {
                    val diff = abs(traffic.lane - playerLaneIndex)
                    if (diff == 1) {
                        traffic.isPassed = true
                        nearMissCount++
                        score += 200
                        BikeAudioSystem.playNearMiss(context)
                        popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "SPLIT-LANE OVERTAKE! +200", NitroGlow, bikeLaneX, 0.64f))
                    }
                }

                if (traffic.yProgress > 1.2f) {
                    trafficIter.remove()
                }
            }

            // Spawn Traffic
            if (trafficCars.size < 4 && Random.nextInt(100) < 6) {
                val sLane = Random.nextInt(4)
                if (trafficCars.none { it.lane == sLane && it.yProgress < 0.25f }) {
                    val colors = listOf(Color(0xFFEF4444), Color(0xFF3B82F6), Color(0xFFF59E0B), Color(0xFFE2E8F0))
                    trafficCars.add(
                        BikeTraffic(
                            id = System.currentTimeMillis() + Random.nextInt(1000),
                            lane = sLane,
                            yProgress = -0.1f,
                            speed = Random.nextFloat() * 70f + 60f,
                            color = colors.random()
                        )
                    )
                }
            }

            // 2. Pickups
            val pickIter = roadPickups.iterator()
            while (pickIter.hasNext()) {
                val pick = pickIter.next()
                pick.yProgress += (speedKmh * 0.00018f).coerceAtLeast(0.005f)

                val playerY = 0.78f
                val playerLane = when {
                    bikeLaneX < 0.25f -> 0
                    bikeLaneX < 0.50f -> 1
                    bikeLaneX < 0.75f -> 2
                    else -> 3
                }

                if (abs(pick.yProgress - playerY) < 0.07f && pick.lane == playerLane) {
                    BikeAudioSystem.playCoin(context)
                    when (pick.type) {
                        PickupType.COIN -> {
                            coinsInRun += 10
                            score += 100
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "+10 G COINS", GoldLight, bikeLaneX, 0.7f))
                        }
                        PickupType.NITRO -> {
                            nitroTank = (nitroTank + 40f).coerceAtMost(100f)
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "+40% NITRO NOS!", NitroGlow, bikeLaneX, 0.7f))
                        }
                        PickupType.SHIELD -> {
                            hasShield = true
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "ENERGY AURA ON!", SkyBlue, bikeLaneX, 0.7f))
                        }
                        PickupType.MULTIPLIER -> {
                            score += 400
                            popEffects.add(SpeedPopEffect(System.currentTimeMillis(), "SPEED DEMON +400!", NeonGreen, bikeLaneX, 0.7f))
                        }
                    }
                    pickIter.remove()
                    continue
                }

                if (pick.yProgress > 1.2f) {
                    pickIter.remove()
                }
            }

            if (roadPickups.size < 2 && Random.nextInt(100) < 4) {
                roadPickups.add(
                    BikePickup(
                        id = System.currentTimeMillis() + Random.nextInt(1000),
                        lane = Random.nextInt(4),
                        yProgress = -0.1f,
                        type = listOf(PickupType.COIN, PickupType.NITRO, PickupType.SHIELD, PickupType.MULTIPLIER).random()
                    )
                )
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                // PC KEYBOARD CONTROLS (WASD, Arrows, Space, K)
                val isDown = keyEvent.type == KeyEventType.KeyDown
                val isUp = keyEvent.type == KeyEventType.KeyUp

                when (keyEvent.key) {
                    Key.W, Key.DirectionUp -> {
                        keyWPressed = isDown
                        isGasPressed = isDown
                        true
                    }
                    Key.S, Key.DirectionDown -> {
                        keySPressed = isDown
                        isBrakePressed = isDown
                        true
                    }
                    Key.A, Key.DirectionLeft -> {
                        keyAPressed = isDown
                        if (isDown) {
                            targetLaneX = when {
                                targetLaneX > 0.7f -> 0.625f
                                targetLaneX > 0.45f -> 0.375f
                                else -> 0.125f
                            }
                        }
                        true
                    }
                    Key.D, Key.DirectionRight -> {
                        keyDPressed = isDown
                        if (isDown) {
                            targetLaneX = when {
                                targetLaneX < 0.25f -> 0.375f
                                targetLaneX < 0.50f -> 0.625f
                                else -> 0.875f
                            }
                        }
                        true
                    }
                    Key.Spacebar, Key.ShiftLeft, Key.ShiftRight -> {
                        keySpacePressed = isDown
                        isNitroPressed = isDown
                        if (isDown && nitroTank > 5f) {
                            BikeAudioSystem.playNitro(context)
                        }
                        true
                    }
                    Key.K, Key.CtrlLeft -> {
                        keyKPressed = isDown
                        isWheelieActive = isDown
                        if (isDown) {
                            BikeAudioSystem.playWheelie(context)
                        }
                        true
                    }
                    Key.H -> {
                        if (isDown) BikeAudioSystem.playHorn(context)
                        true
                    }
                    Key.E -> {
                        if (isDown) {
                            // Cycle High-Level Environments
                            activeEnv = when (activeEnv) {
                                HighLevelEnvironment.CYBERPUNK_NIGHT -> HighLevelEnvironment.SUNSET_CANYON
                                HighLevelEnvironment.SUNSET_CANYON -> HighLevelEnvironment.MIDNIGHT_BAY
                                HighLevelEnvironment.MIDNIGHT_BAY -> HighLevelEnvironment.CYBERPUNK_NIGHT
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            .background(CarbonDark)
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        val screenWVal = screenW.value
        val screenHVal = screenH.value
        val roadWVal = screenWVal * 0.86f
        val roadLeftVal = (screenWVal - roadWVal) / 2f
        val laneWVal = roadWVal / 4f

        // 1. HIGH-LEVEL PARALLAX BACKGROUND (Skyline, Mountains, Sunset / Cyberpunk Horizon)
        Box(modifier = Modifier.fillMaxWidth().height(screenH * 0.38f)) {
            // High-Level Scenery Art
            Image(
                painter = painterResource(id = R.drawable.img_highlevel_bg),
                contentDescription = "High-Level Scenery",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dynamic Atmospheric Gradient Overlay based on selected environment
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                activeEnv.skyTop.copy(alpha = 0.4f),
                                activeEnv.skyBottom.copy(alpha = 0.6f),
                                activeEnv.roadColor
                            )
                        )
                    )
            )
        }

        // 2. HIGH-SPEED HIGHWAY ROAD CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val roadTopY = h * 0.32f
            val roadHeight = h - roadTopY

            val roadLeftPx = (w - (w * 0.86f)) / 2f
            val roadWPx = w * 0.86f
            val laneWPx = roadWPx / 4f

            // Road Surface
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(activeEnv.roadColor, Color(0xFF141824))
                ),
                topLeft = Offset(roadLeftPx, roadTopY),
                size = Size(roadWPx, roadHeight)
            )

            // Neon Road Edge Curb Lines
            val curbW = 10.dp.toPx()
            val numSegments = 20
            val segH = roadHeight / numSegments
            for (i in 0..numSegments) {
                val curbColor = if ((i + (roadStripeOffset / 8).toInt()) % 2 == 0) NitroCyan else SpeedGold
                drawRect(curbColor, Offset(roadLeftPx - curbW, roadTopY + i * segH), Size(curbW, segH))
                drawRect(curbColor, Offset(roadLeftPx + roadWPx, roadTopY + i * segH), Size(curbW, segH))
            }

            // Dashed Lane Lines
            val dashH = 36.dp.toPx()
            val dashGap = 26.dp.toPx()
            val unitH = dashH + dashGap
            for (lane in 1..3) {
                val lx = roadLeftPx + lane * laneWPx
                var yPos = roadTopY + (-(roadStripeOffset * 2.2f) % unitH)
                while (yPos < h) {
                    if (yPos + dashH > roadTopY) {
                        drawLine(
                            color = Color(0xEEFFFFFF),
                            start = Offset(lx, yPos.coerceAtLeast(roadTopY)),
                            end = Offset(lx, yPos + dashH),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    yPos += unitH
                }
            }

            // High-Speed Warp Blur Lines (Speed > 240 km/h or Nitro)
            if (speedKmh > 240f || (isNitroPressed && nitroTank > 2f)) {
                for (i in 0..14) {
                    val rx = roadLeftPx + Random.nextFloat() * roadWPx
                    val ry = roadTopY + Random.nextFloat() * roadHeight
                    drawLine(
                        color = if (isNitroPressed) Color(0x8822D3EE) else Color(0x55FCD34D),
                        start = Offset(rx, ry),
                        end = Offset(rx, ry + 70.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        // 3. PICKUPS ON ROAD
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

        // 4. TRAFFIC VEHICLES
        trafficCars.forEach { traffic ->
            val tx = (roadLeftVal + (traffic.lane + 0.5f) * laneWVal).dp
            val ty = (screenHVal * traffic.yProgress).dp

            Box(
                modifier = Modifier
                    .offset(x = tx - 24.dp, y = ty - 45.dp)
                    .size(48.dp, 90.dp)
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

        // 5. PLAYER'S SUPERBIKE & RIDER
        val playerPx = (roadLeftVal + (bikeLaneX * roadWVal)).dp
        val playerPy = (screenHVal * 0.78f).dp

        Box(
            modifier = Modifier
                .offset(x = playerPx - 26.dp, y = playerPy - 50.dp)
                .size(52.dp, 100.dp)
        ) {
            SuperbikeCanvas(
                bodyColor = currentBike.bodyColor,
                accentColor = currentBike.accentColor,
                suitColor = currentBike.suitColor,
                isBraking = isBrakePressed,
                isNitroActive = isNitroPressed && nitroTank > 2f,
                isWheelie = isWheelieActive,
                leanAngle = leanAngle,
                modifier = Modifier.fillMaxSize()
            )

            if (hasShield) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, NitroGlow, RoundedCornerShape(20.dp))
                        .background(NitroCyan.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                )
            }
        }

        // 6. POPUP FLOATING NOTIFICATIONS
        popEffects.forEach { pop ->
            val age = System.currentTimeMillis() - pop.createdAt
            val yOffset = -(age / 18f).dp
            val popX = (screenWVal * pop.x).dp
            val popY = (screenHVal * pop.y).dp + yOffset
            Box(modifier = Modifier.offset(x = popX, y = popY)) {
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

        // 7. TOP HUD: SPEEDOMETER, TACHOMETER, LIVES, ENVIRONMENT SWITCHER
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
                // High-End Speedometer Gauge Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface.copy(alpha = 0.94f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5f.dp,
                        if (isNitroPressed && nitroTank > 2f) NitroCyan else if (isWheelieActive) SpeedGold else CardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${speedKmh.roundToInt()}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isNitroPressed && nitroTank > 2f) NitroGlow else if (isWheelieActive) SpeedGold else Color.White,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "KM/H",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NitroCyan,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Text(
                                text = "RPM: ${rpmValue.toInt()} | TOP: ${topSpeedReached.roundToInt()}",
                                fontSize = 10.sp,
                                color = if (rpmValue > 12000f) RacingRed else Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Middle Stats (Score & Distance & Wheelie Indicator)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SCORE: $score",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    if (isWheelieActive) {
                        Text("🔥 WHEELIE STUNT!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SpeedGold)
                    } else {
                        Text("${(distanceMeters / 1000f).format(1)} km  •  +$coinsInRun G", fontSize = 11.sp, color = GoldLight)
                    }
                }

                // Environment Switcher & Audio Mute
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CardSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier.clickable {
                            activeEnv = when (activeEnv) {
                                HighLevelEnvironment.CYBERPUNK_NIGHT -> HighLevelEnvironment.SUNSET_CANYON
                                HighLevelEnvironment.SUNSET_CANYON -> HighLevelEnvironment.MIDNIGHT_BAY
                                HighLevelEnvironment.MIDNIGHT_BAY -> HighLevelEnvironment.CYBERPUNK_NIGHT
                            }
                        }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Panorama, contentDescription = "Env", tint = NitroGlow, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(activeEnv.title.take(10), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    IconButton(
                        onClick = { isMuted = BikeAudioSystem.toggleMute() },
                        modifier = Modifier.size(34.dp).testTag("toggle_bike_audio")
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

            // Nitro & Tachometer Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface.copy(alpha = 0.94f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Bolt, contentDescription = "NOS", tint = NitroCyan, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("NITRO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NitroGlow)
                Spacer(Modifier.width(8.dp))
                LinearProgressIndicator(
                    progress = { (nitroTank / 100f).coerceIn(0f, 1f) },
                    color = if (nitroTank > 20f) NitroCyan else RacingRed,
                    trackColor = Color(0xFF0F2633),
                    modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp))
                )
                Spacer(Modifier.width(10.dp))
                Text("${nitroTank.toInt()}%", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // 8. PC KEYBOARD SHORTCUTS HUD OVERLAY (Prominently shows [W], [S], [A], [D], [SPACE], [K])
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface.copy(alpha = 0.88f)),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 14.dp, bottom = 70.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Keyboard, contentDescription = "PC Keyboard", tint = NitroGlow, modifier = Modifier.size(16.dp))
                Text("PC CONTROLS:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = SpeedGold)
                KeyBadge("[W] GAS", isPressed = keyWPressed || isGasPressed)
                KeyBadge("[S] BRAKE", isPressed = keySPressed || isBrakePressed)
                KeyBadge("[A/D] LEAN", isPressed = keyAPressed || keyDPressed)
                KeyBadge("[SPACE] NITRO", isPressed = keySpacePressed || isNitroPressed)
                KeyBadge("[K] WHEELIE", isPressed = keyKPressed || isWheelieActive)
            }
        }

        // 9. BOTTOM TOUCH CONTROLS (for phone/tablet or mouse click)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Steering / Lean Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SteerButton(
                    text = "◀ LEAN L",
                    tag = "lean_left_btn",
                    onClick = {
                        targetLaneX = when {
                            targetLaneX > 0.7f -> 0.625f
                            targetLaneX > 0.45f -> 0.375f
                            else -> 0.125f
                        }
                    }
                )
                SteerButton(
                    text = "LEAN R ▶",
                    tag = "lean_right_btn",
                    onClick = {
                        targetLaneX = when {
                            targetLaneX < 0.25f -> 0.375f
                            targetLaneX < 0.50f -> 0.625f
                            else -> 0.875f
                        }
                    }
                )
            }

            // Actions: Wheelie Stunt, Brake, Nitro, Gas
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                // Wheelie Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .background(if (isWheelieActive) SpeedGold else Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .border(2.dp, SpeedGold, RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isWheelieActive = true
                                    BikeAudioSystem.playWheelie(context)
                                    tryAwaitRelease()
                                    isWheelieActive = false
                                }
                            )
                        }
                        .testTag("wheelie_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏍️", fontSize = 16.sp)
                        Text("WHEELIE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = if (isWheelieActive) Color.Black else SpeedGold)
                    }
                }

                // Brake Button
                PedalButton(
                    label = "BRAKE",
                    color = RacingRed,
                    size = 54.dp,
                    tag = "bike_brake",
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
                                        BikeAudioSystem.playNitro(context)
                                    }
                                    tryAwaitRelease()
                                    isNitroPressed = false
                                }
                            )
                        }
                        .testTag("bike_nitro_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 18.sp)
                        Text("NITRO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                // Gas Button
                PedalButton(
                    label = "GAS",
                    color = NeonGreen,
                    size = 64.dp,
                    tag = "bike_gas",
                    onPressedChange = { isGasPressed = it }
                )
            }
        }

        // 10. GAME OVER MODAL
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
                        text = "💥 WIPEOUT!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = RacingRed
                    )
                    Text(
                        text = "High-speed superbike run terminated",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SCORE", fontSize = 10.sp, color = Color.Gray)
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

                    Spacer(Modifier.height(14.dp))
                    Text("Near Miss Overtakes: $nearMissCount  •  Distance: ${(distanceMeters / 1000f).format(1)} km", fontSize = 11.sp, color = Color.LightGray)

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onGoToGarage,
                            modifier = Modifier.weight(1f).testTag("bike_garage_btn")
                        ) {
                            Icon(Icons.Default.TwoWheeler, contentDescription = "Garage", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Garage", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                lives = 3
                                speedKmh = 110f
                                topSpeedReached = 110f
                                score = 0
                                coinsInRun = 0
                                distanceMeters = 0f
                                nitroTank = 90f
                                trafficCars.clear()
                                roadPickups.clear()
                                isGameOver = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            modifier = Modifier.weight(1f).testTag("bike_restart_btn")
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = "Restart", modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Ride Again", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KeyBadge(text: String, isPressed: Boolean) {
    Surface(
        color = if (isPressed) NitroGlow else Color(0xFF1E293B),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPressed) Color.White else Color(0xFF334155))
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPressed) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}
