package org.poo.game;

import org.poo.fileio.CardInput;
import org.poo.fileio.DecksInput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Server {
    private Player player1;
    private Player player2;
    private int gamesPlayed;
    private GameBoard gameBoard;
    private int roundMana;


    public Server(final Player player1, final Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gamesPlayed = 0;
        this.gameBoard = new GameBoard();

        this.roundMana = 1;
//        this.isPlayerOneStarting = true;

        player1.setDeck(new Deck(0, 0, new ArrayList<>()));
        player2.setDeck(new Deck(0, 0, new ArrayList<>()));
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer1(final Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(final Player player2) {
        this.player2 = player2;
    }

    public void setGamesPlayed(final int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getRoundMana() {
        return roundMana;
    }

    public void setRoundMana(final int roundMana) {
        this.roundMana = roundMana;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     *
     * @param decksInput
     */

    public void setPlayerOneDecks(final DecksInput decksInput) {
        ArrayList<ArrayList<Card>> decks = convertCardInputToCard(decksInput);
        this.player1.setDeck(new Deck(decksInput.getNrCardsInDeck(), decksInput.getNrDecks(), decks));
    }

    public void setPlayerTwoDecks(final DecksInput decksInput) {
        ArrayList<ArrayList<Card>> decks = convertCardInputToCard(decksInput);
        this.player2.setDeck(new Deck(decksInput.getNrCardsInDeck(), decksInput.getNrDecks(), decks));
    }

//    public void setPlayerOneHero(CardInput hero) {
//        player1.setHero(convertHeroInputToHero(hero));
//    }
//
//    public void setPlayerTwoHero(CardInput hero) {
//        player2.setHero(convertHeroInputToHero(hero));
//    }
//
//    // Metode pentru conversii între tipurile CardInput și Card/Hero
//    private static Hero convertHeroInputToHero(CardInput heroInput) {
//        return new Hero(heroInput);
//    }

    /**
     *
     * @param decksInput
     * @return
     */

    private static ArrayList<ArrayList<Card>> convertCardInputToCard(final DecksInput decksInput) {
        ArrayList<ArrayList<Card>> decks = new ArrayList<>();
        for (ArrayList<CardInput> deckInput : decksInput.getDecks()) {
            ArrayList<Card> deck = new ArrayList<>();
            for (CardInput cardInput : deckInput) {
                deck.add(new Card(cardInput));
            }
            decks.add(deck);
        }
        return decks;
    }

    /**
     *
     * @param deck
     * @param seed
     * @param idxDeck
     */

    public void shuffleDeck(final Deck deck,
                            final int seed, final int idxDeck) {
        Random random = new Random(seed);
        ArrayList<Card> deckList = deck.getDecks().get(idxDeck);
        Collections.shuffle(deckList, random);
    }

    public boolean hasSpaceOnRow(final int row) {
        return !gameBoard.isRowFull(row);
    }

    public int turnCount = 0;

    /**
     *
     * @param playerOneDeckIndex
     * @param playerTwoDeckIndex
     * @param startingPlayer
     */

    public void startNewRound(final int playerOneDeckIndex,
                              final int playerTwoDeckIndex, final int startingPlayer) {
         turnCount = 0;
        player1.drawCard(playerOneDeckIndex);
        player2.drawCard(playerTwoDeckIndex);

        roundMana = Math.min(roundMana + 1, 10);
        player1.setMana(player1.getMana() + roundMana);
        player2.setMana(player2.getMana() + roundMana);

        if (startingPlayer == 1) {
            player1.setMyTurn(true);
            player2.setMyTurn(false);
        } else {
            player1.setMyTurn(false);
            player2.setMyTurn(true);
        }
    }
}
