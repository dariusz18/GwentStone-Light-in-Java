package org.poo.game;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

public final class Card {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;

    private boolean isFrozen;

    private boolean hasAttackedThisTurn;

    private int specialPower;

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(final boolean frozen) {
        isFrozen = frozen;
    }

    public int getSpecialPower() {
        return specialPower;
    }

    public void setSpecialPower(final int specialPower) {
        this.specialPower = specialPower;
    }

    public boolean isHasAttackedThisTurn() {
        return hasAttackedThisTurn;
    }

    public void setHasAttackedThisTurn(final boolean hasAttackedThisTurn) {
        this.hasAttackedThisTurn = hasAttackedThisTurn;
    }

    public Card() {
    }

    public Card(final CardInput cardInput) {
        this.mana = cardInput.getMana();
        this.attackDamage = cardInput.getAttackDamage();
        this.health = cardInput.getHealth();
        this.description = cardInput.getDescription();
        this.colors = cardInput.getColors();
        this.name = cardInput.getName();
        this.hasAttackedThisTurn = false;
        this.isFrozen = false;
        this.specialPower = 0;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}

