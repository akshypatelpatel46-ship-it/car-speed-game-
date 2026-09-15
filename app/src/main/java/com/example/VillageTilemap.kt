package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

enum class TileType(
    val title: String,
    val isSolid: Boolean,
    val isCuttable: Boolean = false,
    val isInteractive: Boolean = false
) {
    GRASS("Meadow Grass", false),
    FLOWERS("Flowerbed", false),
    TALL_GRASS("Tall Grass", false, isCuttable = true),
    STONE_PATH("Cobblestone Path", false),
    HOUSE_WALL("Wooden Wall", true),
    HOUSE_ROOF("Shingle Roof", true),
    HOUSE_DOOR("Cottage Door", true, isInteractive = true),
    HOUSE_WINDOW("Warm Window", true),
    TREE_OAK("Oak Canopy", true),
    TREE_PINE("Pine Tree", true),
    WATER_POND("Village Pond", true),
    WELL("Village Well", true, isInteractive = true),
    TRAINING_DUMMY("Training Dummy", true, isInteractive = true),
    LANTERN_POST("Lantern Post", true),
    WOODEN_FENCE("Wooden Fence", true)
}

data class NpcEntity(
    val id: String,
    val name: String,
    val role: String,
    val gridX: Int,
    val gridY: Int,
    val dialogues: List<String>,
    val color: Color
)

data class FloatingEffect(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val color: Color,
    val createdAt: Long = System.currentTimeMillis()
)

data class CutGrassEntity(
    val gridX: Int,
    val gridY: Int,
    var cutAt: Long = 0L
)

enum class VillageTimeOfDay(val label: String, val overlayColor: Color) {
    DAY("Daytime", Color(0x00000000)),
    SUNSET("Sunset Glow", Color(0x35F97316)),
    NIGHT("Lantern Night", Color(0x550F172A))
}

object DefaultVillageMap {
    const val MAP_COLS = 16
    const val MAP_ROWS = 14

    // Initial Cozy Village Map Layout (16x14)
    val DEFAULT_TILES = arrayOf(
        // Row 0: Northern Tree Line
        arrayOf(TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_PINE),
        // Row 1: Cottage 1 & Cottage 2 Roofs
        arrayOf(TileType.TREE_PINE, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.GRASS, TileType.GRASS, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.GRASS, TileType.TREE_OAK, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.TREE_PINE, TileType.TREE_PINE),
        // Row 2: Cottage 1 & Cottage 2 Walls
        arrayOf(TileType.TREE_PINE, TileType.HOUSE_WINDOW, TileType.HOUSE_DOOR, TileType.HOUSE_WINDOW, TileType.GRASS, TileType.GRASS, TileType.HOUSE_WINDOW, TileType.HOUSE_DOOR, TileType.HOUSE_WINDOW, TileType.GRASS, TileType.TREE_OAK, TileType.HOUSE_WINDOW, TileType.HOUSE_DOOR, TileType.HOUSE_WINDOW, TileType.TREE_PINE, TileType.TREE_PINE),
        // Row 3: Stone path connecting doorways
        arrayOf(TileType.GRASS, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.GRASS, TileType.TREE_PINE),
        // Row 4: Path splits to village center
        arrayOf(TileType.GRASS, TileType.LANTERN_POST, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.LANTERN_POST, TileType.GRASS, TileType.TREE_OAK),
        // Row 5: Training yard on left, Well in center, Pond on right
        arrayOf(TileType.WOODEN_FENCE, TileType.TRAINING_DUMMY, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.WELL, TileType.STONE_PATH, TileType.GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.WATER_POND, TileType.WATER_POND, TileType.TREE_OAK),
        // Row 6: Village Square Plaza
        arrayOf(TileType.WOODEN_FENCE, TileType.GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.GRASS, TileType.WATER_POND, TileType.WATER_POND, TileType.TREE_OAK),
        // Row 7: Flowerbeds & benches
        arrayOf(TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.WATER_POND, TileType.FLOWERS, TileType.TREE_PINE),
        // Row 8: Tall grass meadow south
        arrayOf(TileType.GRASS, TileType.TALL_GRASS, TileType.TALL_GRASS, TileType.STONE_PATH, TileType.TALL_GRASS, TileType.TALL_GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.TALL_GRASS, TileType.TALL_GRASS, TileType.STONE_PATH, TileType.TALL_GRASS, TileType.TALL_GRASS, TileType.GRASS, TileType.TREE_PINE),
        // Row 9: South Cottage & Garden
        arrayOf(TileType.TREE_PINE, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.HOUSE_ROOF, TileType.GRASS, TileType.GRASS, TileType.LANTERN_POST, TileType.STONE_PATH, TileType.LANTERN_POST, TileType.GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_PINE, TileType.TREE_PINE),
        // Row 10: South Cottage walls
        arrayOf(TileType.TREE_PINE, TileType.HOUSE_WINDOW, TileType.HOUSE_DOOR, TileType.HOUSE_WINDOW, TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.FLOWERS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.TREE_OAK, TileType.TREE_PINE, TileType.TREE_PINE),
        // Row 11: South exit path
        arrayOf(TileType.GRASS, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.GRASS, TileType.GRASS, TileType.STONE_PATH, TileType.STONE_PATH, TileType.STONE_PATH, TileType.GRASS, TileType.TREE_PINE),
        // Row 12: South meadow
        arrayOf(TileType.TREE_OAK, TileType.GRASS, TileType.TALL_GRASS, TileType.GRASS, TileType.GRASS, TileType.TREE_OAK, TileType.GRASS, TileType.STONE_PATH, TileType.GRASS, TileType.TREE_OAK, TileType.GRASS, TileType.GRASS, TileType.TALL_GRASS, TileType.GRASS, TileType.TREE_OAK, TileType.TREE_PINE),
        // Row 13: Border forest
        arrayOf(TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_PINE, TileType.TREE_PINE, TileType.STONE_PATH, TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_OAK, TileType.TREE_PINE, TileType.TREE_PINE, TileType.TREE_PINE)
    )

