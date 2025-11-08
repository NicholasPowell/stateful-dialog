package com.niloda.contextdialog.scenarios

import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.DialogFlow
import com.niloda.contextdialog.Question
import com.niloda.contextdialog.statemachine.DialogAction
import com.niloda.contextdialog.statemachine.DialogRendering
import com.niloda.contextdialog.statemachine.DialogState
import com.niloda.contextdialog.statemachine.DialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationErrorsAndRetriesDialogTest {
    @Test
    fun `Dialog with validation errors and retries`() {
        // Setup
        val question1 = Question.Text("name", "What is your name?")
        val question2 = Question.MultipleChoice("color", "What is your favorite color?", listOf("Red", "Blue", "Green"))
        val flow = DialogFlow(listOf(question1, question2))
        val context = DialogContext("user1", "session1")
        val stateMachine = DialogStateMachine(flow)

        var state = stateMachine.initialState()

        // First question: try empty answer
        var rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        var qr = rendering as DialogRendering.QuestionRendering
        assertEquals(question1, qr.question)

        val badAction1 = DialogAction.Answer("name", "") // empty
        state = stateMachine.onAction(badAction1, state)
        assertEquals(0, state.currentIndex) // stays
        assertEquals(emptyMap(), state.responses)

        // Retry with valid answer
        val goodAction1 = DialogAction.Answer("name", "Bob")
        state = stateMachine.onAction(goodAction1, state)
        assertEquals(1, state.currentIndex)
        assertEquals(mapOf("name" to "Bob"), state.responses)

        // Second question: try invalid choice
        rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals(question2, qr.question)

        val badAction2 = DialogAction.Answer("color", "Yellow") // invalid
        state = stateMachine.onAction(badAction2, state)
        assertEquals(1, state.currentIndex) // stays
        assertEquals(mapOf("name" to "Bob"), state.responses)

        // Retry with valid choice
        val goodAction2 = DialogAction.Answer("color", "Red")
        state = stateMachine.onAction(goodAction2, state)
        assertEquals(2, state.currentIndex)
        assertEquals(mapOf("name" to "Bob", "color" to "Red"), state.responses)

        // Render completed
        rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.Completed)
        val comp = rendering as DialogRendering.Completed
        assertEquals(mapOf("name" to "Bob", "color" to "Red"), comp.responses)
    }
}