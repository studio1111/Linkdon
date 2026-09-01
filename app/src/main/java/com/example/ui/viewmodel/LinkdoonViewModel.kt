package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.FontOption
import com.example.data.model.SortOption
import com.example.data.model.ThemeOption
import com.example.data.model.VaultItemEntity
import com.example.data.repository.LinkdoonRepository
import com.example.data.repository.PreferenceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchResults(
    val query: String = "",
    val categories: List<CategoryEntity> = emptyList(),
    val items: List<VaultItemEntity> = emptyList()
) {
    val isEmpty: Boolean get() = categories.isEmpty() && items.isEmpty()
    val isSearching: Boolean get() = query.isNotBlank()
    val totalCount: Int get() = categories.size + items.size
}

@OptIn(ExperimentalCoroutinesApi::class)
class LinkdoonViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = LinkdoonRepository(database.categoryDao(), database.vaultItemDao())
    private val prefRepo = PreferenceRepository(application)

    // Preference States
    val theme: StateFlow<ThemeOption> = prefRepo.theme
    val font: StateFlow<FontOption> = prefRepo.font
    val sortOption: StateFlow<SortOption> = prefRepo.sortOption
    val textScale: StateFlow<Float> = prefRepo.textScale
    val isGridLayout: StateFlow<Boolean> = prefRepo.isGridLayout
    val isRtl: StateFlow<Boolean> = prefRepo.isRtl

    // Navigation Stack for hierarchy
    private val _currentCategory = MutableStateFlow<CategoryEntity?>(null)
    val currentCategory: StateFlow<CategoryEntity?> = _currentCategory.asStateFlow()

    private val _navigationStack = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val navigationStack: StateFlow<List<CategoryEntity>> = _navigationStack.asStateFlow()

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // All categories & items for search and lookup
    val allCategories: StateFlow<List<CategoryEntity>> = repository.observeAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allItems: StateFlow<List<VaultItemEntity>> = repository.observeAllItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactive Search Results
    val searchResults: StateFlow<SearchResults> = combine(
        _searchQuery,
        repository.observeAllCategories(),
        repository.observeAllItems()
    ) { query, allCats, allItemsList ->
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            SearchResults(query = "")
        } else {
            val q = trimmed.lowercase()
            val matchedCats = allCats.filter { cat ->
                cat.name.lowercase().contains(q)
            }
            val matchedItems = allItemsList.filter { item ->
                item.title.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.primaryValue.lowercase().contains(q) ||
                item.secondaryValue.lowercase().contains(q) ||
                item.extraData.lowercase().contains(q) ||
                item.type.lowercase().contains(q)
            }
            SearchResults(
                query = trimmed,
                categories = matchedCats,
                items = matchedItems
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchResults()
    )

    // Dynamic Raw Categories
    private val rawCategories = _currentCategory.flatMapLatest { current ->
        if (current == null) {
            repository.getRootCategories()
        } else {
            repository.getSubcategories(current.id)
        }
    }

    // Sorted Categories
    val categories: StateFlow<List<CategoryEntity>> = combine(
        rawCategories,
        sortOption
    ) { list, sort ->
        when (sort) {
            SortOption.DATE_NEWEST -> list.sortedByDescending { it.createdAt }
            SortOption.DATE_OLDEST -> list.sortedBy { it.createdAt }
            SortOption.ALPHABETICAL_ASC -> list.sortedBy { it.name.lowercase() }
            SortOption.ALPHABETICAL_DESC -> list.sortedByDescending { it.name.lowercase() }
            SortOption.RATING_HIGHEST -> list.sortedWith(
                compareByDescending<CategoryEntity> { it.rating }.thenByDescending { it.createdAt }
            )
            SortOption.RATING_LOWEST -> list.sortedWith(
                compareBy<CategoryEntity> { it.rating }.thenByDescending { it.createdAt }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Dynamic Raw Items
    private val rawItems = _currentCategory.flatMapLatest { current ->
        if (current == null) {
            flowOf(emptyList())
        } else {
            repository.getItemsByCategoryId(current.id)
        }
    }

    // Sorted Items
    val items: StateFlow<List<VaultItemEntity>> = combine(
        rawItems,
        sortOption
    ) { list, sort ->
        when (sort) {
            SortOption.DATE_NEWEST -> list.sortedByDescending { it.createdAt }
            SortOption.DATE_OLDEST -> list.sortedBy { it.createdAt }
            SortOption.ALPHABETICAL_ASC -> list.sortedBy { it.title.lowercase() }
            SortOption.ALPHABETICAL_DESC -> list.sortedByDescending { it.title.lowercase() }
            SortOption.RATING_HIGHEST -> list.sortedWith(
                compareByDescending<VaultItemEntity> { it.rating }.thenByDescending { it.createdAt }
            )
            SortOption.RATING_LOWEST -> list.sortedWith(
                compareBy<VaultItemEntity> { it.rating }.thenByDescending { it.createdAt }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Stats
    val categoryCount: StateFlow<Int> = repository.getCategoryCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val subcategoryCount: StateFlow<Int> = repository.getSubcategoryCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val itemCount: StateFlow<Int> = repository.getItemCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Preferences Actions
    fun setTheme(theme: ThemeOption) = prefRepo.setTheme(theme)
    fun setFont(font: FontOption) = prefRepo.setFont(font)
    fun setSortOption(option: SortOption) = prefRepo.setSortOption(option)
    fun setTextScale(scale: Float) = prefRepo.setTextScale(scale)
    fun toggleLayoutMode() = prefRepo.toggleLayoutMode()
    fun setRtl(isRtl: Boolean) = prefRepo.setRtl(isRtl)
    fun toggleRtl() = prefRepo.toggleRtl()

    // Search Actions
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    // Navigation Actions
    fun navigateIntoCategory(category: CategoryEntity) {
        if (_searchQuery.value.isNotBlank()) {
            _searchQuery.value = ""
        }
        val currentStack = _navigationStack.value.toMutableList()
        currentStack.add(category)
        _navigationStack.value = currentStack
        _currentCategory.value = category
    }

    fun navigateBack(): Boolean {
        if (_searchQuery.value.isNotBlank()) {
            _searchQuery.value = ""
            return true
        }
        val currentStack = _navigationStack.value.toMutableList()
        if (currentStack.isNotEmpty()) {
            currentStack.removeAt(currentStack.size - 1)
            _navigationStack.value = currentStack
            _currentCategory.value = currentStack.lastOrNull()
            return true
        }
        return false
    }

    fun navigateToRoot() {
        _searchQuery.value = ""
        _navigationStack.value = emptyList()
        _currentCategory.value = null
    }

    fun findCategoryName(categoryId: Long): String {
        return allCategories.value.firstOrNull { it.id == categoryId }?.name ?: ""
    }

    // Category CRUD
    fun addCategory(name: String, colorHex: String, rating: Int = 0, parentId: Long? = _currentCategory.value?.id) {
        viewModelScope.launch {
            repository.insertCategory(
                name = name,
                parentId = parentId,
                colorHex = colorHex,
                rating = rating
            )
        }
    }

    fun updateCategory(id: Long, name: String, colorHex: String, rating: Int = 0) {
        viewModelScope.launch {
            repository.updateCategoryDetails(id, name, colorHex, rating)
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategoryById(id)
        }
    }

    // Item CRUD
    fun saveItem(item: VaultItemEntity) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.insertItem(item)
            } else {
                repository.updateItem(item)
            }
        }
    }

    fun updateItemColor(id: Long, colorHex: String) {
        viewModelScope.launch {
            repository.updateItemColor(id, colorHex)
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItemById(id)
        }
    }

    // Backup & Restore
    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportToJson()
            onResult(json)
        }
    }

    fun importBackup(jsonString: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.importFromJson(jsonString)
            if (result.isSuccess) {
                val pair = result.getOrNull() ?: Pair(0, 0)
                onResult(true, "${pair.first} دسته و ${pair.second} آیتم با موفقیت بازیابی شدند")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "خطا در بازیابی داده‌ها")
            }
        }
    }
}