    val NPCS = listOf(
        NpcEntity(
            id = "elder",
            name = "Elder Rowan",
            role = "Village Elder",
            gridX = 6,
            gridY = 3,
            dialogues = listOf(
                "Greetings, brave Knight! Welcome to Oakhaven.",
                "Our cozy village thrives under your watchful eye.",
                "Feel free to practice your sword swings on the dummy in the west yard!"
            ),
            color = Color(0xFF60A5FA)
        ),
        NpcEntity(
            id = "blacksmith",
            name = "Bryan",
            role = "Blacksmith",
            gridX = 10,
            gridY = 3,
            dialogues = listOf(
                "Well met! That 16-bit broadsword has excellent temper.",
                "Keep it sharp! Try cutting the tall grass down south for hidden coins.",
                "The village well restores both stamina and spirit!"
            ),
            color = Color(0xFFF97316)
        ),
        NpcEntity(
            id = "florist",
            name = "Lily",
            role = "Florist",
            gridX = 8,
            gridY = 7,
            dialogues = listOf(
                "Aren't the village blossoms radiant today?",
                "The cobblestone paths keep the cozy village tidy and clean.",
                "Watch the sunset over the pond, it's truly magical!"
            ),
            color = Color(0xFFEC4899)
        )
    )
}

/**
 * Procedural 16-bit pixel art tile renderer for each tile type.
 */
