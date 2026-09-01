package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: Int,
    maxStars: Int = 5,
    onRatingChanged: ((Int) -> Unit)? = null,
    starSize: Dp = 26.dp,
    filledColor: Color = Color(0xFFFBBF24), // Amber star
    unfilledColor: Color = Color.White.copy(alpha = 0.35f),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val starModifier = if (onRatingChanged != null) {
                Modifier
                    .size(starSize + 8.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, radius = starSize),
                        onClick = {
                            if (rating == i) {
                                onRatingChanged(0) // tap again to clear
                            } else {
                                onRatingChanged(i)
                            }
                        }
                    )
                    .padding(4.dp)
            } else {
                Modifier.size(starSize)
            }

            Icon(
                imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = "Star $i",
                tint = if (isSelected) filledColor else unfilledColor,
                modifier = starModifier
            )
        }
    }
}
