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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class Direction {
    DOWN, UP, LEFT, RIGHT
}

enum class KnightAction {
    IDLE, RUNNING, SWORD_SWING, VICTORY
}

data class KnightStats(
    val hp: Int = 100,
    val maxHp: Int = 100,
    val stamina: Float = 100f,
    val maxStamina: Float = 100f,
    val coins: Int = 24,
    val level: Int = 3,
    val xp: Int = 180,
    val maxXp: Int = 300,
    val attackPower: Int = 25
)

/**
 * Procedural 16-bit retro pixel-perfect Knight renderer.
 * Draws the medieval knight character for Idle, Running, and Sword Swing animations.
 */
@Composable
fun KnightSpriteCanvas(
    action: KnightAction,
    direction: Direction,
    frame: Int,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    slashProgress: Float = 0f
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pixelSize = (w / 32f).coerceAtLeast(1f)

        // Center knight inside the canvas
        val originX = w / 2f
        val originY = h / 2f

        drawKnightCharacter(
            ox = originX,
            oy = originY,
            px = pixelSize,
            action = action,
            direction = direction,
            frame = frame,
            slashProgress = slashProgress
        )
    }
}

fun DrawScope.drawKnightCharacter(
    ox: Float,
    oy: Float,
    px: Float,
    action: KnightAction,
    direction: Direction,
    frame: Int,
    slashProgress: Float = 0f
) {
    // 16-bit Palette
    val colArmorLight = Color(0xFFD4D8E2)
    val colArmorBase = Color(0xFF94A3B8)
    val colArmorDark = Color(0xFF475569)
    val colArmorOutline = Color(0xFF1E293B)

    val colGoldTrim = Color(0xFFF59E0B)
    val colGoldHighlight = Color(0xFFFDE68A)

    val colPlumeRed = Color(0xFFEF4444)
    val colPlumeDark = Color(0xFF991B1B)

    val colVisorGlow = Color(0xFF38BDF8)
    val colLeather = Color(0xFF78350F)
    val colBladeSteel = Color(0xFFE2E8F0)
    val colBladeGlow = Color(0xFF67E8F9)
    val colSlashArc = Color(0xAA38BDF8)

    // Shadow on ground
    drawOval(
        color = Color(0x44000000),
        topLeft = Offset(ox - 10 * px, oy + 12 * px),
        size = Size(20 * px, 6 * px)
    )

    // Animation bobbing
    val idleBob = if (action == KnightAction.IDLE) {
        if (frame % 2 == 0) 0f else -1f * px
    } else 0f

    val runBob = if (action == KnightAction.RUNNING) {
        if (frame % 2 == 1) -2f * px else 0f
    } else 0f

    val baseOffsetY = oy + idleBob + runBob

    // Facing factor
    val flipX = if (direction == Direction.LEFT) -1f else 1f

    // 1. Cape / Back layer (if facing Down or Right/Left)
    if (direction == Direction.DOWN || direction == Direction.LEFT || direction == Direction.RIGHT) {
        drawRect(
            color = colPlumeDark,
            topLeft = Offset(ox - 6 * px * flipX, baseOffsetY - 4 * px),
            size = Size(12 * px, 15 * px)
        )
        drawRect(
            color = colPlumeRed,
            topLeft = Offset(ox - 5 * px * flipX, baseOffsetY - 3 * px),
            size = Size(10 * px, 13 * px)
        )
    }

    // 2. Legs / Boots
    val legFrame = if (action == KnightAction.RUNNING) frame % 4 else 0
    val leftLegOffset = when (legFrame) {
        1 -> -3f * px
        3 -> 2f * px
        else -> 0f
    }
    val rightLegOffset = when (legFrame) {
        1 -> 2f * px
        3 -> -3f * px
        else -> 0f
    }

    // Left Boot
    drawRect(
        color = colArmorDark,
        topLeft = Offset(ox - 6 * px * flipX, baseOffsetY + 6 * px + leftLegOffset),
        size = Size(4 * px, 7 * px)
    )
    drawRect(
        color = colArmorBase,
        topLeft = Offset(ox - 5 * px * flipX, baseOffsetY + 7 * px + leftLegOffset),
        size = Size(3 * px, 5 * px)
    )

    // Right Boot
    drawRect(
        color = colArmorDark,
        topLeft = Offset(ox + 2 * px * flipX, baseOffsetY + 6 * px + rightLegOffset),
        size = Size(4 * px, 7 * px)
    )
    drawRect(
        color = colArmorBase,
        topLeft = Offset(ox + 3 * px * flipX, baseOffsetY + 7 * px + rightLegOffset),
        size = Size(3 * px, 5 * px)
    )

    // 3. Torso / Breastplate
    drawRect(
        color = colArmorOutline,
        topLeft = Offset(ox - 7 * px * flipX, baseOffsetY - 6 * px),
        size = Size(14 * px, 13 * px)
    )
    drawRect(
        color = colArmorBase,
        topLeft = Offset(ox - 6 * px * flipX, baseOffsetY - 5 * px),
        size = Size(12 * px, 11 * px)
    )
    // Breastplate highlight & gold emblem
    drawRect(
        color = colArmorLight,
        topLeft = Offset(ox - 4 * px * flipX, baseOffsetY - 4 * px),
        size = Size(4 * px, 8 * px)
    )
    drawRect(
        color = colGoldTrim,
        topLeft = Offset(ox - 1 * px, baseOffsetY - 3 * px),
        size = Size(2 * px, 5 * px)
    )
    // Leather belt
    drawRect(
        color = colLeather,
        topLeft = Offset(ox - 6 * px * flipX, baseOffsetY + 4 * px),
        size = Size(12 * px, 2 * px)
    )
    drawRect(
        color = colGoldHighlight,
        topLeft = Offset(ox - 2 * px, baseOffsetY + 4 * px),
        size = Size(4 * px, 2 * px)
    )

    // 4. Helmet & Plume
    val helmY = baseOffsetY - 18 * px

    // Helmet Outline
    drawRect(
        color = colArmorOutline,
        topLeft = Offset(ox - 7 * px * flipX, helmY),
        size = Size(14 * px, 13 * px)
    )
    // Helmet Base
    drawRect(
        color = colArmorBase,
        topLeft = Offset(ox - 6 * px * flipX, helmY + 1 * px),
        size = Size(12 * px, 11 * px)
    )
    // Helmet Highlights
    drawRect(
        color = colArmorLight,
        topLeft = Offset(ox - 5 * px * flipX, helmY + 2 * px),
        size = Size(4 * px, 7 * px)
    )

    // Visor T-slit / Slit
    when (direction) {
        Direction.DOWN -> {
            drawRect(
                color = colArmorOutline,
                topLeft = Offset(ox - 4 * px, helmY + 6 * px),
                size = Size(8 * px, 2 * px)
            )
            drawRect(
                color = colVisorGlow,
                topLeft = Offset(ox - 3 * px, helmY + 6 * px),
                size = Size(6 * px, 1 * px)
            )
            drawRect(
                color = colArmorOutline,
                topLeft = Offset(ox - 1 * px, helmY + 6 * px),
                size = Size(2 * px, 5 * px)
            )
        }
        Direction.UP -> {
            // Back of the helmet
            drawRect(
                color = colArmorDark,
                topLeft = Offset(ox - 4 * px, helmY + 4 * px),
                size = Size(8 * px, 7 * px)
            )
        }
        Direction.LEFT, Direction.RIGHT -> {
            // Side visor slit
            val slitX = if (direction == Direction.RIGHT) ox + 1 * px else ox - 5 * px
            drawRect(
                color = colArmorOutline,
                topLeft = Offset(slitX, helmY + 6 * px),
                size = Size(4 * px, 2 * px)
            )
            drawRect(
                color = colVisorGlow,
                topLeft = Offset(slitX + 1 * px, helmY + 6 * px),
                size = Size(2 * px, 1 * px)
            )
        }
    }

    // Knight Helmet Plume (Feather Crest)
    drawRect(
        color = colPlumeRed,
        topLeft = Offset(ox - 2 * px, helmY - 5 * px),
        size = Size(4 * px, 6 * px)
    )
    drawRect(
        color = colPlumeDark,
        topLeft = Offset(ox - 3 * px * flipX, helmY - 4 * px),
        size = Size(2 * px, 4 * px)
    )
    drawRect(
        color = colGoldTrim,
        topLeft = Offset(ox - 2 * px, helmY - 1 * px),
        size = Size(4 * px, 2 * px)
    )

    // 5. Shield (Held on left arm or back)
    val shieldX = ox - 11 * px * flipX
    val shieldY = baseOffsetY - 4 * px
    drawRect(
        color = colArmorOutline,
        topLeft = Offset(shieldX, shieldY),
        size = Size(5 * px, 10 * px)
    )
    drawRect(
        color = Color(0xFF1E3A8A), // Royal Blue shield field
        topLeft = Offset(shieldX + 1 * px * flipX, shieldY + 1 * px),
        size = Size(3 * px, 8 * px)
    )
    drawRect(
        color = colGoldHighlight,
        topLeft = Offset(shieldX + 1 * px * flipX, shieldY + 3 * px),
        size = Size(3 * px, 3 * px)
    )

    // 6. Sword & Arm / Sword Swing
    if (action == KnightAction.SWORD_SWING) {
        // Dynamic swing arc
        val swordHandX = ox + 8 * px * flipX
        val swordHandY = baseOffsetY - 1 * px

        // Swing frames: 0 (Wind up), 1 (Forward Slash), 2 (Follow-through)
        val swingStage = (slashProgress * 3f).toInt().coerceIn(0, 2)

        val bladeAngle = when (swingStage) {
            0 -> -45f // wound back high
            1 -> 35f  // slashed across
            else -> 80f // downward finish
        }

        // Draw slash energy arc effect
        if (swingStage == 1 || swingStage == 2) {
            val arcPath = Path().apply {
                val startAngle = if (flipX > 0) -60f else 240f
                val sweep = if (flipX > 0) 120f else -120f
                val radius = 24 * px
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        ox - radius,
                        baseOffsetY - radius,
                        ox + radius,
                        baseOffsetY + radius
                    ),
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = sweep,
                    forceMoveTo = true
                )
            }
            drawPath(
                path = arcPath,
                color = colBladeGlow,
                style = Stroke(width = 4 * px)
            )
            drawPath(
                path = arcPath,
                color = colSlashArc,
                style = Stroke(width = 8 * px)
            )
        }

        // Blade
        val rad = (bladeAngle * PI / 180f).toFloat()
        val bladeLength = 22 * px
        val tipX = swordHandX + cos(rad) * bladeLength * flipX
        val tipY = swordHandY + sin(rad) * bladeLength

        // Hilt
        drawCircle(
            color = colGoldTrim,
            radius = 3 * px,
            center = Offset(swordHandX, swordHandY)
        )
        // Crossguard
        drawLine(
            color = colGoldTrim,
            start = Offset(swordHandX - 3 * px, swordHandY - 2 * px),
            end = Offset(swordHandX + 3 * px, swordHandY + 2 * px),
            strokeWidth = 3 * px
        )
        // Blade line
        drawLine(
            color = colBladeSteel,
            start = Offset(swordHandX, swordHandY),
            end = Offset(tipX, tipY),
            strokeWidth = 3.5f * px
        )
        drawLine(
            color = colBladeGlow,
            start = Offset(swordHandX, swordHandY),
            end = Offset(tipX, tipY),
            strokeWidth = 1.5f * px
        )
    } else {
        // Idle / Running sword resting at side
        val swordX = ox + 7 * px * flipX
        val swordY = baseOffsetY - 4 * px

        // Gauntlet / Arm
        drawRect(
            color = colArmorBase,
            topLeft = Offset(swordX - 1 * px, swordY + 2 * px),
            size = Size(4 * px, 6 * px)
        )
        // Pommel & Guard
        drawRect(
            color = colGoldTrim,
            topLeft = Offset(swordX, swordY - 2 * px),
            size = Size(3 * px, 2 * px)
        )
        drawRect(
            color = colGoldTrim,
            topLeft = Offset(swordX - 2 * px, swordY),
            size = Size(6 * px, 2 * px)
        )
        // Sheathed or held blade pointing down
        drawRect(
            color = colBladeSteel,
            topLeft = Offset(swordX, swordY + 2 * px),
            size = Size(2 * px, 14 * px)
        )
    }
}
