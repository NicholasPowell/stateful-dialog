package com.niloda.contextdialog.statemachine

import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.Question
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RenderingTest {
    @Test
    fun `DialogRendering QuestionRendering`() {
        val question = Question.Text("q1", "Question?")
        val context = DialogContext("user", "session")
        val rendering = DialogRendering.QuestionRendering(question, context)

        assertEquals(question, rendering.question)
        assertEquals(context, rendering.context)
    }

    @Test
    fun `DialogRendering Completed`() {
        val responses = mapOf("q1" to "ans1")
        val rendering = DialogRendering.Completed(responses)

        assertEquals(responses, rendering.responses)
        assertTrue(rendering is DialogRendering.Completed)
    }
}