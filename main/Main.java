package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.checker.Checker;
import org.poo.fileio.*;
import org.poo.game.*;
import org.poo.checker.CheckerConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(
                CheckerConstants.TESTS_PATH + filePath1), Input.class);

        ArrayNode output = objectMapper.createArrayNode();

        Player player1 = new Player();
        Player player2 = new Player();
        Server server = new Server(player1, player2);
        //populam deckurile jucatorilor cu inputul primit

        server.getPlayer1().getDeck().setNrDecks(inputData.getPlayerOneDecks().getNrDecks());
        server.getPlayer1().getDeck().setNrCardsInDeck(inputData.getPlayerOneDecks().getNrCardsInDeck());

        server.getPlayer2().getDeck().setNrDecks(inputData.getPlayerTwoDecks().getNrDecks());
        server.getPlayer2().getDeck().setNrCardsInDeck(inputData.getPlayerTwoDecks().getNrCardsInDeck());

        server.setPlayerOneDecks(inputData.getPlayerOneDecks());
        server.setPlayerTwoDecks(inputData.getPlayerTwoDecks());


        for (GameInput game : inputData.getGames()) {
            StartGameInput startGame = game.getStartGame();
            ArrayList<ActionsInput> actions = game.getActions();
            //aici setam eroii jucatorilor
            player1.setPlayerHero(new Card(startGame.getPlayerOneHero()));
            player1.getPlayerHero().setHealth(30);
            player2.setPlayerHero(new Card(startGame.getPlayerTwoHero()));
            player2.getPlayerHero().setHealth(30);

            //aici luam indexul deckului la care ne aflam
            int playerOneDeckIdx = startGame.getPlayerOneDeckIdx();
            int playerTwoDeckIdx = startGame.getPlayerTwoDeckIdx();
            //seedul pt shuffle
            int shuffleSeed = startGame.getShuffleSeed();
            server.shuffleDeck(server.getPlayer1().getDeck(), shuffleSeed, playerOneDeckIdx);
            server.shuffleDeck(server.getPlayer2().getDeck(), shuffleSeed, playerTwoDeckIdx);
            //aici vad care jucator incepe si ii setez campul myTurn
            //myTurn = true => e randul acelui jucator
            int startingPlayer = startGame.getStartingPlayer();
            if (startingPlayer == 1) {
                server.getPlayer1().setMyTurn(true);
            } else {
                server.getPlayer2().setMyTurn(true);
            }

            server.getPlayer1().drawCard(playerOneDeckIdx);
            server.getPlayer2().drawCard(playerTwoDeckIdx);

            server.setRoundMana(1);

            server.getPlayer1().setMana(server.getRoundMana());
            server.getPlayer2().setMana(server.getRoundMana());

            for (int i = 0; i < actions.size(); i++) {
                ActionsInput action = actions.get(i);
                ObjectNode actionNode = null;

                switch (action.getCommand()) {
                    case "playerOneDecks":
                        actionNode = ActionHandler.handlePlayerOneDecks();
                        break;
                    case "playCard":
                        actionNode = ActionHandler.handlePlayCard(action);
                        break;
                    case "getPlayerDeck":
                        actionNode = ActionHandler.handleGetPlayerDeck(server, action,
                                playerOneDeckIdx, playerTwoDeckIdx, shuffleSeed);
                        break;
                    case "getPlayerHero":
                        actionNode = ActionHandler.handleGetPlayerHero(server, action);
                        break;
                    case "getPlayerTurn":
                        actionNode = ActionHandler.handleGetPlayerTurn(server);
                        break;
                    case "getPlayerOneWins":
                        actionNode = ActionHandler.handleGetPlayerOneWins(server);
                        break;
                    case "getPlayerTwoWins":
                        actionNode = ActionHandler.handleGetPlayerTwoWins(server);
                        break;
                    case "getTotalGamesPlayed":
                        actionNode = ActionHandler.handleGetTotalGamesPlayed(server);
                        break;
                    case "getPlayerMana":
                        actionNode = ActionHandler.handleGetPlayerMana(server, action);
                        break;
                    case "getCardsOnTable":
                        actionNode = ActionHandler.handleGetCardsOnTable(server, action);
                        break;
                    case "getCardsInHand":
                        actionNode = ActionHandler.handleGetCardsInHand(server, action);
                        break;
                    case "cardUsesAttack":
                        actionNode = ActionHandler.handleAttack(server, action);
                        break;
                    case "placeCard":
                        actionNode = ActionHandler.handlePlaceCard(server, action);
                        break;
                    case "endPlayerTurn":
                        ActionHandler.handleEndPlayerTurn(server, playerOneDeckIdx, playerTwoDeckIdx,
                                startingPlayer);
                        break;
                    case "getCardAtPosition":
                        actionNode = ActionHandler.handleGetCardAtThisPosition(server, action);
                        break;
                    case "cardUsesAbility":
                        actionNode = ActionHandler.handleCardUsesAbility(server, action);
                        break;
                    case "useAttackHero":
                        actionNode = ActionHandler.handleUseAttackHero(server, action);
                        break;
                    case "useHeroAbility":
                        actionNode = ActionHandler.handleUseHeroAbility(server, action);
                        break;
                    case "getFrozenCardsOnTable":
                        actionNode = ActionHandler.handleGetFrozenCardsOnTable(server);
                        break;
                    default:
                        actionNode = objectMapper.createObjectNode();
                        actionNode.put("result", "Unknown command: " + action.getCommand());
                }
                if (actionNode != null) {
                    output.add(actionNode);
                }
            }
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath2), output);
    }
}
