# Way of working

How to work through the LinkedIn Learning course *Kotlin Essential Training: Functions,
Collections, and I/O* in a fork: fix the build once, type every line of Kotlin yourself, and
take one real review per chapter instead of a rubber stamp per video.

Nothing here is specific to one learner. Fork the course, follow it as written, and adjust
where your setup differs.

## Why this exists

The course ships **39 videos across 77 snapshot branches** (`CH_MOVIEb` = code at the start of
a video, `CH_MOVIEe` = at the end). 39 × 2 = 78, less `03_06b`, which doesn't exist;
`git ls-remote upstream` reports 78 heads because upstream's own `main` is one of them. Those
branches are independent snapshots — `02_02b` does not descend from `02_01e` — so nothing
carries forward by branching off the course.

They also don't build. Every pristine branch ships **Gradle 7.1**, which caps at JDK 16, so on
a current JDK (23 here) they fail before compiling anything. Checking out a course branch means
fixing the build before writing a line of Kotlin, 39 times over.

So: fix the build once, on `main`, and branch from there.

## Two goals, kept separate

| | Goal | What it needs |
| --- | --- | --- |
| **A** | Learn Kotlin — functions, collections, IO | a build that works, code you write yourself, feedback, code that accumulates |
| **B** | Practice a real PR/branching workflow | a handful of genuine reviews — **one PR per chapter, not per video** |

Ration goal B deliberately. Seven real reviews teach more than thirty-nine rubber stamps, and
ceremony competes with goal A.

## Set up

```bash
# 1. Fork the course repo on GitHub, clone your fork, and keep the course as `upstream`.
git clone https://github.com/<you>/kotlin-essential-training-functions-collections-and-io-3008787.git
cd kotlin-essential-training-functions-collections-and-io-3008787
git remote add upstream \
  https://github.com/LinkedInLearning/kotlin-essential-training-functions-collections-and-io-3008787.git
git fetch upstream

# 2. Make `main` build on a current JDK — the seed. Already done if you forked this repo;
#    do it yourself if you forked the course directly. Replace the `kotlinOptions.jvmTarget` /
#    `sourceCompatibility` / `targetCompatibility` trio in project/build.gradle.kts with a
#    single `kotlin { jvmToolchain(21) }`, and add the foojay resolver to
#    project/settings.gradle.kts so Gradle downloads that JDK when the machine lacks it.
cd project && ./gradlew build

# 3. Start the first chapter that has code of your own.
git switch -c chapter/02 main

# 4. Write the first lesson file yourself — the seed ships no Kotlin. Create
#    project/src/main/kotlin/ch02/Booleans.kt with `package ch02` and a `fun main()`,
#    then run it from the IntelliJ gutter or with:
./gradlew run -Plesson=ch02.BooleansKt
```

The first `./gradlew build` takes a few minutes and downloads a JDK (~200 MB): that's the foojay
resolver fetching Temurin 21 because `jvmToolchain(21)` asked for a JDK the machine doesn't have.
It isn't stuck. Later builds reuse it.

Keep `main` free of lesson code: it carries the seed and, later, each finished chapter. The seed's
`project/src/main/kotlin/` holds only a `.gitkeep` — git won't track an empty directory, and
without it a fresh clone would have no source root to put `ch02/Booleans.kt` in.

**Chapter 01 gets no branch.** Its four videos are install-and-first-program; the Gradle project
doesn't exist until `01_04b`, and once the seed on `main` builds, `01_04e` is effectively already
in it. Watch `01_01`–`01_04` for the IntelliJ setup, write nothing, and start typing at
`chapter/02`. So the 39 videos map to **7 chapter branches (02–08)**, not 8.

To see how far along a fork is, read `git log --oneline main` and the merged chapter PRs — that's
the record, so this document doesn't duplicate it.

## Branch model

