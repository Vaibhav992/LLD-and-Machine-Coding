# Design Shopping Cart — LLD Notes

**Code:** `mylearning/` (full) · `forInterview/` (45-min)

```powershell
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.cart.mylearning.CartDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.cart.mylearning.ConcurrencyDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.cart.forInterview.InterviewDemo"
.\mvnw -q test "-Dtest=CartTest,InterviewCartTest"
```

---

## 1. What it is

Customer products collect karta hai, quantity badalta hai, ek discount lagata hai, phir checkout. Checkout ke baad cart order ka record hai — add/remove band.

---

## 2. Requirements to lock

| Ask | Default here |
| --- | --- |
| Same product dobara add? | Quantity merge, price pehli baar ki snapshot |
| Max quantity? | Product pe `maxQuantityPerCart` |
| Kitne discounts ek saath? | Ek strategy. Nayi strategy purani ko replace karti hai |
| Discount types? | Percent, flat (cap at subtotal), buy X get Y |
| Checkout ke baad edit? | Nahi |
| Empty checkout? | Nahi |
| Payment / inventory? | Out of scope |
| Concurrent adds? | Haan — cart ka apna lock |

---

## 3. Entities

| Type | Role |
| --- | --- |
| `Product` | Catalog line: id, price, category, max qty |
| `CartItem` | Product + qty + `priceAtAddition` |
| `Customer` | Cart owner |
| `Cart` | Items, one discount, status |
| `DiscountStrategy` | Percent / flat / buy-X-get-Y |
| `CartObserver` | Learning only: add, remove, checkout |
| `CartService` | `cartId → Cart`. Alag carts alag locks |

```text
Cart
 ├── items: productId → CartItem
 ├── discount: DiscountStrategy (or none)
 └── status: ACTIVE → CHECKED_OUT | ABANDONED
```

---

## 4. Money rules

```text
subtotal = sum(qty * priceAtAddition)
discount = strategy.calculateDiscount(items)   // 0 if none
total    = max(0, subtotal - discount)
```

- **10%** of 300 = 30, total 270
- **Flat 1000** on subtotal 300 = discount 300, total 0 (negative total nahi)
- **Buy 2 get 1:** qty 3 → 1 unit free. Qty 6 → 2 units free

Price cart mein copy hoti hai. Catalog price baad mein badle to purani line nahi badalti.

---

## 5. Concurrency

Shared mutable state = item map + quantity + discount + status.

| Scenario | Without lock | With `synchronized` on the cart |
| --- | --- | --- |
| Two threads add the same product | Both read qty 1, both write 2. One add lost | Second sees qty 2, writes 3 |
| 25 threads add 1, max is 10 | Both pass the check, qty becomes 11 | Exactly 10 succeed, 15 throw |
| `addItem` during `checkout` | Checkout sees a half-updated line | One finishes fully, then the other runs |
| Two customers | Unrelated | Different cart objects, so they do not block each other |

`CartService` ka map `ConcurrentHashMap` hai. Business rule (qty + max + status) cart ke lock ke andar hai. Map thread-safe hone se quantity check safe nahi ho jata.

Observer cart ke lock ke andar chalta hai. Woh dubara `addItem` na call kare.

---

## 6. Interview vs learning

| | 45 min | Learning |
| --- | --- | --- |
| Add, total, checkout | Yes | Yes |
| Percent + flat | Yes | Yes |
| Buy X get Y, observer, customer, categories, abandon | Skip | Yes |
| `synchronized` on cart | Yes, one line | Yes + `ConcurrencyDemo` |

Interview line:

> "I'd synchronize the cart. The check-then-add of quantity is one critical section. A second cart does not need the same lock."
