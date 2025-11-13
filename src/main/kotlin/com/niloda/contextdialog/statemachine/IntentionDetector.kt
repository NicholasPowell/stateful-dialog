package com.niloda.contextdialog.statemachine

/**
 * Default implementation of IntentionParser for dialog responses.
 * Supports prefixes for context changes and combined actions.
 * 
 * This object provides backward compatibility while implementing the
 * IntentionParser SPI interface.
 */
object IntentionDetector : IntentionParser {

    private const val CONTEXT_PREFIX = "/context"
    private const val ANSWER_PREFIX = "/answer"

    /**
     * Parses the intention from a user response.
     * Supports:
     * - "/context <data>" - Change context
     * - "/answer <answer> /context <data>" - Answer and change context
     * - Plain text - Regular answer
     */
    override fun parseIntention(response: String): IntentionParser.Intention {
        val trimmed = response.trim()

        // Check for /context prefix
        if (trimmed.startsWith(CONTEXT_PREFIX)) {
            val contextData = trimmed.removePrefix(CONTEXT_PREFIX).trim()
            return IntentionParser.Intention.ChangeContext(contextData)
        }

        // Check for /answer prefix
        if (trimmed.startsWith(ANSWER_PREFIX)) {
            val afterAnswer = trimmed.removePrefix(ANSWER_PREFIX).trim()
            val parts = afterAnswer.split("/context", limit = 2)
            if (parts.size == 2) {
                val answer = parts[0].trim()
                val contextData = parts[1].trim()
                return IntentionParser.Intention.AnswerWithContextChange(answer, contextData)
            } else {
                // Invalid format, treat as regular answer
                return IntentionParser.Intention.Answer(trimmed)
            }
        }

        // Default to regular answer
        return IntentionParser.Intention.Answer(trimmed)
    }
}