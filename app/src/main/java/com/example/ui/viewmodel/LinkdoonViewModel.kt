package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.FirebaseCloudService
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
    private val cloudService = FirebaseCloudService()

    // Preference States
    val theme: StateFlow<ThemeOption> = prefRepo.theme
    val font: StateFlow<FontOption> = prefRepo.font
    val sortOption: StateFlow<SortOption> = prefRepo.sortOption
    val textScale: StateFlow<Float> = prefRepo.textScale
    val isGridLayout: StateFlow<Boolean> = prefRepo.isGridLayout
    val isRtl: StateFlow<Boolean> = prefRepo.isRtl

    // Security & Auth States
    val userEmail: StateFlow<String> = prefRepo.userEmail
    val userName: StateFlow<String> = prefRepo.userName
    val userPassword: StateFlow<String> = prefRepo.userPassword
    val isOfflineMode: StateFlow<Boolean> = prefRepo.isOfflineMode
    val appPin: StateFlow<String> = prefRepo.appPin
    val isSetupCompleted: StateFlow<Boolean> = prefRepo.isSetupCompleted

    // Cloud Sync Status
    private val _cloudSyncStatus = MutableStateFlow("همگام‌سازی ابری فعال است")
    val cloudSyncStatus: StateFlow<String> = _cloudSyncStatus.asStateFlow()

    init {
        viewModelScope.launch {
            seedDefaultCategoriesIfNeeded()
        }
    }

    /**
     * Seeds one default category for each item type (without any items inside).
     * Only inserts categories that do not already exist at the root level.
     */
    suspend fun seedDefaultCategoriesIfNeeded() {
        val existing = repository.getAllCategoriesList()
        val defaultCategories = listOf(
            CategoryEntity(name = "کارت و حساب بانکی", colorHex = "#10B981", orderIndex = 0),
            CategoryEntity(name = "مخاطب و شماره تماس", colorHex = "#3B82F6", orderIndex = 1),
            CategoryEntity(name = "کانال و شبکه اجتماعی", colorHex = "#EC4899", orderIndex = 2),
            CategoryEntity(name = "وب‌سایت / آدرس اینترنتی", colorHex = "#06B6D4", orderIndex = 3),
            CategoryEntity(name = "ایمیل و رمز عبور", colorHex = "#F59E0B", orderIndex = 4),
            CategoryEntity(name = "کد امنیتی و احراز هویت", colorHex = "#8B5CF6", orderIndex = 5),
            CategoryEntity(name = "پرامپت هوش مصنوعی", colorHex = "#6366F1", orderIndex = 6),
            CategoryEntity(name = "یادداشت و متن مهم", colorHex = "#14B8A6", orderIndex = 7),
            CategoryEntity(name = "کد و اسنیپت برنامه‌نویسی", colorHex = "#64748B", orderIndex = 8),
            CategoryEntity(name = "سایر داده‌ها و متن دلخواه", colorHex = "#84CC16", orderIndex = 9)
        )

        val toInsert = defaultCategories.filter { defaultCat ->
            existing.none { it.parentId == null && it.name.trim().equals(defaultCat.name.trim(), ignoreCase = true) }
        }

        if (toInsert.isNotEmpty()) {
            database.categoryDao().insertCategories(toInsert)
        }
    }

    // Session Unlock State (in-memory)
    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked.asStateFlow()

    fun unlockApp() {
        _isAppUnlocked.value = true
    }

    fun lockApp() {
        _isAppUnlocked.value = false
    }

    fun setCredentials(username: String, email: String, password: String = "") {
        prefRepo.setCredentials(username, email, password)
        _isAppUnlocked.value = true
        triggerAutoCloudSync()
    }

    /**
     * Register account with Cloud/Firebase and initial auto-sync.
     */
    fun registerWithCloud(
        username: String,
        email: String,
        password: String,
        pin: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val regResult = cloudService.registerOrUpdateUser(username, email, password)
            if (regResult.isSuccess) {
                prefRepo.setCredentials(username, email, password)
                if (pin.isNotBlank()) {
                    prefRepo.setAppPin(pin)
                }
                _isAppUnlocked.value = true
                triggerAutoCloudSync()
                onResult(true, "ثبت‌نام و اتصال به ابر فایربیس با موفقیت انجام شد")
            } else {
                onResult(false, regResult.exceptionOrNull()?.message ?: "خطا در ثبت‌نام")
            }
        }
    }

    /**
     * Login to existing Cloud/Firebase account with deduplication.
     */
    fun loginWithCloud(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val authResult = cloudService.authenticateUser(email, password)
            if (authResult.isSuccess) {
                val profile = authResult.getOrThrow()
                prefRepo.setCredentials(profile.username, profile.email, password)
                
                // Restore vault data with strict deduplication (preventing duplicate categories and items)
                val syncResult = cloudService.restoreVaultWithDeduplication(
                    email = profile.email,
                    categoryDao = database.categoryDao(),
                    vaultItemDao = database.vaultItemDao()
                )
                
                _isAppUnlocked.value = true
                _cloudSyncStatus.value = "همگام‌سازی ابری کامل شد"
                onResult(true, syncResult.message.ifBlank { "ورود با موفقیت انجام شد" })
            } else {
                onResult(false, authResult.exceptionOrNull()?.message ?: "اطلاعات ورود نامعتبر است")
            }
        }
    }

    /**
     * Recover Username and Password using registered Email from Cloud/Firebase.
     */
    fun recoverCredentials(
        email: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        viewModelScope.launch {
            val recoverResult = cloudService.recoverCredentialsByEmail(email)
            if (recoverResult.isSuccess) {
                val profile = recoverResult.getOrThrow()
                onResult(true, "نام کاربری: ${profile.username}\nرمز عبور: ${profile.password}", profile.password)
            } else {
                onResult(false, recoverResult.exceptionOrNull()?.message ?: "حسابی با این ایمیل یافت نشد", null)
            }
        }
    }

    /**
     * Log out of current account.
     */
    fun logout() {
        prefRepo.logoutUser()
        _isAppUnlocked.value = false
        _currentCategory.value = null
        _navigationStack.value = emptyList()
    }

    /**
     * Trigger auto-cloud sync and auto-export on every change.
     */
    fun triggerAutoCloudSync() {
        val email = userEmail.value
        val username = userName.value
        if (email.isBlank() || isOfflineMode.value) return

        viewModelScope.launch {
            try {
                val allCats = repository.getAllCategoriesList()
                val allItems = repository.getAllItemsList()
                cloudService.autoSaveVaultToCloud(email, username, allCats, allItems)
                _cloudSyncStatus.value = "ذخیره خودکار ابری انجام شد"
            } catch (e: Exception) {
                _cloudSyncStatus.value = "خطا در همگام‌سازی خودکار"
            }
        }
    }

    fun enterOfflineMode() {
        prefRepo.enterOfflineMode()
        _isAppUnlocked.value = true
    }

    fun setAppPin(pin: String) {
        prefRepo.setAppPin(pin)
    }

    fun removeAppPin() {
        prefRepo.removeAppPin()
    }

    fun clearAllData(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAllData()
            prefRepo.resetSecurityAccount()
            seedDefaultCategoriesIfNeeded()
            _currentCategory.value = null
            _navigationStack.value = emptyList()
            _isAppUnlocked.value = false
            onFinished()
        }
    }

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

    fun getCategoryBreadcrumb(categoryId: Long?): String {
        if (categoryId == null) return "دسته‌های اصلی (ریشه)"
        val cats = allCategories.value
        val map = cats.associateBy { it.id }
        val path = mutableListOf<String>()
        var curr: CategoryEntity? = map[categoryId]
        var depth = 0
        while (curr != null && depth < 20) {
            path.add(0, curr.name)
            curr = curr.parentId?.let { map[it] }
            depth++
        }
        return if (path.isEmpty()) "نامشخص" else path.joinToString(" › ")
    }

    fun getInvalidTargetCategoryIdsFor(categoryId: Long): Set<Long> {
        val result = mutableSetOf(categoryId)
        val cats = allCategories.value
        val queue = ArrayDeque<Long>()
        queue.add(categoryId)
        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            val children = cats.filter { it.parentId == curr }.map { it.id }
            for (childId in children) {
                if (result.add(childId)) {
                    queue.add(childId)
                }
            }
        }
        return result
    }

    // Category CRUD
    fun addCategory(name: String, colorHex: String, rating: Float = 0f, parentId: Long? = _currentCategory.value?.id) {
        viewModelScope.launch {
            repository.insertCategory(
                name = name,
                parentId = parentId,
                colorHex = colorHex,
                rating = rating
            )
            triggerAutoCloudSync()
        }
    }

    fun updateCategory(id: Long, name: String, colorHex: String, rating: Float = 0f) {
        viewModelScope.launch {
            repository.updateCategoryDetails(id, name, colorHex, rating)
            triggerAutoCloudSync()
        }
    }

    fun moveCategory(categoryId: Long, newParentId: Long?) {
        viewModelScope.launch {
            repository.moveCategory(categoryId, newParentId)
            if (_currentCategory.value?.id == categoryId) {
                val updated = repository.getCategoryById(categoryId)
                _currentCategory.value = updated
            }
            triggerAutoCloudSync()
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategoryById(id)
            triggerAutoCloudSync()
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
            triggerAutoCloudSync()
        }
    }

    fun moveItem(itemId: Long, newCategoryId: Long) {
        viewModelScope.launch {
            repository.moveItem(itemId, newCategoryId)
            triggerAutoCloudSync()
        }
    }

    fun updateItemColor(id: Long, colorHex: String) {
        viewModelScope.launch {
            repository.updateItemColor(id, colorHex)
            triggerAutoCloudSync()
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItemById(id)
            triggerAutoCloudSync()
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
