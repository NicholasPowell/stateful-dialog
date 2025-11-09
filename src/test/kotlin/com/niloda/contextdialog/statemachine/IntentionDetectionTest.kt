package com.niloda.contextdialog.statemachine

import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.DialogFlow
import com.niloda.contextdialog.Question
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IntentionDetectionTest {
    @Test
    fun `IntentionDetector parses regular answer`() {
        val intention = IntentionDetector.parseIntention("My answer")
        assertTrue(intention is IntentionDetector.Intention.Answer)
        assertEquals("My answer", (intention as IntentionDetector.Intention.Answer).answer)
    }

    @Test
    fun `IntentionDetector parses context change`() {
        val intention = IntentionDetector.parseIntention("/context new_session_data")
        assertTrue(intention is IntentionDetector.Intention.ChangeContext)
        assertEquals("new_session_data", (intention as IntentionDetector.Intention.ChangeContext).contextData)
    }

    @Test
    fun `IntentionDetector parses answer with context change`() {
        val intention = IntentionDetector.parseIntention("/answer Yes /context updated")
        assertTrue(intention is IntentionDetector.Intention.AnswerWithContextChange)
        val awcc = intention as IntentionDetector.Intention.AnswerWithContextChange
        assertEquals("Yes", awcc.answer)
        assertEquals("updated", awcc.contextData)
    }

    @Test
    fun `IntentionDetector handles invalid answer with context format`() {
        val intention = IntentionDetector.parseIntention("/answer invalid")
        assertTrue(intention is IntentionDetector.Intention.Answer)
        assertEquals("/answer invalid", (intention as IntentionDetector.Intention.Answer).answer)
    }

    @Test
    fun `State machine handles ChangeContext action`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val newState = stateMachine.onAction(DialogAction.ChangeContext("new_data"), state)
        assertEquals(state, newState) // State unchanged
    }

    @Test
    fun `State machine handles AnswerWithContextChange action`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val newState = stateMachine.onAction(DialogAction.AnswerWithContextChange("q1", "Answer", "context_data"), state)
        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("q1" to "Answer"), newState.responses)
    }

    @Test
    fun `onResponse handles regular answer`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val (newState, intention) = stateMachine.onResponse("My answer", state)
        assertTrue(intention is IntentionDetector.Intention.Answer)
        assertEquals("My answer", (intention as IntentionDetector.Intention.Answer).answer)
        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("q1" to "My answer"), newState.responses)
    }

    @Test
    fun `onResponse handles context change`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val (newState, intention) = stateMachine.onResponse("/context new_data", state)
        assertTrue(intention is IntentionDetector.Intention.ChangeContext)
        assertEquals("new_data", (intention as IntentionDetector.Intention.ChangeContext).contextData)
        assertEquals(state, newState) // State unchanged
    }

    @Test
    fun `onResponse handles answer with context change`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val (newState, intention) = stateMachine.onResponse("/answer Yes /context updated", state)
        assertTrue(intention is IntentionDetector.Intention.AnswerWithContextChange)
        val awcc = intention as IntentionDetector.Intention.AnswerWithContextChange
        assertEquals("Yes", awcc.answer)
        assertEquals("updated", awcc.contextData)
        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("q1" to "Yes"), newState.responses)
    }

    @Test
    fun `onResponse ignores responses when dialog completed`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        var state = stateMachine.initialState()
        state = stateMachine.onAction(DialogAction.Answer("q1", "Answer"), state) // Complete dialog

        val (newState, intention) = stateMachine.onResponse("Ignored", state)
        assertTrue(intention is IntentionDetector.Intention.Answer)
        assertEquals(state, newState) // State unchanged
    }

    @Test
    fun `onResponse handles validation errors for answers`() {
        val flow = DialogFlow(listOf(Question.Text("q1", "Question")))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val (newState, intention) = stateMachine.onResponse("", state) // Empty answer
        assertTrue(intention is IntentionDetector.Intention.Answer)
        assertEquals(state, newState) // State unchanged due to validation error
    }

    @Test
    fun `onResponse handles invalid multiple choice answers`() {
        val flow = DialogFlow(listOf(Question.MultipleChoice("q1", "Question", listOf("A", "B"))))
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val (newState, intention) = stateMachine.onResponse("C", state) // Invalid choice
        assertTrue(intention is IntentionDetector.Intention.Answer)
        assertEquals(state, newState) // State unchanged due to validation error
    }
}