package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

data class BikeProfile(
    val id: String,
    val name: String,
    val brandStyle: String,
    val topSpeedKmh: Int,
    val acceleration0To100: Float,
    val handling: Float,
    val wheelieStability: Float,
    val costCoins: Int,
    var isUnlocked: Boolean = false,
    val bodyColor: Color,
    val accentColor: Color,
    val suitColor: Color,
    var engineUpgrade: Int = 1,
    var nitroUpgrade: Int = 1,
    var aeroUpgrade: Int = 1
)

enum class HighLevelEnvironment(val title: String, val description: String, val skyTop: Color, val skyBottom: Color, val roadColor: Color) {
    CYBERPUNK_NIGHT(
        title = "Cyberpunk Neo-Tokyo",
        description = "Towering holographic skyscrapers, neon purple horizon & synthwave atmosphere",
        skyTop = Color(0xFF0D0221),
        skyBottom = Color(0xFF260F3D),
        roadColor = Color(0xFF141722)
    ),
    SUNSET_CANYON(
        title = "Sunset Canyon Expressway",
        description = "Golden hour ocean cliffs, blazing orange horizon & dramatic mountain silhouettes",
        skyTop = Color(0xFF450A0A),
        skyBottom = Color(0xFFD97706),
        roadColor = Color(0xFF1F1D24)
    ),
    MIDNIGHT_BAY(
        title = "Tokyo Bay Wangan",
        description = "Midnight coastal expressway with ocean reflections & distant illuminated suspension bridge",
        skyTop = Color(0xFF020617),
        skyBottom = Color(0xFF0F172A),
        roadColor = Color(0xFF0E131F)
    )
}

object SuperbikeCatalog {
    val ALL_BIKES = listOf(
        BikeProfile(
            id = "dragon_h2",
            name = "Dragon H2-RR",
            brandStyle = "Supercharged 998cc Inline-4",
            topSpeedKmh = 355,
            acceleration0To100 = 2.4f,
            handling = 0.85f,
            wheelieStability = 0.80f,
            costCoins = 0,
            isUnlocked = true,
            bodyColor = Color(0xFF10B981), // Emerald Ninja Green
            accentColor = Color(0xFF111827),
            suitColor = Color(0xFF064E3B)
        ),
        BikeProfile(
            id = "bologna_v4",
            name = "Panigale V4 R",
            brandStyle = "Desmosedici Stradale V4",
            topSpeedKmh = 345,
            acceleration0To100 = 2.6f,
            handling = 0.94f,
            wheelieStability = 0.88f,
            costCoins = 250,
            isUnlocked = false,
            bodyColor = Color(0xFFEF4444), // Racing Italian Red
            accentColor = Color(0xFFFFFFFF),
            suitColor = Color(0xFF991B1B)
        ),
        BikeProfile(
            id = "mach_390",
            name = "Hayabusa Mach 390",
            brandStyle = "1340cc Hypersonic Streamliner",
            topSpeedKmh = 390,
            acceleration0To100 = 2.1f,
            handling = 0.76f,
            wheelieStability = 0.92f,
            costCoins = 450,
            isUnlocked = false,
            bodyColor = Color(0xFF06B6D4), // Cyan Hyper
            accentColor = Color(0xFFF59E0B),
            suitColor = Color(0xFF0E7490)
        ),
        BikeProfile(
            id = "volt_tt",
            name = "Volt TT Quantum",
            brandStyle = "Liquid-Cooled Dual Flux EV",
            topSpeedKmh = 370,
            acceleration0To100 = 1.7f,
            handling = 0.98f,
            wheelieStability = 0.85f,
            costCoins = 750,
            isUnlocked = false,
            bodyColor = Color(0xFFA855F7), // Electric Violet
            accentColor = Color(0xFF22D3EE),
            suitColor = Color(0xFF581C87)
        )
    )
}

/**
 * High-detail procedural superbike motorcycle and leaning rider renderer.
 */
