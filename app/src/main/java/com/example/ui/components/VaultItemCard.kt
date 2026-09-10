package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemType
import com.example.data.model.VaultItemEntity
import com.example.ui.theme.GlassColors

@Composable
fun VaultItemCard(
    modifier: Modifier = Modifier,
    item: VaultItemEntity,
    isDark: Boolean = true,
    isGrid: Boolean = true,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit = {},
    onChangeColor: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val itemType = ItemType.fromId(item.type)
    val colorHex = item.colorHex.ifEmpty { "#3B82F6" }
    val colorItem = GlassColors.getColorItem(colorHex)

    GlassmorphicBox(
        modifier = modifier
            .testTag("item_card_${item.id}")
            .then(
                if (isGrid) {
                    Modifier.height(160.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                }
            ),
        shape = RoundedCornerShape(26.dp),
        backgroundBrush = GlassColors.getGlassCardBrush(colorHex, isDark),
        borderBrush = GlassColors.getCardBorderBrush(colorHex, isDark),
        elevation = if (isGrid) 0.dp else 4.dp,
        shadowColor = if (isGrid) Color.Transparent else colorItem.primaryColor.copy(alpha = 0.35f),
        glowColor = if (isGrid) null else colorItem.secondaryColor,
        onClick = onClick
    ) {
        if (isGrid) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top-Corner 3-dots Menu Button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("item_menu_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "تنظیمات آیتم",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF0F172A).copy(alpha = 0.95f))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "ویرایش آیتم",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "انتقال به دسته دیگر",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.DriveFileMove, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                menuExpanded = false
                                onMove()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "تغییر رنگ (۱۶ رنگ)",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.ColorLens, contentDescription = null, tint = colorItem.secondaryColor)
                            },
                            onClick = {
                                menuExpanded = false
                                onChangeColor()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "حذف آیتم",
                                    color = Color(0xFFEF4444),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }

                // Center Content: Ordered strictly as requested:
                // 1. Rating
                // 2. Title & Name
                // 3. Description
                // 4. Other texts
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 1. Rating (first)
                    if (item.rating > 0f) {
                        RatingBadge(rating = item.rating)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Type Icon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(6.dp, CircleShape, spotColor = colorItem.secondaryColor)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        colorItem.highlightColor.copy(alpha = 0.4f),
                                        colorItem.primaryColor.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = itemType.icon,
                            contentDescription = itemType.titleFa,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Title and Name of item
                    ExpandableAutoText(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        collapsedMaxLines = 2,
                        minFontSize = 10.sp
                    )

                    // 3. Description
                    if (item.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 4. Other texts (Type, details)
                    Text(
                        text = itemType.titleFa,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorItem.highlightColor.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            // List layout: Ordered strictly as requested:
            // 1. Rating, 2. Title & Name, 3. Description, 4. Other texts
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(6.dp, CircleShape, spotColor = colorItem.secondaryColor)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        colorItem.highlightColor.copy(alpha = 0.4f),
                                        colorItem.primaryColor.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = itemType.icon,
                            contentDescription = itemType.titleFa,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // 1. Rating (first)
                        if (item.rating > 0f) {
                            RatingBadge(rating = item.rating)
                            Spacer(modifier = Modifier.height(3.dp))
                        }

                        // 2. Title and Name
                        ExpandableAutoText(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            collapsedMaxLines = 1,
                            minFontSize = 10.sp
                        )

                        // 3. Description
                        if (item.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // 4. Other texts
                        Text(
                            text = itemType.titleFa,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorItem.highlightColor.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 3-dots Menu Button
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("item_menu_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "تنظیمات آیتم",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color(0xFF0F172A).copy(alpha = 0.95f))
                    ) {
                        DropdownMenuItem(
                            text = {
                                AutoFitButtonText(
                                    text = "ویرایش آیتم",
                                    color = Color.White,
                                    targetFontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                AutoFitButtonText(
                                    text = "انتقال به دسته دیگر",
                                    color = Color.White,
                                    targetFontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.AutoMirrored.Filled.DriveFileMove, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                menuExpanded = false
                                onMove()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                AutoFitButtonText(
                                    text = "تغییر رنگ (۱۶ رنگ)",
                                    color = Color.White,
                                    targetFontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.ColorLens, contentDescription = null, tint = colorItem.secondaryColor)
                            },
                            onClick = {
                                menuExpanded = false
                                onChangeColor()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                AutoFitButtonText(
                                    text = "حذف آیتم",
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    targetFontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
