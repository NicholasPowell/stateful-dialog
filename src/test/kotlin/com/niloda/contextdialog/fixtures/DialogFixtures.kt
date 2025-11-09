package com.niloda.contextdialog.fixtures

import com.niloda.contextdialog.*

/**
 * Comprehensive scenario fixtures for testing different flow contexts.
 * Provides reusable test data and examples for the dialog system.
 */
object DialogFixtures {

    // Flow Contexts
    val USER_ONBOARDING_CONTEXT = FlowContext(
        flowType = "user_onboarding",
        metadata = mapOf("version" to "1.0", "priority" to "high")
    )

    val CUSTOMER_SURVEY_CONTEXT = FlowContext(
        flowType = "customer_survey",
        metadata = mapOf("survey_id" to "Q4_2024", "department" to "product")
    )

    val SUPPORT_CHAT_CONTEXT = FlowContext(
        flowType = "support_chat",
        metadata = mapOf("category" to "technical", "urgency" to "medium")
    )

    val ACCOUNT_RECOVERY_CONTEXT = FlowContext(
        flowType = "account_recovery",
        metadata = mapOf("recovery_type" to "password_reset", "security_level" to "high")
    )

    // Question Sets
    val USER_ONBOARDING_QUESTIONS = listOf(
        Question.Text("name", "What is your full name?"),
        Question.Text("email", "What is your email address?"),
        Question.MultipleChoice("role", "What is your role?", listOf("Developer", "Designer", "Manager", "Other")),
        Question.MultipleChoice("experience", "How many years of experience do you have?", listOf("0-2", "3-5", "6-10", "10+"))
    )

    val CUSTOMER_SURVEY_QUESTIONS = listOf(
        Question.MultipleChoice("satisfaction", "How satisfied are you with our product?", listOf("Very Satisfied", "Satisfied", "Neutral", "Dissatisfied", "Very Dissatisfied")),
        Question.Text("improvements", "What improvements would you suggest?"),
        Question.MultipleChoice("recommendation", "Would you recommend our product?", listOf("Definitely", "Probably", "Not Sure", "Probably Not", "Definitely Not")),
        Question.MultipleChoice("usage_frequency", "How often do you use our product?", listOf("Daily", "Weekly", "Monthly", "Rarely", "First Time"))
    )

    val SUPPORT_CHAT_QUESTIONS = listOf(
        Question.MultipleChoice("issue_type", "What type of issue are you experiencing?", listOf("Login Problem", "Performance Issue", "Feature Request", "Bug Report", "Other")),
        Question.Text("description", "Please describe the issue in detail"),
        Question.MultipleChoice("severity", "How severe is this issue?", listOf("Critical", "High", "Medium", "Low")),
        Question.MultipleChoice("reproduction", "Can you consistently reproduce this issue?", listOf("Yes", "No", "Sometimes"))
    )

    val ACCOUNT_RECOVERY_QUESTIONS = listOf(
        Question.Text("username", "What is your username or email?"),
        Question.MultipleChoice("recovery_method", "How would you like to recover your account?", listOf("Email Reset Link", "SMS Code", "Security Questions")),
        Question.Text("last_login", "When did you last successfully log in? (optional)"),
        Question.MultipleChoice("suspicious_activity", "Did you notice any suspicious activity?", listOf("Yes", "No", "Not Sure"))
    )

    // Complete Dialog Flows
    val USER_ONBOARDING_FLOW = DialogFlow(USER_ONBOARDING_QUESTIONS, USER_ONBOARDING_CONTEXT)
    val CUSTOMER_SURVEY_FLOW = DialogFlow(CUSTOMER_SURVEY_QUESTIONS, CUSTOMER_SURVEY_CONTEXT)
    val SUPPORT_CHAT_FLOW = DialogFlow(SUPPORT_CHAT_QUESTIONS, SUPPORT_CHAT_CONTEXT)
    val ACCOUNT_RECOVERY_FLOW = DialogFlow(ACCOUNT_RECOVERY_QUESTIONS, ACCOUNT_RECOVERY_CONTEXT)

    // Dialog Contexts
    val STANDARD_USER_CONTEXT = DialogContext(
        userId = "user123",
        sessionId = "session456",
        data = mapOf("locale" to "en_US", "timezone" to "UTC")
    )

    val PREMIUM_USER_CONTEXT = DialogContext(
        userId = "premium789",
        sessionId = "premium_session",
        data = mapOf("subscription" to "premium", "locale" to "en_US", "features" to "advanced")
    )

    val SUPPORT_AGENT_CONTEXT = DialogContext(
        userId = "agent001",
        sessionId = "support_session",
        data = mapOf("role" to "support_agent", "department" to "technical")
    )

    // Sample Responses for Testing
    object SampleResponses {
        // User Onboarding
        val ONBOARDING_RESPONSES = mapOf(
            "name" to "John Doe",
            "email" to "john.doe@example.com",
            "role" to "Developer",
            "experience" to "3-5"
        )

        // Customer Survey
        val SURVEY_RESPONSES = mapOf(
            "satisfaction" to "Very Satisfied",
            "improvements" to "Add more customization options",
            "recommendation" to "Definitely",
            "usage_frequency" to "Daily"
        )

        // Support Chat
        val SUPPORT_RESPONSES = mapOf(
            "issue_type" to "Performance Issue",
            "description" to "The app is slow when loading large datasets",
            "severity" to "High",
            "reproduction" to "Yes"
        )

        // Account Recovery
        val RECOVERY_RESPONSES = mapOf(
            "username" to "user@example.com",
            "recovery_method" to "Email Reset Link",
            "last_login" to "Yesterday",
            "suspicious_activity" to "No"
        )
    }

    // Context-Specific Validation Rules
    object ValidationRules {
        fun validateEmail(email: String): Boolean {
            return email.contains("@") && email.contains(".")
        }

        fun validateExperienceLevel(level: String): Boolean {
            return level in listOf("0-2", "3-5", "6-10", "10+")
        }

        fun validateSeverity(severity: String): Boolean {
            return severity in listOf("Critical", "High", "Medium", "Low")
        }

        fun validateSatisfaction(satisfaction: String): Boolean {
            return satisfaction in listOf("Very Satisfied", "Satisfied", "Neutral", "Dissatisfied", "Very Dissatisfied")
        }
    }
}