fun DrawScope.drawPixelTile(
    tile: TileType,
    x: Float,
    y: Float,
    size: Float,
    isCut: Boolean = false,
    dummyWobble: Float = 0f
) {
    val px = size / 16f

    when (tile) {
        TileType.GRASS -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            // pixel grass blades
            drawRect(Color(0xFF22C55E), Offset(x + 3 * px, y + 4 * px), Size(2 * px, 4 * px))
            drawRect(Color(0xFF16A34A), Offset(x + 10 * px, y + 9 * px), Size(2 * px, 4 * px))
            drawRect(Color(0xFF86EFAC), Offset(x + 8 * px, y + 3 * px), Size(1 * px, 2 * px))
        }

        TileType.FLOWERS -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF22C55E), Offset(x + 4 * px, y + 5 * px), Size(8 * px, 6 * px))
            // red & yellow blossoms
            drawCircle(Color(0xFFEF4444), radius = 2.5f * px, center = Offset(x + 5 * px, y + 6 * px))
            drawCircle(Color(0xFFFDE047), radius = 1.2f * px, center = Offset(x + 5 * px, y + 6 * px))
            drawCircle(Color(0xFFF59E0B), radius = 2.5f * px, center = Offset(x + 11 * px, y + 10 * px))
            drawCircle(Color(0xFFFFFFFF), radius = 1.2f * px, center = Offset(x + 11 * px, y + 10 * px))
        }

        TileType.TALL_GRASS -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            if (isCut) {
                // Cut grass tuft
                drawRect(Color(0xFF15803D), Offset(x + 4 * px, y + 10 * px), Size(8 * px, 3 * px))
                drawRect(Color(0xFF86EFAC), Offset(x + 6 * px, y + 8 * px), Size(4 * px, 2 * px))
            } else {
                // Dense tall grass clump
                drawRect(Color(0xFF16A34A), Offset(x + 2 * px, y + 3 * px), Size(3 * px, 11 * px))
                drawRect(Color(0xFF22C55E), Offset(x + 6 * px, y + 1 * px), Size(4 * px, 13 * px))
                drawRect(Color(0xFF15803D), Offset(x + 11 * px, y + 4 * px), Size(3 * px, 10 * px))
                drawRect(Color(0xFF86EFAC), Offset(x + 7 * px, y + 2 * px), Size(2 * px, 4 * px))
            }
        }

        TileType.STONE_PATH -> {
            drawRect(Color(0xFF64748B), Offset(x, y), Size(size, size))
            // Cobblestones
            val pCol1 = Color(0xFF94A3B8)
            val pCol2 = Color(0xFF475569)
            drawRect(pCol1, Offset(x + 1 * px, y + 1 * px), Size(6 * px, 5 * px))
            drawRect(pCol2, Offset(x + 8 * px, y + 1 * px), Size(7 * px, 6 * px))
            drawRect(pCol2, Offset(x + 1 * px, y + 8 * px), Size(7 * px, 7 * px))
            drawRect(pCol1, Offset(x + 9 * px, y + 9 * px), Size(6 * px, 6 * px))
        }

        TileType.HOUSE_ROOF -> {
            drawRect(Color(0xFF7C2D12), Offset(x, y), Size(size, size))
            drawRect(Color(0xFFB45309), Offset(x, y), Size(size, 4 * px))
            drawRect(Color(0xFF9A3412), Offset(x, y + 5 * px), Size(size, 4 * px))
            drawRect(Color(0xFFB45309), Offset(x, y + 10 * px), Size(size, 4 * px))
            // Shingle lines
            drawLine(Color(0xFF451A03), Offset(x + 5 * px, y), Offset(x + 5 * px, y + 4 * px), 1.5f * px)
            drawLine(Color(0xFF451A03), Offset(x + 11 * px, y + 5 * px), Offset(x + 11 * px, y + 9 * px), 1.5f * px)
        }

        TileType.HOUSE_WALL -> {
            drawRect(Color(0xFFD97706), Offset(x, y), Size(size, size))
            // Timber framing
            drawRect(Color(0xFF78350F), Offset(x, y), Size(size, 2 * px))
            drawRect(Color(0xFF78350F), Offset(x, y + 14 * px), Size(size, 2 * px))
            drawRect(Color(0xFF78350F), Offset(x, y), Size(2 * px, size))
            drawRect(Color(0xFF78350F), Offset(x + 14 * px, y), Size(2 * px, size))
            drawRect(Color(0xFFFDE68A), Offset(x + 3 * px, y + 3 * px), Size(10 * px, 10 * px))
        }

        TileType.HOUSE_DOOR -> {
            drawRect(Color(0xFFD97706), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF78350F), Offset(x + 3 * px, y + 2 * px), Size(10 * px, 14 * px))
            drawRect(Color(0xFF92400E), Offset(x + 4 * px, y + 3 * px), Size(8 * px, 13 * px))
            // Golden door handle
            drawCircle(Color(0xFFFBBF24), radius = 1.5f * px, center = Offset(x + 10 * px, y + 9 * px))
        }

        TileType.HOUSE_WINDOW -> {
            drawRect(Color(0xFFD97706), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF78350F), Offset(x + 2 * px, y + 2 * px), Size(12 * px, 12 * px))
            drawRect(Color(0xFFFEF08A), Offset(x + 4 * px, y + 4 * px), Size(8 * px, 8 * px))
            // Cross pane
            drawLine(Color(0xFF78350F), Offset(x + 8 * px, y + 4 * px), Offset(x + 8 * px, y + 12 * px), 1.5f * px)
            drawLine(Color(0xFF78350F), Offset(x + 4 * px, y + 8 * px), Offset(x + 12 * px, y + 8 * px), 1.5f * px)
        }

        TileType.TREE_OAK -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            // Trunk
            drawRect(Color(0xFF78350F), Offset(x + 6 * px, y + 10 * px), Size(4 * px, 6 * px))
            // Oak foliage circle
            drawCircle(Color(0xFF15803D), radius = 6.5f * px, center = Offset(x + 8 * px, y + 7 * px))
            drawCircle(Color(0xFF22C55E), radius = 5.5f * px, center = Offset(x + 7 * px, y + 6 * px))
            drawCircle(Color(0xFF86EFAC), radius = 2.5f * px, center = Offset(x + 6 * px, y + 5 * px))
        }

        TileType.TREE_PINE -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            // Trunk
            drawRect(Color(0xFF5A2A18), Offset(x + 7 * px, y + 11 * px), Size(2 * px, 5 * px))
            // Pine tiers
            val path = Path().apply {
                moveTo(x + 8 * px, y + 1 * px)
                lineTo(x + 14 * px, y + 11 * px)
                lineTo(x + 2 * px, y + 11 * px)
                close()
            }
            drawPath(path, Color(0xFF065F46))
            val path2 = Path().apply {
                moveTo(x + 8 * px, y + 2 * px)
                lineTo(x + 12 * px, y + 8 * px)
                lineTo(x + 4 * px, y + 8 * px)
                close()
            }
            drawPath(path2, Color(0xFF059669))
        }

        TileType.WATER_POND -> {
            drawRect(Color(0xFF0284C7), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF38BDF8), Offset(x + 2 * px, y + 3 * px), Size(6 * px, 3 * px))
            drawRect(Color(0xFFBAE6FD), Offset(x + 9 * px, y + 8 * px), Size(4 * px, 2 * px))
            // Lily pad
            drawCircle(Color(0xFF15803D), radius = 2f * px, center = Offset(x + 5 * px, y + 11 * px))
            drawCircle(Color(0xFFF472B6), radius = 1f * px, center = Offset(x + 5 * px, y + 11 * px))
        }

        TileType.WELL -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            // Circular stone rim
            drawCircle(Color(0xFF475569), radius = 6 * px, center = Offset(x + 8 * px, y + 9 * px))
            drawCircle(Color(0xFF94A3B8), radius = 5 * px, center = Offset(x + 8 * px, y + 9 * px))
            drawCircle(Color(0xFF0284C7), radius = 3.5f * px, center = Offset(x + 8 * px, y + 9 * px))
            // Wooden roof
            drawRect(Color(0xFF78350F), Offset(x + 3 * px, y + 2 * px), Size(10 * px, 3 * px))
            drawLine(Color(0xFF78350F), Offset(x + 4 * px, y + 5 * px), Offset(x + 4 * px, y + 9 * px), 2 * px)
            drawLine(Color(0xFF78350F), Offset(x + 12 * px, y + 5 * px), Offset(x + 12 * px, y + 9 * px), 2 * px)
        }

        TileType.TRAINING_DUMMY -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            val wobbleX = dummyWobble * 4 * px
            // Pole
            drawLine(Color(0xFF78350F), Offset(x + 8 * px, y + 14 * px), Offset(x + 8 * px + wobbleX, y + 4 * px), 2.5f * px)
            // Straw Body
            drawRect(Color(0xFFFBBF24), Offset(x + 5 * px + wobbleX, y + 6 * px), Size(6 * px, 7 * px))
            drawRect(Color(0xFFD97706), Offset(x + 4 * px + wobbleX, y + 5 * px), Size(8 * px, 2 * px))
            // Head
            drawCircle(Color(0xFFFDE68A), radius = 3 * px, center = Offset(x + 8 * px + wobbleX, y + 3 * px))
            // Cross arms
            drawLine(Color(0xFF78350F), Offset(x + 1 * px + wobbleX, y + 7 * px), Offset(x + 15 * px + wobbleX, y + 7 * px), 2 * px)
        }

        TileType.LANTERN_POST -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            // Post
            drawLine(Color(0xFF334155), Offset(x + 8 * px, y + 15 * px), Offset(x + 8 * px, y + 3 * px), 2 * px)
            // Glowing lantern
            drawCircle(Color(0xFFFBBF24), radius = 3 * px, center = Offset(x + 8 * px, y + 4 * px))
            drawCircle(Color(0xFFFEF08A), radius = 1.5f * px, center = Offset(x + 8 * px, y + 4 * px))
        }

        TileType.WOODEN_FENCE -> {
            drawRect(Color(0xFF4ADE80), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF92400E), Offset(x, y + 5 * px), Size(size, 2.5f * px))
            drawRect(Color(0xFF92400E), Offset(x, y + 11 * px), Size(size, 2.5f * px))
            drawRect(Color(0xFF78350F), Offset(x + 2 * px, y + 3 * px), Size(2.5f * px, 11 * px))
            drawRect(Color(0xFF78350F), Offset(x + 10 * px, y + 3 * px), Size(2.5f * px, 11 * px))
        }
    }
}
