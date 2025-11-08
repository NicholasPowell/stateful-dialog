package com.niloda.contextdialog

import kotlinx.serialization.Serializable

@Serializable
data class FlowContext(
    val flowType: String, // e.g., "onboarding", "survey", "support"
    val priority: Priority = Priority.NORMAL,
    val metadata: Map<String, String> = emptyMap()
) {
    enum class Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}