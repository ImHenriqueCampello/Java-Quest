# Graph Report - First  (2026-11-14)

## Corpus Check
- 46 files · ~62,659 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1243 nodes · 4363 edges · 59 communities (35 shown, 24 thin omitted)
- Extraction: 79% EXTRACTED · 21% INFERRED · 0% AMBIGUOUS · INFERRED: 916 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Achievements and ANSI Display
- Combat Animation and Boss Mechanics
- Combat Configuration and Battle State
- Color and Constants Unit Tests
- Study Session and Post-Mortem Report
- Achievement Verification Tests
- Battle Actions and Sealed Interfaces
- Combat Input Parsing Parameterized Tests
- I/O Stream Unit Testing Fixtures
- Achievement Model and Definitions
- Game Difficulty Configuration
- Procedural 8-Bit Audio Engine
- ANSI Text Centering and Formatting
- Terminal I/O and Compact Mode
- Localization and Theme Configuration
- ANSI Stripping and Status Bars
- Enemy Animation Frames and Endless Mode
- Visual Theme Palette Management
- Virtual Terminal I/O Test Harness
- JUnit Launcher Discovery Framework
- Game Save and Persistence Manager
- Core Gameplay Architecture and Accessibility
- Game Engine State and Over Screen Tests
- Collections and Queue Utilities
- Player Talent and Save Data Model
- Leaderboard Persistence and Scores
- Player Attribute Mutators and Stats
- Dynamic Question Pool Generation Tests
- Boss Character Sprite Verification
- Question Model and Constructor Tests
- GitHub Release Distribution Packaging
- Combat Arena Canvas Layout Tests
- Terminal CellRow Buffer Clipping Tests
- Talent System and Build Diversity
- CI/CD Pipeline Job Workflow
- Procedural Chiptune Audio and Boss Lore
- Core System Invariants and Regression Suites
- GitHub Actions Environment Step Tokens
- Visual Cleanliness Sprite Tests
- Player Phase 3 Features Test
- Matrix Build OS Test Strategy
- Combat Idle Animation Thread Session
- Player Name Sanitization Constructor Tests
- Git Workflow Triggers and Branches
- Cross-Platform Packaging and CI/CD Release
- Informational Screens and UI Rendering
- JLS Specification and Study Dossier
- Player Spells and Debug Powers Mechanics
- Package Build Job Workflow
- Multi-Platform Artifact Strategy Matrix
- Combat Configuration Modes Tests
- Fast In-Memory Test Runner
- Linux Packaging Automation Script
- macOS Packaging Automation Script
- Terminal Game Launcher Script
- Local Test Suite Execution Script

## God Nodes (most connected - your core abstractions)
1. `Player` - 75 edges
2. `Enemy` - 61 edges
3. `GameEngine` - 59 edges
4. `Ansi` - 51 edges
5. `GameContext` - 49 edges
6. `Question` - 31 edges
7. `BattleRenderer` - 28 edges
8. `Achievement` - 25 edges
9. `GameDifficulty` - 24 edges
10. `Item` - 24 edges

## Surprising Connections (you probably didn't know these)
- `Chiptune Procedural Audio Synthesizer` --semantically_similar_to--> `Procedural 8-bit Audio Engine (javax.sound.sampled)`  [INFERRED] [semantically similar]
  README.md → prompts/polir.txt
- `Post-Mortem JLS Study Dossier` --semantically_similar_to--> `Post-Mortem Debugging Study Report`  [INFERRED] [semantically similar]
  README.md → prompts/polir.txt
- `Bytecode Badges & Achievements` --semantically_similar_to--> `Bytecode Badges Achievement System`  [INFERRED] [semantically similar]
  README.md → prompts/polir.txt
- `Java Quest: The Bytecode Chronicles` --conceptually_related_to--> `Guia Completo de Polimento do Java Quest`  [INFERRED]
  README.md → prompts/polir.txt
