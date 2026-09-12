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
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SubdirectoryArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryEntity
import com.example.ui.theme.GlassColors

data class CategoryHierarchyItem(
    val category: CategoryEntity,
    val depth: Int,
    val path: String
)

@Composable
fun MoveDestinationDialog(
    title: String,
    targetName: String,
    currentLocationName: String,
    allCategories: List<CategoryEntity>,
    isMovingCategory: Boolean,
    currentParentOrCategoryId: Long?,
    disallowedCategoryIds: Set<Long> = emptySet(),
    isDark: Boolean = true,
    onConfirmMoveTo: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // null represents Root (only for categories), or a specific categoryId
    var selectedTargetId by remember {
        mutableStateOf<Long?>(
            if (isMovingCategory && currentParentOrCategoryId != null) {
                null // default suggestion for subcategory is moving to root
            } else {
                null
            }
        )
    }

    // Build hierarchical category list
    val hierarchicalItems = remember(allCategories) {
        buildCategoryHierarchy(allCategories)
    }

    // Filter based on search query
    val filteredItems = remember(hierarchicalItems, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            hierarchicalItems
        } else {
            hierarchicalItems.filter {
                it.category.name.lowercase().contains(q) || it.path.lowercase().contains(q)
            }
        }
    }

    val isConfirmEnabled = remember(selectedTargetId, currentParentOrCategoryId, isMovingCategory) {
        if (isMovingCategory) {
            // Selected must be different from current parent
            selectedTargetId != currentParentOrCategoryId
        } else {
            // For items, selected must be a non-null category different from current
            selectedTargetId != null && selectedTargetId != currentParentOrCategoryId
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .heightIn(max = 680.dp)
                .testTag("move_destination_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = GlassColors.getOpaqueDialogBrush(isDark = isDark, accentColor = Color(0xFF0284C7)),
            borderBrush = GlassColors.getOpaqueBorderBrush(isDark = isDark, accentColor = Color(0xFF38BDF8)),
            elevation = 18.dp,
            shadowColor = if (isDark) Color(0xFF0284C7).copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.12f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(6.dp, CircleShape, spotColor = Color(0xFF38BDF8))
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF38BDF8).copy(alpha = 0.4f),
                                            Color(0xFF0284C7).copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DriveFileMove,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            ExpandableAutoText(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDark) Color.White else Color(0xFF111827),
                                collapsedMaxLines = 1,
                                minFontSize = 12.sp
                            )
                            ExpandableAutoText(
                                text = "انتقال: «$targetName»",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                collapsedMaxLines = 1,
                                minFontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF374151)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Location Info Chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF3ECE0),
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFDCD5C9),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "موقعیت فعلی:",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    ExpandableAutoText(
                        text = currentLocationName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                        collapsedMaxLines = 1,
                        minFontSize = 9.sp,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "جستجوی دسته مقصد...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF6B7280)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "پاک کردن",
                                    tint = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color.White else Color(0xFF111827),
                        unfocusedTextColor = if (isDark) Color.White else Color(0xFF111827),
                        focusedBorderColor = Color(0xFF0284C7),
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color(0xFF9CA3AF),
                        cursorColor = Color(0xFF0284C7)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "مقصد جدید را انتخاب کنید:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Destination Options List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option: Root / Main Category (only when moving a category)
                    if (isMovingCategory && searchQuery.isBlank()) {
                        item {
                            val isCurrentRoot = currentParentOrCategoryId == null
                            val isSelected = selectedTargetId == null && !isCurrentRoot

                            DestinationRow(
                                title = "دسته‌های اصلی (صفحه اصلی / ریشه)",
                                subtitle = if (isCurrentRoot) "موقعیت فعلی این دسته" else "تبدیل به یک دسته اصلی و مستقل",
                                isSelected = isSelected,
                                isCurrent = isCurrentRoot,
                                isDisabled = isCurrentRoot,
                                icon = Icons.Default.FolderOpen,
                                iconColor = Color(0xFF38BDF8),
                                depth = 0,
                                isDark = isDark,
                                onClick = {
                                    if (!isCurrentRoot) {
                                        selectedTargetId = null
                                    }
                                }
                            )
                        }
                    }

                    // List of categories
                    if (filteredItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "دسته‌ای با این نام پیدا نشد" else "هیچ دسته مقصدی وجود ندارد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF6B7280),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(filteredItems, key = { "target_cat_${it.category.id}" }) { item ->
                            val cat = item.category
                            val isDisallowed = cat.id in disallowedCategoryIds
                            val isCurrent = cat.id == currentParentOrCategoryId
                            val isSelected = selectedTargetId == cat.id

                            val colorItem = GlassColors.getColorItem(cat.colorHex)

                            val subtitleText = when {
                                isCurrent -> "موقعیت فعلی"
                                isDisallowed -> "غیرمجاز (خود دسته یا زیردسته آن)"
                                item.path.isNotBlank() -> item.path
                                else -> "دسته اصلی"
                            }

                            DestinationRow(
                                title = cat.name,
                                subtitle = subtitleText,
                                isSelected = isSelected,
                                isCurrent = isCurrent,
                                isDisabled = isDisallowed || isCurrent,
                                icon = if (item.depth > 0) Icons.Default.SubdirectoryArrowLeft else Icons.Default.Folder,
                                iconColor = colorItem.secondaryColor,
                                depth = item.depth,
                                isDark = isDark,
                                onClick = {
                                    if (!isDisallowed && !isCurrent) {
                                        selectedTargetId = cat.id
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF374151)
                        )
                    ) {
                        AutoFitButtonText(text = "انصراف", color = if (isDark) Color.White else Color(0xFF374151))
                    }

                    Button(
                        onClick = {
                            onConfirmMoveTo(selectedTargetId)
                        },
                        enabled = isConfirmEnabled,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7),
                            contentColor = Color.White,
                            disabledContainerColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFDCD5C9),
                            disabledContentColor = if (isDark) Color.White.copy(alpha = 0.35f) else Color(0xFF9CA3AF)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DriveFileMove,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        AutoFitButtonText(
                            text = "تایید و انتقال",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    isCurrent: Boolean,
    isDisabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    depth: Int,
    isDark: Boolean = true,
    onClick: () -> Unit
) {
    val indentPadding = (depth * 14).dp

    val bgColor = when {
        isSelected -> if (isDark) Color(0xFF0284C7).copy(alpha = 0.35f) else Color(0xFFBAE6FD)
        isCurrent -> if (isDark) Color.White.copy(alpha = 0.04f) else Color(0xFFF3ECE0)
        isDisabled -> if (isDark) Color.White.copy(alpha = 0.02f) else Color(0xFFE5DECF).copy(alpha = 0.5f)
        else -> if (isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF3ECE0)
    }

    val borderColor = when {
        isSelected -> Color(0xFF0284C7)
        isCurrent -> if (isDark) Color(0xFFFBBF24).copy(alpha = 0.35f) else Color(0xFFD97706).copy(alpha = 0.45f)
        else -> if (isDark) Color.White.copy(alpha = 0.10f) else Color(0xFFDCD5C9)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indentPadding)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(enabled = !isDisabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDisabled) {
                            if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFDCD5C9)
                        } else {
                            if (isDark) iconColor.copy(alpha = 0.25f) else iconColor.copy(alpha = 0.18f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDisabled) (if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFF9CA3AF)) else iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                ExpandableAutoText(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = when {
                        isDisabled -> if (isDark) Color.White.copy(alpha = 0.35f) else Color(0xFF9CA3AF)
                        isSelected -> if (isDark) Color.White else Color(0xFF0369A1)
                        else -> if (isDark) Color.White.copy(alpha = 0.9f) else Color(0xFF111827)
                    },
                    collapsedMaxLines = 1,
                    minFontSize = 10.sp
                )
                ExpandableAutoText(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = when {
                        isCurrent -> if (isDark) Color(0xFFFBBF24).copy(alpha = 0.8f) else Color(0xFFD97706)
                        isSelected -> if (isDark) Color(0xFF7DD3FC) else Color(0xFF0284C7)
                        isDisabled -> if (isDark) Color.White.copy(alpha = 0.25f) else Color(0xFF9CA3AF)
                        else -> if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF4B5563)
                    },
                    collapsedMaxLines = 1,
                    minFontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Selection / State Indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0284C7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "انتخاب شده",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else if (isCurrent) {
            Text(
                text = "فعلی",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                modifier = Modifier
                    .background(
                        if (isDark) Color(0xFFFBBF24).copy(alpha = 0.15f) else Color(0xFFFEF3C7),
                        RoundedCornerShape(6.dp)
                    )
                    .border(
                        1.dp,
                        if (isDark) Color(0xFFFBBF24).copy(alpha = 0.35f) else Color(0xFFD97706).copy(alpha = 0.35f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

private fun buildCategoryHierarchy(allCategories: List<CategoryEntity>): List<CategoryHierarchyItem> {
    val result = mutableListOf<CategoryHierarchyItem>()
    val rootCategories = allCategories.filter { it.parentId == null }.sortedBy { it.name.lowercase() }

    fun addChildren(parent: CategoryEntity, currentDepth: Int, currentPath: String) {
        val path = if (currentPath.isEmpty()) parent.name else "$currentPath › ${parent.name}"
        result.add(CategoryHierarchyItem(parent, currentDepth, path))

        val children = allCategories.filter { it.parentId == parent.id }.sortedBy { it.name.lowercase() }
        for (child in children) {
            addChildren(child, currentDepth + 1, path)
        }
    }

    for (root in rootCategories) {
        addChildren(root, 0, "")
    }

    return result
}
