package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassColors

@Composable
fun ColorPickerCarousel(
    modifier: Modifier = Modifier,
    selectedHex: String,
    onColorSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val selectedColorItem = GlassColors.getColorItem(selectedHex)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "انتخاب رنگ کارت (۱۶ رنگ شیشه‌ای):",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = selectedColorItem.nameFa,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = selectedColorItem.highlightColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scrolling carousel of 16 3D glass color pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(vertical = 6.dp, horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassColors.presets.forEach { item ->
                val isSelected = item.hex.equals(selectedHex, ignoreCase = true)
                val animatedSize by animateDpAsState(
                    targetValue = if (isSelected) 46.dp else 38.dp,
                    animationSpec = tween(150),
                    label = "color_bubble_size"
                )
                val animatedBorderColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.25f),
                    animationSpec = tween(150),
                    label = "color_border"
                )

                Box(
                    modifier = Modifier
                        .size(animatedSize)
                        .testTag("color_picker_${item.hex.replace("#", "")}")
                        .shadow(
                            elevation = if (isSelected) 10.dp else 4.dp,
                            shape = CircleShape,
                            spotColor = item.secondaryColor
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    item.highlightColor,
                                    item.secondaryColor,
                                    item.primaryColor
                                )
                            )
                        )
                        .border(
                            width = if (isSelected) 2.5.dp else 1.2.dp,
                            color = animatedBorderColor,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(item.hex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "انتخاب شده",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
