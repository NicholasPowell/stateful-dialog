# GitHub Issues for Kobweb Demo Module Implementation

This document contains GitHub issues derived from the KOBWEB_DEMO_PLAN.md. Copy each issue below to create them in the GitHub repository.

## Important Notes

**Dependency References:** Issues include dependency references in the format `#[Phase X.Y]` which are placeholders. After creating each issue in GitHub, you should:
1. Note the issue number assigned by GitHub
2. Update subsequent issues to replace placeholder references with actual issue numbers (e.g., replace `#[Phase 1.1]` with `#123`)
3. Alternatively, create all issues first and then edit them to add the actual issue number links

**Creating Issues:** Issues should be created in order (Phase 1 first, then Phase 2, etc.) to maintain proper dependency tracking.

---

## Issue 1: Phase 1 - Resolve Gradle plugin version conflicts

**Title:** Phase 1.1: Resolve Gradle plugin version conflicts for demo module

**Labels:** enhancement, kobweb-demo, phase-1

**Body:**
### Description
Resolve Gradle plugin version conflicts between the root project (Kotlin JVM) and the demo module (Kotlin Multiplatform/JS). The root project applies Kotlin plugins with versions that conflict when trying to add a multiplatform subproject.

### Acceptance Criteria
- [ ] Research and document Gradle plugin management strategies for mixed JVM/JS multi-project builds
- [ ] Choose between:
  - Refactoring root build.gradle.kts to use `pluginManagement` without applying plugins at root
  - Creating demo as separate repository (recommended approach per plan)
- [ ] If using multi-project approach: Update root build.gradle.kts to avoid plugin version conflicts
- [ ] Document the chosen approach and rationale

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 2 (Gradle Configuration) and Alternative Approach section

### Dependencies
None - this is the first phase

---

## Issue 2: Phase 1 - Update settings.gradle.kts

**Title:** Phase 1.2: Update settings.gradle.kts to include demo module

**Labels:** enhancement, kobweb-demo, phase-1

**Body:**
### Description
Update `settings.gradle.kts` to include the demo module and add required repositories for Kobweb dependencies.

### Acceptance Criteria
- [ ] Add `include("demo")` to settings.gradle.kts
- [ ] Add Kobweb repositories to `pluginManagement`:
  - `maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")`
  - `maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")`
- [ ] Add Kobweb repositories to `dependencyResolutionManagement`
- [ ] Verify settings with `./gradlew projects`

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 2 (Gradle Configuration)

### Dependencies
- Issue #[Phase 1.1] - Must resolve plugin conflicts first

---

## Issue 3: Phase 1 - Create demo module structure

**Title:** Phase 1.3: Create demo module directory structure

**Labels:** enhancement, kobweb-demo, phase-1

**Body:**
### Description
Create the directory structure for the Kobweb demo module following Kobweb conventions.

### Acceptance Criteria
- [ ] Create `demo/` directory
- [ ] Create `demo/src/jsMain/kotlin/com/niloda/demo/` directory
- [ ] Create subdirectories:
  - `demo/src/jsMain/kotlin/com/niloda/demo/pages/`
  - `demo/src/jsMain/kotlin/com/niloda/demo/components/`
  - `demo/src/jsMain/resources/public/`
- [ ] Create `demo/.kobweb/` directory
- [ ] Verify structure matches Kobweb conventions

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 1 (Project Structure)

### Dependencies
- Issue #[Phase 1.1] - Plugin conflicts must be resolved
- Issue #[Phase 1.2] - Settings must be updated

---

## Issue 4: Phase 1 - Configure Kobweb build

**Title:** Phase 1.4: Create demo/build.gradle.kts and configure Kobweb

**Labels:** enhancement, kobweb-demo, phase-1

**Body:**
### Description
Create the build configuration for the demo module with Kobweb plugins and dependencies.

### Acceptance Criteria
- [ ] Create `demo/build.gradle.kts` with:
  - Kotlin Multiplatform plugin
  - Kotlin Compose compiler plugin
  - Kobweb application plugin (v0.19.2+)
- [ ] Configure JS target with IR backend
- [ ] Add dependencies:
  - `com.varabyte.kobweb:kobweb-core:0.19.2`
  - `com.varabyte.kobweb:kobweb-silk:0.19.2`
  - `com.varabyte.kobwebx:silk-icons-fa:0.19.2`
  - `implementation(project(":"))`
