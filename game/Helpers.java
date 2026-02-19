package org.poo.game;

import java.util.ArrayList;

public final class Helpers {
    private static final int TREI = 3;
    public static int getRowForCardType(final Server server,
    final Card card, final Player player) {
        boolean isFrontRowCard = card.getName().equals("Goliath") || card.getName().equals("Warden")
                || card.getName().equals("The Ripper") || card.getName().equals("Miraj");
        if (player == server.getPlayer1()) {
            return isFrontRowCard ? 2 : TREI;
        } else {
            return isFrontRowCard ? 1 : 0;
        }
    }

    /**
     *
     * @param c
     * @return
     */

    public static CardOutput cardToCardOutput(final Card c) {
        return new CardOutput(c.getMana(), c.getAttackDamage(), c.getHealth(), c.getDescription(),
                c.getColors(), c.getName());
    }

    /**
     *
     * @param cards
     * @return
     */

    public static ArrayList<CardOutput> cardsToCardsOutput(final ArrayList<Card> cards) {
        ArrayList<CardOutput> outputs = new ArrayList<>();
        for (Card c : cards) {
            outputs.add(cardToCardOutput(c));
        }
        return outputs;
    }

    public static ArrayList<ArrayList<CardOutput>> arrayCardsToArrayCardsOutput(final ArrayList<ArrayList<Card>> cards) {
        ArrayList<ArrayList<CardOutput>> outputs = new ArrayList<>();
        for (ArrayList<Card> c : cards) {
            outputs.add(cardsToCardsOutput(c));
        }
        return outputs;
    }

    /**
     *
     * @param gameBoard
     * @param start
     * @param end
     */

    public static void resetAttackFlags(final GameBoard gameBoard,
    final int start, final int end) {
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < gameBoard.getBoard()[i].length; j++) {
                Card card = gameBoard.getBoard()[i][j];
                if (card != null) {
                    card.setHasAttackedThisTurn(false);
                }
            }
        }
    }

    /**
     *
     * @param gameBoard
     * @param start
     * @param end
     */

    public static void resetFrozenFlags(final GameBoard gameBoard,
     final int start, final int end) {
        for (int i = start; i <= end; i++) {
            for (int j = 0; j < gameBoard.getBoard()[i].length; j++) {
                Card card = gameBoard.getBoard()[i][j];
                if (card != null) {
                    card.setFrozen(false);
                }
            }
        }
    }

    /**
     *
     * @param row
     * @param removeIndex
     */

    public static void removeCardAndShiftLeft(final Card[] row,
     final int removeIndex) {
        row[removeIndex] = null;
        for (int i = removeIndex; i < row.length - 1; i++) {
            row[i] = row[i + 1];
        }
        row[row.length - 1] = null;
    }


}
