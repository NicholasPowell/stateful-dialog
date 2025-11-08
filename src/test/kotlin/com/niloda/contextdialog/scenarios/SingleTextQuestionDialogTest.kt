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

class SingleTextQuestionDialogTest {
    @Test
    fun `Simple single text question dialog flow`() {
        // Setup
        val question = Question.Text("name", "What is your name?")
        val flow = DialogFlow(listOf(question))
        val context = DialogContext("user1", "session1")
        val stateMachine = DialogStateMachine(flow)

        // Initial state
        val initialState = stateMachine.initialState()
        assertEquals(0, initialState.currentIndex)
        assertEquals(emptyMap(), initialState.responses)

        // Render initial question
        val initialRendering = stateMachine.render(context, initialState)
        assertTrue(initialRendering is DialogRendering.QuestionRendering)
        val qr = initialRendering as DialogRendering.QuestionRendering
        assertEquals(question, qr.question)
        assertEquals(context, qr.context)

        // Answer the question
        val action = DialogAction.Answer("name", "John Doe")
        val newState = stateMachine.onAction(action, initialState)
        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("name" to "John Doe"), newState.responses)

        // Render completed
        val finalRendering = stateMachine.render(context, newState)
        assertTrue(finalRendering is DialogRendering.Completed)
        val comp = finalRendering as DialogRendering.Completed
        assertEquals(mapOf("name" to "John Doe"), comp.responses)
    }
}