- [ ] Create `.kobweb/conf.yaml` with site configuration
- [ ] Verify build with `./gradlew :demo:build`

### Related Documents
- KOBWEB_DEMO_PLAN.md - Sections 2 and 3

### Dependencies
- Issue #[Phase 1.3] - Directory structure must exist

---

## Issue 5: Phase 2 - Create App.kt entry point

**Title:** Phase 2.1: Create App.kt entry point and initialize Kobweb

**Labels:** enhancement, kobweb-demo, phase-2

**Body:**
### Description
Create the main application entry point following Kobweb conventions with Silk integration.

### Acceptance Criteria
- [ ] Create `demo/src/jsMain/kotlin/com/niloda/demo/App.kt`
- [ ] Implement `@App` annotated composable function
- [ ] Initialize SilkApp
- [ ] Configure default color mode (LIGHT)
- [ ] Add InitSilk configuration
- [ ] Verify app starts with `./gradlew :demo:kobwebStart`

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 4 (Application Components)

### Dependencies
- Issue #[Phase 1.4] - Build must be configured

---

## Issue 6: Phase 2 - Build index page

**Title:** Phase 2.2: Create Index.kt home page

**Labels:** enhancement, kobweb-demo, phase-2

**Body:**
### Description
Create the home/index page with welcome message and navigation to demo features.

### Acceptance Criteria
- [ ] Create `demo/src/jsMain/kotlin/com/niloda/demo/pages/Index.kt`
- [ ] Add `@Page` annotation
- [ ] Implement welcome message
- [ ] Add library overview description
- [ ] Add navigation links to:
  - Dialog creation section
  - Dialog management section
- [ ] Include quick start guide
- [ ] Use Kobweb Silk components for layout
- [ ] Test page loads at http://localhost:8080

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 4 (Application Components)

### Dependencies
- Issue #[Phase 2.1] - App entry point must exist

---

## Issue 7: Phase 2 - Implement in-memory DialogStore

**Title:** Phase 2.3: Implement in-memory state management (DialogStore)

**Labels:** enhancement, kobweb-demo, phase-2

**Body:**
### Description
Create an in-memory storage solution for managing dialogs and dialog sessions.

### Acceptance Criteria
- [ ] Create `DialogStore` object with:
  - `mutableStateMapOf<String, DialogFlow>()` for dialogs
  - `mutableStateMapOf<String, DialogSession>()` for sessions
- [ ] Implement dialog CRUD methods:
  - `saveDialog(id: String, flow: DialogFlow)`
  - `getDialog(id: String): DialogFlow?`
  - `getAllDialogs(): Map<String, DialogFlow>`
  - `deleteDialog(id: String)`
- [ ] Implement session management methods:
  - `startSession(dialogId: String, userId: String): String`
  - `getSession(sessionId: String): DialogSession?`
  - `updateSession(sessionId: String, state: DialogState)`
- [ ] Create `DialogSession` data class
- [ ] Add unit tests for store operations

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 5 (In-Memory Storage)

### Dependencies
- Issue #[Phase 2.1] - App must be initialized

---

## Issue 8: Phase 3 - Create DialogBuilder component

**Title:** Phase 3.1: Create DialogBuilder component for creating dialogs

**Labels:** enhancement, kobweb-demo, phase-3

**Body:**
### Description
Build a form component that allows users to create new dialogs with questions.

### Acceptance Criteria
- [ ] Create `demo/src/jsMain/kotlin/com/niloda/demo/components/DialogBuilder.kt`
- [ ] Add form inputs for:
  - Dialog name (text input)
  - Flow type (dropdown: onboarding, survey, support, etc.)
  - Question text (text input)
  - Question type selector (Text or MultipleChoice)
  - Options input (for MultipleChoice questions)
- [ ] Implement add/remove question functionality
- [ ] Add "Save Dialog" button that stores to DialogStore
- [ ] Include form validation
- [ ] Add success/error toast notifications
- [ ] Use Silk form components

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 4 (Application Components)

### Dependencies
- Issue #[Phase 2.3] - DialogStore must exist

---

## Issue 9: Phase 3 - Implement DialogList component

**Title:** Phase 3.2: Implement DialogList component to display all dialogs

**Labels:** enhancement, kobweb-demo, phase-3

**Body:**
### Description
Create a component to display all saved dialogs with management actions.

