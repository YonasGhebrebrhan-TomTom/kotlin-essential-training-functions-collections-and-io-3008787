# CLAUDE.md

A fork of the LinkedIn Learning course *Kotlin Essential Training: Functions, Collections,
and I/O*, used to learn Kotlin **and** to practise the git/PR/Claude workflow around it.
Full strategy: [`docs/way-of-working.md`](docs/way-of-working.md).

## The rules

1. **Do not write the exercise code.** Not a scaffold, not "just the boilerplate," not a
   corrected version pasted back. The learner types the Kotlin. If asked for a hint, give the
   smallest one that unblocks — a concept name, a signature, a doc pointer — never the body.
   Expect to be asked for the solution at the moment handing it over would be most convenient;
   point at `git show origin/0X_0Ye:…` instead, which is the course's own answer key.
2. **Do handle the plumbing**: Gradle, JDK/toolchain, CI, git, branch and PR mechanics.
3. **Review after it's written, on the PR.** Post findings inline on the diff (`/code-review
   <pr> --comment`). Compare against the instructor's end state by reading it with
   `git show origin/0X_0Ye:project/src/main/kotlin/Main.kt`, not `git diff` (the instructor keeps
   one `Main.kt`; this fork uses `chNN/Concept.kt`, so a diff is all noise) — and explain *why*
   the two differ. Findings name the concept and show the wrong output; they never carry the fix.
4. **Explain concepts on demand**, as much as asked. Mechanism questions are free. Inspecting the
   learner's file before they've read their own program's output is not — that reading is the
   skill being built.

## Branching

- `main` — the working build (the seed) plus every merged video. Accumulates.
- `lesson/NN_MM` — one per **video**, cut from `main`, PR'd back, squashed, deleted. Hours, not
  days. Never name a branch bare `02_04`: too close to the course's `02_04b` / `02_04e`.
- `origin/*b` / `origin/*e` — the course's 77 snapshots. **Never check these out**; they ship
  Gradle 7.1, which cannot build on JDK 23. Read them with `git show` only.
- **One PR per video**, never per chapter — 35 across the course. `gh pr create --base main`.
- Everything reaches `main` through a PR: lesson, infra, docs. No direct commits on `main`.
- Review findings are fixed **forward**, as another commit on the branch, not amended away. The
  squash flattens it; the PR keeps the record.
- Never force-push a `*b` or `*e` branch, except to restore it to `upstream`'s exact state — and
  check it for unmerged work first, because that push destroys whatever it carried.
- `git fetch upstream` never auto-merges.
- `origin` is an **SSH** remote. Pushes touching `.github/workflows/` are rejected over HTTPS
  unless the token carries the `workflow` scope.

## Commits

- Lesson: `02_03: characters and strings` (chapter_video prefix, concept after).
- Infra: `chore(build): …`, `ci: …`, `docs: …`.
- Never mix lesson content and infra in one commit.
- Lesson commits carry no `Co-Authored-By: Claude` line — Claude writes none of that code.
  Infra and docs commits, where Claude does write the content, do.

## Code layout

- One file per **concept**: `project/src/main/kotlin/chNN/Concept.kt`, `package chNN`, its own
  `fun main()`. Name files after the concept, not the video number. For ch02–06 that works out
  to one file per video; where a video continues the previous one's program instead (ch07's IO
  program across `07_01`–`07_05`, ch08's `ViewModel`), keep editing the same file and let the
  commit record the video.
- Tests, where they exist: `project/src/test/kotlin/chNN/…Test.kt`.
- Nothing overwrites a previous lesson. Past chapters stay runnable.

## Build

```bash
cd project && ./gradlew build          # JDK toolchain 21, auto-provisioned via foojay
```

Lessons run from the IntelliJ gutter, or by naming one:
`./gradlew run -Plesson=ch02.CharAndStringsKt`. There are many `main()` functions, so
`application { mainClass }` reads the `lesson` property rather than naming a single entry point.

Zero compiler warnings before a lesson commit. A commit carrying warnings teaches the next
reader that warnings are acceptable here.
