package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ThemeOption

/**
 * Advanced & Professional First-Time Setup & Authentication Screen for Linkdoon.
 * Features:
 * 1. Dual Mode: "ثبت‌نام و همگام‌سازی ابری فایربیس" (Register) & "ورود به حساب" (Sign In)
 * 2. Dedicated Email, Username, Password, and Password Confirmation
 * 3. Fast Unlock PIN (4-8 digits, optional)
 * 4. Automatic Cloud Sync & Output to Firebase with intelligent deduplication
 * 5. Account Recovery (Username & Password recovery via Email from Firebase)
 * 6. Direct Offline Mode entry with safety warning
 */
@Composable
fun InitialAuthScreen(
    currentTheme: ThemeOption,
    onRegisterUser: (username: String, email: String, password: String, pin: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onLoginUser: (email: String, password: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onRecoverCredentials: (email: String, onResult: (Boolean, String, String?) -> Unit) -> Unit,
    onEnterOfflineMode: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Register, 1: Sign In

    // Register State
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPin by remember { mutableStateOf("") }
    var regConfirmPin by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regConfirmPasswordVisible by remember { mutableStateOf(false) }
    var regPinVisible by remember { mutableStateOf(false) }

    // Sign In State
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Dialog & Feedback State
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showOfflineWarningDialog by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF080D1A)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF070B14),
                            Color(0xFF0F172A),
                            currentTheme.startGradient.copy(alpha = 0.38f),
                            Color(0xFF070B14)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Glowing Badge with Shield & Cloud
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .shadow(20.dp, CircleShape, spotColor = currentTheme.accentColor)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    currentTheme.accentColor,
                                    currentTheme.startGradient,
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.6f), currentTheme.accentColor)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "امنیت و ابر لینکدون",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "گاوصندوق ابری و هوشمند لینکدون",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "مدیریت امن حساب و همگام‌سازی خودکار ابری",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Main Advanced Glass Card
                GlassmorphicBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    backgroundBrush = Brush.linearGradient(
                        listOf(
                            Color(0xFF1E293B).copy(alpha = 0.94f),
                            Color(0xFF0F172A).copy(alpha = 0.97f)
                        )
                    ),
                    borderBrush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            currentTheme.accentColor.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    elevation = 12.dp,
                    shadowColor = currentTheme.accentColor.copy(alpha = 0.25f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Modern Tab Switcher Pill
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Register Tab Button
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selectedTab == 0) currentTheme.accentColor.copy(alpha = 0.32f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = if (selectedTab == 0) 1.dp else 0.dp,
                                        color = if (selectedTab == 0) currentTheme.accentColor else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedTab = 0
                                        errorMessage = null
                                        successMessage = null
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AppRegistration,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ثبت‌نام و ایجاد حساب",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }

                            // Sign In Tab Button
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selectedTab == 1) currentTheme.accentColor.copy(alpha = 0.32f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = if (selectedTab == 1) 1.dp else 0.dp,
                                        color = if (selectedTab == 1) currentTheme.accentColor else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedTab = 1
                                        errorMessage = null
                                        successMessage = null
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ورود به حساب کاربری",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // TAB 0: REGISTER & CLOUD SYNC
                        if (selectedTab == 0) {
                            // Dedicated Username Field
                            OutlinedTextField(
                                value = regUsername,
                                onValueChange = {
                                    regUsername = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_username_input"),
                                label = { Text("نام کاربری اختصاصی *") },
                                placeholder = { Text("مثال: Studio1111") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Valid Email Field
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = {
                                    regEmail = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_email_input"),
                                label = { Text("آدرس ایمیل معتبر *") },
                                placeholder = { Text("www.studio1111@gmail.com") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Password Field
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = {
                                    regPassword = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_password_input"),
                                label = { Text("رمز عبور حساب کاربری *") },
                                placeholder = { Text("حداقل ۴ نویسه") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "نمایش رمز عبور",
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Confirm Password Field
                            OutlinedTextField(
                                value = regConfirmPassword,
                                onValueChange = {
                                    regConfirmPassword = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_password_confirm_input"),
                                label = { Text("تایید رمز عبور حساب *") },
                                placeholder = { Text("تکرار دقیق رمز عبور") },
                                leadingIcon = {
                                    Icon(Icons.Default.LockReset, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regConfirmPasswordVisible = !regConfirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (regConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "نمایش تکرار رمز عبور",
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                visualTransformation = if (regConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Optional Fast PIN (4-8 digits)
                            OutlinedTextField(
                                value = regPin,
                                onValueChange = {
                                    if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                                        regPin = it
                                        errorMessage = null
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_pin_input"),
                                label = { Text("رمز ورود سریع عددی (۴ تا ۸ رقم - اختیاری)") },
                                placeholder = { Text("پین ۴ تا ۸ رقمی جهت قفل سریع برنامه") },
                                leadingIcon = {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { regPinVisible = !regPinVisible }) {
                                        Icon(
                                            imageVector = if (regPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "نمایش پین",
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                visualTransformation = if (regPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            if (regPin.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = regConfirmPin,
                                    onValueChange = {
                                        if (it.length <= 8 && it.all { char -> char.isDigit() }) {
                                            regConfirmPin = it
                                            errorMessage = null
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("setup_pin_confirm_input"),
                                    label = { Text("تکرار رمز ورود سریع عددی") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Key, contentDescription = null, tint = currentTheme.accentColor)
                                    },
                                    visualTransformation = if (regPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    singleLine = true,
                                    colors = authTextFieldColors(currentTheme.accentColor),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Features highlight banner
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.04f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ذخیره‌سازی و همگام‌سازی خودکار در پایگاه ابری فایربیس", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("خروجی و بازیابی خودکار هوشمند بدون ایجاد فایل تکراری", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("قابلیت بازیابی نام کاربری و رمز عبور از طریق ایمیل", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                                }
                            }

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFF43F5E),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }

                            if (successMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = successMessage!!,
                                    color = Color(0xFF10B981),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Register Submit Button
                            Button(
                                onClick = {
                                    val trimmedUser = regUsername.trim()
                                    val trimmedEmail = regEmail.trim()
                                    val pass = regPassword.trim()
                                    val confirmPass = regConfirmPassword.trim()

                                    if (trimmedUser.isBlank()) {
                                        errorMessage = "لطفاً نام کاربری اختصاصی خود را وارد کنید"
                                        return@Button
                                    }
                                    if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                        errorMessage = "لطفاً آدرس ایمیل معتبر وارد کنید"
                                        return@Button
                                    }
                                    if (pass.length < 4) {
                                        errorMessage = "رمز عبور باید حداقل ۴ نویسه باشد"
                                        return@Button
                                    }
                                    if (pass != confirmPass) {
                                        errorMessage = "تکرار رمز عبور با رمز وارد شده یکسان نیست"
                                        return@Button
                                    }
                                    if (regPin.isNotEmpty()) {
                                        if (regPin.length < 4 || regPin.length > 8) {
                                            errorMessage = "رمز ورود عددی باید بین ۴ تا ۸ رقم باشد"
                                            return@Button
                                        }
                                        if (regPin != regConfirmPin) {
                                            errorMessage = "تکرار رمز ورود عددی مطابقت ندارد"
                                            return@Button
                                        }
                                    }

                                    isLoading = true
                                    errorMessage = null
                                    onRegisterUser(trimmedUser, trimmedEmail, pass, regPin) { success, msg ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_register_auth"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ثبت‌نام و اتصال به Firebase",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                        }

                        // TAB 1: SIGN IN & CLOUD RESTORE
                        if (selectedTab == 1) {
                            Text(
                                text = "ورود به حساب و بازیابی خودکار اطلاعات از فضای ابری",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Login Email Field
                            OutlinedTextField(
                                value = loginEmail,
                                onValueChange = {
                                    loginEmail = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email_input"),
                                label = { Text("آدرس ایمیل ثبت شده *") },
                                placeholder = { Text("www.studio1111@gmail.com") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Login Password Field
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input"),
                                label = { Text("رمز عبور حساب *") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = currentTheme.accentColor)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "نمایش رمز عبور",
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                colors = authTextFieldColors(currentTheme.accentColor),
                                shape = RoundedCornerShape(14.dp)
                            )

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFF43F5E),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Login & Restore Button
                            Button(
                                onClick = {
                                    val trimmedEmail = loginEmail.trim()
                                    val pass = loginPassword.trim()
                                    if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                        errorMessage = "لطفاً ایمیل معتبر حساب خود را وارد کنید"
                                        return@Button
                                    }
                                    if (pass.isBlank()) {
                                        errorMessage = "لطفاً رمز عبور حساب خود را وارد کنید"
                                        return@Button
                                    }

                                    isLoading = true
                                    errorMessage = null
                                    onLoginUser(trimmedEmail, pass) { success, msg ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("btn_login_auth"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ورود و بازیابی هوشمند اطلاعات",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Forgot Password / Username link
                            TextButton(
                                onClick = {
                                    showRecoveryDialog = true
                                    errorMessage = null
                                },
                                modifier = Modifier.testTag("btn_forgot_credentials")
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, tint = currentTheme.accentColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "فراموشی رمز عبور یا نام کاربری؟ (بازیابی با ایمیل)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = currentTheme.accentColor
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Offline Mode Option
                OutlinedButton(
                    onClick = { showOfflineWarningDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_offline_entry"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.85f)),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                        )
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ورود در حالت آفلاین (بدون حساب ابری)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Account Credentials Recovery Dialog (Username & Password Recovery via Email)
    if (showRecoveryDialog) {
        AccountCredentialsRecoveryDialog(
            currentTheme = currentTheme,
            initialEmail = loginEmail.ifBlank { regEmail },
            onRecover = onRecoverCredentials,
            onDismiss = { showRecoveryDialog = false },
            onUseRecovered = { recoveredEmail, recoveredPassword ->
                loginEmail = recoveredEmail
                loginPassword = recoveredPassword
                selectedTab = 1
                showRecoveryDialog = false
            }
        )
    }

    // Offline Warning Dialog
    if (showOfflineWarningDialog) {
        Dialog(onDismissRequest = { showOfflineWarningDialog = false }) {
            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF1E1B2E), Color(0xFF0F172A))
                ),
                borderBrush = Brush.linearGradient(
                    listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "هشدار",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "هشدار مهم ورود در حالت آفلاین",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "در صورت انتخاب ورود آفلاین، اطلاعات شما تنها روی حافظه موقت دستگاه نگهداری می‌شود و قابلیت همگام‌سازی ابری فایربیس و بازیابی خودکار داده‌ها فعال نخواهد بود.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                showOfflineWarningDialog = false
                                onEnterOfflineMode()
                                Toast.makeText(context, "ورود در حالت آفلاین انجام شد", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                        ) {
                            Text("متوجه‌ام، ورود آفلاین", style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { showOfflineWarningDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("بازگشت و ثبت‌نام", style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Account Recovery Dialog:
 * Queries Firebase for Username & Password associated with the entered Email.
 */
@Composable
fun AccountCredentialsRecoveryDialog(
    currentTheme: ThemeOption,
    initialEmail: String = "",
    onRecover: (email: String, onResult: (Boolean, String, String?) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onUseRecovered: (email: String, password: String) -> Unit
) {
    val context = LocalContext.current
    var emailInput by remember { mutableStateOf(initialEmail) }
    var recoveredUsername by remember { mutableStateOf<String?>(null) }
    var recoveredPassword by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label در حافظه کپی شد", Toast.LENGTH_SHORT).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(26.dp),
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF1E293B).copy(alpha = 0.98f), Color(0xFF0F172A).copy(alpha = 0.98f))
            ),
            borderBrush = Brush.linearGradient(
                listOf(currentTheme.accentColor.copy(alpha = 0.7f), Color.White.copy(alpha = 0.3f))
            ),
            elevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(currentTheme.accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = null,
                        tint = currentTheme.accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "بازیابی نام کاربری و رمز عبور",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "ایمیل ثبت شده خود در Firebase را وارد کنید تا اطلاعات کاربری استعلام شود",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recovery_email_input"),
                    label = { Text("ایمیل ثبت شده *") },
                    placeholder = { Text("www.studio1111@gmail.com") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = currentTheme.accentColor)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = authTextFieldColors(currentTheme.accentColor),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFF43F5E),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                if (recoveredUsername != null && recoveredPassword != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.12f))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "اطلاعات حساب با موفقیت از Firebase بازیابی شد:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF6EE7B7)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "نام کاربری: $recoveredUsername",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            IconButton(onClick = { copyToClipboard("نام کاربری", recoveredUsername!!) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "کپی نام کاربری", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "رمز عبور: $recoveredPassword",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            IconButton(onClick = { copyToClipboard("رمز عبور", recoveredPassword!!) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "کپی رمز عبور", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onUseRecovered(emailInput.trim(), recoveredPassword!!)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                    ) {
                        Text(
                            text = "ورود مستقیم با این اطلاعات",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val trimmed = emailInput.trim()
                            if (trimmed.isBlank() || !trimmed.contains("@")) {
                                errorMessage = "لطفاً ایمیل معتبر وارد کنید"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            onRecover(trimmed) { success, userOrMsg, pass ->
                                isLoading = false
                                if (success && pass != null) {
                                    recoveredUsername = userOrMsg
                                    recoveredPassword = pass
                                } else {
                                    errorMessage = userOrMsg
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_perform_recovery"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF0F172A), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "استعلام و بازیابی حساب",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("بستن", style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}

/**
 * Lock Screen when App PIN is enabled.
 * Provides PIN keypad and "Forgot Password" email recovery flow.
 */
@Composable
fun AppLockScreen(
    currentTheme: ThemeOption,
    registeredEmail: String,
    savedPin: String,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    var enteredPin by remember { mutableStateOf("") }
    var errorPin by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0B1120)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0B1120),
                            Color(0xFF0F172A),
                            currentTheme.startGradient.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(10.dp, CircleShape, spotColor = currentTheme.accentColor)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(currentTheme.accentColor, currentTheme.startGradient)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "قفل برنامه",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "گاوصندوق لینکدون قفل است",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "رمز عبور عددی (۴ تا ۸ رقم) خود را وارد کنید",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // PIN Display Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val maxDots = if (savedPin.length in 4..8) savedPin.length else 6
                    for (i in 0 until maxDots) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        errorPin -> Color(0xFFF43F5E)
                                        isFilled -> currentTheme.accentColor
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isFilled) currentTheme.accentColor else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                if (errorPin) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "رمز عبور نادرست است",
                        color = Color(0xFFF43F5E),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Keypad (1 to 9, 0, Backspace)
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "⌫")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    keys.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { digit ->
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(
                                            width = 1.dp,
                                            color = Color.White.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            errorPin = false
                                            when (digit) {
                                                "C" -> enteredPin = ""
                                                "⌫" -> {
                                                    if (enteredPin.isNotEmpty()) {
                                                        enteredPin = enteredPin.dropLast(1)
                                                    }
                                                }
                                                else -> {
                                                    if (enteredPin.length < 8) {
                                                        enteredPin += digit
                                                        if (enteredPin == savedPin) {
                                                            onUnlocked()
                                                        } else if (enteredPin.length == savedPin.length && enteredPin != savedPin) {
                                                            errorPin = true
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Forgot Password / Recovery button
                if (registeredEmail.isNotBlank()) {
                    OutlinedButton(
                        onClick = { showRecoveryDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = currentTheme.accentColor)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "فراموشی رمز عبور و بازیابی با ایمیل",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }

    // Password Recovery Dialog via Email
    if (showRecoveryDialog) {
        Dialog(onDismissRequest = { showRecoveryDialog = false }) {
            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                )
            ) {
                var emailInput by remember { mutableStateOf("") }
                var recoveryMessage by remember { mutableStateOf<String?>(null) }
                var isSuccess by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بازیابی رمز عبور از طریق ایمیل",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "برای دریافت راهنمای بازنشانی، لطفاً آدرس ایمیل ثبت‌شده خود را وارد کنید:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            recoveryMessage = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ایمیل ثبت شده") },
                        placeholder = { Text(registeredEmail) },
                        singleLine = true,
                        colors = authTextFieldColors(currentTheme.accentColor),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (recoveryMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = recoveryMessage!!,
                            color = if (isSuccess) Color(0xFF34D399) else Color(0xFFF43F5E),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (emailInput.trim().equals(registeredEmail.trim(), ignoreCase = true)) {
                                    isSuccess = true
                                    recoveryMessage = "تایید شد! رمز عبور شما: $savedPin"
                                } else {
                                    isSuccess = false
                                    recoveryMessage = "ایمیل وارد شده با ایمیل ثبت شده مطابقت ندارد"
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                        ) {
                            Text("تایید و بازیابی", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0F172A))
                        }

                        OutlinedButton(
                            onClick = { showRecoveryDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("بستن", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Delete All Data confirmation dialog.
 * Requires user to type their username to confirm irreversible deletion.
 */
@Composable
fun DeleteAllDataDialog(
    currentTheme: ThemeOption,
    expectedUsername: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var typedConfirmation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val confirmationTarget = if (expectedUsername.isNotBlank()) expectedUsername else "حذف"

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            backgroundBrush = Brush.linearGradient(
                listOf(Color(0xFF2A080C), Color(0xFF0F172A))
            ),
            borderBrush = Brush.linearGradient(
                listOf(Color(0xFFEF4444), Color(0xFFF43F5E).copy(alpha = 0.5f))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = "حذف کلی",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "حذف کامل کلیه اطلاعات و حساب",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "هشدار: تمامی دسته‌بندی‌ها، آیتم‌ها، رمزها و اطلاعات گاوصندوق شما برای همیشه پاک خواهد شد و قابل بازگشت نخواهد بود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "جهت تایید نهایی، عبارت «$confirmationTarget» را در کادر زیر وارد نمایید:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFFCA5A5),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = typedConfirmation,
                    onValueChange = {
                        typedConfirmation = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delete_all_confirmation_input"),
                    placeholder = { Text(confirmationTarget) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFEF4444),
                        unfocusedBorderColor = Color(0xFFEF4444).copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (typedConfirmation.trim() == confirmationTarget.trim()) {
                                onConfirmDelete()
                            } else {
                                errorMessage = "عبارت وارد شده تطابق ندارد"
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_confirm_delete_all"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("حذف دائم اطلاعات", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("انصراف", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun authTextFieldColors(accentColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = accentColor,
    unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
    focusedLabelColor = accentColor,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = accentColor
)
