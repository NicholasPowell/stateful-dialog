# Plan for Adding a Kobweb Demo Module

## Overview
This document outlines the plan for adding a Kobweb-based demonstration module to the stateful-dialog library. The demo will showcase the library's capabilities through an interactive web application.

## Objectives
- Create a Kobweb module that demonstrates the stateful-dialog library
- Use Kotlin and the latest version of Kobweb (v0.19.2+)
- Follow the default Kobweb layout conventions
- Provide a web interface to define dialogs and store them in memory
- Make it easy for users to understand how to use the library

## Technical Requirements

### 1. Project Structure
```
stateful-dialog/
├── build.gradle.kts              # Root build file (existing)
├── settings.gradle.kts           # Root settings (to be updated)
├── src/                          # Main library source (existing)
└── demo/                         # New Kobweb demo module
    ├── build.gradle.kts
    ├── .kobweb/
    │   └── conf.yaml
    └── src/
        └── jsMain/
            ├── kotlin/
            │   └── com/niloda/demo/
            │       ├── App.kt              # Main app entry point
            │       ├── pages/
            │       │   └── Index.kt        # Home page
            │       └── components/
            │           ├── DialogBuilder.kt
            │           ├── DialogList.kt
            │           └── DialogRunner.kt
            └── resources/
                └── public/
                    └── (static assets)
```

### 2. Gradle Configuration

#### settings.gradle.kts Updates
```kotlin
rootProject.name = "stateful-dialog"
include("demo")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }
}
```

#### demo/build.gradle.kts
The demo module needs:
- Kotlin Multiplatform plugin targeting JS
- Compose compiler plugin for Compose for Web
- Kobweb application plugin
- Dependencies on:
  - Kobweb Core
  - Kobweb Silk (UI components)
  - The main stateful-dialog library

**Note**: Due to Gradle plugin version conflicts when the root project already applies Kotlin plugins, the demo module should be created as a separate Gradle project or the root build.gradle.kts should be refactored to use a multi-project build approach with plugins configured in `pluginManagement` rather than applied at the root level.

### 3. Kobweb Configuration

#### .kobweb/conf.yaml
```yaml
site:
  title: "Stateful Dialog Demo"
  description: "Interactive demo of the stateful-dialog library"

server:
  port: 8080
  
features:
  # Enable live reloading during development
  liveReload: true
```

### 4. Application Components

#### App.kt
- Initialize Kobweb and Silk
- Set up default color mode and theme
- Provide application-level layout

#### Pages

**Index.kt** (Home Page):
- Welcome message and library overview
- Links to dialog creation and management sections
- Quick start guide

**DialogBuilder.kt** (Component):
- Form to create new dialogs
- Input fields for:
  - Dialog name
  - Flow type (onboarding, survey, support, etc.)
  - Questions (text or multiple choice)
- Add/remove questions dynamically
- Save dialog to in-memory store

**DialogList.kt** (Component):
- Display all created dialogs
- Show dialog metadata (name, question count, flow type)
- Actions: View, Edit, Delete, Run
- Search/filter functionality

**DialogRunner.kt** (Component):
- Execute a selected dialog
- Display current question
- Capture user responses
- Show validation errors
- Navigate forward/backward through questions
- Display completed responses
- Demonstrate intention detection features

### 5. In-Memory Storage

Create a simple state management solution:

```kotlin
object DialogStore {
    private val dialogs = mutableStateMapOf<String, DialogFlow>()
    private val sessions = mutableStateMapOf<String, DialogSession>()
    
    fun saveDialog(id: String, flow: DialogFlow)
    fun getDialog(id: String): DialogFlow?
    fun getAllDialogs(): Map<String, DialogFlow>
    fun deleteDialog(id: String)
    
    fun startSession(dialogId: String, userId: String): String
    fun getSession(sessionId: String): DialogSession?
    fun updateSession(sessionId: String, state: DialogState)
}

data class DialogSession(
    val id: String,
    val dialogId: String,
    val userId: String,
    val state: DialogState,
    val context: DialogContext,
    val createdAt: Long
)
```

### 6. Features to Demonstrate

1. **Dialog Creation**:
   - Create dialogs with multiple questions
   - Support both Text and MultipleChoice questions
   - Add flow context metadata

