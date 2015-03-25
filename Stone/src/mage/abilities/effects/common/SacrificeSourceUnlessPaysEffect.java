package mage.abilities.effects.common;

import mage.MageObject;
import mage.constants.Outcome;
import mage.abilities.Ability;
import mage.abilities.Mode;
import mage.abilities.costs.Cost;
import mage.abilities.effects.OneShotEffect;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.util.CardUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Loki
 * Date: 21.12.10
 * Time: 9:21
 */
public class SacrificeSourceUnlessPaysEffect extends OneShotEffect {
    protected Cost cost;

    public SacrificeSourceUnlessPaysEffect(Cost cost) {
        super(Outcome.Sacrifice);
        this.cost = cost;
     }

    public SacrificeSourceUnlessPaysEffect(final SacrificeSourceUnlessPaysEffect effect) {
        super(effect);
        this.cost = effect.cost.copy();
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        Permanent permanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        MageObject sourceObject = source.getSourceObject(game);
        if (sourceObject != null && player != null && permanent != null) {
            StringBuilder sb = new StringBuilder(cost.getText()).append("?");
            if (!sb.toString().toLowerCase().startsWith("exile ") && !sb.toString().toLowerCase().startsWith("return ") ) {
                sb.insert(0, "Pay ");
            } 
            String message = CardUtil.replaceSourceName(sb.toString(), sourceObject.getLogName());
            message = Character.toUpperCase(message.charAt(0)) + message.substring(1);
            if (player.chooseUse(Outcome.Benefit, message, game)) {
                cost.clearPaid();
                if (cost.pay(source, game, source.getSourceId(), source.getControllerId(), false)) {
                    return true;
                }
            }
            permanent.sacrifice(source.getSourceId(), game);
            return true;
        }
        return false;
    }

    @Override
    public SacrificeSourceUnlessPaysEffect copy() {
        return new SacrificeSourceUnlessPaysEffect(this);
    }

        @Override
    public String getText(Mode mode) {
            StringBuilder sb = new StringBuilder("sacrifice {this} unless you ");
            String costText = cost.getText();
            if (costText.toLowerCase().startsWith("discard") 
                    || costText.toLowerCase().startsWith("remove")
                    || costText.toLowerCase().startsWith("return")
                    || costText.toLowerCase().startsWith("exile")
                    || costText.toLowerCase().startsWith("sacrifice")) {
                sb.append(costText.substring(0, 1).toLowerCase());
                sb.append(costText.substring(1));
            } 
            else {
                sb.append("pay ").append(costText);
            }

            return sb.toString();

    }
 }
