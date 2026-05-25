# Airwallex Android Payment SDK

## Project Overview
This is the **Airwallex Android SDK** - a payment integration library that enables Android apps to accept payments through various methods including cards (Visa, Mastercard), e-wallets (Alipay, WeChat Pay, etc.), and Google Pay.

- **Current Version**: 6.7.1
- **Min SDK**: 21 (Android 5.0)
- **Compile SDK**: 36
- **Language**: Kotlin 1.9.25
- **Published to**: Maven Central as `io.github.airwallex:payment`

## Architecture & Module Structure

This is a multi-module Android project with the following modules:

### Core Modules
- **`components-core`** - Core components and shared functionality
- **`ui-core`** - Core UI components and theming
- **`airwallex`** - Main SDK module, entry point for SDK initialization

### Payment Method Modules
- **`card`** - Card payment integration (Visa, Mastercard)
- **`googlepay`** - Google Pay integration
- **`wechat`** - WeChat Pay integration
- **`redirect`** - Redirect-based payment methods (Alipay, AlipayHK, DANA, GCash, Kakao Pay, Touch 'n Go)
- **`security-3ds`** - 3D Secure authentication

### Additional Modules
- **`fingerprinting`** - Device fingerprinting (TrustDefender integration)
- **`sample`** - Demo app showcasing SDK integration
- **`airwallex-detekt-rules`** - Custom Detekt rules for code quality

## Key Files & Directories

### Documentation
- `README.md` / `README-zh.md` - Project overview and installation
- `GUIDE.md` / `GUIDE-zh.md` - Comprehensive integration guide
- `MIGRATING.md` - Migration guide for version updates
- `CHANGELOG.md` - Version history

### Build & Configuration
- `build.gradle` - Root build configuration, version management, Dokka setup
- `settings.gradle` - Module includes and dependency substitution
- `gradle.properties` - Gradle configuration
- `android-module.gradle` - Shared Android module configuration
- `android-shared-compose.gradle` - Shared Compose configuration
- `publish-module.gradle` - Publishing configuration for Maven Central
- `jacoco.gradle` - Code coverage configuration
- `sonar.gradle` - SonarQube integration

### Quality & Testing
- `config/` - Configuration files for linting, code quality
- `.maestro/` - Maestro UI testing flows
- `.github/` - GitHub Actions workflows

## Development Workflows

### Build Commands
```bash
./gradlew build                    # Build all modules
./gradlew assembleDebug            # Build debug variant
./gradlew assembleRelease          # Build release variant
./gradlew test                     # Run unit tests
./gradlew connectedAndroidTest     # Run instrumented tests
```

### Code Quality
```bash
./gradlew detekt                   # Run Detekt static analysis
./gradlew rootCodeCoverageReport   # Generate code coverage report
```

### Documentation
```bash
./gradlew dokkaHtmlMultiModule     # Generate SDK documentation
```

### Publishing
- Publishing is configured via `publish-module.gradle`
- Publishes to Maven Central under group `io.github.airwallex`
- Version is set in root `build.gradle`

## Integration Patterns

The SDK offers two integration approaches:

### 1. Native UI Integration (Recommended)
Pre-built UI components that merchants can use directly:
- Payment method selection page
- Card input form
- Shipping info form
- Payment confirmation page

### 2. Low-level API Integration
For merchants who want to build custom UI:
- Direct API calls for payment operations
- Requires PCI-DSS compliance for card payments
- Full control over UI/UX

## Session Types & Parceling

### Session class hierarchy
The SDK exposes four session types, all extending the abstract `AirwallexSession`:

- **`Session`** — the unified, recommended type for all payment scenarios (one-off, recurring with/without intent). New integrations should use this.
- **`AirwallexPaymentSession`** — `@Deprecated`, replaced by `Session`.
- **`AirwallexRecurringSession`** — `@Deprecated`, replaced by `Session`.
- **`AirwallexRecurringWithIntentSession`** — `@Deprecated`, replaced by `Session`.

The three deprecated classes are kept for source compatibility but should not be used in new code. They carry a `@Deprecated` annotation pointing at `Session.Builder(...)`.

### Parceling: Session is NOT Parcelable
Critical gotcha: of the four session types, only the three deprecated ones are `@Parcelize`. **`Session` itself is not `Parcelable`** because it holds non-parcelable fields (e.g. the transient `PaymentIntentProvider`).

To pass a `Session` between Activities (Intent extras), convert it through `ParcelableSession`:

- **Sending side**: `session.toParcelableSession()` (from `ui-core/SessionExtensions.kt`). For the deprecated session types, use `session.convertToSession().toParcelableSession()` — both `AirwallexPaymentSession` and `AirwallexRecurringWithIntentSession` provide `convertToSession()` extensions in `components-core/extension/Session+Extensions.kt`.
- **Receiving side**: `parcelableSession.toSession()` (in `ParcelableSession.kt`) reconstructs the `Session` and re-attaches the transient `PaymentIntentProvider` from `PaymentIntentProviderRepository`.

### Adding a new field to Session
Because of the round-trip above, adding any new field to `Session` requires **all** of the following or the value will silently drop:

1. Add the field as a constructor `val` on `Session` and on its `Builder` (plus a setter).
2. Add the same field to `ParcelableSession`'s primary constructor.
3. Pipe it through `Session.toParcelableSession()` (`ui-core/SessionExtensions.kt`).
4. Pipe it through `ParcelableSession.toSession()` (`components-core/ParcelableSession.kt`).
5. If the field also exists on the legacy session types, pipe it through both `convertToSession()` extensions in `components-core/extension/Session+Extensions.kt`.

Missing any of these surfaces as "value set on the builder but null when the UI reads it from the session." The `requiredBillingContactFields` regression was caused by skipping steps 2, 3, and 5.

## Coding Conventions

### Kotlin
- Kotlin 1.9.25
- Coroutines for async operations (`kotlinCoroutinesVersion = '1.8.1'`)
- Follow Airwallex custom Detekt rules (defined in `airwallex-detekt-rules`)

### Architecture
- Modular architecture - each payment method is a separate module
- Dependency injection pattern used throughout
- MVVM pattern for UI components

### Naming
- Activities should end with `Activity` (e.g., `PaymentMethodsActivity`)
- Fragments should end with `Fragment`
- ViewModels should end with `ViewModel`

## Current Work Context

### Active Branch: `feature/APAM-728-maestro-claude-skills`
Adds Claude Code skills under `.claude/skills/` that automate the project's Maestro workflow:
- `maestro-run` — pre-flight (env preference via `ensure-env.sh`, host-side `am start -n com.airwallex.paymentacceptance/.ui.MainActivity`) + run a single test through the Maestro MCP server
- `maestro-heal` — failure triage tree (driver-wedge recovery, state cleanup, real-bug detection); strict default — never silently relax assertions
- `maestro-test` — composer that wires `maestro-run` and `maestro-heal` together with bounded retry budgets
- `maestro-author` — guided authoring against `.maestro/docs/AUTHORING_RULES.md`; closes the loop by updating the matching `.feature` file

Operational notes encoded across the skills:
- **DEMO is the canonical env** for local / Claude-driven runs. PREVIEW has disabled methods (e.g. alipayhk recurring) that break tests with non-test-bug failures.
- `- launchApp` stays in committed YAMLs (proper Maestro pattern for CLI/CI). Skills temporarily comment it out for MCP runs and restore after — `launchApp`'s `am start` + wait-for-ready times out on this app's cold-start path.
- Never `pkill maestro` / `bash .maestro/free-port.sh` — severs the MCP tool channel for the rest of the session. Recovery is reboot + manual driver re-bootstrap.

## Testing

### Test Infrastructure
- **JUnit** `4.13.2` - Unit testing framework
- **Robolectric** `4.14.1` - Android unit testing
- **Test Core** `1.5.0` - AndroidX test core
- **Maestro** - UI flow testing (`.maestro/` directory)
- **Jacoco** `0.8.11` - Code coverage

### Test Card Numbers
Reference `GUIDE.md` for test card numbers for different scenarios.

### Maestro UI Tests
- Flows and tests live under `.maestro/` (per-integration folders: `Api/`, `Card/`, `Common/`).
- BDD source-of-truth in `.maestro/docs/*.feature`; coverage philosophy in `.maestro/docs/AUTHORING_RULES.md`; per-test matrix in `.maestro/docs/TEST_PERMUTATIONS.md`.
- Drive Maestro via the project's Claude Code skills (`/maestro-run`, `/maestro-heal`, `/maestro-test`, `/maestro-author`) rather than running `maestro test` directly — the skills handle the `launchApp` workaround, env pre-flight, and driver-wedge recovery.
- Pre-flight env switching: `.maestro/scripts/ensure-env.sh DEMO [device-id]` (writes the SharedPreferences `Environment` key via `run-as`, cold-restarts the app on MainActivity).

## Localization
Supports 13 languages:
- English, Chinese Simplified, Chinese Traditional
- French, German, Japanese, Korean
- Portuguese (Portugal & Brazil), Russian, Spanish, Thai

## Important Notes

### Dependencies
- Android Gradle Plugin: 8.6.1
- WeChat Pay SDK: 6.8.0
- All modules use shared dependency versions defined in root `build.gradle`

### CI/CD
- GitHub Actions workflows in `.github/`
- Semantic release configuration in `.releaserc.json`
- SonarQube integration for code quality metrics

### Publishing
- Published to Maven Central
- SDK reference docs: https://airwallex.github.io/airwallex-payment-android/

## Support & Feedback
- Issues: Create GitHub issues in this repository
- Email: pa_mobile_sdk@airwallex.com
