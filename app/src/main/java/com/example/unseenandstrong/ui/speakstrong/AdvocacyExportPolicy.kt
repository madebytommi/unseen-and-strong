package com.example.unseenandstrong.ui.speakstrong

enum class InteractionExportAction {
    NONE,
    CREATE,
    UPDATE
}

object AdvocacyExportPolicy {
    fun decide(
        exportRequested: Boolean,
        linkedInteractionId: Long?
    ): InteractionExportAction = when {
        linkedInteractionId != null -> InteractionExportAction.UPDATE
        exportRequested -> InteractionExportAction.CREATE
        else -> InteractionExportAction.NONE
    }
}
