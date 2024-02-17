#!/bin/bash

# Initialize an array to hold module names that contain Android Instrumented Tests
modulesWithTests=()

# Base directory for searching - should be the root of your project
basePath=$(pwd)

# Normalize basePath to ensure no trailing slash
basePath=${basePath%/}

# Find all modules with androidTest implemented in Kotlin
while IFS= read -r moduleDir; do
    # Determine if the androidTest directory contains Kotlin files
    if [ "$(find "$moduleDir" -type f -name "*.kt" | wc -l)" -gt 0 ]; then
        # Extract the module path relative to the project root, ensuring no leading './'
        relativePath="${moduleDir#$basePath/}"
        relativePath="${relativePath#./}" # Remove leading './' if present

        # Convert the file path to a Gradle module path
        # This replaces '/' with ':' and removes the 'src/androidTest/kotlin' part
        moduleName="${relativePath%/src/androidTest/kotlin}" # Ensure to remove the suffix
        moduleName="${moduleName//\//:}"    # Replace all slashes with colons

        # Add the module name to the list
        modulesWithTests+=("$moduleName")
    fi
done < <(find . -type d -path "*/src/androidTest/kotlin" -not -path "*/build/*")

# Check if any modules were found
if [ ${#modulesWithTests[@]} -eq 0 ]; then
    echo "No instrumented tests to run."
else
    # Print all modules that include Android tests and confirm to proceed
    echo "Found instrumented tests in the following modules:"
    for moduleName in "${modulesWithTests[@]}"; do
        echo "- Module: $moduleName"
        echo "  Gradle Task: ./gradlew ${moduleName}:connectedDebugAndroidTest"
    done

    # Run the connectedDebugAndroidTest task for each module
    for moduleName in "${modulesWithTests[@]}"; do
        echo "Running tests for module: $moduleName"
        ./gradlew "${moduleName}:connectedDebugAndroidTest"
    done
fi
