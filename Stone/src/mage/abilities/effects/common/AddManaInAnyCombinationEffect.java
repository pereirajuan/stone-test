/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mage.abilities.effects.common;

import java.util.ArrayList;
import java.util.Arrays;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.dynamicvalue.DynamicValue;
import mage.abilities.dynamicvalue.common.StaticValue;
import mage.constants.ColoredManaSymbol;
import mage.game.Game;
import mage.players.Player;
import mage.util.CardUtil;

/**
 *
 * @author LevelX2
 */
public class AddManaInAnyCombinationEffect extends ManaEffect {

    private ArrayList<ColoredManaSymbol> manaSymbols = new ArrayList<>();
    private final DynamicValue amount;

    public AddManaInAnyCombinationEffect(int amount) {
        this(new StaticValue(amount), ColoredManaSymbol.B, ColoredManaSymbol.U, ColoredManaSymbol.R, ColoredManaSymbol.W, ColoredManaSymbol.G);
    }

    public AddManaInAnyCombinationEffect(int amount, ColoredManaSymbol... coloredManaSymbols) {
        this(new StaticValue(amount), coloredManaSymbols);
    }

    public AddManaInAnyCombinationEffect(DynamicValue amount, ColoredManaSymbol... coloredManaSymbols) {
        super();
        this.manaSymbols.addAll(Arrays.asList(coloredManaSymbols));
        this.amount = amount;
        this.staticText = setText();
    }

    public AddManaInAnyCombinationEffect(final AddManaInAnyCombinationEffect effect) {
        super(effect);
        this.manaSymbols = effect.manaSymbols;
        this.amount = effect.amount;
    }

    @Override
    public AddManaInAnyCombinationEffect copy() {
        return new AddManaInAnyCombinationEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null){
            Mana mana = new Mana();
            int amountOfManaLeft = amount.calculate(game, source, this);

            while (amountOfManaLeft > 0 && player.isInGame()) {
                for (ColoredManaSymbol coloredManaSymbol: manaSymbols) {
                    int number = player.getAmount(0, amountOfManaLeft, new StringBuilder("How many ").append(coloredManaSymbol.name()).append(" mana?").toString(), game);
                    if (number > 0) {
                        for (int i = 0; i < number; i++) {
                            mana.add(new Mana(coloredManaSymbol));
                        }
                        amountOfManaLeft -= number;
                    }
                    if (amountOfManaLeft == 0) {
                        break;
                    }
                }
            }
            checkToFirePossibleEvents(mana, game, source);
            player.getManaPool().addMana(mana, game, source);            
            return true;
        }
        return false;
    }

    @Override
    public Mana getMana(Game game, Ability source) {
        return null;
    }

    private String setText() {
        StringBuilder sb = new StringBuilder("Add ");
        sb.append(CardUtil.numberToText(amount.toString()));
        sb.append(" mana in any combination of ");
        if (manaSymbols.size() == 5) {
            sb.append("colors");
        } else {
            int i = 0;
            for (ColoredManaSymbol coloredManaSymbol: manaSymbols) {
                i++;
                if (i > 1) {
                    sb.append(" and/or ");
                }
                sb.append("{").append(coloredManaSymbol.toString()).append("}");
            }
        }
        sb.append(" to your mana pool");
        return sb.toString();
    }
}
