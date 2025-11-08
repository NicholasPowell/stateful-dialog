package com.niloda.contextdialog

import com.niloda.contextdialog.statemachine.DialogAction
import com.niloda.contextdialog.statemachine.DialogState
import com.niloda.contextdialog.statemachine.InOrderDialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowContextTest {
    @Test
    fun `Flow context with metadata is preserved`() {
        val flowContext = FlowContext(
            flowType = "user_onboarding",
            metadata = mapOf("version" to "2.0", "locale" to "en-US")
        )
        val question = Question.Text("name", "What is your name?")
        val flow = DialogFlow(listOf(question), flowContext)
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()

        // Valid answer advances
        val action = DialogAction.Answer("name", "Alice")
        val newState = stateMachine.onAction(action, state)

        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("name" to "Alice"), newState.responses)
    }

    @Test
    fun `Flow without context uses strict validation`() {
        val question = Question.MultipleChoice("choice", "Choose", listOf("A", "B"))
        val flow = DialogFlow(listOf(question)) // no flowContext
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()

        // Invalid choice should not advance
        val action = DialogAction.Answer("choice", "C")
        val newState = stateMachine.onAction(action, state)

        assertEquals(0, newState.currentIndex) // stays
        assertEquals(emptyMap(), newState.responses)
    }

    @Test
    fun `Flow without context uses default validation`() {
        val question = Question.Text("name", "Your name")
        val flow = DialogFlow(listOf(question)) // no flowContext
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()

        // Empty answer should not advance
        val action = DialogAction.Answer("name", "")
        val newState = stateMachine.onAction(action, state)

        assertEquals(0, newState.currentIndex)
        assertEquals(emptyMap(), newState.responses)
    }
}