- `PlayerTest` --references--> `Player`  [EXTRACTED]
  test/game/PlayerTest.java → src/game/Player.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Turn-Based Combat Integration Testing Pipeline** — prompts_test_options_combat_turn_integration, prompts_test_options_debug_powers_integration, prompts_test_options_campaign_progression_test [INFERRED 0.85]
- **Game Engine Decoupling and Architecture Refactoring Pattern** — prompts_polir_state_pattern, prompts_polir_terminal_io, prompts_polir_combat_config, prompts_polir_battle_action [INFERRED 0.95]
- **Cross-Platform Packaging and GitHub Actions CD Pipeline** — prompts_git_pipeline, prompts_git_jpackage_distribution, prompts_git_launchers [INFERRED 0.95]

## Communities (59 total, 24 thin omitted)

### Community 0 - "Achievements and ANSI Display"
Cohesion: 0.06
Nodes (10): Ansi, BattleRenderer, CellRow, MidCanvas, BytecodeLab, LabResult, GameEngine, Player (+2 more)

### Community 1 - "Combat Animation and Boss Mechanics"
Cohesion: 0.05
Nodes (20): BossMechanic, ARRAY_SLICE, COSMIC_VOID, COUPLING_LOCK, CPU_THROTTLE, DEEP_RECURSION, HEAP_OVERFLOW, NONE (+12 more)

### Community 2 - "Combat Configuration and Battle State"
Cohesion: 0.06
Nodes (10): CombatConfig, BattleState, GameContext, GameOverState, GameState, LevelTransitionState, MainMenuState, StateMachine (+2 more)

### Community 3 - "Color and Constants Unit Tests"
Cohesion: 0.06
Nodes (9): ColorizeAndConstantsTests, BytecodeLabStudyModeTests, AnswerAndComboTests, DamageTests, HealTests, InitialStateTests, LevelUpTests, PlayerTest (+1 more)

### Community 4 - "Study Session and Post-Mortem Report"
Cohesion: 0.12
Nodes (6): MissedQuestion, Question, QuestionBank, JlsReferenceTests, QuestionsAndCategorizationTests, PostMortemAnalysisTests

### Community 5 - "Achievement Verification Tests"
Cohesion: 0.09
Nodes (4): AchievementTests, ManaTests, ShieldTests, EdgeCasesTests

### Community 6 - "Battle Actions and Sealed Interfaces"
Cohesion: 0.09
Nodes (15): AnswerQuestion, BattleAction, CastCompilerHint, CastDebugHeal, CastVariableShield, ForfeitBattle, UseInventoryItem, Inventory (+7 more)

### Community 7 - "Combat Input Parsing Parameterized Tests"
Cohesion: 0.10
Nodes (9): CombatActionsParsingTests, DebugPowersParsingTests, GameEngineLogicTest, MainMenuParsingTests, QuizParsingTests, HybridShortcutsTests, ExhaustiveIntegrityScanTests, LevelCoverageTests (+1 more)

### Community 8 - "I/O Stream Unit Testing Fixtures"
Cohesion: 0.13
Nodes (4): ConcurrencyAndThreadSafetyIntegrationTest, EndToEndHeadlessSessionTest, FullCampaignProgressionTest, GameOverAndRetryIntegrationTest

### Community 9 - "Achievement Model and Definitions"
Cohesion: 0.08
Nodes (17): Achievement, ARCHITECT, BREAKPOINT_SAVIOR, BUG_HUNTER, BYTECODE_SCHOLAR, CLEAN_ARCHITECTURE, CLEAN_CODE, COFFEE_ADDICT (+9 more)

### Community 10 - "Game Difficulty Configuration"
Cohesion: 0.09
Nodes (6): GameDifficulty, INTERN, MID_LEVEL, SENIOR_ARCHITECT, LeaderboardEntry, GameDifficultyModesTests

### Community 11 - "Procedural 8-Bit Audio Engine"
Cohesion: 0.14
Nodes (8): AudioEngine, Tone, Waveform, NOISE, SAWTOOTH, SINE, SQUARE, AudioEngineTests

