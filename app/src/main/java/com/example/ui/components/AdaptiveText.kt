package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * An intelligent Text composable that:
 * 1. Automatically reduces its font size (down to [minFontSize]) to fit available space without truncation.
 * 2. If the text is still longer than [collapsedMaxLines], tapping on it expands smoothly to reveal all text.
 * 3. Tapping again collapses it back to [collapsedMaxLines].
 */
@Composable
fun ExpandableAutoText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    collapsedMaxLines: Int = 1,
    minFontSize: TextUnit = 10.sp,
    enableTapToExpand: Boolean = true,
    style: TextStyle = LocalTextStyle.current
) {
    var isExpanded by remember { mutableStateOf(false) }
    var hasVisualOverflow by remember { mutableStateOf(false) }

    // Start with the provided or style font size
    val baseFontSize = if (fontSize != TextUnit.Unspecified) fontSize else (style.fontSize.takeIf { it != TextUnit.Unspecified } ?: 14.sp)
    var currentFontSize by remember(text, baseFontSize) { mutableStateOf(baseFontSize) }
    var readyToDraw by remember(text, baseFontSize) { mutableStateOf(false) }

    val clickModifier = if (enableTapToExpand) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            isExpanded = !isExpanded
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .animateContentSize(animationSpec = spring())
            .then(clickModifier)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = if (isExpanded) baseFontSize else currentFontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            softWrap = true,
            style = style,
            onTextLayout = { textLayoutResult ->
                if (!isExpanded) {
                    val didOverflow = textLayoutResult.hasVisualOverflow
                    hasVisualOverflow = didOverflow
                    if (didOverflow && currentFontSize.value > minFontSize.value) {
                        // Automatically downscale font size smoothly to fit
                        val nextSize = (currentFontSize.value - 1f).coerceAtLeast(minFontSize.value).sp
                        if (nextSize != currentFontSize) {
                            currentFontSize = nextSize
                        }
                    } else {
                        readyToDraw = true
                    }
                } else {
                    readyToDraw = true
                }
            },
            modifier = Modifier.drawWithContent {
                if (readyToDraw) {
                    drawContent()
                }
            }
        )
    }
}

/**
 * Text composable specially designed for Buttons, Chips, and compact Action Bars.
 * Scales down font size gracefully so labels never get cut off abruptly,
 * and if tapped expands or allows multiline wrap.
 */
@Composable
fun AutoFitButtonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    targetFontSize: TextUnit = 13.sp,
    minFontSize: TextUnit = 9.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textAlign: TextAlign = TextAlign.Center,
    maxLines: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    var currentFontSize by remember(text, targetFontSize) { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(text, targetFontSize) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = currentFontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        softWrap = true,
        style = style,
        onTextLayout = { layoutResult ->
            if (layoutResult.hasVisualOverflow && currentFontSize.value > minFontSize.value) {
                currentFontSize = (currentFontSize.value - 0.75f).coerceAtLeast(minFontSize.value).sp
            } else {
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        }
    )
}
