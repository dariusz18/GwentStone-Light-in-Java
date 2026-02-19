package game;

/*
public class Gameplay {
    public ObjectNode convertCardToJson(Card card) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode cardJson = mapper.createObjectNode();

        cardJson.put("name", card.getName());
        cardJson.put("mana", card.getMana());
        cardJson.put("attackDamage", card.getAttackDamage());
        cardJson.put("health", card.getHealth());
        cardJson.put("description", card.getDescription());

        if (card.getColors() != null) {
            cardJson.putPOJO("colors", card.getColors());
        }

        return cardJson;
    }

    public boolean isRowEmpty(ArrayList<Card>row) {
        return row.isEmpty();
    }

//    public void playRound(Player player1, Player player2) {
//        // Implement logic for each round, including validations, attacks, and other mechanics
//        Player jucator1;
//        Player jucator2;
//        if(player1.isMyTurn()) {
//            jucator1 = player1;
//            jucator2 = player2;
//        }
//        else {
//            jucator1 = player2;
//            jucator2 = player1;
//        }
//        if(jucator1.getMana() < 10) {
//            jucator1.setMana(jucator1.getMana() + 1);
//        }
//        jucator1.drawCard();
//        jucator1.setMyTurn(false);
//        jucator2.setMyTurn(true);
//    }
}*/