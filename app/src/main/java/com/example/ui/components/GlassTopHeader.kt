package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassColors

@Composable
fun GlassTopHeader(
    modifier: Modifier = Modifier,
    isGridLayout: Boolean,
    onToggleLayout: () -> Unit,
    onOpenDrawer: () -> Unit,
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onClearSearch: () -> Unit = {},
    currentCategoryName: String? = null,
    isDark: Boolean = true
) {
    val searchFocusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Button 1: Back Button (if navigating deep) OR List/Grid Switcher
        if (canNavigateBack) {
            GlassmorphicBox(
                modifier = Modifier
                    .size(46.dp)
                    .testTag("header_back_button"),
                shape = RoundedCornerShape(15.dp),
                backgroundBrush = GlassColors.getGlassButtonBrush(isDark),
                borderBrush = GlassColors.getGlassBorderBrush(isDark),
                elevation = 6.dp,
                shadowColor = Color(0xFF06B6D4).copy(alpha = 0.35f),
                onClick = onNavigateBack
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Side Button 1: Hamburger Menu Button (placed on the same side as the drawer menu)
        GlassmorphicBox(
            modifier = Modifier
                .size(46.dp)
                .testTag("hamburger_menu_button"),
            shape = RoundedCornerShape(15.dp),
            backgroundBrush = GlassColors.getGlassButtonBrush(isDark),
            borderBrush = GlassColors.getGlassBorderBrush(isDark),
            elevation = 6.dp,
            shadowColor = Color(0xFF8B5CF6).copy(alpha = 0.35f),
            onClick = onOpenDrawer
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "منوی کشویی",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Center / Main Floating Combined 3D Glass Bar:
        // Integrated Title ("لینکدون") on one side + Search Input in middle + Magnifying Glass icon (ذره‌بین) on other side!
        GlassmorphicBox(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("floating_search_header_bar"),
            shape = RoundedCornerShape(24.dp),
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = if (isDark) 0.16f else 0.40f),
                    Color(0xFF38BDF8).copy(alpha = if (isDark) 0.18f else 0.25f),
                    Color(0xFF818CF8).copy(alpha = if (isDark) 0.12f else 0.20f),
                    Color.Black.copy(alpha = if (isDark) 0.30f else 0.05f)
                )
            ),
            borderBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.75f),
                    Color(0xFF38BDF8).copy(alpha = 0.6f),
                    Color.White.copy(alpha = 0.25f)
                )
            ),
            elevation = 8.dp,
            shadowColor = Color(0xFF38BDF8).copy(alpha = 0.3f),
            glowColor = Color(0xFF38BDF8).copy(alpha = 0.2f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // One End: 3D App Name Badge ("لینکدون")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        searchFocusRequester.requestFocus()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color(0xFF38BDF8))
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "لینکدون",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Middle: Integrated Search Input Field
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = if (currentCategoryName != null) "جستجو در $currentCategoryName..." else "جستجو در آیتم‌ها، کارت‌ها...",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = Color.White.copy(alpha = 0.55f),
                            maxLines = 1
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(searchFocusRequester)
                            .testTag("header_search_input"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(Color(0xFF38BDF8))
                    )
                }

                // Clear Search Button (if query is active)
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = onClearSearch,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("clear_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "پاک کردن جستجو",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Other End: Magnifying Glass Icon (علامت ذره‌بین)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable { searchFocusRequester.requestFocus() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "جستجو",
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Other Side Button: List / Grid toggle button (3D Glass)
        GlassmorphicBox(
            modifier = Modifier
                .size(46.dp)
                .testTag("toggle_layout_button"),
            shape = RoundedCornerShape(15.dp),
            backgroundBrush = GlassColors.getGlassButtonBrush(isDark),
            borderBrush = GlassColors.getGlassBorderBrush(isDark),
            elevation = 6.dp,
            shadowColor = Color(0xFF38BDF8).copy(alpha = 0.35f),
            onClick = onToggleLayout
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGridLayout) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = if (isGridLayout) "نمایش لیستی" else "نمایش جدولی",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
