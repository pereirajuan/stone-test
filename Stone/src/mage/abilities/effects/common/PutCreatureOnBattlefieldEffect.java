package mage.abilities.effects.common;

import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.players.Player;
import mage.target.common.TargetCardInHand;

/**
 * @author magenoxx_at_gmail.com
 */
public class PutCreatureOnBattlefieldEffect extends OneShotEffect {

    private static final String choiceText = "Put a creature card from your hand onto the battlefield?";

    public PutCreatureOnBattlefieldEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "You may put a creature card from your hand onto the battlefield";
    }

    public PutCreatureOnBattlefieldEffect(final PutCreatureOnBattlefieldEffect effect) {
        super(effect);
    }

    @Override
    public PutCreatureOnBattlefieldEffect copy() {
        return new PutCreatureOnBattlefieldEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player == null || !player.chooseUse(Outcome.PutCreatureInPlay, choiceText, source, game)) {
            return false;
        }

        TargetCardInHand target = new TargetCardInHand(new FilterCreatureCard());
        if (player.choose(Outcome.PutCreatureInPlay, target, source.getSourceId(), game)) {
            Card card = game.getCard(target.getFirstTarget());
            if (card != null) {
                player.putOntoBattlefieldWithInfo(card, game, Zone.HAND, source.getSourceId());
                return true;
            }
        }
        return false;
    }
}