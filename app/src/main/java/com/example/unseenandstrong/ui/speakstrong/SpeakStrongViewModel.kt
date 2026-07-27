package com.example.unseenandstrong.ui.speakstrong

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unseenandstrong.data.local.script.ScriptDao
import com.example.unseenandstrong.data.local.script.ScriptEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SpeakStrongViewModel(
    scriptDao: ScriptDao
) : ViewModel() {

    enum class Tone {
        GENTLE,
        DIRECT,
        FIRM
    }

    private val _selectedTone = MutableStateFlow(Tone.GENTLE)
    val selectedTone: StateFlow<Tone> = _selectedTone.asStateFlow()

    private val _selectedCategory = MutableStateFlow(SpeakStrongCatalog.CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedScript = MutableStateFlow<ScriptEntity?>(null)
    val selectedScript: StateFlow<ScriptEntity?> = _selectedScript.asStateFlow()

    val scripts: StateFlow<List<ScriptEntity>> =
        combine(_selectedCategory, scriptDao.getAllScripts()) { category, allScripts ->
            SpeakStrongCatalog.filterScripts(allScripts, category)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun setTone(tone: Tone) {
        _selectedTone.value = tone
    }

    fun setCategory(category: String) {
        _selectedCategory.value = if (category in SpeakStrongCatalog.categories) {
            category
        } else {
            SpeakStrongCatalog.CATEGORY_ALL
        }
    }

    fun selectScript(script: ScriptEntity) {
        _selectedScript.value = script
    }

    fun clearSelectedScript() {
        _selectedScript.value = null
    }

    class Factory(
        private val scriptDao: ScriptDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SpeakStrongViewModel::class.java)) {
                return SpeakStrongViewModel(scriptDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        const val CATEGORY_ALL = SpeakStrongCatalog.CATEGORY_ALL
        const val CATEGORY_DOCTOR = SpeakStrongCatalog.CATEGORY_DOCTOR
        const val CATEGORY_WORK = SpeakStrongCatalog.CATEGORY_WORK
        const val CATEGORY_INSURANCE = SpeakStrongCatalog.CATEGORY_INSURANCE
        const val CATEGORY_FAMILY = SpeakStrongCatalog.CATEGORY_FAMILY
        const val CATEGORY_STRANGERS = SpeakStrongCatalog.CATEGORY_STRANGERS
        const val CATEGORY_BOUNDARY = SpeakStrongCatalog.CATEGORY_BOUNDARY
    }
}
