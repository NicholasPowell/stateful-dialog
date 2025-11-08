package com.niloda.contextdialog.statemachine

import com.niloda.contextdialog.DialogContext
import com.niloda.contextdialog.Question

sealed class DialogRendering {
    data class QuestionRendering(val question: Question, val context: DialogContext) : DialogRendering()
    data class Completed(val responses: Map<String, String>) : DialogRendering()
}