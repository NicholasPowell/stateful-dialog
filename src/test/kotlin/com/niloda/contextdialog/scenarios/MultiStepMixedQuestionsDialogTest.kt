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

class Scenario3Test {
    @Test
    fun `Scenario 3 Multi-step dialog with mixed question types`() {
        // Setup
        val question1 = Question.Text("name", "What is your name?")
        val question2 = Question.MultipleChoice("color", "What is your favorite color?", listOf("Red", "Blue", "Green"))
        val flow = DialogFlow(listOf(question1, question2))
        val context = DialogContext("user1", "session1")
        val stateMachine = DialogStateMachine(flow)

        // Initial state
        var state = stateMachine.initialState()
        assertEquals(0, state.currentIndex)

        // Render first question
        var rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        var qr = rendering as DialogRendering.QuestionRendering
        assertEquals(question1, qr.question)

        // Answer first question
        val action1 = DialogAction.Answer("name", "Alice")
        state = stateMachine.onAction(action1, state)
        assertEquals(1, state.currentIndex)
        assertEquals(mapOf("name" to "Alice"), state.responses)

        // Render second question
        rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals(question2, qr.question)

        // Answer second question
        val action2 = DialogAction.Answer("color", "Green")
        state = stateMachine.onAction(action2, state)
        assertEquals(2, state.currentIndex)
        assertEquals(mapOf("name" to "Alice", "color" to "Green"), state.responses)

        // Render completed
        rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.Completed)
        val comp = rendering as DialogRendering.Completed
        assertEquals(mapOf("name" to "Alice", "color" to "Green"), comp.responses)
    }
}