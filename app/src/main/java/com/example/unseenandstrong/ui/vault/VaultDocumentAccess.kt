package com.example.unseenandstrong.ui.vault

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.FileNotFoundException

/** The result of asking a provider to retain read access to a selected URI. */
internal enum class VaultDocumentPersistence {
    PERSISTED,
    UNAVAILABLE
}

/** Safe, user-facing outcomes from trying to open a saved document. */
internal enum class VaultDocumentOpenResult {
    OPENED,
    INVALID_URI,
    NO_HANDLER,
    PERMISSION_DENIED,
    UNAVAILABLE
}

/**
 * Retains read access only for content URIs whose provider supports Android's persistable grant.
 * A URI is not considered durable until the grant is visible in persistedUriPermissions.
 */
internal fun persistVaultReadPermission(
    contentResolver: ContentResolver,
    uri: Uri
): VaultDocumentPersistence {
    if (uri.scheme != ContentResolver.SCHEME_CONTENT) {
        return VaultDocumentPersistence.UNAVAILABLE
    }

    return try {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        if (contentResolver.persistedUriPermissions.any { permission ->
                permission.uri == uri && permission.isReadPermission
            }
        ) {
            VaultDocumentPersistence.PERSISTED
        } else {
            VaultDocumentPersistence.UNAVAILABLE
        }
    } catch (_: SecurityException) {
        VaultDocumentPersistence.UNAVAILABLE
    } catch (_: UnsupportedOperationException) {
        VaultDocumentPersistence.UNAVAILABLE
    } catch (_: IllegalArgumentException) {
        VaultDocumentPersistence.UNAVAILABLE
    }
}

/**
 * Opens a saved URI through the system. The read probe gives stale or revoked content-provider
 * URIs a calm, in-app failure state instead of silently launching a viewer that cannot read them.
 */
internal fun openVaultDocument(
    context: Context,
    rawUri: String
): VaultDocumentOpenResult {
    val uri = runCatching { Uri.parse(rawUri) }.getOrNull()
        ?: return VaultDocumentOpenResult.INVALID_URI
    if (uri.scheme.isNullOrBlank()) {
        return VaultDocumentOpenResult.INVALID_URI
    }

    val contentResolver = context.contentResolver
    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        when (probeContentUri(contentResolver, uri)) {
            ContentProbe.READABLE -> Unit
            ContentProbe.PERMISSION_DENIED -> return VaultDocumentOpenResult.PERMISSION_DENIED
            ContentProbe.UNAVAILABLE -> return VaultDocumentOpenResult.UNAVAILABLE
        }
    }

    val mimeType = runCatching { contentResolver.getType(uri) }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
        ?: "image/*"
    val intent = createVaultDocumentViewIntent(context, uri, mimeType)

    if (intent.resolveActivity(context.packageManager) == null) {
        return VaultDocumentOpenResult.NO_HANDLER
    }

    return try {
        context.startActivity(intent)
        VaultDocumentOpenResult.OPENED
    } catch (_: ActivityNotFoundException) {
        VaultDocumentOpenResult.NO_HANDLER
    } catch (_: SecurityException) {
        VaultDocumentOpenResult.PERMISSION_DENIED
    } catch (_: FileNotFoundException) {
        VaultDocumentOpenResult.UNAVAILABLE
    } catch (_: IllegalArgumentException) {
        VaultDocumentOpenResult.INVALID_URI
    } catch (_: RuntimeException) {
        VaultDocumentOpenResult.UNAVAILABLE
    }
}

internal fun createVaultDocumentViewIntent(
    context: Context,
    uri: Uri,
    mimeType: String
): Intent = Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(uri, mimeType)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    if (context !is Activity) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}

internal fun vaultDocumentOpenMessage(result: VaultDocumentOpenResult): String? = when (result) {
    VaultDocumentOpenResult.OPENED -> null
    VaultDocumentOpenResult.INVALID_URI ->
        "This saved document has an invalid location. It was kept in your Vault so you can remove or replace it."
    VaultDocumentOpenResult.NO_HANDLER ->
        "No app is available to view this document. You can keep it in your Vault or choose another source."
    VaultDocumentOpenResult.PERMISSION_DENIED ->
        "This document is no longer available to the app. Nothing was deleted from your Vault."
    VaultDocumentOpenResult.UNAVAILABLE ->
        "This document could not be reached right now. Nothing was deleted from your Vault."
}

private enum class ContentProbe {
    READABLE,
    PERMISSION_DENIED,
    UNAVAILABLE
}

private fun probeContentUri(contentResolver: ContentResolver, uri: Uri): ContentProbe {
    return try {
        val descriptor = contentResolver.openFileDescriptor(uri, "r")
            ?: return ContentProbe.UNAVAILABLE
        descriptor.use { ContentProbe.READABLE }
    } catch (_: SecurityException) {
        ContentProbe.PERMISSION_DENIED
    } catch (_: FileNotFoundException) {
        ContentProbe.UNAVAILABLE
    } catch (_: IllegalArgumentException) {
        ContentProbe.UNAVAILABLE
    } catch (_: RuntimeException) {
        ContentProbe.UNAVAILABLE
    }
}
