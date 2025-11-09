# Opencode Development Lifecycle

## Task Creation
- New tasks are created in the default GitHub project: https://github.com/users/NicholasPowell/projects/4
- Tasks can be added manually or via automation
- Each task should have a clear title, description, and priority

## Task Review
- Do not start executing any task until the user has explicitly reviewed and approved it
- User will indicate approval by asking for execution (e.g., "execute task X")

## Task Execution
- When user requests execution:
  1. Read the task details from the GitHub project
  2. Update the task status to "In Progress"
  3. Perform the required development work
  4. Make code changes following best practices
  5. Run tests and linting to ensure quality
  6. Commit changes with descriptive messages
  7. Create a pull request for approval

## Pull Request and Approval
- All changes are submitted via pull requests
- PRs include clear descriptions of what was changed and why
- User reviews and approves PRs before merging
- After approval, merge the PR to complete the task

## Transparency Measures
- Always explain what you're doing during execution
- Provide status updates on task progress
- Use clear commit messages and PR descriptions
- Maintain git history for traceability