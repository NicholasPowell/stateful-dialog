package com.niloda.contextdialog

import kotlin.test.Test
import kotlin.test.assertEquals

class DialogFlowTest {
    @Test
    fun `DialogFlow with questions`() {
        val questions = listOf(
            Question.Text("q1", "Name?"),
            Question.MultipleChoice("q2", "Color?", listOf("Red", "Blue"))
        )
        val flow = DialogFlow(questions)

        assertEquals(2, flow.questions.size)
        assertEquals("q1", flow.questions[0].id)
        assertEquals("q2", flow.questions[1].id)
    }

    @Test
    fun `DialogFlow with empty questions`() {
        val flow = DialogFlow(emptyList())

        assertEquals(0, flow.questions.size)
    }
}