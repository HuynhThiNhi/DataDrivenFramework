#!/bin/bash

# Script to run tests with ReportNG
echo "Starting Data Driven Framework tests with ReportNG..."

# Clean previous reports
echo "Cleaning previous reports..."
rm -rf target/screenshots
rm -rf target/reportng-reports
rm -rf target/surefire-reports

# Create directories
mkdir -p target/reportng-reports/screenshots

# Run tests with ReportNG
echo "Running tests with ReportNG..."
mvn clean test -Dorg.uncommons.reportng.escape-output=false -Dorg.uncommons.reportng.title="Data Driven Framework Test Report"

# Check if tests completed successfully
if [ $? -eq 0 ]; then
    echo "Tests completed successfully!"
    echo "ReportNG reports generated in: target/reportng-reports/"
    echo "Screenshots saved in: target/reportng-reports/screenshots/"
    
    # Open the report in browser (if on macOS)
    if [[ "$OSTYPE" == "darwin"* ]]; then
        echo "Opening ReportNG report in browser..."
        open target/reportng-reports/html/index.html
    fi
else
    echo "Tests failed! Check the logs for details."
    exit 1
fi