# Way of working

How to work through the LinkedIn Learning course *Kotlin Essential Training: Functions,
Collections, and I/O* in a fork: fix the build once, type every line of Kotlin yourself, and
open one small PR per video so the feedback arrives while the video is still fresh.

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
| **B** | Learn the workflow — git, PRs, and working with Claude | **one PR per video**, reviewed before it merges |

Goal B is not ceremony to be minimised. Working fluently with git and with an AI assistant is a
skill on its own, and skills come from repetition: 35 rounds of branch → write → PR → review →
fix → merge, not 7. The cost is real — a couple of minutes per video, a couple of hours across
the course — and it buys the reps.

**Why per video rather than per chapter**, having tried both:

- **Feedback lands before the next concept is built on it.** A finding buried in a four-video PR
  arrives after you've already written the next two videos on top of the misunderstanding.
- **Small diffs get read; large ones get skimmed.** A 70-line PR gets acted on line by line. A
  300-line one gets three findings fixed and the rest quietly dropped.
- **Failure stays contained.** A shaky video affects one PR, not three later videos stacked on it.
- **`main` becomes a finer record.** One commit per video means `git log main` reads as your
  actual progression, and `revert`/`bisect` work at video granularity permanently.
- **Branching gets simpler.** Cut from `main`, merge, delete. No branch drifting behind `main`
  for a week accumulating half-finished work.

