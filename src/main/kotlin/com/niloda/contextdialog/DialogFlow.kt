package com.niloda.contextdialog

import kotlinx.serialization.Serializable

@Serializable
data class DialogFlow(
    val questions: List<Question>
)