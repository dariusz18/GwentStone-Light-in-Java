# GwentStone Light — Java

A card game engine inspired by Gwent & Hearthstone, built in Java.

## Architecture Overview

| Class | Role |
|---|---|
| `ActionHandler` | Handles all game commands (placement, attacks, hero abilities, win tracking) |
| `Server` | Central game state — board, rows, players, turn management |
| `Card` | Unified card model (heroes + minions merged for simplicity) |
| `CardOutput` | DTO used for JSON response generation |
| `GameBoard` | Board matrix logic — placement, row capacity, mana checks |
| `Decks` | Deck management and empty-deck validation |
| `Player` | Game-logic player (draws cards, tracks mana) |
| `Helpers` | Static utilities — card conversion, attack/freeze state resets |

## Game Flow

1. Players are initialized with their decks, heroes, and starting conditions
2. A `switch` dispatches each input command to `ActionHandler`
3. `ActionHandler` resolves the command using the current `Server` state
4. The game loop continues until a win condition is met

## Design Decisions

- **Hero & Minion merged into `Card`** — avoids casting overhead and simplifies logic
- **`Server` as a state container** — single source of truth for the entire game
- **Static helpers** — reduce instantiation and keep utility logic centralized
