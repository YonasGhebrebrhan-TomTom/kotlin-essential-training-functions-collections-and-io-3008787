# Way of working

How I work through the LinkedIn Learning course *Kotlin Essential Training: Functions,
Collections, and I/O* in this fork.

## Why this exists

The course ships **39 videos across 78 snapshot branches** (`CH_MOVIEb` = code at the start
of a video, `CH_MOVIEe` = at the end). Those branches are independent snapshots — `02_02b`
does not descend from `02_01e` — so nothing carries forward by branching off the course.

They also don't build. Every pristine branch ships **Gradle 7.1**, which caps at JDK 16,
on a machine with JDK 23. Checking out a course branch means fixing the build before
writing a line of Kotlin, 39 times over.

So: fix the build once, on `main`, and branch from there.

## Two goals, kept separate

| | Goal | What it needs |
| --- | --- | --- |
| **A** | Learn Kotlin — functions, collections, IO | a build that works, code I write myself, feedback, code that accumulates |
| **B** | Practice a real PR/branching workflow | a handful of genuine reviews — **one PR per chapter, not per video** |

Goal B is deliberately rationed. Seven real reviews teach more than thirty-nine rubber stamps,
and ceremony competes with goal A.

## Branch model

```
COURSE SNAPSHOTS — 78 branches, read-only. Never checked out.
┌───────────────────────────────────────────────────────────────────────────┐
│   02_01b  02_01e     02_02b  02_02e     02_03b  02_03e     02_04b  02_04e │
└──────┬───────┬──────────────────────────────────────────────────┬─────────┘
       │       │                                                  │
       │       └──────────── git diff  (answer key) ───────────────┘
       └──────────────────── git show  (starting code) ──────┐
                                                             ▼
                        02_01      02_02      02_03      02_04
   chapter/02             ●──────────●──────────●──────────●
                         ╱                                  ╲
                        ╱                          PR ───────╲──  review → squash
                       ╱                                      ╲
 main ────●───────────●────────────────────────────────────────●──────────●────►
          │           │                                       │          │
       seed PR      cut from main                        ch02/ lands   chapter/03
  jvmToolchain(21)                                       (permanent)      …
```

| Ref | Lifetime | Job |
| --- | --- | --- |
| `main` | permanent | the working build (the *seed*) plus every chapter finished so far. Accumulates. |
| `chapter/NN` | days | one per chapter, cut from `main`, one commit per video, PR'd back, then deleted |
| `origin/*b` | upstream's | starting code — read with `git show`, never checked out |
| `origin/*e` | upstream's | the instructor's end state — the answer key, via `git diff` |

**Rules**

1. Never check out a course branch. Read files out of it (`git show`, `git diff`) so its broken
   Gradle never lands in the working tree.
2. `chapter/NN` is always cut from `main`, never from a course branch.
3. Only infra/docs PRs and chapter PRs target `main`. A lesson commit on `main` is a mistake.
4. Never force-push a `*b` or `*e` branch — `upstream` remains the source of truth for them.
5. `git fetch upstream` never auto-merges. Inspect, then cherry-pick deliberately.

## Repo structure

```
├── README.md                    upstream's, plus a pointer to this document
├── CLAUDE.md                    rules for Claude Code sessions
├── LICENSE  NOTICE  CONTRIBUTING.md        upstream, untouched
├── .github/workflows/build-and-test.yml    the one CI job
└── project/
    ├── build.gradle.kts         the seed: jvmToolchain(21), Kotlin 1.9.25
    ├── settings.gradle.kts      foojay resolver (downloads the toolchain JDK)
    ├── scores.txt               ch07 input data  (sorted.txt is generated → ignored)
    └── src/
        ├── main/kotlin/
        │   ├── ch02/            types & null safety
        │   │   ├── Booleans.kt          fun main()   ← 02_01
        │   │   ├── NumericTypes.kt      fun main()   ← 02_02
        │   │   ├── Strings.kt           fun main()   ← 02_03
        │   │   └── NullSafety.kt        fun main()   ← 02_04
        │   ├── ch03/            functions, lambdas, function parameters
        │   ├── ch04/            when expressions, is / smart casts
        │   ├── ch05/            loops, ranges
        │   ├── ch06/            collections — map / sortedBy / take
        │   ├── ch07/            file IO — read, write, readLine
        │   └── ch08/            ViewModel.kt, AnalyticsClient.kt
        └── test/kotlin/
            └── ch08/ViewModelTest.kt
```

