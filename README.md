# Stateful Dialog

A Kotlin library for managing stateful dialog flows using conversational contexts and questions. Built with Kotlin, Square Workflow for state machines, Arrow for functional programming, and Kotlin serialization.

## Features

- **Stateful Dialog Management**: Handle sequential question-answer flows with persistent state
- **Multiple Question Types**: Support for text input and multiple-choice questions
- **Validation**: Built-in validation for answers with error handling
- **Intention Detection**: Parse user responses to detect answers, context changes, or combined actions
- **Flow Context Awareness**: Adapt dialog behavior based on flow context (priority, type, metadata)
- **Serialization**: Full Kotlin serialization support for persistence and state snapshots
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

// Create flow context
val flowContext = FlowContext(
    flowType = "user_onboarding",
    metadata = mapOf("version" to "1.0")
)

// Create dialog flow
val flow = DialogFlow(questions, flowContext)

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

### Intention Detection

The library supports parsing user responses to detect different intentions using special prefixes:

- **Regular answers**: Plain text responses (e.g., "John Doe")
- **Context changes**: Responses starting with `/context <data>` (e.g., "/context new_session")
- **Combined actions**: Responses like `/answer <answer> /context <data>` (e.g., "/answer Yes /context updated")

```kotlin
import com.niloda.contextdialog.statemachine.IntentionDetector

// Parse intention from response
val intention = IntentionDetector.parseIntention("/answer Yes /context new_session")

when (intention) {
    is IntentionDetector.Intention.Answer -> {
        // Handle regular answer
        val action = DialogAction.Answer(questionId, intention.answer)
        state = stateMachine.onAction(action, state)
    }
    is IntentionDetector.Intention.ChangeContext -> {
        // Handle context change - update DialogContext
        val newContext = context.copy(data = context.data + ("session" to intention.contextData))
    }
    is IntentionDetector.Intention.AnswerWithContextChange -> {
        // Handle both answer and context change
        val action = DialogAction.AnswerWithContextChange(questionId, intention.answer, intention.contextData)
        state = stateMachine.onAction(action, state)
        val newContext = context.copy(data = context.data + ("session" to intention.contextData))
    }
}
```

For convenience, use the `onResponse` method which handles parsing and state updates:

```kotlin
val (newState, intention) = stateMachine.onResponse(userInput, state)
// Use intention to update context if needed
```

#### API Conventions

- **Answer Prefix**: `/answer <answer>` - Used to explicitly mark an answer when combined with context changes
- **Context Prefix**: `/context <data>` - Used to change the dialog context
- **Combined Format**: `/answer <answer> /context <data>` - Answer the current question and change context in one response
- **Regular Answers**: No prefix required for standard responses

These prefixes allow users to perform multiple actions in a single response, enabling more flexible dialog interactions.

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
Container for a sequence of questions with optional flow context.

```kotlin
data class DialogFlow(
    val questions: List<Question>,
    val flowContext: FlowContext? = null
)
```

#### `FlowContext`
Contextual information about the dialog flow.

```kotlin
data class FlowContext(
    val flowType: String, // e.g., "onboarding", "survey", "support"
    val metadata: Map<String, String> = emptyMap()
)
```

#### `DialogStateMachine`
The main state machine for managing dialog flow.

```kotlin
class DialogStateMachine(private val flow: DialogFlow) {
    fun initialState(): DialogState
    fun render(context: DialogContext, state: DialogState): DialogRendering
    fun onAction(action: DialogAction, state: DialogState): DialogState
    fun onResponse(response: String, state: DialogState): Pair<DialogState, IntentionDetector.Intention>
    fun snapshotState(state: DialogState): Snapshot
    fun restoreState(snapshot: Snapshot): DialogState
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
    data class ChangeContext(val contextData: String) : DialogAction()
    data class AnswerWithContextChange(val questionId: String, val answer: String, val contextData: String) : DialogAction()
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

#### `IntentionDetector`
Parses user responses to detect different intentions.

```kotlin
object IntentionDetector {
    sealed class Intention {
        data class Answer(val answer: String) : Intention()
        data class ChangeContext(val contextData: String) : Intention()
        data class AnswerWithContextChange(val answer: String, val contextData: String) : Intention()
    }

    fun parseIntention(response: String): Intention
}
```

## Testing with Fixtures

The library includes comprehensive scenario fixtures for testing different dialog contexts:

```kotlin
import com.niloda.contextdialog.fixtures.DialogFixtures

// Use predefined flows for testing
val onboardingFlow = DialogFixtures.USER_ONBOARDING_FLOW
val surveyFlow = DialogFixtures.CUSTOMER_SURVEY_FLOW

// Use sample responses for testing
val responses = DialogFixtures.SampleResponses.ONBOARDING_RESPONSES

// Use different dialog contexts
val userContext = DialogFixtures.STANDARD_USER_CONTEXT
val premiumContext = DialogFixtures.PREMIUM_USER_CONTEXT
```

Available fixtures:
- **User Onboarding**: 4 questions covering name, email, role, and experience
- **Customer Survey**: 4 questions about satisfaction, improvements, recommendation, and usage
- **Support Chat**: 4 questions for issue type, description, severity, and reproduction
- **Account Recovery**: 4 questions for username, recovery method, last login, and suspicious activity

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