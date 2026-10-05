# Machine Coding / LLD Practice

Java 17. Each problem has **one notes file**, a full `mylearning/` design, and a lean `forInterview/` version.

Local-only study notes (gitignored, not pushed to GitHub):

- `fundamentals/oops-notes.md`
- `fundamentals/design-patterns-notes.md`
- `fundamentals/concurrency-notes.md`
- `fundamentals/rate-limiting-notes.md`

## Problems

| Problem | Notes | Learning | Interview |
| --- | --- | --- | --- |
| Splitwise | [notes.md](src/main/java/com/machine_coding_round/splitwise/notes.md) | `splitwise/mylearning/` | `splitwise/forInterview/` |
| Snake & Ladder | [notes.md](src/main/java/com/machine_coding_round/snakeladder/notes.md) | `snakeladder/mylearning/` | `snakeladder/forInterview/` |
| File System | [notes.md](src/main/java/com/machine_coding_round/filesystem/notes.md) | `filesystem/mylearning/` | `filesystem/forInterview/` |

## Run demos

```powershell
# Splitwise
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.mylearning.SplitwiseDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.forInterview.InterviewDemo"

# Snake & Ladder
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.snakeladder.mylearning.SnakeLadderDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.snakeladder.forInterview.InterviewDemo"

# In-Memory File System
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.filesystem.mylearning.FileSystemDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.filesystem.forInterview.InterviewDemo"

# Concurrency demos (mylearning)
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.mylearning.ConcurrencyDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.filesystem.mylearning.ConcurrencyDemo"
```

