package com.niloda.contextdialog

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuestionTest {
    @Test
    fun `serialize and deserialize Text question`() {
        val question = Question.Text(
            id = "q1",
            text = "What is your name?"
        )

        val json = Json.encodeToString(Question.serializer(), question)
        val deserialized = Json.decodeFromString(Question.serializer(), json)

        assertEquals(question, deserialized)
        assertTrue(deserialized is Question.Text)
    }

    @Test
    fun `serialize and deserialize MultipleChoice question`() {
        val question = Question.MultipleChoice(
            id = "q2",
            text = "Choose your favorite color",
            options = listOf("Red", "Blue", "Green")
        )

        val json = Json.encodeToString(Question.serializer(), question)
        val deserialized = Json.decodeFromString(Question.serializer(), json)

        assertEquals(question, deserialized)
        assertTrue(deserialized is Question.MultipleChoice)
        assertEquals(listOf("Red", "Blue", "Green"), (deserialized as Question.MultipleChoice).options)
    }
}