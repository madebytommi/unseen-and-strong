package com.example.unseenandstrong.ui.claims

enum class RequestLogSyncAction {
    NONE,
    CREATE,
    UPDATE,
    CLEAR_STALE_LINK
}

object ClaimRequestLogSyncPolicy {
    fun determineAction(
        hasLinkedId: Boolean,
        linkedRequestExists: Boolean,
        enableIntegration: Boolean
    ): RequestLogSyncAction {
        return when {
            hasLinkedId && !linkedRequestExists -> RequestLogSyncAction.CLEAR_STALE_LINK
            hasLinkedId && linkedRequestExists -> {
                if (enableIntegration) RequestLogSyncAction.UPDATE else RequestLogSyncAction.NONE
            }
            !hasLinkedId && enableIntegration -> RequestLogSyncAction.CREATE
            else -> RequestLogSyncAction.NONE
        }
    }
}