```
COURSE SNAPSHOTS — 77 branches, read-only. Never checked out.
┌───────────────────────────────────────────────────────────────────────────┐
│   02_01b  02_01e     02_02b  02_02e     02_03b  02_03e     02_04b  02_04e │
└──────┬───────┬──────────────────────────────────────────────────┬─────────┘
       │       │                                                  │
       │       └──────────── git show   (answer key) ──────────────┘
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
| `origin/*e` | upstream's | the instructor's end state — the answer key, also read with `git show` |

**Rules**

1. Never check out a course branch. Read files out of it (`git show`) so its broken Gradle never
   lands in the working tree.
2. Cut `chapter/NN` from `main`, never from a course branch.
3. Only infra/docs PRs and chapter PRs target `main`. A lesson commit straight on `main` is a
   mistake.
4. Never commit to or force-push a `*b` or `*e` branch — `upstream` stays the source of truth
   for them. Local tooling config belongs on `main`.
5. `git fetch upstream` never auto-merges. Inspect, then cherry-pick deliberately.

## Repo structure

The **target** layout, once the course is finished — not what a fresh fork looks like.

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
        │   ├── .gitkeep         keeps the source root in git on a bare seed
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
JVM class (`ch02.NumericTypesKt`), so any number of `main()` functions coexist. Run whichever one
you're working on from the green arrow in the IntelliJ gutter — every past lesson stays one click
away — or name it from the terminal:

```bash
./gradlew run -Plesson=ch02.NumericTypesKt     # the class name, not the file name
```

There's no single entry point to default to, so the seed's `application { mainClass }` reads that
`lesson` property (defaulting to `ch02.BooleansKt`) instead of naming one lesson for good.

The rule is really one file per **concept**; for chapters 02–06 that happens to be one per video.
Where a video continues the previous video's program instead of introducing something new — ch07
grows one IO program across `07_01`–`07_05`, ch08 splits a `ViewModel` out into its own file and
tests it — keep editing the same file and let the commit message record which video it was. Adding
`FileIo2.kt` to preserve one-file-per-video would follow the letter and lose the point.

Nothing overwrites a previous lesson. Past chapters stay runnable.

## The loops

**Per video**, on the chapter branch:

```
   watch video ──▶ write the code yourself ──▶ read origin/0X_0Ye
                            ▲                            │
                            └──── fix what differs ◀──────┘
                                                         │
                                              commit ("02_03: …")
```

```bash
git show origin/03_01b:project/src/main/kotlin/Main.kt     # starting code, when needed
#   ... write it ...
git show origin/03_01e:project/src/main/kotlin/Main.kt     # answer key — read it, don't diff it
git commit -m "03_01: local functions and default arguments"
```

**Read the answer key, don't diff it.** The instructor keeps the whole course in one
`project/src/main/kotlin/Main.kt`, rewritten each video. Your code lives in
`project/src/main/kotlin/ch03/Lambdas.kt`, under `package ch03`, with a named `main()`. So
`git diff HEAD origin/03_01e -- project/src/` reports a delete of your file and an add of theirs:
every line changed, nothing lined up, no signal. Read their file and compare the bodies — by eye,
or in a split pane. `git diff` earns its keep only where the paths genuinely match, which in this
course means chapter 08, where the instructor splits into `ViewModel.kt` / `AnalyticsClient.kt` /
`ViewModelTest.kt` and your layout can match theirs.

**Per chapter** — this is where goal B lives:

```bash
git switch -c chapter/03 main                              # start
# ... videos ...
git show origin/03_06e:project/src/main/kotlin/Main.kt     # the chapter's end state
/code-review                                               # then self-review
gh pr create --base main --fill
gh pr merge --squash --delete-branch
```

`--base main` matters: `main` is the default branch, so `gh` targets it anyway — but being
explicit is what stops a lesson PR landing on the wrong base.

## How Claude is used

These are the rules that decide whether this works at all:

1. **Claude does not write the exercise code.** Not a scaffold, not "just the boilerplate."
   You type it. This is the entire point, and it's the easiest rule to erode.
2. **Claude handles the plumbing** — Gradle, JDKs, CI, git. That's where the time goes otherwise.
3. **Claude reviews after you've written it**, against `origin/*e`, and explains *why* the
   instructor's version differs. This is the feedback the video can't give.
4. **Claude explains concepts on demand** while you're in the file.

`CLAUDE.md` states these as instructions a session has to follow.

## Course map

| Chapter | Videos | Topic | Notes |
| --- | --- | --- | --- |
| 01 | 01_01–01_04 | setup, first program | no Gradle project until `01_04`; no chapter branch |
| 02 | 02_01–02_04 | types, variables, null safety | |
| 03 | 03_01–03_06 | functions, lambdas, function parameters | **`03_06` has no `b`** — start from `03_05e` |
| 04 | 04_01–04_08 | `when` expressions, `is` / smart casts | |
| 05 | 05_01–05_03 | loops, ranges | |
| 06 | 06_01–06_07 | collections — `map`, `sortedBy`, `take` | ships data as Kotlin source |
| 07 | 07_01–07_05 | file IO | reads `scores.txt` from the working dir, writes `sorted.txt` |
| 08 | 08_01–08_02 | ViewModel / AnalyticsClient | where the course itself introduces `src/test/` |

39 videos, 7 chapter branches, 7 chapter PRs.

## Known loose ends

- CI installs Java 17 while the toolchain is 21. Harmless — Gradle auto-provisions 21, verified
  green — but tidier to align. Needs `gh auth refresh -h github.com -s workflow` to push a
  workflow change.
- `main`'s `.gitignore` is the pristine 4-line upstream version, so `.idea/` and `qodana.yaml`
  show as untracked. Rules for them were committed onto `02_02b` instead — a course snapshot
  branch, which rule 4 says to leave alone. Move them to `main` and reset that branch to
  `upstream/02_02b`.
- Qodana (`qodana.yaml`, `qodana_code_quality.yml`) is untracked and has never run in CI. Either
  commit it properly or delete it.