One file per video, named after the **concept** rather than the video number, each with its own
`fun main()` in a package matching its directory (`package ch02`). Every file compiles to its own
JVM class (`ch02.NumericTypesKt`), so any number of `main()` functions coexist. Run the one you're
working on from the green arrow in the IntelliJ gutter — every past lesson stays one click away.

## The loops

**Per video**, on the chapter branch:

```
   watch video ──▶ write the code myself ──▶ git diff vs origin/0X_0Ye
                            ▲                            │
                            └──── fix what differs ◀──────┘
                                                         │
                                              commit ("02_03: …")
```

```bash
git show origin/03_01b:project/src/main/kotlin/Main.kt     # starting code, when needed
#   ... write it ...
git diff HEAD origin/03_01e -- project/src/                # answer key
git commit -m "03_01: local functions and default arguments"
```

**Per chapter** — this is where goal B lives:

```bash
git switch -c chapter/03 main      # start
# ... videos ...
git diff chapter/03 origin/03_06e -- project/src/          # compare against the chapter's end
/code-review                                                # then self-review
gh pr create --base main --fill
gh pr merge --squash --delete-branch
```

`--base main` matters: `main` is the default branch, so `gh` targets it anyway — but being
explicit is what stops a lesson PR landing on the wrong base.

## How Claude is used

These are the rules that decide whether this works at all:

1. **Claude does not write the exercise code.** Not a scaffold, not "just the boilerplate."
   I type it. This is the entire point, and it's the easiest rule to erode.
2. **Claude handles the plumbing** — Gradle, JDKs, CI, git. That's where the time was going before.
3. **Claude reviews after I've written it**, against `origin/*e`, and explains *why* the
   instructor's version differs. This is the feedback the video can't give.
4. **Claude explains concepts on demand** while I'm in the file.

## Course map

| Chapter | Videos | Topic | Notes |
| --- | --- | --- | --- |
| 01 | 01_01–01_04 | setup, first program | no Gradle project until `01_04` |
| 02 | 02_01–02_04 | types, variables, null safety | |
| 03 | 03_01–03_06 | functions, lambdas, function parameters | **`03_06` has no `b`** — start from `03_05e` |
| 04 | 04_01–04_08 | `when` expressions, `is` / smart casts | |
| 05 | 05_01–05_03 | loops, ranges | |
| 06 | 06_01–06_07 | collections — `map`, `sortedBy`, `take` | ships data as Kotlin source |
| 07 | 07_01–07_05 | file IO | reads `scores.txt` from the working dir, writes `sorted.txt` |
| 08 | 08_01–08_02 | ViewModel / AnalyticsClient | where the course itself introduces `src/test/` |

39 videos, 7 chapter branches, 7 chapter PRs.

## Known loose ends

- `./gradlew run` has no target since `Main.kt` was removed from the seed. Run from the IDE
  gutter, or make it selectable: `mainClass.set(providers.gradleProperty("lesson")…)` then
  `./gradlew run -Plesson=ch02.BooleansKt`.
- CI installs Java 17 while the toolchain is 21. Harmless — Gradle auto-provisions 21, verified
  green — but tidier to align. Needs `gh auth refresh -h github.com -s workflow` to push.
- `main`'s `.gitignore` is the pristine 4-line upstream version, so `.idea/` and `qodana.yaml`
  show as untracked. The ignore rules for them exist only on `02_02b`.
- Qodana (`qodana.yaml`, `qodana_code_quality.yml`) is untracked and has never run in CI.
  Either commit it properly or delete it.
