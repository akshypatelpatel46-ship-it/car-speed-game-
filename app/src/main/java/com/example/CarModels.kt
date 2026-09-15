package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

enum class CarType {
    SUPERCAR, MUSCLE, HYPERCAR, FORMULA, TRUCK, SEDAN
}

enum class PickupType {
    COIN, NITRO, SHIELD, MULTIPLIER
}

data class TrafficCar(
    val id: Long,
    val lane: Int, // 0, 1, 2, 3
    var yProgress: Float, // 0.0 (top/horizon) to 1.1 (past bottom)
    val speed: Float, // Relative speed factor
    val color: Color,
    val carType: CarType = CarType.SEDAN,
    var isPassed: Boolean = false
)

data class RoadPickup(
    val id: Long,
    val lane: Int,
    var yProgress: Float,
    val type: PickupType,
    var isCollected: Boolean = false
)

data class CarProfile(
    val id: String,
    val name: String,
    val typeName: String,
    val topSpeedKmh: Int,
    val acceleration0To100: Float, // in seconds (lower is faster)
    val handling: Float, // 0.5 to 1.0
    val costCoins: Int,
    var isUnlocked: Boolean = false,
    val bodyColor: Color,
    val accentColor: Color,
    var engineUpgrade: Int = 1,
    var turboUpgrade: Int = 1,
    var handlingUpgrade: Int = 1,
    var nosUpgrade: Int = 1
)

object CarCatalog {
    val ALL_CARS = listOf(
        CarProfile(
            id = "apex_gt",
            name = "Apex GT-R",
            typeName = "V8 Twin-Turbo Supercar",
            topSpeedKmh = 240,
            acceleration0To100 = 3.4f,
            handling = 0.80f,
            costCoins = 0,
            isUnlocked = true,
            bodyColor = Color(0xFFEF4444), // Racing Red
            accentColor = Color(0xFF1E293B)
        ),
        CarProfile(
            id = "cyber_phantom",
            name = "Cyber Phantom",
            typeName = "Electric AWD Hypercar",
            topSpeedKmh = 290,
            acceleration0To100 = 2.4f,
            handling = 0.90f,
            costCoins = 250,
            isUnlocked = false,
            bodyColor = Color(0xFF06B6D4), // Cyan Neon
            accentColor = Color(0xFF22D3EE)
        ),
        CarProfile(
            id = "thunder_muscle",
            name = "Thunder V8",
            typeName = "Supercharged American Muscle",
            topSpeedKmh = 265,
            acceleration0To100 = 3.0f,
            handling = 0.75f,
            costCoins = 400,
            isUnlocked = false,
            bodyColor = Color(0xFFF59E0B), // Speed Amber
            accentColor = Color(0xFF000000)
        ),
        CarProfile(
            id = "formula_neon",
            name = "Formula Mach-1",
            typeName = "Carbon Aero Prototype",
            topSpeedKmh = 340,
            acceleration0To100 = 1.9f,
            handling = 0.98f,
            costCoins = 800,
            isUnlocked = false,
            bodyColor = Color(0xFF10B981), // Emerald Neon
            accentColor = Color(0xFFFCD34D)
        )
    )
}

/**
 * Procedural top-down racing sports car canvas renderer.
 */
@Composable
fun CarCanvas(
    bodyColor: Color,
    accentColor: Color,
    isBraking: Boolean = false,
    isNitroActive: Boolean = false,
    steerTilt: Float = 0f, // -1f (left) to +1f (right)
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawRacingCar(
            w = size.width,
            h = size.height,
            bodyColor = bodyColor,
            accentColor = accentColor,
            isBraking = isBraking,
            isNitroActive = isNitroActive,
            steerTilt = steerTilt
        )
    }
}

