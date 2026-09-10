package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ItemType
import com.example.data.model.VaultItemEntity
import com.example.ui.theme.GlassColors

@Composable
fun ItemDetailDialog(
    item: VaultItemEntity,
    onEdit: () -> Unit,
    onMove: () -> Unit = {},
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val itemType = ItemType.fromId(item.type)
    val colorHex = item.colorHex.ifEmpty { "#3B82F6" }
    val colorItem = GlassColors.getColorItem(colorHex)
    var passwordVisible by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label در حافظه کپی شد", Toast.LENGTH_SHORT).show()
    }

    fun openUrl(url: String) {
        try {
            val formatted = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else url
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formatted))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در باز کردن مرورگر", Toast.LENGTH_SHORT).show()
        }
    }

    fun dialPhone(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در شماره‌گیری", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "خطا در باز کردن ایمیل", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .heightIn(max = 700.dp)
                .testTag("item_detail_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B).copy(alpha = 0.98f),
                    Color(0xFF0F172A).copy(alpha = 0.98f),
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
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header Bar
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
                                .size(44.dp)
                                .shadow(6.dp, CircleShape, spotColor = colorItem.secondaryColor)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            colorItem.highlightColor.copy(alpha = 0.4f),
                                            colorItem.primaryColor.copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = itemType.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            // 1. Rating (First) - Numerical rating strictly below stars
                            if (item.rating > 0f) {
                                StarRatingBar(
                                    rating = item.rating,
                                    starSize = 14.dp,
                                    filledColor = Color(0xFFFBBF24),
                                    showControls = false,
                                    showNumberBelow = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // 2. Title and Name of item
                            ExpandableAutoText(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                collapsedMaxLines = 2,
                                minFontSize = 12.sp
                            )

                            // 3. Description
                            if (item.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }

                            // 4. Other texts
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = itemType.titleFa,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorItem.highlightColor
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
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Detail Value Blocks
                when (itemType) {
                    ItemType.BANK_CARD -> {
                        val bank = item.secondaryValue
                        val cardNum = item.primaryValue
                        val owner = parseDetailField(item.extraData, "owner")
                        val cvv = parseDetailField(item.extraData, "cvv2")
                        val exp = parseDetailField(item.extraData, "exp")
                        val sheba = parseDetailField(item.extraData, "sheba")

                        // 3D Glass Bank Card Visual Representation
                        GlassmorphicBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(20.dp),
                            backgroundBrush = Brush.linearGradient(
                                colors = listOf(
                                    colorItem.secondaryColor.copy(alpha = 0.85f),
                                    Color(0xFF0F172A).copy(alpha = 0.95f),
                                    colorItem.primaryColor.copy(alpha = 0.60f)
                                )
                            ),
                            borderBrush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.8f),
                                    colorItem.highlightColor,
                                    Color.White.copy(alpha = 0.2f)
                                )
                            ),
                            elevation = 10.dp,
                            shadowColor = colorItem.secondaryColor
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (bank.isNotBlank()) bank else "کارت بانکی",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CreditCard,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    text = if (cardNum.isNotBlank()) formatCardDisplay(cardNum) else "----  ----  ----  ----",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "دارنده کارت:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = if (owner.isNotBlank()) owner else "---",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "انقضا:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = if (exp.isNotBlank()) exp else "--/--",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Card Number Quick Copy Block
                        DetailValueBlock(
                            label = "شماره ۱۶ رقمی کارت",
                            value = formatCardDisplay(cardNum),
                            onCopy = { copyToClipboard("شماره کارت", cardNum) },
                            colorItem = colorItem
                        )

                        if (cvv.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailValueBlock(
                                label = "کد CVV2",
                                value = cvv,
                                onCopy = { copyToClipboard("کد CVV2", cvv) },
                                colorItem = colorItem
                            )
                        }

                        if (sheba.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailValueBlock(
                                label = "شماره شبا (IBAN)",
                                value = if (sheba.startsWith("IR", ignoreCase = true)) sheba else "IR$sheba",
                                onCopy = {
                                    val full = if (sheba.startsWith("IR", ignoreCase = true)) sheba else "IR$sheba"
                                    copyToClipboard("شماره شبا", full)
                                },
                                colorItem = colorItem
                            )
                        }
                    }

                    ItemType.CONTACT -> {
                        val mainPhone = item.primaryValue
                        val secPhone = item.secondaryValue
                        val email = parseDetailField(item.extraData, "email")

                        DetailValueBlock(
                            label = "شماره تماس اصلی",
                            value = mainPhone,
                            onCopy = { copyToClipboard("شماره تماس", mainPhone) },
                            actionIcon = Icons.Default.Call,
                            actionLabel = "تماس",
                            onAction = { dialPhone(mainPhone) },
                            colorItem = colorItem
                        )

                        if (secPhone.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailValueBlock(
                                label = "شماره تماس دوم / ثابت",
                                value = secPhone,
                                onCopy = { copyToClipboard("شماره تماس دوم", secPhone) },
                                actionIcon = Icons.Default.Call,
                                actionLabel = "تماس",
                                onAction = { dialPhone(secPhone) },
                                colorItem = colorItem
                            )
                        }

                        if (email.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailValueBlock(
                                label = "آدرس ایمیل",
                                value = email,
                                onCopy = { copyToClipboard("ایمیل", email) },
                                actionIcon = Icons.Default.Email,
                                actionLabel = "ارسال ایمیل",
                                onAction = { sendEmail(email) },
                                colorItem = colorItem
                            )
                        }
                    }

                    ItemType.SOCIAL_MEDIA -> {
                        val channelId = item.primaryValue
                        val platform = item.secondaryValue
                        val link = item.extraData

                        DetailValueBlock(
                            label = "پلتفرم و شناسه کانال ($platform)",
                            value = channelId,
                            onCopy = { copyToClipboard("شناسه کانال", channelId) },
                            colorItem = colorItem
                        )

                        if (link.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailValueBlock(
                                label = "لینک مستقیم کانال",
                                value = link,
                                onCopy = { copyToClipboard("لینک کانال", link) },
                                actionIcon = Icons.Default.OpenInBrowser,
                                actionLabel = "باز کردن در مرورگر",
                                onAction = { openUrl(link) },
                                colorItem = colorItem
                            )
                        }
                    }

                    ItemType.URL -> {
                        DetailValueBlock(
                            label = "آدرس وب‌سایت",
                            value = item.primaryValue,
                            onCopy = { copyToClipboard("آدرس سایت", item.primaryValue) },
                            actionIcon = Icons.Default.OpenInBrowser,
                            actionLabel = "باز کردن",
                            onAction = { openUrl(item.primaryValue) },
                            colorItem = colorItem
                        )
                    }

                    ItemType.EMAIL_PASSWORD -> {
                        DetailValueBlock(
                            label = "ایمیل / نام کاربری",
                            value = item.primaryValue,
                            onCopy = { copyToClipboard("ایمیل", item.primaryValue) },
                            colorItem = colorItem
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        DetailValueBlock(
                            label = "رمز عبور",
                            value = if (passwordVisible) item.secondaryValue else "••••••••••••",
                            onCopy = { copyToClipboard("رمز عبور", item.secondaryValue) },
                            actionIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            actionLabel = if (passwordVisible) "مخفی" else "نمایش",
                            onAction = { passwordVisible = !passwordVisible },
                            colorItem = colorItem
                        )
                    }

                    ItemType.SECURITY_CODE -> {
                        DetailValueBlock(
                            label = "کد امنیتی / کلید ۲ مرحله‌ای",
                            value = if (passwordVisible) item.primaryValue else "••••••••••••",
                            onCopy = { copyToClipboard("کد امنیتی", item.primaryValue) },
                            actionIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            actionLabel = if (passwordVisible) "مخفی" else "نمایش",
                            onAction = { passwordVisible = !passwordVisible },
                            colorItem = colorItem
                        )

                        if (item.secondaryValue.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailValueBlock(
                                label = "رمز عبور / اطلاعات محرمانه",
                                value = if (passwordVisible) item.secondaryValue else "••••••••••••",
                                onCopy = { copyToClipboard("رمز محرمانه", item.secondaryValue) },
                                colorItem = colorItem
                            )
                        }
                    }

                    ItemType.AI_PROMPT -> {
                        DetailValueBlock(
                            label = "متن پرامپت هوش مصنوعی",
                            value = item.primaryValue,
                            onCopy = { copyToClipboard("پرامپت", item.primaryValue) },
                            isMultiLine = true,
                            colorItem = colorItem
                        )
                    }

                    ItemType.NOTE -> {
                        DetailValueBlock(
                            label = "محتوای یادداشت",
                            value = item.primaryValue,
                            onCopy = { copyToClipboard("یادداشت", item.primaryValue) },
                            isMultiLine = true,
                            colorItem = colorItem
                        )
                    }

                    ItemType.CODE_SNIPPET -> {
                        if (item.secondaryValue.isNotBlank()) {
                            Text(
                                text = "زبان: ${item.secondaryValue}",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorItem.highlightColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        DetailValueBlock(
                            label = "قطعه کد",
                            value = item.primaryValue,
                            onCopy = { copyToClipboard("کد برنامه‌نویسی", item.primaryValue) },
                            isCode = true,
                            isMultiLine = true,
                            colorItem = colorItem
                        )
                    }

                    ItemType.OTHER_TEXT -> {
                        DetailValueBlock(
                            label = "محتوای متنی",
                            value = item.primaryValue,
                            onCopy = { copyToClipboard("متن", item.primaryValue) },
                            isMultiLine = true,
                            colorItem = colorItem
                        )
                    }
                }

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "توضیحات تکمیلی:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons: Edit and Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onDelete()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFEF4444)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onMove()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF38BDF8)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DriveFileMove,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AutoFitButtonText(text = "انتقال", color = Color(0xFF38BDF8))
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onEdit()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorItem.secondaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        AutoFitButtonText(text = "ویرایش", color = Color.White)
                    }
                }
            }
        }
    }
}

private fun parseDetailField(extraData: String, key: String): String {
    if (extraData.isBlank()) return ""
    val parts = extraData.split("|")
    for (part in parts) {
        val kv = part.split(":", limit = 2)
        if (kv.size == 2 && kv[0].trim().equals(key, ignoreCase = true)) {
            return kv[1].trim()
        }
    }
    return ""
}

private fun formatCardDisplay(num: String): String {
    val clean = num.filter { it.isDigit() }
    return clean.chunked(4).joinToString("  -  ")
}

@Composable
private fun DetailValueBlock(
    label: String,
    value: String,
    onCopy: () -> Unit,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    isCode: Boolean = false,
    isMultiLine: Boolean = false,
    colorItem: com.example.ui.theme.GlassColorItem
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colorItem.highlightColor
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (actionIcon != null && onAction != null) {
                    IconButton(
                        onClick = onAction,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = actionLabel ?: "",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "کپی",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        ExpandableAutoText(
            text = value.ifEmpty { "(خالی)" },
            style = if (isCode) {
                MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            } else MaterialTheme.typography.bodyMedium,
            color = Color.White,
            collapsedMaxLines = if (isMultiLine) 4 else 2,
            minFontSize = 10.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
