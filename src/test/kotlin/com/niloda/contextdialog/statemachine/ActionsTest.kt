package com.niloda.contextdialog.statemachine

import kotlin.test.Test
import kotlin.test.assertEquals

class ActionsTest {
    @Test
    fun `DialogAction Answer creation`() {
        val action = DialogAction.Answer("q1", "my answer")

        assertEquals("q1", action.questionId)
        assertEquals("my answer", action.answer)
    }
}