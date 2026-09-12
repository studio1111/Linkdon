package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * Format a rating value like 8.5 to "8.5", or 8.0 to "8".
 */
fun formatRating(rating: Float): String {
    return if (rating <= 0f) {
        "0"
    } else if (rating % 1f == 0f) {
        rating.toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", rating)
    }
}

/**
 * 10-Star Rating Bar with full support for half-stars (e.g. 8.5 stars),
 * precise touch detection (left half vs right half of each star),
 * tap toggling, and fine-tuning controls (+0.5 / -0.5 / reset).
 */
@Composable
fun StarRatingBar(
    rating: Float,
    maxStars: Int = 10,
    onRatingChanged: ((Float) -> Unit)? = null,
    starSize: Dp = 18.dp,
    showControls: Boolean = true,
    showNumberBelow: Boolean = true,
    isDark: Boolean = true,
    filledColor: Color = Color(0xFFFBBF24), // Vibrant Amber gold
    unfilledColor: Color = if (isDark) Color.White.copy(alpha = 0.30f) else Color(0xFFD1D5DB),
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Rating Stars Row (1 to 10) - Compact spacing & sizing to fit any screen
        Row(
            horizontalArrangement = Arrangement.spacedBy(1.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..maxStars) {
                val starIndex = i.toFloat()
                // fraction for this star: 1f if >= i, 0.5f if >= i - 0.5f, else 0f
                val fillFraction = when {
                    rating >= starIndex -> 1f
                    rating >= starIndex - 0.5f -> 0.5f
                    else -> 0f
                }

                val starModifier = if (onRatingChanged != null) {
                    Modifier
                        .size(starSize + 4.dp)
                        .clip(CircleShape)
                        .pointerInput(rating, i, layoutDirection) {
                            detectTapGestures { offset ->
                                val halfWidth = size.width / 2f
                                val isFirstHalf = if (layoutDirection == LayoutDirection.Rtl) {
                                    offset.x > halfWidth
                                } else {
                                    offset.x <= halfWidth
                                }

                                val clickedValue = if (isFirstHalf) {
                                    starIndex - 0.5f
                                } else {
                                    starIndex
                                }

                                if (rating == clickedValue) {
                                    // If tapped again on same value, step down to half or zero
                                    if (clickedValue > 0.5f && isFirstHalf) {
                                        onRatingChanged(0f)
                                    } else if (!isFirstHalf) {
                                        onRatingChanged(starIndex - 0.5f)
                                    } else {
                                        onRatingChanged(0f)
                                    }
                                } else {
                                    onRatingChanged(clickedValue)
                                }
                            }
                        }
                        .padding(1.dp)
                } else {
                    Modifier.size(starSize)
                }

                SingleStarItem(
                    fraction = fillFraction,
                    size = starSize,
                    filledColor = filledColor,
                    unfilledColor = unfilledColor,
                    layoutDirection = layoutDirection,
                    modifier = starModifier
                )
            }
        }

        // Numerical display and fine-tuning controls
        if (showControls && onRatingChanged != null) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Minus 0.5 button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE5E7EB))
                        .clickable(enabled = rating > 0f) {
                            val newRating = (rating - 0.5f).coerceAtLeast(0f)
                            onRatingChanged(newRating)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "کاهش نیم ستاره",
                        tint = if (rating > 0f) (if (isDark) Color.White else Color(0xFF111827)) else (if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFF9CA3AF)),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Current Rating Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFF59E0B).copy(alpha = 0.25f),
                                    Color(0xFFFBBF24).copy(alpha = 0.18f)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable {
                            // Tap to clear
                            if (rating > 0f) onRatingChanged(0f)
                        }
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★",
                            color = filledColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${formatRating(rating)} از $maxStars",
                            color = if (isDark) Color.White else Color(0xFF111827),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (rating > 0f) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(پاک کردن)",
                                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF6B7280),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Plus 0.5 button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE5E7EB))
                        .clickable(enabled = rating < maxStars) {
                            val newRating = (rating + 0.5f).coerceAtMost(maxStars.toFloat())
                            onRatingChanged(newRating)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزایش نیم ستاره",
                        tint = if (rating < maxStars) (if (isDark) Color.White else Color(0xFF111827)) else (if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFF9CA3AF)),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else if (showNumberBelow && rating > 0f) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "${formatRating(rating)} / $maxStars",
                color = if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF111827),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Renders a single star with exact fractional fill (0f, 0.5f, 1f).
 */
@Composable
private fun SingleStarItem(
    fraction: Float,
    size: Dp,
    filledColor: Color,
    unfilledColor: Color,
    layoutDirection: LayoutDirection,
    modifier: Modifier = Modifier
) {
    val clipShape = remember(fraction, layoutDirection) {
        GenericShape { shapeSize, dir ->
            val w = shapeSize.width
            val h = shapeSize.height
            if (fraction >= 1f) {
                addRect(Rect(0f, 0f, w, h))
            } else if (fraction <= 0f) {
                // empty
            } else {
                if (dir == LayoutDirection.Rtl) {
                    addRect(Rect(w * (1f - fraction), 0f, w, h))
                } else {
                    addRect(Rect(0f, 0f, w * fraction, h))
                }
            }
        }
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Unfilled background star
        Icon(
            imageVector = Icons.Outlined.StarOutline,
            contentDescription = null,
            tint = unfilledColor,
            modifier = Modifier.fillMaxSize()
        )

        // Filled overlay star with fraction clip (perfect half star)
        if (fraction > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(clipShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = filledColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Compact, beautiful rating badge for Category Cards, Item Cards, and Dialogs.
 * Displays the rating star with the numerical score strictly BELOW the star.
 */
@Composable
fun RatingBadge(
    rating: Float,
    modifier: Modifier = Modifier,
    maxStars: Int = 10,
    filledColor: Color = Color(0xFFFBBF24),
    showMax: Boolean = false,
    isDark: Boolean = true
) {
    if (rating <= 0f) return

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF59E0B).copy(alpha = 0.22f))
            .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.50f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "★",
                color = filledColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 12.sp
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = if (showMax) "${formatRating(rating)}/$maxStars" else formatRating(rating),
                color = if (isDark) Color.White else Color(0xFF111827),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 10.sp
            )
        }
    }
}
