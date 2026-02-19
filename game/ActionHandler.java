package org.poo.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.ActionsInput;

import java.util.ArrayList;
import com.fasterxml.jackson.databind.node.ArrayNode;


import static org.poo.game.Helpers.*;

public final class ActionHandler {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int LINIE = 3;
    private static final int COLOANA = 4;
    private static final int PATRU = 4;
    private static final int DOI = 2;
    private static final int CINCI = 5;

    private ActionHandler() { }

    /**
     * Handles the action of setting player one decks.
     *
     * @return JSON node representing the action result
     */
    public static ObjectNode handlePlayerOneDecks() {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("result", "Player 1 decks set");
        return actionNode;
    }

    /**
     * Handles the action of playing a card.

     *
     * @param action the action input containing details
     * @return JSON node representing the action result
     */
    public static ObjectNode handlePlayCard(final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("result", "Player " + action.getPlayerIdx()
                + " plays a card from hand index " + action.getHandIdx());
        return actionNode;
    }

    /**
     * Handles the action of attacking another card.
     *
     * @param server the game server managing the state

     * @param action the action input containing details
     * @return JSON node representing the action result
     */
    public static ObjectNode handleAttack(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
        actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
        actionNode.put("command", "cardUsesAttack");

        int attackerX = action.getCardAttacker().getX();
        int attackerY = action.getCardAttacker().getY();
        int attackedX = action.getCardAttacked().getX();
        int attackedY = action.getCardAttacked().getY();

        Card attackerCard = server.getGameBoard().getBoard()[attackerX][attackerY];
        Card attackedCard = server.getGameBoard().getBoard()[attackedX][attackedY];

        if (server.getPlayer1().isMyTurn()) {
            if (attackedX >= 2) {
                actionNode.put("error", "Attacked card does not belong to the enemy.");
                return actionNode;
            }
        } else {
            if (attackedX < 2) {
                actionNode.put("error", "Attacked card does not belong to the enemy.");
                return actionNode;
            }
        }

        if (attackerCard.isHasAttackedThisTurn()) {
            actionNode.put("error", "Attacker card has already attacked this turn.");
            return actionNode;
        }

        if (attackerCard.isFrozen()) {
            actionNode.put("error", "Attacker card is frozen.");
            return actionNode;
        }

        boolean tankExists = false;
        int startRow = server.getPlayer1().isMyTurn() ? 0 : 2;
        int endRow = startRow + 2;

        for (int i = startRow; i < endRow; i++) {
            for (Card c : server.getGameBoard().getBoard()[i]) {
                if (c != null && (c.getName().equals("Goliath") || c.getName().equals("Warden"))) {
                    tankExists = true;
                    break;
                }
            }
            if (tankExists) {
                break;
            }
        }
        if (attackedCard == null) {
            actionNode.put("error", "TEST14");
            return actionNode;
        }
        if (tankExists && !(attackedCard.getName().equals("Goliath")
                || attackedCard.getName().equals("Warden"))) {
            actionNode.put("error", "Attacked card is not of type 'Tank'.");
            return actionNode;
        }

//        -------------ATACUL IMPLEMETARE--------------
        attackedCard.setHealth(attackedCard.getHealth() - attackerCard.getAttackDamage());

        if (attackedCard.getHealth() <= 0) {
            server.getGameBoard().getBoard()[attackedX][attackedY] = null;
        }

        attackerCard.setHasAttackedThisTurn(true);

        return null;
    }

    /**
     * @param server server

     * @param action action
     * @param playerOneDeckIdx player
     * @param playerTwoDeckIdx player
     * @param shuffleSeed shuffle
     * @return return
     */

    public static ObjectNode handleGetPlayerDeck(final Server server, final ActionsInput action,
    final int playerOneDeckIdx, final int playerTwoDeckIdx, final int shuffleSeed) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getPlayerDeck");
        actionNode.put("playerIdx", action.getPlayerIdx());

