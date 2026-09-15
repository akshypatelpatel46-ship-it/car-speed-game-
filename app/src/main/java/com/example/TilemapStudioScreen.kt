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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SkyBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TilemapStudioScreen(
    onApplyCustomMap: (Array<Array<TileType>>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPaintTile by remember { mutableStateOf(TileType.STONE_PATH) }

    // Sandbox editable 8x8 grid
    val initialSandbox = remember {
        Array(8) { r ->
            Array(8) { c ->
                when {
                    r == 0 || r == 7 || c == 0 || c == 7 -> TileType.TREE_OAK
                    r == 4 -> TileType.STONE_PATH
                    c == 4 -> TileType.STONE_PATH
                    r == 2 && c == 2 -> TileType.HOUSE_ROOF
                    r == 3 && c == 2 -> TileType.HOUSE_DOOR
                    r == 5 && c == 5 -> TileType.WELL
                    r == 2 && c == 5 -> TileType.FLOWERS
                    else -> TileType.GRASS
                }
            }
        }
    }

    var sandboxGrid by remember { mutableStateOf(initialSandbox) }
    var appliedNotification by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F19))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "🏡 Cozy Village Tilemap Studio",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
                Text(
                    text = "Top-down 2D tile set inspector, digital pixel art tileset & interactive village level painter",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }

        // 1. Generated Cozy Village Map Artwork
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "COZY RPG VILLAGE OVERVIEW (16-BIT)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SkyBlue,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Complete rendered top-down village landscape with cottages, trees, and cobblestones",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_village_map),
                            contentDescription = "Village Map",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // 2. Tilemap Modular Tileset Sprite Sheet Asset
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MODULAR TILESET SPRITE SHEET ASSET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tile grid containing grass, stone paths, wooden houses, roofs, and trees",
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
                            painter = painterResource(id = R.drawable.img_village_tileset),
                            contentDescription = "Village Tileset",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // 3. Tile Palette & Sandbox Village Builder
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
                        Column {
                            Text(
                                text = "INTERACTIVE VILLAGE LEVEL PAINTER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreen,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Pick a tile from the palette below and tap to paint the grid!",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Tile Palette selection
                    Text("Tile Palette (Selected: ${selectedPaintTile.title}):", fontSize = 11.sp, color = GoldLight)
                    Spacer(Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TileType.values().forEach { tile ->
                            val isSelected = selectedPaintTile == tile
                            Surface(
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.25f) else Color(0xFF131A26),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) GoldPrimary else Color(0xFF26354D)
                                ),
                                modifier = Modifier
                                    .clickable { selectedPaintTile = tile }
                                    .testTag("palette_tile_${tile.name}")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Box(modifier = Modifier.size(24.dp)) {
                                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawPixelTile(tile, 0f, 0f, size.width)
                                        }
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = tile.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) GoldLight else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // 8x8 Interactive Painting Sandbox Grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (r in 0 until 8) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    for (c in 0 until 8) {
                                        val tile = sandboxGrid[r][c]
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .clickable {
                                                    val newGrid = sandboxGrid.map { it.clone() }.toTypedArray()
                                                    newGrid[r][c] = selectedPaintTile
                                                    sandboxGrid = newGrid
                                                    appliedNotification = false
                                                }
                                        ) {
                                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                                drawPixelTile(tile, 0f, 0f, size.width)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                sandboxGrid = initialSandbox
                                appliedNotification = false
                            },
                            modifier = Modifier.weight(1f).testTag("reset_sandbox_button")
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reset Preset", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                // Scale 8x8 sandbox to 16x14 for the playable game
                                val expanded = Array(14) { r ->
                                    Array(16) { c ->
                                        val sr = (r * 8) / 14
                                        val sc = (c * 8) / 16
                                        sandboxGrid[sr][sc]
                                    }
                                }
                                onApplyCustomMap(expanded)
                                appliedNotification = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.weight(1f).testTag("apply_map_button")
                        ) {
                            Icon(
                                if (appliedNotification) Icons.Default.Check else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(if (appliedNotification) "Map Loaded!" else "Play Layout", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
