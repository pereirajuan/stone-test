package mage.abilities.keyword;

import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.Token;
import mage.players.Player;

public class LivingWeaponAbility extends EntersBattlefieldTriggeredAbility {
    public LivingWeaponAbility() {
        super(new LivingWeaponEffect());
    }

     public LivingWeaponAbility(final LivingWeaponAbility ability) {
        super(ability);
    }

    @Override
    public String getRule() {
        return "Living weapon <i>(When this Equipment enters the battlefield, put a 0/0 black Germ creature token onto the battlefield, then attach this to it.)<i/>";
    }

    @Override
    public EntersBattlefieldTriggeredAbility copy() {
        return new LivingWeaponAbility(this);
    }
}

class LivingWeaponEffect extends OneShotEffect {
    LivingWeaponEffect() {
        super(Outcome.PutCreatureInPlay);
    }

    LivingWeaponEffect(final LivingWeaponEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            GermToken token = new GermToken();
            token.putOntoBattlefield(1, game, source.getSourceId(), source.getControllerId());
            Permanent p = game.getPermanent(token.getLastAddedToken());
            if (p != null) {
                 p.addAttachment(source.getSourceId(), game);
                 return true;
            }            
        }
        return false;
    }

    @Override
    public LivingWeaponEffect copy() {
        return new LivingWeaponEffect(this);
    }
}

class GermToken extends Token {
    public GermToken() {
        super("Germ", "a 0/0 black Germ creature token");
        this.setOriginalExpansionSetCode("MBS");
        cardType.add(CardType.CREATURE);
        color.setBlack(true);
        subtype.add("Germ");
        power = new MageInt(0);
        toughness = new MageInt(0);
    }
}