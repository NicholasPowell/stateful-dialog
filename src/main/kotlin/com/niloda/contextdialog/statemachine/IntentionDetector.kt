package com.niloda.contextdialog.statemachine

import com.niloda.contextdialog.DialogContext

/**
 * Intention detection for dialog responses.
 * Supports prefixes for context changes and combined actions.
 */
object IntentionDetector {

    private const val CONTEXT_PREFIX = "/context"
    private const val ANSWER_PREFIX = "/answer"

    sealed class Intention {
        data class Answer(val answer: String) : Intention()
        data class ChangeContext(val contextData: String) : Intention()
        data class AnswerWithContextChange(val answer: String, val contextData: String) : Intention()
    }

    /**
     * Parses the intention from a user response.
     * Supports:
     * - "/context <data>" - Change context
     * - "/answer <answer> /context <data>" - Answer and change context
     * - Plain text - Regular answer
     */
    fun parseIntention(response: String): Intention {
        val trimmed = response.trim()

        // Check for /context prefix
        if (trimmed.startsWith(CONTEXT_PREFIX)) {
            val contextData = trimmed.removePrefix(CONTEXT_PREFIX).trim()
            return Intention.ChangeContext(contextData)
        }

        // Check for /answer prefix
        if (trimmed.startsWith(ANSWER_PREFIX)) {
            val afterAnswer = trimmed.removePrefix(ANSWER_PREFIX).trim()
            val parts = afterAnswer.split("/context", limit = 2)
            if (parts.size == 2) {
                val answer = parts[0].trim()
                val contextData = parts[1].trim()
                return Intention.AnswerWithContextChange(answer, contextData)
            } else {
                // Invalid format, treat as regular answer
                return Intention.Answer(trimmed)
            }
        }

        // Default to regular answer
        return Intention.Answer(trimmed)
    }
}