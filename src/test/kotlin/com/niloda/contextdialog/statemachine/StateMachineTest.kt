package com.niloda.contextdialog.statemachine

import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.DialogFlow
import com.niloda.contextdialog.Question
import com.squareup.workflow1.Snapshot
import okio.ByteString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StateMachineTest {
    private val flow = DialogFlow(
        listOf(
            Question.Text("q1", "Name?"),
            Question.MultipleChoice("q2", "Color?", listOf("Red", "Blue"))
        )
    )
    private val context = DialogContext("user", "session")
    private val stateMachine = InOrderDialogStateMachine(flow)



    @Test
    fun `initialState`() {
        val state = stateMachine.initialState()

        assertEquals(0, state.currentIndex)
        assertEquals(emptyMap(), state.responses)
    }

    @Test
    fun `render first question`() {
        val state = DialogState(0)

        val rendering = stateMachine.render(context, state)

        assertTrue(rendering is DialogRendering.QuestionRendering)
        val qr = rendering as DialogRendering.QuestionRendering
        assertEquals(flow.questions[0], qr.question)
        assertEquals(context, qr.context)
    }

    @Test
    fun `render completed`() {
        val state = DialogState(2, mapOf("q1" to "ans1", "q2" to "ans2"))

        val rendering = stateMachine.render(context, state)

        assertTrue(rendering is DialogRendering.Completed)
        val comp = rendering as DialogRendering.Completed
        assertEquals(state.responses, comp.responses)
    }

    @Test
    fun `onAction valid answer advances state`() {
        val state = DialogState(0)
        val action = DialogAction.Answer("q1", "John")

        val newState = stateMachine.onAction(action, state)

        assertEquals(1, newState.currentIndex)
        assertEquals(mapOf("q1" to "John"), newState.responses)
    }

    @Test
    fun `onAction invalid question id stays same`() {
        val state = DialogState(0)
        val action = DialogAction.Answer("q2", "answer") // wrong id

        val newState = stateMachine.onAction(action, state)

        assertEquals(state, newState)
    }

    @Test
    fun `onAction validation error stays same`() {
        val state = DialogState(0)
        val action = DialogAction.Answer("q1", "") // empty

        val newState = stateMachine.onAction(action, state)

        assertEquals(state, newState)
    }

    @Test
    fun `snapshotState returns empty`() {
        val state = DialogState(1)
        val snapshot = stateMachine.snapshotState(state)

        assertEquals(Snapshot.of(ByteString.EMPTY), snapshot)
    }
}