### Acceptance Criteria
- [ ] Create `demo/src/jsMain/kotlin/com/niloda/demo/components/DialogList.kt`
- [ ] Display dialog cards with:
  - Dialog name
  - Flow type
  - Question count
  - Created date (if tracked)
- [ ] Add action buttons for each dialog:
  - View details
  - Edit (stretch goal)
  - Delete (with confirmation)
  - Run dialog
- [ ] Implement search/filter functionality
- [ ] Show empty state when no dialogs exist
- [ ] Use Silk Card components for display

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 4 (Application Components)

### Dependencies
- Issue #[Phase 2.3] - DialogStore must exist

---

## Issue 10: Phase 3 - Add dialog CRUD operations

**Title:** Phase 3.3: Integrate CRUD operations between components and store

**Labels:** enhancement, kobweb-demo, phase-3

**Body:**
### Description
Connect DialogBuilder and DialogList components to DialogStore for full CRUD functionality.

### Acceptance Criteria
- [ ] DialogBuilder can create and save dialogs to store
- [ ] DialogList reads from store and displays all dialogs
- [ ] Delete operation removes dialog from store and updates UI
- [ ] Updates are reactive (UI updates when store changes)
- [ ] Add error handling for operations
- [ ] Test all CRUD operations work correctly

### Related Documents
- KOBWEB_DEMO_PLAN.md - Sections 4 and 5

### Dependencies
- Issue #[Phase 3.1] - DialogBuilder must exist
- Issue #[Phase 3.2] - DialogList must exist

---

## Issue 11: Phase 3 - Test with sample dialogs

**Title:** Phase 3.4: Pre-populate demo with sample dialogs from DialogFixtures

**Labels:** enhancement, kobweb-demo, phase-3

**Body:**
### Description
Pre-populate the demo with example dialogs from the library's DialogFixtures for better UX.

### Acceptance Criteria
- [ ] Import DialogFixtures from main library
- [ ] Add initialization code to load sample dialogs on first run:
  - USER_ONBOARDING_FLOW
  - CUSTOMER_SURVEY_FLOW
  - SUPPORT_CHAT_FLOW (if available)
  - ACCOUNT_RECOVERY_FLOW (if available)
- [ ] Add "Load Sample Dialogs" button
- [ ] Verify sample dialogs display correctly in DialogList
- [ ] Test running sample dialogs

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.5 (Multiple Flows)
- Main library README - Testing with Fixtures section

### Dependencies
- Issue #[Phase 3.3] - CRUD operations must work

---

## Issue 12: Phase 4 - Build DialogRunner component

**Title:** Phase 4.1: Create DialogRunner component to execute dialogs

**Labels:** enhancement, kobweb-demo, phase-4

**Body:**
### Description
Build the interactive component that runs a dialog and collects user responses.

### Acceptance Criteria
- [ ] Create `demo/src/jsMain/kotlin/com/niloda/demo/components/DialogRunner.kt`
- [ ] Display current question with:
  - Question text
  - Appropriate input (text field or multiple choice)
  - Question number / total questions
- [ ] Add navigation buttons:
  - "Next" / "Submit Answer"
  - "Previous" (if applicable)
- [ ] Show progress indicator
- [ ] Display validation errors from library
- [ ] Handle dialog completion state
- [ ] Show collected responses on completion

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 4 (Application Components)

### Dependencies
- Issue #[Phase 2.3] - DialogStore for session management

---

## Issue 13: Phase 4 - Integrate state machine

**Title:** Phase 4.2: Integrate InOrderDialogStateMachine with DialogRunner

**Labels:** enhancement, kobweb-demo, phase-4

**Body:**
### Description
Connect the DialogRunner component to the stateful-dialog library's state machine.

### Acceptance Criteria
- [ ] Create DialogStateMachine instance when running a dialog
- [ ] Use `initialState()` to start dialog
- [ ] Call `render()` to get current question
- [ ] Use `onAction()` or `onResponse()` to process user input
- [ ] Handle `DialogRendering.QuestionRendering` state
- [ ] Handle `DialogRendering.Completed` state
- [ ] Maintain DialogContext throughout session
- [ ] Store session state in DialogStore

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.2 (Dialog Execution)
- Main library README - Usage section

### Dependencies
- Issue #[Phase 4.1] - DialogRunner component must exist

---

## Issue 14: Phase 4 - Handle user responses and validation

**Title:** Phase 4.3: Implement response handling and validation error display

**Labels:** enhancement, kobweb-demo, phase-4

