/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mage.deck;

import mage.cards.decks.Constructed;
import mage.constants.Rarity;

/**
 *
 * @author LevelX2
 */

public class Pauper extends Constructed {
    public Pauper() {
        super("Constructed - Pauper");

        //TODO: Add only Magic Online sets for pauper

        rarities.add(Rarity.COMMON);

        banned.add("Cloudpost");
        banned.add("Cranial Plating");
        banned.add("Empty the Warrens");
        banned.add("Frantic Search");
        banned.add("Grapeshot");
        banned.add("Invigorate");
        banned.add("Temporal Fissure");


    }
}
