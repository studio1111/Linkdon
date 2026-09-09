package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.FontOption
import com.example.data.model.ThemeOption
import com.example.ui.components.CategoryCard
import com.example.data.model.CategoryEntity
import com.example.ui.theme.LinkdoonTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun category_card_screenshot() {
        val sampleCategory = CategoryEntity(
            id = 1L,
            name = "پروژه‌های کاری",
            parentId = null,
            colorHex = "#3B82F6"
        )

        composeTestRule.setContent {
            LinkdoonTheme(
                themeOption = ThemeOption.DARK,
                fontOption = FontOption.DEFAULT,
                textScale = 1.0f
            ) {
                CategoryCard(
                    category = sampleCategory,
                    isDark = true,
                    isGrid = true,
                    onClick = {},
                    onRename = {},
                    onMove = {},
                    onChangeColor = {},
                    onDelete = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/category_card.png")
    }
}
