package com.example

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RubyAccent
import com.example.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun GameScreen(
    customTiles: Array<Array<TileType>>? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Map tiles
    val tiles = remember(customTiles) {
        customTiles ?: DefaultVillageMap.DEFAULT_TILES
    }
    val rows = tiles.size
    val cols = tiles[0].size

    // Knight position (Grid coordinates)
    var knightX by remember { mutableFloatStateOf(7.0f) }
    var knightY by remember { mutableFloatStateOf(6.0f) }
    var knightDirection by remember { mutableStateOf(Direction.DOWN) }
    var knightAction by remember { mutableStateOf(KnightAction.IDLE) }
    var knightFrame by remember { mutableIntStateOf(0) }
    var slashProgress by remember { mutableFloatStateOf(0f) }

    // Knight Stats
    var stats by remember { mutableStateOf(KnightStats()) }
    var timeOfDay by remember { mutableStateOf(VillageTimeOfDay.DAY) }
    var isMuted by remember { mutableStateOf(RetroAudioSystem.isMuted()) }

    // Active cut grass records
    val cutGrass = remember { mutableStateListOf<CutGrassEntity>() }

    // Floating text effects (damage, loot)
    val floatingEffects = remember { mutableStateListOf<FloatingEffect>() }

    // Training dummy wobble
    var dummyWobble by remember { mutableFloatStateOf(0f) }

    // Dialogue State
    var activeDialogue by remember { mutableStateOf<Pair<NpcEntity, String>?>(null) }

    // Animation ticker
    LaunchedEffect(knightAction) {
        while (true) {
            val frameDelay = when (knightAction) {
                KnightAction.IDLE -> 500L
                KnightAction.RUNNING -> 120L
                KnightAction.SWORD_SWING -> 60L
                KnightAction.VICTORY -> 300L
            }
            delay(frameDelay)
            knightFrame = (knightFrame + 1) % 4
            if (knightAction == KnightAction.RUNNING && knightFrame % 2 == 0) {
                RetroAudioSystem.playStep(context)
            }
        }
    }

    // Recover stamina gradually
    LaunchedEffect(Unit) {
        while (true) {
            delay(200)
            if (stats.stamina < stats.maxStamina && knightAction != KnightAction.RUNNING) {
                stats = stats.copy(stamina = (stats.stamina + 2f).coerceAtMost(stats.maxStamina))
            }
            // Decay dummy wobble
            if (dummyWobble > 0.05f) {
                dummyWobble *= -0.7f
            } else {
                dummyWobble = 0f
            }
        }
    }

    // Helper to check collision with solid tiles
    fun canMoveTo(newX: Float, newY: Float): Boolean {
        val minX = 0.5f
        val maxX = cols - 1.5f
        val minY = 0.5f
        val maxY = rows - 1.5f
        if (newX < minX || newX > maxX || newY < minY || newY > maxY) return false

        val checkTileX = newX.roundToInt().coerceIn(0, cols - 1)
        val checkTileY = (newY + 0.3f).roundToInt().coerceIn(0, rows - 1)
        val tile = tiles[checkTileY][checkTileX]
        return !tile.isSolid
    }

    // Helper: Perform attack / sword swing
    fun performSwordSwing() {
        if (knightAction == KnightAction.SWORD_SWING) return
        RetroAudioSystem.playSwordSwing(context)
        knightAction = KnightAction.SWORD_SWING

        scope.launch {
            // Animate swing
            for (step in 0..10) {
                slashProgress = step / 10f
                delay(18)
            }

            // Hit detection in front of knight
            val targetX = when (knightDirection) {
                Direction.LEFT -> knightX - 0.9f
                Direction.RIGHT -> knightX + 0.9f
                else -> knightX
            }
            val targetY = when (knightDirection) {
                Direction.UP -> knightY - 0.9f
                Direction.DOWN -> knightY + 0.9f
                else -> knightY
            }

            val tX = targetX.roundToInt().coerceIn(0, cols - 1)
            val tY = targetY.roundToInt().coerceIn(0, rows - 1)
            val hitTile = tiles[tY][tX]

            if (hitTile == TileType.TALL_GRASS) {
                val alreadyCut = cutGrass.any { it.gridX == tX && it.gridY == tY }
                if (!alreadyCut) {
                    cutGrass.add(CutGrassEntity(tX, tY, System.currentTimeMillis()))
                    val coinsFound = Random.nextInt(2, 6)
                    stats = stats.copy(coins = stats.coins + coinsFound)
                    RetroAudioSystem.playCoin(context)
                    floatingEffects.add(
                        FloatingEffect(
                            id = System.currentTimeMillis(),
                            text = "+$coinsFound Gold!",
                            x = tX.toFloat(),
                            y = tY.toFloat(),
                            color = GoldLight
                        )
                    )
                }
            } else if (hitTile == TileType.TRAINING_DUMMY) {
                val isCrit = Random.nextInt(100) < 30
                val dmg = if (isCrit) stats.attackPower * 2 else stats.attackPower + Random.nextInt(-3, 4)
                dummyWobble = if (knightDirection == Direction.LEFT) -1f else 1f
                RetroAudioSystem.playHit(context)
                val newXp = stats.xp + 15
                val levelUp = newXp >= stats.maxXp
                stats = stats.copy(
                    xp = if (levelUp) newXp - stats.maxXp else newXp,
                    level = if (levelUp) stats.level + 1 else stats.level,
                    attackPower = if (levelUp) stats.attackPower + 5 else stats.attackPower
                )
                floatingEffects.add(
                    FloatingEffect(
                        id = System.currentTimeMillis(),
                        text = if (isCrit) "CRIT $dmg!" else "$dmg DMG",
                        x = tX.toFloat(),
                        y = tY.toFloat(),
                        color = if (isCrit) RubyAccent else SkyBlue
                    )
                )
                if (levelUp) {
                    RetroAudioSystem.playHeal(context)
                    floatingEffects.add(
                        FloatingEffect(
                            id = System.currentTimeMillis() + 1,
                            text = "LEVEL UP! Lv.${stats.level}",
                            x = knightX,
                            y = knightY - 0.5f,
                            color = GoldPrimary
                        )
                    )
                }
            }

            delay(60)
            knightAction = KnightAction.IDLE
            slashProgress = 0f
        }
    }

    // Helper: Interact
    fun performInteract() {
        val lookX = when (knightDirection) {
            Direction.LEFT -> knightX - 1.1f
            Direction.RIGHT -> knightX + 1.1f
            else -> knightX
        }
        val lookY = when (knightDirection) {
            Direction.UP -> knightY - 1.1f
            Direction.DOWN -> knightY + 1.1f
            else -> knightY
        }
        val tX = lookX.roundToInt().coerceIn(0, cols - 1)
        val tY = lookY.roundToInt().coerceIn(0, rows - 1)

        // Check NPCs
        val nearbyNpc = DefaultVillageMap.NPCS.find { (it.gridX == tX && it.gridY == tY) || (it.gridX == knightX.roundToInt() && it.gridY == knightY.roundToInt()) }
        if (nearbyNpc != null) {
            val line = nearbyNpc.dialogues.random()
            activeDialogue = Pair(nearbyNpc, line)
            RetroAudioSystem.playTalk(context)
            return
        }

        // Check Well
        if (tiles[tY][tX] == TileType.WELL) {
            RetroAudioSystem.playHeal(context)
            stats = stats.copy(hp = stats.maxHp, stamina = stats.maxStamina)
            floatingEffects.add(
                FloatingEffect(
                    id = System.currentTimeMillis(),
                    text = "Wellspring: Full HP & Stamina!",
                    x = tX.toFloat(),
                    y = tY.toFloat(),
                    color = EmeraldGreen
                )
            )
            return
        }

        // Check Cottage Door
        if (tiles[tY][tX] == TileType.HOUSE_DOOR) {
            RetroAudioSystem.playTalk(context)
            floatingEffects.add(
                FloatingEffect(
                    id = System.currentTimeMillis(),
                    text = "Cozy cottage hearth is warm inside.",
                    x = tX.toFloat(),
                    y = tY.toFloat(),
                    color = GoldLight
                )
            )
            return
        }

        floatingEffects.add(
            FloatingEffect(
                id = System.currentTimeMillis(),
                text = "Nothing to inspect here.",
                x = knightX,
                y = knightY,
                color = Color.LightGray
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
    ) {
        val screenW = maxWidth
        val screenH = maxHeight

        // Dynamic tile sizing centered on viewport
        val tileSizeDp = 44.dp
        val tileSizePx = 44f * 2.5f // Approximate reference scale

        // Calculate Camera Offset to keep Knight centered on screen
        val cameraOffsetX = (screenW / 2) - (tileSizeDp * knightX) - (tileSizeDp / 2)
        val cameraOffsetY = (screenH / 2.2f) - (tileSizeDp * knightY) - (tileSizeDp / 2)

        // 1. GAME WORLD CONTAINER
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(cameraOffsetX.roundToPx(), cameraOffsetY.roundToPx()) }
        ) {
            // Render Tilemap Grid
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val tile = tiles[r][c]
                    val isCut = cutGrass.any { it.gridX == c && it.gridY == r }
                    val wobble = if (tile == TileType.TRAINING_DUMMY) dummyWobble else 0f

                    Box(
                        modifier = Modifier
                            .offset(x = tileSizeDp * c, y = tileSizeDp * r)
                            .size(tileSizeDp)
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                            drawPixelTile(
                                tile = tile,
                                x = 0f,
                                y = 0f,
                                size = size.width,
                                isCut = isCut,
                                dummyWobble = wobble
                            )
                        }
                    }
                }
            }

            // Render NPCs
            DefaultVillageMap.NPCS.forEach { npc ->
                Box(
                    modifier = Modifier
                        .offset(x = tileSizeDp * npc.gridX, y = tileSizeDp * npc.gridY)
                        .size(tileSizeDp)
                        .clickable {
                            activeDialogue = Pair(npc, npc.dialogues.random())
                            RetroAudioSystem.playTalk(context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // NPC Sprite representation
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(34.dp)) {
                        val px = size.width / 16f
                        // Shadow
                        drawOval(Color(0x44000000), Offset(2 * px, 12 * px), androidx.compose.ui.geometry.Size(12 * px, 4 * px))
                        // Robe / Body
                        drawRect(npc.color, Offset(3 * px, 6 * px), androidx.compose.ui.geometry.Size(10 * px, 8 * px))
                        // Face
                        drawRect(Color(0xFFFED7AA), Offset(5 * px, 2 * px), androidx.compose.ui.geometry.Size(6 * px, 5 * px))
                        // Eyes
                        drawRect(Color(0xFF1E293B), Offset(6 * px, 4 * px), androidx.compose.ui.geometry.Size(1 * px, 2 * px))
                        drawRect(Color(0xFF1E293B), Offset(9 * px, 4 * px), androidx.compose.ui.geometry.Size(1 * px, 2 * px))
                    }
                    // NPC Role Indicator Tag
                    Surface(
                        color = Color(0xDD1E293B),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-12).dp)
                    ) {
                        Text(
                            text = npc.name,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            // Render Medieval Knight Character
            Box(
                modifier = Modifier
                    .offset(x = tileSizeDp * knightX, y = tileSizeDp * knightY)
                    .size(tileSizeDp),
                contentAlignment = Alignment.Center
            ) {
                KnightSpriteCanvas(
                    action = knightAction,
                    direction = knightDirection,
                    frame = knightFrame,
                    slashProgress = slashProgress,
                    modifier = Modifier.size(tileSizeDp * 1.35f)
                )
            }

            // Render Floating Effects (Damage & Loot)
            floatingEffects.forEach { effect ->
                val timeAlive = (System.currentTimeMillis() - effect.createdAt)
                val offsetY = -(timeAlive / 35f).dp
                Box(
                    modifier = Modifier
                        .offset(x = tileSizeDp * effect.x, y = tileSizeDp * effect.y + offsetY)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = effect.text,
                        color = effect.color,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .shadow(4.dp, RoundedCornerShape(4.dp))
                            .background(Color(0xCC000000), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // 2. DAY/NIGHT ATMOSPHERE TINT OVERLAY
        if (timeOfDay.overlayColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(timeOfDay.overlayColor)
            )
        }

        // 3. TOP HUD (Health, Stamina, Coins, Time, Location)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character Status Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardSurface.copy(alpha = 0.92f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D56)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚔️ Sir Knight (Lv.${stats.level})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            // Coins Counter
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.MonetizationOn,
                                    contentDescription = "Gold",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    text = "${stats.coins} G",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        // HP Bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "HP",
                                tint = RubyAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            LinearProgressIndicator(
                                progress = { (stats.hp / stats.maxHp.toFloat()).coerceIn(0f, 1f) },
                                color = RubyAccent,
                                trackColor = Color(0xFF3B181E),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${stats.hp}/${stats.maxHp}",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }

                        Spacer(Modifier.height(3.dp))

                        // Stamina Bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = "Stamina",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            LinearProgressIndicator(
                                progress = { (stats.stamina / stats.maxStamina).coerceIn(0f, 1f) },
                                color = EmeraldGreen,
                                trackColor = Color(0xFF143526),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "${stats.stamina.toInt()}%",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Quick Tool Controls (Time of Day & Audio Mute & Reset)
                Row(
                    modifier = Modifier
                        .background(CardSurface.copy(alpha = 0.92f), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFF2E3D56), RoundedCornerShape(10.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            timeOfDay = when (timeOfDay) {
                                VillageTimeOfDay.DAY -> VillageTimeOfDay.SUNSET
                                VillageTimeOfDay.SUNSET -> VillageTimeOfDay.NIGHT
                                VillageTimeOfDay.NIGHT -> VillageTimeOfDay.DAY
                            }
                        },
                        modifier = Modifier.size(36.dp).testTag("toggle_time_button")
                    ) {
                        val icon = when (timeOfDay) {
                            VillageTimeOfDay.DAY -> Icons.Default.WbSunny
                            VillageTimeOfDay.SUNSET -> Icons.Default.WbTwilight
                            VillageTimeOfDay.NIGHT -> Icons.Default.Bedtime
                        }
                        Icon(icon, contentDescription = "Time of Day", tint = GoldLight, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = {
                            isMuted = RetroAudioSystem.toggleMute()
                        },
                        modifier = Modifier.size(36.dp).testTag("toggle_audio_button")
                    ) {
                        Icon(
                            if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Audio",
                            tint = if (isMuted) Color.Gray else SkyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            knightX = 7.0f
                            knightY = 6.0f
                            stats = stats.copy(hp = stats.maxHp)
                        },
                        modifier = Modifier.size(36.dp).testTag("reset_pos_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Pos", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Location banner
            Surface(
                color = Color(0xBB0F172A),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "🏡 Oakhaven Village • Cozy Square (${timeOfDay.label})",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        // 4. DIALOGUE MODAL (when talking to NPCs)
        AnimatedVisibility(
            visible = activeDialogue != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
        ) {
            activeDialogue?.let { (npc, text) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(npc.color, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(npc.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(npc.name, color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(npc.role, color = Color.LightGray, fontSize = 11.sp)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "\"$text\"",
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Surface(
                                color = GoldPrimary,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.clickable { activeDialogue = null }
                            ) {
                                Text(
                                    "Continue [Tap]",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. BOTTOM ON-SCREEN GAME CONTROLS (D-PAD & ACTION BUTTONS)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Virtual 4-Way D-Pad
            DpadController(
                onDirectionPress = { dir ->
                    knightDirection = dir
                    knightAction = KnightAction.RUNNING
                    val stepSize = 0.22f
                    val nextX = when (dir) {
                        Direction.LEFT -> knightX - stepSize
                        Direction.RIGHT -> knightX + stepSize
                        else -> knightX
                    }
                    val nextY = when (dir) {
                        Direction.UP -> knightY - stepSize
                        Direction.DOWN -> knightY + stepSize
                        else -> knightY
                    }
                    if (canMoveTo(nextX, nextY)) {
                        knightX = nextX
                        knightY = nextY
                    }
                },
                onRelease = {
                    if (knightAction == KnightAction.RUNNING) {
                        knightAction = KnightAction.IDLE
                    }
                }
            )

            // Action Buttons (Sword Swing, Sprint, Interact)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Secondary Action: Interact / Talk
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Dash / Sprint
                    ActionButton(
                        label = "DASH",
                        icon = Icons.Default.Bolt,
                        backgroundColor = Color(0xFF0284C7),
                        sizeDp = 48.dp,
                        tag = "dash_button",
                        onClick = {
                            if (stats.stamina >= 15f) {
                                stats = stats.copy(stamina = stats.stamina - 15f)
                                val dashDist = 0.9f
                                val nX = when (knightDirection) {
                                    Direction.LEFT -> knightX - dashDist
                                    Direction.RIGHT -> knightX + dashDist
                                    else -> knightX
                                }
                                val nY = when (knightDirection) {
                                    Direction.UP -> knightY - dashDist
                                    Direction.DOWN -> knightY + dashDist
                                    else -> knightY
                                }
                                if (canMoveTo(nX, nY)) {
                                    knightX = nX
                                    knightY = nY
                                }
                                RetroAudioSystem.playStep(context)
                            }
                        }
                    )

                    // Interact
                    ActionButton(
                        label = "TALK",
                        icon = Icons.Default.Chat,
                        backgroundColor = EmeraldGreen,
                        sizeDp = 48.dp,
                        tag = "interact_button",
                        onClick = { performInteract() }
                    )
                }

                // Primary Action: Sword Swing Attack
                ActionButton(
                    label = "SWORD SWING",
                    icon = null,
                    customEmoji = "⚔️",
                    backgroundColor = RubyAccent,
                    sizeDp = 64.dp,
                    tag = "attack_button",
                    onClick = { performSwordSwing() }
                )
            }
        }
    }
}

/**
 * Responsive Retro D-Pad Controller for touch.
 */
@Composable
fun DpadController(
    onDirectionPress: (Direction) -> Unit,
    onRelease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var activeDirection by remember { mutableStateOf<Direction?>(null) }

    LaunchedEffect(activeDirection) {
        while (activeDirection != null) {
            onDirectionPress(activeDirection!!)
            delay(50)
        }
    }

    Box(
        modifier = modifier
            .size(130.dp)
            .background(Color(0xBB1E293B), CircleShape)
            .border(2.dp, Color(0xFF334155), CircleShape)
    ) {
        // UP
        DpadButton(
            text = "▲",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(42.dp),
            onPress = { activeDirection = Direction.UP },
            onRelease = { activeDirection = null; onRelease() }
        )
        // DOWN
        DpadButton(
            text = "▼",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(42.dp),
            onPress = { activeDirection = Direction.DOWN },
            onRelease = { activeDirection = null; onRelease() }
        )
        // LEFT
        DpadButton(
            text = "◀",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(42.dp),
            onPress = { activeDirection = Direction.LEFT },
            onRelease = { activeDirection = null; onRelease() }
        )
        // RIGHT
        DpadButton(
            text = "▶",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp),
            onPress = { activeDirection = Direction.RIGHT },
            onRelease = { activeDirection = null; onRelease() }
        )
        // Center Core
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp)
                .background(Color(0xFF334155), CircleShape)
        )
    }
}

@Composable
fun DpadButton(
    text: String,
    modifier: Modifier = Modifier,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    customEmoji: String? = null,
    backgroundColor: Color,
    sizeDp: androidx.compose.ui.unit.Dp,
    tag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(sizeDp)
            .shadow(6.dp, CircleShape)
            .background(backgroundColor, CircleShape)
            .border(2.dp, Color(0xFFFFFFFF).copy(alpha = 0.35f), CircleShape)
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (customEmoji != null) {
                Text(customEmoji, fontSize = (sizeDp.value * 0.34f).sp)
            } else if (icon != null) {
                Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(sizeDp * 0.45f))
            }
            Text(
                text = label,
                fontSize = (sizeDp.value * 0.14f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
