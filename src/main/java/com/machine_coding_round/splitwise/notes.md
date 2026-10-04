# Design Splitwise — Complete LLD Notes

**Difficulty:** Medium  
**Use this with:** `mylearning/` (full design) and `forInterview/` (45-min version)

```powershell
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.mylearning.SplitwiseDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.forInterview.InterviewDemo"
```

---

## Contents

1. [What is Splitwise?](#1-what-is-splitwise)
2. [Clarifying Requirements](#2-clarifying-requirements)
3. [Identifying Core Entities](#3-identifying-core-entities)
4. [Class Design and Relationships](#4-class-design-and-relationships)
5. [Key Design Patterns](#5-key-design-patterns)
6. [Balance Tracking (deep dive)](#6-balance-tracking-deep-dive)
7. [addExpense and settleUp flow](#7-addexpense-and-settleup-flow)
8. [Code structure in this repo](#8-code-structure-in-this-repo)
9. [45-minute interview playbook](#9-45-minute-interview-playbook)
10. [Run and test scenarios](#10-run-and-test-scenarios)
11. [Concurrency](#11-concurrency)
12. [Extensions](#12-extensions)
13. [Interview follow-up Q&A](#13-interview-follow-up-qa)
14. [Common mistakes](#14-common-mistakes)
15. [Check yourself](#15-check-yourself)

---

## 1. What is Splitwise?

Expense-sharing system for roommates, friends, coworkers, or travelers.

**Core idea:** people log who paid for what. The system keeps a running record of **who owes whom**. Real money is transferred only at settle-up time — not after every bill.

Example: four friends on a trip. One pays hotel, another fuel, another dinner. Instead of settling ten times, they record expenses and settle the **net** at the end.

This is a **ledger of obligations**, not a payment gateway. It does not move money; it tracks debt.

### Vocabulary

| Term | Meaning |
| --- | --- |
| User | Person with id, name, email |
| Group | Named set of users (trip, roommates) |
| Expense | One spend event: amount, payer, splits |
| Payer | Who paid the merchant |
| Participant | Who shares the cost (may differ from payer) |
| Split | One participant’s share |
| Balance | Net debt between two users |
| Settlement | Real transfer that reduces a balance |

**Critical insight:** payer and participants are different relationships. Amit can pay for a dinner he did not eat.

---

## 2. Clarifying Requirements

Ask these before coding. Attach your default assumption so you are never blocked.

| Ask | Default for this design |
| --- | --- |
| One-to-one and group expenses? | Yes, both (`groupId` nullable) |
| Split types? | Equal, Exact, Percentage |
| Pairwise balances or only net per user? | **Pairwise** (required for settle) |
| Partial settlements? | Yes |
| Rounding on equal split? | Last person absorbs remainder |
| Notifications? | Learning: yes. Interview: skip |
| Thread-safe? | Learning: yes. Interview: mention only |
| Keep expense history? | Yes, immutable expenses |
| Multi-currency? | No for v1 |
| UI / REST / DB? | No — hardcode demo sequence |

### Functional requirements

1. Add users (id, name, email, phone)
2. Create groups and add members
3. Add expense with EQUAL / EXACT / PERCENTAGE split
4. Track pairwise net balances
5. Partial and full settle up
6. Notify on expense/settlement (learning track)
7. Rounding so shares always equal the total

### Non-functional

- Clear OOP separation
- Extensible for new split types
- Components testable in isolation
- Concurrent updates safe (learning)

### Out of scope (especially interview)

Auth, REST, database, debt simplification, multi-currency, recurring expenses.

### Worked numeric example

| Step | Action | Balances |
| --- | --- | --- |
| 1 | Alice pays 300, equal among Alice/Bob/Charlie | Bob→Alice 100, Charlie→Alice 100 |
| 2 | Bob pays 500 exact: A100 B200 C200 | Alice→Bob nets to 0 vs prior; Charlie→Bob 200 |
| 3 | Charlie pays 400, 50%/25%/25% | Alice→Charlie and Bob↔Charlie update by netting |
| 4 | Alice settles 50 to Charlie | Alice→Charlie decreases by 50 |

---

## 3. Identifying Core Entities

Four kinds of types:

| Kind | Examples | Role |
| --- | --- | --- |
| Enum | `SplitType` | Fixed set of split modes |
| Data classes | `User`, `Split` hierarchy, `Expense`, `Group` | Hold state, little logic |
| Interfaces | `SplitStrategy`, `ExpenseObserver` | Swappable behavior |
| Core classes | `BalanceSheet`, `SplitwiseService` | Real business logic |

```text
SplitwiseService
  ├── BalanceSheet          (pairwise debts)
  ├── Map<SplitType, SplitStrategy>
  ├── List<ExpenseObserver> (learning only)
  ├── users / groups / expenses
  └── Expense ──has──> List<Split>
                         ├── EqualSplit
                         ├── ExactSplit
                         └── PercentageSplit
```

**Nouns → classes:** user, group, expense, split, balance, settlement  
**Verbs → methods:** addExpense, validate, calculateSplits, updateBalance, settleUp, notify

---

## 4. Class Design and Relationships

### SplitType

```java
public enum SplitType { EQUAL, EXACT, PERCENTAGE }
```

Used on `Expense` and as the key to look up the right strategy.

### Custom exceptions

All extend `RuntimeException` (unchecked):

- `InvalidSplitException` — amounts/percentages wrong
- `UserNotFoundException`
- `GroupNotFoundException`

```java
public class InvalidSplitException extends RuntimeException {
    public InvalidSplitException(String message) {
        super(message); // stored in parent; retrieved via getMessage()
    }
}
```

### User

Immutable data class: `id`, `name`, `email`, `phone`.  
No pay/settle methods on User — that logic lives in the service.

### Split hierarchy (inheritance)

```text
Split
  - userId (final)
  - amount (mutable — strategies set it)
  ├── EqualSplit(userId)
  ├── ExactSplit(userId, amount)
  └── PercentageSplit(userId, percentage)
```

**Why inheritance?** Different split types carry different data. One class with nullable `percentage` and `amount` allows invalid states. Subclasses make invalid states hard to create.

**Why is `amount` mutable?** For EQUAL and PERCENTAGE the amount is unknown at construction; the strategy computes it later.

### Expense

Immutable record after creation:

- id, amount, description, paidByUserId  
- splitType, List\<Split\>, groupId (nullable), createdAt  

**Composition:** Expense owns its splits. Splits alone have no meaning without the expense.

### Group

- id, name, memberIds, expenseIds  
- Stores **IDs**, not full `User`/`Expense` objects → no circular deps; service maps are source of truth  
- Does **not** own balances (BalanceSheet does)

### SplitStrategy

```java
public interface SplitStrategy {
    void validate(List<Split> splits, double totalAmount);
    void calculateSplits(List<Split> splits, double totalAmount);
}
```

| Strategy | validate | calculate |
| --- | --- | --- |
| Equal | non-empty list | equal shares; **last** gets remainder |
| Exact | amounts sum to total (±0.01) | no-op (already set) |
| Percentage | percentages sum to 100 | amount = total × % / 100; last gets remainder |

Rounding rule used in code: floor to 2 decimals for first n−1 people; last person gets `total − allocated` so the sum is exact.

### ExpenseObserver (learning)

```java
void onExpenseAdded(Expense expense);
void onSettlement(String from, String to, double amount);
```

`EmailNotificationObserver` prints notifications. Service does not hardcode channels.

### BalanceSheet

```text
Map<String, Map<String, Double>> balances
// balances[A][B] > 0  =>  A owes B
```

Methods: `updateBalance`, `settleUp`, `getBalance`, `getBalancesForUser`.

### SplitwiseService

Facade / orchestrator:

- Learning: Singleton + synchronized + observers  
- Interview: plain constructor, no observers, no locks  

Methods: `addUser`, `createGroup`, `addMemberToGroup`, `addExpense`, `settleUp`, `getBalance`, `getBalancesForUser`.

### Relationships summary

| Relation | Pair |
| --- | --- |
| composition | Expense → Split |
| inheritance | Equal/Exact/Percentage → Split |
| composition | Service → BalanceSheet |
| uses | Service → SplitStrategy |
| uses | Service → ExpenseObserver |
| references by id | Group/Expense → User |

---

## 5. Key Design Patterns

### Strategy — justified

**Problem:** three (and later more) ways to split money.  
**Why not a switch?** Validation and remainder logic pile into case arms; every new type edits the service.  
**Flexibility:** new split type = new class implementing `SplitStrategy`.  
**Cost:** a few extra files + a map lookup.

### Observer — learning only

**Problem:** notify users on expense/settlement without coupling service to email/SMS.  
**Skip in interview** unless asked — no second consumer yet is speculative.

### Singleton — learning / demo only

**Problem:** one shared in-memory service for the demo.  
**Interview preference:** `new SplitwiseService()` — easier to test, no global state.

### Facade

External code only talks to `SplitwiseService`. It never touches strategies or BalanceSheet directly.

---

## 6. Balance Tracking (deep dive)

### Why pairwise, not net-per-user?

Net: “Alice is owed 900” — cannot settle, because you don’t know **who** should pay.  
Pairwise: “Bob owes Alice 400” — settlement is possible.  
Net can be derived by summing a row; pairwise cannot be derived from net.

### Mirror updates

When recording Alice owes Bob 50:

```text
balances[Alice][Bob] += 50
balances[Bob][Alice] -= 50
```

Netting is automatic. Bob owed Alice 100, then Alice owes Bob 60 → Bob owes Alice 40.

### Self-debt

Skip the payer’s own share when updating balances. A user never owes themselves.

### Settlement

```text
settleUp(from, to, amount)  =>  updateBalance(from, to, -amount)
```

Reject: self-settle, amount ≤ 0, amount > owed.

---

## 7. addExpense and settleUp flow

```text
addExpense:
  1. require payer + all participants exist
  2. if groupId != null → membership checks
  3. strategy.validate(splits, amount)
  4. strategy.calculateSplits(splits, amount)
  5. create Expense, store it
  6. for each split where userId != payer:
        balanceSheet.updateBalance(participant, payer, share)
  7. notify observers (learning)

settleUp:
  1. require both users
  2. check amount > 0 and amount <= owed
  3. balanceSheet.settleUp(from, to, amount)
  4. notify observers (learning)
```

---

## 8. Code structure in this repo

```text
splitwise/
  notes.md                          ← this file (only markdown)
  mylearning/                       ← full design
    model/     User, Split*, Expense, Group, SplitType
    exception/ InvalidSplit, UserNotFound, GroupNotFound
    split/     SplitStrategy + 3 implementations
    observer/  ExpenseObserver, EmailNotificationObserver
    service/   BalanceSheet, SplitwiseService
    SplitwiseDemo.java
  forInterview/                     ← 45-min design
    model/ exception/ split/ service/
    InterviewDemo.java
```

| Feature | mylearning | forInterview |
| --- | --- | --- |
| 3 split strategies | Yes | Yes |
| Pairwise BalanceSheet | Yes | Yes |
| Groups | Yes | Light |
| Observer | Yes | No |
| Singleton | Yes | No |
| synchronized | Yes | No |

---

## 9. 45-minute interview playbook

| Minutes | Do |
| --- | --- |
| 0–5 | Clarify: 3 splits, pairwise balances, partial settle, single currency. Scope out REST/DB/Observer |
| 5–10 | List classes + one-line responsibility each |
| 10–15 | Justify Strategy only. Say you skip Observer/Singleton |
| 15–40 | Implement: Split → strategies → BalanceSheet → Service → demo that prints balances |
| 40–50 | Remainder case, exact/percent validation, settle edge cases |
| 50–60 | Trade-offs + “not thread-safe; I’d synchronize BalanceSheet” |

**Must finish:** add expense, pairwise netting, settle, runnable demo.  
**Cut if short on time:** percentage/exact (keep interface), groups, history.  
**Never cut:** equal split + balances + something that runs.

### Sentences to say out loud

- “Payer and participants are separate relationships.”
- “Pairwise balances, not net-only — otherwise settle can’t work.”
- “Last participant absorbs rounding so shares sum to the total.”
- “New split type is one new Strategy class; service stays closed.”

---

## 10. Run and test scenarios

1. Equal 300 / 3 → Bob & Charlie each owe Alice 100  
2. Exact amounts must sum to total — else `InvalidSplitException`  
3. Percentages must sum to 100 — else exception  
4. Opposite debts net off  
5. Settle reduces balance; cannot settle more than owed  
6. Payer-only participant → no balance rows created  

---

## 11. Concurrency

Without locks, two threads updating the same pair can lose an update (classic read-modify-write race on mirrored balances).

### What can race

| Scenario | Risk |
| --- | --- |
| Two `addExpense` on Alice↔Bob | Lost update on balance |
| `addExpense` + `settleUp` same pair | Half-applied mirror (A→B updated, B→A not) |
| `getBalance` during update | Dirty / inconsistent read |

### Learning implementation (`mylearning/`)

- `BalanceSheet` uses **`ReentrantReadWriteLock`** — many concurrent reads, exclusive writes  
- `SplitwiseService` mutating APIs are **`synchronized`** (users/groups/expenses maps)  
- Demo: `ConcurrencyDemo` — N threads add equal expenses; final Bob→Alice must equal expected  

```powershell
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.splitwise.mylearning.ConcurrencyDemo"
```

### Scale-up answers (say in interview)

1. **Ordered pair locks** — lock `(min(id1,id2), max(...))` so unrelated pairs run in parallel; prevents deadlock  
2. **Append-only expense log** — single writer folds balances; self-healing  

Interview track: no locks — state the race and the sync plan.

---

## 12. Extensions

Mention only when asked:

| Extension | Idea |
| --- | --- |
| Debt simplification | Net per user → two heaps → greedy match → ≤ n−1 transfers. Query only, not stored |
| Multi-currency | Money + currency; balances per pair per currency; FX policy |
| Recurring expenses | Scheduler creates expenses on a schedule; same addExpense path |
| Persistence | Append-only expense/settlement tables; derive balances |
| REST | Factory at controller maps `splitType` string → strategy |

---

## 13. Interview follow-up Q&A

**Why Strategy?** Requirements already name three split variants; fourth is likely. New type = new file.

**Why not switch?** OK for three forever-fixed types; validation + remainder bloat the service.

**Why pairwise?** Settlement needs a counterparty.

**Why last person remainder?** Guarantees sum equals total.

**Why mutable amount on Split?** Equal/Percentage compute amount after construction.

**Why IDs in Group?** Avoid circular references; service maps own the objects.

**Why no Observer in interview?** Speculative without a real second consumer.

**Thread-safe?** Synchronize writes or derive balances from an append-only log.

**Persist?** Expense log as truth; balances as cache/projection.

**Fails halfway?** Expense+shares one transaction; apply balance delta after commit.

---

## 14. Common mistakes

1. Using `double` carelessly without remainder handling  
2. Net-only balance map → cannot settle  
3. God service with a giant switch on split type  
4. Building Observer / debt-simplify before core balances work  
5. 40 minutes designing, 10 minutes coding  
6. Silent assumptions instead of stated ones  
7. Unjustified Singleton  

---

## 15. Check yourself

1. Can the payer be outside the participant list?  
2. Why is pairwise primary and net derived?  
3. What are the shares for 100.00 split equally 3 ways under our rule?  
4. Exact amounts don’t sum — what happens?  
5. How do you add “split by shares” without editing existing strategy classes?  
6. What do you cut first with 20 minutes left and nothing running?

If you can answer these without opening the code, you understand the design — not just the class names.
