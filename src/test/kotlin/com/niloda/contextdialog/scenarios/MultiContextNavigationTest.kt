package com.niloda.contextdialog.scenarios

import com.niloda.contextdialog.fixtures.DialogFixtures
import com.niloda.contextdialog.statemachine.DialogRendering
import com.niloda.contextdialog.statemachine.InOrderDialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultiContextNavigationTest {

    @Test
    fun `User navigates between multiple flow contexts using intention detection`() {
        // Setup multiple state machines for different contexts
        val onboardingMachine = InOrderDialogStateMachine(DialogFixtures.USER_ONBOARDING_FLOW)
        val surveyMachine = InOrderDialogStateMachine(DialogFixtures.CUSTOMER_SURVEY_FLOW)
        val supportMachine = InOrderDialogStateMachine(DialogFixtures.SUPPORT_CHAT_FLOW)

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

        // Initial rendering - should show first onboarding question
        var rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        var qr = rendering as DialogRendering.QuestionRendering
        assertEquals("What is your full name?", qr.question.text)
        assertEquals(currentContext, qr.context)

        // User answers first question
        val (newState1, intention1) = currentMachine.onResponse("John Doe", currentState)
        assertEquals("Answer", intention1::class.simpleName)
        machineStates[currentMachine] = newState1
        currentState = newState1
        assertEquals(1, currentState.currentIndex)

        // User decides to switch to customer survey context
        val (newState2, intention2) = currentMachine.onResponse("/context survey_mode", currentState)
        assertEquals("ChangeContext", intention2::class.simpleName)
        // State remains the same, but caller would update context
        machineStates[currentMachine] = newState2
        currentState = newState2
        assertEquals(1, currentState.currentIndex) // Still at same position

        // Switch to survey machine and context
        currentMachine = surveyMachine
        currentContext = currentContext.copy(data = currentContext.data + ("mode" to "survey"))
        currentState = machineStates[currentMachine]!!

        // Render with new context - should show first survey question
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals("How satisfied are you with our product?", qr.question.text)

        // User answers survey question
        val (newState3, intention3) = currentMachine.onResponse("Very Satisfied", currentState)
        assertEquals("Answer", intention3::class.simpleName)
        machineStates[currentMachine] = newState3
        currentState = newState3
        assertEquals(1, currentState.currentIndex)

        // User wants to report an issue and switch to support context
        val (newState4, intention4) = currentMachine.onResponse("/answer The app is great! /context support_needed", currentState)
        assertEquals("AnswerWithContextChange", intention4::class.simpleName)
        val awcc = intention4 as com.niloda.contextdialog.statemachine.IntentionDetector.Intention.AnswerWithContextChange
        assertEquals("The app is great!", awcc.answer)
        assertEquals("support_needed", awcc.contextData)
        machineStates[currentMachine] = newState4
        currentState = newState4
        assertEquals(2, currentState.currentIndex)

        // Switch to support machine
        currentMachine = supportMachine
        currentContext = currentContext.copy(data = currentContext.data + ("support_reason" to "follow_up"))
        currentState = machineStates[currentMachine]!!

        // Render support question
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals("What type of issue are you experiencing?", qr.question.text)

        // User answers support question and indicates they want to go back to survey
        val (newState5, intention5) = currentMachine.onResponse("/answer Feature Request /context back_to_survey", currentState)
        assertEquals("AnswerWithContextChange", intention5::class.simpleName)
        machineStates[currentMachine] = newState5
        currentState = newState5
        assertEquals(1, currentState.currentIndex)

        // Switch back to survey machine
        currentMachine = surveyMachine
        currentContext = currentContext.copy(data = currentContext.data + ("navigation" to "back_to_survey"))
        currentState = machineStates[currentMachine]!!

        // Continue with survey
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        qr = rendering as DialogRendering.QuestionRendering
        assertEquals("Would you recommend our product?", qr.question.text)

        // Complete the survey
        val (newState6, intention6) = currentMachine.onResponse("Add more customization options", currentState)
        assertEquals("Answer", intention6::class.simpleName)
        machineStates[currentMachine] = newState6
        currentState = newState6
        assertEquals(2, currentState.currentIndex)

        val (newState7, intention7) = currentMachine.onResponse("Definitely", currentState)
        machineStates[currentMachine] = newState7
        currentState = newState7
        assertEquals(3, currentState.currentIndex)

        val (newState8, intention8) = currentMachine.onResponse("Daily", currentState)
        machineStates[currentMachine] = newState8
        currentState = newState8
        assertEquals(4, currentState.currentIndex)

        // Survey completed
        rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.Completed)
        val completed = rendering as DialogRendering.Completed
        assertEquals(4, completed.responses.size)
        assertEquals("Very Satisfied", completed.responses["satisfaction"])
        assertEquals("The app is great!", completed.responses["improvements"])
        assertEquals("Definitely", completed.responses["recommendation"])
        assertEquals("Daily", completed.responses["usage_frequency"])
    }

    @Test
    fun `Context navigation preserves partial progress across machines`() {
        // Start with onboarding
        val onboardingMachine = InOrderDialogStateMachine(DialogFixtures.USER_ONBOARDING_FLOW)
        val surveyMachine = InOrderDialogStateMachine(DialogFixtures.CUSTOMER_SURVEY_FLOW)

        // Track state for each machine
        val machineStates = mutableMapOf(
            onboardingMachine to onboardingMachine.initialState(),
            surveyMachine to surveyMachine.initialState()
        )

        var currentContext = DialogFixtures.PREMIUM_USER_CONTEXT
        var currentMachine = onboardingMachine
        var currentState = machineStates[currentMachine]!!

        // Answer first two onboarding questions
        val (state1, _) = currentMachine.onResponse("Jane Smith", currentState)
        machineStates[currentMachine] = state1
        currentState = state1

        val (state2, _) = currentMachine.onResponse("jane@example.com", currentState)
        machineStates[currentMachine] = state2
        currentState = state2
        assertEquals(2, currentState.currentIndex)

        // Switch to survey context
        val (state3, intention) = currentMachine.onResponse("/context survey", currentState)
        assertEquals("ChangeContext", intention::class.simpleName)
        machineStates[currentMachine] = state3 // State unchanged for onboarding
        currentState = state3

        // Switch machines
        currentMachine = surveyMachine
        currentContext = currentContext.copy(data = currentContext.data + ("switched_from" to "onboarding"))
        currentState = machineStates[currentMachine]!! // Get survey state (fresh)

        // Answer survey questions
        val (state4, _) = currentMachine.onResponse("Satisfied", currentState)
        machineStates[currentMachine] = state4
        currentState = state4

        val (state5, _) = currentMachine.onResponse("Better performance", currentState)
        machineStates[currentMachine] = state5
        currentState = state5

        // Switch back to onboarding
        val (state6, _) = currentMachine.onResponse("/context resume_onboarding", currentState)
        machineStates[currentMachine] = state6 // Survey state unchanged

        // Switch back to onboarding machine
        currentMachine = onboardingMachine
        currentContext = currentContext.copy(data = currentContext.data + ("resumed" to "true"))
        currentState = machineStates[currentMachine]!! // Get saved onboarding state

        // Should continue from where we left off
        val rendering = currentMachine.render(currentContext, currentState)
        assertTrue(rendering is DialogRendering.QuestionRendering)
        val qr = rendering as DialogRendering.QuestionRendering
        assertEquals("What is your role?", qr.question.text) // Third question
        assertEquals(2, currentState.currentIndex)
    }
}