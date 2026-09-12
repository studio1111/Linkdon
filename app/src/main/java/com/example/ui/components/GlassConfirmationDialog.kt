package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.GlassColors

@Composable
fun GlassConfirmationDialog(
    title: String = "آیا از حذف مطمئن هستید؟",
    message: String = "این عملیات غیرقابل بازگشت است و تمامی محتویات داخل آن حذف خواهند شد.",
    confirmButtonText: String = "بله، حذف شود",
    cancelButtonText: String = "انصراف",
    isDark: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("delete_confirmation_dialog"),
            shape = RoundedCornerShape(28.dp),
            backgroundBrush = GlassColors.getOpaqueDialogBrush(isDark = isDark, accentColor = Color(0xFFEF4444)),
            borderBrush = GlassColors.getOpaqueBorderBrush(isDark = isDark, accentColor = Color(0xFFEF4444)),
            elevation = 16.dp,
            shadowColor = if (isDark) Color(0xFFEF4444).copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.12f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3D Glass Warning Icon Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0xFFEF4444))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFF991B1B)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "هشدار حذف",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExpandableAutoText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDark) Color.White else Color(0xFF111827),
                    textAlign = TextAlign.Center,
                    collapsedMaxLines = 2,
                    minFontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpandableAutoText(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color.White.copy(alpha = 0.75f) else Color(0xFF4B5563),
                    textAlign = TextAlign.Center,
                    collapsedMaxLines = 3,
                    minFontSize = 11.sp
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
                            .testTag("dialog_cancel_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDark) Color.White else Color(0xFF374151)
                        )
                    ) {
                        AutoFitButtonText(
                            text = cancelButtonText,
                            color = if (isDark) Color.White else Color(0xFF374151)
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("dialog_confirm_delete_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDC2626),
                            contentColor = Color.White
                        )
                    ) {
                        AutoFitButtonText(
                            text = confirmButtonText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
