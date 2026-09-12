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
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.ui.theme.GlassColors

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: CategoryEntity,
    isDark: Boolean = true,
    isGrid: Boolean = true,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onMove: () -> Unit = {},
    onChangeColor: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val colorItem = GlassColors.getColorItem(category.colorHex)

    GlassmorphicBox(
        modifier = modifier
            .testTag("category_card_${category.id}")
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
        backgroundBrush = GlassColors.getGlassCardBrush(category.colorHex, isDark),
        borderBrush = GlassColors.getCardBorderBrush(category.colorHex, isDark),
        elevation = if (isGrid) 0.dp else 4.dp,
        shadowColor = if (isGrid) Color.Transparent else colorItem.primaryColor.copy(alpha = 0.35f),
        glowColor = if (isGrid) null else colorItem.secondaryColor,
        onClick = onClick
    ) {
        if (isGrid) {
            // Grid Layout: 3D Card with center bold category name
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
                            .testTag("category_menu_btn_${category.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "تنظیمات دسته",
                            tint = if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF111827),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // 3D Glass Dropdown Menu
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.95f) else Color(0xFFFFFFFF))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "تغییر نام دسته",
                                    color = if (isDark) Color.White else Color(0xFF111827),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "انتقال دسته",
                                    color = if (isDark) Color.White else Color(0xFF111827),
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
                                    color = if (isDark) Color.White else Color(0xFF111827),
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
                                    text = "حذف دسته",
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

                // Center Category Title (Clean minimal: only category name displayed in center)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 3D Icon Badge
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(6.dp, CircleShape, spotColor = colorItem.secondaryColor)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        colorItem.highlightColor.copy(alpha = 0.4f),
                                        colorItem.primaryColor.copy(alpha = 0.6f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ExpandableAutoText(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.3.sp
                        ),
                        color = if (isDark) Color.White else Color(0xFF111827),
                        textAlign = TextAlign.Center,
                        collapsedMaxLines = 2,
                        minFontSize = 10.sp
                    )

                    if (category.rating > 0f) {
                        Spacer(modifier = Modifier.height(4.dp))
                        RatingBadge(rating = category.rating, isDark = isDark)
                    }
                }
            }
        } else {
            // List Layout: Horizontal 3D Glass Bar
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
                                        colorItem.primaryColor.copy(alpha = 0.6f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        ExpandableAutoText(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isDark) Color.White else Color(0xFF111827),
                            collapsedMaxLines = 1,
                            minFontSize = 10.sp
                        )
                        if (category.rating > 0f) {
                            Spacer(modifier = Modifier.height(2.dp))
                            RatingBadge(rating = category.rating, isDark = isDark)
                        }
                    }
                }

                // 3-dots Menu Button on List Item
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("category_menu_btn_${category.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "تنظیمات دسته",
                            tint = if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF111827)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(if (isDark) Color(0xFF0F172A).copy(alpha = 0.95f) else Color(0xFFFFFFFF))
                    ) {
                        DropdownMenuItem(
                            text = {
                                AutoFitButtonText(
                                    text = "تغییر نام دسته",
                                    color = if (isDark) Color.White else Color(0xFF111827),
                                    targetFontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF38BDF8))
                            },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                AutoFitButtonText(
                                    text = "انتقال دسته",
                                    color = if (isDark) Color.White else Color(0xFF111827),
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
                                    color = if (isDark) Color.White else Color(0xFF111827),
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
                                    text = "حذف دسته",
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