        Deck deck;
        if (action.getPlayerIdx() == 1) {
            deck = server.getPlayer1().getDeck();
            ArrayList<Card> deck1 = deck.getDeckbyIndex(playerOneDeckIdx);
            actionNode.put("output", OBJECT_MAPPER.valueToTree(cardsToCardsOutput(deck1)));
        } else {
            deck = server.getPlayer2().getDeck();
            ArrayList<Card> deck2 = deck.getDeckbyIndex(playerTwoDeckIdx);
            actionNode.put("output", OBJECT_MAPPER.valueToTree(cardsToCardsOutput(deck2)));
        }
        return actionNode;
    }

    /**
     *
     * @param server server
     * @param action action

     * @return return
     */

    public static ObjectNode handleGetPlayerHero(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getPlayerHero");
        actionNode.put("playerIdx", action.getPlayerIdx());

        Card hero = (action.getPlayerIdx() == 1)
                ? server.getPlayer1().getPlayerHero()
                : server.getPlayer2().getPlayerHero();

        ObjectNode heroNode = OBJECT_MAPPER.createObjectNode();
        heroNode.put("mana", hero.getMana());
        heroNode.put("description", hero.getDescription());
        heroNode.set("colors", OBJECT_MAPPER.valueToTree(hero.getColors()));
        heroNode.put("name", hero.getName());
        heroNode.put("health", hero.getHealth());

        actionNode.set("output", heroNode);
        return actionNode;
    }

    /**
     *
     * @param server server

     * @return
     */

    public static ObjectNode handleGetPlayerTurn(final Server server) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getPlayerTurn");
        int currentPlayerTurn = server.getPlayer1().isMyTurn() ? 1 : 2;
        actionNode.put("output", currentPlayerTurn);
        return actionNode;
    }

    /**
     *
     * @param server server
     * @return return

     */

    public static ObjectNode handleGetPlayerOneWins(final Server server) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getPlayerOneWins");
        Card hero = server.getPlayer1().getPlayerHero();
        if (hero.getHealth() <= 0) {
            server.getPlayer1().setWins(server.getPlayer1().getWins() + 1);
        }
        actionNode.put("output",server.getPlayer1().getWins());
        return actionNode;
    }


    /**
     *
     * @param server server

     * @return return
     */

    public static ObjectNode handleGetPlayerTwoWins(final Server server) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getPlayerTwoWins");
        Card hero = server.getPlayer2().getPlayerHero();
        if (hero.getHealth() <= 0) {
            server.getPlayer1().setWins(server.getPlayer2().getWins() + 1);
        }
        actionNode.put("output", server.getPlayer2().getWins());
        return actionNode;
    }

    /**
     *
     * @param server server
     * @return return
     */

    public static ObjectNode handleGetTotalGamesPlayed(final Server server) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getTotalGamesPlayed");
        Card hero1 = server.getPlayer1().getPlayerHero();
        Card hero2 = server.getPlayer2().getPlayerHero();
        if ((hero1.getHealth() <= 0) || (hero2.getHealth() <= 0)) {
            server.setGamesPlayed(server.getGamesPlayed() + 1);
        }

        actionNode.put("output", server.getGamesPlayed());
        return actionNode;
    }

    /**
     *
     * @param server server

     * @param action action
     * @return return
     */

    public static ObjectNode handleGetPlayerMana(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getPlayerMana");
        actionNode.put("playerIdx", action.getPlayerIdx());

        int mana = (action.getPlayerIdx() == 1)
                ? server.getPlayer1().getMana() : server.getPlayer2().getMana();
        actionNode.put("output", mana);
        return actionNode;
    }

    /**
     *
     * @param server server
     * @param action action

     * @return return
     */

    public static ObjectNode handleGetCardsOnTable(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getCardsOnTable");

        ArrayList<ArrayList<Card>> outputCards = new ArrayList<>();
        for (int i = 0; i <= LINIE; i++) {
            ArrayList<Card> row = new ArrayList<>();
            for (int j = 0; j <= COLOANA; j++) {
                if (server.getGameBoard().getBoard()[i][j] != null) {
                    row.add(server.getGameBoard().getBoard()[i][j]);
                }
            }
            outputCards.add(row);
        }

        actionNode.set("output",
                OBJECT_MAPPER.valueToTree(arrayCardsToArrayCardsOutput(outputCards)));
        return actionNode;
    }

    /**
     *
     * @param server
     * @param action
     * @return
     */

    public static ObjectNode handleGetCardsInHand(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getCardsInHand");
        actionNode.put("playerIdx", action.getPlayerIdx());

        ArrayList<Card> handCards = (action.getPlayerIdx() == 1)
                ? server.getPlayer1().getHand() : server.getPlayer2().getHand();
        actionNode.set("output", OBJECT_MAPPER.valueToTree(cardsToCardsOutput(handCards)));
        return actionNode;
    }

    /**
     *
     * @param action
     * @return
     */

    public static ObjectNode handleCardUsesAttack(final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "cardUsesAttack");
        actionNode.put("output", "implementare card uses attack");
        return actionNode;
    }

    /**
     *

     * @param server
     * @param action
     * @return
     */

    public static ObjectNode handlePlaceCard(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "placeCard");

        Player currentPlayer = server.getPlayer1().isMyTurn()
                ? server.getPlayer1() : server.getPlayer2();
        if (currentPlayer.getHand().isEmpty()) {
            actionNode.put("error", "TEST14");
            return actionNode;
        }
        Card cardToPlace = currentPlayer.getHand().get(action.getHandIdx());

        if (cardToPlace.getMana() > currentPlayer.getMana()) {
            actionNode.put("error", "Not enough mana to place card on table.");
            actionNode.put("handIdx", action.getHandIdx());
            return actionNode;
        }

        int row = getRowForCardType(server, cardToPlace, currentPlayer);

        if (!server.hasSpaceOnRow(row)) {
            actionNode.put("error", "Cannot place card on table since row is full.");
            actionNode.put("handIdx", action.getHandIdx());
            return actionNode;
        }

        boolean placed = server.getGameBoard().placeCard(cardToPlace, row, currentPlayer.getMana());
        if (placed) {
            currentPlayer.setMana(currentPlayer.getMana() - cardToPlace.getMana());
            currentPlayer.getHand().remove(action.getHandIdx());
            return null; // Card placed successfully.
        } else {
            actionNode.put("error", "Failed to place card on table.");
        }
        actionNode.put("handIdx", action.getHandIdx());

        return actionNode;
    }

    /**
     *
     * @param server
     * @param playerOneDeckIndex
     * @param playerTwoDeckIndex

     * @param startingPlayer
     */

    public static void handleEndPlayerTurn(final Server server, final int playerOneDeckIndex,
                                           final int playerTwoDeckIndex, final int startingPlayer) {
        if (server.getPlayer1().isMyTurn()) {
            resetFrozenFlags(server.getGameBoard(), 2, LINIE);
            resetAttackFlags(server.getGameBoard(), 2, LINIE);
            server.getPlayer1().getPlayerHero().setHasAttackedThisTurn(false);
            server.getPlayer1().setMyTurn(false);
            server.getPlayer2().setMyTurn(true);
        } else {
            resetFrozenFlags(server.getGameBoard(), 0, 1);
            resetAttackFlags(server.getGameBoard(), 0, 1);
            server.getPlayer2().getPlayerHero().setHasAttackedThisTurn(false);
            server.getPlayer2().setMyTurn(false);
            server.getPlayer1().setMyTurn(true);
        }
        server.turnCount++;

        if (server.turnCount == 2) {
            server.startNewRound(playerOneDeckIndex, playerTwoDeckIndex, startingPlayer);
        }
    }

    /**

     *
     * @param server
     * @param action
     * @return

     */

    public static ObjectNode handleGetCardAtThisPosition(final Server server,
                                                         final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getCardAtPosition");

        int x = action.getX();
        int y = action.getY();

        Card card = server.getGameBoard().getBoard()[x][y];

        if (card != null) {
            ObjectNode cardNode = OBJECT_MAPPER.createObjectNode();
            cardNode.put("mana", card.getMana());
            cardNode.put("attackDamage", card.getAttackDamage());
            cardNode.put("health", card.getHealth());
            cardNode.put("description", card.getDescription());
            cardNode.set("colors", OBJECT_MAPPER.valueToTree(card.getColors()));
            cardNode.put("name", card.getName());

            actionNode.set("output", cardNode);
        } else {
            actionNode.put("output", "No card available at that position.");
        }
        actionNode.put("x", action.getX());
        actionNode.put("y", action.getY());
        return actionNode;
    }

    /**
     *
     * @param server
     * @return
     */

    public static ObjectNode handleGetFrozenCardsOnTable(final Server server) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();
        actionNode.put("command", "getFrozenCardsOnTable");
        ArrayNode frozenCards = OBJECT_MAPPER.createArrayNode();

        for (int i = 0; i < PATRU; i++) {
            for (int j = 0; j < CINCI; j++) {

                Card card = server.getGameBoard().getBoard()[i][j];
                if (card != null && card.isFrozen()) {
                    ObjectNode cardNode = OBJECT_MAPPER.createObjectNode();
                    cardNode.put("mana", card.getMana());
                    cardNode.put("attackDamage", card.getAttackDamage());
                    cardNode.put("health", card.getHealth());
                    cardNode.put("description", card.getDescription());

                    ArrayNode colors = OBJECT_MAPPER.createArrayNode();
                    for (String color : card.getColors()) {
                        colors.add(color);
                    }
                    cardNode.put("colors", colors);
                    cardNode.put("name", card.getName());
                    frozenCards.add(cardNode);
                }
            }
        }

        actionNode.put("output", frozenCards);
        return actionNode;
    }


    /**
     *
     * @param server
     * @param action
     * @return
     */

    public static ObjectNode handleCardUsesAbility(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();

        int attackerX = action.getCardAttacker().getX();
        int attackerY = action.getCardAttacker().getY();
        int attackedX = action.getCardAttacked().getX();
        int attackedY = action.getCardAttacked().getY();

        Card attackerCard = server.getGameBoard().getBoard()[attackerX][attackerY];
        Card attackedCard = server.getGameBoard().getBoard()[attackedX][attackedY];
        if (attackerCard == null) {
            actionNode.put("error", "TEST14");
            return actionNode;
        }
        if (attackerCard.isFrozen()) {
            actionNode.put("command", "cardUsesAbility");
            actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
            actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
            actionNode.put("error", "Attacker card is frozen.");
            return actionNode;
        }

// caz2
        if (attackerCard.isHasAttackedThisTurn()) {
            actionNode.put("command", "cardUsesAbility");
            actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
            actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
            actionNode.put("error", "Attacker card has already attacked this turn.");
            return actionNode;
        }

// caz3
        if (attackerCard.getName().equals("Disciple")) {
            if (server.getPlayer1().isMyTurn() && attackedX < 2) { //!!! sa verific ca nu e disciple
                actionNode.put("command", "cardUsesAbility");
                actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
                actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
                actionNode.put("error", "Attacked card does not belong to the current player.");
                return actionNode;
            }
            if (server.getPlayer2().isMyTurn() && attackedX >= 2) {
                actionNode.put("command", "cardUsesAbility");
                actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
                actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
                actionNode.put("error", "Attacked card does not belong to the current player.");
                return actionNode;
            }
        }

// caz4
        if (attackerCard.getName().equals("The Ripper")
                || attackerCard.getName().equals("Miraj")
                || attackerCard.getName().equals("The Cursed One")) {

            if (server.getPlayer1().isMyTurn() && attackedX >= 2) {
                actionNode.put("command", "cardUsesAbility");
                actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
                actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
                actionNode.put("error", "Attacked card does not belong to the enemy.");
                return actionNode;
            }
            if (server.getPlayer2().isMyTurn() && attackedX < 2) {
                actionNode.put("command", "cardUsesAbility");
                actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
                actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
                actionNode.put("error", "Attacked card does not belong to the enemy.");
                return actionNode;
            }

            boolean tankExists = false;
            if (server.getPlayer1().isMyTurn()) {
                for (int i = 0; i < DOI; i++) {
                    for (Card c : server.getGameBoard().getBoard()[i]) {
                        if (c != null) {
                            if (c.getName().equals("Goliath") || c.getName().equals("Warden")) {
                                tankExists = true;
                                break;
                            }
                        }
                    }
                    if (tankExists) {
                        break;
                    }
                }

            } else {
                for (int i = 2; i < PATRU; i++) {
                    for (Card c : server.getGameBoard().getBoard()[i]) {
                        if (c != null) {
                            if (c.getName().equals("Goliath") || c.getName().equals("Warden")) {
                                tankExists = true;
                                break;
                            }
                        }
                    }
                    if (tankExists) {
                        break;
                    }
                }

            }

            if (attackedCard == null) {
                actionNode.put("error", "TEST14");
                return actionNode;
            }
            if (tankExists && !(attackedCard.getName().equals("Goliath")
                    || attackedCard.getName().equals("Warden"))) {
                actionNode.put("command", "cardUsesAbility");
                actionNode.put("cardAttacked", OBJECT_MAPPER.valueToTree(action.getCardAttacked()));
                actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
                actionNode.put("error", "Attacked card is not of type 'Tank'.");
                return actionNode;
            }
        }

        //-------------ABILITY IMPLEMETARE--------------

        switch (attackerCard.getName()) {
            case "Disciple":
                attackedCard.setHealth(attackedCard.getHealth() + 2);
                break;
            case "The Ripper":
                attackedCard.setAttackDamage(Math.max(attackedCard.getAttackDamage() - 2, 0));
                break;
            case "Miraj":
                int auxHealth = attackedCard.getHealth();
                attackedCard.setHealth(attackerCard.getHealth());
                attackerCard.setHealth(auxHealth);
                break;
            case "The Cursed One":
                int auxAttack = attackedCard.getAttackDamage();
                attackedCard.setAttackDamage(attackedCard.getHealth());
                attackedCard.setHealth(auxAttack);
                if (attackedCard.getAttackDamage() <= 0) {
                    server.getGameBoard().getBoard()[attackedX][attackedY] = null;
                }
                break;
            default:
                actionNode.put("error", "Unknown ability.");
                return actionNode;

        }

        attackerCard.setHasAttackedThisTurn(true);

        return null;
    }

    /**

     *
     * @param server
     * @param action
     * @return

     */

    public static ObjectNode handleUseAttackHero(final Server server, final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();

        int attackerX = action.getCardAttacker().getX();
        int attackerY = action.getCardAttacker().getY();

        Card attackerCard = server.getGameBoard().getBoard()[attackerX][attackerY];

        // caz1
        if (attackerCard == null) {
            actionNode.put("error", "TESET14");
            return actionNode;
        }
        if (attackerCard.isFrozen()) {
            actionNode.put("command", "useAttackHero");
            actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
            actionNode.put("error", "Attacker card is frozen.");
            return actionNode;
        }

        // caz2
        if (attackerCard.isHasAttackedThisTurn()) {
            actionNode.put("command", "useAttackHero");
            actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
            actionNode.put("error", "Attacker card has already attacked this turn.");
            return actionNode;
        }
        // caz3
        boolean tankExists = false;
        if (server.getPlayer1().isMyTurn()) {
            for (int i = 0; i < 2; i++) {
                for (Card c : server.getGameBoard().getBoard()[i]) {
                    if (c != null) {
                        if (c.getName().equals("Goliath") || c.getName().equals("Warden")) {
                            tankExists = true;
                            break;
                        }
                    }
                }
                if (tankExists) {
                    break;
                }
            }

        } else {
            for (int i = 2; i < PATRU; i++) {
                for (Card c : server.getGameBoard().getBoard()[i]) {
                    if (c != null) {
                        if (c.getName().equals("Goliath") || c.getName().equals("Warden")) {
                            tankExists = true;
                            break;
                        }
                    }
                }
                if (tankExists) {
                    break;
                }
            }

        }
        if (tankExists) {
            actionNode.put("command", "useAttackHero");
            actionNode.put("cardAttacker", OBJECT_MAPPER.valueToTree(action.getCardAttacker()));
            actionNode.put("error", "Attacked card is not of type 'Tank'.");
            return actionNode;
        }

        //-------------Atac asupra eroului IMPLEMETARE--------------

        Card enemyHero;
        if (server.getPlayer1().isMyTurn()) {
            enemyHero = server.getPlayer2().getPlayerHero();
        } else {
            enemyHero = server.getPlayer1().getPlayerHero();
        }

        enemyHero.setHealth(enemyHero.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setHasAttackedThisTurn(true);

        if (enemyHero.getHealth() <= 0) {
            if (server.getPlayer1().isMyTurn()) {
                actionNode.put("gameEnded",  "Player one killed the enemy hero.");
                return actionNode;
            } else {
                actionNode.put("gameEnded",  "Player two killed the enemy hero.");
                return actionNode;
            }
        }

        return null;
    }

    /**
     *
     * @param server

     * @param action
     * @return
     */

    public static ObjectNode handleUseHeroAbility(final Server server,
                                                  final ActionsInput action) {
        ObjectNode actionNode = OBJECT_MAPPER.createObjectNode();

        int affectedRow = action.getAffectedRow();

        Player currentPLayer = server.getPlayer1().isMyTurn()
                ? server.getPlayer1() : server.getPlayer2();
        Card hero = currentPLayer.getPlayerHero();
        //caz1
        if (currentPLayer.getMana() < hero.getMana()) {
            actionNode.put("command", "useHeroAbility");
            actionNode.put("affectedRow", action.getAffectedRow());
            actionNode.put("error", "Not enough mana to use hero's ability.");
            return actionNode;
        }
        //    System.out.println("HEROOOOOOOOOOOOO "+ hero.isHasAttackedThisTurn());

        //caz2
        if (hero.isHasAttackedThisTurn()) {
            actionNode.put("command", "useHeroAbility");
            actionNode.put("affectedRow", action.getAffectedRow());
            actionNode.put("error", "Hero has already attacked this turn.");
            return actionNode;
        }

        //caz3
        if (hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina")) {
            if (currentPLayer == server.getPlayer1() && affectedRow >= 2) {
                actionNode.put("command", "useHeroAbility");
                actionNode.put("affectedRow", action.getAffectedRow());
                actionNode.put("error", "Selected row does not belong to the enemy.");
                return actionNode;
            }
            if (currentPLayer == server.getPlayer2() && affectedRow < 2) {
                actionNode.put("command", "useHeroAbility");
                actionNode.put("affectedRow", action.getAffectedRow());
                actionNode.put("error", "Selected row does not belong to the enemy.");
                return actionNode;
            }
        }

        //caz4
        if (hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface")) {
            if (currentPLayer == server.getPlayer1() && affectedRow < 2) {
                actionNode.put("command", "useHeroAbility");
                actionNode.put("affectedRow", action.getAffectedRow());
                actionNode.put("error", "Selected row does not belong to the current player.");
                return actionNode;
            }
            if (currentPLayer == server.getPlayer2() && affectedRow >= 2) {
                actionNode.put("command", "useHeroAbility");
                actionNode.put("affectedRow", action.getAffectedRow());
                actionNode.put("error", "Selected row does not belong to the current player.");
                return actionNode;
            }
        }

        hero.setHasAttackedThisTurn(true);
        currentPLayer.setMana(currentPLayer.getMana() - hero.getMana());

        Card[] affectedCards = server.getGameBoard().getBoard()[affectedRow];
        switch (hero.getName()) {
            case "Lord Royce":
                for (int i = 0; i < affectedCards.length; i++) {
                    Card card = affectedCards[i];
                    if (card != null) {
                        card.setFrozen(true);
                    }
                }
                break;
            case "Empress Thorina":
                int maxHealthCardIndex = -1;
                for (int i = 0; i < affectedCards.length; i++) {
                    Card card = affectedCards[i];
                    if (card != null) {
                        if (maxHealthCardIndex == -1
       || card.getHealth() > affectedCards[maxHealthCardIndex].getHealth()) {
                            maxHealthCardIndex = i;
                        }
                    }
                }
                if (maxHealthCardIndex != -1) {
                    removeCardAndShiftLeft(affectedCards, maxHealthCardIndex);
                }
                break;
            case "King Mudface":
                for (int i = 0; i < affectedCards.length; i++) {
                    Card card = affectedCards[i];
                    if (card != null) {
                        card.setHealth(card.getHealth() + 1);
                    }
                }
                break;
            case "General Kocioraw":
                for (int i = 0; i < affectedCards.length; i++) {
                    Card card = affectedCards[i];
                    if (card != null) {
                        card.setAttackDamage(card.getAttackDamage() + 1);
                    }
                }
                break;
            default:
                actionNode.put("error", "Unknown hero ability.");
                return actionNode;
        }
        return null;

    }
}
