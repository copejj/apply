# Brain Dribbler Application - Testing Guide

This guide outlines how to execute, isolate, and debug automated tests for the job application portal using the Maven command-line interface.

---

## Running All Tests

To execute every test suite across the entire application, run the standard test phase command:

```bash
mvn test
```

To run a complete build while ensuring all tests pass successfully before packaging the final application .jar:

```bash
mvn clean package
```

---

## Running Specific Tests (Isolation)

Instead of waiting for the full test suite to complete, you can use the -Dtest property to cherry-pick exactly what you want to execute.

### Example 1: Run a specific test class
To test only the Mailgun email service integration:
```bash
mvn test -Dtest=MailgunEmailServiceTest
```

### Example 2: Run a specific method inside a test class
To target a single scenario (e.g., verifying just the successful email delivery path) use the # separator:
```bash
mvn test -Dtest=MailgunEmailServiceTest#testSendEmail_Success
```

### Example 3: Run using wildcards
To run all test suites matching a specific naming pattern (e.g., all service-layer tests):
```bash
mvn test -Dtest=*ServiceTest
```

---

## Advanced Testing Flags

| Command | Purpose |
| :--- | :--- |
| mvn test -Dtest=MailgunEmailServiceTest -DfailIfNoTests=false | Prevents the build from failing if a specific pattern isn't matched. |
| mvn clean package -DskipTests | Compiles and packages the application while skipping execution of all tests entirely (use sparingly). |

---

## Troubleshooting Mailgun Integration Tests

If MailgunEmailServiceTest fails, check the following variables:
1. Secret Properties: Ensure src/main/resources/secret.properties contains your live mailgun.api.key and matching sandbox or custom domain.
2. Authorized Recipients: If using a Mailgun Sandbox account (sandbox*.mailgun.org), you must manually authorize your personal test destination address in your Mailgun Dashboard before running the test, or the API will reject it with a 400 Bad Request.
