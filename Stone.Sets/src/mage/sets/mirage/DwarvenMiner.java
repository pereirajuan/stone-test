/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mage.sets.mirage;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DestroyTargetEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.target.Target;
import mage.target.common.TargetNonBasicLandPermanent;

/**
 *
 * @author nick.myers
 */
public class DwarvenMiner extends CardImpl {
    
    public DwarvenMiner(UUID ownerId) {
        super(ownerId, 169, "Dwarven Miner", Rarity.UNCOMMON, new CardType[]{CardType.CREATURE}, "{1}{R}");
        this.expansionSetCode = "MIR";
        this.subtype.add("Dwarf");
        
        this.power = new MageInt(1);
        this.toughness = new MageInt(2);
        
        // {2}{R}, {tap}: Destroy target nonbasic land
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new DestroyTargetEffect(), new ManaCostsImpl("{2}{R}"));
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetNonBasicLandPermanent());
        this.addAbility(ability);
        
    }
    
    public DwarvenMiner(final DwarvenMiner card) {
        super(card);
    }
    
    @Override 
    public DwarvenMiner copy() {
        return new DwarvenMiner(this);
    }

    
    
}
