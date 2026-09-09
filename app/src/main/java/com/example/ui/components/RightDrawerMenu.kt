package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.FormatTextdirectionLToR
import androidx.compose.material.icons.automirrored.filled.FormatTextdirectionRToL
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FontOption
import com.example.data.model.SortOption
import com.example.data.model.ThemeOption
import com.example.ui.theme.GlassColors

@Composable
fun RightDrawerMenu(
    modifier: Modifier = Modifier,
    currentTheme: ThemeOption,
    onSelectTheme: (ThemeOption) -> Unit,
    currentFont: FontOption,
    onSelectFont: (FontOption) -> Unit,
    currentSort: SortOption,
    onSelectSort: (SortOption) -> Unit,
    isGridLayout: Boolean,
    onToggleLayout: () -> Unit,
    textScale: Float,
    onTextScaleChange: (Float) -> Unit,
    isRtl: Boolean,
    onToggleRtl: (Boolean) -> Unit,
    categoryCount: Int,
    subcategoryCount: Int,
    itemCount: Int,
    userEmail: String = "",
    userName: String = "",
    isOfflineMode: Boolean = false,
    appPin: String = "",
    cloudSyncStatus: String = "همگام‌سازی ابری فعال است",
    onUpdateCredentials: (username: String, email: String) -> Unit = { _, _ -> },
    onSetPin: (String) -> Unit = {},
    onRemovePin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSyncNow: () -> Unit = {},
    onClearAllData: () -> Unit = {},
    onExportJson: (onJsonReady: (String) -> Unit) -> Unit,
    onImportJson: (json: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedJsonString by remember { mutableStateOf("") }
    var importJsonText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    // Account & Security edit state in Drawer
    var showEditAccountDialog by remember { mutableStateOf(false) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    // Section collapse / expand states
    var isSortExpanded by remember { mutableStateOf(true) }
    var isLayoutExpanded by remember { mutableStateOf(false) }
    var isThemeExpanded by remember { mutableStateOf(false) }
    var isFontExpanded by remember { mutableStateOf(false) }
    var isBackupExpanded by remember { mutableStateOf(false) }
    var isStatsExpanded by remember { mutableStateOf(false) }

    fun shareJsonToCloud(json: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, json)
            putExtra(Intent.EXTRA_TITLE, "پشتیبان داده‌های لینکدون (JSON)")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "ذخیره در گوگل درایو یا سایر برنامه‌ها")
        context.startActivity(shareIntent)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(340.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A).copy(alpha = 0.98f),
                        Color(0xFF1E293B).copy(alpha = 0.96f),
                        currentTheme.startGradient.copy(alpha = 0.45f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        currentTheme.accentColor.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
            )
            .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Drawer Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(6.dp, CircleShape, spotColor = currentTheme.accentColor)
                            .clip(CircleShape)
                            .background(currentTheme.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "تنظیمات لینکدون",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.testTag("close_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن منو",
                        tint = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: مرتب‌سازی و چیدمان (Sort & Order)
            DrawerCollapsibleSection(
                title = "ترتیب و مرتب‌سازی",
                subtitle = currentSort.titleFa,
                icon = Icons.AutoMirrored.Filled.Sort,
                accentColor = currentTheme.accentColor,
                isExpanded = isSortExpanded,
                onToggle = { isSortExpanded = !isSortExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SortOption.entries.forEach { option ->
                        val isSelected = option == currentSort
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) currentTheme.accentColor.copy(alpha = 0.25f)
                                    else Color.White.copy(alpha = 0.04f)
                                )
                                .border(
                                    width = if (isSelected) 1.dp else 0.5.dp,
                                    color = if (isSelected) currentTheme.accentColor else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectSort(option) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.titleFa,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = option.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.55f)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = currentTheme.accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 2: جهت و ساختار چینش (Layout & Direction)
            DrawerCollapsibleSection(
                title = "جهت و ساختار چینش",
                subtitle = if (isRtl) "راست‌چین (فارسی)" else "چپ‌چین (انگلیسی)",
                icon = Icons.AutoMirrored.Filled.FormatTextdirectionRToL,
                accentColor = currentTheme.accentColor,
                isExpanded = isLayoutExpanded,
                onToggle = { isLayoutExpanded = !isLayoutExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // RTL vs LTR Toggle
                    Text(
                        text = "جهت چیدمان صفحه:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // RTL Button
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isRtl) currentTheme.accentColor.copy(alpha = 0.28f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (isRtl) 1.2.dp else 0.5.dp,
                                    color = if (isRtl) currentTheme.accentColor else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onToggleRtl(true) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatTextdirectionRToL,
                                contentDescription = null,
                                tint = if (isRtl) currentTheme.accentColor else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "راست‌چین",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isRtl) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isRtl) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }

                        // LTR Button
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (!isRtl) currentTheme.accentColor.copy(alpha = 0.28f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (!isRtl) 1.2.dp else 0.5.dp,
                                    color = if (!isRtl) currentTheme.accentColor else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onToggleRtl(false) }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.FormatTextdirectionLToR,
                                contentDescription = null,
                                tint = if (!isRtl) currentTheme.accentColor else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "چپ‌چین",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (!isRtl) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (!isRtl) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Grid vs List Layout Toggle
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "نحوه نمایش آیتم‌ها:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isGridLayout) currentTheme.accentColor.copy(alpha = 0.28f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (isGridLayout) 1.2.dp else 0.5.dp,
                                    color = if (isGridLayout) currentTheme.accentColor else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { if (!isGridLayout) onToggleLayout() }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = null,
                                tint = if (isGridLayout) currentTheme.accentColor else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "شبکه‌ای",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (isGridLayout) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isGridLayout) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (!isGridLayout) currentTheme.accentColor.copy(alpha = 0.28f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (!isGridLayout) 1.2.dp else 0.5.dp,
                                    color = if (!isGridLayout) currentTheme.accentColor else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { if (isGridLayout) onToggleLayout() }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewList,
                                contentDescription = null,
                                tint = if (!isGridLayout) currentTheme.accentColor else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "لیستی",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (!isGridLayout) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (!isGridLayout) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 3: تم‌ها و رنگ‌بندی (Themes)
            DrawerCollapsibleSection(
                title = "تم و رنگ‌بندی شیشه‌ای",
                subtitle = currentTheme.titleFa,
                icon = Icons.Default.Palette,
                accentColor = currentTheme.accentColor,
                isExpanded = isThemeExpanded,
                onToggle = { isThemeExpanded = !isThemeExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val themeRows = ThemeOption.entries.chunked(2)
                    themeRows.forEach { rowThemes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowThemes.forEach { theme ->
                                val isSelected = theme == currentTheme
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) theme.accentColor.copy(alpha = 0.35f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.5.dp,
                                            color = if (isSelected) theme.accentColor else Color.White.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { onSelectTheme(theme) }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(theme.startGradient, theme.accentColor)
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = theme.titleFa,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 4: فونت و اندازه متن (Typography & Font Size)
            DrawerCollapsibleSection(
                title = "فونت و اندازه متن",
                subtitle = "${(textScale * 100).toInt()}% - ${currentFont.titleFa}",
                icon = Icons.Default.FormatSize,
                accentColor = currentTheme.accentColor,
                isExpanded = isFontExpanded,
                onToggle = { isFontExpanded = !isFontExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اندازه متن (${(textScale * 100).toInt()}%):",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Slider(
                        value = textScale,
                        onValueChange = onTextScaleChange,
                        valueRange = 0.80f..1.35f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = currentTheme.accentColor,
                            activeTrackColor = currentTheme.accentColor,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "قلم و فونت برنامه:",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FontOption.entries.forEach { fontOption ->
                            val isSelected = fontOption == currentFont
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) currentTheme.accentColor.copy(alpha = 0.3f)
                                        else Color.White.copy(alpha = 0.05f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.2.dp else 0.5.dp,
                                        color = if (isSelected) currentTheme.accentColor else Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onSelectFont(fontOption) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fontOption.titleFa,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 5: پشتیبان‌گیری، امنیت و مدیریت داده‌ها
            DrawerCollapsibleSection(
                title = "پشتیبان‌گیری، امنیت و حساب",
                subtitle = if (userName.isNotBlank()) "کاربر: $userName" else if (isOfflineMode) "حالت آفلاین" else "گوگل درایو و امنیت",
                icon = Icons.Default.CloudUpload,
                accentColor = currentTheme.accentColor,
                isExpanded = isBackupExpanded,
                onToggle = { isBackupExpanded = !isBackupExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Current Account Info Card
                    GlassmorphicBox(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        backgroundBrush = Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.03f))
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (userName.isNotBlank()) Icons.Default.Person else Icons.Default.Security,
                                        contentDescription = null,
                                        tint = currentTheme.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (userName.isNotBlank()) userName else if (isOfflineMode) "حالت ورود آفلاین" else "حساب ثبت نشده",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = if (appPin.isNotBlank()) "🔒 رمز فعال" else "بدون رمز",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (appPin.isNotBlank()) Color(0xFF34D399) else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            if (userEmail.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = userEmail,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Firebase Cloud Live Sync Banner & Manual Sync
                    GlassmorphicBox(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        backgroundBrush = Brush.linearGradient(
                            listOf(Color(0xFF10B981).copy(alpha = 0.12f), Color(0xFF0F172A).copy(alpha = 0.4f))
                        ),
                        borderBrush = Brush.linearGradient(
                            listOf(Color(0xFF10B981).copy(alpha = 0.35f), Color.Transparent)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(if (isOfflineMode) Color(0xFFEF4444) else Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isOfflineMode) "همگام‌سازی ابری: غیرفعال (آفلاین)" else "همگام‌سازی ابری Firebase:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isOfflineMode) Color(0xFFFCA5A5) else Color(0xFF6EE7B7)
                                    )
                                }
                            }

                            if (!isOfflineMode) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = cloudSyncStatus,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.18f))
                                        .clickable { onSyncNow() }
                                        .padding(vertical = 6.dp, horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = null,
                                        tint = Color(0xFF6EE7B7),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "همگام‌سازی دستی اکنون",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF6EE7B7)
                                    )
                                }
                            }
                        }
                    }

                    // Edit Account & Email Button
                    Button(
                        onClick = { showEditAccountDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor.copy(alpha = 0.35f))
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (userName.isNotBlank()) "ویرایش نام کاربری و ایمیل" else "احراز هویت با ایمیل و نام کاربری",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                    // Set / Change / Remove App PIN Button
                    OutlinedButton(
                        onClick = { showSetPinDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = if (appPin.isNotBlank()) Icons.Default.Lock else Icons.Default.Key,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = currentTheme.accentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appPin.isNotBlank()) "تغییر یا حذف رمز عبور (PIN)" else "تنظیم رمز عبور ۴ تا ۸ رقمی",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    // Google Drive / Cloud Share Button
                    Button(
                        onClick = {
                            onExportJson { json ->
                                shareJsonToCloud(json)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ذخیره در گوگل درایو / اشتراک", style = MaterialTheme.typography.labelMedium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onExportJson { json ->
                                    exportedJsonString = json
                                    showExportDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("خروجی JSON", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                importJsonText = ""
                                importStatusMessage = null
                                showImportDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("بازیابی JSON", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Logout Button (if registered)
                    if (userName.isNotBlank() || !isOfflineMode) {
                        OutlinedButton(
                            onClick = { onLogout() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5)),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFEF4444).copy(alpha = 0.5f), Color(0xFFEF4444).copy(alpha = 0.2f))
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "خروج از حساب",
                                tint = Color(0xFFFCA5A5),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("خروج از حساب کاربری", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Danger zone: Delete All Data
                    Button(
                        onClick = { showDeleteAllDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444).copy(alpha = 0.25f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "حذف کل اطلاعات",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حذف کل اطلاعات و بازنشانی",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION 6: آمار و درباره لینکدون (Stats & About)
            DrawerCollapsibleSection(
                title = "اطلاعات و آمار لینکدون",
                subtitle = "$categoryCount دسته | $itemCount آیتم",
                icon = Icons.Default.Info,
                accentColor = currentTheme.accentColor,
                isExpanded = isStatsExpanded,
                onToggle = { isStatsExpanded = !isStatsExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("تعداد کل دسته‌بندی‌ها:", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                        Text("$categoryCount دسته", color = Color.White, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("تعداد کل آیتم‌ها و حساب‌ها:", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                        Text("$itemCount آیتم", color = currentTheme.accentColor, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "لینکدون نسخه ۲.۰ • گاوصندوق هوشمند داده‌ها و پیوندها",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Export Dialog
    if (showExportDialog) {
        Dialog(onDismissRequest = { showExportDialog = false }) {
            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "پشتیبان داده‌های لینکدون",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = exportedJsonString,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Linkdoon Backup", exportedJsonString))
                                Toast.makeText(context, "کد JSON در حافظه کپی شد", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            AutoFitButtonText("کپی متن", color = Color(0xFF0F172A))
                        }
                        OutlinedButton(
                            onClick = { showExportDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AutoFitButtonText("بستن", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        Dialog(onDismissRequest = { showImportDialog = false }) {
            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ExpandableAutoText(
                        text = "بازیابی اطلاعات از فایل JSON",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        collapsedMaxLines = 1,
                        minFontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { Text("محتوای فایل JSON پشتیبان را اینجا جای‌گذاری کنید...") },
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    if (importStatusMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ExpandableAutoText(
                            text = importStatusMessage!!,
                            color = Color(0xFF34D399),
                            style = MaterialTheme.typography.bodySmall,
                            collapsedMaxLines = 2,
                            minFontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (importJsonText.isNotBlank()) {
                                    onImportJson(importJsonText) { success, msg ->
                                        importStatusMessage = msg
                                        if (success) {
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                        ) {
                            AutoFitButtonText("شروع بازیابی", color = Color(0xFF0F172A))
                        }
                        OutlinedButton(
                            onClick = { showImportDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AutoFitButtonText("بستن", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Edit Account / Authentication Dialog
    if (showEditAccountDialog) {
        Dialog(onDismissRequest = { showEditAccountDialog = false }) {
            var inputUser by remember { mutableStateOf(userName) }
            var inputMail by remember { mutableStateOf(userEmail) }
            var editError by remember { mutableStateOf<String?>(null) }

            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "احراز هویت و حساب اختصاصی",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = inputUser,
                        onValueChange = {
                            inputUser = it
                            editError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("نام کاربری اختصاصی *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = currentTheme.accentColor) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputMail,
                        onValueChange = {
                            inputMail = it
                            editError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("آدرس ایمیل معتبر *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = currentTheme.accentColor) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (editError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = editError!!, color = Color(0xFFF43F5E), style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val trimmedUser = inputUser.trim()
                                val trimmedMail = inputMail.trim()
                                if (trimmedUser.isBlank()) {
                                    editError = "نام کاربری نمی‌تواند خالی باشد"
                                    return@Button
                                }
                                if (trimmedMail.isBlank() || !trimmedMail.contains("@")) {
                                    editError = "لطفاً ایمیل معتبر وارد کنید"
                                    return@Button
                                }
                                onUpdateCredentials(trimmedUser, trimmedMail)
                                Toast.makeText(context, "اطلاعات حساب با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
                                showEditAccountDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                        ) {
                            Text("ذخیره حساب", color = Color(0xFF0F172A), style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { showEditAccountDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("انصراف", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }

    // Set / Change App PIN Dialog
    if (showSetPinDialog) {
        Dialog(onDismissRequest = { showSetPinDialog = false }) {
            var newPin by remember { mutableStateOf("") }
            var confirmPin by remember { mutableStateOf("") }
            var pinError by remember { mutableStateOf<String?>(null) }

            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (appPin.isNotBlank()) "تغییر یا حذف رمز ورود" else "تنظیم رمز عبور ۴ تا ۸ رقمی",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "رمز عبور برای قفل گاوصندوق برنامه استفاده می‌شود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = {
                            if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                                newPin = it
                                pinError = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("رمز عبور جدید (۴ تا ۸ رقم)") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = currentTheme.accentColor) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = {
                            if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                                confirmPin = it
                                pinError = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("تکرار رمز عبور جدید") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = currentTheme.accentColor) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accentColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = pinError!!, color = Color(0xFFF43F5E), style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (newPin.length < 4 || newPin.length > 8) {
                                    pinError = "رمز عبور باید بین ۴ تا ۸ رقم باشد"
                                    return@Button
                                }
                                if (newPin != confirmPin) {
                                    pinError = "تکرار رمز عبور مطابقت ندارد"
                                    return@Button
                                }
                                onSetPin(newPin)
                                Toast.makeText(context, "رمز عبور با موفقیت فعال شد", Toast.LENGTH_SHORT).show()
                                showSetPinDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                        ) {
                            Text("ذخیره رمز", color = Color(0xFF0F172A), style = MaterialTheme.typography.labelSmall)
                        }
                        if (appPin.isNotBlank()) {
                            Button(
                                onClick = {
                                    onRemovePin()
                                    Toast.makeText(context, "رمز عبور حذف شد", Toast.LENGTH_SHORT).show()
                                    showSetPinDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("حذف رمز", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete All Data Confirmation Dialog
    if (showDeleteAllDialog) {
        DeleteAllDataDialog(
            currentTheme = currentTheme,
            expectedUsername = if (userName.isNotBlank()) userName else "حذف",
            onConfirmDelete = {
                onClearAllData()
                showDeleteAllDialog = false
                onCloseDrawer()
                Toast.makeText(context, "تمام اطلاعات با موفقیت پاک شدند", Toast.LENGTH_LONG).show()
            },
            onDismiss = { showDeleteAllDialog = false }
        )
    }
}

@Composable
private fun DrawerCollapsibleSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrowRotation"
    )

    GlassmorphicBox(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        backgroundBrush = Brush.linearGradient(
            colors = listOf(
                if (isExpanded) accentColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                Color(0xFF1E293B).copy(alpha = 0.6f)
            )
        ),
        borderBrush = Brush.linearGradient(
            colors = listOf(
                if (isExpanded) accentColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f),
                Color.White.copy(alpha = 0.05f)
            )
        ),
        elevation = if (isExpanded) 6.dp else 2.dp,
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isExpanded) accentColor.copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.08f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isExpanded) accentColor else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ExpandableAutoText(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            collapsedMaxLines = 1,
                            minFontSize = 11.sp
                        )
                        ExpandableAutoText(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isExpanded) accentColor else Color.White.copy(alpha = 0.6f),
                            collapsedMaxLines = 1,
                            minFontSize = 9.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "بستن بخش" else "باز کردن بخش",
                    tint = if (isExpanded) accentColor else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(arrowRotation)
                )
            }

            // Expandable Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    content()
                }
            }
        }
    }
}
