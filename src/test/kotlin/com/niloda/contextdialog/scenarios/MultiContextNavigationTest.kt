package com.niloda.contextdialog.scenarios

import com.niloda.contextdialog.fixtures.DialogFixtures
import com.niloda.contextdialog.fixtures.DialogFixtures.CUSTOMER_SURVEY_FLOW
import com.niloda.contextdialog.fixtures.DialogFixtures.SUPPORT_CHAT_FLOW
import com.niloda.contextdialog.fixtures.DialogFixtures.USER_ONBOARDING_FLOW
import com.niloda.contextdialog.statemachine.DialogRendering
import com.niloda.contextdialog.statemachine.InOrderDialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultiContextNavigationTest {

    @Test
    fun `User navigates between multiple flow contexts using intention detection`() {
        println("=== Starting Multi-Context Navigation Test ===")

        // Setup multiple state machines for different contexts
        val onboardingMachine = InOrderDialogStateMachine(USER_ONBOARDING_FLOW)
        val surveyMachine = InOrderDialogStateMachine(CUSTOMER_SURVEY_FLOW)
        val supportMachine = InOrderDialogStateMachine(SUPPORT_CHAT_FLOW)

        // Track state for each machine separately
        val machineStates = mutableMapOf(
            onboardingMachine to onboardingMachine.initialState(),
            surveyMachine to surveyMachine.initialState(),
            supportMachine to supportMachine.initialState()
        )

        // Start with user onboarding context
        var currentContext = DialogFixtures.STANDARD_USER_CONTEXT
        var currentMachine = onboardingMachine
        var currentState = machineStates[currentMachine]!!

        println("Starting with: ${currentMachine::class.simpleName} in context: ${currentContext.userId}")

        // Initial rendering - should show first onboarding question
        var rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        var qr = rendering as DialogRendering.QuestionRendering
        assertEquals("What is your full name?", qr.question.text)
        assertEquals(currentContext, qr.context)

        // User answers first question
        println("Q: ${qr.question.text}")
        println("User response: 'John Doe'")
        val (newState1, intention1) = currentMachine.onResponse("John Doe", currentState)
        println("Intention detected: ${intention1::class.simpleName}")
        assertEquals("Answer", intention1::class.simpleName)
        machineStates[currentMachine] = newState1
        currentState = newState1
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(1, currentState.currentIndex)

        // User decides to switch to customer survey context
        println("Q: ${qr.question.text} (continuing from previous)")
        println("User response: '/context survey_mode'")
        val (newState2, intention2) = currentMachine.onResponse("/context survey_mode", currentState)
        println("Intention detected: ${intention2::class.simpleName}")
        assertEquals("ChangeContext", intention2::class.simpleName)
        // State remains the same, but caller would update context
        machineStates[currentMachine] = newState2
        currentState = newState2
        println("State unchanged: currentIndex = ${currentState.currentIndex}")
        assertEquals(1, currentState.currentIndex) // Still at same position

        // Switch to survey machine and context
        println("Switching to survey context...")
        currentMachine = surveyMachine
        currentContext = currentContext.copy(data = currentContext.data + ("mode" to "survey"))
        currentState = machineStates[currentMachine]!!
        println("Now in: ${currentMachine::class.simpleName} with context data: ${currentContext.data}")

        // Render with new context - should show first survey question
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals("How satisfied are you with our product?", qr.question.text)

        // User answers survey question
        println("Q: ${qr.question.text}")
        println("User response: 'Very Satisfied'")
        val (newState3, intention3) = currentMachine.onResponse("Very Satisfied", currentState)
        println("Intention detected: ${intention3::class.simpleName}")
        assertEquals("Answer", intention3::class.simpleName)
        machineStates[currentMachine] = newState3
        currentState = newState3
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(1, currentState.currentIndex)

        // User wants to report an issue and switch to support context
        println("Q: ${qr.question.text} (continuing from previous)")
        println("User response: '/answer The app is great! /context support_needed'")
        val (newState4, intention4) = currentMachine.onResponse("/answer The app is great! /context support_needed", currentState)
        println("Intention detected: ${intention4::class.simpleName}")
        assertEquals("AnswerWithContextChange", intention4::class.simpleName)
        val awcc = intention4 as com.niloda.contextdialog.statemachine.IntentionDetector.Intention.AnswerWithContextChange
        println("Answer: '${awcc.answer}', Context data: '${awcc.contextData}'")
        assertEquals("The app is great!", awcc.answer)
        assertEquals("support_needed", awcc.contextData)
        machineStates[currentMachine] = newState4
        currentState = newState4
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(2, currentState.currentIndex)

        // Switch to support machine
        println("Switching to support context...")
        currentMachine = supportMachine
        currentContext = currentContext.copy(data = currentContext.data + ("support_reason" to "follow_up"))
        currentState = machineStates[currentMachine]!!
        println("Now in: ${currentMachine::class.simpleName} with context data: ${currentContext.data}")

        // Render support question
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals("What type of issue are you experiencing?", qr.question.text)

        // User answers support question and indicates they want to go back to survey
        println("Q: ${qr.question.text}")
        println("User response: '/answer Feature Request /context back_to_survey'")
        val (newState5, intention5) = currentMachine.onResponse("/answer Feature Request /context back_to_survey", currentState)
        println("Intention detected: ${intention5::class.simpleName}")
        machineStates[currentMachine] = newState5
        currentState = newState5
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(1, currentState.currentIndex)

        // Switch back to survey machine
        println("Switching back to survey context...")
        currentMachine = surveyMachine
        currentContext = currentContext.copy(data = currentContext.data + ("navigation" to "back_to_survey"))
        currentState = machineStates[currentMachine]!!
        println("Now in: ${currentMachine::class.simpleName} with context data: ${currentContext.data}")

        // Continue with survey
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals("Would you recommend our product?", qr.question.text)

        // Complete the survey
        println("Q: ${qr.question.text}")
        println("User response: 'Definitely'")
        val (newState6, intention6) = currentMachine.onResponse("Definitely", currentState)
        println("Intention detected: ${intention6::class.simpleName}")
        assertEquals("Answer", intention6::class.simpleName)
        machineStates[currentMachine] = newState6
        currentState = newState6
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(3, currentState.currentIndex)

        println("Q: How often do you use our product?")
        println("User response: 'Daily'")
        val (newState7, intention7) = currentMachine.onResponse("Daily", currentState)
        println("Intention detected: ${intention7::class.simpleName}")
        machineStates[currentMachine] = newState7
        currentState = newState7
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(4, currentState.currentIndex)

        // Survey completed
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.Completed)
        val completed = rendering as DialogRendering.Completed
        println("Dialog completed! Responses: ${completed.responses}")
        assertEquals(4, completed.responses.size)
        assertEquals("Very Satisfied", completed.responses["satisfaction"])
        assertEquals("The app is great!", completed.responses["improvements"])
        assertEquals("Definitely", completed.responses["recommendation"])
        assertEquals("Daily", completed.responses["usage_frequency"])

        println("=== Multi-Context Navigation Test Completed Successfully ===")
    }

    @Test
    fun `Context navigation preserves partial progress across machines`() {
        println("=== Starting Context Preservation Test ===")

        // Start with onboarding
        val onboardingMachine = InOrderDialogStateMachine(USER_ONBOARDING_FLOW)
        val surveyMachine = InOrderDialogStateMachine(CUSTOMER_SURVEY_FLOW)

        // Track state for each machine
        val machineStates = mutableMapOf(
            onboardingMachine to onboardingMachine.initialState(),
            surveyMachine to surveyMachine.initialState()
        )

        var currentContext = DialogFixtures.PREMIUM_USER_CONTEXT
        var currentMachine = onboardingMachine
        var currentState = machineStates[currentMachine]!!

        println("Starting with: ${currentMachine::class.simpleName} in context: ${currentContext.userId}")

        // Answer first two onboarding questions
        println("Q: What is your full name?")
        println("User response: 'Jane Smith'")
        val (state1, _) = currentMachine.onResponse("Jane Smith", currentState)
        machineStates[currentMachine] = state1
        currentState = state1
        println("State updated: currentIndex = ${currentState.currentIndex}")

        println("Q: What is your email address?")
        println("User response: 'jane@example.com'")
        val (state2, _) = currentMachine.onResponse("jane@example.com", currentState)
        machineStates[currentMachine] = state2
        currentState = state2
        println("State updated: currentIndex = ${currentState.currentIndex}")
        assertEquals(2, currentState.currentIndex)

        // Switch to survey context
        println("Q: What is your role? (not yet asked)")
        println("User response: '/context survey'")
        val (state3, intention) = currentMachine.onResponse("/context survey", currentState)
        println("Intention detected: ${intention::class.simpleName}")
        assertEquals("ChangeContext", intention::class.simpleName)
        machineStates[currentMachine] = state3 // State unchanged for onboarding
        currentState = state3
        println("Onboarding state preserved: currentIndex = ${currentState.currentIndex}")

        // Switch machines
        println("Switching to survey context...")
        currentMachine = surveyMachine
        currentContext = currentContext.copy(data = currentContext.data + ("switched_from" to "onboarding"))
        currentState = machineStates[currentMachine]!! // Get survey state (fresh)
        println("Now in: ${currentMachine::class.simpleName} with fresh state")

        // Answer survey questions
        println("Q: How satisfied are you with our product?")
        println("User response: 'Satisfied'")
        val (state4, _) = currentMachine.onResponse("Satisfied", currentState)
        machineStates[currentMachine] = state4
        currentState = state4
        println("State updated: currentIndex = ${currentState.currentIndex}")

        println("Q: What improvements would you suggest?")
        println("User response: 'Better performance'")
        val (state5, _) = currentMachine.onResponse("Better performance", currentState)
        machineStates[currentMachine] = state5
        currentState = state5
        println("State updated: currentIndex = ${currentState.currentIndex}")

        // Switch back to onboarding
        println("Q: Would you recommend our product? (not yet asked)")
        println("User response: '/context resume_onboarding'")
        val (state6, _) = currentMachine.onResponse("/context resume_onboarding", currentState)
        println("Intention detected: ${intention::class.simpleName}")
        machineStates[currentMachine] = state6 // Survey state unchanged
        println("Survey state preserved: currentIndex = ${currentState.currentIndex}")

        // Switch back to onboarding machine
        println("Switching back to onboarding context...")
        currentMachine = onboardingMachine
        currentContext = currentContext.copy(data = currentContext.data + ("resumed" to "true"))
        currentState = machineStates[currentMachine]!! // Get saved onboarding state
        println("Resumed onboarding with preserved state: currentIndex = ${currentState.currentIndex}")

        // Should continue from where we left off
        val rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        val qr = rendering as DialogRendering.QuestionRendering
        assertEquals("What is your role?", qr.question.text) // Third question
        assertEquals(2, currentState.currentIndex)

        println("=== Context Preservation Test Completed Successfully ===")
    }
}