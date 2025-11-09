package com.niloda.contextdialog.statemachine

sealed class DialogAction {
    data class Answer(val questionId: String, val answer: String) : DialogAction()
    data class ChangeContext(val contextData: String) : DialogAction()
    data class AnswerWithContextChange(val questionId: String, val answer: String, val contextData: String) : DialogAction()
}