fun DrawScope.drawRacingCar(
    w: Float,
    h: Float,
    bodyColor: Color,
    accentColor: Color,
    isBraking: Boolean = false,
    isNitroActive: Boolean = false,
    steerTilt: Float = 0f
) {
    val cx = w / 2f
    val cy = h / 2f

    // Scale unit
    val sx = w / 40f
    val sy = h / 70f

    val tiltOffset = steerTilt * 3.5f * sx

    // 1. Drop Shadow underneath car
    drawOval(
        color = Color(0x66000000),
        topLeft = Offset(cx - 16 * sx + tiltOffset, cy - 28 * sy),
        size = Size(32 * sx, 58 * sy)
    )

    // 2. Headlight Beams (forward on asphalt)
    val beamPath = Path().apply {
        moveTo(cx - 11 * sx + tiltOffset, cy - 28 * sy)
        lineTo(cx - 22 * sx + tiltOffset, cy - 65 * sy)
        lineTo(cx + 22 * sx + tiltOffset, cy - 65 * sy)
        lineTo(cx + 11 * sx + tiltOffset, cy - 28 * sy)
        close()
    }
    drawPath(beamPath, Color(0x35FEF08A), style = Fill)

    // 3. Wheels / Tires (4 wheels)
    val tireColor = Color(0xFF111827)
    val rimColor = Color(0xFF94A3B8)

    // Front Left Wheel (steered angle)
    drawRoundRect(
        color = tireColor,
        topLeft = Offset(cx - 17 * sx + tiltOffset, cy - 24 * sy),
        size = Size(5 * sx, 12 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )
    drawRect(rimColor, Offset(cx - 16 * sx + tiltOffset, cy - 20 * sy), Size(3 * sx, 4 * sy))

    // Front Right Wheel
    drawRoundRect(
        color = tireColor,
        topLeft = Offset(cx + 12 * sx + tiltOffset, cy - 24 * sy),
        size = Size(5 * sx, 12 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )
    drawRect(rimColor, Offset(cx + 13 * sx + tiltOffset, cy - 20 * sy), Size(3 * sx, 4 * sy))

    // Rear Left Wheel
    drawRoundRect(
        color = tireColor,
        topLeft = Offset(cx - 17 * sx + tiltOffset, cy + 12 * sy),
        size = Size(5.5f * sx, 13 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )
    drawRect(rimColor, Offset(cx - 16 * sx + tiltOffset, cy + 16 * sy), Size(3 * sx, 5 * sy))

    // Rear Right Wheel
    drawRoundRect(
        color = tireColor,
        topLeft = Offset(cx + 11.5f * sx + tiltOffset, cy + 12 * sy),
        size = Size(5.5f * sx, 13 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )
    drawRect(rimColor, Offset(cx + 13 * sx + tiltOffset, cy + 16 * sy), Size(3 * sx, 5 * sy))

    // 4. Main Aerodynamic Chassis Body
    val bodyPath = Path().apply {
        moveTo(cx - 7 * sx + tiltOffset, cy - 30 * sy) // Front nose left
        lineTo(cx + 7 * sx + tiltOffset, cy - 30 * sy) // Front nose right
        lineTo(cx + 13 * sx + tiltOffset, cy - 20 * sy) // Front fender
        lineTo(cx + 12 * sx + tiltOffset, cy - 4 * sy) // Side door waist
        lineTo(cx + 14 * sx + tiltOffset, cy + 14 * sy) // Rear arch
        lineTo(cx + 13 * sx + tiltOffset, cy + 28 * sy) // Rear bumper right
        lineTo(cx - 13 * sx + tiltOffset, cy + 28 * sy) // Rear bumper left
        lineTo(cx - 14 * sx + tiltOffset, cy + 14 * sy) // Rear arch left
        lineTo(cx - 12 * sx + tiltOffset, cy - 4 * sy) // Side waist left
        lineTo(cx - 13 * sx + tiltOffset, cy - 20 * sy) // Front fender left
        close()
    }
    drawPath(bodyPath, bodyColor, style = Fill)
    drawPath(bodyPath, Color(0xFF0F172A), style = Stroke(width = 1.5f * sx))

    // Center Racing Stripe / Accent Hood Lines
    val stripePath = Path().apply {
        moveTo(cx - 2.5f * sx + tiltOffset, cy - 29 * sy)
        lineTo(cx + 2.5f * sx + tiltOffset, cy - 29 * sy)
        lineTo(cx + 3.5f * sx + tiltOffset, cy + 27 * sy)
        lineTo(cx - 3.5f * sx + tiltOffset, cy + 27 * sy)
        close()
    }
    drawPath(stripePath, accentColor, style = Fill)

    // Hood Vents / Indentations
    drawLine(
        color = Color(0x88000000),
        start = Offset(cx - 5 * sx + tiltOffset, cy - 22 * sy),
        end = Offset(cx - 4 * sx + tiltOffset, cy - 14 * sy),
        strokeWidth = 1.5f * sx
    )
    drawLine(
        color = Color(0x88000000),
        start = Offset(cx + 5 * sx + tiltOffset, cy - 22 * sy),
        end = Offset(cx + 4 * sx + tiltOffset, cy - 14 * sy),
        strokeWidth = 1.5f * sx
    )

    // 5. Cockpit & Windshield (Glossy Glass)
    val windshieldPath = Path().apply {
        moveTo(cx - 8 * sx + tiltOffset, cy - 12 * sy)
        lineTo(cx + 8 * sx + tiltOffset, cy - 12 * sy)
        lineTo(cx + 9 * sx + tiltOffset, cy - 2 * sy)
        lineTo(cx - 9 * sx + tiltOffset, cy - 2 * sy)
        close()
    }
    drawPath(windshieldPath, Color(0xFF1E293B), style = Fill)
    // Windshield Reflection
    drawLine(
        color = Color(0x8867E8F9),
        start = Offset(cx - 6 * sx + tiltOffset, cy - 11 * sy),
        end = Offset(cx + 1 * sx + tiltOffset, cy - 3 * sy),
        strokeWidth = 1.5f * sx
    )

    // Roof & Rear Window
    drawRect(
        color = accentColor,
        topLeft = Offset(cx - 7.5f * sx + tiltOffset, cy - 2 * sy),
        size = Size(15 * sx, 10 * sy)
    )
    drawRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(cx - 7 * sx + tiltOffset, cy + 8 * sy),
        size = Size(14 * sx, 6 * sy)
    )

    // 6. Rear Spoiler / Wing
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - 15 * sx + tiltOffset, cy + 24 * sy),
        size = Size(30 * sx, 4 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(cx - 14 * sx + tiltOffset, cy + 24.5f * sy),
        size = Size(28 * sx, 2.5f * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5f * sx, 1.5f * sy)
    )

    // 7. Headlights (Bright Xenon/LED)
    drawCircle(Color(0xFFFEF08A), radius = 2f * sx, center = Offset(cx - 9 * sx + tiltOffset, cy - 28 * sy))
    drawCircle(Color(0xFFFFFFFF), radius = 1.2f * sx, center = Offset(cx - 9 * sx + tiltOffset, cy - 28 * sy))
    drawCircle(Color(0xFFFEF08A), radius = 2f * sx, center = Offset(cx + 9 * sx + tiltOffset, cy - 28 * sy))
    drawCircle(Color(0xFFFFFFFF), radius = 1.2f * sx, center = Offset(cx + 9 * sx + tiltOffset, cy - 28 * sy))

    // 8. Taillights / Brake Lights
    val brakeColor = if (isBraking) Color(0xFFFF1111) else Color(0xFF991B1B)
    val brakeGlow = if (isBraking) Color(0xAAFF2222) else Color(0x33FF0000)

    // Left Taillight
    drawRect(brakeGlow, Offset(cx - 13 * sx + tiltOffset, cy + 27 * sy), Size(6 * sx, 4 * sy))
    drawRect(brakeColor, Offset(cx - 12 * sx + tiltOffset, cy + 27.5f * sy), Size(5 * sx, 2 * sy))

    // Right Taillight
    drawRect(brakeGlow, Offset(cx + 7 * sx + tiltOffset, cy + 27 * sy), Size(6 * sx, 4 * sy))
    drawRect(brakeColor, Offset(cx + 7 * sx + tiltOffset, cy + 27.5f * sy), Size(5 * sx, 2 * sy))

    // 9. Nitro Exhaust Flames (when Nitro Boost active)
    if (isNitroActive) {
        val flameLen = 14f * sy
        // Left Exhaust Flame
        val leftFlame = Path().apply {
            moveTo(cx - 6 * sx + tiltOffset, cy + 28 * sy)
            lineTo(cx - 4 * sx + tiltOffset, cy + 28 * sy)
            lineTo(cx - 5 * sx + tiltOffset, cy + 28 * sy + flameLen)
            close()
        }
        drawPath(leftFlame, Color(0xFF06B6D4), style = Fill)
        drawPath(leftFlame, Color(0xFF67E8F9), style = Stroke(width = 1.5f * sx))

        // Right Exhaust Flame
        val rightFlame = Path().apply {
            moveTo(cx + 4 * sx + tiltOffset, cy + 28 * sy)
            lineTo(cx + 6 * sx + tiltOffset, cy + 28 * sy)
            lineTo(cx + 5 * sx + tiltOffset, cy + 28 * sy + flameLen)
            close()
        }
        drawPath(rightFlame, Color(0xFF06B6D4), style = Fill)
        drawPath(rightFlame, Color(0xFF67E8F9), style = Stroke(width = 1.5f * sx))

        // Center Cyan Energy Core
        drawCircle(Color(0xFF22D3EE), radius = 3 * sx, center = Offset(cx - 5 * sx + tiltOffset, cy + 29 * sy))
        drawCircle(Color(0xFF22D3EE), radius = 3 * sx, center = Offset(cx + 5 * sx + tiltOffset, cy + 29 * sy))
    }
}
