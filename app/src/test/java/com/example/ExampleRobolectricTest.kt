package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CategoryEntity
import com.example.data.model.ItemType
import com.example.data.model.VaultItemEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("لینکدون", appName)
    }

    @Test
    fun `test item type conversion`() {
        val urlType = ItemType.fromId("URL")
        assertEquals(ItemType.URL, urlType)

        val emailType = ItemType.fromId("EMAIL_PASSWORD")
        assertEquals(ItemType.EMAIL_PASSWORD, emailType)

        val promptType = ItemType.fromId("AI_PROMPT")
        assertEquals(ItemType.AI_PROMPT, promptType)
    }

    @Test
    fun `test entity creation`() {
        val category = CategoryEntity(
            id = 1L,
            name = "پروژه‌های کاری",
            parentId = null,
            colorHex = "#3B82F6"
        )
        assertEquals("پروژه‌های کاری", category.name)
        assertEquals("#3B82F6", category.colorHex)

        val item = VaultItemEntity(
            id = 1L,
            categoryId = 1L,
            type = ItemType.URL.id,
            title = "گیت‌هاب",
            primaryValue = "https://github.com"
        )
        assertEquals("گیت‌هاب", item.title)
        assertEquals("https://github.com", item.primaryValue)
    }
}