2. **Dialog Execution**:
   - Step through questions
   - Validate answers
   - Handle errors gracefully
   - Show progress

3. **Intention Detection**:
   - Demonstrate `/context` commands
   - Show `/answer X /context Y` combined actions
   - Display how context changes affect the dialog

4. **State Management**:
   - Save and restore dialog state
   - Show serialization capabilities
   - Display state snapshots

5. **Multiple Flows**:
   - Pre-populate with example flows from DialogFixtures
   - Allow creating custom flows
   - Compare different flow types

### 7. UI/UX Design

Use Kobweb Silk components for:
- Responsive layout (mobile-friendly)
- Form inputs with validation feedback
- Buttons and navigation
- Cards for dialog display
- Modal dialogs for confirmation
- Toast notifications for success/error messages

Color scheme:
- Follow Silk's default theme
- Light/dark mode support
- Accessible color contrast

### 8. Development Workflow

```bash
# Start development server
cd demo
../gradlew :demo:kobwebStart

# Build for production
../gradlew :demo:kobwebExport

# Run in production mode
../gradlew :demo:kobwebRun -t
```

### 9. Documentation

Create `demo/README.md`:
- How to run the demo
- Features overview
- Code examples
- Screenshots
- Link to main library documentation

Update main `README.md`:
- Add "Demo" section
- Link to live demo (if hosted)
- Quick start using the demo

### 10. Testing Strategy

While the demo is primarily for demonstration purposes, consider:
- Smoke tests to ensure pages load
- Integration tests for dialog creation and execution
- Validation tests for form inputs

### 11. Deployment Options

Consider deployment to:
- GitHub Pages (static export)
- Netlify
- Vercel
- Cloudflare Pages

Include deployment configuration in the plan.

### 12. Implementation Steps

1. **Phase 1: Project Setup**
   - [ ] Resolve Gradle plugin version conflicts
   - [ ] Update settings.gradle.kts
   - [ ] Create demo module structure
   - [ ] Configure Kobweb

2. **Phase 2: Core Application**
   - [ ] Create App.kt entry point
   - [ ] Build index page
   - [ ] Set up routing
   - [ ] Implement in-memory store

3. **Phase 3: Dialog Management**
   - [ ] Create DialogBuilder component
   - [ ] Implement DialogList component
   - [ ] Add CRUD operations
   - [ ] Test with sample dialogs

4. **Phase 4: Dialog Execution**
   - [ ] Build DialogRunner component
   - [ ] Integrate state machine
   - [ ] Handle user responses
   - [ ] Display validation errors
   - [ ] Show completion state

5. **Phase 5: Advanced Features**
   - [ ] Add intention detection demo
   - [ ] Implement state serialization
   - [ ] Add flow context examples
   - [ ] Create multiple dialog templates

6. **Phase 6: Polish**
   - [ ] Improve UI/UX
   - [ ] Add responsive design
   - [ ] Write documentation
   - [ ] Add screenshots
   - [ ] Test across browsers

7. **Phase 7: Deployment**
   - [ ] Configure for static export
   - [ ] Test production build
   - [ ] Set up hosting
   - [ ] Add deployment workflow

## Alternative Approach: Separate Repository

Due to Gradle plugin version management complexities, consider creating the Kobweb demo as a **separate repository**:

### Advantages:
- No plugin version conflicts
- Cleaner dependency management
- Can use latest Kobweb versions independently
- Easier to maintain

### Structure:
```
stateful-dialog-demo/
├── build.gradle.kts
├── settings.gradle.kts
├── .kobweb/
└── src/
```

### Dependencies:
```kotlin
dependencies {
    implementation("com.niloda.contextdialog:stateful-dialog:0.1.0-SNAPSHOT")
    // Other Kobweb dependencies
}
```

Link between repositories via:
- README cross-references
- Git submodules (optional)
- Shared documentation

## Conclusion

This plan provides a comprehensive roadmap for creating a Kobweb-based demo module that effectively showcases the stateful-dialog library. The implementation can proceed incrementally, with each phase building upon the previous one. The separate repository approach is recommended to avoid build system complications while maintaining clear separation of concerns.
