package mage.cards.h;

import mage.abilities.Ability;
import mage.abilities.abilityword.StriveAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.HourOfNeedSphinxToken;
import mage.game.permanent.token.Token;
import mage.players.Player;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class HourOfNeed extends CardImpl {

    public HourOfNeed(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{2}{U}");

        // Strive — Hour of Need costs {1}{U} more to cast for each target beyond the first.
        this.addAbility(new StriveAbility("{1}{U}"));

        // Exile any number of target creatures. For each creature exiled this way, its controller creates a 4/4 blue Sphinx creature token with flying.
        this.getSpellAbility().addEffect(new HourOfNeedExileEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent(0, Integer.MAX_VALUE));
    }

    private HourOfNeed(final HourOfNeed card) {
        super(card);
    }

    @Override
    public HourOfNeed copy() {
        return new HourOfNeed(this);
    }
}

class HourOfNeedExileEffect extends OneShotEffect {

    public HourOfNeedExileEffect() {
        super(Outcome.Benefit);
        this.staticText = "Exile any number of target creatures. For each creature exiled this way, its controller creates a 4/4 blue Sphinx creature token with flying";
    }

    public HourOfNeedExileEffect(final HourOfNeedExileEffect effect) {
        super(effect);
    }

    @Override
    public HourOfNeedExileEffect copy() {
        return new HourOfNeedExileEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            for (UUID creatureId : getTargetPointer().getTargets(game, source)) {
                Permanent creature = game.getPermanent(creatureId);
                if (creature != null) {
                    controller.moveCardToExileWithInfo(creature, null, null, source, game, Zone.BATTLEFIELD, true);
                    Token token = new HourOfNeedSphinxToken();
                    token.putOntoBattlefield(1, game, source, creature.getControllerId());
                }
            }
            return true;
        }
        return false;
    }
}
