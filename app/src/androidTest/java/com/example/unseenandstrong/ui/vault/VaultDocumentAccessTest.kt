package com.example.unseenandstrong.ui.vault

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VaultDocumentAccessTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun invalidSavedUriDoesNotEscapeAsAnExceptionOrRawUri() {
        val result = openVaultDocument(context, "not a uri")

        assertEquals(VaultDocumentOpenResult.INVALID_URI, result)
        val message = vaultDocumentOpenMessage(result)
        assertNotNull(message)
        assertFalse(message.orEmpty().contains("not a uri"))
    }

    @Test
    fun unavailableDocumentMessagePreservesSavedRecord() {
        val message = vaultDocumentOpenMessage(VaultDocumentOpenResult.UNAVAILABLE)

        assertNotNull(message)
        assertFalse(message.orEmpty().contains("content://"))
        assertTrue(message.orEmpty().contains("Nothing was deleted"))
    }

    @Test
    fun nonContentUriIsNotPresentedAsDurablyPersisted() {
        val result = persistVaultReadPermission(
            context.contentResolver,
            Uri.parse("file:///temporary/document.jpg")
        )

        assertEquals(VaultDocumentPersistence.UNAVAILABLE, result)
    }

    @Test
    fun viewIntentCarriesDocumentMimeTypeAndReadGrant() {
        val uri = Uri.parse("content://documents/example")
        val intent = createVaultDocumentViewIntent(context, uri, "image/png")

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(uri, intent.data)
        assertEquals("image/png", intent.type)
        assertTrue(intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0)
    }
}
