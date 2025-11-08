package com.niloda.contextdialog

import com.niloda.contextdialog.statemachine.DialogAction
import com.niloda.contextdialog.statemachine.DialogState
import com.niloda.contextdialog.statemachine.InOrderDialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowContextTest {
    @Test
    fun `Flow context with high priority relaxes text validation`() {
        val flowContext = FlowContext(
            flowType = "urgent_support",
            priority = FlowContext.Priority.HIGH,
            metadata = mapOf("department" to "tech")
        )
        val question = Question.Text("issue", "Describe your issue")
        val flow = DialogFlow(listOf(question), flowContext)
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()

        // With high priority, any non-blank answer should be valid
        val action = DialogAction.Answer("issue", "My computer is broken!")
        val newState = stateMachine.onAction(action, state)

        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("issue" to "My computer is broken!"), newState.responses)
    }

    @Test
    fun `Flow context with high priority relaxes multiple choice validation`() {
        val flowContext = FlowContext(
            flowType = "survey",
            priority = FlowContext.Priority.URGENT
        )
        val question = Question.MultipleChoice("rating", "Rate us", listOf("Good", "Bad"))
        val flow = DialogFlow(listOf(question), flowContext)
        val stateMachine = InOrderDialogStateMachine(flow)

        val state = stateMachine.initialState()

        // With urgent priority, any answer should be accepted
        val action = DialogAction.Answer("rating", "Excellent")
        val newState = stateMachine.onAction(action, state)

        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("rating" to "Excellent"), newState.responses)
    }

    @Test
    fun `Normal priority flow uses strict validation`() {
        val flowContext = FlowContext(
            flowType = "regular",
            priority = FlowContext.Priority.NORMAL
        )
        val question = Question.MultipleChoice("choice", "Choose", listOf("A", "B"))
        val flow = DialogFlow(listOf(question), flowContext)
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