package com.niloda.contextdialog.statemachine

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationErrorTest {
    @Test
    fun `ValidationError InvalidChoice`() {
        val error = ValidationError.InvalidChoice("wrong", listOf("a", "b"))

        assertEquals("wrong", error.answer)
        assertEquals(listOf("a", "b"), error.options)
    }

    @Test
    fun `ValidationError EmptyAnswer`() {
        val error = ValidationError.EmptyAnswer

        assertEquals(ValidationError.EmptyAnswer, error)
    }
}