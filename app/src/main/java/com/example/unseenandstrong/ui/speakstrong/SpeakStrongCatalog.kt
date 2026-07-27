package com.example.unseenandstrong.ui.speakstrong

import com.example.unseenandstrong.data.local.script.ScriptEntity

object SpeakStrongCatalog {
    const val CATEGORY_ALL = "All"
    const val CATEGORY_DOCTOR = "Doctor"
    const val CATEGORY_WORK = "Work"
    const val CATEGORY_INSURANCE = "Insurance"
    const val CATEGORY_FAMILY = "Family"
    const val CATEGORY_STRANGERS = "Strangers"
    const val CATEGORY_BOUNDARY = "Boundary"

    val categories: List<String> = listOf(
        CATEGORY_ALL,
        CATEGORY_DOCTOR,
        CATEGORY_WORK,
        CATEGORY_INSURANCE,
        CATEGORY_FAMILY,
        CATEGORY_STRANGERS,
        CATEGORY_BOUNDARY
    )

    fun filterScripts(
        scripts: List<ScriptEntity>,
        category: String
    ): List<ScriptEntity> = if (category == CATEGORY_ALL) {
        scripts
    } else {
        scripts.filter { it.category == category }
    }
}
