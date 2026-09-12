package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GlassColors

@Composable
fun AddCategoryDialog(
    initialName: String = "",
    initialColorHex: String = "#3B82F6",
    initialRating: Float = 0f,
    title: String = "افزودن دسته جدید",
    buttonLabel: String = "ذخیره دسته",
    isDark: Boolean = true,
    onConfirm: (name: String, colorHex: String, rating: Float) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedColorHex by remember { mutableStateOf(initialColorHex) }
    var rating by remember { mutableStateOf(initialRating) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val colorItem = GlassColors.getColorItem(selectedColorHex)

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("add_category_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = GlassColors.getOpaqueDialogBrush(isDark = isDark, accentColor = colorItem.secondaryColor),
            borderBrush = GlassColors.getOpaqueBorderBrush(isDark = isDark, accentColor = colorItem.highlightColor),
            elevation = 16.dp,
            shadowColor = if (isDark) colorItem.secondaryColor.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.12f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color(0xFF111827),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (it.isNotBlank()) errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_name_input"),
                    label = { Text("نام دسته یا زیر دسته") },
                    placeholder = { Text("مثال: سرویس‌های ابری، اکانت‌های مالی...") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = Color(0xFFEF4444))
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color.White else Color(0xFF111827),
                        unfocusedTextColor = if (isDark) Color.White else Color(0xFF111827),
                        focusedBorderColor = colorItem.secondaryColor,
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.3f) else Color(0xFF9CA3AF),
                        focusedLabelColor = if (isDark) colorItem.highlightColor else colorItem.secondaryColor,
                        unfocusedLabelColor = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF4B5563),
                        cursorColor = if (isDark) colorItem.highlightColor else colorItem.secondaryColor
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Rating section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "امتیاز و اولویت دسته‌بندی:",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isDark) Color.White.copy(alpha = 0.85f) else Color(0xFF111827)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        StarRatingBar(
                            rating = rating,
                            onRatingChanged = { rating = it },
                            starSize = 18.dp,
                            isDark = isDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                ColorPickerCarousel(
                    selectedHex = selectedColorHex,
                    isDark = isDark,
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
                            .testTag("category_dialog_cancel_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDark) Color.White else Color(0xFF374151)
                        )
                    ) {
                        AutoFitButtonText(
                            text = "انصراف",
                            color = if (isDark) Color.White else Color(0xFF374151)
                        )
                    }

                    Button(
                        onClick = {
                            if (name.trim().isBlank()) {
                                errorMessage = "لطفاً نام دسته را وارد کنید"
                            } else {
                                onConfirm(name.trim(), selectedColorHex, rating)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("category_dialog_save_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorItem.secondaryColor,
                            contentColor = Color.White
                        )
                    ) {
                        AutoFitButtonText(
                            text = buttonLabel,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