**Body:**
### Description
Add proper handling of user responses with validation feedback from the library.

### Acceptance Criteria
- [ ] Capture user input for current question
- [ ] Call state machine with user response
- [ ] Display validation errors:
  - Empty answer errors
  - Invalid choice errors
- [ ] Keep user on same question when validation fails
- [ ] Show error messages using Silk components
- [ ] Clear errors when user corrects input
- [ ] Progress to next question on valid response

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.2 (Dialog Execution)

### Dependencies
- Issue #[Phase 4.2] - State machine integration must work

---

## Issue 15: Phase 4 - Display completion state and responses

**Title:** Phase 4.4: Show completion state and collected responses

**Labels:** enhancement, kobweb-demo, phase-4

**Body:**
### Description
Display a completion screen showing all collected responses when dialog finishes.

### Acceptance Criteria
- [ ] Detect `DialogRendering.Completed` state
- [ ] Display completion message
- [ ] Show all questions and answers in a summary view
- [ ] Add option to:
  - Start dialog again
  - Return to dialog list
  - Export responses (stretch goal)
- [ ] Use attractive formatting for response display
- [ ] Clear session from DialogStore on completion

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.2 (Dialog Execution)

### Dependencies
- Issue #[Phase 4.3] - Response handling must work

---

## Issue 16: Phase 5 - Add intention detection demo

**Title:** Phase 5.1: Demonstrate intention detection features

**Labels:** enhancement, kobweb-demo, phase-5

**Body:**
### Description
Add UI elements and examples that demonstrate the library's intention detection capabilities.

### Acceptance Criteria
- [ ] Add help text explaining intention detection syntax:
  - `/context <data>` for context changes
  - `/answer <answer> /context <data>` for combined actions
- [ ] Show examples of each intention type
- [ ] Display parsed intention when user submits response
- [ ] Show how context changes affect the dialog
- [ ] Add a "Try Intention Detection" section with examples
- [ ] Update DialogContext when context changes detected

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.3 (Intention Detection)
- Main library README - Intention Detection section

### Dependencies
- Issue #[Phase 4.4] - Basic dialog execution must work

---

## Issue 17: Phase 5 - Implement state serialization demo

**Title:** Phase 5.2: Demonstrate state serialization capabilities

**Labels:** enhancement, kobweb-demo, phase-5

**Body:**
### Description
Add features that showcase the library's serialization support for state persistence.

### Acceptance Criteria
- [ ] Add "Save State" button during dialog execution
- [ ] Use state machine's `snapshotState()` method
- [ ] Serialize state to JSON using Kotlin serialization
- [ ] Display serialized JSON in a code block
- [ ] Add "Restore State" functionality
- [ ] Use `restoreState()` to resume dialog from snapshot
- [ ] Add copy-to-clipboard for JSON
- [ ] Show practical use cases for serialization

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.4 (State Management)
- Main library README - Serialization section

### Dependencies
- Issue #[Phase 4.2] - State machine integration must work

---

## Issue 18: Phase 5 - Add flow context examples

**Title:** Phase 5.3: Create examples showcasing FlowContext usage

**Labels:** enhancement, kobweb-demo, phase-5

**Body:**
### Description
Demonstrate how FlowContext affects dialog behavior with practical examples.

### Acceptance Criteria
- [ ] Create dialogs with different flow types:
  - onboarding
  - survey
  - support
  - account_recovery
- [ ] Add metadata fields to DialogBuilder
- [ ] Display flow context information in DialogRunner
- [ ] Show how metadata can be used
- [ ] Add examples of context-aware behavior
- [ ] Document flow context best practices

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.5 (Multiple Flows)
- Main library README - FlowContext section

### Dependencies
- Issue #[Phase 3.1] - DialogBuilder must support flow context

---

## Issue 19: Phase 5 - Create multiple dialog templates

**Title:** Phase 5.4: Add dialog templates for common use cases

**Labels:** enhancement, kobweb-demo, phase-5

**Body:**
### Description
Create pre-built dialog templates that users can select and customize.

### Acceptance Criteria
- [ ] Create template selector in DialogBuilder
- [ ] Add templates for:
  - User onboarding (5 questions)
  - Customer survey (5 questions)
  - Support intake (5 questions)
  - Feedback form (3-5 questions)
- [ ] Allow users to customize templates
- [ ] Pre-fill template data from DialogFixtures
- [ ] Add template descriptions
- [ ] Test all templates work correctly

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6.5 (Multiple Flows)

