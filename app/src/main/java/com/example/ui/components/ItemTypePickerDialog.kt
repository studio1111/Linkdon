package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ItemType
import com.example.ui.theme.GlassColors

@Composable
fun ItemTypePickerDialog(
    selectedType: ItemType,
    accentHex: String = "#3B82F6",
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
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0F172A).copy(alpha = 0.98f),
                    Color(0xFF1E293B).copy(alpha = 0.98f),
                    colorItem.primaryColor.copy(alpha = 0.40f)
                )
            ),
            borderBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.7f),
                    colorItem.secondaryColor.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.15f)
                )
            ),
            elevation = 18.dp,
            shadowColor = colorItem.secondaryColor.copy(alpha = 0.5f)
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
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "نوع داده مورد نظر برای ذخیره‌سازی را انتخاب کنید:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White.copy(alpha = 0.8f)
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
                                colors = if (isSelected) {
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
                            ),
                            borderBrush = Brush.linearGradient(
                                colors = if (isSelected) {
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
                            ),
                            borderWidth = if (isSelected) 1.8.dp else 1.dp,
                            elevation = if (isSelected) 8.dp else 2.dp,
                            glowColor = if (isSelected) colorItem.secondaryColor else Color.Transparent,
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
                                                    Brush.linearGradient(
                                                        listOf(
                                                            Color.White.copy(alpha = 0.20f),
                                                            Color.White.copy(alpha = 0.05f)
                                                        )
                                                    )
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = type.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
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
                                            color = Color.White
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        // Category group badge placed underneath the title
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isSelected) colorItem.secondaryColor.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.12f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = type.categoryGroupFa,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = if (isSelected) colorItem.highlightColor else Color.White.copy(alpha = 0.75f)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = type.descriptionFa,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                            color = Color.White.copy(alpha = 0.75f),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Selection Indicator Icon
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isSelected) "انتخاب شده" else "انتخاب",
                                    tint = if (isSelected) colorItem.highlightColor else Color.White.copy(alpha = 0.35f),
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
