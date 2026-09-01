package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.PostAdd
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

@Composable
fun AddOptionChoiceDialog(
    categoryName: String,
    onChooseSubcategory: () -> Unit,
    onChooseItem: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("add_option_choice_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B).copy(alpha = 0.95f),
                    Color(0xFF0F172A).copy(alpha = 0.98f),
                    Color(0xFF1E40AF).copy(alpha = 0.35f)
                )
            ),
            borderBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.7f),
                    Color(0xFF38BDF8).copy(alpha = 0.4f),
                    Color.White.copy(alpha = 0.15f)
                )
            ),
            elevation = 16.dp,
            shadowColor = Color(0xFF38BDF8).copy(alpha = 0.4f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "افزودن به «$categoryName»",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "نوع محتوایی که می‌خواهید ایجاد کنید را انتخاب نمایید:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Choice 1: Add Subcategory
                GlassmorphicBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .testTag("choose_subcategory_option"),
                    shape = RoundedCornerShape(20.dp),
                    backgroundBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0284C7).copy(alpha = 0.35f),
                            Color(0xFF0F172A).copy(alpha = 0.60f)
                        )
                    ),
                    borderBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    elevation = 6.dp,
                    onClick = onChooseSubcategory,
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(6.dp, CircleShape, spotColor = Color(0xFF0284C7))
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF38BDF8),
                                            Color(0xFF0369A1)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "افزودن زیر دسته",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ایجاد پوشه و شاخه تودرتو جدید",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF93C5FD)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Choice 2: Add Item
                GlassmorphicBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .testTag("choose_item_option"),
                    shape = RoundedCornerShape(20.dp),
                    backgroundBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7C3AED).copy(alpha = 0.35f),
                            Color(0xFF0F172A).copy(alpha = 0.60f)
                        )
                    ),
                    borderBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFA78BFA).copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    elevation = 6.dp,
                    onClick = onChooseItem,
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(6.dp, CircleShape, spotColor = Color(0xFF7C3AED))
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFA78BFA),
                                            Color(0xFF6D28D9)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PostAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "افزودن آیتم",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "سایت، ایمیل، پسورد، پرامپت، کد یا یادداشت",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC4B5FD)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
