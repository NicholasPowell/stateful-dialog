package com.niloda.contextdialog

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DialogContextTest {
    @Test
    fun `serialize and deserialize DialogContext`() {
        val context = DialogContext(
            userId = "user123",
            sessionId = "session456",
            data = mapOf("key1" to "value1", "key2" to "value2")
        )

        val json = Json.encodeToString(DialogContext.serializer(), context)
        val deserialized = Json.decodeFromString(DialogContext.serializer(), json)

        assertEquals(context, deserialized)
    }

    @Test
    fun `DialogContext with empty data`() {
        val context = DialogContext(
            userId = "user123",
            sessionId = "session456"
        )

        assertEquals("user123", context.userId)
        assertEquals("session456", context.sessionId)
        assertEquals(emptyMap(), context.data)
    }
}