@Composable
fun SuperbikeCanvas(
    bodyColor: Color,
    accentColor: Color,
    suitColor: Color,
    isBraking: Boolean = false,
    isNitroActive: Boolean = false,
    isWheelie: Boolean = false,
    leanAngle: Float = 0f, // -1f (left lean) to +1f (right lean)
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawSuperbike(
            w = size.width,
            h = size.height,
            bodyColor = bodyColor,
            accentColor = accentColor,
            suitColor = suitColor,
            isBraking = isBraking,
            isNitroActive = isNitroActive,
            isWheelie = isWheelie,
            lean = leanAngle
        )
    }
}

fun DrawScope.drawSuperbike(
    w: Float,
    h: Float,
    bodyColor: Color,
    accentColor: Color,
    suitColor: Color,
    isBraking: Boolean,
    isNitroActive: Boolean,
    isWheelie: Boolean,
    lean: Float
) {
    val cx = w / 2f
    val cy = h / 2f

    val sx = w / 40f
    val sy = h / 70f

    val leanOffset = lean * 5.5f * sx
    val wheelieLift = if (isWheelie) -6f * sy else 0f

    // 1. Ground Shadow
    drawOval(
        color = Color(0x66000000),
        topLeft = Offset(cx - 10 * sx + leanOffset * 0.4f, cy + 24 * sy),
        size = Size(20 * sx, 8 * sy)
    )

    // 2. Wide Slick Rear Tire (Racing compound)
    val tireW = 12f * sx
    val tireH = 18f * sy
    drawRoundRect(
        color = Color(0xFF090C12),
        topLeft = Offset(cx - tireW / 2f + leanOffset * 0.8f, cy + 14 * sy + wheelieLift),
        size = Size(tireW, tireH),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4 * sx, 4 * sy)
    )
    // Tire tread center strip
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(cx - 2 * sx + leanOffset * 0.8f, cy + 16 * sy + wheelieLift),
        size = Size(4 * sx, 14 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1 * sx, 1 * sy)
    )

    // 3. Swingarm & Chain Drive (Titanium/Carbon)
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(cx - 7 * sx + leanOffset * 0.7f, cy + 10 * sy + wheelieLift),
        size = Size(14 * sx, 6 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )

    // 4. Twin Underseat / High-Mount Titanium Exhaust Pipes
    val exhaustLeft = Offset(cx - 8 * sx + leanOffset * 0.8f, cy + 8 * sy + wheelieLift)
    val exhaustRight = Offset(cx + 4 * sx + leanOffset * 0.8f, cy + 8 * sy + wheelieLift)
    // Left pipe
    drawOval(Color(0xFF64748B), topLeft = exhaustLeft, size = Size(4 * sx, 6 * sy))
    drawOval(Color(0xFF0F172A), topLeft = Offset(exhaustLeft.x + 0.8f * sx, exhaustLeft.y + 1f * sy), size = Size(2.4f * sx, 4 * sy))
    // Right pipe
    drawOval(Color(0xFF64748B), topLeft = exhaustRight, size = Size(4 * sx, 6 * sy))
    drawOval(Color(0xFF0F172A), topLeft = Offset(exhaustRight.x + 0.8f * sx, exhaustRight.y + 1f * sy), size = Size(2.4f * sx, 4 * sy))

    // 5. Tail Cowl & LED Taillights
    val tailPath = Path().apply {
        moveTo(cx - 7 * sx + leanOffset, cy + 6 * sy + wheelieLift)
        lineTo(cx + 7 * sx + leanOffset, cy + 6 * sy + wheelieLift)
        lineTo(cx + 4 * sx + leanOffset, cy + 12 * sy + wheelieLift)
        lineTo(cx - 4 * sx + leanOffset, cy + 12 * sy + wheelieLift)
        close()
    }
    drawPath(tailPath, bodyColor, style = Fill)
    drawPath(tailPath, Color.Black, style = Stroke(width = 1.2f * sx))

    // Taillight Bar
    val brakeColor = if (isBraking) Color(0xFFFF1E1E) else Color(0xFFDC2626)
    val brakeGlow = if (isBraking) Color(0xAAFF0000) else Color(0x44FF0000)
    drawRoundRect(
        color = brakeGlow,
        topLeft = Offset(cx - 6 * sx + leanOffset, cy + 8.5f * sy + wheelieLift),
        size = Size(12 * sx, 4 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )
    drawRoundRect(
        color = brakeColor,
        topLeft = Offset(cx - 5 * sx + leanOffset, cy + 9.5f * sy + wheelieLift),
        size = Size(10 * sx, 2 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1 * sx, 1 * sy)
    )

    // 6. Motorcycle Rider (Leather Racing Suit, Knee Pucks, Aerodynamic Hump)
    // Rider Hips & Lower Back
    drawRoundRect(
        color = suitColor,
        topLeft = Offset(cx - 6.5f * sx + leanOffset * 1.1f, cy - 2 * sy + wheelieLift),
        size = Size(13 * sx, 9 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3 * sx, 3 * sy)
    )
    // Suit Accents / Stripes
    drawLine(
        color = accentColor,
        start = Offset(cx - 4 * sx + leanOffset * 1.1f, cy - 1 * sy + wheelieLift),
        end = Offset(cx - 3 * sx + leanOffset * 1.1f, cy + 5 * sy + wheelieLift),
        strokeWidth = 1.5f * sx
    )
    drawLine(
        color = accentColor,
        start = Offset(cx + 4 * sx + leanOffset * 1.1f, cy - 1 * sy + wheelieLift),
        end = Offset(cx + 3 * sx + leanOffset * 1.1f, cy + 5 * sy + wheelieLift),
        strokeWidth = 1.5f * sx
    )

    // Rider Torso / Shoulders & Aero Hump
    val torsoPath = Path().apply {
        moveTo(cx - 10 * sx + leanOffset * 1.25f, cy - 12 * sy + wheelieLift) // Left shoulder
        lineTo(cx + 10 * sx + leanOffset * 1.25f, cy - 12 * sy + wheelieLift) // Right shoulder
        lineTo(cx + 6 * sx + leanOffset * 1.15f, cy - 2 * sy + wheelieLift) // Right waist
        lineTo(cx - 6 * sx + leanOffset * 1.15f, cy - 2 * sy + wheelieLift) // Left waist
        close()
    }
    drawPath(torsoPath, suitColor, style = Fill)
    drawPath(torsoPath, Color.Black, style = Stroke(width = 1.2f * sx))

    // Speed Hump on Rider's Back
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(cx - 3 * sx + leanOffset * 1.2f, cy - 10 * sy + wheelieLift),
        size = Size(6 * sx, 7 * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2 * sx, 2 * sy)
    )

    // Elbows & Knees (Knee pucks scraping the asphalt when leaning!)
    val kneePuckColor = Color(0xFFFACC15)
    // Left Knee Puck
    drawCircle(
        color = if (lean < -0.3f) kneePuckColor else suitColor,
        radius = 2.2f * sx,
        center = Offset(cx - 10 * sx + leanOffset * 1.1f, cy + 4 * sy + wheelieLift)
    )
    // Right Knee Puck
    drawCircle(
        color = if (lean > 0.3f) kneePuckColor else suitColor,
        radius = 2.2f * sx,
        center = Offset(cx + 10 * sx + leanOffset * 1.1f, cy + 4 * sy + wheelieLift)
    )

    // 7. Rider Racing Helmet & Tinted Mirror Visor
    val helmetCenter = Offset(cx + leanOffset * 1.35f, cy - 18 * sy + wheelieLift)
    // Helmet Shell
    drawOval(
        color = bodyColor,
        topLeft = Offset(helmetCenter.x - 6 * sx, helmetCenter.y - 7 * sy),
        size = Size(12 * sx, 13 * sy)
    )
    drawOval(
        color = Color.Black,
        topLeft = Offset(helmetCenter.x - 6 * sx, helmetCenter.y - 7 * sy),
        size = Size(12 * sx, 13 * sy),
        style = Stroke(width = 1.2f * sx)
    )

    // Helmet Aerodynamic Fin / Spoiler
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(helmetCenter.x - 3.5f * sx, helmetCenter.y + 2 * sy),
        size = Size(7 * sx, 2.5f * sy),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(1 * sx, 1 * sy)
    )

    // Helmet Tinted Visor (Black / Iridium Mirror)
    val visorPath = Path().apply {
        moveTo(helmetCenter.x - 4.5f * sx, helmetCenter.y - 3 * sy)
        lineTo(helmetCenter.x + 4.5f * sx, helmetCenter.y - 3 * sy)
        lineTo(helmetCenter.x + 4 * sx, helmetCenter.y + 1 * sy)
        lineTo(helmetCenter.x - 4 * sx, helmetCenter.y + 1 * sy)
        close()
    }
    drawPath(visorPath, Color(0xFF0A0E1A), style = Fill)
    // Iridium Blue Reflection line on Visor
    drawLine(
        color = Color(0xFF38BDF8),
        start = Offset(helmetCenter.x - 3 * sx, helmetCenter.y - 2 * sy),
        end = Offset(helmetCenter.x + 3 * sx, helmetCenter.y - 1 * sy),
        strokeWidth = 1.2f * sx
    )

    // 8. Clip-on Handlebars & Mirrors
    drawLine(
        color = Color(0xFF475569),
        start = Offset(cx - 11 * sx + leanOffset * 1.2f, cy - 11 * sy + wheelieLift),
        end = Offset(cx - 15 * sx + leanOffset * 1.2f, cy - 9 * sy + wheelieLift),
        strokeWidth = 2f * sx
    )
    drawLine(
        color = Color(0xFF475569),
        start = Offset(cx + 11 * sx + leanOffset * 1.2f, cy - 11 * sy + wheelieLift),
        end = Offset(cx + 15 * sx + leanOffset * 1.2f, cy - 9 * sy + wheelieLift),
        strokeWidth = 2f * sx
    )

    // 9. NITRO Exhaust Blast Flames (Dual Jets)
    if (isNitroActive) {
        val flameH = 20f * sy
        // Left Flame
        val flameLeft = Path().apply {
            moveTo(exhaustLeft.x, exhaustLeft.y + 4 * sy)
            lineTo(exhaustLeft.x + 3 * sx, exhaustLeft.y + 4 * sy)
            lineTo(exhaustLeft.x + 1.5f * sx, exhaustLeft.y + 4 * sy + flameH)
            close()
        }
        drawPath(flameLeft, Color(0xFF06B6D4), style = Fill)
        drawPath(flameLeft, Color(0xFFA855F7), style = Stroke(width = 1.5f * sx))

        // Right Flame
        val flameRight = Path().apply {
            moveTo(exhaustRight.x, exhaustRight.y + 4 * sy)
            lineTo(exhaustRight.x + 3 * sx, exhaustRight.y + 4 * sy)
            lineTo(exhaustRight.x + 1.5f * sx, exhaustRight.y + 4 * sy + flameH)
            close()
        }
        drawPath(flameRight, Color(0xFF06B6D4), style = Fill)
        drawPath(flameRight, Color(0xFFA855F7), style = Stroke(width = 1.5f * sx))

        // Plasma Core glow
        drawCircle(Color(0xFF22D3EE), radius = 3.5f * sx, center = Offset(exhaustLeft.x + 1.5f * sx, exhaustLeft.y + 5 * sy))
        drawCircle(Color(0xFF22D3EE), radius = 3.5f * sx, center = Offset(exhaustRight.x + 1.5f * sx, exhaustRight.y + 5 * sy))
    }

    // 10. Wheelie Sparks / Stunt Indicator
    if (isWheelie) {
        // Ground sparks behind rear wheel
        drawCircle(Color(0xFFFDE047), radius = 3f * sx, center = Offset(cx - 3 * sx, cy + 28 * sy))
        drawCircle(Color(0xFFF97316), radius = 2f * sx, center = Offset(cx + 4 * sx, cy + 29 * sy))
        drawCircle(Color(0xFFFEF08A), radius = 1.5f * sx, center = Offset(cx, cy + 30 * sy))
    }
}
