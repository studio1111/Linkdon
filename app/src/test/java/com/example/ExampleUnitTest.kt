package com.example

import com.example.data.model.CategoryEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for category hierarchy and move destination calculations.
 */
class ExampleUnitTest {

    @Test
    fun `test circular dependency prevention when moving categories`() {
        // Hierarchy:
        // Cat 1 (Work)
        //   -> Cat 2 (Projects)
        //        -> Cat 3 (Android App)
        // Cat 4 (Personal)

        val categories = listOf(
            CategoryEntity(id = 1L, name = "کارها", parentId = null),
            CategoryEntity(id = 2L, name = "پروژه‌ها", parentId = 1L),
            CategoryEntity(id = 3L, name = "اپلیکیشن اندروید", parentId = 2L),
            CategoryEntity(id = 4L, name = "شخصی", parentId = null)
        )

        // Helper function mimicking LinkdoonViewModel.getInvalidTargetCategoryIdsFor
        fun getInvalidTargetCategoryIdsFor(categoryId: Long): Set<Long> {
            val invalidIds = mutableSetOf(categoryId)
            val queue = ArrayDeque<Long>()
            queue.add(categoryId)

            while (queue.isNotEmpty()) {
                val currentId = queue.removeFirst()
                val directChildren = categories.filter { it.parentId == currentId }
                for (child in directChildren) {
                    if (invalidIds.add(child.id)) {
                        queue.add(child.id)
                    }
                }
            }
            return invalidIds
        }

        // Cat 1 cannot be moved into itself, Cat 2, or Cat 3
        val invalidForCat1 = getInvalidTargetCategoryIdsFor(1L)
        assertEquals(setOf(1L, 2L, 3L), invalidForCat1)
        assertTrue(invalidForCat1.contains(1L))
        assertTrue(invalidForCat1.contains(2L))
        assertTrue(invalidForCat1.contains(3L))
        assertFalse(invalidForCat1.contains(4L))

        // Cat 2 cannot be moved into itself or Cat 3
        val invalidForCat2 = getInvalidTargetCategoryIdsFor(2L)
        assertEquals(setOf(2L, 3L), invalidForCat2)
        assertFalse(invalidForCat2.contains(1L))
        assertFalse(invalidForCat2.contains(4L))

        // Cat 3 cannot be moved into Cat 3 only
        val invalidForCat3 = getInvalidTargetCategoryIdsFor(3L)
        assertEquals(setOf(3L), invalidForCat3)
        assertFalse(invalidForCat3.contains(1L))
        assertFalse(invalidForCat3.contains(2L))
        assertFalse(invalidForCat3.contains(4L))
    }

    @Test
    fun `test category breadcrumb generation`() {
        val categories = listOf(
            CategoryEntity(id = 1L, name = "کارها", parentId = null),
            CategoryEntity(id = 2L, name = "پروژه‌ها", parentId = 1L),
            CategoryEntity(id = 3L, name = "اپلیکیشن اندروید", parentId = 2L)
        )

        fun getCategoryBreadcrumb(categoryId: Long?): String {
            if (categoryId == null) return "صفحه اصلی (ریشه)"
            val trail = mutableListOf<String>()
            var current: CategoryEntity? = categories.find { it.id == categoryId }
            var safety = 0
            while (current != null && safety++ < 20) {
                trail.add(0, current.name)
                val parentId = current.parentId ?: break
                current = categories.find { it.id == parentId }
            }
            return if (trail.isEmpty()) "صفحه اصلی (ریشه)" else trail.joinToString(" › ")
        }

        assertEquals("صفحه اصلی (ریشه)", getCategoryBreadcrumb(null))
        assertEquals("کارها", getCategoryBreadcrumb(1L))
        assertEquals("کارها › پروژه‌ها", getCategoryBreadcrumb(2L))
        assertEquals("کارها › پروژه‌ها › اپلیکیشن اندروید", getCategoryBreadcrumb(3L))
    }
}