### Community 12 - "ANSI Text Centering and Formatting"
Cohesion: 0.14
Nodes (5): AnsiTest, CenterTests, PadLeftTests, PadRightTests, VisibleLengthTests

### Community 13 - "Terminal I/O and Compact Mode"
Cohesion: 0.11
Nodes (3): StandardTerminalIO, PolishingFeaturesTest, TerminalCompactAndVisualTests

### Community 14 - "Localization and Theme Configuration"
Cohesion: 0.13
Nodes (6): I18n, Language, EN_US, PT_BR, ComprehensiveGameFlowValidationTest, ArchitectureFase4Tests

### Community 15 - "ANSI Stripping and Status Bars"
Cohesion: 0.15
Nodes (3): StripAnsiTests, HpBarTests, ManaBarTests

### Community 17 - "Visual Theme Palette Management"
Cohesion: 0.10
Nodes (5): Theme, CYBERPUNK, DRACULA, MATRIX, MONOKAI

### Community 22 - "Core Gameplay Architecture and Accessibility"
Cohesion: 0.13
Nodes (17): BattleAction Sealed Interface Hierarchy, Boss Unique Phase Mechanics, Bytecode Lab Training Environment, Dynamic Terminal Size & 24-Line Compact Mode, Game Difficulty Modes & Permadeath, High Contrast Mode & POSIX NO_COLOR Support, Internationalization Framework (i18n PT-BR/EN-US), Dev Backpack Consumables & Inventory (+9 more)

### Community 26 - "Player Talent and Save Data Model"
Cohesion: 0.14
Nodes (8): SaveData, Talent, GRAALVM_COMPILER, HOTSPOT_INLINING, PATTERN_MATCHING_REFLEX, STREAM_PARALLELISM, TYPE_SAFETY_SHIELD, ZERO_ALLOCATION

### Community 27 - "Leaderboard Persistence and Scores"
Cohesion: 0.26
Nodes (3): LeaderboardManager, ComprehensivePolishingFeaturesTest, LeaderboardTests

### Community 30 - "Boss Character Sprite Verification"
Cohesion: 0.24
Nodes (4): BossSpritesTests, CharacterSpriteTest, ConstantsTests, PlayerSpritesTests

### Community 31 - "Question Model and Constructor Tests"
Cohesion: 0.24
Nodes (3): ConstructorAndGettersTests, QuestionTest, RandomizationTests

### Community 32 - "GitHub Release Distribution Packaging"
Cohesion: 0.17
Nodes (12): with, distribution, draft, files, generate_release_notes, if-no-files-found, java-version, name (+4 more)

### Community 33 - "Combat Arena Canvas Layout Tests"
Cohesion: 0.20
Nodes (4): ArenaNameTests, BattleRendererTest, MidCanvasTests, SpriteShiftTests

### Community 36 - "CI/CD Pipeline Job Workflow"
Cohesion: 0.20
Nodes (10): if, name, needs, permissions, runs-on, jobs, cd-release, name (+2 more)

### Community 37 - "Procedural Chiptune Audio and Boss Lore"
Cohesion: 0.18
Nodes (11): Bytecode Badges Achievement System, Procedural 8-bit Audio Engine (javax.sound.sampled), Bytecode Badges & Achievements, Chiptune Procedural Audio Synthesizer, Deadlock Shadow, IndexOutOfBounds Golem, Infinite Loop Serpent, Java Quest: The Bytecode Chronicles (+3 more)

### Community 38 - "Core System Invariants and Regression Suites"
Cohesion: 0.22
Nodes (11): CombatConfig Immutability Record, Ansi Formatting & CharacterSprite Geometry Test Suite, BattleRenderer Buffer & Canvas Test Suite, Full Campaign Progression & Level-Up Test, Combat Turn Simulation Integration Test, Enemy Factory & Boss Test Suite, Mutation Testing Targets & Operators Matrix, Player Unit Test Suite (+3 more)

