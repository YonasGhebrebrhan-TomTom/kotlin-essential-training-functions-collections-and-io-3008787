# CLAUDE.md

A fork of the LinkedIn Learning course *Kotlin Essential Training: Functions, Collections,
and I/O*, used to learn Kotlin. Full strategy: [`docs/way-of-working.md`](docs/way-of-working.md).

## The rules

1. **Do not write the exercise code.** Not a scaffold, not "just the boilerplate," not a
   corrected version pasted back. The learner types the Kotlin. If asked for a hint, give the
   smallest one that unblocks — a concept name, a signature, a doc pointer — never the body.
2. **Do handle the plumbing**: Gradle, JDK/toolchain, CI, git, branch and PR mechanics.
3. **Review only after it's written**, against the instructor's end state — read it with
   `git show origin/0X_0Ye:project/src/main/kotlin/Main.kt`, not `git diff` (the instructor
   keeps one `Main.kt`; this fork uses `chNN/Concept.kt`, so a diff is all noise) — and explain
   *why* the two differ.
4. **Explain concepts on demand**, as much as asked.

## Branching

- `main` — the working build (the seed) plus every finished chapter. Accumulates.
- `chapter/NN` — one per chapter, cut from `main`, one commit per video, PR'd back, deleted.
- `origin/*b` / `origin/*e` — the course's 77 snapshots. **Never check these out**; they ship
  Gradle 7.1, which cannot build on JDK 23. Read them with `git show` / `git diff` only.
- One PR per **chapter**, never per video. `gh pr create --base main`.
- Only infra/docs PRs and chapter PRs target `main`.
- Never force-push a `*b` or `*e` branch. `git fetch upstream` never auto-merges.

## Commits

- Lesson: `02_03: strings and string templates` (chapter_video prefix, concept after).
- Infra: `chore(build): …`, `ci: …`, `docs: …`.
- Never mix lesson content and infra in one commit.

## Code layout

- One file per video: `project/src/main/kotlin/chNN/Concept.kt`, `package chNN`, its own
  `fun main()`. Name files after the concept, not the video number.
- Tests, where they exist: `project/src/test/kotlin/chNN/…Test.kt`.
- Nothing overwrites a previous lesson. Past chapters stay runnable.

## Build

```bash
cd project && ./gradlew build          # JDK toolchain 21, auto-provisioned via foojay
```

Lessons run from the IntelliJ gutter (many `main()` functions, so `./gradlew run` has no
single target).
