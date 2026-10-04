# Machine Coding / LLD Practice

Java 17. Each problem has **one notes file**, a full `mylearning/` design, and a lean `forInterview/` version.

## Problems

| Problem | Notes | Learning | Interview |
| --- | --- | --- | --- |
| Splitwise | [notes.md](src/main/java/com/machine_coding_round/splitwise/notes.md) | `splitwise/mylearning/` | `splitwise/forInterview/` |
| Snake & Ladder | [notes.md](src/main/java/com/machine_coding_round/snakeladder/notes.md) | `snakeladder/mylearning/` | `snakeladder/forInterview/` |

## Run demos

```powershell
# Splitwise
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.mylearning.SplitwiseDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.forInterview.InterviewDemo"

# Snake & Ladder
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.snakeladder.mylearning.SnakeLadderDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.snakeladder.forInterview.InterviewDemo"
```
