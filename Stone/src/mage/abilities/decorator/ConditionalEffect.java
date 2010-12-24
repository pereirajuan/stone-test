package mage.abilities.decorator;

import mage.Constants;
import mage.abilities.Ability;
import mage.abilities.condition.Condition;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.game.Game;

/**
 * Adds condition to effect. Acts as decorator.
 *
 * @author nantuko
 */
public class ConditionalEffect extends ContinuousEffectImpl<ConditionalEffect> {

    protected ContinuousEffect effect;
    protected Condition condition;
    protected String text;

    public ConditionalEffect(ContinuousEffect effect, Condition condition, String text) {
        super(effect.getDuration(), effect.getLayer(), effect.getSublayer(), effect.getOutcome());
        this.effect = effect;
        this.condition = condition;
        this.text = text;
    }

    @Override
    public boolean apply(Constants.Layer layer, Constants.SubLayer sublayer, Ability source, Game game) {
        if (condition.apply(game, source)) {
            return effect.apply(layer, sublayer, source, game);
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        if (condition.apply(game, source)) {
            return effect.apply(game, source);
        }
        return false;
    }

    @Override
    public boolean hasLayer(Constants.Layer layer) {
        return effect.hasLayer(layer);
    }

    @Override
    public ConditionalEffect copy() {
        return new ConditionalEffect(effect, condition, text);
    }

    @Override
    public String getText(Ability source) {
        return text;
    }
}
