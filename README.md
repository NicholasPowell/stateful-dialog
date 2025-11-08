# Stateful Dialog

A Kotlin library for managing stateful dialog flows using conversational contexts and questions. Built with Kotlin, Square Workflow for state machines, Arrow for functional programming, and Kotlin serialization.

## Features

- **Stateful Dialog Management**: Handle sequential question-answer flows with persistent state
- **Multiple Question Types**: Support for text input and multiple-choice questions
- **Validation**: Built-in validation for answers with error handling
- **Serialization**: Full Kotlin serialization support for persistence
- **Functional Programming**: Uses Arrow for pure functions and error handling
- **Workflow Integration**: Leverages Square Workflow for robust state management

## Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.niloda.contextdialog:stateful-dialog:0.1.0-SNAPSHOT")
}
```

## Usage

### Basic Example

```kotlin
import com.niloda.contextdialog.*
import com.niloda.contextdialog.statemachine.*

// Define questions
val questions = listOf(
    Question.Text("name", "What is your name?"),
    Question.MultipleChoice("color", "Favorite color?", listOf("Red", "Blue", "Green"))
)

// Create dialog flow
val flow = DialogFlow(questions)

// Create state machine
val stateMachine = DialogStateMachine(flow)

// Initialize
val context = DialogContext("user123", "session456")
var state = stateMachine.initialState()

// Process dialog
while (state.currentIndex < flow.questions.size) {
    val rendering = stateMachine.render(context, state)
    when (rendering) {
        is DialogRendering.QuestionRendering -> {
            val question = rendering.question
            val answer = getUserInput(question) // Your input logic
            val action = DialogAction.Answer(question.id, answer)
            state = stateMachine.onAction(action, state)
        }
        is DialogRendering.Completed -> break
    }
}

// Get results
val finalRendering = stateMachine.render(context, state) as DialogRendering.Completed
println("Responses: ${finalRendering.responses}")
```

### Question Types

#### Text Question
```kotlin
val textQuestion = Question.Text(
    id = "email",
    text = "What is your email address?"
)
```

#### Multiple Choice Question
```kotlin
val choiceQuestion = Question.MultipleChoice(
    id = "preference",
    text = "Choose your preference:",
    options = listOf("Option A", "Option B", "Option C")
)
```

### Validation

The library automatically validates answers:
- Text questions: Must not be blank
- Multiple choice: Must be one of the provided options

Invalid answers keep the dialog in the same state.

### Serialization

All models support Kotlin serialization:

```kotlin
import kotlinx.serialization.json.Json

val context = DialogContext("user", "session", mapOf("key" to "value"))
val json = Json.encodeToString(DialogContext.serializer(), context)
val deserialized = Json.decodeFromString(DialogContext.serializer(), json)
```

## API Reference

### Core Classes

#### `DialogContext`
Represents the conversational context.

```kotlin
data class DialogContext(
    val userId: String,
    val sessionId: String,
    val data: Map<String, String> = emptyMap()
)
```

#### `Question`
Sealed class for question types.

```kotlin
sealed class Question {
    abstract val id: String
    abstract val text: String

    data class Text(
        override val id: String,
        override val text: String
    ) : Question()

    data class MultipleChoice(
        override val id: String,
        override val text: String,
        val options: List<String>
    ) : Question()
}
```

#### `DialogFlow`
Container for a sequence of questions.

```kotlin
data class DialogFlow(val questions: List<Question>)
```

#### `DialogStateMachine`
The main state machine for managing dialog flow.

```kotlin
class DialogStateMachine(private val flow: DialogFlow) {
    fun initialState(): DialogState
    fun render(context: DialogContext, state: DialogState): DialogRendering
    fun onAction(action: DialogAction, state: DialogState): DialogState
    fun snapshotState(state: DialogState): Snapshot
}
```

#### `DialogState`
Internal state of the dialog.

```kotlin
data class DialogState(
    val currentIndex: Int,
    val responses: Map<String, String> = emptyMap()
)
```

#### `DialogAction`
Actions that can be performed on the dialog.

```kotlin
sealed class DialogAction {
    data class Answer(val questionId: String, val answer: String) : DialogAction()
}
```

#### `DialogRendering`
Output of the dialog state machine.

```kotlin
sealed class DialogRendering {
    data class QuestionRendering(val question: Question, val context: DialogContext) : DialogRendering()
    data class Completed(val responses: Map<String, String>) : DialogRendering()
}
```

#### `ValidationError`
Errors that can occur during validation.

```kotlin
sealed class ValidationError {
    object EmptyAnswer : ValidationError()
    data class InvalidChoice(val answer: String, val options: List<String>) : ValidationError()
}
```

## Development

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test
```

### Publishing
```bash
./gradlew publish
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

MIT License - see LICENSE file for details.