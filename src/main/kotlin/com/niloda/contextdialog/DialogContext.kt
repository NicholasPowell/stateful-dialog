package com.niloda.contextdialog

import kotlinx.serialization.Serializable

@Serializable
data class DialogContext(
    val userId: String,
    val sessionId: String,
    val data: Map<String, String> = emptyMap()
)