### Dependencies
- Issue #[Phase 3.1] - DialogBuilder must exist

---

## Issue 20: Phase 6 - Improve UI/UX design

**Title:** Phase 6.1: Polish UI/UX with Silk components

**Labels:** enhancement, kobweb-demo, phase-6, ui/ux

**Body:**
### Description
Enhance the visual design and user experience using Kobweb Silk components.

### Acceptance Criteria
- [ ] Apply consistent spacing and padding
- [ ] Use Silk color palette throughout
- [ ] Improve button styling and states
- [ ] Add hover effects
- [ ] Enhance form input styling
- [ ] Use Silk typography system
- [ ] Add icons from silk-icons-fa
- [ ] Improve visual hierarchy
- [ ] Add transitions and animations (subtle)

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 7 (UI/UX Design)

### Dependencies
- Issues #[Phase 3-4] - Core components must exist

---

## Issue 21: Phase 6 - Add responsive design

**Title:** Phase 6.2: Make demo mobile-friendly with responsive design

**Labels:** enhancement, kobweb-demo, phase-6, ui/ux

**Body:**
### Description
Ensure the demo works well on mobile devices and different screen sizes.

### Acceptance Criteria
- [ ] Test on mobile viewports (320px, 375px, 425px)
- [ ] Test on tablet viewports (768px, 1024px)
- [ ] Use responsive Silk layout components
- [ ] Adjust spacing for smaller screens
- [ ] Make forms usable on mobile
- [ ] Ensure touch targets are adequate (44px minimum)
- [ ] Test with browser DevTools device emulation
- [ ] Fix any layout issues on small screens

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 7 (UI/UX Design)

### Dependencies
- Issue #[Phase 6.1] - UI improvements should be done first

---

## Issue 22: Phase 6 - Write demo documentation

**Title:** Phase 6.3: Create demo/README.md with usage instructions

**Labels:** documentation, kobweb-demo, phase-6

**Body:**
### Description
Write comprehensive documentation for the demo module.

### Acceptance Criteria
- [ ] Create `demo/README.md` with:
  - How to run the demo locally
  - Development workflow commands
  - Features overview
  - Code examples
  - Architecture explanation
- [ ] Add inline code comments
- [ ] Document component props/parameters
- [ ] Add troubleshooting section
- [ ] Include link to main library docs

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 9 (Documentation)

### Dependencies
- Issues #[Phase 3-5] - Features must be implemented

---

## Issue 23: Phase 6 - Add screenshots and visual documentation

**Title:** Phase 6.4: Add screenshots to demo documentation

**Labels:** documentation, kobweb-demo, phase-6

**Body:**
### Description
Capture and add screenshots showing the demo's features and UI.

### Acceptance Criteria
- [ ] Take screenshots of:
  - Home page
  - Dialog creation form
  - Dialog list view
  - Dialog runner in action
  - Validation errors
  - Completion screen
  - Intention detection examples
- [ ] Store screenshots in `demo/screenshots/` or `docs/`
- [ ] Add screenshots to demo/README.md
- [ ] Ensure screenshots show both light/dark modes
- [ ] Optimize image sizes

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 9 (Documentation)

### Dependencies
- Issue #[Phase 6.1] - UI should be polished

---

## Issue 24: Phase 6 - Cross-browser testing

**Title:** Phase 6.5: Test demo across different browsers

**Labels:** testing, kobweb-demo, phase-6

**Body:**
### Description
Test the demo application in different browsers to ensure compatibility.

### Acceptance Criteria
- [ ] Test in Chrome/Chromium
- [ ] Test in Firefox
- [ ] Test in Safari (if available)
- [ ] Test in Edge
- [ ] Document any browser-specific issues
- [ ] Fix critical compatibility issues
- [ ] Verify all features work in each browser
- [ ] Test on different operating systems if possible

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 6 (Phase 6: Polish)

### Dependencies
- Issues #[Phase 3-5] - All features must be complete

---

## Issue 25: Phase 7 - Configure for static export

**Title:** Phase 7.1: Configure Kobweb for static site export

**Labels:** deployment, kobweb-demo, phase-7

**Body:**
### Description
Set up the demo for static export to enable deployment to static hosts.

