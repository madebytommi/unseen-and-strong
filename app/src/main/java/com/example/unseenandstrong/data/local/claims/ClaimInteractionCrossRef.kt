package com.example.unseenandstrong.data.local.claims

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "claim_interaction_cross_ref",
    primaryKeys = ["claimId", "interactionId"],
    indices = [
        Index("interactionId")
    ]
)
data class ClaimInteractionCrossRef(
    val claimId: Long,
    val interactionId: Long
)
