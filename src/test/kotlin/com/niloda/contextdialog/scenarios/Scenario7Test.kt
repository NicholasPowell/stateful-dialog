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

class Scenario7Test {
    @Test
    fun `Scenario 7 Dialog flow interrupted by user changing context`() {
        // Setup
        val question1 = Question.Text("name", "What is your name?")
        val question2 = Question.MultipleChoice("color", "What is your favorite color?", listOf("Red", "Blue"))
        val flow = DialogFlow(listOf(question1, question2))
        val stateMachine = DialogStateMachine(flow)

        var state = stateMachine.initialState()

        // Start with context 1
        var context = DialogContext("user1", "session1")
        var rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        var qr = rendering as DialogRendering.QuestionRendering
        assertEquals(context, qr.context)

        // Answer first question
        val action1 = DialogAction.Answer("name", "Dave")
        state = stateMachine.onAction(action1, state)
        assertEquals(1, state.currentIndex)

        // Change context (user changes context, e.g., switches device)
        context = DialogContext("user1", "session2") // same user, new session
        rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals(question2, qr.question)
        assertEquals(context, qr.context) // new context reflected

        // Answer second question
        val action2 = DialogAction.Answer("color", "Blue")
        state = stateMachine.onAction(action2, state)
        assertEquals(2, state.currentIndex)

        // Render completed with new context
        rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.Completed)
        val comp = rendering as DialogRendering.Completed
        assertEquals(mapOf("name" to "Dave", "color" to "Blue"), comp.responses)
    }
}