package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.ItemType
import com.example.ui.theme.GlassColors

@Composable
fun ItemTypePickerDialog(
    selectedType: ItemType,
    accentHex: String = "#3B82F6",
    isDark: Boolean = true,
    onSelectType: (ItemType) -> Unit,
    onDismiss: () -> Unit
) {
    val colorItem = GlassColors.getColorItem(accentHex)

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .heightIn(max = 640.dp)
                .testTag("item_type_picker_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = GlassColors.getOpaqueDialogBrush(isDark = isDark, accentColor = colorItem.secondaryColor),
            borderBrush = GlassColors.getOpaqueBorderBrush(isDark = isDark, accentColor = colorItem.highlightColor),
            elevation = 18.dp,
            shadowColor = if (isDark) colorItem.secondaryColor.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.12f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "انتخاب نوع داده و آیتم",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isDark) Color.White else Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "نوع داده مورد نظر برای ذخیره‌سازی را انتخاب کنید:",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF4B5563)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF374151)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Structured List of all Item Types
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ItemType.entries) { type ->
                        val isSelected = type == selectedType

                        GlassmorphicBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("type_option_${type.id}"),
                            shape = RoundedCornerShape(16.dp),
                            backgroundBrush = Brush.linearGradient(
                                colors = if (isDark) {
                                    if (isSelected) {
                                        listOf(
                                            colorItem.secondaryColor.copy(alpha = 0.45f),
                                            Color(0xFF1E293B).copy(alpha = 0.90f)
                                        )
                                    } else {
                                        listOf(
                                            Color.White.copy(alpha = 0.08f),
                                            Color.Black.copy(alpha = 0.20f)
                                        )
                                    }
                                } else {
                                    if (isSelected) {
                                        listOf(
                                            colorItem.secondaryColor.copy(alpha = 0.18f),
                                            Color(0xFFEDE4D5)
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFF3ECE0),
                                            Color(0xFFEBE3D5)
                                        )
                                    }
                                }
                            ),
                            borderBrush = Brush.linearGradient(
                                colors = if (isDark) {
                                    if (isSelected) {
                                        listOf(
                                            Color.White.copy(alpha = 0.9f),
                                            colorItem.highlightColor,
                                            colorItem.secondaryColor
                                        )
                                    } else {
                                        listOf(
                                            Color.White.copy(alpha = 0.25f),
                                            Color.White.copy(alpha = 0.05f)
                                        )
                                    }
                                } else {
                                    if (isSelected) {
                                        listOf(
                                            colorItem.secondaryColor,
                                            colorItem.highlightColor
                                        )
                                    } else {
                                        listOf(
                                            Color(0xFFDCD5C9),
                                            Color(0xFFDCD5C9)
                                        )
                                    }
                                }
                            ),
                            borderWidth = if (isSelected) 1.8.dp else 1.dp,
                            elevation = if (isSelected) 8.dp else 2.dp,
                            glowColor = if (isSelected && isDark) colorItem.secondaryColor else Color.Transparent,
                            onClick = {
                                onSelectType(type)
                                onDismiss()
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // 3D Glass Circular Icon
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .shadow(
                                                elevation = if (isSelected) 8.dp else 4.dp,
                                                shape = CircleShape,
                                                spotColor = if (isSelected) colorItem.secondaryColor else Color.Black
                                            )
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) {
                                                    Brush.linearGradient(
                                                        listOf(
                                                            colorItem.highlightColor,
                                                            colorItem.secondaryColor
                                                        )
                                                    )
                                                } else {
                                                    if (isDark) {
                                                        Brush.linearGradient(
                                                            listOf(
                                                                Color.White.copy(alpha = 0.20f),
                                                                Color.White.copy(alpha = 0.05f)
                                                            )
                                                        )
                                                    } else {
                                                        Brush.linearGradient(
                                                            listOf(
                                                                colorItem.secondaryColor.copy(alpha = 0.8f),
                                                                colorItem.primaryColor
                                                            )
                                                        )
                                                    }
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) Color.White else (if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFFDCD5C9)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = type.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = type.titleFa,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                            ),
                                            color = if (isDark) Color.White else Color(0xFF111827)
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        // Category group badge placed underneath the title
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isSelected) colorItem.secondaryColor.copy(alpha = if (isDark) 0.45f else 0.2f) else (if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFDCD5C9)),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = type.categoryGroupFa,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = if (isSelected) (if (isDark) colorItem.highlightColor else colorItem.secondaryColor) else (if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF374151))
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = type.descriptionFa,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF4B5563),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Selection Indicator Icon
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isSelected) "انتخاب شده" else "انتخاب",
                                    tint = if (isSelected) (if (isDark) colorItem.highlightColor else colorItem.secondaryColor) else (if (isDark) Color.White.copy(alpha = 0.35f) else Color(0xFF9CA3AF)),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
