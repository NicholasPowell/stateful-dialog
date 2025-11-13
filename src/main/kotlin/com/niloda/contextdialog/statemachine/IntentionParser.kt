package com.niloda.contextdialog.statemachine

/**
 * Service Provider Interface (SPI) for parsing user intentions from responses.
 * 
 * Implementations of this interface can provide custom logic for detecting
 * different types of intentions in user responses, such as:
 * - Regular answers
 * - Context changes
 * - Combined actions (answer + context change)
 * 
 * This allows library users to extend or replace the default intention
 * detection logic with their own implementations.
 */
interface IntentionParser {
    
    /**
     * Parses a user response and returns the detected intention.
     * 
     * @param response The user's response text
     * @return The parsed intention
     */
    fun parseIntention(response: String): Intention
    
    /**
     * Represents the different types of intentions that can be detected
     * from a user response.
     */
    sealed class Intention {
        /**
         * A regular answer to the current question.
         */
        data class Answer(val answer: String) : Intention()
        
        /**
         * A request to change the dialog context.
         */
        data class ChangeContext(val contextData: String) : Intention()
        
        /**
         * A combined action: answer the current question and change context.
         */
        data class AnswerWithContextChange(
            val answer: String,
            val contextData: String
        ) : Intention()
    }
}
