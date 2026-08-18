package com.example.unseenandstrong.ui.speakstrong

import com.example.unseenandstrong.data.local.script.ScriptDao
import com.example.unseenandstrong.data.local.script.ScriptEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Test

class SpeakStrongSupportTest {
    private val scripts = listOf(
        ScriptEntity(1, "Doctor", "Doctor", "g", "d", "f"),
        ScriptEntity(2, "Insurance", "Insurance", "g", "d", "f")
    )

    @Test
    fun categoryFilteringSupportsAllAndSpecificCategories() {
        assertEquals(scripts, SpeakStrongCatalog.filterScripts(scripts, SpeakStrongCatalog.CATEGORY_ALL))
        assertEquals(
            listOf(scripts[1]),
            SpeakStrongCatalog.filterScripts(scripts, SpeakStrongCatalog.CATEGORY_INSURANCE)
        )
        assertEquals(
            listOf("All", "Doctor", "Work", "Insurance", "Family", "Strangers", "Boundary"),
            SpeakStrongCatalog.categories
        )
    }

    @Test
    fun toneSelectionRemainsStableWhenCategoryChanges() {
        val viewModel = SpeakStrongViewModel(
            object : ScriptDao {
                override suspend fun insertAll(scripts: List<ScriptEntity>): List<Long> = emptyList()
                override fun getAllScripts(): Flow<List<ScriptEntity>> = flowOf(scripts)
                override fun getScriptsByCategory(category: String): Flow<List<ScriptEntity>> =
                    flowOf(scripts.filter { it.category == category })
                override suspend fun getScriptById(id: Long): ScriptEntity? =
                    scripts.firstOrNull { it.id == id }
            }
        )

        viewModel.setTone(SpeakStrongViewModel.Tone.FIRM)
        viewModel.setCategory(SpeakStrongCatalog.CATEGORY_INSURANCE)

        assertEquals(SpeakStrongViewModel.Tone.FIRM, viewModel.selectedTone.value)
        assertEquals(SpeakStrongCatalog.CATEGORY_INSURANCE, viewModel.selectedCategory.value)
    }

    @Test
    fun interactionExportIsOptInAndLinkedSavesUpdate() {
        assertEquals(
            InteractionExportAction.NONE,
            AdvocacyExportPolicy.decide(false, null)
        )
        assertEquals(
            InteractionExportAction.CREATE,
            AdvocacyExportPolicy.decide(true, null)
        )
        assertEquals(
            InteractionExportAction.UPDATE,
            AdvocacyExportPolicy.decide(false, 42L)
        )
    }

    @Test
    fun rehearsalSplitsReadableFocusSections() {
        assertEquals(
            listOf("First sentence.", "Second question?", "Third statement!"),
            splitScriptSections("First sentence. Second question? Third statement!")
        )
        assertEquals(listOf("One thought"), splitScriptSections("One thought"))
    }
}
