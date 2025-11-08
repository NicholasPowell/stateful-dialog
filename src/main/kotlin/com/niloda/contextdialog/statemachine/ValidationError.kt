package com.niloda.contextdialog.statemachine

sealed class ValidationError {
    data class InvalidChoice(val answer: String, val options: List<String>) : ValidationError()
    object EmptyAnswer : ValidationError()
}