### Community 39 - "GitHub Actions Environment Step Tokens"
Cohesion: 0.33
Nodes (10): steps, steps, steps, GITHUB_TOKEN, env, if, name, run (+2 more)

### Community 42 - "Matrix Build OS Test Strategy"
Cohesion: 0.29
Nodes (8): strategy, name, runs-on, strategy, ci-test, os, fail-fast, matrix

### Community 46 - "Git Workflow Triggers and Branches"
Cohesion: 0.29
Nodes (7): on, pull_request, push, workflow_dispatch, branches, branches, tags

### Community 47 - "Cross-Platform Packaging and CI/CD Release"
Cohesion: 0.29
Nodes (7): cd-release Automated Release Publisher Job, ci-test Automated Matrix Job, Guia de CI/CD com GitHub Actions, jpackage Standalone Packaging & jlink Runtime, Cross-Platform Desktop Launchers, GitHub Actions CI/CD Pipeline Workflow, TestRunner In-Memory Test Harness & test.sh

### Community 49 - "JLS Specification and Study Dossier"
Cohesion: 0.40
Nodes (6): Final Keyword Study Question, Java Language Specification §4.12.4 (final Variables), Relatório Pós-Jogo de Estudo e Revisão, JLS Normative Specification Explanations, Post-Mortem Debugging Study Report, Post-Mortem JLS Study Dossier

### Community 50 - "Player Spells and Debug Powers Mechanics"
Cohesion: 0.40
Nodes (5): Debug Powers & Spell Mechanics Integration Test, Breakpoint Token Spell, Byte Paladin Hero, HotSwap Refactor Spell, Try-Catch Shield Spell

### Community 51 - "Package Build Job Workflow"
Cohesion: 0.50
Nodes (4): name, needs, runs-on, build-packages

### Community 52 - "Multi-Platform Artifact Strategy Matrix"
Cohesion: 0.50
Nodes (4): artifact_pattern, os, platform, include

## Knowledge Gaps
- **75 isolated node(s):** `FIRST_COMMIT`, `CLEAN_CODE`, `ZERO_ALLOCATION`, `CONCURRENCY_WIZARD`, `STACK_TRACE_SURVIVOR` (+70 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 193 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **24 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `GameEngine` connect `Achievements and ANSI Display` to `Combat Animation and Boss Mechanics`, `Combat Configuration and Battle State`, `Study Session and Post-Mortem Report`, `Battle Actions and Sealed Interfaces`, `Combat Input Parsing Parameterized Tests`, `Game Difficulty Configuration`, `Localization and Theme Configuration`, `Enemy Animation Frames and Endless Mode`, `Informational Screens and UI Rendering`, `Combat Configuration Modes Tests`, `Game Engine State and Over Screen Tests`, `Collections and Queue Utilities`?**
  _High betweenness centrality (0.041) - this node is a cross-community bridge._
- **What connects `FIRST_COMMIT`, `CLEAN_CODE`, `ZERO_ALLOCATION` to the rest of the system?**
  _75 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Achievements and ANSI Display` be split into smaller, more focused modules?**
  _Cohesion score 0.05957492486045513 - nodes in this community are weakly interconnected._
- **Why does `Achievement` connect `Achievement Model and Definitions` to `Achievements and ANSI Display`, `Achievement Verification Tests`?**
  _High betweenness centrality (0.040) - this node is a cross-community bridge._
- **Should `Combat Animation and Boss Mechanics` be split into smaller, more focused modules?**
  _Cohesion score 0.05388961321164711 - nodes in this community are weakly interconnected._
- **Why does `Ansi` connect `Achievements and ANSI Display` to `Game Engine State Classes Hierarchy`, `ANSI Text Centering and Formatting`, `Terminal I/O and Compact Mode`, `Localization and Theme Configuration`, `ANSI Stripping and Status Bars`, `Visual Theme Palette Management`, `Game Save and Persistence Manager`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Should `Combat Configuration and Battle State` be split into smaller, more focused modules?**
  _Cohesion score 0.06049213943950786 - nodes in this community are weakly interconnected._