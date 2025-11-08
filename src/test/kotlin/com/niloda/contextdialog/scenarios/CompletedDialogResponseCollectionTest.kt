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

class CompletedDialogResponseCollectionTest {
    @Test
    fun `Completed dialog with response collection`() {
        // Setup with three questions
        val question1 = Question.Text("name", "What is your name?")
        val question2 = Question.Text("age", "How old are you?")
        val question3 = Question.MultipleChoice("pet", "Do you have a pet?", listOf("Yes", "No"))
        val flow = DialogFlow(listOf(question1, question2, question3))
        val context = DialogContext("user1", "session1")
        val stateMachine = DialogStateMachine(flow)

        var state = stateMachine.initialState()

        // Answer all questions
        val answers = listOf(
            DialogAction.Answer("name", "Charlie"),
            DialogAction.Answer("age", "25"),
            DialogAction.Answer("pet", "Yes")
        )

        for (action in answers) {
            state = stateMachine.onAction(action, state)
        }

        // Check final state
        assertEquals(3, state.currentIndex)
        val expectedResponses = mapOf(
            "name" to "Charlie",
            "age" to "25",
            "pet" to "Yes"
        )
        assertEquals(expectedResponses, state.responses)

        // Render completed
        val rendering = stateMachine.render(context, state)
        assertTrue(rendering is DialogRendering.Completed)
        val comp = rendering as DialogRendering.Completed
        assertEquals(expectedResponses, comp.responses)
    }
}