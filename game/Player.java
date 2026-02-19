package org.poo.game;

import java.util.ArrayList;

public final class Player {
    private Deck deck;
    private int mana;
    private int wins;
    private boolean myTurn;
    private ArrayList<Card> hand = new ArrayList<>();
    private Card playerHero;

    public Card getPlayerHero() {
        return playerHero;
    }

    public void setPlayerHero(final Card playerHero) {
        this.playerHero = playerHero;
    }

    public void setHand(final ArrayList<Card> hand) {
        this.hand = hand;
    }

    public Player(final Deck deck, final int mana,
                  final int wins, final Card playerHero) {
        this.deck = deck;
        this.mana = mana;
        this.wins = wins;
        this.myTurn = false;
        this.playerHero = playerHero;
    }

    public Player() {

    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     *
     * @param deckIndex
     * @return
     */

public boolean drawCard(final int deckIndex) {
    if (!deck.isEmpty() && hand.size() < 10) {
        ArrayList<Card> currentDeck = deck.getDeckbyIndex(deckIndex);
        if (!currentDeck.isEmpty()) {
            Card drawnCard = currentDeck.remove(0);
            hand.add(drawnCard);
            return true;
        }
    }
    return false;
}
    public Deck getDeck() {
        return deck;
    }

    public void setDeck(final Deck deck) {
        this.deck = deck;
    }

//    public Hero getHero() {
//        return hero;
//    }
//
//    public void setHero(Hero hero) {
//        this.hero = hero;
//    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(final int wins) {
        this.wins = wins;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(final boolean myTurn) {
        this.myTurn = myTurn;
    }



}