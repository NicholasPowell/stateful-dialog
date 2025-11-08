package com.niloda.contextdialog.scenarios

import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.DialogFlow
import com.niloda.contextdialog.Question
import com.niloda.contextdialog.statemachine.DialogAction
import com.niloda.contextdialog.statemachine.DialogRendering
import com.niloda.contextdialog.statemachine.InOrderDialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EdgeCasesDialogTest {
    @Test
    fun `Edge cases empty dialog`() {
        // Empty dialog
        val flow = DialogFlow(emptyList())
        val context = DialogContext("user1", "session1")
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()
        assertEquals(0, state.currentIndex)

        // Render should be completed immediately
        val rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.Completed)
        val comp = rendering as DialogRendering.Completed
        assertEquals(emptyMap(), comp.responses)
    }

    @Test
    fun `Edge cases invalid actions`() {
        val question = Question.Text("q1", "Question?")
        val flow = DialogFlow(listOf(question))
        val context = DialogContext("user1", "session1")
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()

        // Invalid question id
        val badAction1 = DialogAction.Answer("q2", "answer")
        val newState1 = stateMachine.onAction(badAction1, state)
        assertEquals(state, newState1) // no change

        // Valid action
        val goodAction = DialogAction.Answer("q1", "valid")
        val newState2 = stateMachine.onAction(goodAction, state)
        assertEquals(1, newState2.currentIndex)
        assertEquals(mapOf("q1" to "valid"), newState2.responses)

        // Try another action after completion (out of bounds)
        val badAction2 = DialogAction.Answer("q1", "another")
        val newState3 = stateMachine.onAction(badAction2, newState2)
        assertEquals(newState2, newState3) // no change
    }
}