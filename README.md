# GwentStone Light — Java

> A turn-based card game engine inspired by **Gwent** and **Hearthstone**, implemented in Java with full JSON-driven input/output support.

---

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Game Rules Summary](#game-rules-summary)
- [Card Types](#card-types)
- [Supported Commands](#supported-commands)
- [Design Decisions](#design-decisions)
- [How to Run](#how-to-run)

---

## Overview

GwentStone Light is a server-side simulation of a two-player card game. The game is driven entirely by a JSON input file that contains player decks, hero assignments, game start conditions, and a sequence of actions. The engine processes these actions turn by turn, producing a JSON output that reflects every game event, debug query, or error encountered.

The project was developed as part of the **OOP (Object-Oriented Programming)** course at **Politehnica University of Bucharest**, and is designed to demonstrate solid OOP principles including encapsulation, separation of concerns, and single-responsibility design.

---

## Project Structure

```
src/
├── org.poo.fileio/          # Input parsing classes (mapped from JSON)
│   ├── ActionsInput.java
│   ├── CardInput.java
│   ├── Coordinates.java
│   ├── DecksInput.java
│   ├── GameInput.java
│   ├── Input.java
│   └── StartGameInput.java
│
├── org.poo.game/            # Core game logic
│   ├── ActionHandler.java   # Dispatches and executes all game commands
│   ├── Card.java            # Unified card model (minion + hero)
│   ├── CardOutput.java      # DTO for JSON serialization
│   ├── Deck.java            # Deck storage and access
│   ├── GameBoard.java       # 4×5 board matrix with placement logic
│   ├── Gameplay.java        # Game loop utilities
│   ├── Helpers.java         # Static utility methods
│   ├── Hero.java            # Hero-specific logic
│   ├── Minion.java          # Minion-specific logic
│   ├── Player.java          # Player state (hand, mana, turn flag)
│   └── Server.java          # Central game state container
│
└── org.poo.main/
    └── Main.java            # Entry point — reads input, runs game, writes output
```

---

## Architecture Overview

| Class | Role |
|---|---|
| `ActionHandler` | Handles all game commands: card placement, attacks, hero abilities, win condition tracking |
| `Server` | Central game state — manages the board, players, turn order, round count, and mana |
| `Card` | Unified card model — heroes and minions share a single class to reduce complexity |
| `CardOutput` | Data Transfer Object (DTO) used exclusively for JSON response serialization |
| `GameBoard` | Encapsulates the 4×5 board matrix, card placement, row capacity checks, and left-shift on death |
| `Deck` | Manages a player's card collection and supports indexed access after shuffling |
| `Player` | Tracks a player's hand, mana pool, hero, turn status, and win count |
| `Helpers` | Static utility class — handles card conversion, frozen/attack flag resets |

---

## Game Rules Summary

### Board Layout

The game board is a **4×5 matrix**. Each player controls two rows:

```
Row 0  ←─ Player 2 (back row)
Row 1  ←─ Player 2 (front row)
──────────────────────────────
Row 2  ←─ Player 1 (front row)
Row 3  ←─ Player 1 (back row)
```

- Cards are placed from left to right within a row (max 5 cards per row).
- When a card is killed, all cards to its right shift one position left.

### Turn & Round System

- Each round consists of two turns — one per player.
- At the start of each round, both players draw one card and receive mana.
- Mana starts at **1** in round 1 and increases by 1 each round, capping at **10**.
- Mana accumulates: unused mana carries over to the next round.
- A player ends their turn explicitly via the `endPlayerTurn` command.
- At end of a player's turn, **frozen** status is cleared from their own cards.

### Win Condition

The game ends immediately when a hero's health drops to **0 or below**. The player who delivered the killing blow wins, and a `"gameEnded"` message is added to the output.

---

## Card Types

### Minions

Standard combat units placed on the board. Each has: `mana`, `health`, `attackDamage`, `description`, `colors`, and `name`.

| Card | Placement | Special |
|---|---|---|
| `Sentinel` | Back row | — |
| `Berserker` | Back row | — |
| `Goliath` | Front row | **Tank** — must be attacked first |
| `Warden` | Front row | **Tank** — must be attacked first |
| `The Ripper` | Front row | Ability: *Weak Knees* — reduces enemy attack by 2 (min 0) |
| `Miraj` | Front row | Ability: *Skyjack* — swaps its health with an enemy's health |
| `The Cursed One` | Back row | Ability: *Shapeshift* — swaps an enemy's attack and health values |
| `Disciple` | Back row | Ability: *God's Plan* — grants +2 health to a friendly minion |

### Heroes

Each player has one hero with **30 HP**. Heroes can use abilities once per turn by spending mana.

| Hero | Ability | Target |
|---|---|---|
| `Lord Royce` | *Sub-Zero* — freezes all cards on a row | Enemy row |
| `Empress Thorina` | *Low Blow* — destroys the card with the highest health on a row | Enemy row |
| `King Mudface` | *Earth Born* — grants +1 health to all cards on a row | Friendly row |
| `General Kocioraw` | *Blood Thirst* — grants +1 attack to all cards on a row | Friendly row |

---

## Supported Commands

### Game Actions

| Command | Description |
|---|---|
| `placeCard` | Places a card from hand onto the board |
| `endPlayerTurn` | Ends the current player's turn |
| `cardUsesAttack` | A minion attacks an enemy minion |
| `cardUsesAbility` | A minion uses its special ability |
| `useAttackHero` | A minion attacks the enemy hero |
| `useHeroAbility` | The active hero uses its ability on a row |

### Debug Queries

| Command | Description |
|---|---|
| `getPlayerDeck` | Returns the current deck of a player |
| `getCardsInHand` | Returns all cards in a player's hand |
| `getCardsOnTable` | Returns all cards currently on the board |
| `getCardAtPosition` | Returns the card at a specific board position |
| `getFrozenCardsOnTable` | Returns all frozen cards on the board |
| `getPlayerHero` | Returns a player's hero and its current state |
| `getPlayerTurn` | Returns which player's turn it currently is |
| `getPlayerMana` | Returns a player's current mana |

### Statistics

| Command | Description |
|---|---|
| `getPlayerOneWins` | Returns the total wins for player 1 |
| `getPlayerTwoWins` | Returns the total wins for player 2 |
| `getTotalGamesPlayed` | Returns the total number of completed games |

---

## Design Decisions

### Unified `Card` Model
Heroes and minions are represented by a single `Card` class rather than separate class hierarchies. This avoids unnecessary casting and simplifies board logic, since the board only ever needs to store and query card state — not distinguish between card types at the storage level.

### `Server` as State Container
The `Server` class acts as a single source of truth for the entire game. It holds references to both players, the game board, round/turn counters, and mana state. Passing `Server` into `ActionHandler` methods keeps game state centralized and avoids global variables.

### Static `ActionHandler` and `Helpers`
Both classes are stateless utility classes with only static methods. This keeps the main game loop in `Main.java` clean and readable — each command maps to exactly one handler call — while avoiding unnecessary object instantiation.

### JSON-Driven Design
The engine is fully decoupled from any UI. Input and output are pure JSON, processed via the **Jackson** library. This makes the engine easy to test, extend, and integrate — the `CardOutput` DTO ensures internal card state is never directly serialized, giving full control over the output format.

### Deck Immutability Between Games
Player decks are stored in their original unshuffled form. Before each game, a copy is shuffled using a provided seed and a freshly instantiated `Random` object, ensuring deterministic and reproducible results without mutating the source deck.

---

## How to Run

1. Open the project in **IntelliJ IDEA** from the directory containing `src/`, `lib/`, `ref/`, and `input/`.
2. Ensure the `.iml` file points correctly to the `lib/` folder containing the Jackson JARs.
3. Run `Main.java` — it will automatically process all test files from the `input/` directory and write results to `output/`.
4. Use the checker via `Checker.calculateScore()` to validate output against reference files.

> **Note:** If you encounter issues with the IDE configuration, delete the `.idea/` folder and re-import the project from scratch in IntelliJ.
