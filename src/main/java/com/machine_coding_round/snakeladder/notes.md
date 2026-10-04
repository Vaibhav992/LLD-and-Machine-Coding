# Design Snake and Ladder — Complete LLD Notes

**Difficulty:** Beginner → Intermediate  
**Code:** `mylearning/` (full) · `forInterview/` (45-min)

```powershell
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.snakeladder.mylearning.SnakeLadderDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.snakeladder.forInterview.InterviewDemo"
```

---

## Contents

1. [What is Snake and Ladder?](#1-what-is-snake-and-ladder)
2. [Clarifying Requirements](#2-clarifying-requirements)
3. [Core Entities](#3-core-entities)
4. [Class Design](#4-class-design)
5. [Design Patterns](#5-design-patterns)
6. [Game Flow](#6-game-flow)
7. [Code in this repo](#7-code-in-this-repo)
8. [45-minute interview playbook](#8-45-minute-interview-playbook)
9. [Edge Cases](#9-edge-cases)
10. [Extensions](#10-extensions)
11. [Interview Q&A](#11-interview-qa)
12. [Common Mistakes](#12-common-mistakes)
13. [Check yourself](#13-check-yourself)

---

## 1. What is Snake and Ladder?

A board game. Players start at 0 (or 1), roll a die, move forward. Landing on a **ladder** start jumps you up; landing on a **snake** head slides you down. First to reach the last cell (usually 100) wins.

**Core loop:** roll → move → apply jump → check win → next player.

This problem tests: modeling a board, turn order, rules (exact win), and where to put Strategy (dice behaviour).

---

## 2. Clarifying Requirements

| Ask | Default in this design |
| --- | --- |
| Board size? | 100 |
| How many players? | ≥ 2 |
| Exact win required? | **Yes** — if roll would pass 100, stay put |
| Can snakes/ladders chain? | Learning: yes (resolve until no jump). Interview: one hop is enough |
| Start position? | 0 (off board); first move enters board |
| Extra turn on rolling 6? | No for v1 (common extension) |
| Crooked / special dice? | Learning has `CrookedDice`; interview: mention only |
| UI? | Console demo |

### Functional

1. Create board with snakes and ladders  
2. Add players  
3. Roll dice and take turns in order  
4. Apply snake/ladder when landing on a jump cell  
5. Exact-win rule  
6. Declare winner  

### Out of scope (interview)

GUI, persistence, multi-board tournaments, undo, AI opponent.

---

## 3. Core Entities

| Entity | Responsibility | Owns |
| --- | --- | --- |
| `Player` | Identity + current position | id/name, position |
| `Snake` | Head → tail (down) | head, tail |
| `Ladder` | Start → end (up) | start, end |
| `Board` | Size + jump map; resolve landing | size, jumps |
| `Dice` | Produce a roll | — |
| `SnakeLadderGame` | Turn order, rules, win check | board, dice, players queue |

**Nouns:** board, snake, ladder, player, dice, cell, turn  
**Verbs:** roll, move, jump, win, nextTurn

---

## 4. Class Design

```text
SnakeLadderGame
  ├── Board
  │     snakes / ladders → Map<Integer,Integer> jumps
  ├── Dice  <<interface>>
  │     ├── NormalDice
  │     └── CrookedDice          (learning)
  └── Queue<Player> turnOrder
```

### Player

```java
class Player {
    String id / name;
    int position; // mutable during game
}
```

Position changes every turn — this is intentional mutable state, owned by the player object (or by the game; either is fine if consistent).

### Snake / Ladder

Validated at construction:

- Snake: `head > tail`
- Ladder: `start < end`
- Neither should start at the winning cell

Interview shortcut: skip separate classes; use `board.addSnake(h,t)` / `addLadder(s,e)` directly into maps.

### Board

```java
int size;
Map<Integer, Integer> jumps; // start -> end

int resolvePosition(int pos); // follow jumps (optionally chained)
```

**Why one jump map?** Snakes and ladders are the same operation: land on A, go to B. Distinguishing them matters for validation (direction) and UX, not for move resolution.

### Dice (Strategy)

```java
interface Dice { int roll(); }
class NormalDice implements Dice { /* 1..6 */ }
class CrookedDice implements Dice { /* always even */ } // extension
```

### SnakeLadderGame

```java
playTurn()           // one player
playUntilWinner(max) // loop for demo
```

Turn order: `Queue<Player>` — poll current, offer back unless they won.

### Relationships

| Relation | Pair |
| --- | --- |
| composition | Game → Board, Dice, Players |
| uses | Game uses Dice.roll() |
| has-a | Board has jump map |
| is-a | NormalDice / CrookedDice → Dice |

No inheritance between Snake and Ladder unless you introduce a shared `Jump` — optional, not required.

---

## 5. Design Patterns

### Strategy — Dice (justified when multiple dice exist)

**Problem:** roll behaviour may vary (normal, crooked, two-dice sum).  
**Interview:** even with one `NormalDice`, an interface is a small cost and a clean answer to “how would you add crooked dice?”  
**If truly one forever:** concrete `Dice` class is fine — say so.

### Not needed

| Pattern | Why skip |
| --- | --- |
| Singleton | Multiple games should be creatable |
| Observer | No second consumer for events in v1 |
| Factory | Board setup is straightforward |
| State pattern for GameStatus | Enum + if checks are enough |

---

## 6. Game Flow

```text
start game
while no winner:
  current = next player
  roll = dice.roll()
  next = position + roll
  if next > boardSize and exactWin:
      stay; enqueue player; continue
  next = board.resolve(next)   // snake/ladder
  player.position = next
  if next == boardSize:
      winner = player; stop
  else:
      enqueue player again
```

### Exact win example

Player at 97, rolls 5 → 102 > 100 → stay at 97.  
Player at 97, rolls 3 → 100 → win.

---

## 7. Code in this repo

```text
snakeladder/
  notes.md
  mylearning/
    model/     Player, Snake, Ladder, GameStatus
    board/     Board (chained resolve + validation)
    dice/      Dice, NormalDice, CrookedDice
    service/   SnakeLadderGame
    SnakeLadderDemo.java
  forInterview/
    model/Player
    board/Board          (addSnake / addLadder maps)
    dice/Dice, NormalDice
    service/SnakeLadderGame
    InterviewDemo.java
```

| Feature | mylearning | forInterview |
| --- | --- | --- |
| Snake/Ladder classes | Yes | Maps only |
| Chained jumps | Yes | One hop |
| GameStatus enum | Yes | Implicit via winner |
| CrookedDice | Yes | No |
| Exact win | Yes | Yes |
| Seeded dice for demo | Yes | Yes |

---

## 8. 45-minute interview playbook

| Min | Do |
| --- | --- |
| 0–5 | Clarify: board 100, exact win, ≥2 players, snakes/ladders as maps |
| 5–10 | Entities: Player, Board, Dice, Game |
| 10–12 | Dice interface (one sentence) |
| 12–40 | Code Board → Dice → Player → Game.playTurn → demo |
| 40–50 | Edge: overshoot, snake/ladder, win |
| 50–60 | Extensions: roll-6 extra turn, crooked dice, chaining |

**Implement first:** `playTurn` happy path.  
**Skip if short:** CrookedDice, GameStatus, Snake/Ladder classes, chaining.

### Sentences to say

- “Board owns jumps; game owns turn rules.”
- “Exact win: overshoot means stay — common house rule I’ll assume unless you say otherwise.”
- “Dice behind an interface so crooked/two-dice is a new class.”

---

## 9. Edge Cases

| Case | Handling |
| --- | --- |
| Roll past last cell | Stay (exact win) |
| Land on snake head | Go to tail |
| Land on ladder start | Go to end |
| Snake/ladder on same cell | Reject at board build |
| Snake/ladder at cell 100 | Reject |
| Ladder start ≥ end / snake head ≤ tail | Reject |
| < 2 players | Reject |
| Chained jumps (ladder to snake) | Learning resolves until stable |
| Infinite jump cycle | Learning throws after guard |

---

## 10. Concurrency

Snake & Ladder is **turn-based**. The shared state is the turn queue + player positions + status/winner.

### What can race

| Scenario | Risk |
| --- | --- |
| Two threads call `playTurn()` | Both `poll` same player / corrupt queue |
| Read `getWinner()` while turn updates | Stale / null winner |

### Learning implementation (`mylearning/`)

- `start`, `playTurn`, `getStatus`, `getWinner`, `getPlayers` are **`synchronized`**  
- Coarse lock on the game object is the **correct** model here (turns are sequential by design)  
- Board jump map is immutable after construction → no lock needed on Board  

### Interview talk

> “I’d synchronize `playTurn`. Finer locks don’t help — there is only one turn at a time.”

---

## 11. Extensions

| Extension | Approach |
| --- | --- |
| Extra turn on 6 | After move, if roll==6 && not win, don’t rotate queue |
| Two dice | `SumDice` wraps two `Dice` |
| Crooked dice | `CrookedDice` implementing `Dice` |
| First to finish among N | Already supported via queue |
| Undo | Command pattern / move history stack |
| Configurable board size | `Board(size, ...)` already |

---

## 12. Interview Q&A

**Where do snakes/ladders live?** On `Board` as a jump map. Game should not hardcode cell numbers.

**Why not put move logic on Player?** Player doesn’t know board rules or other players. Game orchestrates; Player only holds position.

**Why Dice interface?** Roll behaviour varies (normal/crooked/loaded). Open/Closed for that axis.

**Exact win vs wrap/bounce?** Exact stay is the usual interview default. State your assumption.

**How would you test?** Seed the `Random`, or inject a `Dice` stub that returns a fixed sequence.

**Thread safety?** Synchronize `playTurn` — turn queue is shared. Coarse game lock is enough (see §10).

**Snake vs Ladder as subclasses of Jump?** Fine if you want shared validation; not required. Direction rules differ, so separate types or factory methods both work.

---

## 13. Common Mistakes

1. Putting snakes/ladders in the Game class → Board becomes anemic  
2. Forgetting exact-win → player teleports past 100 and “wins” wrongly  
3. Using `Player[]` with index arithmetic instead of a queue for turns  
4. No validation that snake head > tail  
5. Hardcoding `new Random().nextInt(6)+1` inside Game — untestable  
6. Building Observer/Factory for a console demo  

---

## 14. Check yourself

1. What happens at position 98 with roll 5 under exact win?  
2. Who owns the jump map — Game or Board?  
3. Why is Dice an interface?  
4. How do you model turn order for 4 players?  
5. Ladder 14→7 — valid or invalid? Why?  
6. What do you cut first if time is short?

If you can answer these, you can redesign Snake and Ladder from scratch in an interview.
