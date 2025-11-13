package com.niloda.contextdialog.statemachine

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.DialogFlow
import com.niloda.contextdialog.FlowContext
import com.niloda.contextdialog.Question
import com.squareup.workflow1.*
import kotlinx.serialization.json.Json
import okio.ByteString

class InOrderDialogStateMachine(
    private val flow: DialogFlow,
    private val intentionParser: IntentionParser = IntentionDetector
) {

    private fun validateAnswer(question: Question, answer: String): Either<ValidationError, String> {
        return when (question) {
            is Question.Text -> if (answer.isBlank()) ValidationError.EmptyAnswer.left() else answer.right()
            is Question.MultipleChoice -> if (answer in question.options) answer.right() else
                ValidationError.InvalidChoice(
                    answer = answer,
                    options = question.options
                ).left()
        }
    }

    fun initialState(): DialogState {
        return DialogState(currentIndex = 0)
    }

    fun render(context: DialogContext, state: DialogState): DialogRendering {
        return if (state.currentIndex < flow.questions.size) {
            val question = flow.questions[state.currentIndex]
            DialogRendering.QuestionRendering(question, context)
        } else {
            DialogRendering.Completed(state.responses)
        }
    }

    fun snapshotState(state: DialogState): Snapshot {
        val json = Json.encodeToString(DialogState.serializer(), state)
        return Snapshot.of(ByteString.of(*json.encodeToByteArray()))
    }

    fun restoreState(snapshot: Snapshot): DialogState {
        return try {
            val json = snapshot.bytes.utf8()
            Json.decodeFromString(DialogState.serializer(), json)
        } catch (e: Exception) {
            // If deserialization fails, return initial state
            initialState()
        }
    }

    fun onAction(action: DialogAction, state: DialogState): DialogState {
        return when (action) {
            is DialogAction.Answer -> {
                val question = flow.questions.getOrNull(state.currentIndex)
                if (question != null && question.id == action.questionId) {
                    validateAnswer(question, action.answer).fold(
                        { error -> state }, // On validation error, stay in same state
                        { validAnswer ->
                            val newResponses = state.responses + (action.questionId to validAnswer)
                            state.copy(
                                currentIndex = state.currentIndex + 1,
                                responses = newResponses
                            )
                        }
                    )
                } else {
                    state // Invalid question id or out of bounds
                }
            }
            is DialogAction.ChangeContext -> {
                // Context change handled by caller, state remains the same
                state
            }
            is DialogAction.AnswerWithContextChange -> {
                val question = flow.questions.getOrNull(state.currentIndex)
                if (question != null && question.id == action.questionId) {
                    validateAnswer(question, action.answer).fold(
                        { error -> state }, // On validation error, stay in same state
                        { validAnswer ->
                            val newResponses = state.responses + (action.questionId to validAnswer)
                            state.copy(
                                currentIndex = state.currentIndex + 1,
                                responses = newResponses
                            )
                        }
                    )
                } else {
                    state // Invalid question id or out of bounds
                }
            }
        }
    }

    /**
     * Processes a user response by parsing the intention and updating the dialog state.
     * Returns the new state and the parsed intention for handling context changes.
     */
    fun onResponse(response: String, state: DialogState): Pair<DialogState, IntentionParser.Intention> {
        val intention = intentionParser.parseIntention(response)
        val newState = when (intention) {
            is IntentionParser.Intention.Answer -> {
                val question = flow.questions.getOrNull(state.currentIndex)
                if (question != null) {
                    onAction(DialogAction.Answer(question.id, intention.answer), state)
                } else {
                    state // Dialog completed, ignore
                }
            }
            is IntentionParser.Intention.ChangeContext -> {
                // State unchanged, intention returned for caller to handle context change
                state
            }
            is IntentionParser.Intention.AnswerWithContextChange -> {
                val question = flow.questions.getOrNull(state.currentIndex)
                if (question != null) {
                    onAction(DialogAction.AnswerWithContextChange(question.id, intention.answer, intention.contextData), state)
                } else {
                    state // Dialog completed, ignore
                }
            }
        }
        return newState to intention
    }
}