What per-video PRs *can't* see is drift across files — `fun main ()` in one file and `fun main()`
in its neighbours, naming wandering from `ourFirstVariable` to `anInt`. That's what the
[per-chapter pass](#per-chapter) is for, and it needs no PR.

## Set up

```bash
# 1. Fork the course repo on GitHub, clone your fork, and keep the course as `upstream`.
git clone git@github.com:<you>/kotlin-essential-training-functions-collections-and-io-3008787.git
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

# 3. Start the first video that has code of your own.
git switch -c lesson/02_01 main

# 4. Write the lesson file yourself — the seed ships no Kotlin. Create
#    project/src/main/kotlin/ch02/Booleans.kt with `package ch02` and a `fun main()`,
#    then run it from the IntelliJ gutter or with:
./gradlew run -Plesson=ch02.BooleansKt
```

Clone over **SSH**, as above. Pushes that touch `.github/workflows/` are rejected over HTTPS
unless the OAuth token carries the `workflow` scope, because a workflow file is code GitHub will
execute with access to your secrets — so it sits behind a scope of its own. SSH keys have no
scopes, so the question never arises.

The first `./gradlew build` takes a few minutes and downloads a JDK (~200 MB): that's the foojay
resolver fetching Temurin 21 because `jvmToolchain(21)` asked for a JDK the machine doesn't have.
It isn't stuck. Later builds reuse it.

Keep `main` free of hand-written lesson commits: it carries the seed, and every merged lesson PR.
The seed's `project/src/main/kotlin/` holds only a `.gitkeep` — git won't track an empty
directory, and without it a fresh clone would have no source root to put `ch02/Booleans.kt` in.

**Chapter 01 gets no branch and no PR.** Its four videos are install-and-first-program; the Gradle
project doesn't exist until `01_04b`, and once the seed on `main` builds, `01_04e` is effectively
already in it. Watch `01_01`–`01_04` for the IntelliJ setup, write nothing, and start typing at
`lesson/02_01`. So the 39 videos map to **35 lesson PRs** (chapters 02–08), not 39.

To see how far along a fork is, read `git log --oneline main` and the merged PRs — that's the
record, so this document doesn't duplicate it.

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
              lesson/02_01      lesson/02_02      lesson/02_03
                  ●                 ●                 ●
                 ╱ ╲ PR            ╱ ╲ PR            ╱ ╲ PR
 main ──────────●───●─────────────●───●─────────────●───●──────────────►
                    │                 │                 │
                 squash +        one commit         35 of these
              branch deleted     per video       across the course
```

| Ref | Lifetime | Job |
| --- | --- | --- |
| `main` | permanent | the working build (the *seed*) plus every merged video. Accumulates. |
| `lesson/NN_MM` | hours | one per video, cut from `main`, PR'd back, then deleted |
| `origin/*b` | upstream's | starting code — read with `git show`, never checked out |
| `origin/*e` | upstream's | the instructor's end state — the answer key, also read with `git show` |

**Rules**

1. Never check out a course branch. Read files out of it (`git show`) so its broken Gradle never
   lands in the working tree.
2. Cut `lesson/NN_MM` from `main`, never from a course branch or another lesson branch.
3. Everything reaches `main` through a PR — lesson, infra or docs. A commit made straight on
   `main` is a mistake.
4. Never commit to or force-push a `*b` or `*e` branch — `upstream` stays the source of truth
   for them. Local tooling config belongs on `main`. The one exception is restoring a snapshot to
   `upstream`'s exact state, which is the property this rule exists to protect; check it for
   unmerged work first, because that force-push destroys whatever it carried.
5. `git fetch upstream` never auto-merges. Inspect, then cherry-pick deliberately.
6. Don't name a branch `02_04` — it reads too close to the course's own `02_04b` / `02_04e`.
   The `lesson/` prefix keeps your refs and upstream's visibly distinct.

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
        │   │   ├── CharAndStrings.kt    fun main()   ← 02_03
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
`FileIo2.kt` to preserve one-file-per-video would follow the letter and lose the point. The PR is
still per video; it just carries a change to an existing file rather than a new one.

Nothing overwrites a previous lesson. Past chapters stay runnable.

## The loops

### Per video

```
   watch video ──▶ write the code yourself ──▶ read origin/0X_0Ye
                            ▲                            │
                            └──── fix what differs ◀──────┘
                                                         │
                                       commit ──▶ push ──▶ PR ──▶ review
                                                         ▲          │
                                                         └── fix ◀──┘
                                                                    │
                                                        squash + delete
```

```bash
git switch -c lesson/03_01 main
git show origin/03_01b:project/src/main/kotlin/Main.kt     # starting code, when needed
#   ... write it ...
git show origin/03_01e:project/src/main/kotlin/Main.kt     # answer key — read it, don't diff it
cd project && ./gradlew build                              # zero warnings before committing
git commit -m "03_01: local functions and default arguments"
git push -u origin lesson/03_01
gh pr create --base main --fill
#   ... review, then fix forward on the branch ...
gh pr merge --squash --delete-branch
git switch main && git pull
```

**Read the answer key, don't diff it.** The instructor keeps the whole course in one
`project/src/main/kotlin/Main.kt`, rewritten each video. Your code lives in
`project/src/main/kotlin/ch03/Lambdas.kt`, under `package ch03`, with a named `main()`. So
`git diff HEAD origin/03_01e -- project/src/` reports a delete of your file and an add of theirs:
every line changed, nothing lined up, no signal. Read their file and compare the bodies — by eye,
or in a split pane. `git diff` earns its keep only where the paths genuinely match, which in this
course means chapter 08, where the instructor splits into `ViewModel.kt` / `AnalyticsClient.kt` /
`ViewModelTest.kt` and your layout can match theirs.

**Fix forward, don't amend.** When review finds something, land the fix as its own commit on the
branch. The PR then records what was found and what you did about it, which is the loop being
practised. The squash flattens it on `main` anyway, so tidiness costs nothing.

### Per chapter

No PR and no branch — just the pass a single-file PR cannot do, run on `main` after the
chapter's last video merges:

```bash
/code-review                                # cross-file consistency
git diff main~4 main -- project/src/        # e.g. chapter 02's four videos
```

This is what catches naming drift, the same idea demonstrated three different ways, and style
that diverges between neighbouring files. If it finds something, that's an ordinary PR of its own.

### Where review sits

| When | What | Who |
| --- | --- | --- |
| before commit | compiles with zero warnings, runs, output matches what you expected | you |
| on the PR | correctness, idiom, comparison with `origin/*e` | Claude, inline on the diff |
| after each chapter | cross-file consistency | `/code-review` on `main` |

Reading your program's output against what you *expected* is the part worth guarding. It's the
skill that transfers, and it's the one thing an assistant reviewing first will quietly take from
you.

## What survives a squash

`gh pr merge --squash` builds **one new commit** from the branch's diff; the branch's own commits
are never its parents, and `--delete-branch` leaves nothing referencing them. With one video per
PR that's exactly what you want — one commit per video on `main`:

```
02_03: characters and strings (#9)
```

Two things to know:

- **GitHub's default squash body** is the branch's commit messages as a bullet list, so a
  review-response commit shows up there. Keep it; it's the honest history.
- **The PR page keeps every commit forever**, with its diff and the review comments anchored to
  it, even after the branch is deleted. `git log` loses that granularity; GitHub doesn't.

## How Claude is used

These are the rules that decide whether this works at all:

1. **Claude does not write the exercise code.** Not a scaffold, not "just the boilerplate."
   You type it. This is the entire point, and it's the easiest rule to erode — expect to be asked
   for the solution at the exact moment it would be most convenient to hand it over.
2. **Claude handles the plumbing** — Gradle, JDKs, CI, git, branch and PR mechanics. That's where
   the time goes otherwise.
3. **Claude reviews on the PR**, against `origin/*e`, inline on the diff, and explains *why* the
   instructor's version differs. That's the feedback the video can't give.
4. **Claude explains concepts on demand**, as much as asked. A question about a mechanism
   ("why does `1_234_567_890.toByte()` print `-46`?") costs you nothing; asking Claude to inspect
   your file before you've read your own output costs you the discovery.

`CLAUDE.md` states these as instructions a session has to follow.

## Course map

| Chapter | Videos | PRs | Topic | Notes |
| --- | --- | --- | --- | --- |
| 01 | 01_01–01_04 | 0 | setup, first program | no Gradle project until `01_04`; watch only |
| 02 | 02_01–02_04 | 4 | types, variables, null safety | |
| 03 | 03_01–03_06 | 6 | functions, lambdas, function parameters | **`03_06` has no `b`** — start from `03_05e` |
| 04 | 04_01–04_08 | 8 | `when` expressions, `is` / smart casts | |
| 05 | 05_01–05_03 | 3 | loops, ranges | |
| 06 | 06_01–06_07 | 7 | collections — `map`, `sortedBy`, `take` | ships data as Kotlin source |
| 07 | 07_01–07_05 | 5 | file IO | reads `scores.txt` from the working dir, writes `sorted.txt` |
| 08 | 08_01–08_02 | 2 | ViewModel / AnalyticsClient | where the course itself introduces `src/test/` |

39 videos, **35 lesson PRs**, one commit per video on `main`.

## Known loose ends

- `origin/02_01b` sits three commits ahead of `upstream/02_01b`. All three reached `main` through
  PR #1, so restoring it — `git push --force origin upstream/02_01b:refs/heads/02_01b` — would
  lose nothing. `02_02b` was restored the same way; the lesson work it carried was discarded
  deliberately, to be redone from the video.
