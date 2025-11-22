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

# Track labels that have been checked/created
LABELS_CHECKED_FILE="$TEMP_DIR/labels_checked.txt"
touch "$LABELS_CHECKED_FILE"

# Function to ensure a label exists, creating it if necessary
ensure_label_exists() {
    local label=$1
    
    # Check if we've already verified this label
    if grep -qx "$label" "$LABELS_CHECKED_FILE" 2>/dev/null; then
        return 0
    fi
    
    # Check if label exists in the repository
    if gh label list --repo "$REPO" | grep -q "^$label[[:space:]]"; then
        echo "$label" >> "$LABELS_CHECKED_FILE"
        return 0
    fi
    
    # Label doesn't exist, create it with a default color
    echo "  Creating missing label: $label"
    if gh label create "$label" --repo "$REPO" --color "0366d6" 2>/dev/null; then
        echo "$label" >> "$LABELS_CHECKED_FILE"
        return 0
    else
        echo "  Warning: Could not create label '$label', it may already exist"
        echo "$label" >> "$LABELS_CHECKED_FILE"
        return 0
    fi
}

# Function to ensure all labels in a comma-separated list exist
ensure_labels_exist() {
    local labels_string=$1
    local label
    
    # Split labels by comma and check each one (bash 3.x compatible)
    # Save IFS and change to comma
    local old_ifs=$IFS
    IFS=','
    for label in $labels_string; do
        IFS=$old_ifs
        # Trim any whitespace (though we normalize earlier)
        label=$(echo "$label" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
        if [ -n "$label" ]; then
            ensure_label_exists "$label"
        fi
        IFS=','
    done
    IFS=$old_ifs
}

# Function to create an issue
create_issue() {
    local issue_num=$1
    local title=$2
    local labels=$3
    local body_file=$4
    
    echo "Creating Issue $issue_num: $title"
    
    # Ensure all labels exist before creating the issue
    ensure_labels_exist "$labels"
    
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
        # Remove leading/trailing whitespace and normalize spaces around commas
        current_labels="${BASH_REMATCH[1]}"
        # Remove spaces around commas for proper label parsing
        current_labels=$(echo "$current_labels" | sed 's/ *, */,/g')
        
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
