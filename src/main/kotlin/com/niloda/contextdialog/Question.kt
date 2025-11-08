package com.niloda.contextdialog

import kotlinx.serialization.Serializable

@Serializable
sealed class Question {
    abstract val id: String
    abstract val text: String

    @Serializable
    data class Text(
        override val id: String,
        override val text: String
    ) : Question()

    @Serializable
    data class MultipleChoice(
        override val id: String,
        override val text: String,
        val options: List<String>
    ) : Question()
}