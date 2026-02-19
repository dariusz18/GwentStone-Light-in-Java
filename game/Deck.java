package org.poo.game;

import java.util.ArrayList;

public final class Deck {
    private int nrCardsInDeck;
    private int nrDecks;
    private ArrayList<ArrayList<Card>> decks;

    public Deck(final int nrCardsInDeck,
    final  int nrDecks, final ArrayList<ArrayList<Card>> decks) {
        this.nrCardsInDeck = nrCardsInDeck;
        this.nrDecks = nrDecks;
        this.decks = decks;
    }

    /**
     *
     * @return
     */

    public boolean isEmpty() {
        for (ArrayList<Card> deck : decks) {
            if (!deck.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<ArrayList<Card>> getDecks() {
        return decks;
    }

    public void setDecks(final ArrayList<ArrayList<Card>> decks) {
        this.decks = decks;
    }

    public int getNrCardsInDeck() {
        return nrCardsInDeck;
    }

    public void setNrCardsInDeck(final int nrCardsInDeck) {
        this.nrCardsInDeck = nrCardsInDeck;
    }

    public int getNrDecks() {
        return nrDecks;
    }

    public void setNrDecks(final int nrDecks) {
        this.nrDecks = nrDecks;
    }

    /*
    public ArrayList<String> getNameOfCardsInDeck() {
        ArrayList<String> names = new ArrayList<>();
        for (ArrayList<Card> deck : decks) {
            for (Card card : deck) {
                names.add(card.getName());
            }
        }
        return names;
    }*/

    /**
     *
     * @param index
     * @return
     */

    public ArrayList<Card> getDeckbyIndex(final int index) {
        return decks.get(index);
    }
}
