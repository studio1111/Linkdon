package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.CategoryEntity
import com.example.data.model.VaultItemEntity
import com.example.ui.components.AddCategoryDialog
import com.example.ui.components.AddItemDialog
import com.example.ui.components.AddOptionChoiceDialog
import com.example.ui.components.CategoryCard
import com.example.ui.components.ColorPickerCarousel
import com.example.ui.components.GlassConfirmationDialog
import com.example.ui.components.GlassFloatingAddButton
import com.example.ui.components.GlassTopHeader
import com.example.ui.components.GlassmorphicBox
import com.example.ui.components.ItemDetailDialog
import com.example.ui.components.RightDrawerMenu
import com.example.ui.components.VaultItemCard
import com.example.ui.theme.GlassColors
import com.example.ui.theme.LinkdoonTheme
import com.example.ui.viewmodel.LinkdoonViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LinkdoonViewModel = viewModel()
            val themeOption by viewModel.theme.collectAsStateWithLifecycle()
            val fontOption by viewModel.font.collectAsStateWithLifecycle()
            val textScale by viewModel.textScale.collectAsStateWithLifecycle()
            val isRtl by viewModel.isRtl.collectAsStateWithLifecycle()

            LinkdoonTheme(
                themeOption = themeOption,
                fontOption = fontOption,
                textScale = textScale
            ) {
                // Configurable Layout Direction (RTL / LTR)
                CompositionLocalProvider(LocalLayoutDirection provides (if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        LinkdoonMainApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun LinkdoonMainApp(
    viewModel: LinkdoonViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentTheme by viewModel.theme.collectAsStateWithLifecycle()
    val currentFont by viewModel.font.collectAsStateWithLifecycle()
    val textScale by viewModel.textScale.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val isGridLayout by viewModel.isGridLayout.collectAsStateWithLifecycle()
    val isRtl by viewModel.isRtl.collectAsStateWithLifecycle()

    val currentCategory by viewModel.currentCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    val categoryCount by viewModel.categoryCount.collectAsStateWithLifecycle()
    val subcategoryCount by viewModel.subcategoryCount.collectAsStateWithLifecycle()
    val itemCount by viewModel.itemCount.collectAsStateWithLifecycle()

    // Dialog state holders
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddOptionChoiceDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToChangeColor by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    var itemToDetail by remember { mutableStateOf<VaultItemEntity?>(null) }
    var itemToEdit by remember { mutableStateOf<VaultItemEntity?>(null) }
    var itemToChangeColor by remember { mutableStateOf<VaultItemEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<VaultItemEntity?>(null) }

    // Intercept back button for search, category traversal, and drawer
    BackHandler(enabled = currentCategory != null || drawerState.isOpen || searchResults.isSearching) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (searchResults.isSearching) {
            viewModel.clearSearch()
        } else {
            viewModel.navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            RightDrawerMenu(
                currentTheme = currentTheme,
                onSelectTheme = { viewModel.setTheme(it) },
                currentFont = currentFont,
                onSelectFont = { viewModel.setFont(it) },
                currentSort = sortOption,
                onSelectSort = { viewModel.setSortOption(it) },
                isGridLayout = isGridLayout,
                onToggleLayout = { viewModel.toggleLayoutMode() },
                textScale = textScale,
                onTextScaleChange = { viewModel.setTextScale(it) },
                isRtl = isRtl,
                onToggleRtl = { viewModel.setRtl(it) },
                categoryCount = categoryCount,
                subcategoryCount = subcategoryCount,
                itemCount = itemCount,
                onExportJson = { onJsonReady ->
                    viewModel.exportBackup(onJsonReady)
                },
                onImportJson = { json, onResult ->
                    viewModel.importBackup(json, onResult)
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        // App Canvas: Directional Asymmetrical Gradient (dark to light)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            currentTheme.startGradient,
                            currentTheme.endGradient.copy(alpha = 0.90f),
                            currentTheme.startGradient.copy(alpha = 0.95f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top 3D Floating Glass Header (Integrated Search Bar + App Name + Magnifying Glass + Controls)
                GlassTopHeader(
                    isGridLayout = isGridLayout,
                    onToggleLayout = { viewModel.toggleLayoutMode() },
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    canNavigateBack = currentCategory != null || searchResults.isSearching,
                    onNavigateBack = {
                        if (searchResults.isSearching) {
                            viewModel.clearSearch()
                        } else {
                            viewModel.navigateBack()
                        }
                    },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onClearSearch = { viewModel.clearSearch() },
                    currentCategoryName = currentCategory?.name,
                    isDark = currentTheme.isDark
                )

                // Main Content View (Search Results OR Categories & Items)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (searchResults.isSearching) {
                        // Advanced Search Results View
                        SearchResultsView(
                            searchResults = searchResults,
                            isGridLayout = isGridLayout,
                            isDark = currentTheme.isDark,
                            onCategoryClick = { cat ->
                                viewModel.navigateIntoCategory(cat)
                            },
                            onItemClick = { item ->
                                itemToDetail = item
                            },
                            onItemEdit = { item ->
                                itemToEdit = item
                            },
                            onItemChangeColor = { item ->
                                itemToChangeColor = item
                            },
                            onItemDelete = { item ->
                                itemToDelete = item
                            }
                        )
                    } else if (categories.isEmpty() && items.isEmpty()) {
                        // Empty State with 3D Glass Illustration
                        EmptyStateView(
                            isRoot = currentCategory == null,
                            categoryName = currentCategory?.name ?: "",
                            onAddCategory = {
                                if (currentCategory == null) {
                                    showAddCategoryDialog = true
                                } else {
                                    showAddOptionChoiceDialog = true
                                }
                            }
                        )
                    } else {
                        AnimatedContent(
                            targetState = isGridLayout,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            label = "content_layout_mode"
                        ) { targetIsGrid ->
                            if (targetIsGrid) {
                                // 3D Grid View
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("categories_grid_view"),
                                    contentPadding = PaddingValues(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 10.dp,
                                        bottom = 96.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    // Categories & Subcategories
                                    items(categories, key = { "cat_${it.id}" }) { category ->
                                        CategoryCard(
                                            category = category,
                                            isDark = currentTheme.isDark,
                                            isGrid = true,
                                            onClick = { viewModel.navigateIntoCategory(category) },
                                            onRename = { categoryToEdit = category },
                                            onChangeColor = { categoryToChangeColor = category },
                                            onDelete = { categoryToDelete = category }
                                        )
                                    }

                                    // Items (inside category)
                                    items(items, key = { "item_${it.id}" }) { item ->
                                        VaultItemCard(
                                            item = item,
                                            isDark = currentTheme.isDark,
                                            isGrid = true,
                                            onClick = { itemToDetail = item },
                                            onEdit = { itemToEdit = item },
                                            onChangeColor = { itemToChangeColor = item },
                                            onDelete = { itemToDelete = item }
                                        )
                                    }
                                }
                            } else {
                                // 3D List View
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("categories_list_view"),
                                    contentPadding = PaddingValues(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 10.dp,
                                        bottom = 96.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(categories, key = { "cat_${it.id}" }) { category ->
                                        CategoryCard(
                                            category = category,
                                            isDark = currentTheme.isDark,
                                            isGrid = false,
                                            onClick = { viewModel.navigateIntoCategory(category) },
                                            onRename = { categoryToEdit = category },
                                            onChangeColor = { categoryToChangeColor = category },
                                            onDelete = { categoryToDelete = category }
                                        )
                                    }

                                    items(items, key = { "item_${it.id}" }) { item ->
                                        VaultItemCard(
                                            item = item,
                                            isDark = currentTheme.isDark,
                                            isGrid = false,
                                            onClick = { itemToDetail = item },
                                            onEdit = { itemToEdit = item },
                                            onChangeColor = { itemToChangeColor = item },
                                            onDelete = { itemToDelete = item }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3D Glass Blue Floating Add Button (+), Bottom-Left, slightly elevated above corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 24.dp, bottom = 28.dp)
            ) {
                GlassFloatingAddButton(
                    onClick = {
                        if (currentCategory == null) {
                            // On Main Screen: Add Category directly
                            showAddCategoryDialog = true
                        } else {
                            // Inside Category / Subcategory: Show choice dialog (Subcategory vs Item)
                            showAddOptionChoiceDialog = true
                        }
                    }
                )
            }
        }
    }

    // --- Dialogs ---

    // 1. Add Category / Subcategory Dialog
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            title = if (currentCategory == null) "افزودن دسته جدید" else "افزودن زیردسته به «${currentCategory?.name}»",
            buttonLabel = "ایجاد دسته",
            onConfirm = { name, colorHex, rating ->
                viewModel.addCategory(name, colorHex, rating)
                showAddCategoryDialog = false
            },
            onDismiss = { showAddCategoryDialog = false }
        )
    }

    // 2. Choice Dialog when pressing '+' inside category/subcategory
    if (showAddOptionChoiceDialog) {
        AddOptionChoiceDialog(
            categoryName = currentCategory?.name ?: "",
            onChooseSubcategory = {
                showAddOptionChoiceDialog = false
                showAddCategoryDialog = true
            },
            onChooseItem = {
                showAddOptionChoiceDialog = false
                showAddItemDialog = true
            },
            onDismiss = { showAddOptionChoiceDialog = false }
        )
    }

    // 3. Add Item Dialog
    if (showAddItemDialog && currentCategory != null) {
        AddItemDialog(
            categoryId = currentCategory!!.id,
            onConfirm = { item ->
                viewModel.saveItem(item)
                showAddItemDialog = false
            },
            onDismiss = { showAddItemDialog = false }
        )
    }

    // 4. Edit Category Name Dialog
    if (categoryToEdit != null) {
        AddCategoryDialog(
            initialName = categoryToEdit!!.name,
            initialColorHex = categoryToEdit!!.colorHex,
            initialRating = categoryToEdit!!.rating,
            title = "تغییر مشخصات دسته",
            buttonLabel = "بروزرسانی",
            onConfirm = { name, colorHex, rating ->
                viewModel.updateCategory(categoryToEdit!!.id, name, colorHex, rating)
                categoryToEdit = null
            },
            onDismiss = { categoryToEdit = null }
        )
    }

    // 5. Change Category Color Dialog (16 colors carousel)
    if (categoryToChangeColor != null) {
        var selectedColor by remember { mutableStateOf(categoryToChangeColor!!.colorHex) }
        val cat = categoryToChangeColor!!
        val colorItem = GlassColors.getColorItem(selectedColor)

        androidx.compose.ui.window.Dialog(onDismissRequest = { categoryToChangeColor = null }) {
            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(26.dp),
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E293B).copy(alpha = 0.98f),
                        Color(0xFF0F172A).copy(alpha = 0.98f),
                        colorItem.primaryColor.copy(alpha = 0.40f)
                    )
                ),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "انتخاب رنگ برای «${cat.name}»",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    ColorPickerCarousel(
                        selectedHex = selectedColor,
                        onColorSelected = { selectedColor = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = { categoryToChangeColor = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("انصراف", style = MaterialTheme.typography.labelMedium)
                        }
                        androidx.compose.material3.Button(
                            onClick = {
                                viewModel.updateCategory(cat.id, cat.name, selectedColor, cat.rating)
                                categoryToChangeColor = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = colorItem.secondaryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text("تایید رنگ", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }

    // 6. Delete Category Confirmation Dialog
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        GlassConfirmationDialog(
            title = "آیا از حذف دسته «${cat.name}» مطمئنید؟",
            message = "با حذف این دسته، تمامی زیردسته‌ها و آیتم‌های داخل آن به طور کامل پاک خواهند شد.",
            onConfirm = {
                viewModel.deleteCategory(cat.id)
                categoryToDelete = null
            },
            onDismiss = { categoryToDelete = null }
        )
    }

    // 7. Item Detail Dialog
    if (itemToDetail != null) {
        val item = itemToDetail!!
        ItemDetailDialog(
            item = item,
            onEdit = {
                itemToDetail = null
                itemToEdit = item
            },
            onDelete = {
                itemToDetail = null
                itemToDelete = item
            },
            onDismiss = { itemToDetail = null }
        )
    }

    // 8. Edit Item Dialog
    if (itemToEdit != null) {
        val item = itemToEdit!!
        AddItemDialog(
            initialItem = item,
            categoryId = item.categoryId,
            onConfirm = { updated ->
                viewModel.saveItem(updated)
                itemToEdit = null
            },
            onDismiss = { itemToEdit = null }
        )
    }

    // 9. Change Item Color Dialog
    if (itemToChangeColor != null) {
        var selectedColor by remember { mutableStateOf(itemToChangeColor!!.colorHex) }
        val item = itemToChangeColor!!
        val colorItem = GlassColors.getColorItem(selectedColor)

        androidx.compose.ui.window.Dialog(onDismissRequest = { itemToChangeColor = null }) {
            GlassmorphicBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(26.dp),
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E293B).copy(alpha = 0.98f),
                        Color(0xFF0F172A).copy(alpha = 0.98f),
                        colorItem.primaryColor.copy(alpha = 0.40f)
                    )
                ),
                elevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "انتخاب رنگ برای «${item.title}»",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    ColorPickerCarousel(
                        selectedHex = selectedColor,
                        onColorSelected = { selectedColor = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = { itemToChangeColor = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("انصراف", style = MaterialTheme.typography.labelMedium)
                        }
                        androidx.compose.material3.Button(
                            onClick = {
                                viewModel.updateItemColor(item.id, selectedColor)
                                itemToChangeColor = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = colorItem.secondaryColor,
                                contentColor = Color.White
                            )
                        ) {
                            Text("تایید رنگ", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }

    // 10. Delete Item Confirmation Dialog
    if (itemToDelete != null) {
        val item = itemToDelete!!
        GlassConfirmationDialog(
            title = "حذف آیتم «${item.title}»",
            message = "آیا مطمئن هستید که می‌خواهید این آیتم را از لیست حذف کنید؟",
            onConfirm = {
                viewModel.deleteItem(item.id)
                itemToDelete = null
            },
            onDismiss = { itemToDelete = null }
        )
    }
}

@Composable
private fun SearchResultsView(
    searchResults: com.example.ui.viewmodel.SearchResults,
    isGridLayout: Boolean,
    isDark: Boolean,
    onCategoryClick: (CategoryEntity) -> Unit,
    onItemClick: (VaultItemEntity) -> Unit,
    onItemEdit: (VaultItemEntity) -> Unit,
    onItemChangeColor: (VaultItemEntity) -> Unit,
    onItemDelete: (VaultItemEntity) -> Unit
) {
    if (searchResults.isEmpty) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            GlassmorphicBox(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                backgroundBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFFEF4444).copy(alpha = 0.35f), Color(0xFF7F1D1D).copy(alpha = 0.45f))
                ),
                elevation = 8.dp,
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "نتیجه‌ای یافت نشد",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "هیچ دسته، کارت بانکی، مخاطب یا آیتمی با عبارت «${searchResults.query}» پیدا نشد.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "نتایج جستجو برای «${searchResults.query}» (${searchResults.totalCount} مورد یافت شد):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF38BDF8)
                )
            }

            if (searchResults.categories.isNotEmpty()) {
                item {
                    Text(
                        text = "دسته‌بندی‌ها (${searchResults.categories.size}):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                items(searchResults.categories, key = { "search_cat_${it.id}" }) { cat ->
                    CategoryCard(
                        category = cat,
                        isDark = isDark,
                        isGrid = false,
                        onClick = { onCategoryClick(cat) },
                        onRename = {},
                        onChangeColor = {},
                        onDelete = {}
                    )
                }
            }

            if (searchResults.items.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "آیتم‌ها، کارت‌ها و داده‌ها (${searchResults.items.size}):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                items(searchResults.items, key = { "search_item_${it.id}" }) { item ->
                    VaultItemCard(
                        item = item,
                        isDark = isDark,
                        isGrid = false,
                        onClick = { onItemClick(item) },
                        onEdit = { onItemEdit(item) },
                        onChangeColor = { onItemChangeColor(item) },
                        onDelete = { onItemDelete(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    isRoot: Boolean,
    categoryName: String,
    onAddCategory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassmorphicBox(
            modifier = Modifier.size(90.dp),
            shape = CircleShape,
            backgroundBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF38BDF8).copy(alpha = 0.35f),
                    Color(0xFF1E40AF).copy(alpha = 0.45f)
                )
            ),
            borderBrush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)
            ),
            elevation = 10.dp,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRoot) Icons.Default.FolderOpen else Icons.Default.Inventory2,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isRoot) "هنوز دسته‌ای ایجاد نشده است" else "این دسته در حال حاضر خالی است",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isRoot) {
                "برای ساخت اولین دسته شیشه‌ای، دکمه آبی رنگ «+» در پایین صفحه را لمس کنید."
            } else {
                "دکمه «+» در پایین صفحه را بزنید تا زیردسته یا آیتم‌های جدید به «$categoryName» اضافه شود."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
