package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ItemType
import com.example.data.model.VaultItemEntity
import com.example.ui.theme.GlassColors

@Composable
fun AddItemDialog(
    initialItem: VaultItemEntity? = null,
    categoryId: Long,
    defaultType: ItemType? = null,
    title: String = if (initialItem == null) "افزودن آیتم جدید" else "ویرایش آیتم",
    onConfirm: (item: VaultItemEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var showTypePickerPopup by remember { mutableStateOf(false) }

    var selectedType by remember {
        mutableStateOf(
            if (initialItem != null) ItemType.fromId(initialItem.type) else (defaultType ?: ItemType.BANK_CARD)
        )
    }
    var itemTitle by remember { mutableStateOf(initialItem?.title ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var selectedColorHex by remember { mutableStateOf(initialItem?.colorHex ?: "#3B82F6") }
    var rating by remember { mutableStateOf(initialItem?.rating ?: 0f) }

    // Standard fields
    var primaryValue by remember { mutableStateOf(initialItem?.primaryValue ?: "") }
    var secondaryValue by remember { mutableStateOf(initialItem?.secondaryValue ?: "") }
    var extraData by remember { mutableStateOf(initialItem?.extraData ?: "") }

    // Bank Card parsed fields
    var bankName by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.BANK_CARD.id) initialItem.secondaryValue else ""
        )
    }
    var cardNumber by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.BANK_CARD.id) initialItem.primaryValue else ""
        )
    }
    var cardOwner by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.BANK_CARD.id) parseField(initialItem.extraData, "owner") else ""
        )
    }
    var cvv2 by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.BANK_CARD.id) parseField(initialItem.extraData, "cvv2") else ""
        )
    }
    var expireDate by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.BANK_CARD.id) parseField(initialItem.extraData, "exp") else ""
        )
    }
    var shebaNumber by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.BANK_CARD.id) parseField(initialItem.extraData, "sheba") else ""
        )
    }

    // Contact parsed fields
    var contactPhone by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.CONTACT.id) initialItem.primaryValue else ""
        )
    }
    var contactSecondaryPhone by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.CONTACT.id) initialItem.secondaryValue else ""
        )
    }
    var contactEmail by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.CONTACT.id) parseField(initialItem.extraData, "email") else ""
        )
    }

    // Social Media parsed fields
    var socialPlatform by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.SOCIAL_MEDIA.id) initialItem.secondaryValue.ifBlank { "تلگرام" } else "تلگرام"
        )
    }
    var socialUsername by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.SOCIAL_MEDIA.id) initialItem.primaryValue else ""
        )
    }
    var socialDirectLink by remember {
        mutableStateOf(
            if (initialItem?.type == ItemType.SOCIAL_MEDIA.id) initialItem.extraData else ""
        )
    }

    var passwordVisible by remember { mutableStateOf(false) }
    var secondaryPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val colorItem = GlassColors.getColorItem(selectedColorHex)
    val dialogScrollState = rememberScrollState()

    // 1. Item Type Picker Dialog
    if (showTypePickerPopup) {
        ItemTypePickerDialog(
            selectedType = selectedType,
            accentHex = selectedColorHex,
            onSelectType = { newType ->
                selectedType = newType
            },
            onDismiss = { showTypePickerPopup = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .heightIn(max = 700.dp)
                .testTag("add_item_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B).copy(alpha = 0.98f),
                    Color(0xFF0F172A).copy(alpha = 0.98f),
                    colorItem.primaryColor.copy(alpha = 0.35f)
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
                    .verticalScroll(dialogScrollState)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Beautiful 3D Glass Trigger to open the Type Selection Popup Dialog
                Text(
                    text = "نوع آیتم:",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                GlassmorphicBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("item_type_picker_trigger"),
                    shape = RoundedCornerShape(16.dp),
                    backgroundBrush = Brush.linearGradient(
                        colors = listOf(
                            colorItem.secondaryColor.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    borderBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.7f),
                            colorItem.highlightColor,
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    elevation = 6.dp,
                    onClick = { showTypePickerPopup = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .shadow(4.dp, CircleShape, spotColor = colorItem.secondaryColor)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(colorItem.highlightColor, colorItem.secondaryColor)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = selectedType.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = selectedType.titleFa,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = selectedType.categoryGroupFa,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorItem.highlightColor
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "تغییر نوع",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "تغییر نوع آیتم",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item Title field
                OutlinedTextField(
                    value = itemTitle,
                    onValueChange = {
                        itemTitle = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_title_input"),
                    label = {
                        Text(
                            when (selectedType) {
                                ItemType.BANK_CARD -> "عنوان کارت (مثال: کارت حقوق سامان)"
                                ItemType.CONTACT -> "نام مخاطب (مثال: دکتر احمدی)"
                                ItemType.SOCIAL_MEDIA -> "نام کانال یا صفحه (مثال: کانال خبر فوری)"
                                else -> "عنوان آیتم"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when (selectedType) {
                                ItemType.BANK_CARD -> "مثال: کارت اصلی بانک ملی..."
                                ItemType.CONTACT -> "مثال: علی محمدی..."
                                ItemType.SOCIAL_MEDIA -> "مثال: کانال آموزش کاتلین..."
                                ItemType.URL -> "مثال: وب‌سایت دانشگاه، گیت‌هاب..."
                                ItemType.EMAIL_PASSWORD -> "مثال: حساب جیمیل شرکت..."
                                ItemType.SECURITY_CODE -> "مثال: کلید پشتیبان ۲ مرحله‌ای..."
                                ItemType.AI_PROMPT -> "مثال: پرامپت تولید محتوا..."
                                ItemType.NOTE -> "مثال: نکات جلسه کاری..."
                                ItemType.CODE_SNIPPET -> "مثال: تابع محاسباتی کاتلین..."
                                ItemType.OTHER_TEXT -> "مثال: لایسنس ویندوز یا یادداشت..."
                            }
                        )
                    },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = Color(0xFFEF4444))
                        }
                    },
                    colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Specialized Input Sections according to ItemType
                when (selectedType) {
                    ItemType.BANK_CARD -> {
                        // Bank Name with quick suggestions chips
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_bank_name_input"),
                            label = { Text("نام بانک") },
                            placeholder = { Text("مثال: سامان، ملی، ملت، صادرات، بلو...") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick bank suggestion chips
                        val quickBanks = listOf("سامان", "ملی", "ملت", "صادرات", "بلو", "پاسارگاد", "رسالت", "سپه", "تجارت", "آینده")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickBanks.forEach { b ->
                                FilterChip(
                                    selected = bankName == b,
                                    onClick = { bankName = b },
                                    label = { Text(b, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = colorItem.secondaryColor,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White.copy(alpha = 0.08f),
                                        labelColor = Color.White.copy(alpha = 0.8f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Card Number (16 digits)
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = {
                                val filtered = it.filter { ch -> ch.isDigit() }.take(16)
                                cardNumber = filtered
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_card_number_input"),
                            label = { Text("شماره ۱۶ رقمی کارت بانکی") },
                            placeholder = { Text("6037991812345678") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        if (cardNumber.isNotBlank()) {
                            Text(
                                text = formatCardDisplay(cardNumber),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colorItem.highlightColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Owner Name
                        OutlinedTextField(
                            value = cardOwner,
                            onValueChange = { cardOwner = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_card_owner_input"),
                            label = { Text("نام مالک / دارنده کارت") },
                            placeholder = { Text("مثال: علی محمدی") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // CVV2 and Expire Date in 1 Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = cvv2,
                                onValueChange = {
                                    cvv2 = it.filter { ch -> ch.isDigit() }.take(5)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("item_card_cvv2_input"),
                                label = { Text("کد CVV2") },
                                placeholder = { Text("مثال: 842") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            OutlinedTextField(
                                value = expireDate,
                                onValueChange = { expireDate = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("item_card_expire_input"),
                                label = { Text("تاریخ انقضا") },
                                placeholder = { Text("06 / 28") },
                                singleLine = true,
                                colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Sheba Number (IBAN)
                        OutlinedTextField(
                            value = shebaNumber,
                            onValueChange = {
                                val clean = it.replace("\\s+".toRegex(), "").uppercase()
                                shebaNumber = clean
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_card_sheba_input"),
                            label = { Text("شماره شبا (IBAN)") },
                            placeholder = { Text("IR120560084280001234567801") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.CONTACT -> {
                        // Contact Phone (Primary)
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_contact_phone_input"),
                            label = { Text("شماره تلفن همراه / اصلی") },
                            placeholder = { Text("09121234567") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Contact Secondary Phone / Telephone
                        OutlinedTextField(
                            value = contactSecondaryPhone,
                            onValueChange = { contactSecondaryPhone = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_contact_secondary_phone_input"),
                            label = { Text("شماره تلفن دوم یا ثابت (اختیاری)") },
                            placeholder = { Text("02188776655") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Contact Email
                        OutlinedTextField(
                            value = contactEmail,
                            onValueChange = { contactEmail = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_contact_email_input"),
                            label = { Text("آدرس ایمیل مخاطب (اختیاری)") },
                            placeholder = { Text("contact@domain.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.SOCIAL_MEDIA -> {
                        Text(
                            text = "انتخاب پلتفرم یا پیام‌رسان:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val platforms = listOf("تلگرام", "اینستاگرام", "یوتیوب", "ایتا", "روبیکا", "بله", "توییتر/X", "لینکدین", "گیت‌هاب", "سایر")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            platforms.forEach { plat ->
                                FilterChip(
                                    selected = socialPlatform == plat,
                                    onClick = { socialPlatform = plat },
                                    label = { Text(plat, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = colorItem.secondaryColor,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White.copy(alpha = 0.08f),
                                        labelColor = Color.White.copy(alpha = 0.8f)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Channel ID or Username
                        OutlinedTextField(
                            value = socialUsername,
                            onValueChange = { socialUsername = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_social_username_input"),
                            label = { Text("آیدی یا شناسه کانال / پیج") },
                            placeholder = { Text("@channel_id یا username") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Direct Link
                        OutlinedTextField(
                            value = socialDirectLink,
                            onValueChange = { socialDirectLink = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_social_link_input"),
                            label = { Text("لینک مستقیم کانال یا گروه (URL)") },
                            placeholder = { Text("https://t.me/channel_id") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.URL -> {
                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_url_input"),
                            label = { Text("آدرس وب‌سایت (URL)") },
                            placeholder = { Text("https://example.com") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.EMAIL_PASSWORD -> {
                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_email_input"),
                            label = { Text("آدرس ایمیل یا نام کاربری") },
                            placeholder = { Text("user@domain.com") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = secondaryValue,
                            onValueChange = { secondaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_password_input"),
                            label = { Text("رمز عبور") },
                            placeholder = { Text("رمز عبور را وارد کنید") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "نمایش رمز",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.SECURITY_CODE -> {
                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_security_code_input"),
                            label = { Text("کد امنیتی / PIN / کلید ریکاوری") },
                            placeholder = { Text("کد رمزنگاری یا پین را وارد کنید") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "نمایش کد",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = secondaryValue,
                            onValueChange = { secondaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_security_password_input"),
                            label = { Text("رمز عبور ثانویه یا یادداشت محرمانه") },
                            placeholder = { Text("رمز مربوطه (اختیاری)") },
                            singleLine = true,
                            visualTransformation = if (secondaryPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { secondaryPasswordVisible = !secondaryPasswordVisible }) {
                                    Icon(
                                        imageVector = if (secondaryPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "نمایش رمز",
                                        tint = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.AI_PROMPT -> {
                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 180.dp)
                                .testTag("item_prompt_input"),
                            label = { Text("متن پرامپت هوش مصنوعی") },
                            placeholder = { Text("دستور، نقش و توضیحات پرامپت را وارد کنید...") },
                            maxLines = 6,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.NOTE -> {
                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 180.dp)
                                .testTag("item_note_input"),
                            label = { Text("متن یادداشت") },
                            placeholder = { Text("متن دلخواه خود را بنویسید...") },
                            maxLines = 6,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.CODE_SNIPPET -> {
                        OutlinedTextField(
                            value = secondaryValue,
                            onValueChange = { secondaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("item_code_lang_input"),
                            label = { Text("زبان برنامه‌نویسی") },
                            placeholder = { Text("مثال: Kotlin, Python, JS, SQL, Rust...") },
                            singleLine = true,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 110.dp, max = 200.dp)
                                .testTag("item_code_body_input"),
                            label = { Text("قطعه کد (Source Code)") },
                            placeholder = { Text("کد مورد نظر را اینجا وارد کنید...") },
                            maxLines = 8,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }

                    ItemType.OTHER_TEXT -> {
                        OutlinedTextField(
                            value = primaryValue,
                            onValueChange = { primaryValue = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 90.dp, max = 160.dp)
                                .testTag("item_other_text_input"),
                            label = { Text("محتوا یا داده متنی") },
                            placeholder = { Text("متن دلخواه خود را وارد کنید...") },
                            maxLines = 5,
                            colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description field (Optional)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_description_input"),
                    label = { Text("توضیحات تکمیلی (اختیاری)") },
                    placeholder = { Text("توضیحات یا یادآوری بیشتر...") },
                    maxLines = 3,
                    colors = customTextFieldColors(colorItem.secondaryColor, colorItem.highlightColor),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Rating section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "امتیاز و اولویت آیتم:",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StarRatingBar(
                            rating = rating,
                            onRatingChanged = { rating = it },
                            starSize = 18.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ColorPickerCarousel(
                    selectedHex = selectedColorHex,
                    onColorSelected = { selectedColorHex = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("item_dialog_cancel_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        AutoFitButtonText(
                            text = "انصراف",
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            if (itemTitle.trim().isBlank()) {
                                errorMessage = "لطفاً عنوان آیتم را وارد کنید"
                            } else {
                                val (primVal, secVal, extVal) = when (selectedType) {
                                    ItemType.BANK_CARD -> {
                                        val ext = "owner:${cardOwner.trim()}|cvv2:${cvv2.trim()}|exp:${expireDate.trim()}|sheba:${shebaNumber.trim()}"
                                        Triple(cardNumber.trim(), bankName.trim(), ext)
                                    }
                                    ItemType.CONTACT -> {
                                        val ext = "email:${contactEmail.trim()}"
                                        Triple(contactPhone.trim(), contactSecondaryPhone.trim(), ext)
                                    }
                                    ItemType.SOCIAL_MEDIA -> {
                                        Triple(socialUsername.trim(), socialPlatform.trim(), socialDirectLink.trim())
                                    }
                                    else -> {
                                        Triple(primaryValue.trim(), secondaryValue.trim(), extraData.trim())
                                    }
                                }

                                val entity = VaultItemEntity(
                                    id = initialItem?.id ?: 0,
                                    categoryId = categoryId,
                                    type = selectedType.id,
                                    title = itemTitle.trim(),
                                    description = description.trim(),
                                    primaryValue = primVal,
                                    secondaryValue = secVal,
                                    extraData = extVal,
                                    colorHex = selectedColorHex,
                                    rating = rating,
                                    createdAt = initialItem?.createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                onConfirm(entity)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("item_dialog_save_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorItem.secondaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        AutoFitButtonText(
                            text = if (initialItem == null) "ذخیره آیتم" else "بروزرسانی",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun parseField(extraData: String, key: String): String {
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
private fun customTextFieldColors(primary: Color, highlight: Color) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = primary,
    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
    focusedLabelColor = highlight,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    cursorColor = highlight
)
