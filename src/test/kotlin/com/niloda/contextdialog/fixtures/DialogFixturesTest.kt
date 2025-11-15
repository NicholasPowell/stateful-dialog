package com.niloda.contextdialog.fixtures

import com.niloda.contextdialog.statemachine.InOrderDialogStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DialogFixturesTest {

    @Test
    fun `user onboarding flow has correct structure`() {
        val flow = DialogFixtures.USER_ONBOARDING_FLOW
        assertEquals(5, flow.questions.size)
        assertEquals("user_onboarding", flow.flowContext?.flowType)
        assertEquals("1.0", flow.flowContext?.metadata?.get("version"))
    }

    @Test
    fun `customer survey flow has correct structure`() {
        val flow = DialogFixtures.CUSTOMER_SURVEY_FLOW
        assertEquals(5, flow.questions.size)
        assertEquals("customer_survey", flow.flowContext?.flowType)
        assertEquals("Q4_2024", flow.flowContext?.metadata?.get("survey_id"))
    }

    @Test
    fun `support chat flow has correct structure`() {
        val flow = DialogFixtures.SUPPORT_CHAT_FLOW
        assertEquals(5, flow.questions.size)
        assertEquals("support_chat", flow.flowContext?.flowType)
        assertEquals("technical", flow.flowContext?.metadata?.get("category"))
    }

    @Test
    fun `account recovery flow has correct structure`() {
        val flow = DialogFixtures.ACCOUNT_RECOVERY_FLOW
        assertEquals(5, flow.questions.size)
        assertEquals("account_recovery", flow.flowContext?.flowType)
        assertEquals("password_reset", flow.flowContext?.metadata?.get("recovery_type"))
    }

    @Test
    fun `all flows can be used with state machine`() {
        val flows = listOf(
            DialogFixtures.USER_ONBOARDING_FLOW,
            DialogFixtures.CUSTOMER_SURVEY_FLOW,
            DialogFixtures.SUPPORT_CHAT_FLOW,
            DialogFixtures.ACCOUNT_RECOVERY_FLOW
        )

        flows.forEach { flow ->
            val stateMachine = InOrderDialogStateMachine(flow)
            val initialState = stateMachine.initialState()
            assertEquals(0, initialState.currentIndex)
            assertTrue(initialState.responses.isEmpty())
        }
    }

    @Test
    fun `sample responses match question counts`() {
        val responseSets = listOf(
            DialogFixtures.SampleResponses.ONBOARDING_RESPONSES,
            DialogFixtures.SampleResponses.SURVEY_RESPONSES,
            DialogFixtures.SampleResponses.SUPPORT_RESPONSES,
            DialogFixtures.SampleResponses.RECOVERY_RESPONSES
        )

        val flows = listOf(
            DialogFixtures.USER_ONBOARDING_FLOW,
            DialogFixtures.CUSTOMER_SURVEY_FLOW,
            DialogFixtures.SUPPORT_CHAT_FLOW,
            DialogFixtures.ACCOUNT_RECOVERY_FLOW
        )

        responseSets.zip(flows).forEach { (responses, flow) ->
            assertEquals(flow.questions.size, responses.size)
            flow.questions.forEach { question ->
                assertTrue(responses.containsKey(question.id), "Missing response for question: ${question.id}")
            }
        }
    }

    @Test
    fun `validation rules work correctly`() {
        // Email validation
        assertTrue(DialogFixtures.ValidationRules.validateEmail("test@example.com"))
        assertTrue(DialogFixtures.ValidationRules.validateEmail("user.name+tag@domain.co.uk"))
        assertTrue(!DialogFixtures.ValidationRules.validateEmail("invalid-email"))
        assertTrue(!DialogFixtures.ValidationRules.validateEmail("missing@domain"))

        // Experience level validation
        assertTrue(DialogFixtures.ValidationRules.validateExperienceLevel("0-2"))
        assertTrue(DialogFixtures.ValidationRules.validateExperienceLevel("10+"))
        assertTrue(!DialogFixtures.ValidationRules.validateExperienceLevel("invalid"))

        // Severity validation
        assertTrue(DialogFixtures.ValidationRules.validateSeverity("Critical"))
        assertTrue(DialogFixtures.ValidationRules.validateSeverity("Low"))
        assertTrue(!DialogFixtures.ValidationRules.validateSeverity("Invalid"))

        // Satisfaction validation
        assertTrue(DialogFixtures.ValidationRules.validateSatisfaction("Very Satisfied"))
        assertTrue(DialogFixtures.ValidationRules.validateSatisfaction("Very Dissatisfied"))
        assertTrue(!DialogFixtures.ValidationRules.validateSatisfaction("Okay"))
    }

    @Test
    fun `dialog contexts have required fields`() {
        val contexts = listOf(
            DialogFixtures.STANDARD_USER_CONTEXT,
            DialogFixtures.PREMIUM_USER_CONTEXT,
            DialogFixtures.SUPPORT_AGENT_CONTEXT
        )

        contexts.forEach { context ->
            assertTrue(context.userId.isNotBlank())
            assertTrue(context.sessionId.isNotBlank())
        }
    }

    @Test
    fun `flows can be rendered with different contexts`() {
        val flow = DialogFixtures.USER_ONBOARDING_FLOW
        val stateMachine = InOrderDialogStateMachine(flow)
        val state = stateMachine.initialState()

        val contexts = listOf(
            DialogFixtures.STANDARD_USER_CONTEXT,
            DialogFixtures.PREMIUM_USER_CONTEXT
        )

        contexts.forEach { context ->
            val rendering = stateMachine.render(context, state)
            assertNotNull(rendering)
            // Should render the first question
            assertTrue(rendering is com.niloda.contextdialog.statemachine.DialogRendering.QuestionRendering)
        }
    }
}