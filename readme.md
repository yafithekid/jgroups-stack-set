# Jgroups Stack and Set

Replicated Stack and Set implementation with JGroups.

### Members

- 13512014 Muhammad Yafi
- 13512066 Calvin Sadewa

### Prerequisites

1. Install Java JDK 1.8
2. Install Gradle, add it to your PATH environment variables.
3. Ensure gradle can works by typing `gradle` in your command prompt.

### Run the jar files

1. Run `gradle setJar stackJar` on root project directory.
2. In `build/libs` folder, run `java -jar set-1.0.jar` for replicated set or `java -jar stack-1.0.jar` for replicated stack.

### Test Scripts

Located at `src/test/java`

Sometimes the test script will fail, because synchronization between members has latency.
For example, when there are two stack A and B, when we push element 'a' at stack A, the stack B
will not immediately have 'a' too. So, testing A.push(a) and then B.top() == 'a' will have non deterministic
result: it will correct when B completes its sync before B.top() == 'a' called, and it will fails otherwise.