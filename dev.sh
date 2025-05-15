#!/bin/bash

# GitHub Repository Management Script
# Prerequisites:
# 1. Install GitHub CLI (gh): https://cli.github.com/
# 2. Authenticate with: gh auth login (this script will help if not logged in)

# Exit on any error
set -e

# Function to check GitHub CLI login with retries
check_gh_login() {
    local max_attempts=3
    local attempt=1

    while ! gh auth status &> /dev/null; do
        echo "🔑 You are not authenticated with GitHub CLI."
        echo "Starting GitHub login process (Attempt $attempt of $max_attempts)..."
        
        # Launch interactive login
        gh auth login
        
        if gh auth status &> /dev/null; then
            echo "✅ Successfully authenticated!"
            return 0
        else
            echo "❌ Authentication failed."
            if [ $attempt -lt $max_attempts ]; then
                read -p "Do you want to try logging in again? [Y/n]: " answer
                answer=${answer:-Y}
                if [[ ! $answer =~ ^[Yy]$ ]]; then
                    echo "Exiting script due to failed authentication."
                    exit 1
                fi
            else
                echo "Max login attempts reached. Exiting."
                exit 1
            fi
        fi
        
        ((attempt++))
    done
    echo "✅ You are already authenticated with GitHub CLI."
}

# Call login check function before anything else
check_gh_login

# Ask for user input
read -p "📦 Enter the name for your new GitHub repository: " REPO_NAME
read -p "📝 Enter a description for the repository: " REPO_DESCRIPTION
read -p "🔒 Should the repository be public or private? [public/private]: " REPO_VISIBILITY
read -p "🌿 Enter the name for the new branch (e.g., develop): " BRANCH_NAME
read -p "👥 Enter the GitHub username of the collaborator to add (press Enter to skip): " COLLABORATOR_USERNAME

echo "🚀 Starting GitHub repository setup..."

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed. Please install it from https://cli.github.com/"
    exit 1
fi

# Create the repository
echo "📁 Creating repository: $REPO_NAME..."
gh repo create "$REPO_NAME" --description "$REPO_DESCRIPTION" --"$REPO_VISIBILITY" --clone

# Navigate to the repository directory
cd "$REPO_NAME"

# Create a sample README file
echo "📝 Creating README.md..."
cat > README.md << EOF
# $REPO_NAME

$REPO_DESCRIPTION

## Getting Started

This repository was created automatically using a script.
EOF

# Create .gitignore file
echo "📝 Creating .gitignore..."
cat > .gitignore << EOF
# OS files
.DS_Store
Thumbs.db

# Editor directories and files
.idea/
.vscode/
*.sublime-project
*.sublime-workspace

# Logs
logs
*.log
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Dependencies
node_modules/
vendor/
EOF

# Initialize git, add files, and commit
echo "🔄 Initializing git repository..."
git add README.md .gitignore
git commit -m "Initial commit"

# Rename default branch to 'main' if it doesn't exist yet
git branch -M main

# Push to main branch
echo "⬆️ Pushing to main branch..."
git push -u origin main

# Create and switch to new branch
echo "🌿 Creating branch: $BRANCH_NAME..."
git checkout -b "$BRANCH_NAME"

# Create a sample file in the new branch
echo "📝 Creating sample file in $BRANCH_NAME branch..."
cat > sample.txt << EOF
This is a sample file created in the $BRANCH_NAME branch.
EOF

# Commit and push the new branch
git add sample.txt
git commit -m "Add sample file in $BRANCH_NAME branch"
git push -u origin "$BRANCH_NAME"

# Add collaborator if provided
if [ -n "$COLLABORATOR_USERNAME" ]; then
    echo "👥 Adding collaborator: $COLLABORATOR_USERNAME..."
    gh api \
      --method PUT \
      -H "Accept: application/vnd.github+json" \
      /repos/$(gh repo view --json nameWithOwner -q .nameWithOwner)/collaborators/$COLLABORATOR_USERNAME \
      -f permission=push
fi

echo "✅ Repository setup complete!"
echo "📊 Repository URL: $(gh repo view --web)"
