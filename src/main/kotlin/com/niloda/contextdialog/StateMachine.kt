package com.niloda.contextdialog

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.squareup.workflow1.*
import kotlinx.serialization.Serializable

@Serializable
data class DialogState(
    val currentIndex: Int,
    val responses: Map<String, String> = emptyMap()
)

sealed class DialogAction {
    data class Answer(val questionId: String, val answer: String) : DialogAction()
}

sealed class ValidationError {
    data class InvalidChoice(val answer: String, val options: List<String>) : ValidationError()
    data class EmptyAnswer : ValidationError()
}

sealed class DialogRendering {
    data class QuestionRendering(val question: Question, val context: DialogContext) : DialogRendering()
    data class Completed(val responses: Map<String, String>) : DialogRendering()
}

class DialogStateMachine(
    private val flow: DialogFlow
) : StatefulWorkflow<DialogContext, DialogState, Unit, DialogRendering>() {

    private fun validateAnswer(question: Question, answer: String): Either<ValidationError, String> {
        return when (question) {
            is Question.Text -> if (answer.isBlank()) ValidationError.EmptyAnswer().left() else answer.right()
            is Question.MultipleChoice -> if (answer in question.options) answer.right() else ValidationError.InvalidChoice(answer, question.options).left()
        }
    }

    override fun initialState(props: DialogContext, snapshot: Snapshot?): DialogState {
        return DialogState(currentIndex = 0)
    }

    override fun render(props: DialogContext, state: DialogState, context: RenderContext): DialogRendering {
        return if (state.currentIndex < flow.questions.size) {
            val question = flow.questions[state.currentIndex]
            DialogRendering.QuestionRendering(question, props).also {
                context.runningWorker(Worker.create { /* No workers for now */ }) { }
            }
        } else {
            DialogRendering.Completed(state.responses)
        }
    }

    override fun snapshotState(state: DialogState): Snapshot {
        // TODO: Implement snapshot for persistence
        return Snapshot.EMPTY
    }

    override fun onAction(action: Any, state: DialogState, props: DialogContext): DialogState {
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
            else -> state
        }
    }

    override fun onPropsChanged(old: DialogContext, new: DialogContext, state: DialogState): DialogState {
        // For now, no change on props
        return state
    }
}