### Acceptance Criteria
- [ ] Verify `kobwebExport` task works
- [ ] Configure `.kobweb/conf.yaml` for export
- [ ] Set appropriate base path if needed
- [ ] Test exported site locally
- [ ] Verify all features work in exported build
- [ ] Document export process
- [ ] Optimize bundle size if needed

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 8 (Development Workflow)
- Section 11 (Deployment Options)

### Dependencies
- Issues #[Phase 6] - Demo must be complete and polished

---

## Issue 26: Phase 7 - Test production build

**Title:** Phase 7.2: Test production build and optimize

**Labels:** deployment, kobweb-demo, phase-7

**Body:**
### Description
Build the demo in production mode and verify it works correctly.

### Acceptance Criteria
- [ ] Run `./gradlew :demo:kobwebExport -PkobwebReuseServer=false`
- [ ] Test production build locally
- [ ] Verify all features work in production mode
- [ ] Check bundle size and optimize if needed
- [ ] Test loading performance
- [ ] Fix any production-only issues
- [ ] Document production build process

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 8 and 11

### Dependencies
- Issue #[Phase 7.1] - Export configuration must be done

---

## Issue 27: Phase 7 - Set up hosting

**Title:** Phase 7.3: Deploy demo to hosting platform

**Labels:** deployment, kobweb-demo, phase-7

**Body:**
### Description
Deploy the demo to a hosting platform for public access.

### Acceptance Criteria
- [ ] Choose hosting platform (GitHub Pages, Netlify, Vercel, or Cloudflare Pages)
- [ ] Create deployment configuration
- [ ] Set up automated deployment
- [ ] Configure custom domain (if available)
- [ ] Test deployed site
- [ ] Verify all features work on live site
- [ ] Add deployment status badge to README

### Deployment Options (choose one):
- GitHub Pages (recommended for GitHub repos)
- Netlify
- Vercel
- Cloudflare Pages

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 11 (Deployment Options)

### Dependencies
- Issue #[Phase 7.2] - Production build must be tested

---

## Issue 28: Phase 7 - Add deployment workflow

**Title:** Phase 7.4: Create CI/CD workflow for automatic deployment

**Labels:** deployment, kobweb-demo, phase-7, ci/cd

**Body:**
### Description
Set up GitHub Actions workflow to automatically build and deploy the demo.

### Acceptance Criteria
- [ ] Create `.github/workflows/deploy-demo.yml`
- [ ] Configure workflow to:
  - Build the demo on push to main
  - Run tests (if available)
  - Export static site
  - Deploy to chosen hosting platform
- [ ] Test workflow runs successfully
- [ ] Add workflow status badge to README
- [ ] Document deployment process

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 11 (Deployment Options)

### Dependencies
- Issue #[Phase 7.3] - Hosting must be configured

---

## Issue 29: Update main README with demo information

**Title:** Update main README.md with demo section and links

**Labels:** documentation, kobweb-demo

**Body:**
### Description
Update the main repository README to reference the demo module.

### Acceptance Criteria
- [ ] Add "Demo" section to main README.md
- [ ] Include link to live demo (once deployed)
- [ ] Add quick start section referencing the demo
- [ ] Add screenshot or GIF showing demo in action
- [ ] Update installation instructions if needed
- [ ] Link to demo/README.md for detailed instructions

### Related Documents
- KOBWEB_DEMO_PLAN.md - Section 9 (Documentation)

### Dependencies
- Issue #[Phase 6.3] - Demo README must exist
- Issue #[Phase 7.3] - Demo should be deployed

---

## Notes

- These issues follow the implementation phases outlined in KOBWEB_DEMO_PLAN.md
- Issues are ordered to show dependencies and logical progression
- Each issue includes acceptance criteria for clear definition of done
- Labels help organize issues by phase and type
- Dependencies are noted to help with planning
- Some issues can be worked in parallel if dependencies are met

To create these issues in GitHub:
1. Copy each issue's content
2. Go to the repository's Issues page
3. Click "New Issue"
4. Paste the title and body
5. Add the specified labels
6. Create the issue
7. Update dependency references in remaining issues with actual issue numbers

Alternatively, use GitHub CLI:
```bash
# Create an issue with title, body, and labels
gh issue create --repo niloda-tech/stateful-dialog --title "Issue Title" --body "Issue body" --label "label1,label2"

# Or create from a file
gh issue create --repo niloda-tech/stateful-dialog --title "Issue Title" --body-file issue-body.md --label "label1,label2"
```

After creating each issue, note its number and update subsequent issue dependency references.
