# GitHub Issues Creation Script

This directory contains a script to automatically create GitHub issues from the `GITHUB_ISSUES.md` file.

## Prerequisites

1. **GitHub CLI (gh)**: The script requires the GitHub CLI to be installed.
   - Installation instructions: https://cli.github.com/
   - On macOS: `brew install gh`
   - On Linux: See https://github.com/cli/cli/blob/trunk/docs/install_linux.md
   - On Windows: `winget install GitHub.cli` or `scoop install gh`

2. **Authentication**: You must authenticate with GitHub CLI before running the script.
   ```bash
   gh auth login
   ```

## Usage

### Running the Script

1. Make sure you're in the repository root directory:
   ```bash
   cd /path/to/stateful-dialog
   ```

2. Run the script:
   ```bash
   ./create-issues.sh
   ```

The script will:
- Parse `GITHUB_ISSUES.md`
- Create each issue in the `niloda-tech/stateful-dialog` repository
- Display progress for each issue created
- Show a summary mapping of issue numbers at the end

## What the Script Does

1. **Validates Prerequisites**: Checks that `gh` is installed and authenticated
2. **Parses GITHUB_ISSUES.md**: Extracts title, labels, and body for each issue
3. **Creates Issues**: Uses `gh issue create` to create each issue in order
4. **Rate Limiting**: Waits 2 seconds between issue creations to avoid rate limits
5. **Provides Mapping**: Shows which issue number was assigned to each issue

## Output

The script will output something like:

```
Creating GitHub issues from GITHUB_ISSUES.md...
Repository: niloda-tech/stateful-dialog

Creating Issue 1: Phase 1.1: Resolve Gradle plugin version conflicts for demo module
  ✓ Created: https://github.com/niloda-tech/stateful-dialog/issues/46

Creating Issue 2: Phase 1.2: Update settings.gradle.kts to include demo module
  ✓ Created: https://github.com/niloda-tech/stateful-dialog/issues/47

...

================================================
Summary: Created 29 issues
================================================

Issue Number Mapping:
--------------------
Issue 1 -> #46
Issue 2 -> #47
...
```

## Notes

- **Dependency References**: The issues contain placeholder references like `#[Phase 1.1]`. After all issues are created, you may want to manually update these references with the actual issue numbers.
- **Labels**: The script automatically applies labels as specified in `GITHUB_ISSUES.md`. Make sure these labels exist in the repository or the script will fail.
- **Order**: Issues are created in the order they appear in `GITHUB_ISSUES.md`, which follows the implementation phases.

## Troubleshooting

### Error: GitHub CLI is not installed
Install the GitHub CLI from https://cli.github.com/

### Error: GitHub CLI is not authenticated
Run `gh auth login` and follow the prompts to authenticate.

### Error: Label does not exist
Create the required labels in the repository first, or modify the script to skip labels.

### Rate Limiting
If you encounter rate limiting issues, increase the sleep time in the script (line with `sleep 2`).

## Files

- `create-issues.sh` - Main script to create issues
- `GITHUB_ISSUES.md` - Source file containing all issue definitions
- `CREATE_ISSUES_README.md` - This file
