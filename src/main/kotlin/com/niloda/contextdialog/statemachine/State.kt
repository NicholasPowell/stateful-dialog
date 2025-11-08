package com.niloda.contextdialog.statemachine

import kotlinx.serialization.Serializable

@Serializable
data class DialogState(
    val currentIndex: Int,
    val responses: Map<String, String> = emptyMap()
)