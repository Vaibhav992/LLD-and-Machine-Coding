# Design In-Memory File System — Complete LLD Notes

**Difficulty:** Intermediate  
**Code:** `mylearning/` (full) · `forInterview/` (45-min)

```powershell
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.filesystem.mylearning.FileSystemDemo"
.\mvnw -q "exec:java" "-Dexec.mainClass=com.machine_coding_round.filesystem.forInterview.InterviewDemo"
```

---

## Contents

1. [What is an in-memory file system?](#1-what-is-an-in-memory-file-system)
2. [Clarifying Requirements](#2-clarifying-requirements)
3. [Core Entities](#3-core-entities)
4. [Class Design](#4-class-design)
5. [Design Patterns](#5-design-patterns)
6. [Path Resolution](#6-path-resolution)
7. [Operations](#7-operations)
8. [Code in this repo](#8-code-in-this-repo)
9. [45-minute interview playbook](#9-45-minute-interview-playbook)
10. [Edge Cases](#10-edge-cases)
11. [Extensions](#11-extensions)
12. [Interview Q&A](#12-interview-qa)
13. [Common Mistakes](#13-common-mistakes)
14. [Check yourself](#14-check-yourself)

---

## 1. What is an in-memory file system?

A tree of directories and files that lives in RAM — no disk, no OS. You support shell-like operations:

```text
mkdir /home/alice
createFile /home/alice/notes.txt
write /home/alice/notes.txt "hello"
read  /home/alice/notes.txt
ls /home/alice
rm /home/alice/notes.txt
```

**Why interviewers like it:** tree modeling, Composite pattern with a real reason, path parsing, and clear ownership (Directory owns children).

---

## 2. Clarifying Requirements

| Ask | Default here |
| --- | --- |
| Absolute paths only, or relative + `cd`? | Interview: absolute. Learning: both + `pwd`/`cd` |
| `mkdir -p` (create parents)? | Learning: `mkdirp`. Interview: parents must exist |
| Can you `rm` non-empty directory? | No (must be empty) |
| File content type? | String in memory |
| Soft links / permissions / sizes on disk? | Out of scope |
| Concurrent access? | Mention; skip for interview |

### Functional (must have)

1. `mkdir(path)`  
2. `createFile(path)`  
3. `write(path, content)` / `read(path)`  
4. `ls(path)`  
5. `rm(path)` — file or empty directory  

### Learning extras

- `mkdirp`, `appendFile`, `cd`, `pwd`, `.` / `..`, timestamps on nodes

### Out of scope

Real disk I/O, permissions, symlinks, concurrent locks, watchers.

---

## 3. Core Entities

| Entity | Role |
| --- | --- |
| `Node` | Abstract base: name (+ optional timestamps/parent) |
| `FileNode` | Leaf: holds content |
| `Directory` | Composite: map of child name → Node |
| `FileSystem` | Facade: path parse + operations on the tree |

```text
/  (root Directory)
├── home/          (Directory)
│   └── alice/     (Directory)
│       ├── docs/  (Directory)
│       └── notes.txt  (FileNode)
```

---

## 4. Class Design

```text
FileSystem
  └── root: Directory
        children: Map<String, Node>
                    ├── Directory (composite)
                    └── FileNode  (leaf)

Node <<abstract>>
  ├── FileNode   content, write/read
  └── Directory  children map, add/remove/list
```

### Node

Shared: `name`, optionally `parent`, `createdAt`, `modifiedAt`.  
`isDirectory()` distinguishes type without `instanceof` everywhere (still needed for casts after checks).

### FileNode

```java
write(String) / append(String) / read() / size()
```

### Directory

```java
Map<String, Node> children   // LinkedHashMap keeps insertion order for ls
add(Node) / remove(name) / getChild(name) / listNames()
```

**Why Map not List?** O(1) lookup by name; names must be unique in a directory.

### FileSystem

Owns root (and `cwd` in learning). All public APIs take paths as strings.

### Relationships

| Relation | Pair | Why |
| --- | --- | --- |
| inheritance | File/Directory → Node | Both are tree entries |
| composition | Directory → children Nodes | Directory owns subtree |
| facade | FileSystem → root | Callers never touch Node APIs for paths |

---

## 5. Design Patterns

### Composite — justified

Directories and files are treated uniformly as `Node` in the tree; directories contain nodes. This is the textbook case where Composite is not decorative.

### Facade — FileSystem

Path string in, behavior out. Hides tree walking.

### Not needed

| Pattern | Why skip |
| --- | --- |
| Singleton | Multiple FS instances are fine (tests) |
| Visitor | No multi-operation tree walk required in v1 |
| Flyweight | No shared identical file content requirement |

---

## 6. Path Resolution

Absolute path: `/home/alice/notes.txt`

```text
1. Start at root
2. Split into segments: home, alice, notes.txt
3. Walk: root → home → alice → notes.txt
4. Fail if any segment missing or a file is used as directory
```

Interview: absolute only.  
Learning: relative paths from `cwd`, support `.` and `..`.

**Create/remove** need parent + leaf name:

```text
/home/alice/notes.txt
  parent = /home/alice
  name   = notes.txt
```

---

## 7. Operations

| Op | Behavior |
| --- | --- |
| mkdir | Create directory under existing parent |
| mkdirp | Create every missing segment |
| createFile | Create empty file under existing parent |
| write / append | Replace or append file content |
| read | Return file content |
| ls | List child names (or single file name) |
| rm | Delete file or **empty** directory |
| cd / pwd | Change / print working directory (learning) |

---

## 8. Code in this repo

```text
filesystem/
  notes.md
  mylearning/
    model/      Node, FileNode, Directory
    exception/  PathNotFound, InvalidPath, AlreadyExists
    service/    FileSystem  (cd, pwd, mkdirp, append, ..)
    FileSystemDemo.java
  forInterview/
    model/      Node, FileNode, Directory
    exception/  FsException
    service/    FileSystem  (absolute paths, core ops)
    InterviewDemo.java
```

| Feature | mylearning | forInterview |
| --- | --- | --- |
| Composite Node | Yes | Yes |
| Absolute paths | Yes | Yes |
| Relative + cd/pwd | Yes | No |
| mkdirp | Yes | No |
| append | Yes | No |
| Empty-dir check on rm | Yes | Yes |

---

## 9. 45-minute interview playbook

| Min | Do |
| --- | --- |
| 0–5 | Clarify: absolute paths, mkdir parents must exist, rm empty dirs only, string content |
| 5–10 | Entities: Node, File, Directory, FileSystem |
| 10–12 | Justify Composite in one sentence |
| 12–40 | Code Directory/File → resolve path → mkdir/create/write/read/ls/rm → demo |
| 40–50 | Edge: exists, not a dir, not empty |
| 50–60 | Mention cd, symlinks, concurrency as extensions |

**Implement first:** `mkdir` + `createFile` + `ls` + `write`/`read`.  
**Skip if short:** cd, mkdirp, append, timestamps.

### Sentences to say

- “Directory is a composite Node; File is a leaf.”
- “FileSystem is a facade over path resolution.”
- “Children live in a Map for unique names and fast lookup.”

---

## 10. Edge Cases

| Case | Handling |
| --- | --- |
| mkdir existing name | AlreadyExists / FsException |
| path through a file | Not a directory |
| read directory | Error |
| write directory | Error |
| rm non-empty directory | Error |
| rm root | Error |
| empty / null path | Invalid |
| trailing slash | Normalize |

---

## 11. Concurrency

### What can race

| Scenario | Risk |
| --- | --- |
| Two `mkdir` same path | Both see missing → duplicate / corrupt |
| `writeFile` + `readFile` | Partial content visible |
| Shared `cwd` field across threads | Thread A `cd /a`, B `cd /b`, A `ls .` sees `/b` |

### Learning implementation (`mylearning/`)

- All public FS ops are **`synchronized`** on the FileSystem (coarse tree lock)  
- **`ThreadLocal<Directory>`** for cwd — each thread has its own working directory  
- `clearThreadCwd()` at end of a “request”  
- Demo: `ConcurrencyDemo` — parallel mkdir + append  

```powershell
.\mvnw -q compile "exec:java" "-Dexec.mainClass=com.machine_coding_round.filesystem.mylearning.ConcurrencyDemo"
```

### Scale-up

- Lock per directory (finer) for parallel ops under different folders  
- Prefer absolute paths in servers so cwd is unnecessary  

Interview track: no locks — mention coarse sync + ThreadLocal cwd.

---

## 12. Extensions

| Extension | Approach |
| --- | --- |
| Symlinks | `SymlinkNode` pointing to path; resolve with cycle guard |
| Permissions | User + mode bits checked in FileSystem |
| Move / rename | Re-link child under new parent |
| Search by name | DFS/BFS from root |
| Size of directory | Sum recursive file sizes (Visitor optional) |

---

## 13. Interview Q&A

**Why Composite?** Files and directories share tree membership; directories contain nodes. Uniform `Node` type makes ls/resolve natural.

**Why not one class with `isDir` flag and nullable content/children?** Invalid states: directory with content, file with children. Subclasses prevent that.

**Where does path parsing live?** FileSystem (or a small PathUtil). Nodes should not parse full paths.

**How to test?** Create FS, run ops, assert ls/read; inject nothing — pure in-memory.

**Difference from LeetCode Design File System?** Same idea; interview focus is OOP structure, not only a Map of paths.

**Could you store `Map<String,String>` of fullPath → content?** Works for files only; directories directories and `ls` become painful. Tree is the right model.

---

## 14. Common Mistakes

1. Flat `Map<path, content>` — weak `ls` and directory semantics  
2. No validation that intermediate path is a directory  
3. Allowing duplicate child names  
4. `rm -rf` by default without stating it  
5. Putting path strings on every node instead of parent links / resolve  
6. Over-engineering permissions in 45 minutes  

---

## 15. Check yourself

1. Why Map for children instead of List?  
2. What happens if you `mkdir` when parent is missing (interview design)?  
3. How do you split `/a/b/c.txt` into parent + name?  
4. Why can’t File and Directory be the same class safely?  
5. What does Composite buy you in `ls` and resolve?  
6. What do you cut first when time is short?

If you can answer these, you can rebuild the design without memorizing class names.
