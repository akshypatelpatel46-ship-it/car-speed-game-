package com.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RubyAccent
import com.example.ui.theme.SkyBlue
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpriteSheetStudioScreen(
    modifier: Modifier = Modifier
) {
    var selectedAction by remember { mutableStateOf(KnightAction.IDLE) }
    var selectedDirection by remember { mutableStateOf(Direction.DOWN) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentFrame by remember { mutableIntStateOf(0) }
    var fps by remember { mutableFloatStateOf(8f) }
    var showGrid by remember { mutableStateOf(true) }
    var zoomLevel by remember { mutableFloatStateOf(2.5f) }
    var slashProgress by remember { mutableFloatStateOf(0f) }

    // Frame loops
    val totalFrames = when (selectedAction) {
        KnightAction.IDLE -> 4
        KnightAction.RUNNING -> 4
        KnightAction.SWORD_SWING -> 6
        KnightAction.VICTORY -> 2
    }

    LaunchedEffect(isPlaying, fps, selectedAction) {
        while (isPlaying) {
            val delayMs = (1000f / fps).toLong().coerceAtLeast(30L)
            delay(delayMs)
            currentFrame = (currentFrame + 1) % totalFrames
            if (selectedAction == KnightAction.SWORD_SWING) {
                slashProgress = currentFrame / (totalFrames.toFloat())
            } else {
                slashProgress = 0f
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "🛡️ 16-Bit Knight Sprite Studio",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
                Text(
                    text = "Interactive animation previewer & sprite sheet inspector for medieval knight character",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }

        // 1. Live Interactive Animation Previewer Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE ANIMATION PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SkyBlue,
                            letterSpacing = 1.sp
                        )
                        // Zoom & Grid toggles
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showGrid = !showGrid },
                                modifier = Modifier.size(32.dp).testTag("toggle_grid_button")
                            ) {
                                Icon(
                                    Icons.Default.GridOn,
                                    contentDescription = "Grid",
                                    tint = if (showGrid) GoldPrimary else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    zoomLevel = if (zoomLevel >= 3.5f) 1.5f else zoomLevel + 1.0f
                                },
                                modifier = Modifier.size(32.dp).testTag("zoom_sprite_button")
                            ) {
                                Icon(
                                    Icons.Default.ZoomIn,
                                    contentDescription = "Zoom",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Center Viewport for Knight
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF131A26))
                            .border(1.dp, Color(0xFF26354D), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Optional Pixel checkerboard / grid
                        if (showGrid) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val gridSize = 20.dp.toPx()
                                for (x in 0..(size.width / gridSize).toInt()) {
                                    drawLine(
                                        color = Color(0x18FFFFFF),
                                        start = androidx.compose.ui.geometry.Offset(x * gridSize, 0f),
                                        end = androidx.compose.ui.geometry.Offset(x * gridSize, size.height)
                                    )
                                }
                                for (y in 0..(size.height / gridSize).toInt()) {
                                    drawLine(
                                        color = Color(0x18FFFFFF),
                                        start = androidx.compose.ui.geometry.Offset(0f, y * gridSize),
                                        end = androidx.compose.ui.geometry.Offset(size.width, y * gridSize)
                                    )
                                }
                            }
                        }

                        // Knight Character Rendered at dynamic zoom
                        KnightSpriteCanvas(
                            action = selectedAction,
                            direction = selectedDirection,
                            frame = currentFrame,
                            slashProgress = slashProgress,
                            modifier = Modifier.size((48 * zoomLevel).dp)
                        )

                        // Frame badge
                        Surface(
                            color = Color(0xCC000000),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Frame: ${currentFrame + 1}/$totalFrames  |  ${zoomLevel}x Zoom",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Animation Selector Chips
                    Text("Select Animation Sequence:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val actions = listOf(
                            KnightAction.IDLE to "🧘 Idle",
                            KnightAction.RUNNING to "🏃 Running",
                            KnightAction.SWORD_SWING to "⚔️ Sword Swing"
                        )
                        actions.forEach { (action, label) ->
                            FilterChip(
                                selected = selectedAction == action,
                                onClick = {
                                    selectedAction = action
                                    currentFrame = 0
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.testTag("action_chip_${action.name}")
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Direction Selector Chips
                    Text("Facing Direction:", fontSize = 11.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val dirs = listOf(
                            Direction.DOWN to "Down (Front)",
                            Direction.RIGHT to "Right",
                            Direction.LEFT to "Left",
                            Direction.UP to "Up (Back)"
                        )
                        dirs.forEach { (dir, label) ->
                            FilterChip(
                                selected = selectedDirection == dir,
                                onClick = { selectedDirection = dir },
                                label = { Text(label, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SkyBlue,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Playback & FPS Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (isPlaying) RubyAccent else EmeraldGreen, CircleShape)
                                    .testTag("play_pause_button")
                            ) {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    isPlaying = false
                                    currentFrame = (currentFrame + 1) % totalFrames
                                    if (selectedAction == KnightAction.SWORD_SWING) {
                                        slashProgress = currentFrame / (totalFrames.toFloat())
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Step", tint = Color.White)
                            }
                        }

                        // FPS Slider
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f).padding(start = 16.dp)
                        ) {
                            Text("${fps.toInt()} FPS", fontSize = 11.sp, color = Color.White, modifier = Modifier.width(44.dp))
                            Slider(
                                value = fps,
                                onValueChange = { fps = it },
                                valueRange = 3f..20f,
                                steps = 16,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldPrimary,
                                    activeTrackColor = GoldPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Full 16-Bit Sprite Sheet Artwork Image
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "16-BIT KNIGHT SPRITE SHEET ASSET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Full generated sprite sheet grid with idle, running, and sword swing frames",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_knight_spritesheet),
                            contentDescription = "Knight Sprite Sheet",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // 3. 16-Bit Color Palette Breakdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "16-BIT COLOR PALETTE SPECIFICATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SkyBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))

                    val palette = listOf(
                        Pair(Color(0xFFD4D8E2), "Steel Light"),
                        Pair(Color(0xFF94A3B8), "Armor Base"),
                        Pair(Color(0xFF475569), "Armor Dark"),
                        Pair(Color(0xFF1E293B), "Deep Outline"),
                        Pair(Color(0xFFF59E0B), "Gold Crest"),
                        Pair(Color(0xFFFDE68A), "Glint Amber"),
                        Pair(Color(0xFFEF4444), "Ruby Plume"),
                        Pair(Color(0xFF991B1B), "Crimson Shade"),
                        Pair(Color(0xFF38BDF8), "Visor Glow"),
                        Pair(Color(0xFF67E8F9), "Blade Aura"),
                        Pair(Color(0xFF1E3A8A), "Shield Blue"),
                        Pair(Color(0xFF78350F), "Leather Strap")
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        palette.forEach { (color, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0xFF111827), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(color, RoundedCornerShape(3.dp))
                                        .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(3.dp))
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(label, fontSize = 10.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
