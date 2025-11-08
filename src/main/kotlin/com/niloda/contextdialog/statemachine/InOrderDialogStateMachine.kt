package com.niloda.contextdialog.statemachine

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.DialogFlow
import com.niloda.contextdialog.FlowContext
import com.niloda.contextdialog.Question
import com.squareup.workflow1.*
import okio.ByteString

class InOrderDialogStateMachine(
    private val flow: DialogFlow
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
        // TODO: Implement snapshot for persistence
        return Snapshot.of(ByteString.EMPTY)
    }

    fun onAction(action: DialogAction, state: DialogState): DialogState {
        val answerAction = action as DialogAction.Answer
        val question = flow.questions.getOrNull(state.currentIndex)
        if (question != null && question.id == answerAction.questionId) {
            return validateAnswer(question, answerAction.answer).fold(
                { error -> state }, // On validation error, stay in same state
                { validAnswer ->
                    val newResponses = state.responses + (answerAction.questionId to validAnswer)
                    state.copy(
                        currentIndex = state.currentIndex + 1,
                        responses = newResponses
                    )
                }
            )
        } else {
            return state // Invalid question id or out of bounds
        }
    }
}