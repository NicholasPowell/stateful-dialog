package com.niloda.contextdialog

import kotlinx.serialization.Serializable

@Serializable
data class FlowContext(
    val flowType: String, // e.g., "onboarding", "survey", "support"
    val metadata: Map<String, String> = emptyMap()
)