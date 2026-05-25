# Claude Code config for this repo

This folder configures [Claude Code](https://claude.com/claude-code) for working in this repo — primarily for driving the project's Maestro UI tests. None of this affects the published SDK; it's developer tooling only.

## What's in here

- `settings.json` — pre-approved tool permissions (so Claude doesn't prompt you for every `./gradlew`, `adb`, `maestro`, etc.).
- `skills/` — slash commands that encode the project's Maestro workflow:
  - `/maestro-run` — run a single test (handles `launchApp` workaround, env preference, driver-wedge recovery).
  - `/maestro-test` — orchestrator: run + auto-recover driver wedges + auto-heal once on assertion failures.
  - `/maestro-heal` — triage a failed test (decide: fix the test, recover state, or surface a real bug).
  - `/maestro-author` — author a new test against `.maestro/docs/AUTHORING_RULES.md`.

The Maestro MCP server is wired up at the repo root in `../.mcp.json`.

## First-time setup for a teammate

1. **Install Maestro CLI** (the MCP server is just `maestro mcp` under the hood):
   ```sh
   brew install maestro
   ```
2. **Open the repo in Claude Code.** On first launch you'll get a one-time consent prompt:
   *"Approve MCP server `maestro` from .mcp.json?"* — accept it.
3. Verify it connected:
   ```sh
   claude mcp list
   # should show: maestro: maestro mcp - ✓ Connected
   ```
4. (Optional) If you previously registered a local-scope `maestro` MCP, remove it so the project-scoped one is used cleanly:
   ```sh
   claude mcp remove maestro -s local
   ```

That's it. The skills are auto-discovered from `skills/` and the permissions in `settings.json` apply automatically.

## Personal overrides

If you want to grant extra permissions or add hooks just for your machine, create `.claude/settings.local.json` — it's gitignored and Claude Code merges it on top of `settings.json`.

## Running a test

```
/maestro-test .maestro/Card/test_card_one_off.yaml
```

The skills assume **DEMO env** (PREVIEW has methods disabled that break tests). The pre-flight `.maestro/scripts/ensure-env.sh` handles the switch.

See `../CLAUDE.md` for the broader project context Claude reads on session start.
