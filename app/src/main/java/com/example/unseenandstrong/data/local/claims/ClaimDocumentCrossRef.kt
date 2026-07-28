package com.example.unseenandstrong.data.local.claims

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "claim_document_cross_ref",
    primaryKeys = ["claimId", "documentId"],
    indices = [
        Index("documentId")
    ]
)
data class ClaimDocumentCrossRef(
    val claimId: Long,
    val documentId: Long
)
