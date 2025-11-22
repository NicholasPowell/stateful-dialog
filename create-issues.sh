#!/usr/bin/env bash

# Script to create GitHub issues from GITHUB_ISSUES.md
# This script parses the GITHUB_ISSUES.md file and creates issues using the GitHub CLI
# Compatible with bash 3.2+ (macOS and older Linux systems)

set -e

REPO="niloda-tech/stateful-dialog"
ISSUES_FILE="GITHUB_ISSUES.md"

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo "Error: GitHub CLI (gh) is not installed."
    echo "Please install it from: https://cli.github.com/"
    exit 1
fi

# Check if gh is authenticated
if ! gh auth status &> /dev/null; then
    echo "Error: GitHub CLI is not authenticated."
    echo "Please run: gh auth login"
    exit 1
fi

# Check if issues file exists
if [ ! -f "$ISSUES_FILE" ]; then
    echo "Error: $ISSUES_FILE not found in current directory"
    exit 1
fi

echo "Creating GitHub issues from $ISSUES_FILE..."
echo "Repository: $REPO"
echo ""

# Temporary directory for issue bodies and mapping
TEMP_DIR=$(mktemp -d)
trap "rm -rf $TEMP_DIR" EXIT

# File to store issue number mappings
MAPPING_FILE="$TEMP_DIR/issue_mapping.txt"
touch "$MAPPING_FILE"

# Function to create an issue
create_issue() {
    local issue_num=$1
    local title=$2
    local labels=$3
    local body_file=$4
    
    echo "Creating Issue $issue_num: $title"
    
    # Create the issue and capture the URL
    if issue_url=$(gh issue create \
        --repo "$REPO" \
        --title "$title" \
        --body-file "$body_file" \
        --label "$labels" 2>&1); then
        
        echo "  ✓ Created: $issue_url"
        # Extract issue number from URL
        issue_number=$(echo "$issue_url" | grep -oE '[0-9]+$')
        # Store mapping
        echo "Issue $issue_num -> #$issue_number" >> "$MAPPING_FILE"
        return 0
    else
        echo "  ✗ Failed to create issue"
        echo "$issue_url"
        return 1
    fi
}

# Parse the GITHUB_ISSUES.md file and extract issues
current_title=""
current_labels=""
current_body=""
in_body=false
issue_count=0

while IFS= read -r line; do
    # Detect issue boundary
    if [[ "$line" =~ ^##\ Issue\ ([0-9]+): ]]; then
        # Save previous issue if exists
        if [ -n "$current_title" ] && [ -n "$current_body" ]; then
            issue_count=$((issue_count + 1))
            body_file="$TEMP_DIR/issue_${issue_count}.md"
            echo "$current_body" > "$body_file"
            
            # Create the issue
            create_issue "$issue_count" "$current_title" "$current_labels" "$body_file"
            echo ""
            sleep 2  # Rate limiting - wait between issue creations
        fi
        
        # Reset for new issue
        current_title=""
        current_labels=""
        current_body=""
        in_body=false
        
    elif [[ "$line" =~ ^\*\*Title:\*\*\ (.+)$ ]]; then
        current_title="${BASH_REMATCH[1]}"
        
    elif [[ "$line" =~ ^\*\*Labels:\*\*\ (.+)$ ]]; then
        current_labels="${BASH_REMATCH[1]}"
        
    elif [[ "$line" =~ ^\*\*Body:\*\*$ ]]; then
        in_body=true
        
    elif [[ "$line" =~ ^---$ ]] && [ "$in_body" = true ]; then
        # End of current issue body
        continue
        
    elif [ "$in_body" = true ]; then
        if [ -n "$current_body" ]; then
            current_body="$current_body"$'\n'"$line"
        else
            current_body="$line"
        fi
    fi
done < "$ISSUES_FILE"

# Create the last issue
if [ -n "$current_title" ] && [ -n "$current_body" ]; then
    issue_count=$((issue_count + 1))
    body_file="$TEMP_DIR/issue_${issue_count}.md"
    echo "$current_body" > "$body_file"
    
    create_issue "$issue_count" "$current_title" "$current_labels" "$body_file"
fi

echo ""
echo "================================================"
echo "Summary: Created $issue_count issues"
echo "================================================"

# Print mapping of issue descriptions to numbers
if [ -s "$MAPPING_FILE" ]; then
    echo ""
    echo "Issue Number Mapping:"
    echo "--------------------"
    cat "$MAPPING_FILE"
    
    echo ""
    echo "Note: You may want to update dependency references in the issues"
    echo "to replace placeholders like '#[Phase 1.1]' with actual issue numbers."
fi

echo ""
echo "Done! All issues have been created in $REPO"
