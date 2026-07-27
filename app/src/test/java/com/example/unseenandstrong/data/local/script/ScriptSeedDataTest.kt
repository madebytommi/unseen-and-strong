package com.example.unseenandstrong.data.local.script

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScriptSeedDataTest {
    @Test
    fun newAdvocacyCategoriesHaveFiveUsefulScenariosEach() {
        val counts = ScriptSeedData.scripts.groupingBy { it.category }.eachCount()
        assertEquals(5, counts["Insurance"])
        assertEquals(5, counts["Family"])
        assertEquals(5, counts["Strangers"])
    }

    @Test
    fun seedKeysAreUniqueAndEveryToneHasText() {
        val keys = ScriptSeedData.scripts.map { it.category to it.title }
        assertEquals(keys.size, keys.distinct().size)
        assertTrue(
            ScriptSeedData.scripts.all {
                it.gentleText.isNotBlank() && it.directText.isNotBlank() && it.firmText.isNotBlank()
            }
        )
    }
}
