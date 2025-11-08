package com.niloda.contextdialog.statemachine

import kotlin.test.Test
import kotlin.test.assertEquals

class StateTest {
    @Test
    fun `DialogState initial values`() {
        val state = DialogState(currentIndex = 0)

        assertEquals(0, state.currentIndex)
        assertEquals(emptyMap(), state.responses)
    }

    @Test
    fun `DialogState with responses`() {
        val responses = mapOf("q1" to "answer1", "q2" to "answer2")
        val state = DialogState(currentIndex = 2, responses = responses)

        assertEquals(2, state.currentIndex)
        assertEquals(responses, state.responses)
    }

    @Test
    fun `DialogState copy`() {
        val original = DialogState(currentIndex = 1, responses = mapOf("q1" to "ans"))
        val copied = original.copy(currentIndex = 2)

        assertEquals(2, copied.currentIndex)
        assertEquals(original.responses, copied.responses)
    }
}