package com.niloda.contextdialog.statemachine

sealed class DialogAction {
    data class Answer(val questionId: String, val answer: String) : DialogAction()
}